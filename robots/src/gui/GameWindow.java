package gui;

import javax.swing.*;

public class GameWindow extends JInternalFrame {
    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        setName("GameWindow");
        GameVisualizer visualizer = new GameVisualizer(model);
        add(visualizer);

        setSize(600, 500);
        setLocation(10, 10);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }
}

