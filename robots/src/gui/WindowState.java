package gui;

public class WindowState {
    private final String windowType; // "LogWindow" или "GameWindow"
    private final int x, y, width, height;

    public WindowState(String windowType, int x, int y, int width, int height) {
        this.windowType = windowType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Геттеры
    public String getWindowType() { return windowType; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
