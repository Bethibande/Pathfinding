package de.bethibande.path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

public class Window {

    private final JFrame frame;
    private final JPanel root;
    private final int cells;
    private final int cellSize;

    private Board board = null;
    private ColorTheme theme = null;
    private APathFinder pathFinder = null;
    private Consumer<MouseEvent> onClick;
    private Consumer<KeyEvent> onKey;

    public Window(String title, int cells, int cellSize) {
        frame = new JFrame();
        this.cells = cells;
        this.cellSize = cellSize;

        frame.setTitle(title);
        frame.setSize(cellSize * cells + 65, cellSize * cells + 90);
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel fRoot = new JPanel();
        fRoot.setSize(frame.getWidth(), frame.getHeight());
        fRoot.setLayout(new GroupLayout(fRoot));
        frame.add(fRoot);

        root = new JPanel() {
            @Override
            public void paint(Graphics g) {
                paintWindow(g);
            }
        };
        root.setLocation(25, 25);
        root.setSize(cellSize * cells, cellSize * cells);
        root.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClickedWindow(e);
            }
        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(onKey == null) return;
                onKey.accept(e);
            }
        });

        fRoot.add(root);

        frame.setVisible(true);
        frame.validate();
    }

    public void setPathFinder(APathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public void setTheme(ColorTheme theme) {
        this.theme = theme;
    }

    public void setBoard(Board board) {
        this.board = board;

        repaint();
    }

    public void setOnClick(Consumer<MouseEvent> onClick) {
        this.onClick = onClick;
    }

    public void setOnKey(Consumer<KeyEvent> onKey) {
        this.onKey = onKey;
    }

    private void onMouseClickedWindow(MouseEvent e) {
        if(onClick != null) onClick.accept(e);
    }

    public void repaint() {
        this.root.repaint();
    }

    private void paintWindow(Graphics g2){
        Graphics2D g = (Graphics2D) g2;

        g.clearRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);

        if(board == null || theme == null) return;

        int x = 0;
        int y = 0;
        for(byte[] row : board.getBoard()) {
            for(byte b : row) {
                if(b == 1) {
                    g.setColor(theme.getObstacleColor());
                    g.fillRect(x, y, cellSize, cellSize);
                }
                if(b == 2) {
                    g.setColor(theme.getPlayerColor());
                    g.fillRect(x, y, cellSize, cellSize);
                }
                if(b == 3) {
                    g.setColor(theme.getGoalColor());
                    g.fillRect(x, y, cellSize, cellSize);
                }

                x += cellSize;
            }
            x = 0;
            y += cellSize;
        }

        if(pathFinder == null) return;

        g.setFont(g.getFont().deriveFont((float)cellSize/2f));

        x = 0;
        y = 0;
        int max = pathFinder.getHighestNumber();
        for(int[] row : pathFinder.getNodes()) {
            for(int n : row) {
                if(n == -1) {
                    x += cellSize;
                    continue;
                }

                //g.setColor(calcColor(n, max));
                g.setColor(new Color(150, 200, 150));

                Rectangle2D b = g.getFontMetrics().getStringBounds(n + "", g);
                int tx = x + ((cellSize/2) - ((int)b.getWidth()/2));
                int ty = y + ((cellSize/2) + ((int)b.getHeight()/2));

                g.drawString(n + "", tx, ty);

                x += cellSize;
            }
            x = 0;
            y += cellSize;
        }

        if(pathFinder.getPath().isEmpty()) {
            g.setColor(new Color(200, 150, 150));
            g.setFont(g.getFont().deriveFont(15.5f));
            g.drawString("No Path found", 25, 25);

            return;
        }

        int[] lastNode = board.getPlayer();
        int strokeSize = cellSize / 5;
        if(strokeSize <= 1) strokeSize = 2;
        int offX = cellSize / 2 - strokeSize / 2;
        int offY = cellSize / 2 + strokeSize / 2;

        g.setColor(theme.getPathColor());
        g.setStroke(new BasicStroke(strokeSize));

        for(int[] pathNode : pathFinder.getPath()) {

            g.drawLine(lastNode[0]*cellSize + offX, lastNode[1]*cellSize + offY, pathNode[0]*cellSize + offX, pathNode[1]*cellSize + offY);

            lastNode = pathNode;
        }

        g.setColor(Color.black);

        g.setFont(g.getFont().deriveFont(15.5f));
        g.drawString("Time: " + pathFinder.getCalcTime() + " ms", 25, 25);
    }

    /*public Color calcColor(int num, int max) {
        int rgb = com.material.colors.blend.Blend.blendCam16Ucs(new Color(150, 200, 150).getRGB(), new Color(255, 150, 150).getRGB(), (float)num / (float)max);

        return new Color(rgb);
    }*/

}
