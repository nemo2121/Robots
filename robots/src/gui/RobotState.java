package gui;

import java.io.Serializable;

public class RobotState implements Serializable {
    public final double robotX;
    public final double robotY;
    public final double direction;
    public final int targetX;
    public final int targetY;

    public RobotState(double robotX, double robotY, double direction, int targetX, int targetY) {
        this.robotX = robotX;
        this.robotY = robotY;
        this.direction = direction;
        this.targetX = targetX;
        this.targetY = targetY;
    }
}