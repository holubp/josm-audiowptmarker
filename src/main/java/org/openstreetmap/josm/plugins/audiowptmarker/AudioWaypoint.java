package org.openstreetmap.josm.plugins.audiowptmarker;

import java.time.Instant;

import org.openstreetmap.josm.gui.layer.markerlayer.AudioMarker;

final class AudioWaypoint {
    private final AudioMarker marker;
    private final double relativeSeconds;
    private final double durationSeconds;
    private final Instant absoluteTime;

    AudioWaypoint(AudioMarker marker, double relativeSeconds, double durationSeconds, Instant absoluteTime) {
        this.marker = marker;
        this.relativeSeconds = relativeSeconds;
        this.durationSeconds = durationSeconds;
        this.absoluteTime = absoluteTime;
    }

    AudioMarker marker() {
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
