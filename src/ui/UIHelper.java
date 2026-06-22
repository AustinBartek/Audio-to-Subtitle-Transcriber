package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIHelper {
    public static JButton createIconButton(BufferedImage image, String tooltipText, int imageSize) {
        JButton newButton = new JButton();
        newButton.setToolTipText(tooltipText);
        newButton.setIcon(new ImageIcon(image.getScaledInstance(imageSize, imageSize, 0)));
        return newButton;
    }

    public static void setMaxAndPreferredSize(Component c, Dimension d) {
        c.setPreferredSize(d);
        c.setMaximumSize(d);
    }
}
