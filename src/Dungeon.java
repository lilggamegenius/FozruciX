import java.awt.*;
import java.util.Random;

/**
 * Created by ggonz on 11/18/2015.
 */
enum Tile {
    EMPTY, WALL, ITEM, ENEMY
}

enum Direction {
    NORTH, SOUTH, EAST, WEST
}

public class Dungeon {
    final int mapSize = 100;
    Tile[][] tiles = new Tile[mapSize][mapSize];
    Random rand = new Random();
    Point currentPoint = new Point(0, 0);


    public Dungeon() {
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                int num = rand.nextInt();
                if (num <= 40) {
                    tiles[x][y] = Tile.EMPTY;
                } else if (num > 40 && num <= 60) {
                    tiles[x][y] = Tile.WALL;
                } else if (num > 60 && num <= 75) {
                    tiles[x][y] = Tile.ITEM;
                } else if (num > 75 && num <= 100) {
                    tiles[x][y] = Tile.ENEMY;
                }
            }
        }
    }

    public Tile[] getSurroundingTiles() {
        Tile[] temp = new Tile[4];
        temp[0] = tiles[(int) currentPoint.getX()][(int) currentPoint.getY() + 1];
        temp[1] = tiles[(int) currentPoint.getX() + 1][(int) currentPoint.getY()];
        temp[2] = tiles[(int) currentPoint.getX()][(int) currentPoint.getY() - 1];
        temp[3] = tiles[(int) currentPoint.getX() - 1][(int) currentPoint.getY()];
        return temp;
    }

    public int move(Point p) {
        Tile tempTile = tiles[currentPoint.x + p.x][currentPoint.y + p.y];
        if (tempTile == Tile.EMPTY) {
            currentPoint.move(p.x, p.y);
            return 0;
        } else if (tempTile == Tile.ITEM) {
            currentPoint.move(p.x, p.y);
            return 1;
        } else if (tempTile == Tile.WALL) {
            return -1;
        } else if (tempTile == Tile.ENEMY) {
            return 2;
        } else return -2;
    }

    public void setLocation(Point p) {
        currentPoint.setLocation(p);
    }
}
