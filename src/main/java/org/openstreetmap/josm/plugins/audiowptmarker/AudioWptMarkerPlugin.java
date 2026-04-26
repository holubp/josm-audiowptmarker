package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Shortcut;

public final class AudioWptMarkerPlugin extends Plugin {
    private final AudioWaypointController controller = new AudioWaypointController();
    private final List<JosmAction> actions = new ArrayList<>();
    private AudioWaypointDialog dialog;

    public AudioWptMarkerPlugin(PluginInformation info) {
        super(info);
        registerActions();
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (oldFrame != null && dialog != null) {
            oldFrame.removeToggleDialog(dialog);
            dialog.destroy();
            dialog = null;
        }
        if (newFrame != null) {
            dialog = new AudioWaypointDialog(controller);
            newFrame.addToggleDialog(dialog);
        }
    }

    private void registerActions() {
        addAction(new AudioWaypointAction(tr("Audio WPT previous"), "audiowptmarker-prev",
                tr("Center previous audio waypoint"),
                Shortcut.registerShortcut("audiowptmarker:previous", tr("Audio waypoint: previous"),
                        KeyEvent.VK_J, Shortcut.ALT_CTRL),
                controller, AudioWaypointAction.Move.PREVIOUS, false));
        addAction(new AudioWaypointAction(tr("Audio WPT current"), "audiowptmarker",
                tr("Center current audio waypoint"),
                Shortcut.registerShortcut("audiowptmarker:current", tr("Audio waypoint: current"),
                        KeyEvent.VK_K, Shortcut.ALT_CTRL),
                controller, AudioWaypointAction.Move.CURRENT, false));
        addAction(new AudioWaypointAction(tr("Audio WPT next"), "audiowptmarker-next",
                tr("Center next audio waypoint"),
                Shortcut.registerShortcut("audiowptmarker:next", tr("Audio waypoint: next"),
                        KeyEvent.VK_L, Shortcut.ALT_CTRL),
                controller, AudioWaypointAction.Move.NEXT, false));

        addAction(new AudioWaypointAction(tr("Audio WPT previous and play"), "audiowptmarker-prev-play",
                tr("Center previous audio waypoint and play it"),
                Shortcut.registerShortcut("audiowptmarker:previous-play", tr("Audio waypoint: previous and play"),
                        KeyEvent.VK_J, Shortcut.ALT_CTRL_SHIFT),
                controller, AudioWaypointAction.Move.PREVIOUS, true));
        addAction(new AudioWaypointAction(tr("Audio WPT current and play"), "audiowptmarker-play",
                tr("Center current audio waypoint and play it"),
                Shortcut.registerShortcut("audiowptmarker:current-play", tr("Audio waypoint: current and play"),
                        KeyEvent.VK_K, Shortcut.ALT_CTRL_SHIFT),
                controller, AudioWaypointAction.Move.CURRENT, true));
        addAction(new AudioWaypointAction(tr("Audio WPT next and play"), "audiowptmarker-next-play",
                tr("Center next audio waypoint and play it"),
                Shortcut.registerShortcut("audiowptmarker:next-play", tr("Audio waypoint: next and play"),
                        KeyEvent.VK_L, Shortcut.ALT_CTRL_SHIFT),
                controller, AudioWaypointAction.Move.NEXT, true));
    }

    private void addAction(JosmAction action) {
        actions.add(action);
        JMenu audioMenu = MainApplication.getMenu() == null ? null : MainApplication.getMenu().audioMenu;
        if (audioMenu != null) {
            MainMenu.add(audioMenu, action);
        }
    }
}
