package util.transcription;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.SwingUtilities;

import org.vosk.Model;
import org.vosk.Recognizer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ui.ProgressBarDialog;

public class AudioTranscriber {
    /**
     * Assumes <b>inputFile</b> is already in the correct format.
     * 
     * @param inputFile - an audio file with speech
     * @return a WordChunkGroup that represents the audio of the file,
     *         dissected into the groups that they will appear in visually.
     */
    public static WordChunkGroup transcribeAudio(File inputFile) throws Exception {
        // Initializing transcription model (vosk)
        Path jarDir = Paths.get(AudioTranscriber.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParent();
        Path modelPath = jarDir.resolve("vosk-model-small-en-us-0.15");
        Model model = new Model(modelPath.toString());
        Recognizer recognizer = new Recognizer(model, 16000.0f);
        recognizer.setWords(true);

        // Setting up all variables necessary to transcribe + render progress
        AudioInputStream ais = AudioSystem.getAudioInputStream(inputFile);
        byte[] buffer = new byte[16384];
        int n;
        List<JsonObject> words = new ArrayList<>();
        long totalBytes = inputFile.length(), processedBytes = 0, lastUpdateTime = 0;
        ProgressBarDialog bar = new ProgressBarDialog(null, "Transcription Progress");
        SwingUtilities.invokeLater(() -> bar.show());

        while ((n = ais.read(buffer)) >= 0) {
            // Progress Bar
            processedBytes += n;
            double percent = (double) processedBytes / totalBytes;
            if (System.currentTimeMillis() - lastUpdateTime > 50) {
                lastUpdateTime = System.currentTimeMillis();
                SwingUtilities.invokeLater(() -> {
                    bar.setProgress((int) (percent * 100), "Transcribing Audio");
                });
            }

            // Transcription
            if (recognizer.acceptWaveForm(buffer, n)) {
                JsonObject obj = JsonParser.parseString(recognizer.getResult()).getAsJsonObject();

                if (obj.has("result")) {
                    for (JsonElement e : obj.getAsJsonArray("result")) {
                        words.add(e.getAsJsonObject());
                    }
                }
            }
        }
        // Ensuring last transcriptions are also added
        JsonObject finalObj = JsonParser.parseString(recognizer.getFinalResult()).getAsJsonObject();
        if (finalObj.has("result")) {
            for (JsonElement e : finalObj.getAsJsonArray("result")) {
                words.add(e.getAsJsonObject());
            }
        }
        recognizer.close();
        bar.close();

        // Formatting results into WordChunkGroup
        WordChunkGroup group = new WordChunkGroup();
        for (JsonObject wordObj : words) {
            long start = (long) (wordObj.get("start").getAsFloat() * 1000);
            long end = (long) (wordObj.get("end").getAsFloat() * 1000);
            String word = wordObj.get("word").getAsString();
            group.addChunk(new WordChunk(word, start, end));
        }

        return group;
    }

    public static double getAudioSecondLength(File audioFile) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = ais.getFormat();
            long frames = ais.getFrameLength();
            double duration = frames / format.getFrameRate();
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
