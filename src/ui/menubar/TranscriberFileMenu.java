package ui.menubar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ui.TranscriberWindow;

public class TranscriberFileMenu extends JMenu {
    private JMenuItem importAudioItem, importFontItem, renderVideoItem;

    public TranscriberFileMenu(TranscriberWindow parent) {
        super("File");

        importAudioItem = new JMenuItem();
        importAudioItem.setAction(new AbstractAction("Import Audio File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.tryImportAudio();
            }
        });

        importFontItem = new JMenuItem();
        importFontItem.setAction(new AbstractAction("Import Font File (ttf)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.tryImportFont();
            }
        });

        renderVideoItem = new JMenuItem();
        renderVideoItem.setAction(new AbstractAction("Render Video") {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.tryRenderVideo();
            }
        });

        add(importAudioItem);
        add(importFontItem);
        add(renderVideoItem);
    }
}
