package gui.handler;

import javax.swing.*;
import java.awt.Frame;

public class ExitHandler {
    private final JFrame mainFrame;

    public ExitHandler(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // Метод для подтверждения выхода или закрытия окна
    public void confirmAndExit(JInternalFrame frame) {
        String message;
        String title;

        if (frame == null) {
            // Сообщение для главного окна
            message = "Вы хотите выйти?";
            title = "Подтверждение выхода";
        } else {
            // Сообщение для внутренних окон
            message = "Вы уверены, что хотите закрыть это окно?";
            title = "Подтверждение закрытия окна";
        }

        int option = JOptionPane.showOptionDialog(
                mainFrame,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Да", "Нет"},
                "Нет"
        );

        if (option == JOptionPane.YES_OPTION) {
            if (frame == null) {
                mainFrame.dispose();
                System.exit(0);
            } else {
                frame.dispose();
            }
        }
    }
}