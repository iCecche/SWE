import javax.swing.*;
import ui.panels.LoginPanel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}