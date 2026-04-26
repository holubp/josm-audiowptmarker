package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TimeFormatTest {
    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("0:00", TimeFormat.formatDuration(0));
        assertEquals("1:05", TimeFormat.formatDuration(65));
        assertEquals("1:01:01", TimeFormat.formatDuration(3661));
    }

    @Test
    void formatsFractionalSeconds() {
        assertEquals("0:01.250", TimeFormat.formatDuration(1.25));
    }

    @Test
    void hidesUnknownDurations() {
        assertEquals("", TimeFormat.formatDuration(Double.NaN));
        assertEquals("", TimeFormat.formatDuration(-1));
    }
}
