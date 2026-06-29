package ui.renderpreview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import ui.TranscriberWindow;
import util.rendering.ImageRenderer;
import util.rendering.RenderSettings;
import util.transcription.WordChunkGroup;
import util.transcription.WordChunker;

public class RenderPreviewPanel extends JPanel {
    private TranscriberWindow parent;
    private JPanel renderPanel;
    private int currentFrame;
    private JSlider frameSlider;
    private JLabel frameLabel;

    public RenderPreviewPanel(TranscriberWindow parent) {
        this.parent = parent;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.magenta));
        int frameRate = parent.getRenderSettings().getFrameRate();

        currentFrame = 0;
        frameSlider = new JSlider(JSlider.HORIZONTAL, 0, (int) parent.getChunkGroup().getEndFrame(frameRate), 0);
        frameSlider.addChangeListener((e) -> {
            currentFrame = frameSlider.getValue();
            updateFrameLabel();
            updateRender();
        });
        frameSlider.setPreferredSize(new Dimension(0, 20));

        frameLabel = new JLabel();
        frameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frameLabel.setPreferredSize(new Dimension(100, 20));
        frameLabel.setMaximumSize(new Dimension(100, 20));
        updateFrameLabel();

        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int x = 0; x <= getWidth(); x += 30) {
                    for (int y = 0; y <= getHeight(); y += 30) {
                        if (((x % 60) >= 30) == ((y % 60) >= 30)) {
                            g.setColor(Color.lightGray);
                        } else {
                            g.setColor(Color.darkGray);
                        }
                        g.fillRect(x, y, 30, 30);
                    }
                }

                RenderSettings settings = parent.getRenderSettings();
                int width = settings.getRenderWidth(), height = settings.getRenderHeight();
                BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = frame.createGraphics();
                g2.setFont(settings.getWordFont().deriveFont(settings.getWordFontSize()));
                ArrayList<WordChunkGroup> chunkedGroups = WordChunker.chunk(parent.getChunkGroup(), g2, settings, width,
                        height);
                ImageRenderer renderer = new ImageRenderer(chunkedGroups, settings, g2, width, height);
                renderer.renderFrame(currentFrame);

                float scale = 1 / Math.max((float) height / getHeight(), (float) width / getWidth());
                int renderedWidth = (int) (scale * width), renderedHeight = (int) (scale * height);
                int renderX = (getWidth() - renderedWidth) / 2, renderY = (getHeight() - renderedHeight) / 2;

                g.setColor(Color.black);
                g.fillRect(renderX, renderY, renderedWidth, renderedHeight);
                g.drawImage(frame, renderX, renderY, renderedWidth, renderedHeight, null);
            }
        };

        add(renderPanel);
        add(frameLabel);
        add(frameSlider);
    }

    public void setFrame(int frame) {
        int useFrame = Math.min(frame, frameSlider.getMaximum());
        currentFrame = useFrame;
        frameSlider.setValue(useFrame);
        updateFrameLabel();
        updateRender();
    }

    public void updateRender() {
        renderPanel.repaint();
    }

    public void updateSliderMaximum() {
        int frameRate = parent.getRenderSettings().getFrameRate();
        if (parent.getVideoTime() == -1) {
            frameSlider.setMaximum((int) (parent.getChunkGroup().getEndFrame(frameRate)));
        } else {
            frameSlider.setMaximum((int) (parent.getVideoTime() * frameRate));
        }
        currentFrame = Math.min(currentFrame, frameSlider.getMaximum());
        updateFrameLabel();
    }

    private void updateFrameLabel() {
        frameLabel.setText("Frame: " + currentFrame + "/" + frameSlider.getMaximum());
    }
}
