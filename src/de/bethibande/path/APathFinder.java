package de.bethibande.path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple A* pathfinding
 */
public class APathFinder {

    private Board board;

    private int[][] nodes;
    private LinkedList<int[]> path;

    private int highestNumber = 0;
    private long calcTime = 0;

    public int[][] getNodes() {
        return nodes;
    }

    public List<int[]> getPath() {
        return path;
    }

    public long getCalcTime() {
        return calcTime;
    }

    public int getHighestNumber() {
        return highestNumber;
    }

    public void boardUpdate() {
        long start = System.currentTimeMillis();
        nodes = new int[board.getHeight()][board.getWidth()];

        for(int[] arr : nodes) {
            Arrays.fill(arr, -1);
        }

        explore(0, board.getGoal()[0], board.getGoal()[1]);

        calcPath();

        long end = System.currentTimeMillis();
        calcTime = end-start;
    }

    public void calcPath() {
        path = new LinkedList<>();

        int px = board.getPlayer()[0];
        int py = board.getPlayer()[1];

        int val = nodes[py][px];

        explorePath(val, px, py);
    }

    private int getNodeValue(int x, int y) {
        if(x < 0 || y < 0) return -1;
        if(x >= board.getWidth() || y >= board.getHeight()) return -1;

        return nodes[y][x];
    }

    private void explorePath(int val, int x, int y) {
        int v1 = getNodeValue(x-1, y);
        int v2 = getNodeValue(x+1, y);
        int v3 = getNodeValue(x, y-1);
        int v4 = getNodeValue(x, y+1);

        if(v1 == -1) v1 = Integer.MAX_VALUE;
        if(v2 == -1) v2 = Integer.MAX_VALUE;
        if(v3 == -1) v3 = Integer.MAX_VALUE;
        if(v4 == -1) v4 = Integer.MAX_VALUE;

        int smallest = Math.min(v1, Math.min(v2, Math.min(v3, v4)));

        if(v1 != -1 && v1 < val && v1 == smallest) {
            val = v1;
            path.add(new int[]{x-1, y});

            explorePath(val, x-1, y);
            return;
        }

        if(v2 != -1 && v2 < val && v2 == smallest) {
            val = v2;
            path.add(new int[]{x+1, y});

            explorePath(val, x+1, y);
            return;
        }

        if(v3 != -1 && v3 < val && v3 == smallest) {
            val = v3;
            path.add(new int[]{x, y-1});

            explorePath(val, x, y-1);
            return;
        }

        if(v4 != -1 && v4 < val && v4 == smallest) {
            val = v4;
            path.add(new int[]{x, y+1});

            explorePath(val, x, y+1);
        }
    }

    public void printNodes() {
        System.out.println("\nNodes:\n");
        for(int[] arr : nodes) {
            System.out.println(Arrays.toString(arr));
        }
    }

    public int getPlayerNodeValue() {
        return getNodeValue(board.getPlayer()[0], board.getPlayer()[1]);
    }

    public void explore(int prev, int x, int y) {
        if(x < 0 || y < 0) return;
        if(x >= board.getWidth() || y >= board.getHeight()) return;
        if(board.get(x, y) == (byte)1) return;

        // Increases performance drastically on large boards, but does not necessarily calculate the shortest de.bethibande.path, sometimes the de.bethibande.path will be ridiculously long
        //if(getPlayerNodeValue() != -1) return;

        int current = nodes[y][x];
        if(current == -1 || current > prev + 1) {
            nodes[y][x] = ++prev;
        } else return;

        if(prev > highestNumber) highestNumber = prev;

        explore(prev, x-1, y);
        explore(prev, x+1, y);
        explore(prev, x, y-1);
        explore(prev, x, y+1);
    }

    public void setBoard(Board board) {
        this.board = board;

        boardUpdate();
    }

    public Board getBoard() {
        return board;
    }
}
