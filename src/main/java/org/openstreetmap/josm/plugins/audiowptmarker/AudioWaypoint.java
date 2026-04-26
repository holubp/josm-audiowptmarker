package org.openstreetmap.josm.plugins.audiowptmarker;

import java.time.Instant;

import org.openstreetmap.josm.gui.layer.markerlayer.Marker;

final class AudioWaypoint {
    private final Marker marker;
    private final double relativeSeconds;
    private final double durationSeconds;
    private final Instant absoluteTime;

    AudioWaypoint(Marker marker, double relativeSeconds, double durationSeconds, Instant absoluteTime) {
        this.marker = marker;
        this.relativeSeconds = relativeSeconds;
        this.durationSeconds = durationSeconds;
        this.absoluteTime = absoluteTime;
    }

    Marker marker() {
        return marker;
    }

    double relativeSeconds() {
        return relativeSeconds;
    }

    double durationSeconds() {
        return durationSeconds;
    }

    Instant absoluteTime() {
        return absoluteTime;
    }
}
