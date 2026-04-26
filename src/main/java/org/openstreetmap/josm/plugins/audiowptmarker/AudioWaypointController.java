package org.openstreetmap.josm.plugins.audiowptmarker;

import java.io.File;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxLink;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.markerlayer.AudioMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.gui.layer.markerlayer.WebMarker;
import org.openstreetmap.josm.io.audio.AudioUtil;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.date.Interval;

final class AudioWaypointController {
    private static final List<String> AUDIO_EXTENSIONS = List.of(
            ".wav", ".mp3", ".aac", ".aif", ".aiff", ".3gp", ".3gpp", ".amr"
    );

    private Layer selectedLayer;
    private List<AudioWaypoint> waypoints = List.of();
    private int currentIndex;
    private Runnable changeListener;

    List<Layer> selectableLayers() {
        if (MainApplication.getLayerManager() == null) {
            return List.of();
        }
        return MainApplication.getLayerManager().getLayers().stream()
                .filter(layer -> layer instanceof MarkerLayer || layer instanceof GpxLayer)
                .sorted(Comparator.comparingInt(AudioWaypointController::layerPriority)
                        .thenComparing(Layer::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    Layer selectedLayer() {
        return selectedLayer;
    }

    Layer defaultLayer(List<Layer> layers) {
        List<Layer> markerLayers = layers.stream()
                .filter(AudioWaypointController::isMarkersFromLayer)
                .toList();
        if (markerLayers.size() == 1) {
            return markerLayers.get(0);
        }
        return layers.isEmpty() ? null : layers.get(0);
    }

    boolean shouldAutoSelect(Layer selected, List<Layer> layers) {
        boolean hasExactlyOneMarkersFromLayer = layers.stream()
                .filter(AudioWaypointController::isMarkersFromLayer)
                .limit(2)
                .count() == 1;
        return selected == null || !layers.contains(selected)
                || hasExactlyOneMarkersFromLayer && !isMarkersFromLayer(selected);
    }

    void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    void setSelectedLayer(Layer layer) {
        if (layer != selectedLayer) {
            selectedLayer = layer;
            refreshWaypoints();
            currentIndex = 0;
            fireChanged();
        }
    }

    List<AudioWaypoint> waypoints() {
        ensureWaypoints();
        return waypoints;
    }

    int currentIndex() {
        ensureWaypoints();
        return currentIndex;
    }

    void setCurrentIndex(int index, boolean recenter) {
        ensureWaypoints();
        if (waypoints.isEmpty()) {
            currentIndex = 0;
            return;
        }
        currentIndex = Math.max(0, Math.min(index, waypoints.size() - 1));
        if (recenter) {
            recenterCurrent(false);
        }
        fireChanged();
    }

    boolean current(boolean play) {
        ensureWaypoints();
        if (waypoints.isEmpty()) {
            return false;
        }
        recenterCurrent(play);
        fireChanged();
        return true;
    }

    boolean previous(boolean play) {
        ensureWaypoints();
        if (waypoints.isEmpty()) {
            return false;
        }
        currentIndex = Math.max(0, currentIndex - 1);
        recenterCurrent(play);
        fireChanged();
        return true;
    }

    boolean next(boolean play) {
        ensureWaypoints();
        if (waypoints.isEmpty()) {
            return false;
        }
        currentIndex = Math.min(waypoints.size() - 1, currentIndex + 1);
        recenterCurrent(play);
        fireChanged();
        return true;
    }

    private void ensureWaypoints() {
        if (selectedLayer == null) {
            selectedLayer = selectableLayers().stream().findFirst().orElse(null);
            refreshWaypoints();
        }
    }

    private void refreshWaypoints() {
        MarkerLayer markerLayer = resolveMarkerLayer(selectedLayer).orElse(null);
        waypoints = markerLayer == null ? List.of() : collectAudioWaypoints(markerLayer);
    }

    private static int layerPriority(Layer layer) {
        if (isMarkersFromLayer(layer)) {
            return 0;
        }
        if (layer instanceof MarkerLayer) {
            return 1;
        }
        if (layer instanceof GpxLayer) {
            return 2;
        }
        return 3;
    }

    private static boolean isMarkersFromLayer(Layer layer) {
        return layer != null && layer.getName().startsWith("Markers from ");
    }

    private static Optional<MarkerLayer> resolveMarkerLayer(Layer layer) {
        if (layer instanceof MarkerLayer markerLayer) {
            return Optional.of(markerLayer);
        }
        if (layer instanceof GpxLayer gpxLayer) {
            MarkerLayer linkedLayer = gpxLayer.getLinkedMarkerLayer();
            if (linkedLayer != null) {
                return Optional.of(linkedLayer);
            }
            if (MainApplication.getLayerManager() != null) {
                return MainApplication.getLayerManager().getLayersOfType(MarkerLayer.class).stream()
                        .filter(markerLayer -> markerLayer.fromLayer == gpxLayer)
                        .findFirst();
            }
        }
        return Optional.empty();
    }

    private List<AudioWaypoint> collectAudioWaypoints(MarkerLayer layer) {
        Instant trackStart = trackStart(layer).orElse(null);
        List<AudioWaypoint> result = new ArrayList<>();
        for (Marker marker : layer.data) {
            if (isAudioWaypointMarker(marker)) {
                Instant absolute = marker.time > 0
                        ? Instant.ofEpochMilli(Math.round(marker.time * 1000.0))
                        : null;
                double relative = relativeSeconds(marker, trackStart);
                result.add(new AudioWaypoint(marker, relative, durationSeconds(marker), absolute));
            }
        }
        result.sort(Comparator.comparingDouble(AudioWaypoint::relativeSeconds));
        return List.copyOf(result);
    }

    private static boolean isAudioWaypointMarker(Marker marker) {
        if (marker instanceof AudioMarker) {
            return true;
        }
        return marker instanceof WebMarker && markerUrl(marker)
                .map(AudioWaypointController::hasAudioExtension)
                .orElse(false);
    }

    private static Optional<Instant> trackStart(MarkerLayer layer) {
        GpxLayer gpxLayer = layer.fromLayer;
        if (gpxLayer == null) {
            return Optional.empty();
        }
        GpxData data = gpxLayer.data;
        if (data == null) {
            return Optional.empty();
        }
        return data.getMinMaxTimeForAllTracks().map(Interval::getStart);
    }

    private static double relativeSeconds(Marker marker, Instant trackStart) {
        if (trackStart != null && marker.time > 0) {
            return marker.time - trackStart.toEpochMilli() / 1000.0;
        }
        return marker.offset;
    }

    private static double durationSeconds(Marker marker) {
        URL url = markerUrl(marker).orElse(null);
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return Double.NaN;
        }
        try {
            return AudioUtil.getCalibratedDuration(new File(url.toURI()));
        } catch (URISyntaxException | RuntimeException exception) {
            Logging.debug(exception);
            return Double.NaN;
        }
    }

    private static Optional<URL> markerUrl(Marker marker) {
        if (marker instanceof AudioMarker audioMarker) {
            return Optional.ofNullable(audioMarker.url());
        }
        WayPoint wayPoint = marker.convertToWayPoint();
        Collection<GpxLink> links = wayPoint.getCollection("meta.links");
        if (links == null) {
            return Optional.empty();
        }
        for (GpxLink link : links) {
            try {
                return Optional.of(new URL(link.uri));
            } catch (MalformedURLException exception) {
                Logging.debug(exception);
            }
        }
        return Optional.empty();
    }

    private static boolean hasAudioExtension(URL url) {
        String path = url.getPath().toLowerCase(java.util.Locale.ROOT);
        return AUDIO_EXTENSIONS.stream().anyMatch(path::endsWith);
    }

    private void recenterCurrent(boolean play) {
        AudioWaypoint waypoint = waypoints.get(currentIndex);
        MapFrame mapFrame = MainApplication.getMap();
        if (mapFrame != null) {
            MapView mapView = mapFrame.mapView;
            mapView.zoomTo(waypoint.marker());
        }
        if (play) {
            if (waypoint.marker() instanceof AudioMarker audioMarker) {
                audioMarker.play();
            } else {
                waypoint.marker().actionPerformed(
                        new ActionEvent(waypoint.marker(), ActionEvent.ACTION_PERFORMED, "audiowptmarker-play"));
            }
        }
    }

    private void fireChanged() {
        if (changeListener != null) {
            changeListener.run();
        }
    }
}
