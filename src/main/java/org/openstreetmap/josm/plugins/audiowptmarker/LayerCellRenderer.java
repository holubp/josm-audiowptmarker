package org.openstreetmap.josm.plugins.audiowptmarker;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.openstreetmap.josm.gui.layer.Layer;

final class LayerCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Layer layer) {
            setText(layer.getName());
            setIcon(layer.getIcon());
        }
        return this;
    }
}
