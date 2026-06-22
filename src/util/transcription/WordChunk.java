package util.transcription;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;

public class WordChunk {
    private TimeFrame timeFrame;
    private String word;
    private int themeIndex;
    private boolean forceChunk;

    public WordChunk(WordResult wordResult) {
        timeFrame = wordResult.getTimeFrame();
        word = wordResult.getWord().getSpelling();
        themeIndex = 0;
        forceChunk = false;
    }

    public WordChunk(String word, long start, long end) {
        timeFrame = new TimeFrame(start, end);
        this.word = word;
        themeIndex = 0;
        forceChunk = false;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public long getStart() {
        return timeFrame.getStart();
    }

    public long getStartFrame() {
        return getStart() * 60 / 1000;
    }

    public long getEnd() {
        return timeFrame.getEnd();
    }

    public long getEndFrame() {
        return getEnd() * 60 / 1000;
    }

    public long getDuration() {
        return getEnd() - getStart();
    }

    public String getWord() {
        return word;
    }

    public int getThemeIndex() {
        return themeIndex;
    }
    
    public boolean getForceChunk() {
        return forceChunk;
    }

    public void setTimeFrame(long start, long end) {
        timeFrame = new TimeFrame(start, end);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setThemeIndex(int ti) {
        themeIndex = ti;
    }

    public void setForceChunk(boolean forceChunk) {
        this.forceChunk = forceChunk;
    }
}
