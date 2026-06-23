package ui.rendersettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ui.TranscriberWindow;
import ui.UIHelper;
import util.rendering.WordColorTheme;
import util.rendering.RenderSettings.BackgroundMode;
import util.rendering.RenderSettings.ProgressMode;
import util.rendering.RenderSettings.TransitionEasingMode;
import util.rendering.RenderSettings.TransitionMode;
import util.transcription.ChunkingRules;
import util.transcription.ChunkingRuleGenerator.ChunkGeneratorType;

public class RenderSettingsPanel extends JPanel {
    private TranscriberWindow parent;
    private JScrollPane settingsScroller;
    private JPanel settingsPanel;
    private JLabel fontSettingsLabel, chunkingRulesLabel, backgroundLabel, progressLabel, transitionLabel,
            miscSettingsLabel;
    private JComboBox<ChunkGeneratorType> chunkingRulesSelector;
    private JComboBox<BackgroundMode> backgroundModeSelector;
    private JComboBox<ProgressMode> progressModeSelector;
    private JComboBox<TransitionMode> transitionModeSelector;
    private JComboBox<TransitionEasingMode> transitionEasingModeSelector;
    private JComboBox<String> fontSelector;
    private JSpinner backgroundPadL, backgroundPadR, backgroundPadU, backgroundPadD, chunkingValue,
            transitionLengthValue, outlineSize, fontSize, renderWidth, renderHeight, maxWidthRatio;
    private JCheckBox wordsPersist, hideUnspokenWords, roundBackgroundCorners, scaleActiveWord;
    private HashMap<String, Font> fontMap;

