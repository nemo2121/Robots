package gui.utils;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class WindowStateManager {
    private static final String CONFIG_FILE = "window_state.properties";

    // Сохраняем состояние окна в файл
    public static void saveWindowState(JInternalFrame frame, String windowId) {
        Properties properties = new Properties();
        properties.setProperty(windowId + ".x", String.valueOf(frame.getX()));
        properties.setProperty(windowId + ".y", String.valueOf(frame.getY()));
        properties.setProperty(windowId + ".width", String.valueOf(frame.getWidth()));
        properties.setProperty(windowId + ".height", String.valueOf(frame.getHeight()));
        properties.setProperty(windowId + ".isIcon", String.valueOf(frame.isIcon())); // Свернуто
        properties.setProperty(windowId + ".isMaximum", String.valueOf(frame.isMaximum())); // Развернуто

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Window State");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Восстанавливаем состояние окна из файла
    public static void restoreWindowState(JInternalFrame frame, String windowId) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);

            int x = Integer.parseInt(properties.getProperty(windowId + ".x", "100"));
            int y = Integer.parseInt(properties.getProperty(windowId + ".y", "100"));
            int width = Integer.parseInt(properties.getProperty(windowId + ".width", "400"));
            int height = Integer.parseInt(properties.getProperty(windowId + ".height", "300"));
            boolean isIcon = Boolean.parseBoolean(properties.getProperty(windowId + ".isIcon", "false"));
            boolean isMaximum = Boolean.parseBoolean(properties.getProperty(windowId + ".isMaximum", "false"));

            frame.setLocation(x, y);
            frame.setSize(width, height);

            if (isIcon) {
                try {
                    frame.setIcon(true); // Свернуть
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isMaximum) {
                try {
                    frame.setMaximum(true); // Развернуть
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}