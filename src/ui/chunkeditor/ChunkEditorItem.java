package ui.chunkeditor;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ui.PopupManager;
import ui.TranscriberWindow;
import ui.UIHelper;
import util.io.ImageManager;
import util.transcription.WordChunk;

public class ChunkEditorItem extends JPanel {
    private WordChunk chunk;
    private JTextField chunkWordField;
    private JSpinner chunkStartField, chunkEndField, chunkThemeIndexField;
    private JCheckBox forceChunkBox;
    private JButton deleteButton, addUpButton, addDownButton;

    public ChunkEditorItem(TranscriberWindow parent, ChunkEditorPanel editorPanel, WordChunk chunk) {
        this.chunk = chunk;

        chunkWordField = new JTextField();
        chunkWordField.setPreferredSize(new Dimension(90, 50));
        chunkWordField.setMaximumSize(new Dimension(90, 50));
        chunkWordField.setText(chunk.getWord());
        chunkWordField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String word = chunkWordField.getText();
                if (word.length() == 0) {
                    word = chunk.getWord();
                } else {
                    chunk.setWord(word);
                }
                parent.updateStuff();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        chunkWordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                chunkWordField.setText(chunk.getWord());
            }
        });

        chunkStartField = new JSpinner(new SpinnerNumberModel((int) chunk.getStart(), 0, Integer.MAX_VALUE, 1));
        chunkStartField.setPreferredSize(new Dimension(70, 50));
        chunkStartField.setMaximumSize(new Dimension(70, 50));
        chunkStartField.setToolTipText("Start millisecond of this entry");
        chunkStartField.addChangeListener((e) -> {
            int value = (Integer) chunkStartField.getValue();
            int minValue = 0;
            int maxValue = (int) chunk.getEnd();
            int index = parent.getChunkGroup().getChunkIndex(chunk);
            if (index != 0) {
                minValue = (int) parent.getChunkGroup().getChunks().get(index - 1).getEnd() + 1;
            }

            if (value >= minValue && value <= maxValue) {
                chunk.setTimeFrame(value, chunk.getEnd());
                parent.updateStuff();
            }
        });
        JFormattedTextField startTextField = ((JSpinner.DefaultEditor) chunkStartField.getEditor()).getTextField();
        startTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    chunkStartField.commitEdit();
                } catch (Exception ex) {
                }
                chunkStartField.setValue((int) chunk.getStart());
                editorPanel.populateItems(parent.getChunkGroup());
            }
        });

        chunkEndField = new JSpinner(new SpinnerNumberModel((int) chunk.getEnd(), 0, Integer.MAX_VALUE, 1));
        chunkEndField.setPreferredSize(new Dimension(70, 50));
        chunkEndField.setMaximumSize(new Dimension(70, 50));
        chunkEndField.setToolTipText("End millisecond of this entry");
        chunkEndField.addChangeListener((e) -> {
            int value = (Integer) chunkEndField.getValue();
            int minValue = (int) chunk.getStart();
            int maxValue = (int) 1e7;
            int index = parent.getChunkGroup().getChunkIndex(chunk);
            if (index != parent.getChunkGroup().getNumChunks() - 1) {
                maxValue = (int) parent.getChunkGroup().getChunks().get(index + 1).getStart() - 1;
            }

            if (value >= minValue && value <= maxValue) {
                chunk.setTimeFrame(chunk.getStart(), value);
                parent.updateStuff();
            }
        });
        JFormattedTextField endTextField = ((JSpinner.DefaultEditor) chunkEndField.getEditor()).getTextField();
        endTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    chunkEndField.commitEdit();
                } catch (Exception ex) {
                }
                chunkEndField.setValue((int) chunk.getEnd());
                editorPanel.populateItems(parent.getChunkGroup());
            }
        });

        chunkThemeIndexField = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        chunkThemeIndexField.setPreferredSize(new Dimension(50, 50));
        chunkThemeIndexField.setMaximumSize(new Dimension(50, 50));
        chunkThemeIndexField.setToolTipText("Theme index of this entry");
        chunkThemeIndexField.addChangeListener((e) -> {
            int value = (Integer) chunkThemeIndexField.getValue();
            int maxValue = parent.getRenderSettings().getThemeCount() - 1;
            if (value > maxValue) {
                chunkThemeIndexField.setValue(maxValue);
                value = maxValue;
            }
            chunk.setThemeIndex(value);
            parent.updateStuff();
        });

        forceChunkBox = new JCheckBox("Force");
        forceChunkBox.setToolTipText("Force this word to be part of a new grouping");
        forceChunkBox.addActionListener((e) -> {
            chunk.setForceChunk(forceChunkBox.isSelected());
            parent.updateStuff();
        });
        UIHelper.setMaxAndPreferredSize(forceChunkBox, new Dimension(60, 50));

        deleteButton = UIHelper.createIconButton(ImageManager.TRASH_IMAGE, "Delete this entry", 40);
        deleteButton.setPreferredSize(new Dimension(50, 50));
        deleteButton.setMaximumSize(new Dimension(50, 50));
        deleteButton.addActionListener((e) -> {
            if (parent.getChunkGroup().getNumChunks() == 1) {
                PopupManager.showMessage("Must have at least [1] word in your video!");
                return;
            }
            boolean confirm = PopupManager
                    .getConfirmation("Are you sure you would like to delete: [" + chunk.getWord() + "]?");
            if (!confirm)
                return;
            parent.getChunkGroup().removeChunk(chunk);
            parent.updateStuff();
            editorPanel.populateItems(parent.getChunkGroup());
        });

        addUpButton = UIHelper.createIconButton(ImageManager.UP_IMAGE, "Add entry above", 40);
        addUpButton.setPreferredSize(new Dimension(50, 50));
        addUpButton.setMaximumSize(new Dimension(50, 50));
        addUpButton.addActionListener((e) -> {
            int index = parent.getChunkGroup().getChunkIndex(chunk);
            long start = 0;
            long end = chunk.getStart() - 1;
            if (index != 0) {
                start = parent.getChunkGroup().getChunks().get(index - 1).getEnd() + 1;
            }
            if (start > end)
                return;

            String newWord = PopupManager.getUserInput("Input text of new word:");
            if (newWord == null || newWord.length() == 0 || newWord.isBlank()) {
                return;
            }

            WordChunk newChunk = new WordChunk(newWord, start, end);
            parent.getChunkGroup().addChunkAt(newChunk, index);
            editorPanel.populateItems(parent.getChunkGroup());
        });

        addDownButton = UIHelper.createIconButton(ImageManager.DOWN_IMAGE, "Add entry below", 40);
        addDownButton.setPreferredSize(new Dimension(50, 50));
        addDownButton.setMaximumSize(new Dimension(50, 50));
        addDownButton.addActionListener((e) -> {
            int index = parent.getChunkGroup().getChunkIndex(chunk);
            long start = chunk.getEnd() + 1;
            long end = (long) 1e6;
            if (index != parent.getChunkGroup().getNumChunks() - 1) {
                end = parent.getChunkGroup().getChunks().get(index + 1).getStart() - 1;
            }
            if (start > end)
                return;

            String newWord = PopupManager.getUserInput("Input text of new word:");
            if (newWord == null || newWord.length() == 0 || newWord.isBlank()) {
                return;
            }

            WordChunk newChunk = new WordChunk(newWord, start, end);
            parent.getChunkGroup().addChunkAt(newChunk, index + 1);
            editorPanel.populateItems(parent.getChunkGroup());
        });

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(chunkWordField);
        add(chunkStartField);
        add(chunkEndField);
        add(chunkThemeIndexField);
        add(forceChunkBox);
        add(addUpButton);
        add(addDownButton);
        add(deleteButton);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setAlignmentX(LEFT_ALIGNMENT);
    }

    public WordChunk getChunk() {
        return chunk;
    }
}
