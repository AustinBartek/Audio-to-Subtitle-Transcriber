package ui;

import javax.swing.JOptionPane;

public class PopupManager {
    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static boolean getConfirmation(String message) {
        int option = JOptionPane.showConfirmDialog(null, message);
        return option == JOptionPane.YES_OPTION;
    }

    public static String getUserInput(String message) {
        String userInput = JOptionPane.showInputDialog(null, message);
        return userInput;
    }
}
