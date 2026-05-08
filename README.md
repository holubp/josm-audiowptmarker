# JOSM Audio Waypoint Marker

JOSM Audio Waypoint Marker is a review tool for audio notes recorded while mapping.

Audio mapping is one of the most effective ways to collect detailed observations while moving by bicycle, car, motorcycle, or on foot. Instead of stopping for every detail, you can record short spoken notes with tools such as OsmTracker, OsmAnd, or similar GPX/audio logging apps, then review the notes later in JOSM. A few hours of mapping can easily produce hundreds of audio waypoints, so efficient navigation through those recordings matters.

This plugin adds a dedicated JOSM dialog and shortcuts for reviewing those recorded audio waypoints one by one, centered on the map, with optional playback.

## Typical Workflow

1. Record a GPX track with linked audio notes in OsmTracker or a similar tool while mapping.
2. Export or share the OsmTracker trip as an archive containing the GPX file and audio recordings.
3. Unzip the archive on the computer where you run JOSM, keeping the GPX file and audio files together.
4. Open the GPX file in JOSM and let JOSM create the related marker layer, usually named `Markers from ...`.
5. Open the **Audio Waypoints** dialog and select the marker layer if it is not selected automatically.
6. Step through the audio waypoints one by one, recentering the map and playing each recording as needed.
7. Add or update OSM data in JOSM while listening to each note.

The plugin is designed for the review phase: it does not record audio and does not import GPX/audio by itself. It works with layers already loaded by JOSM.

## From OsmTracker To JOSM

An effective audio-mapping session usually looks like this:

1. Start tracking in OsmTracker before you begin riding or driving.
2. Whenever you notice something worth mapping, tap the audio note button and say a short, specific note.
3. Keep moving; do not stop for every sign, surface change, missing path, shop name, access restriction, or road detail.
4. At the end of the survey, use OsmTracker's export or share function to create a trip archive.
5. Transfer that archive to your editing computer.
6. Unzip it into a normal directory. Do not move the audio files away from the GPX file, because the GPX links point to those recordings.
7. Open the GPX file in JOSM.

After loading the GPX, JOSM normally creates two related layers:

- A GPX track layer showing where you moved.
- A marker layer named like `Markers from survey.gpx`, containing the audio waypoints.

Open **Windows -> Audio Waypoints**. If there is only one `Markers from ...` layer, the plugin selects it automatically. Otherwise, choose the marker layer for the trip you want to review.

A typical review loop is:

1. Press the current/next-and-play shortcut or use the toolbar action.
2. JOSM recenters the map on the audio waypoint without changing your current zoom.
3. Listen to the note.
4. Edit the map at that position: add missing objects, adjust tags, split ways, fix access, add names, or mark things for later investigation.
5. Move to the next audio waypoint.

For example, during a bicycle survey you might record: "cycleway starts here, asphalt, separated from road", then a few minutes later: "bench and drinking water on right", and later: "track becomes gravel after bridge". In JOSM, the plugin lets you replay those notes in track order, jump the map to each exact recording location, and immediately make the corresponding edits while the context is still clear.

The goal is to turn hundreds of short field recordings into a predictable editing checklist: one audio waypoint, one map location, one decision, then the next waypoint.

## Features

- Dockable **Audio Waypoints** dialog listing audio waypoints from a selected GPX or marker layer.
- Layer selector showing all GPX and marker layers, with layers named `Markers from ...` sorted first.
- Automatic selection when exactly one `Markers from ...` layer is present.
- Table columns for relative time from track start, audio duration, GPS coordinates, and absolute time.
- Default waypoint order by relative time from the beginning of the linked GPX track.
- Current waypoint selection in the table.
- Navigation actions for current, previous, and next waypoint.
- Optional play variants for each navigation action.
- Explicit **Sync** button that moves the dialog selection to the last clicked or played audio marker from the selected layer.
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

The **Sync** button next to **Refresh** selects the last clicked audio marker from the currently selected layer. Clicking or playing a marker in the main map view does not automatically move the dialog selection; use **Sync** when you explicitly want to align the dialog with that map marker. For native JOSM audio markers, **Sync** can also use JOSM's recently played marker state when the marker belongs to the selected layer.

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

If you try to move before the first audio waypoint or after the last one, the plugin shows a warning dialog and does not replay the boundary waypoint.

## Installation

Download the latest jar from the GitHub releases page and place it in your JOSM plugins directory.

Stable latest jar URL:

```text
https://github.com/holubp/josm-audiowptmarker/releases/latest/download/audiowptmarker.jar
```

Release page:

```text
https://github.com/holubp/josm-audiowptmarker/releases
```

Restart JOSM after installing or replacing the jar. The jar filename is unversioned for JOSM plugin-list compatibility; the plugin version is stored in the jar manifest as `Plugin-Version`.

## Build

```sh
gradle build
```

The plugin jar is written to `build/libs/audiowptmarker.jar`.
