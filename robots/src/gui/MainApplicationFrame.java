package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        loadWindowStates();

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit(null);
            }
        });
    }

    private void loadWindowStates() {
        try {
            List<WindowState> states = WindowStateManager.loadStates();

            if (states.isEmpty()) {
                // Создание окон с настройками по умолчанию
                logWindow = createLogWindow();
                gameWindow = new GameWindow();
                gameWindow.setSize(400, 400);
            } else {
                // Восстановление окон из сохраненных состояний
                for (WindowState state : states) {
                    switch (state.getWindowType()) {
                        case "LogWindow":
                            logWindow = createLogWindow();
                            logWindow.setLocation(state.getX(), state.getY());
                            logWindow.setSize(state.getWidth(), state.getHeight());
                            break;
                        case "GameWindow":
                            gameWindow = new GameWindow();
                            gameWindow.setLocation(state.getX(), state.getY());
                            gameWindow.setSize(state.getWidth(), state.getHeight());
                            break;
                    }
                }
            }

            addWindow(logWindow);
            addWindow(gameWindow);

        } catch (IOException e) {
            Logger.error("Ошибка загрузки состояний окон: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Не удалось загрузить состояния окон",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveWindowStates() {
        try {
            List<WindowState> states = List.of(
                    new WindowState("LogWindow",
                            logWindow.getX(), logWindow.getY(),
                            logWindow.getWidth(), logWindow.getHeight()),
                    new WindowState("GameWindow",
                            gameWindow.getX(), gameWindow.getY(),
                            gameWindow.getWidth(), gameWindow.getHeight())
            );
            WindowStateManager.saveStates(states);
        } catch (IOException e) {
            Logger.error("Ошибка сохранения состояний окон: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Не удалось сохранить состояния окон",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
        setupInternalFrame(frame);
    }

    private void setupInternalFrame(JInternalFrame frame) {
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                confirmAndExit(frame);
            }
        });
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_S);
        exitMenuItem.addActionListener((event) -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(exitMenuItem);

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_M);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private void confirmAndExit(JInternalFrame frame) {
        int option = JOptionPane.showOptionDialog(
                this,
                "Вы уверены, что хотите закрыть это окно?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Да", "Нет"},
                "Нет"
        );

        if (option == JOptionPane.YES_OPTION) {
            if (frame == null) {
                saveWindowStates(); // Сохраняем состояния перед выходом
                dispose();
                System.exit(0);
            } else {
                frame.dispose();
            }
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error("Ошибка установки темы: " + e.getMessage());
        }
    }
}
