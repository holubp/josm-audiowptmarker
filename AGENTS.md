# Repository Instructions

- Do not commit anything from `examples-private/`; it contains private GPX/audio recordings.
- Keep source packages neutral under `org.openstreetmap.josm.plugins.audiowptmarker` unless the maintainer explicitly requests a package rename.
- Do not infer personal names or usernames for package paths, authorship, or metadata. Use only identity information explicitly provided by the maintainer.
- Run `gradle test jar` before committing code changes.
- Before publishing a release, verify `build/libs/audiowptmarker.jar` exists and its manifest has the intended `Plugin-Version`, `Plugin-Mainversion`, `Plugin-Class`, `Plugin-Minimum-Java-Version`, and `Plugin-Icon`.
- Publish the GitHub release asset as the unversioned `audiowptmarker.jar`; do not use a versioned jar filename as the primary release asset because JOSM plugin lists expect a stable jar URL.
- Put semantic versioning in the jar manifest and Git tag/release name, not in the primary release asset filename.
- Use a release notes file or another newline-safe mechanism for GitHub release notes; do not pass shell-escaped `\n` text that renders literally.
- Keep playback behavior delegated to JOSM's `AudioMarker.play()` path so it remains consistent with native marker playback.
- Bundle plugin-owned icons under `src/main/resources/images/` and `src/main/resources/images/dialogs/`; do not rely on JOSM core image paths for required plugin/dialog icons.
