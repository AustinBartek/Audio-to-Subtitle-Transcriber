package util.transcription;

import java.awt.Graphics2D;

import util.rendering.RenderSettings;

/**
 * Defines how a WordChunkGroup is formed by checking whether another chunk can
 * be added (essentially limiting the size of the group depending on whatever
 * the interface defines as the limiting factor).
 */
@FunctionalInterface
public interface ChunkingRules {
    public boolean canAddChunk(WordChunkGroup group, WordChunk newChunk, Graphics2D g, RenderSettings settings,
            int frameWidth, int frameHeight);
}