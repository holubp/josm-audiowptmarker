# JOSM Audio Waypoint Marker

JOSM Audio Waypoint Marker is a review tool for audio notes recorded while mapping.

Audio mapping is one of the most effective ways to collect detailed observations while moving by bicycle, car, motorcycle, or on foot. Instead of stopping for every detail, you can record short spoken notes with tools such as OsmTracker, OsmAnd, or similar GPX/audio logging apps, then review the notes later in JOSM. A few hours of mapping can easily produce hundreds of audio waypoints, so efficient navigation through those recordings matters.

This plugin adds a dedicated JOSM dialog and shortcuts for reviewing those recorded audio waypoints one by one, centered on the map, with optional playback.

## Typical Workflow

1. Record a GPX track with linked audio notes in OsmTracker or a similar tool.
2. Open the GPX file in JOSM together with the referenced audio files.
3. Let JOSM create the marker layer, usually named `Markers from ...`.
4. Open the **Audio Waypoints** dialog.
5. Step through the audio waypoints, recentering the map and playing recordings as needed.
6. Add or update OSM data while listening to each note.

The plugin is designed for the review phase: it does not record audio and does not import GPX/audio by itself. It works with layers already loaded by JOSM.

## Features

- Dockable **Audio Waypoints** dialog listing audio waypoints from a selected GPX or marker layer.
- Layer selector showing all GPX and marker layers, with layers named `Markers from ...` sorted first.
- Automatic selection when exactly one `Markers from ...` layer is present.
- Table columns for relative time from track start, audio duration, GPS coordinates, and absolute time.
- Default waypoint order by relative time from the beginning of the linked GPX track.
- Current waypoint selection in the table.
- Navigation actions for current, previous, and next waypoint.
- Optional play variants for each navigation action.
- Support for JOSM-native audio markers and OsmTracker-style `.3gp`, `.3gpp`, and `.amr` links that JOSM represents as web markers.

## Opening The Dialog

After opening a GPX file in JOSM, open:

```text
Windows -> Audio Waypoints
```

The default toggle shortcut is:

```text
Alt+Shift+F8
```

The layer selector is at the top of the dialog. If there is only one layer named `Markers from ...`, it should be selected automatically. If not, select the marker layer or its related GPX layer manually.

## Reviewing Notes

Use the table to see where each audio note sits in the track:

- **Relative time**: time from the beginning of the linked GPX track.
- **Duration**: detected audio clip duration when available.
- **GPS coords**: marker coordinates.
- **Absolute time**: timestamp from the waypoint.

Selecting a row recenters the map on that waypoint while keeping the current zoom level.

The default shortcuts are:

- `Ctrl+Alt+J`: previous waypoint.
- `Ctrl+Alt+K`: current waypoint.
- `Ctrl+Alt+L`: next waypoint.
- `Ctrl+Alt+Shift+J`: previous waypoint and play.
- `Ctrl+Alt+Shift+K`: current waypoint and play.
- `Ctrl+Alt+Shift+L`: next waypoint and play.

The play actions use JOSM's native audio-marker playback when JOSM created an `AudioMarker`. For OsmTracker `.3gpp`-style links that JOSM represents as web markers, the plugin invokes the same marker action as double-clicking the marker in JOSM.

## Installation

Download the latest jar from the GitHub releases page and place it in your JOSM plugins directory.

Releases:

```text
https://github.com/holubp/josm-audiowptmarker/releases
```

Restart JOSM after installing or replacing the jar.

## Build

```sh
gradle build
```

The plugin jar is written to `build/libs/`.

## Private Examples

`examples-private/` is intentionally git-ignored. Do not commit GPX/audio recordings from that directory.
