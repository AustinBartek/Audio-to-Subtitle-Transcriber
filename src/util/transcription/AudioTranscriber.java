package util.transcription;

import java.io.File;
import java.io.FileInputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

public class AudioTranscriber {
    /**
     * Assumes <b>inputFile</b> is already in the correct Sphinx4 format.
     * 
     * @param inputFile - an audio file with speech
     * @return a WordChunkGroup that represents the audio of the file,
     *         dissected into the groups that they will appear in visually.
     */
    public static WordChunkGroup transcribeAudio(File inputFile) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        WordChunkGroup group = new WordChunkGroup();
        FileInputStream stream = new FileInputStream(inputFile);

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            for (WordResult wordResult : result.getWords()) {
                if (wordResult.isFiller()) {
                    continue;
                }
                WordChunk newChunk = new WordChunk(wordResult);
                group.addChunk(newChunk);
            }
        }
        recognizer.stopRecognition();
        return group;
    }
}
