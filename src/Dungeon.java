import java.awt.*;

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
    Point currentPoint = new Point(mapSize / 2, mapSize / 2);


    public Dungeon() {
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                int num = MyBotX.randInt(0, 100);
                if (num <= 60) {
                    tiles[x][y] = Tile.EMPTY;
                } else if (num > 60 && num <= 85) {
                    tiles[x][y] = Tile.WALL;
                } else if (num > 85 && num <= 90) {
                    tiles[x][y] = Tile.ITEM;
                } else if (num > 90 && num <= 100) {
                    tiles[x][y] = Tile.ENEMY;
                }
            }
        }
        tiles[mapSize / 2][mapSize / 2] = Tile.EMPTY;
    }

    public Tile[] getSurroundingTiles() {
        Tile[] temp = new Tile[9];
        temp[0] = tiles[(int) currentPoint.getX()][(int) currentPoint.getY() + 1];
        temp[1] = tiles[(int) currentPoint.getX() + 1][(int) currentPoint.getY() + 1];
        temp[2] = tiles[(int) currentPoint.getX() + 1][(int) currentPoint.getY()];
        temp[3] = tiles[(int) currentPoint.getX() + 1][(int) currentPoint.getY() - 1];
        temp[4] = tiles[(int) currentPoint.getX()][(int) currentPoint.getY() - 1];
        temp[5] = tiles[(int) currentPoint.getX() - 1][(int) currentPoint.getY() - 1];
        temp[6] = tiles[(int) currentPoint.getX() - 1][(int) currentPoint.getY()];
        temp[7] = tiles[(int) currentPoint.getX() - 1][(int) currentPoint.getY() + 1];
        temp[8] = tiles[(int) currentPoint.getX()][(int) currentPoint.getY()];
        return temp;
    }

    public int move(Point p) {
        if (p.getX() + currentPoint.getX() < 0) {
            p.setLocation(0, p.getY());
        }
        if (p.getX() + currentPoint.getX() > mapSize) {
            p.setLocation(mapSize, p.getY());
        }
        if (p.getY() + currentPoint.getY() < 0) {
            p.setLocation(p.getX(), 0);
        }
        if (p.getY() + currentPoint.getY() > mapSize) {
            p.setLocation(p.getX(), mapSize);
        }

        Tile tempTile = tiles[currentPoint.x + p.x][currentPoint.y + p.y];
        if (tempTile == Tile.EMPTY) {
            currentPoint.translate(p.x, p.y);
            return 0;
        } else if (tempTile == Tile.ITEM) {
            currentPoint.translate(p.x, p.y);
            return 1;
        } else if (tempTile == Tile.WALL) {
            return -1;
        } else if (tempTile == Tile.ENEMY) {
            return 2;
        } else return -2;
    }

    public void setLocation(int p1, int p2) {
        currentPoint.setLocation(p1 + (mapSize / 2), p2 + (mapSize / 2));
    }

    public Point getLocation() {
        return currentPoint;
    }

    @Override
    public String toString() {
        return "(" + currentPoint.x + "," + currentPoint.x + ")";
    }
}
