package ui.chunkeditor;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ui.TranscriberWindow;
import util.transcription.WordChunk;
import util.transcription.WordChunkGroup;

public class ChunkEditorPanel extends JPanel {
    private TranscriberWindow parent;
    private JScrollPane scroller;
    private JPanel scrollPanel;

    public ChunkEditorPanel(TranscriberWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.green));

        scrollPanel = new JPanel();
        scrollPanel.setBackground(Color.black);
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        scrollPanel.setAlignmentY(TOP_ALIGNMENT);
        scroller = new JScrollPane(scrollPanel);
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller);

        populateItems(parent.getChunkGroup());
    }

    public void populateItems(WordChunkGroup chunkGroup) {
        scrollPanel.removeAll();
        for (WordChunk chunk : chunkGroup.getChunks()) {
            ChunkEditorItem item = new ChunkEditorItem(parent, this, chunk);
            scrollPanel.add(item);
        }
        scrollPanel.revalidate();
        scrollPanel.repaint();
    }
}
