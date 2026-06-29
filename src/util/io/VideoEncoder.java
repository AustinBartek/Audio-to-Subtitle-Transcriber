package util.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import ui.ProgressBarDialog;
import util.rendering.ImageRenderer;
import util.rendering.RenderSettings;
import util.transcription.WordChunkGroup;
import util.transcription.WordChunker;

public class VideoEncoder {
    public static File encodeTranscriptionToVideo(WordChunkGroup allWords, RenderSettings settings, double videoTime)
            throws Exception {
        File tempVideoFile = File.createTempFile("render_", ".mov");
        tempVideoFile.deleteOnExit();

        // Format to encode the video and preserve its transparency
        Process process = new ProcessBuilder(
                "ffmpeg",
                "-f", "rawvideo",
                "-pix_fmt", "rgba",
                "-s", settings.getRenderWidth() + "x" + settings.getRenderHeight(),
                "-r", settings.getFrameRate() + "",
                "-i", "-",
                "-c:v", "prores_ks",
                "-profile:v", "4444",
                "-pix_fmt", "yuva444p10le",
                "-progress", "pipe:1",
                "-nostats",
                "-y",
                tempVideoFile.getAbsolutePath())
                .redirectErrorStream(false)
                .start();

        OutputStream ffmpegIn = process.getOutputStream();

        // Progress bar stuff
        new Thread(() -> {
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                ProgressBarDialog bar = new ProgressBarDialog(null, "Video Rendering Process");
                SwingUtilities.invokeAndWait(() -> bar.show());

                long lastUpdate = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("out_time_ms=")) {
                        long micros = Long.parseLong(line.split("=")[1]);
                        double seconds = micros / 1_000_000.0;
                        double progress = seconds / videoTime;

                        // update UI progress here
                        System.out.println("FFmpeg progress: " + progress);
                        if (System.currentTimeMillis() - lastUpdate > 50) {
                            lastUpdate = System.currentTimeMillis();
                            SwingUtilities.invokeLater(() -> {
                                bar.setProgress((int) (progress * 100), "Rendering Video");
                            });
                        }
                    }

                    if (line.startsWith("progress=end")) {
                        break;
                    }
                }

                bar.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Drawing the frames of the video and encoding them
        int width = settings.getRenderWidth(), height = settings.getRenderHeight();
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        g.setFont(settings.getWordFont().deriveFont(settings.getWordFontSize()));
        byte[] rgba = new byte[width * height * 4];

        ArrayList<WordChunkGroup> chunked = WordChunker.chunk(allWords, g, settings, width, height);
        // long maxFrame = (chunked.getLast().getEnd() * 60) / 1000;
        long maxFrame = (long) Math.ceil(videoTime * settings.getFrameRate());
        ImageRenderer renderer = new ImageRenderer(chunked, settings, g, width, height);

        for (long frameNum = 0; frameNum < maxFrame; frameNum++) {
            renderer.renderFrame(frameNum);

            // Converting the image into a byte[] that we can send to ffmpeg
            int[] pixels = ((DataBufferInt) frame.getRaster().getDataBuffer()).getData();

            for (int i = 0; i < pixels.length; i++) {
                int argb = pixels[i];

                rgba[i * 4] = (byte) ((argb >> 16) & 0xFF); // R
                rgba[i * 4 + 1] = (byte) ((argb >> 8) & 0xFF); // G
                rgba[i * 4 + 2] = (byte) (argb & 0xFF); // B
                rgba[i * 4 + 3] = (byte) ((argb >> 24) & 0xFF); // A
            }

            ffmpegIn.write(rgba);
        }

        g.dispose();
        ffmpegIn.close();
        return tempVideoFile;
    }
}
