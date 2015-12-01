import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ggonz on 11/18/2015.
 */

public class Dungeon {
    int map_size = 64;
    int[][] map = new int[map_size][map_size];
    List<int[]> rooms = new ArrayList<>();


    public Dungeon() {
        for (int x = 0; x < map_size; x++) {
            for (int y = 0; y < map_size; y++) {
                map[x][y] = 0;
            }
        }

        int min_size = 5;
        int max_size = 15;
        int room_count = MyBotX.randInt(10, 20);

        for (int i = 0; i < room_count; i++) {
            int room[] = new int[4];

            room[0] = MyBotX.randInt(1, this.map_size - max_size - 1); //x
            room[1] = MyBotX.randInt(1, this.map_size - max_size - 1); //y
            room[2] = MyBotX.randInt(min_size, max_size); //w
            room[3] = MyBotX.randInt(min_size, max_size); //h

            if (this.DoesCollide(room, i)) {
                i--;
                continue;
            }
            room[2]--;
            room[3]--;

            rooms.add(room);

            SquashRooms();

            for (i = 0; i < room_count; i++) {
                int[] roomA = rooms.get(i);
                int[] roomB = FindClosestRoom(roomA);
                Point pointA = new Point(
                        MyBotX.randInt(roomA[0], roomA[0] + roomA[2]),
                        MyBotX.randInt(roomA[1], roomA[1] + roomA[3]));
                Point pointB = new Point(
                        MyBotX.randInt(roomB[0], roomB[0] + roomB[2]),
                        MyBotX.randInt(roomB[1], roomB[1] + roomB[3]));
                while ((pointB.x != pointA.x) || (pointB.y != pointA.y)) {
                    if (pointB.x != pointA.x) {
                        if (pointB.x > pointA.x) pointB.x--;
                        else pointB.x++;
                    } else if (pointB.y != pointA.y) {
                        if (pointB.y > pointA.y) pointB.y--;
                        else pointB.y++;
                    }

                    map[pointB.x][pointB.y] = 1;
                }
            }

            for (i = 0; i < room_count; i++) {
                room = rooms.get(i);
                for (int x = room[0]; x < room[0] + room[2]; x++) {
                    for (int y = room[1]; y < room[1] + room[3]; y++) {
                        map[x][y] = 1;
                    }
                }
            }

            for (int x = 0; x < map_size; x++) {
                for (int y = 0; y < map_size; y++) {
                    if (map[x][y] == 1) {
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            for (int yy = y - 1; yy <= y + 1; yy++) {
                                if (map[xx][yy] == 0) map[xx][yy] = 2;
                            }
                        }
                    }
                }
            }
        }
    }

    public int[] FindClosestRoom(int[] room) {
        int[] mid = {
                room[0] + (room[2] / 2),
                room[1] + (room[3] / 2)
        };
        int[] closest = null;
        int closest_distance = 1000;
        for (int i = 0; i < rooms.size(); i++) {
            int[] check = rooms.get(i);
            if (check == room) continue;
            int[] check_mid = {
                    check[0] + (check[2] / 2),
                    check[1] + (check[3] / 2)
            };
            int distance = Math.abs(mid[0] - check_mid[0]) + Math.abs(mid[1] - check_mid[1]);
            if (distance < closest_distance) {
                closest_distance = distance;
                closest = check;
            }
        }
        return closest;
    }

    public boolean DoesCollide(int[] room, int ignore) {
        for (int i = 0; i < this.rooms.size(); i++) {
            if (i == ignore) continue;
            int[] check = this.rooms.get(i);
            if (!((room[0] + room[2] < check[0]) || (room[0] > check[0] + check[2]) || (room[1] + room[3] < check[1]) || (room[1] > check[1] + check[3])))
                return true;
        }

        return false;
    }

    public void SquashRooms() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < this.rooms.size(); j++) {
                int[] room = this.rooms.get(j);
                while (true) {
                    Point old_position = new Point(
                            room[0],
                            room[1]
                    );
                    if (room[0] > 1) room[0]--;
                    if (room[1] > 1) room[1]--;
                    if ((room[0] == 1) && (room[1] == 1)) break;
                    if (DoesCollide(room, j)) {
                        room[0] = old_position.x;
                        room[1] = old_position.y;
                        break;
                    }
                }
            }
        }
    }

}
