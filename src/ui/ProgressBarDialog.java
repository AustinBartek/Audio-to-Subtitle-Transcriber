package ui;

import javax.swing.*;
import java.awt.*;

public class ProgressBarDialog {

    private final JDialog dialog;
    private final JProgressBar bar;
    private final JLabel label;

    public ProgressBarDialog(JFrame parent, String title) {
        dialog = new JDialog(parent, title, false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(350, 120);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parent);

        label = new JLabel("Starting...", SwingConstants.CENTER);

        bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);

        dialog.add(label, BorderLayout.NORTH);
        dialog.add(bar, BorderLayout.CENTER);
    }

    public void setProgress(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            bar.setValue(percent);
            bar.setString(percent + "%");
            label.setText(message);
        });
    }

    public void show() {
        dialog.setVisible(true);
    }

    public void close() {
        dialog.dispose();
    }
}