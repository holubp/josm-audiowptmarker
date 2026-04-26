package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.AbstractTableModel;

final class AudioWaypointTableModel extends AbstractTableModel {
    private static final DateTimeFormatter ABSOLUTE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX").withZone(ZoneId.systemDefault());
    private static final String[] COLUMNS = {
            tr("Relative time"),
            tr("Duration"),
            tr("GPS coords"),
            tr("Absolute time")
    };

    private List<AudioWaypoint> waypoints = List.of();

    void setWaypoints(List<AudioWaypoint> waypoints) {
        this.waypoints = List.copyOf(waypoints);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return waypoints.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AudioWaypoint waypoint = waypoints.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> TimeFormat.formatDuration(waypoint.relativeSeconds());
            case 1 -> TimeFormat.formatDuration(waypoint.durationSeconds());
            case 2 -> String.format("%.7f, %.7f", waypoint.marker().lat(), waypoint.marker().lon());
            case 3 -> waypoint.absoluteTime() == null ? "" : ABSOLUTE_TIME_FORMAT.format(waypoint.absoluteTime());
            default -> throw new IllegalArgumentException("Unknown column: " + columnIndex);
        };
    }
}
