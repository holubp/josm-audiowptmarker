package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.tools.Shortcut;

final class AudioWaypointDialog extends ToggleDialog implements LayerChangeListener {
    private final AudioWaypointController controller;
    private final JComboBox<MarkerLayer> layerCombo = new JComboBox<>();
    private final AudioWaypointTableModel tableModel = new AudioWaypointTableModel();
    private final JTable table = new JTable(tableModel);
    private boolean updating;

    AudioWaypointDialog(AudioWaypointController controller) {
        super(
                tr("Audio Waypoints"),
                "audiowptmarker",
                tr("Review OsmTracker audio waypoints"),
                Shortcut.registerShortcut("subwindow:audiowptmarker", tr("Toggle: {0}", tr("Audio Waypoints")),
                        java.awt.event.KeyEvent.VK_F8, Shortcut.ALT_SHIFT),
                200
        );
        this.controller = controller;
        this.controller.setChangeListener(this::syncFromController);
        buildContent();
        if (MainApplication.getLayerManager() != null) {
            MainApplication.getLayerManager().addAndFireLayerChangeListener(this);
        }
    }

    @Override
    public void destroy() {
        if (MainApplication.getLayerManager() != null) {
            MainApplication.getLayerManager().removeLayerChangeListener(this);
        }
        super.destroy();
    }

    @Override
    public void layerAdded(LayerAddEvent event) {
        refreshLayers();
    }

    @Override
    public void layerRemoving(LayerRemoveEvent event) {
        if (event.getRemovedLayer() == controller.selectedLayer()) {
            controller.setSelectedLayer(null);
        }
        SwingUtilities.invokeLater(this::refreshLayers);
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent event) {
        refreshLayers();
    }

    private void buildContent() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout(5, 0));
        layerCombo.setRenderer(new MarkerLayerCellRenderer());
        layerCombo.addActionListener(event -> {
            if (!updating) {
                controller.setSelectedLayer((MarkerLayer) layerCombo.getSelectedItem());
            }
        });
        top.add(layerCombo, BorderLayout.CENTER);

        JButton refresh = new JButton(tr("Refresh"));
        refresh.addActionListener(event -> refreshLayers());
        top.add(refresh, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && !updating && table.getSelectedRow() >= 0) {
                controller.setCurrentIndex(table.convertRowIndexToModel(table.getSelectedRow()), true);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshLayers();
    }

    private void refreshLayers() {
        updating = true;
        try {
            List<MarkerLayer> layers = controller.audioMarkerLayers();
            layerCombo.setModel(new DefaultComboBoxModel<>(layers.toArray(MarkerLayer[]::new)));
            MarkerLayer selected = controller.selectedLayer();
            if (selected == null || !layers.contains(selected)) {
                selected = layers.isEmpty() ? null : layers.get(0);
            }
            layerCombo.setSelectedItem(selected);
            controller.setSelectedLayer(selected);
            tableModel.setWaypoints(controller.waypoints());
            selectControllerRow();
        } finally {
            updating = false;
        }
    }

    private void syncFromController() {
        updating = true;
        try {
            if (layerCombo.getSelectedItem() != controller.selectedLayer()) {
                layerCombo.setSelectedItem(controller.selectedLayer());
            }
            tableModel.setWaypoints(controller.waypoints());
            selectControllerRow();
        } finally {
            updating = false;
        }
    }

    private void selectControllerRow() {
        int modelRow = controller.currentIndex();
        if (modelRow >= 0 && modelRow < tableModel.getRowCount()) {
            int viewRow = table.convertRowIndexToView(modelRow);
            table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
        } else {
            table.clearSelection();
        }
    }

    @Override
    protected Dimension getDefaultDetachedSize() {
        return new Dimension(760, 360);
    }
}
