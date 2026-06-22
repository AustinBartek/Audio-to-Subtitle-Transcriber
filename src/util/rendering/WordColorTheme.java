package util.rendering;

import java.awt.Color;

public class WordColorTheme {
    private Color normalColor, outlineColor, progressColor, backgroundColor;

    public WordColorTheme(Color nc, Color oc, Color pc, Color bc) {
        normalColor = nc;
        outlineColor = oc;
        progressColor = pc;
        backgroundColor = bc;
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setNormalColor(Color nc) {
        normalColor = nc;
    }

    public void setOutlineColor(Color oc) {
        outlineColor = oc;
    }

    public void setProgressColor(Color pc) {
        progressColor = pc;
    }

    public void setBackgroundColor(Color bc) {
        backgroundColor = bc;
    }
}
