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
    private int backgroundPaddingLeft, backgroundPaddingRight, backgroundPaddingUp, backgroundPaddingDown;
    private boolean roundBackgroundCorners;
    private TransitionMode transitionMode;
    private TransitionEasingMode transitionEasingMode;
    private float maxWidthRatio;
    private Font wordFont;
    private float wordFontSize;
    private boolean wordsPersistIfSilent, hideUnspokenWords;
    private int renderWidth, renderHeight, frameRate;

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
            BackgroundMode bm, int bpl, int bpr, int bpu, int bpd, boolean rbc, TransitionMode tm,
            TransitionEasingMode tem, int tal, float mwr, Font wf, float wfs, boolean wpis, boolean huw, int rw,
            int rh, int fr) {
        wordColorThemes = themes;
        outlineSize = os;
        chunkingRules = cr;
        progressMode = pm;
        scaleActiveWord = saw;
        backgroundMode = bm;
        backgroundPaddingLeft = bpl;
        backgroundPaddingRight = bpr;
        backgroundPaddingUp = bpu;
        backgroundPaddingDown = bpd;
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
        frameRate = fr;
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

    public int getBackgroundPaddingLeft() {
        return backgroundPaddingLeft;
    }

    public void setBackgroundPaddingLeft(int bpl) {
        backgroundPaddingLeft = bpl;
    }

    public int getBackgroundPaddingRight() {
        return backgroundPaddingRight;
    }

    public void setBackgroundPaddingRight(int bpr) {
        backgroundPaddingRight = bpr;
    }

    public int getBackgroundPaddingUp() {
        return backgroundPaddingUp;
    }

    public void setBackgroundPaddingUp(int bpu) {
        backgroundPaddingUp = bpu;
    }

    public int getBackgroundPaddingDown() {
        return backgroundPaddingDown;
    }

    public void setBackgroundPaddingDown(int bpd) {
        backgroundPaddingDown = bpd;
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

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int fr) {
        frameRate = fr;
    }
}
