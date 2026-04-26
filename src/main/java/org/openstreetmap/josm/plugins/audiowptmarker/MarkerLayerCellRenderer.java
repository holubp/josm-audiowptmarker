package org.openstreetmap.josm.plugins.audiowptmarker;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;

final class MarkerLayerCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof MarkerLayer layer) {
            setText(layer.getName());
            setIcon(layer.getIcon());
        }
        return this;
    }
}
