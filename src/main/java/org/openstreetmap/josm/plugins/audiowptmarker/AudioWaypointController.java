package org.openstreetmap.josm.plugins.audiowptmarker;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.markerlayer.AudioMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.io.audio.AudioUtil;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.date.Interval;

final class AudioWaypointController {
    private MarkerLayer selectedLayer;
    private List<AudioWaypoint> waypoints = List.of();
    private int currentIndex;
    private Runnable changeListener;

    List<MarkerLayer> audioMarkerLayers() {
        if (MainApplication.getLayerManager() == null) {
            return List.of();
        }
        return MainApplication.getLayerManager().getLayersOfType(MarkerLayer.class).stream()
                .filter(AudioWaypointController::hasAudioMarkers)
                .toList();
    }

    MarkerLayer selectedLayer() {
        return selectedLayer;
    }

    void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    void setSelectedLayer(MarkerLayer layer) {
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
            selectedLayer = audioMarkerLayers().stream().findFirst().orElse(null);
            refreshWaypoints();
        }
    }

    private void refreshWaypoints() {
        waypoints = selectedLayer == null ? List.of() : collectAudioWaypoints(selectedLayer);
    }

    private static boolean hasAudioMarkers(MarkerLayer layer) {
        for (Marker marker : layer.data) {
            if (marker instanceof AudioMarker) {
                return true;
            }
        }
        return false;
    }

    private List<AudioWaypoint> collectAudioWaypoints(MarkerLayer layer) {
        Instant trackStart = trackStart(layer).orElse(null);
        List<AudioWaypoint> result = new ArrayList<>();
        for (Marker marker : layer.data) {
            if (marker instanceof AudioMarker audioMarker) {
                Instant absolute = marker.time > 0
                        ? Instant.ofEpochMilli(Math.round(marker.time * 1000.0))
                        : null;
                double relative = relativeSeconds(marker, trackStart);
                result.add(new AudioWaypoint(audioMarker, relative, durationSeconds(audioMarker), absolute));
            }
        }
        result.sort(Comparator.comparingDouble(AudioWaypoint::relativeSeconds));
        return List.copyOf(result);
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

    private static double durationSeconds(AudioMarker marker) {
        URL url = marker.url();
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

    private void recenterCurrent(boolean play) {
        AudioWaypoint waypoint = waypoints.get(currentIndex);
        MapFrame mapFrame = MainApplication.getMap();
        if (mapFrame != null) {
            MapView mapView = mapFrame.mapView;
            mapView.zoomTo(waypoint.marker());
        }
        if (play) {
            waypoint.marker().play();
        }
    }

    private void fireChanged() {
        if (changeListener != null) {
            changeListener.run();
        }
    }
}
