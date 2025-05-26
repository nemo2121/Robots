package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class CoordinatesWindow extends JInternalFrame implements Observer {
    private final JLabel coordinatesLabel;

    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        model.addObserver(this);
        setName("CoordinatesWindow");

        coordinatesLabel = new JLabel("", SwingConstants.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        coordinatesLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setContentPane(new JPanel(new BorderLayout()) {{
            add(coordinatesLabel, BorderLayout.CENTER);
        }});

        setSize(300, 120);
        setLocation(650, 10);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof RobotState) {
            RobotState state = (RobotState) arg;
            SwingUtilities.invokeLater(() -> updateCoordinates(state));
        }
    }

    private void updateCoordinates(RobotState state) {
        String text = String.format("<html><center>Робот: (%.1f, %.1f)<br>Цель: (%d, %d)<br>Направление: %.2f°</center></html>",
                state.robotX,
                state.robotY,
                state.targetX,
                state.targetY,
                Math.toDegrees(state.direction));

        coordinatesLabel.setText(text);
    }
}