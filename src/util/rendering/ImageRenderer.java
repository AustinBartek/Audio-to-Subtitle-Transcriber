package util.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import util.transcription.WordChunk;
import util.transcription.WordChunkGroup;

public class ImageRenderer {
    private RenderSettings settings;
    private Graphics2D g;
    private int frameWidth, frameHeight, ascentHeight;
    private long lastEndFrame; // Last frame of the most recent chunkGroup that ended
    private ArrayList<WordChunkGroupDisplayer> displayers;

    public ImageRenderer(ArrayList<WordChunkGroup> chunkedGroups, RenderSettings s, Graphics2D g, int fw, int fh) {
        settings = s;
        this.g = g;
        frameWidth = fw;
        frameHeight = fh;
        displayers = new ArrayList<>();

        int frameRate = settings.getFrameRate();

        // Populating displayers
        FontMetrics metrics = g.getFontMetrics();
        ascentHeight = metrics.getAscent();
        int effectiveFrameWidth = (int) (settings.getMaxWidthRatio() * frameWidth);
        int lineHeight = metrics.getHeight();
        int spaceWidth = metrics.stringWidth(" ");

        for (WordChunkGroup group : chunkedGroups) {
            // Organizing the WordChunkGroup into individual lines based on width
            ArrayList<WordChunkGroup> regroupedLines = new ArrayList<>();
            ArrayList<Integer> regroupedLineWidths = new ArrayList<>();

            WordChunkGroup currentGroup = new WordChunkGroup();
            for (WordChunk chunk : group.getChunks()) {
                currentGroup.addChunk(chunk);
                int strWidth = metrics.stringWidth(currentGroup.getAppearanceString());
                if (strWidth > effectiveFrameWidth) {
                    // if this chunk was way too long then it needs to go somewhere, so just keep.
                    if (currentGroup.getNumChunks() == 1)
                        continue;
                    currentGroup.removeLast();
                    regroupedLines.add(currentGroup);
                    regroupedLineWidths.add(metrics.stringWidth(currentGroup.getAppearanceString()));
                    currentGroup = new WordChunkGroup();
                    currentGroup.addChunk(chunk);
                }
            }
            regroupedLines.add(currentGroup);
            regroupedLineWidths.add(metrics.stringWidth(currentGroup.getAppearanceString()));

            // Calculating the rects of each WordChunkDisplayer
            ArrayList<WordChunkDisplayer> newChunkDisplayers = new ArrayList<>();
            int totalLineHeight = lineHeight * regroupedLines.size();
            int startY = (frameHeight - totalLineHeight) / 2;
            for (int i = 0; i < regroupedLines.size(); i++) {
                WordChunkGroup line = regroupedLines.get(i);
                int lineWidth = regroupedLineWidths.get(i);
                int startX = (frameWidth - lineWidth) / 2;
                int runningLineWidth = 0;
                for (int j = 0; j < line.getNumChunks(); j++) {
                    WordChunk chunk = line.getChunks().get(j);
                    int chunkWidth = metrics.stringWidth(chunk.getWord());
                    Rectangle displayerRect = new Rectangle(
                            startX + runningLineWidth,
                            startY + i * lineHeight,
                            chunkWidth,
                            lineHeight);
                    WordChunkDisplayer newChunkDisplayer = new WordChunkDisplayer(displayerRect, chunk);
                    newChunkDisplayers.add(newChunkDisplayer);
                    runningLineWidth += chunkWidth + spaceWidth; // Update the running width count for proper x pos
                }
            }

            WordChunkGroupDisplayer newGroupDisplayer = new WordChunkGroupDisplayer(group.getStartFrame(frameRate),
                    group.getEndFrame(frameRate), newChunkDisplayers);
            displayers.add(newGroupDisplayer);
        }
    }

    public void renderFrame(long frameNum) {
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // Transparent bg
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, frameWidth, frameHeight);
        g.setComposite(AlphaComposite.SrcOver);

        // Deciding the active word chunks ============================
        WordChunkGroupDisplayer active = null;
        for (int i = 0; i < displayers.size(); i++) {
            // Words PERSIST
            if (settings.wordsPersistIfSilentEnabled()) {
                WordChunkGroupDisplayer current = displayers.get(i);

                // If it's the last group, it stays active indefinitely after it starts
                if (i == displayers.size() - 1) {
                    if (frameNum >= current.startFrame) {
                        active = current;
                        lastEndFrame = (displayers.size() <= 1) ? 0 : displayers.get(i - 1).endFrame;
                    }
                    break;
                }

                WordChunkGroupDisplayer next = displayers.get(i + 1);

                // Current group stays active until the NEXT group officially starts
                if (frameNum >= current.startFrame && frameNum < next.startFrame) {
                    active = current;
                    if (i == 0) {
                        lastEndFrame = 0;
                    } else {
                        lastEndFrame = displayers.get(i - 1).endFrame;
                    }
                    break;
                }
            } else {
                // NORMAL behavior (no words persisting)
                WordChunkGroupDisplayer d = displayers.get(i);
                if (frameNum > d.startFrame && frameNum < d.endFrame) {
                    active = d;

                    if (i == 0) {
                        lastEndFrame = 0;
                    } else {
                        lastEndFrame = displayers.get(i - 1).endFrame;
                    }
                    break;
                }
            }
        }

