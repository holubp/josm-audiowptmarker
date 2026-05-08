package org.openstreetmap.josm.plugins.audiowptmarker;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.tools.Shortcut;

final class AudioWaypointAction extends JosmAction {
    enum Move {
        PREVIOUS,
        CURRENT,
        NEXT
    }

    private final AudioWaypointController controller;
    private final Move move;
    private final boolean play;

    AudioWaypointAction(
            String name,
            String iconName,
            String tooltip,
            Shortcut shortcut,
            AudioWaypointController controller,
            Move move,
            boolean play
    ) {
        super(name, iconName, tooltip, shortcut, true, toolbarId(name), true);
        this.controller = controller;
        this.move = move;
        this.play = play;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        AudioWaypointController.NavigationResult result = switch (move) {
            case PREVIOUS -> controller.previous(play);
            case CURRENT -> controller.current(play);
            case NEXT -> controller.next(play);
        };
        switch (result) {
            case MOVED -> {
                // No UI feedback needed for successful navigation.
            }
            case NO_WAYPOINTS -> new Notification(tr("No audio waypoints are available in the selected marker layer."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .show();
            case BEFORE_FIRST -> showBoundaryWarning(tr("Already at the first audio waypoint."));
            case AFTER_LAST -> showBoundaryWarning(tr("Already at the last audio waypoint."));
        }
    }

    private static void showBoundaryWarning(String message) {
        JOptionPane.showMessageDialog(
                MainApplication.getMainFrame(),
                message,
                tr("Audio Waypoints"),
                JOptionPane.WARNING_MESSAGE);
    }

    private static String toolbarId(String name) {
        return "audiowptmarker/" + name.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }
}
