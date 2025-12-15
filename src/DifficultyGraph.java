import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DifficultyGraph extends JPanel {

    private List<Integer> data;

    public DifficultyGraph(List<Integer> data) {
        this.data = data;
        setPreferredSize(new Dimension(500, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data.size() < 2) return;

        int w = getWidth();
        int h = getHeight();
        int max = data.stream().max(Integer::compare).orElse(1);

        int prevX = 20, prevY = h - (data.get(0) * h / max);

        for (int i = 1; i < data.size(); i++) {
            int x = 20 + i * (w - 40) / data.size();
            int y = h - (data.get(i) * h / max);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }
}
