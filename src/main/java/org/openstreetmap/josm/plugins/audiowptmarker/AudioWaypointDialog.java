package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

final class AudioWaypointDialog extends ToggleDialog implements LayerChangeListener {
    private final AudioWaypointController controller;
    private final JComboBox<Layer> layerCombo = new JComboBox<>();
    private final AudioWaypointTableModel tableModel = new AudioWaypointTableModel();
    private final JTable table = new JTable(tableModel);
    private final MouseAdapter mapClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            controller.noteMapClick(event.getPoint());
        }
    };
    private MapView listenedMapView;
    private boolean layerChangeListenerRegistered;
    private boolean destroyed;
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
        installLayerChangeListener();
        installMapClickListener();
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        controller.setChangeListener(null);
        uninstallMapClickListener();
        uninstallLayerChangeListener();
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
        layerCombo.setRenderer(new LayerCellRenderer());
        layerCombo.addActionListener(event -> {
            if (!updating) {
                controller.setSelectedLayer((Layer) layerCombo.getSelectedItem());
            }
        });
        top.add(layerCombo, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        JButton sync = new JButton(tr("Sync"));
        sync.setToolTipText(tr("Select the last clicked or played audio marker from the selected layer"));
        sync.addActionListener(event -> syncToLastMarker());
        buttons.add(sync);

        JButton refresh = new JButton(tr("Refresh"));
        refresh.addActionListener(event -> refreshLayers());
        buttons.add(refresh);
        top.add(buttons, BorderLayout.EAST);
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
            List<Layer> layers = controller.selectableLayers();
            layerCombo.setModel(new DefaultComboBoxModel<>(layers.toArray(Layer[]::new)));
            Layer selected = controller.selectedLayer();
            if (controller.shouldAutoSelect(selected, layers)) {
                selected = controller.defaultLayer(layers);
            }
            layerCombo.setSelectedItem(selected);
            controller.setSelectedLayer(selected);
            tableModel.setWaypoints(controller.waypoints());
            selectControllerRow();
        } finally {
            updating = false;
        }
    }

    private void syncToLastMarker() {
        if (!controller.syncToLastSelectedLayerMarker()) {
            new Notification(tr("No clicked or recently played audio marker is available in the selected layer."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .show();
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

    private void installMapClickListener() {
        MapFrame mapFrame = MainApplication.getMap();
        if (mapFrame != null) {
            listenedMapView = mapFrame.mapView;
            listenedMapView.addMouseListener(mapClickListener);
        }
    }

    private void uninstallMapClickListener() {
        if (listenedMapView != null) {
            listenedMapView.removeMouseListener(mapClickListener);
            listenedMapView = null;
        }
    }

    private void installLayerChangeListener() {
        if (!layerChangeListenerRegistered && MainApplication.getLayerManager() != null) {
            MainApplication.getLayerManager().addAndFireLayerChangeListener(this);
            layerChangeListenerRegistered = true;
        }
    }

    private void uninstallLayerChangeListener() {
        if (!layerChangeListenerRegistered) {
            return;
        }
        layerChangeListenerRegistered = false;
        if (MainApplication.getLayerManager() == null) {
            return;
        }
        try {
            MainApplication.getLayerManager().removeLayerChangeListener(this);
        } catch (IllegalArgumentException exception) {
            // JOSM clears layer listeners while removing the final layer before plugin teardown.
            Logging.debug(exception);
        }
    }
}
