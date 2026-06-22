package util.transcription;

import java.awt.Graphics2D;
import java.util.ArrayList;

import util.rendering.RenderSettings;

public class WordChunker {
    public static ArrayList<WordChunkGroup> chunk(WordChunkGroup group, Graphics2D g, RenderSettings settings,
            int frameWidth, int frameHeight) {
        ArrayList<WordChunkGroup> chunks = new ArrayList<>();
        WordChunkGroup currentGroup = new WordChunkGroup();
        for (WordChunk chunk : group.getChunks()) {
            // If there are no chunks yet, you must add at least one, otherwise you'll never
            // make a chunk!
            if (currentGroup.getNumChunks() == 0 ||
                    (!chunk.getForceChunk() &&
                            settings.getChunkingRules().canAddChunk(currentGroup, chunk, g, settings, frameWidth,
                                    frameHeight))) {
                currentGroup.addChunk(chunk);
            } else {
                chunks.add(currentGroup);
                currentGroup = new WordChunkGroup();
                currentGroup.addChunk(chunk);
            }
        }
        // Don't forget the last one
        if (currentGroup.getNumChunks() > 0) {
            chunks.add(currentGroup);
        }
        return chunks;
    }
}
