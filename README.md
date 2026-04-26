# JOSM Audio Waypoint Marker

JOSM plugin for reviewing OsmTracker audio waypoints from an existing marker layer.

## Features

- Dockable dialog listing audio waypoints from a selected marker layer.
- Columns for time relative to track start, audio duration, GPS coordinates, and absolute time.
- Default waypoint order is by relative time from the start of the linked GPX track.
- Keyboard and toolbar actions for current, previous, and next waypoint navigation.
- Shift variants of the navigation shortcuts also start playback by invoking JOSM's `AudioMarker.play()` path.
- Default shortcuts are `Ctrl+Alt+J/K/L` for previous/current/next and `Ctrl+Alt+Shift+J/K/L` for previous/current/next and play.

## Build

```sh
gradle build
```

The plugin jar is written to `build/libs/`.

## Private Examples

`examples-private/` is intentionally git-ignored. Do not commit GPX/audio recordings from that directory.
