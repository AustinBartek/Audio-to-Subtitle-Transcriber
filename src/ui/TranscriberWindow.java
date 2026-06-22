package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ui.audioPreview.AudioPreviewPanel;
import ui.chunkeditor.ChunkEditorPanel;
import ui.menubar.TranscriberToolBar;
import ui.renderpreview.RenderPreviewPanel;
import ui.rendersettings.RenderSettingsPanel;
import util.io.FileInputter;
import util.io.ImageManager;
import util.io.VideoEncoder;
import util.rendering.RenderSettings;
import util.rendering.RenderSettings.BackgroundMode;
import util.rendering.RenderSettings.ProgressMode;
import util.rendering.RenderSettings.TransitionEasingMode;
import util.rendering.RenderSettings.TransitionMode;
import util.transcription.AudioConverter;
import util.transcription.AudioTranscriber;
import util.transcription.ChunkingRuleGenerator;
import util.transcription.ChunkingRules;
import util.transcription.WordChunk;
import util.transcription.WordChunkGroup;

/*
What I NEED

top menu bar, for importing audio files and font files

preview panel for the current render settings (includes frame slider to test the visual at different frames)

render settings panel that allows the user to customize the settings for the render (updates the preview)

word chunk editor panel that allows the creation, deletion, and altering of words + timeframes of all current wordchunks (has a button that once pressed, will update the status of the preview panel to match the new words)
also includes the chunk rule selector that determines how the words are chunked

a pop up menu that allows for adding a new chunk, checks to make sure that it does not occur concurrently with other chunks before adding!
*/

public class TranscriberWindow extends JFrame {
    private RenderSettings settings;
    private ChunkingRules chunkingRules;
    private WordChunkGroup chunkGroup;
    private File originalAudioFile;

    private TranscriberToolBar toolBar;
    private ChunkEditorPanel chunkEditorPanel;
    private RenderSettingsPanel renderSettingsPanel;
    private RenderPreviewPanel renderPreviewPanel;
    private AudioPreviewPanel audioPreviewPanel;

    public TranscriberWindow() {
        super();
        setTitle("Bartek's Video Transcriber");
        setSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageManager.MEDIA_IMAGE);

        // Initializing default information
        Font defaultFont = Font.decode(Font.MONOSPACED);
        try {
            InputStream is = TranscriberWindow.class.getResourceAsStream("/res/Roboto-Medium.ttf");
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            PopupManager.showMessage("Error loading default font: " + e.getMessage());
        }

        settings = new RenderSettings(
                new ArrayList<>(), 6, ChunkingRuleGenerator.generateTimeLimitRules(2.5f), ProgressMode.WORD, true,
                BackgroundMode.NONE, 30, 15, true, TransitionMode.POP, TransitionEasingMode.COS, 12, 0.5f, defaultFont,
                70f, false, false, 1920, 1080);

        chunkGroup = new WordChunkGroup();
        chunkGroup.addChunk(new WordChunk("TESTING", 0, 1000));

        // Setting up UI elements
        GridBagLayout gb = new GridBagLayout();

        JPanel mainPanel = new JPanel(gb);
        mainPanel.setBackground(Color.gray);
        add(mainPanel);

        toolBar = new TranscriberToolBar(this);
        setJMenuBar(toolBar);

        Insets insets = new Insets(2, 2, 2, 2);

        chunkEditorPanel = new ChunkEditorPanel(this);
        chunkEditorPanel.setPreferredSize(new Dimension(380, 450));
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.weightx = 1.5;
        gbc1.weighty = 2;
        gbc1.insets = insets;
        mainPanel.add(chunkEditorPanel, gbc1);

        renderSettingsPanel = new RenderSettingsPanel(this);
        renderSettingsPanel.setPreferredSize(new Dimension(380, 250));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 1.5;
        gbc2.weighty = 1;
        gbc2.insets = insets;
        mainPanel.add(renderSettingsPanel, gbc2);

        renderPreviewPanel = new RenderPreviewPanel(this);
        renderPreviewPanel.setPreferredSize(new Dimension(520, 450));
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 1;
        gbc3.gridy = 0;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.weightx = 2;
        gbc3.weighty = 2;
        gbc3.insets = insets;
        mainPanel.add(renderPreviewPanel, gbc3);

        audioPreviewPanel = new AudioPreviewPanel(this);
        audioPreviewPanel.setPreferredSize(new Dimension(520, 250));
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.weightx = 2;
        gbc4.weighty = 1;
        gbc4.insets = insets;
        mainPanel.add(audioPreviewPanel, gbc4);

        renderSettingsPanel.addFontOption(getRenderSettings().getWordFont());

        mainPanel.revalidate();
        revalidate();
        repaint();
        setVisible(true);
        updateStuff();
    }

    public RenderSettings getRenderSettings() {
        return settings;
    }

    public ChunkingRules getChunkingRules() {
        return chunkingRules;
    }

    public WordChunkGroup getChunkGroup() {
        return chunkGroup;
    }

    public void setChunkingRules(ChunkingRules rules) {
        chunkingRules = rules;
    }

    public void setChunkGroup(WordChunkGroup group) {
        chunkGroup = group;
    }

    public TranscriberToolBar getToolBar() {
        return toolBar;
    }

    public void tryImportAudio() {
        File audioFile = FileInputter.getUserAudioFileInput();
        if (audioFile == null)
            return;
        try {
            audioFile = AudioConverter.convertForSphinx(audioFile);
            WordChunkGroup transcription = AudioTranscriber.transcribeAudio(audioFile);
            if (chunkGroup != null) {
                boolean confirm = PopupManager
                        .getConfirmation("Are you sure you would like to overwrite the current transcript?");
                if (!confirm)
                    return;
            }
            chunkGroup = transcription;
            originalAudioFile = audioFile;

            audioPreviewPanel.updateDisplay(audioFile);
            chunkEditorPanel.populateItems(chunkGroup);
            updateStuff();
        } catch (Exception e) {
            e.printStackTrace();
            PopupManager.showMessage("Error transcribing the audio file: " + e.getMessage());
            return;
        }
    }

    public void tryImportFont() {
        try {
            Font font = FileInputter.getUserFontInput();
            if (font == null)
                return;
            renderSettingsPanel.addFontOption(font);
            updateStuff();
        } catch (Exception e) {
            e.printStackTrace();
            PopupManager.showMessage("Error loading the font file: " + e.getMessage());
            return;
        }
    }

    public void tryRenderVideo() {
        try {
            VideoEncoder.encodeTranscriptionToVideo(originalAudioFile, chunkGroup, settings);
            PopupManager.showMessage("Successfully rendered the video!");
        } catch (Exception e) {
            e.printStackTrace();
            PopupManager.showMessage("Error rendering video: " + e.getMessage());
        }
    }

    public void updateStuff() {
        renderPreviewPanel.updateSliderMaximum();
        renderPreviewPanel.updateRender();
    }
}
