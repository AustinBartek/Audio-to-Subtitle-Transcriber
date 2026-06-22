package ui.menubar;

import javax.swing.JMenuBar;

import ui.TranscriberWindow;

public class TranscriberToolBar extends JMenuBar {
    private TranscriberFileMenu fileMenu;

    public TranscriberToolBar(TranscriberWindow parent) {
        super();

        fileMenu = new TranscriberFileMenu(parent);
        add(fileMenu);
    }

    public TranscriberFileMenu getFileMenu() {
        return fileMenu;
    }
}
