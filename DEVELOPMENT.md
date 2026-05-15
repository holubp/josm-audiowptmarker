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

The resulting plugin jar is written to `build/libs/audiowptmarker.jar`. Keep the release asset filename unversioned for JOSM plugin-list compatibility; the semantic version belongs in the jar manifest as `Plugin-Version`.

Before publishing a release, inspect the built jar manifest:

```sh
unzip -p build/libs/audiowptmarker.jar META-INF/MANIFEST.MF
```

Confirm these entries are correct:

- `Plugin-Version`: the semantic release version.
- `Plugin-Mainversion`: the minimum supported JOSM version.
- `Plugin-Class`: the plugin entry point.
- `Plugin-Minimum-Java-Version`: the minimum supported Java version.
- `Plugin-Icon`: the bundled plugin icon name.

## Release Policy

JOSM official plugin sources expect a stable jar URL. Publish the primary GitHub release asset as:

```text
audiowptmarker.jar
```

Do not publish a versioned filename such as `audiowptmarker-0.1.9.jar` as the primary asset. Versioning belongs in the Git tag, GitHub release name, and the jar manifest `Plugin-Version`.

The stable URL for JOSM plugin-list use is:

```text
https://github.com/holubp/josm-audiowptmarker/releases/latest/download/audiowptmarker.jar
```

When creating GitHub releases from the command line, use `--notes-file` or another newline-safe method so release notes contain real newlines, not literal `\n` text.

## Private Examples

`examples-private/` is intentionally git-ignored because it may contain private GPX tracks and audio recordings. Use it only for local manual testing and never include files from it in commits, releases, or public documentation.

## JOSM Integration Notes

The plugin operates on marker layers that JOSM has already created from a GPX layer with linked audio files. It does not import GPX/audio files itself.

Playback is delegated to JOSM's `AudioMarker.play()` method so behavior stays aligned with double-clicking an audio marker in JOSM.

Navigation recenters `MapView` on the selected `AudioMarker` and intentionally keeps the current zoom level.

JOSM may remove the final layer and clear layer listeners before plugin dialogs are destroyed. Dialog cleanup must therefore be idempotent: track listener registration state, tolerate already-cleared listeners, and detach callbacks from destroyed dialogs.
