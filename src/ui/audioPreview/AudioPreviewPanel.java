package ui.audioPreview;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.function.Consumer;

import ui.TranscriberWindow;

public class AudioPreviewPanel extends JPanel {
    TranscriberWindow parent;
    private JPanel audioDisplayPanel;
    private JButton playPauseButton;
    private float[] sampleData;
    private Clip audioClip;
    private Timer drawTimer;
    private double playheadProgress;

    public AudioPreviewPanel(TranscriberWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.cyan));
        sampleData = new float[0];

        // Handles the rendering of waveform and playhead
        audioDisplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int width = getWidth(), height = getHeight();
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);

                if (sampleData.length == 0) {
                    return;
                }

                Graphics2D g2d = (Graphics2D) g;
                int numSubsets = width;
                int subsetLength = Math.max(1, sampleData.length / numSubsets);
                g2d.setColor(Color.blue);
                for (int i = 0; i < numSubsets; i++) {
                    float maxVal = 0;
                    // Find peak in this bin
                    for (int j = 0; j < subsetLength; j++) {
                        float val = Math.abs(sampleData[i * subsetLength + j]);
                        if (val > maxVal)
                            maxVal = val;
                    }

                    int barHeight = (int) (maxVal * (height / 2));
                    int y = (height / 2) - barHeight;
                    g2d.fillRect(i, y, 1, barHeight * 2);
                }

                // Playhead
                g2d.setColor(Color.red);
                int playHeadX = (int) (playheadProgress * width);
                g2d.fillRect(playHeadX - 2, 0, 5, height);

                // Milli indicator
                int millis = (int) (audioClip.getMicrosecondPosition() / 1000);
                g2d.setFont(Font.decode(Font.MONOSPACED).deriveFont(14f));
                g2d.setColor(Color.white);
                FontMetrics fm = g2d.getFontMetrics();
                String dispString = millis + "";
                int padding = 5;
                int textWidth = fm.stringWidth(dispString);
                int x = width - textWidth - padding;
                int y = fm.getAscent() + padding;
                g2d.drawString(dispString, x, y);
            }
        };

        // Click and drag functionality for the audio display panel
        Consumer<MouseEvent> audioDisplayInteract = (e) -> {
            if (audioClip == null)
                return;
            if (audioClip.isOpen()) {
                int clickX = e.getX();
                int width = audioDisplayPanel.getWidth();
                long totalMicros = audioClip.getMicrosecondLength();
                long seekPosition = (long) ((clickX / (double) width) * totalMicros);

                boolean wasRunning = audioClip.isRunning();
                audioClip.stop();
                audioClip.flush();
                audioClip.setMicrosecondPosition(seekPosition);
                if (wasRunning) {
                    audioClip.start();
                }

                updatePlayhead();
                audioDisplayPanel.repaint();
                updateRenderPreview();
            }
        };
        audioDisplayPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                audioDisplayInteract.accept(e);
            }
        });
        audioDisplayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                audioDisplayInteract.accept(e);
            }
        });
        add(audioDisplayPanel, BorderLayout.CENTER);

        // Play/pause button functionality
        playPauseButton = new JButton("Play");
        playPauseButton.addActionListener((e) -> {
            if (audioClip == null)
                return;
            if (audioClip.isRunning()) {
                audioClip.stop();
                playPauseButton.setText("Play");
            } else {
                if (audioClip.getMicrosecondPosition() >= audioClip.getMicrosecondLength()) {
                    audioClip.setMicrosecondPosition(0);
                }
                audioClip.start();
                playPauseButton.setText("Pause");
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(0, 50));
        buttonPanel.add(playPauseButton);
        buttonPanel.setBackground(Color.lightGray);
        buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        add(buttonPanel, BorderLayout.SOUTH);

        drawTimer = new Timer(100, (e) -> {
            if (audioClip == null)
                return;
            if (audioClip.isRunning()) {
                updatePlayhead();
                audioDisplayPanel.repaint();
                updateRenderPreview();
            }
        });
        drawTimer.start();
    }

    private void updatePlayhead() {
        long current = audioClip.getMicrosecondPosition();
        long total = audioClip.getMicrosecondLength();
        playheadProgress = (double) current / total;
    }

    private void updateRenderPreview() {
        if (audioClip != null) {
            int frame = (int) (audioClip.getMicrosecondPosition() / 16666);
            parent.getRenderPreviewPanel().setFrame(frame);
        }
    }

    public void goToStart() {
        if (audioClip == null)
            return;

        if (audioClip.isRunning()) {
            audioClip.stop();
        }
        audioClip.setMicrosecondPosition(0);
        updatePlayhead();
        audioDisplayPanel.repaint();
        updateRenderPreview();
    }

    public void goToEnd() {
        if (audioClip == null)
            return;

        if (audioClip.isRunning()) {
            audioClip.stop();
        }
        audioClip.setMicrosecondPosition(audioClip.getMicrosecondLength() - 1);
        updatePlayhead();
        audioDisplayPanel.repaint();
        updateRenderPreview();
    }

    public void stopAndClose() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
        }
    }

    public void updateDisplay(File newAudioFile) {
        try {
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();
            }

            // Processing audio data into float array
            AudioInputStream ais = AudioSystem.getAudioInputStream(newAudioFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] bytes = baos.toByteArray();
            float[] samples = new float[bytes.length / 2];
            for (int i = 0; i < samples.length; i++) {
                int lowByte = bytes[i * 2] & 0xff;
                int highByte = bytes[i * 2 + 1];
                short sample = (short) ((highByte << 8) | lowByte);
                samples[i] = sample / 32768.0f;
            }

            sampleData = samples;
            loadClip(newAudioFile);
            audioDisplayPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void loadClip(File audioFile) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            clip.open(ais);
            audioClip = clip;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
