package gui;

import java.awt.*;
import java.util.Observable;
import javax.swing.Timer;

public class RobotModel extends Observable {
    private RobotState state;
    private RobotState lastPublishedState;
    private final Timer updateTimer;

    private static final double CRUISE_VELOCITY = 2.0;
    private static final double PRECISION_VELOCITY = 0.6;
    private static final double MAX_ANGULAR_VELOCITY = 0.05;
    private static final double STOP_THRESHOLD = 1.0;
    private static final double INSTANT_TURN_ZONE = 8.0;
    private static final double PRECISION_ZONE = 20.0;
    private static final double MIN_TARGET_DISTANCE = 5.0;

    public RobotModel() {
        this.state = new RobotState(100, 100, 0, 150, 100);
        this.lastPublishedState = state;


        this.updateTimer = new Timer(40, e -> {
            update(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        });
        updateTimer.start();
    }

    public void update(Dimension bounds) {
        double dx = state.targetX - state.robotX;
        double dy = state.targetY - state.robotY;
        double distance = Math.hypot(dx, dy);

        if (distance <= STOP_THRESHOLD) {
            state = new RobotState(state.targetX, state.targetY, state.direction, state.targetX, state.targetY);
            notifyIfChanged();
            return;
        }

        double velocity = distance > PRECISION_ZONE ? CRUISE_VELOCITY : PRECISION_VELOCITY;
        double targetAngle = Math.atan2(dy, dx);

        double newDirection;
        if (distance < INSTANT_TURN_ZONE) {
            newDirection = targetAngle;
        } else {
            double angleDiff = normalizeAngle(targetAngle - state.direction);
            double angularVelocity = Math.signum(angleDiff) *
                    Math.min(Math.abs(angleDiff)/2, MAX_ANGULAR_VELOCITY);
            newDirection = normalizeAngle(state.direction + angularVelocity);
        }

        double newRobotX = state.robotX + velocity * Math.cos(newDirection);
        double newRobotY = state.robotY + velocity * Math.sin(newDirection);

        // Проверка границ
        newRobotX = Math.max(15, Math.min(newRobotX, bounds.width - 15));
        newRobotY = Math.max(15, Math.min(newRobotY, bounds.height - 15));

        state = new RobotState(newRobotX, newRobotY, newDirection, state.targetX, state.targetY);
        notifyIfChanged();
    }

    private void notifyIfChanged() {
        if (!state.equals(lastPublishedState)) {
            lastPublishedState = state;
            setChanged();
            notifyObservers(state);
        }
    }

    public void setTargetPosition(Point p, Dimension bounds) {
        double newDist = Math.hypot(p.x - state.robotX, p.y - state.robotY);
        if (newDist < MIN_TARGET_DISTANCE) return;

        int newTargetX = Math.max(1, Math.min(p.x, bounds.width - 1));
        int newTargetY = Math.max(1, Math.min(p.y, bounds.height - 1));

        state = new RobotState(state.robotX, state.robotY, state.direction, newTargetX, newTargetY);
        notifyIfChanged();
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2*Math.PI;
        while (angle < -Math.PI) angle += 2*Math.PI;
        return angle;
    }

    public RobotState getCurrentState() {
        return state;
    }
}