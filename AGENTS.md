# Repository Instructions

- Do not commit anything from `examples-private/`; it contains private GPX/audio recordings.
- Keep source packages neutral under `org.openstreetmap.josm.plugins.audiowptmarker` unless the maintainer explicitly requests a package rename.
- Do not infer personal names or usernames for package paths, authorship, or metadata. Use only identity information explicitly provided by the maintainer.
- Run `gradle test jar` before committing code changes.
- Keep playback behavior delegated to JOSM's `AudioMarker.play()` path so it remains consistent with native marker playback.
- Bundle plugin-owned icons under `src/main/resources/images/` and `src/main/resources/images/dialogs/`; do not rely on JOSM core image paths for required plugin/dialog icons.
