package de.bethibande.path;

import java.util.concurrent.ThreadLocalRandom;

public class Board {

    public static Board ofRandom(int width, int height) {
        Board b = new Board(width, height);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(ThreadLocalRandom.current().nextInt(0, 3) == 0) b.set((byte)1, x, y);
            }
        }

        int px = ThreadLocalRandom.current().nextInt(0, width);
        int py = ThreadLocalRandom.current().nextInt(0, height);

        b.set((byte)2, px, py);

        int gx = ThreadLocalRandom.current().nextInt(0, width);
        int gy = ThreadLocalRandom.current().nextInt(0, height);

        b.set((byte)3, gx, gy);

        return b;
    }

    private final int width;
    private final int height;

    private int[] player;
    private int[] goal;

    /**
     * Byte values, <br>
     * 0 = nothing <br>
     * 1 = obstacle <br>
     * 2 = player <br>
     * 3 = goal <br>
     */
    private byte[][] board;

    public Board(int width, int height) {
        board = new byte[height][width];

        this.width = width;
        this.height = height;
    }

    public int[] getPlayer() {
        return player;
    }

    public int[] getGoal() {
        return goal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void set(byte value, int x, int y) {
        board[y][x] = value;

        if(value == (byte)2) player = new int[]{x, y};
        if(value == (byte)3) goal = new int[]{x, y};
    }

    public byte get(int x, int y) {
        return board[y][x];
    }

    public void clearBoard() {
        board = new byte[height][width];
    }

    public byte[][] getBoard() {
        return board;
    }
}
