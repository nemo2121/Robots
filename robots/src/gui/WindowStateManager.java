package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WindowStateManager {
    private static final String CONFIG_FILE = System.getProperty("user.home") + "/app_config";

    public void saveWindowStates(JDesktopPane desktopPane) {
        Map<String, WindowState> windowStates = new HashMap<>();

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String windowName = frame.getName();
            if (windowName == null) {
                System.err.println("Окно без имени: " + frame.getTitle());
                continue;
            }
            Rectangle bounds = frame.getBounds();
            boolean isIcon = frame.isIcon();

            windowStates.put(windowName, new WindowState(windowName, bounds, isIcon));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(windowStates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWindowStates(JDesktopPane desktopPane) {
        if (!Files.exists(Paths.get(CONFIG_FILE))) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIG_FILE))) {
            @SuppressWarnings("unchecked")
            Map<String, WindowState> windowStates = (Map<String, WindowState>) ois.readObject();

            for (Map.Entry<String, WindowState> entry : windowStates.entrySet()) {
                String windowName = entry.getKey();
                WindowState state = entry.getValue();

                for (JInternalFrame frame : desktopPane.getAllFrames()) {
                    if (windowName.equals(frame.getName())) {
                        frame.setBounds(state.getBounds());
                        try {
                            if (state.isCollapsed()) {
                                frame.setIcon(true);
                            }
                        } catch (java.beans.PropertyVetoException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}