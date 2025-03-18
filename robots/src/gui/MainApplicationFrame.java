package gui;

import gui.handler.ExitHandler;
import gui.utils.WindowStateManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.io.File;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ExitHandler exitHandler;

    public MainApplicationFrame() {
        // Устанавливаем размеры окна
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        // Создаем и добавляем окно лога
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow, "logWindow");

        // Создаем и добавляем игровое окно
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow, "gameWindow");

        // Инициализируем обработчик выхода
        exitHandler = new ExitHandler(this);

        // Устанавливаем меню
        setJMenuBar(generateMenuBar());

        // Обработка закрытия главного окна
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveAllWindowsState(); // Сохраняем состояние окон перед выходом
                exitHandler.confirmAndExit(null); // Передаем null для главного окна
            }
        });
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

    protected void addWindow(JInternalFrame frame, String windowId) {
        desktopPane.add(frame);
        frame.setVisible(true);
        setupInternalFrame(frame, windowId); // Настраиваем поведение при закрытии
        WindowStateManager.restoreWindowState(frame, windowId); // Восстанавливаем состояние окна
    }

    private void setupInternalFrame(JInternalFrame frame, String windowId) {
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                WindowStateManager.saveWindowState(frame, windowId); // Сохраняем состояние окна перед закрытием
                exitHandler.confirmAndExit(frame); // Передаем текущее внутреннее окно
            }
        });
    }

    // Сохраняем состояние всех окон перед выходом
    private void saveAllWindowsState() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof LogWindow) {
                WindowStateManager.saveWindowState(frame, "logWindow");
            } else if (frame instanceof GameWindow) {
                WindowStateManager.saveWindowState(frame, "gameWindow");
            }
        }
    }

    // Генерация меню (остается без изменений)
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Пункт меню "Выход"
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_Q);
        exitMenuItem.addActionListener((event) -> {
            saveAllWindowsState(); // Сохраняем состояние окон перед выходом
            exitHandler.confirmAndExit(null); // Передаем null для главного окна
        });
        fileMenu.add(exitMenuItem);

        // Меню "Режим отображения"
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        // Пункт меню "Системная схема"
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        // Пункт меню "Универсальная схема"
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        // Меню "Тесты"
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        // Пункт меню "Сообщение в лог"
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_M);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        // Добавляем все меню в менюбар
        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    // Метод для установки темы
    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}