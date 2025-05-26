package gui;

import log.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.InternalFrameEvent;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager = new WindowStateManager();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        RobotModel robotModel = new RobotModel();

        initWindows(robotModel);
        initMenu();
        initWindowListeners();
    }

    private void initWindows(RobotModel model) {
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(model);
        gameWindow.setSize(600, 500);
        addWindow(gameWindow);

        CoordinatesWindow coordinatesWindow = new CoordinatesWindow(model);
        coordinatesWindow.setSize(300, 120);
        addWindow(coordinatesWindow);
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

        frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                e.getInternalFrame().setVisible(false);
            }
        });
    }

    private void initMenu() {
        setJMenuBar(new MenuBar(this).createMenuBar());
    }

    private void initWindowListeners() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (confirmAndExit()) {
                    windowStateManager.saveWindowStates(desktopPane);
                    System.exit(0);
                }
            }
        });

        windowStateManager.loadWindowStates(desktopPane);
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error("Ошибка при установке Look and Feel: " + e.getMessage());
        }
    }

    public boolean confirmAndExit() {
        int option = JOptionPane.showOptionDialog(
                this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Да", "Нет"},
                "Нет"
        );

        return option == JOptionPane.YES_OPTION;
    }
}

