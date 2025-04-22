package gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WindowStateManager {
    private static final String FILE_PATH = "window_states.txt";

    public static void saveStates(List<WindowState> states) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (WindowState state : states) {
                writer.write(String.format(
                        "%s %d %d %d %d\n",
                        state.getWindowType(),
                        state.getX(),
                        state.getY(),
                        state.getWidth(),
                        state.getHeight()
                ));
            }
        }
    }

    public static List<WindowState> loadStates() throws IOException {
        List<WindowState> states = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return states;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 5) continue;

                states.add(new WindowState(
                        parts[0],  // тип
                        Integer.parseInt(parts[1]),  // x
                        Integer.parseInt(parts[2]),  // y
                        Integer.parseInt(parts[3]),  // ширина
                        Integer.parseInt(parts[4])   // высота
                ));
            }
        }
        return states;
    }
}
