package util.io;

import java.awt.Font;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class FileInputter {
    public static File getUserFileInput(FileNameExtensionFilter filter) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        int option = chooser.showDialog(null, "Select");
        if ((option == JFileChooser.CANCEL_OPTION) || (option == JFileChooser.ERROR_OPTION)) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    public static Font getUserFontInput() throws Exception {
        File file = getUserFileInput(new FileNameExtensionFilter("Font Files", "ttf"));
        if (file == null)
            return null;
        return Font.createFont(Font.TRUETYPE_FONT, file);
    }

    public static File getUserAudioFileInput() {
        return getUserFileInput(new FileNameExtensionFilter("Sound Files", "wav", "mp3", "m4a", "ogg"));
    }
}
