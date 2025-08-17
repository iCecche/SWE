package ui.panels;

import java.util.function.Consumer;

// Configuration class that holds all view-specific data
public class PanelConfiguration {
    final UsersPanel.ViewMode viewMode;
    final int targetUserId;
    final Consumer<Integer> userSelectionCallback;

    PanelConfiguration(UsersPanel.ViewMode viewMode, int targetUserId, Consumer<Integer> userSelectionCallback) {
        this.viewMode = viewMode;
        this.targetUserId = targetUserId;
        this.userSelectionCallback = userSelectionCallback;
    }
}