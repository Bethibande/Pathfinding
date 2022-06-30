package de.bethibande.path;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Pathfinding {

    public static final int CELL_SIZE = 25; // cell size in px
    public static final int CELLS = 25; // board size Cells x Cells

    public static Window w;
    public static Board board;
    public static ColorTheme theme;
    public static APathFinder pathFinder;

    public static byte build_type = 1;

    public static void main(String[] args) {
        w = new Window("Pathfinding - A*", CELLS, CELL_SIZE);
        board = Board.ofRandom(CELLS, CELLS);
        theme = new ColorTheme(new Color(50, 50, 70), new Color(150, 150, 200), new Color(150, 200, 150), new Color(150, 200, 200));
        pathFinder = new APathFinder();

        pathFinder.setBoard(board);

        w.setBoard(board);
        w.setTheme(theme);
        w.setPathFinder(pathFinder);

        w.setOnClick(Pathfinding::onClick);
        w.setOnKey(Pathfinding::onKey);

        w.repaint();
    }

    public static void onKey(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_F5) {
            board.clearBoard();
            board = Board.ofRandom(CELLS, CELLS);

            w.setBoard(board);

            pathFinder.setBoard(board);
            pathFinder.boardUpdate();

            w.repaint();
        }
    }

    public static void onClick(MouseEvent e) {
        int x = e.getX()/CELL_SIZE;
        int y = e.getY()/CELL_SIZE;

        if(board.get(x, y) == build_type) {
            board.set((byte)0, x, y);

            pathFinder.boardUpdate();
            w.repaint();
            return;
        }

        board.set(build_type, x, y);

        pathFinder.boardUpdate();

        w.repaint();
    }

}
