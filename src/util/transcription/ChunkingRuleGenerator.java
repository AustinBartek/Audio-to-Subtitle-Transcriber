package util.transcription;

import java.awt.FontMetrics;
import java.util.function.Function;

public class ChunkingRuleGenerator {
    public enum ChunkGeneratorType {
        WordCount((num) -> generateWordCountRules((int) Math.ceil(num))),
        LetterCount((num) -> generateLetterCountRules((int) Math.ceil(num))),
        TimeLimit((num) -> generateTimeLimitRules(num)),
        LineLimit((num) -> generateLineLimitRules((int) Math.ceil(num)));

        private final Function<Float, ChunkingRules> getter;

        private ChunkGeneratorType(Function<Float, ChunkingRules> getter) {
            this.getter = getter;
        }

        public Function<Float, ChunkingRules> getGetter() {
            return getter;
        }
    }

    public static ChunkingRules generateWordCountRules(int maxWords) {
        return (g, nc, graphics, settings, w, h) -> {
            return g.getNumChunks() == maxWords - 1;
        };
    }

    public static ChunkingRules generateLetterCountRules(int maxLetters) {
        return (g, nc, graphics, settings, w, h) -> {
            int count = g.getNumLetters() + nc.getWord().length() + 1;
            return count <= maxLetters;
        };
    }

    public static ChunkingRules generateTimeLimitRules(float maxSeconds) {
        return (g, nc, graphics, settings, w, h) -> {
            float newDuration = (float) (nc.getEnd() - g.getStart());
            return newDuration <= (maxSeconds * 1000);
        };
    }

    public static ChunkingRules generateLineLimitRules(int maxLines) {
        return (g, nc, graphics, settings, w, h) -> {
            FontMetrics metrics = graphics.getFontMetrics();
            int effectiveFrameWidth = (int) (settings.getMaxWidthRatio() * w);
            int lineCount = 0;

            WordChunkGroup testGroup = new WordChunkGroup();
            for (WordChunk c : g.getChunks()) {
                testGroup.addChunk(c);
            }
            testGroup.addChunk(nc);

            WordChunkGroup currentLine = new WordChunkGroup();
            for (WordChunk chunk : testGroup.getChunks()) {
                currentLine.addChunk(chunk);
                int strWidth = metrics.stringWidth(currentLine.getAppearanceString());
                if (strWidth > effectiveFrameWidth) {
                    lineCount++;
                    // if this chunk was way too long then it needs to go somewhere, so just keep.
                    if (currentLine.getNumChunks() == 1) {
                        currentLine = new WordChunkGroup();
                        continue;
                    }
                    currentLine.removeLast();
                    currentLine = new WordChunkGroup();
                    currentLine.addChunk(chunk);
                }
            }

            if (currentLine.getNumChunks() > 0)
                lineCount++;
            return lineCount <= maxLines;
        };
    }
}
