# Development

## Project Structure

This is a plain Java JOSM plugin project built with Gradle.

- `src/main/java/org/openstreetmap/josm/plugins/audiowptmarker/` contains the plugin code.
- `src/test/java/org/openstreetmap/josm/plugins/audiowptmarker/` contains unit tests for logic that can be tested without a running JOSM UI.
- `build.gradle` defines the JOSM compile-only dependency and plugin manifest metadata.

The Java package intentionally uses the neutral JOSM plugin namespace. Personal identity belongs in manifest metadata, not in source package paths.

## Build And Validation

Run the standard validation before committing:

```sh
gradle test jar
```

The resulting plugin jar is written to `build/libs/josm-audiowptmarker-<version>.jar`.

## JOSM Integration Notes

The plugin operates on marker layers that JOSM has already created from a GPX layer with linked audio files. It does not import GPX/audio files itself.

Playback is delegated to JOSM's `AudioMarker.play()` method so behavior stays aligned with double-clicking an audio marker in JOSM.

Navigation recenters `MapView` on the selected `AudioMarker` and intentionally keeps the current zoom level.
