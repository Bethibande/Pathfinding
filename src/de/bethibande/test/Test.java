package de.bethibande.test;

import de.bethibande.path.*;

import java.awt.*;

public class Test {

    public static final int CELL_SIZE = 25; // cell size in px
    public static final int CELLS = 25; // board size Cells x Cells

    public static Window window;
    public static Renderer renderer;

    public static Board board;
    public static ColorTheme theme;
    public static APathFinder pathFinder;

    public static byte build_type = 1;

    public static void main(String[] args) {
        window = new Window();
        board = Board.ofRandom(CELLS, CELLS);
        theme = new ColorTheme(new Color(50, 50, 70), new Color(150, 150, 200), new Color(150, 200, 150), new Color(150, 200, 200));
        pathFinder = new APathFinder();

        pathFinder.setBoard(board);

        window.setBoard(board);
        window.setPathfinder(pathFinder);
        window.setTheme(theme);

        window.setSize(900, 900);
        window.setTitle("Test");
        window.setResizable(false);

        window.setDefaultCloseOperation(3);

        window.setVisible(true);
        window.revalidate();
        window.createBufferStrategy(2);

        window.setLoadAnimationTime(2500);

        renderer = new Renderer();
        renderer.setFps_cap(30);

        renderer.setRenderFunc(window::render);

        window.setRenderer(renderer);

        renderer.start();
    }

    public static void regenBoard() {
        while(true) {
            board.clearBoard();
            board = Board.ofRandom(CELLS, CELLS);

            window.setBoard(board);
            pathFinder.setBoard(board);

            if(!pathFinder.getPath().isEmpty() && pathFinder.getPath().size() > 5) {
                break;
            }
        }

        System.gc();
    }

}
