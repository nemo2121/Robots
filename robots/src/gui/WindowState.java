package gui;

import java.awt.Rectangle;
import java.io.Serializable;

public class WindowState implements Serializable {
    private String windowName;
    private Rectangle bounds;
    private boolean isCollapsed;


    public WindowState(String windowName, Rectangle bounds, boolean isCollapsed) {
        this.windowName = windowName;
        this.bounds = bounds;
        this.isCollapsed = isCollapsed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }
}