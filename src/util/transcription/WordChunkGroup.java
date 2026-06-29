package util.transcription;

import java.util.ArrayList;

public class WordChunkGroup {
    private ArrayList<WordChunk> chunks;

    public WordChunkGroup() {
        chunks = new ArrayList<>();
    }

    public ArrayList<WordChunk> getChunks() {
        return chunks;
    }

    public void addChunk(WordChunk newChunk) {
        chunks.add(newChunk);
    }

    public void addChunkAt(WordChunk newChunk, int index) {
        chunks.add(index, newChunk);
    }

    public void removeChunk(WordChunk chunk) {
        chunks.remove(chunk);
    }

    public WordChunk removeLast() {
        return chunks.removeLast();
    }

    public int getChunkIndex(WordChunk chunk) {
        return chunks.indexOf(chunk);
    }

    public int getNumChunks() {
        return chunks.size();
    }

    public long getStart() {
        long min = Long.MAX_VALUE;
        for (WordChunk c : chunks) {
            min = Math.min(c.getStart(), min);
        }
        return min;
    }

    public long getStartFrame(int frameRate) {
        long min = Long.MAX_VALUE;
        for (WordChunk c : chunks) {
            min = Math.min(c.getStartFrame(frameRate), min);
        }
        return min;
    }

    public long getEnd() {
        long max = Long.MIN_VALUE;
        for (WordChunk c : chunks) {
            max = Math.max(c.getEnd(), max);
        }
        return max;
    }

    public long getEndFrame(int frameRate) {
        long max = Long.MIN_VALUE;
        for (WordChunk c : chunks) {
            max = Math.max(c.getEndFrame(frameRate), max);
        }
        return max;
    }

    public long getDuration() {
        return getEnd() - getStart();
    }

    public int getNumLetters() {
        int count = 0;
        for (WordChunk c : chunks) {
            count += c.getWord().length();
        }
        count += getNumChunks() - 1;
        return count;
    }

    public ArrayList<String> getWords() {
        ArrayList<String> words = new ArrayList<>();
        for (WordChunk c : chunks) {
            words.add(c.getWord());
        }
        return words;
    }

    public String getAppearanceString() {
        return String.join(" ", getWords());
    }

    @Override
    public String toString() {
        return String.join(" ", getWords()) + " [" + getStart() + " : " + getEnd() + "]";
    }
}