        if (active != null) {
            active.renderFrame(frameNum);
        }
    }

    class WordChunkGroupDisplayer {
        private long startFrame, endFrame;
        private ArrayList<WordChunkDisplayer> chunkDisplayers;

        public WordChunkGroupDisplayer(long startFrame, long endFrame, ArrayList<WordChunkDisplayer> cds) {
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            chunkDisplayers = cds;
        }

        public void renderFrame(long frameNum) {
            int frameRate = settings.getFrameRate();
            AffineTransform oldTransform = g.getTransform();

            // Transition animations ============================================
            long useStart = startFrame;
            if (settings.wordsPersistIfSilentEnabled()) {
                useStart = lastEndFrame;
            }
            float t = (frameNum - useStart) / (float) settings.getTransitionAnimationLength();
            t = Math.max(0, Math.min(t, 1));
            switch (settings.getTransitionEasingMode()) {
                case BOUNCE:
                    t = (-1.875f * t * t + 2.875f * t);
                    break;
                case COS:
                    t = ((float) -Math.cos(Math.PI * t) + 1) / 2;
                    break;
                case LINEAR:
                    break;
                case SQRT:
                    t = (float) Math.sqrt(t);
                    break;
                default:
                    break;
            }

            switch (settings.getTransitionMode()) {
                case NONE:
                    break;
                case POP:
                    double scale = 0.8 + 0.2 * t;

                    int centerX = frameWidth / 2;
                    int centerY = frameHeight / 2;

                    g.translate(centerX, centerY);
                    g.scale(scale, scale);
                    g.translate(-centerX, -centerY);
                    break;
                case SLIDELEFT:
                    int slideLeft = (int) (t * frameWidth);
                    g.translate(-frameWidth + slideLeft, 0);
                    break;
                case SLIDERIGHT:
                    int slideRight = (int) (t * frameWidth);
                    g.translate(frameWidth - slideRight, 0);
                    break;
                case SLIDEUP:
                    int slideUp = (int) (t * frameHeight);
                    g.translate(0, frameHeight - slideUp);
                    break;
                case SLIDEDOWN:
                    int slideDown = (int) (t * frameHeight);
                    g.translate(0, -frameHeight + slideDown);
                default:
                    break;

            }

            // Rendering all the chunks ==================================================
            switch (settings.getBackgroundMode()) {
                case ACTIVE:
                    for (WordChunkDisplayer displayer : chunkDisplayers) {
                        if (frameNum >= displayer.chunk.getStartFrame(frameRate)
                                && frameNum <= displayer.chunk.getEndFrame(frameRate)) {
                            displayer.renderBackground(g);
                        }
                    }
                    break;
                case ALL:
                    for (WordChunkDisplayer displayer : chunkDisplayers) {
                        displayer.renderBackground(g);
                    }
                    break;
                case NONE:
                    break;
                default:
                    break;
            }

            // Implementing the hide unspoken words option
            ArrayList<WordChunkDisplayer> shouldRender = new ArrayList<>();
            if (settings.hideUnspokenWords()) {
                for (WordChunkDisplayer displayer : chunkDisplayers) {
                    if (frameNum >= displayer.chunk.getStartFrame(frameRate)) {
                        shouldRender.add(displayer);
                    } else {
                        break;
                    }
                }
            } else {
                shouldRender.addAll(chunkDisplayers);
            }

            // Implementing the scale active word option
            WordChunkDisplayer scaledDisplayer = null;
            if (settings.scaleActiveWord()) {
                for (WordChunkDisplayer displayer : chunkDisplayers) {
                    if (frameNum >= displayer.chunk.getStartFrame(frameRate)
                            && frameNum <= displayer.chunk.getEndFrame(frameRate)) {
                        scaledDisplayer = displayer;
                        break;
                    }
                }
            }
            BiConsumer<WordChunkDisplayer, WordChunkDisplayer> scaleG = (disp, check) -> {
                if (disp != check)
                    return;
                int centerX = (int) (disp.rect.x + disp.rect.width / 2);
                int centerY = (int) (disp.rect.y + disp.rect.height / 2);
                g.translate(centerX, centerY);
                g.scale(1.1, 1.1);
                g.translate(-centerX, -centerY);
            };
            BiConsumer<WordChunkDisplayer, WordChunkDisplayer> unscaleG = (disp, check) -> {
                if (disp != check)
                    return;
                int centerX = (int) (disp.rect.x + disp.rect.width / 2);
                int centerY = (int) (disp.rect.y + disp.rect.height / 2);
                g.translate(centerX, centerY);
                g.scale(1 / 1.1, 1 / 1.1);
                g.translate(-centerX, -centerY);
            };

            // Rendering each word individually
            for (WordChunkDisplayer displayer : shouldRender) {
                scaleG.accept(displayer, scaledDisplayer);
                displayer.renderOutline(g);
                unscaleG.accept(displayer, scaledDisplayer);
            }
            for (WordChunkDisplayer displayer : shouldRender) {
                scaleG.accept(displayer, scaledDisplayer);
                displayer.renderNormal(g);
                unscaleG.accept(displayer, scaledDisplayer);
            }
            switch (settings.getProgressMode()) {
                case NONE:
                    break;
                case SLIDER:
                    for (WordChunkDisplayer displayer : shouldRender) {
                        if (frameNum > displayer.chunk.getEndFrame(frameRate)) {
                            displayer.renderProgression(g, 1f);
                        } else if (frameNum >= displayer.chunk.getStartFrame(frameRate)
                                && frameNum <= displayer.chunk.getEndFrame(frameRate)) {
                            scaleG.accept(displayer, scaledDisplayer);
                            float progress = (float) (frameNum - displayer.chunk.getStartFrame(frameRate))
                                    / (float) (displayer.chunk.getEndFrame(frameRate)
                                            - displayer.chunk.getStartFrame(frameRate));
                            displayer.renderProgression(g, progress);
                            unscaleG.accept(displayer, scaledDisplayer);
                        }
                    }
                    break;
                case WORD:
                    for (WordChunkDisplayer displayer : shouldRender) {
                        if (frameNum >= displayer.chunk.getStartFrame(frameRate)
                                && frameNum <= displayer.chunk.getEndFrame(frameRate)) {
                            scaleG.accept(displayer, scaledDisplayer);
                            displayer.renderProgression(g, 0);
                            unscaleG.accept(displayer, scaledDisplayer);
                        }
                    }
                    break;
                default:
                    break;
            }

            g.setTransform(oldTransform);
        }
    }

    class WordChunkDisplayer {
        private Rectangle rect;
        private WordChunk chunk;

        public WordChunkDisplayer(Rectangle r, WordChunk chunk) {
            rect = r;
            this.chunk = chunk;
        }

        public void renderOutline(Graphics2D g) {
            int outlineSize = settings.getOutlineSize();

            int xPos = rect.x;
            int yPos = rect.y + ascentHeight;
            String text = chunk.getWord();

            FontRenderContext frc = g.getFontRenderContext();

            GlyphVector gv = g.getFont().createGlyphVector(frc, text);
            Shape textShape = gv.getOutline(xPos, yPos);

            g.setColor(settings.getTheme(chunk.getThemeIndex()).getOutlineColor());

            Stroke oldStroke = g.getStroke();
            g.setStroke(new BasicStroke(
                    outlineSize * 2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g.draw(textShape);

            g.setStroke(oldStroke);
        }

        public void renderNormal(Graphics2D g) {
            g.setColor(settings.getTheme(chunk.getThemeIndex()).getNormalColor());
            g.drawString(chunk.getWord(), rect.x, rect.y + ascentHeight);
        }

        public void renderProgression(Graphics2D g, float progress) {
            progress = Math.max(0f, Math.min(1f, progress));
            g.setColor(settings.getTheme(chunk.getThemeIndex()).getProgressColor());

            switch (settings.getProgressMode()) {
                case NONE:
                    break;
                case SLIDER:
                    int fillWidth = (int) (rect.width * progress);
                    // Restrict the drawing to only this clip, saving the old one to restore it
                    Shape oldClip = g.getClip();
                    g.clipRect(rect.x, rect.y, fillWidth, rect.height);
                    g.drawString(chunk.getWord(), rect.x, rect.y + ascentHeight);
                    g.setClip(oldClip);
                    break;
                case WORD:
                    g.drawString(chunk.getWord(), rect.x, rect.y + ascentHeight);
                    break;
                default:
                    break;
            }
        }

        public void renderBackground(Graphics2D g) {
            int padL = settings.getBackgroundPaddingLeft(), padR = settings.getBackgroundPaddingRight(),
                    padU = settings.getBackgroundPaddingUp(), padD = settings.getBackgroundPaddingDown();
            g.setColor(settings.getTheme(chunk.getThemeIndex()).getBackgroundColor());

            FontMetrics fm = g.getFontMetrics();

            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector gv = g.getFont().createGlyphVector(frc, chunk.getWord());
            Rectangle2D visualBounds = gv.getOutline(rect.x, rect.y + ascentHeight).getBounds2D();

            double standardizedY = (rect.y + ascentHeight) - fm.getAscent();
            double standardizedHeight = fm.getHeight();
            double standardizedX = visualBounds.getX();
            double standardizedWidth = visualBounds.getWidth();

            if (settings.roundBackgroundCorners()) {
                int rounding = (int) (settings.getWordFontSize() / 4);
                g.fill(new RoundRectangle2D.Double(
                        standardizedX - padL,
                        standardizedY - padU,
                        standardizedWidth + padL + padR,
                        standardizedHeight + padU + padD,
                        rounding, rounding));
            } else {
                g.fill(new Rectangle2D.Double(
                        standardizedX - padL,
                        standardizedY - padU,
                        standardizedWidth + padL + padR,
                        standardizedHeight + padU + padD));
            }
        }
    }
}
