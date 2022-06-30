package de.bethibande.test;

import de.bethibande.path.APathFinder;
import de.bethibande.path.Board;
import de.bethibande.path.ColorTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Window extends JFrame {
    private final Map<RenderingHints.Key, Object> hints = Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,  RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    private Board board;
    private APathFinder pathFinder;
    private ColorTheme theme;
    private Renderer renderer;

    private int cellSize = 35;
    private int loadAnimationTime = 1000;

    private Long lastPlayerMove = 0L;
    private boolean canMove = true;

    boolean loading = false;
    private Long loadStart = 0L;
    private Long loadEnd = 0L;

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public void setLoadAnimationTime(int loadAnimationTime) {
        this.loadAnimationTime = loadAnimationTime;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPathfinder(APathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public void setTheme(ColorTheme theme) {
        this.theme = theme;
    }

    // sqrt(1 - pow(x - 1, 4))
    // or
    // sqrt(1 - pow(x - 1, 2))
    public double calcPlayerOffset() {
        double x = (System.currentTimeMillis() - lastPlayerMove) / 1000D;
        double num = Math.sqrt(1 - Math.pow(x - 1, 4));

        if(num < 0 || x < 0) return 0;
        if(num > 1 || x > 1) return 1;
        return num;
    }

    public void render() {
        Graphics2D g = (Graphics2D) super.getBufferStrategy().getDrawGraphics();
        g.setRenderingHints(hints);

        g.clearRect(0, 0, getWidth(), getHeight());

        if(!pathFinder.getPath().isEmpty() && !canMove && loadEnd == 0L) {
            loadEnd = System.currentTimeMillis();
        }

        //-----------------------------------------------------------------------------------------------------
        // draw board
        //-----------------------------------------------------------------------------------------------------

        if(board == null || theme == null) return;

        int x = board.getPlayer()[0];
        int y = board.getPlayer()[1];

        if(lastPlayerMove == 0L) lastPlayerMove = System.currentTimeMillis();
        double offset = calcPlayerOffset();
        int pOffX = 0;
        int pOffY = 0;

        if(pathFinder != null && !pathFinder.getPath().isEmpty() && canMove) {
            int[] node = pathFinder.getPath().get(0);

            if(node[0] > x) pOffX = (int)(cellSize * offset);
            if(node[0] < x) pOffX = (int)(cellSize * offset) * -1;
            if(node[1] > y) pOffY = (int)(cellSize * offset);
            if(node[1] < y) pOffY = (int)(cellSize * offset) * -1;

            if(offset > 0.98D) {
                if(pOffX > 0) pOffX = cellSize;
                if(pOffX < 0) pOffX = cellSize * -1;
                if(pOffY > 0) pOffY = cellSize;
                if(pOffY < 0) pOffY = cellSize * -1;
            }

            if(offset == 1D) {
                lastPlayerMove = System.currentTimeMillis();

                if(!pathFinder.getPath().isEmpty()) {
                    int[] newPos = pathFinder.getPath().get(0);

                    board.set((byte) 0, board.getPlayer()[0], board.getPlayer()[1]);
                    board.set((byte) 2, newPos[0], newPos[1]);

                    pathFinder.getPath().remove(0);
                }

                pOffX = 0;
                pOffY = 0;
            }
        }

        x = 6;
        y = 6;
        for(byte[] row : board.getBoard()) {
            for(byte b : row) {
                if(b == 1) {
                    g.setColor(theme.getObstacleColor());
                    g.fillRect(x, y, cellSize, cellSize);
                }
                if(b == 3) {
                    g.setColor(theme.getGoalColor());
                    g.fillRect(x, y, cellSize, cellSize);
                }

                x += cellSize;
            }
            x = 6;
            y += cellSize;
        }

        g.setColor(theme.getPlayerColor());
        g.fillRect(board.getPlayer()[0] * cellSize + pOffX + 6, board.getPlayer()[1] * cellSize + pOffY + 6, cellSize, cellSize);

        if(pathFinder.getPath().isEmpty() && loadStart == 0L) {
            g.setColor(new Color(200, 150, 150));
            g.setFont(g.getFont().deriveFont(15.5f));
            g.drawString("No Path found", 25, 25);

            canMove = false;
            loadStart = System.currentTimeMillis();
        }

        //-----------------------------------------------------------------------------------------------------
        // draw de.bethibande.path
        //-----------------------------------------------------------------------------------------------------

        int[] lastNode = board.getPlayer();
        int strokeSize = cellSize / 5;
        if(strokeSize <= 1) strokeSize = 2;
        int offX = cellSize / 2 - strokeSize / 2 + 6;
        int offY = cellSize / 2 + strokeSize / 2 + 6;

        g.setColor(theme.getPathColor());
        g.setStroke(new BasicStroke(strokeSize));

        int i = 0;
        for(int[] pathNode : pathFinder.getPath()) {

            if(i == 0) {
                g.drawLine(lastNode[0]*cellSize + offX + pOffX, lastNode[1]*cellSize + offY + pOffY, pathNode[0]*cellSize + offX, pathNode[1]*cellSize + offY);
            } else g.drawLine(lastNode[0]*cellSize + offX, lastNode[1]*cellSize + offY, pathNode[0]*cellSize + offX, pathNode[1]*cellSize + offY);

            i++;
            lastNode = pathNode;
        }

        //-----------------------------------------------------------------------------------------------------
        // fade in and out while loading
        //-----------------------------------------------------------------------------------------------------

        if(loadStart != 0L || loadEnd != 0) {
            int alpha = 0;

            if(loadStart != 0L) {
                int time = (int)(System.currentTimeMillis() - loadStart);

                if(time >= loadAnimationTime) {
                    alpha = 255;

                    if(!loading) {
                        new Thread(Test::regenBoard).start();
                        loading = true;
                    }
                } else {
                    alpha = (int)((float)time / (float)loadAnimationTime * 255);
                }
            }

            if(loadEnd != 0L) {
                int time = (int)(System.currentTimeMillis() - loadEnd);

                if(time > loadAnimationTime) {
                    alpha = 0;

                    loadEnd = 0L;
                    loadStart = 0L;

                    canMove = true;
                    loading = false;

                    lastPlayerMove = System.currentTimeMillis();
                } else alpha = 255 - (int)((float)time / (float)loadAnimationTime * 255);
            }

            //g.setComposite(AlphaComposite.SrcOver);
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        //-----------------------------------------------------------------------------------------------------
        // debug drawing
        //-----------------------------------------------------------------------------------------------------

        g.setColor(new Color(150, 200, 150));
        g.setFont(g.getFont().deriveFont(25f));
        g.drawString("FPS: " + renderer.getCurrentFPS(), 6, 50);

        super.getBufferStrategy().show();
    }

    /*public Color calcColor(int num, int max) {
        int rgb = com.material.colors.blend.Blend.blendCam16Ucs(new Color(150, 200, 150).getRGB(), new Color(255, 150, 150).getRGB(), (float)num / (float)max);

        return new Color(rgb);
    }*/
}
