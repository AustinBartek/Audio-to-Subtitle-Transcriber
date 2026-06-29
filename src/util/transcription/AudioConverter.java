package util.transcription;

import java.io.*;

public class AudioConverter {
    public static File convertForSphinx(File input) throws Exception {
        File output = File.createTempFile("sphinx_", ".wav");
        output.deleteOnExit();

        ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-vn", "-y", "-i", input.getAbsolutePath(), "-ac", "1",
                "-ar", "16000", "-sample_fmt", "s16", output.getAbsolutePath(), "-progress", "pipe:1", "-nostats");
        builder.inheritIO();

        Process ffmpegProc = builder.start();
        int result = ffmpegProc.waitFor();

        if (result != 0) {
            System.out.println("Error doing ffmpeg conversion to file!");
            throw new Exception("Could not convert file using FFMPEG!");
        }

        return output;
    }
}