    public RenderSettingsPanel(TranscriberWindow parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.red));
        fontMap = new HashMap<>();

        settingsPanel = new JPanel();
        settingsPanel.setBackground(Color.black);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsScroller = new JScrollPane(settingsPanel);
        settingsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        settingsScroller.getVerticalScrollBar().setUnitIncrement(8);

        // Misc settings
        miscSettingsLabel = new JLabel("Miscellaneous Settings");
        outlineSize = new JSpinner(new SpinnerNumberModel(6, 0, 200, 1));
        renderWidth = new JSpinner(new SpinnerNumberModel(1920, 1, 5000, 1));
        renderHeight = new JSpinner(new SpinnerNumberModel(1080, 1, 5000, 1));
        maxWidthRatio = new JSpinner(new SpinnerNumberModel(0.8, 0.01, 2, 0.01));
        wordsPersist = new JCheckBox("Persist");
        hideUnspokenWords = new JCheckBox("Hide");
        outlineSize.setToolTipText("Outline Size");
        renderWidth.setToolTipText("Width of the Render");
        renderHeight.setToolTipText("Height of the Render");
        maxWidthRatio.setToolTipText("Maximum Text Width/Screen Width Ratio");
        wordsPersist.setToolTipText("Display Words through Silence");
        hideUnspokenWords.setToolTipText("Hide Words that haven't been Spoken");
        outlineSize.addChangeListener((e) -> updateMiscSettings());
        renderWidth.addChangeListener((e) -> updateMiscSettings());
        renderHeight.addChangeListener((e) -> updateMiscSettings());
        maxWidthRatio.addChangeListener((e) -> updateMiscSettings());
        wordsPersist.addActionListener((e) -> updateMiscSettings());
        hideUnspokenWords.addActionListener((e) -> updateMiscSettings());
        UIHelper.setMaxAndPreferredSize(outlineSize, new Dimension(50, 50));
        UIHelper.setMaxAndPreferredSize(renderWidth, new Dimension(50, 50));
        UIHelper.setMaxAndPreferredSize(renderHeight, new Dimension(50, 50));
        UIHelper.setMaxAndPreferredSize(maxWidthRatio, new Dimension(50, 50));
        UIHelper.setMaxAndPreferredSize(wordsPersist, new Dimension(70, 50));
        UIHelper.setMaxAndPreferredSize(hideUnspokenWords, new Dimension(70, 50));
        JPanel miscSettingsPanel = new JPanel();
        miscSettingsPanel.setLayout(new BorderLayout());
        JPanel miscSettingsList = new JPanel();
        miscSettingsList.setBackground(Color.gray);
        miscSettingsList.setLayout(new BoxLayout(miscSettingsList, BoxLayout.X_AXIS));
        miscSettingsList.add(outlineSize);
        miscSettingsList.add(renderWidth);
        miscSettingsList.add(renderHeight);
        miscSettingsList.add(maxWidthRatio);
        miscSettingsList.add(wordsPersist);
        miscSettingsList.add(hideUnspokenWords);
        miscSettingsPanel.add(miscSettingsLabel, BorderLayout.NORTH);
        miscSettingsPanel.add(miscSettingsList);
        miscSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        settingsPanel.add(miscSettingsPanel);

        // Font settings stuff
        fontSettingsLabel = new JLabel("Font Settings");
        fontSize = new JSpinner(new SpinnerNumberModel(70, 1, 500, 1));
        fontSize.setToolTipText("Font Size");
        fontSize.addChangeListener((e) -> updateFontSettings());
        UIHelper.setMaxAndPreferredSize(fontSize, new Dimension(50, 50));
        fontSelector = new JComboBox<>();
        fontSelector.setToolTipText("Font Family");
        fontSelector.addActionListener((e) -> updateFontSettings());
        UIHelper.setMaxAndPreferredSize(fontSelector, new Dimension(120, 50));
        JPanel fontSettingsPanel = new JPanel();
        fontSettingsPanel.setLayout(new BorderLayout());
        JPanel fontSettingsList = new JPanel();
        fontSettingsList.setBackground(Color.gray);
        fontSettingsList.setLayout(new BoxLayout(fontSettingsList, BoxLayout.X_AXIS));
        fontSettingsPanel.add(fontSettingsLabel, BorderLayout.NORTH);
        fontSettingsPanel.add(fontSettingsList);
        fontSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        fontSettingsList.add(fontSelector);
        fontSettingsList.add(fontSize);
        settingsPanel.add(fontSettingsPanel);

        // Chunking rules stuff
        chunkingRulesLabel = new JLabel("Chunking Settings");
        chunkingRulesSelector = new JComboBox<>(ChunkGeneratorType.values());
        chunkingRulesSelector.setToolTipText("Chunking Modes");
        chunkingRulesSelector.setSelectedItem(ChunkGeneratorType.TimeLimit);
        chunkingRulesSelector.addActionListener((e) -> updateChunkingSettings());
        UIHelper.setMaxAndPreferredSize(chunkingRulesSelector, new Dimension(120, 50));
        chunkingValue = new JSpinner(new SpinnerNumberModel(2.5f, 0.1f, 1000f, 0.1f));
        chunkingValue.setToolTipText("Chunking Mode Basis Value");
        chunkingValue.addChangeListener((e) -> updateChunkingSettings());
        UIHelper.setMaxAndPreferredSize(chunkingValue, new Dimension(50, 50));
        JPanel chunkingSettingsPanel = new JPanel();
        chunkingSettingsPanel.setLayout(new BorderLayout());
        JPanel chunkingSettingsList = new JPanel();
        chunkingSettingsList.setBackground(Color.gray);
        chunkingSettingsList.setLayout(new BoxLayout(chunkingSettingsList, BoxLayout.X_AXIS));
        chunkingSettingsPanel.add(chunkingRulesLabel, BorderLayout.NORTH);
        chunkingSettingsPanel.add(chunkingSettingsList);
        chunkingSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        chunkingSettingsList.add(chunkingRulesSelector);
        chunkingSettingsList.add(chunkingValue);
        settingsPanel.add(chunkingSettingsPanel);

        // Background stuff
        backgroundLabel = new JLabel("Background Settings");
        backgroundModeSelector = new JComboBox<>(BackgroundMode.values());
        backgroundModeSelector.setToolTipText("Background Mode");
        backgroundModeSelector.setSelectedItem(BackgroundMode.NONE);
        backgroundModeSelector.addActionListener((e) -> updateBackgroundSettings());
        UIHelper.setMaxAndPreferredSize(backgroundModeSelector, new Dimension(120, 50));
        backgroundPadL = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        backgroundPadL.setToolTipText("Background Padding Left");
        backgroundPadL.addChangeListener((e) -> updateBackgroundSettings());
        UIHelper.setMaxAndPreferredSize(backgroundPadL, new Dimension(50, 50));
        backgroundPadR = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        backgroundPadR.setToolTipText("Background Padding Right");
        backgroundPadR.addChangeListener((e) -> updateBackgroundSettings());
        UIHelper.setMaxAndPreferredSize(backgroundPadR, new Dimension(50, 50));
        backgroundPadU = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        backgroundPadU.setToolTipText("Background Padding Up");
        backgroundPadU.addChangeListener((e) -> updateBackgroundSettings());
        UIHelper.setMaxAndPreferredSize(backgroundPadU, new Dimension(50, 50));
        backgroundPadD = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        backgroundPadD.setToolTipText("Background Padding Down");
        backgroundPadD.addChangeListener((e) -> updateBackgroundSettings());
        UIHelper.setMaxAndPreferredSize(backgroundPadD, new Dimension(50, 50));
        roundBackgroundCorners = new JCheckBox("Round");
        roundBackgroundCorners.setSelected(true);
        roundBackgroundCorners.setToolTipText("Round Background Corners");
        roundBackgroundCorners.addActionListener((e) -> {
            updateBackgroundSettings();
        });
        UIHelper.setMaxAndPreferredSize(roundBackgroundCorners, new Dimension(70, 50));
        JPanel backgroundSettingsPanel = new JPanel();
        backgroundSettingsPanel.setLayout(new BorderLayout());
        JPanel backgroundSettingsList = new JPanel();
        backgroundSettingsList.setBackground(Color.gray);
        backgroundSettingsList.setLayout(new BoxLayout(backgroundSettingsList, BoxLayout.X_AXIS));
        backgroundSettingsPanel.add(backgroundLabel, BorderLayout.NORTH);
        backgroundSettingsPanel.add(backgroundSettingsList);
        backgroundSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        backgroundSettingsList.add(backgroundModeSelector);
        backgroundSettingsList.add(backgroundPadL);
        backgroundSettingsList.add(backgroundPadR);
        backgroundSettingsList.add(backgroundPadU);
        backgroundSettingsList.add(backgroundPadD);
        backgroundSettingsList.add(roundBackgroundCorners);
        settingsPanel.add(backgroundSettingsPanel);

        // Progress stuff
        progressLabel = new JLabel("Progress Settings");
        progressModeSelector = new JComboBox<>(ProgressMode.values());
        progressModeSelector.setToolTipText("Progress Mode");
        progressModeSelector.setSelectedItem(ProgressMode.WORD);
        progressModeSelector.addActionListener((e) -> updateProgressSettings());
        UIHelper.setMaxAndPreferredSize(progressModeSelector, new Dimension(120, 50));
        scaleActiveWord = new JCheckBox("Scale");
        scaleActiveWord.setSelected(true);
        scaleActiveWord.setToolTipText("Scale Active Word");
        scaleActiveWord.addActionListener((e) -> updateProgressSettings());
        UIHelper.setMaxAndPreferredSize(scaleActiveWord, new Dimension(70, 50));
        JPanel progressSettingsPanel = new JPanel();
        progressSettingsPanel.setLayout(new BorderLayout());
        JPanel progressSettingsList = new JPanel();
        progressSettingsList.setBackground(Color.gray);
        progressSettingsList.setLayout(new BoxLayout(progressSettingsList, BoxLayout.X_AXIS));
        progressSettingsPanel.add(progressLabel, BorderLayout.NORTH);
        progressSettingsPanel.add(progressSettingsList);
        progressSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        progressSettingsList.add(progressModeSelector);
        progressSettingsList.add(scaleActiveWord);
        settingsPanel.add(progressSettingsPanel);

        // Transition stuff
        transitionLabel = new JLabel("Transition Settings");
        transitionModeSelector = new JComboBox<>(TransitionMode.values());
        transitionModeSelector.setToolTipText("Transition Mode");
        transitionModeSelector.setSelectedItem(TransitionMode.POP);
        transitionModeSelector.addActionListener((e) -> updateTransitionSettings());
        UIHelper.setMaxAndPreferredSize(transitionModeSelector, new Dimension(120, 50));
        transitionEasingModeSelector = new JComboBox<>(TransitionEasingMode.values());
        transitionEasingModeSelector.setToolTipText("Transition Easing Mode");
        transitionEasingModeSelector.setSelectedItem(TransitionEasingMode.COS);
        transitionEasingModeSelector.addActionListener((e) -> updateTransitionSettings());
        UIHelper.setMaxAndPreferredSize(transitionEasingModeSelector, new Dimension(120, 50));
        transitionLengthValue = new JSpinner(new SpinnerNumberModel(12, 1, 200, 1));
        transitionLengthValue.setToolTipText("Transition Length (Frames)");
        transitionLengthValue.addChangeListener((e) -> updateTransitionSettings());
        UIHelper.setMaxAndPreferredSize(transitionLengthValue, new Dimension(50, 50));
        JPanel transitionSettingsPanel = new JPanel();
        transitionSettingsPanel.setLayout(new BorderLayout());
        JPanel transitionSettingsList = new JPanel();
        transitionSettingsList.setBackground(Color.gray);
        transitionSettingsList.setLayout(new BoxLayout(transitionSettingsList, BoxLayout.X_AXIS));
        transitionSettingsPanel.add(transitionLabel, BorderLayout.NORTH);
        transitionSettingsPanel.add(transitionSettingsList);
        transitionSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        transitionSettingsList.add(transitionModeSelector);
        transitionSettingsList.add(transitionEasingModeSelector);
        transitionSettingsList.add(transitionLengthValue);
        settingsPanel.add(transitionSettingsPanel);

        // Color themes
        JPanel colorThemePanel = new JPanel();
        colorThemePanel.setLayout(new BorderLayout());
        JPanel colorThemeList = new JPanel();
        colorThemeList.setLayout(new BoxLayout(colorThemeList, BoxLayout.Y_AXIS));
        JButton addColorThemeButton = new JButton("Create New Theme");
        addColorThemeButton.addActionListener((e) -> {
            JPanel newThemePanel = createColorThemePanel();
            colorThemeList.add(newThemePanel);
            settingsPanel.revalidate();
            settingsPanel.repaint();
        });
        colorThemePanel.add(addColorThemeButton, BorderLayout.NORTH);
        colorThemePanel.add(colorThemeList, BorderLayout.CENTER);
        settingsPanel.add(colorThemePanel);
        colorThemeList.add(createColorThemePanel());

        add(settingsScroller);
    }

    public void addFontOption(Font newFont) {
        fontSelector.addItem(newFont.getFamily());
        fontSelector.setSelectedIndex(fontSelector.getItemCount() - 1);
        fontMap.put(newFont.getFamily(), newFont);
        updateFontSettings();
        revalidate();
        repaint();
    }

    private JPanel createColorThemePanel() {
        WordColorTheme newTheme = new WordColorTheme(Color.black, Color.white, Color.green, Color.white);
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.X_AXIS));
        themePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Create compact color buttons instead of embedding the whole JColorChooser
        JButton normalBtn = createColorPickerButton("Text Base", newTheme.getNormalColor(), (newColor) -> {
            newTheme.setNormalColor(newColor);
            parent.updateStuff();
        });

        JButton outlineBtn = createColorPickerButton("Outline", newTheme.getOutlineColor(), (newColor) -> {
            newTheme.setOutlineColor(newColor);
            parent.updateStuff();
        });

        JButton progressBtn = createColorPickerButton("Progress", newTheme.getProgressColor(), (newColor) -> {
            newTheme.setProgressColor(newColor);
            parent.updateStuff();
        });

        JButton backgroundBtn = createColorPickerButton("Background", newTheme.getBackgroundColor(), (newColor) -> {
            newTheme.setBackgroundColor(newColor);
            parent.updateStuff();
        });

        themePanel.add(normalBtn);
        themePanel.add(outlineBtn);
        themePanel.add(progressBtn);
        themePanel.add(backgroundBtn);

        parent.getRenderSettings().getThemes().add(newTheme);

        return themePanel;
    }

    private JButton createColorPickerButton(String label, Color initialColor, Consumer<Color> onColorSelected) {
        JButton button = new JButton(label);
        button.setBackground(initialColor);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 40));

        button.addActionListener((e) -> {
            Color selectedColor = JColorChooser.showDialog(null, "Select " + label + " Color",
                    button.getBackground());
            if (selectedColor != null) {
                button.setBackground(selectedColor);
                onColorSelected.accept(selectedColor);
            }
        });

        return button;
    }

    private void updateBackgroundSettings() {
        BackgroundMode mode = (BackgroundMode) backgroundModeSelector.getSelectedItem();
        int padL = (Integer) backgroundPadL.getValue();
        int padR = (Integer) backgroundPadR.getValue();
        int padU = (Integer) backgroundPadU.getValue();
        int padD = (Integer) backgroundPadD.getValue();
        boolean roundCorners = roundBackgroundCorners.isSelected();
        parent.getRenderSettings().setWordBackgroundMode(mode);
        parent.getRenderSettings().setBackgroundPaddingLeft(padL);
        parent.getRenderSettings().setBackgroundPaddingRight(padR);
        parent.getRenderSettings().setBackgroundPaddingDown(padD);
        parent.getRenderSettings().setBackgroundPaddingUp(padU);
        parent.getRenderSettings().setRoundBackgroundCorners(roundCorners);
        parent.updateStuff();
    }

    private void updateChunkingSettings() {
        ChunkGeneratorType gen = (ChunkGeneratorType) chunkingRulesSelector.getSelectedItem();
        float value = ((Double) chunkingValue.getValue()).floatValue();
        ChunkingRules rules = gen.getGetter().apply(value);
        parent.getRenderSettings().setChunkingRules(rules);
        parent.updateStuff();
    }

    private void updateProgressSettings() {
        ProgressMode pm = (ProgressMode) progressModeSelector.getSelectedItem();
        boolean saw = scaleActiveWord.isSelected();
        parent.getRenderSettings().setProgressMode(pm);
        parent.getRenderSettings().setScaleActiveWord(saw);
        parent.updateStuff();
    }

    private void updateTransitionSettings() {
        TransitionMode tm = (TransitionMode) transitionModeSelector.getSelectedItem();
        TransitionEasingMode tem = (TransitionEasingMode) transitionEasingModeSelector.getSelectedItem();
        int tal = (Integer) transitionLengthValue.getValue();
        parent.getRenderSettings().setTransitionMode(tm);
        parent.getRenderSettings().setTransitionEasingMode(tem);
        parent.getRenderSettings().setTransitionAnimationLength(tal);
        parent.updateStuff();
    }

    private void updateFontSettings() {
        String fString = (String) fontSelector.getSelectedItem();
        Font f = fontMap.get(fString);
        int fs = (Integer) fontSize.getValue();
        parent.getRenderSettings().setWordFont(f);
        parent.getRenderSettings().setWordFontSize(fs);
        parent.updateStuff();
    }

    private void updateMiscSettings() {
        int os = (Integer) outlineSize.getValue();
        int rw = (Integer) renderWidth.getValue();
        int rh = (Integer) renderHeight.getValue();
        float mwr = ((Double) maxWidthRatio.getValue()).floatValue();
        boolean wp = wordsPersist.isSelected();
        boolean hus = hideUnspokenWords.isSelected();
        parent.getRenderSettings().setOutlineSize(os);
        parent.getRenderSettings().setRenderWidth(rw);
        parent.getRenderSettings().setRenderHeight(rh);
        parent.getRenderSettings().setMaxWidthRatio(mwr);
        parent.getRenderSettings().setWordsPersistIfSilentEnabled(wp);
        parent.getRenderSettings().setHideUnspokenWords(hus);
        parent.updateStuff();
    }
}
