package util.rendering;

import java.awt.Font;
import java.util.ArrayList;

import util.transcription.ChunkingRules;

public class RenderSettings {
    private ArrayList<WordColorTheme> wordColorThemes;
    private int outlineSize;
    private ChunkingRules chunkingRules;
    private ProgressMode progressMode;
    private boolean scaleActiveWord;
    private int transitionAnimationLength;
    private BackgroundMode backgroundMode;
    private int wordBackgroundPaddingX, wordBackgroundPaddingY;
    private boolean roundBackgroundCorners;
    private TransitionMode transitionMode;
    private TransitionEasingMode transitionEasingMode;
    private float maxWidthRatio;
    private Font wordFont;
    private float wordFontSize;
    private boolean wordsPersistIfSilent, hideUnspokenWords;
    private int renderWidth, renderHeight;

    public enum BackgroundMode {
        NONE,
        ALL,
        ACTIVE;
    }

    public enum ProgressMode {
        NONE,
        SLIDER,
        WORD;
    }

    public enum TransitionMode {
        NONE,
        POP,
        SLIDEUP,
        SLIDERIGHT,
        SLIDELEFT,
        SLIDEDOWN;
    }

    public enum TransitionEasingMode {
        LINEAR,
        COS,
        SQRT,
        BOUNCE;
    }

    public RenderSettings(ArrayList<WordColorTheme> themes, int os, ChunkingRules cr, ProgressMode pm, boolean saw,
            BackgroundMode bm, int wbpx, int wbpy, boolean rbc, TransitionMode tm, TransitionEasingMode tem, int tal,
            float mwr, Font wf, float wfs, boolean wpis, boolean huw, int rw, int rh) {
        wordColorThemes = themes;
        outlineSize = os;
        chunkingRules = cr;
        progressMode = pm;
        scaleActiveWord = saw;
        backgroundMode = bm;
        wordBackgroundPaddingX = wbpx;
        wordBackgroundPaddingY = wbpy;
        roundBackgroundCorners = rbc;
        transitionMode = tm;
        transitionEasingMode = tem;
        transitionAnimationLength = tal;
        maxWidthRatio = mwr;
        wordFont = wf;
        wordFontSize = wfs;
        wordsPersistIfSilent = wpis;
        hideUnspokenWords = huw;
        renderWidth = rw;
        renderHeight = rh;
    }

    public ArrayList<WordColorTheme> getThemes() {
        return wordColorThemes;
    }

    public WordColorTheme getTheme(int index) {
        return wordColorThemes.get(index);
    }

    public void setThemes(ArrayList<WordColorTheme> newThemes) {
        wordColorThemes = newThemes;
    }

    public int getThemeCount() {
        return wordColorThemes.size();
    }

    public int getOutlineSize() {
        return outlineSize;
    }

    public void setOutlineSize(int os) {
        outlineSize = os;
    }

    public ChunkingRules getChunkingRules() {
        return chunkingRules;
    }

    public void setChunkingRules(ChunkingRules cr) {
        chunkingRules = cr;
    }

    public ProgressMode getProgressMode() {
        return progressMode;
    }

    public void setProgressMode(ProgressMode pm) {
        progressMode = pm;
    }

    public boolean scaleActiveWord() {
        return scaleActiveWord;
    }

    public void setScaleActiveWord(boolean saw) {
        scaleActiveWord = saw;
    }

    public int getTransitionAnimationLength() {
        return transitionAnimationLength;
    }

    public void setTransitionAnimationLength(int tal) {
        transitionAnimationLength = tal;
    }

    public BackgroundMode getBackgroundMode() {
        return backgroundMode;
    }

    public void setWordBackgroundMode(BackgroundMode bm) {
        backgroundMode = bm;
    }

    public int getWordBackgroundPaddingX() {
        return wordBackgroundPaddingX;
    }

    public void setWordBackgroundPaddingX(int padX) {
        wordBackgroundPaddingX = padX;
    }

    public int getWordBackgroundPaddingY() {
        return wordBackgroundPaddingY;
    }

    public void setWordBackgroundPaddingY(int padY) {
        wordBackgroundPaddingY = padY;
    }

    public boolean roundBackgroundCorners() {
        return roundBackgroundCorners;
    }

    public void setRoundBackgroundCorners(boolean enabled) {
        roundBackgroundCorners = enabled;
    }

    public float getMaxWidthRatio() {
        return maxWidthRatio;
    }

    public void setMaxWidthRatio(float mwr) {
        maxWidthRatio = mwr;
    }

    public Font getWordFont() {
        return wordFont;
    }

    public void setWordFont(Font font) {
        wordFont = font;
    }

    public float getWordFontSize() {
        return wordFontSize;
    }

    public void setWordFontSize(float fontSize) {
        wordFontSize = fontSize;
    }

    public boolean wordsPersistIfSilentEnabled() {
        return wordsPersistIfSilent;
    }

    public void setWordsPersistIfSilentEnabled(boolean enabled) {
        wordsPersistIfSilent = enabled;
    }

    public boolean hideUnspokenWords() {
        return hideUnspokenWords;
    }

    public void setHideUnspokenWords(boolean enabled) {
        hideUnspokenWords = enabled;
    }

    public TransitionMode getTransitionMode() {
        return transitionMode;
    }

    public void setTransitionMode(TransitionMode tm) {
        transitionMode = tm;
    }

    public TransitionEasingMode getTransitionEasingMode() {
        return transitionEasingMode;
    }

    public void setTransitionEasingMode(TransitionEasingMode tem) {
        transitionEasingMode = tem;
    }

    public int getRenderWidth() {
        return renderWidth;
    }

    public void setRenderWidth(int rw) {
        renderWidth = rw;
    }

    public int getRenderHeight() {
        return renderHeight;
    }

    public void setRenderHeight(int rh) {
        renderHeight = rh;
    }
}
