import ui.DashboardUI;
import ui.LoginPanel;
import ui.RegisterPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}