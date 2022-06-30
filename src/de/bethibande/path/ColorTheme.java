package de.bethibande.path;

import java.awt.*;

public class ColorTheme {

    private final Color obstacleColor;
    private final Color playerColor;
    private final Color goalColor;
    private final Color pathColor;

    public ColorTheme(Color obstacleColor, Color playerColor, Color goalColor, Color pathColor) {
        this.obstacleColor = obstacleColor;
        this.playerColor = playerColor;
        this.goalColor = goalColor;
        this.pathColor = pathColor;
    }

    public Color getPathColor() {
        return pathColor;
    }

    public Color getObstacleColor() {
        return obstacleColor;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public Color getGoalColor() {
        return goalColor;
    }
}
