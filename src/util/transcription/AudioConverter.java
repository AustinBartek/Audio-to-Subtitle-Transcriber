package util.transcription;

import java.io.*;

public class AudioConverter {
    public static File convertForSphinx(File input) throws Exception {
        File output = new File(input.getAbsolutePath() + ".wav");
        int count = 1;
        while (output.exists()) {
            output = new File(input.getAbsolutePath() + count + ".wav");
            count++;
        }

        ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-i", input.getAbsolutePath(), "-ac", "1", "-ar",
                "16000", "-sample_fmt", "s16", output.getAbsolutePath());
        builder.redirectErrorStream(true);

        Process ffmpegProc = builder.start();
        int result = ffmpegProc.waitFor();

        if (result != 0) {
            System.out.println("Error doing ffmpeg conversion to file!");
            throw new Exception("Could not convert file using FFMPEG!");
        }

        return output;
    }
}
