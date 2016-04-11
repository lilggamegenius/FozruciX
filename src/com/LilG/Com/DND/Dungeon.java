package com.LilG.Com.DND;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ggonz on 11/18/2015.
 * The com.LilG.Com.DND.Dungeon! (Dun dun duuuuuuun!)
 */

public class Dungeon {
    private final List<int[]> rooms = new ArrayList<>();
	private final int map_size = 64;
	private final int[][] map = new int[map_size][map_size];
	private boolean currentPointSet = false;
	private Point currentPoint;


    public Dungeon() {
        generate();
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

	private void generate(){

		for(int x = 0; x < map_size; x++){
			for(int y = 0; y < map_size; y++){
				map[x][y] = 0;
			}
		}

		int min_size = 5;
        int max_size = 15;
        int room_count = randInt(10, 20);

		for(int i = 0; i < room_count; i++){
            int room[] = new int[4];

            room[0] = randInt(1, map_size - max_size - 1); //x
            room[1] = randInt(1, map_size - max_size - 1); //y
            room[2] = randInt(min_size, max_size); //w
            room[3] = randInt(min_size, max_size); //h

			if (DoesCollide(room, i)){
				i--;
				continue;
			}
			room[2]--;
			room[3]--;

			rooms.add(room);
		}

		SquashRooms();

		for(int i = 0; i < room_count; i++){
			int[] roomA = rooms.get(i);
			int[] roomB;
			if (FindClosestRoom(roomA) == null){
				roomB = new int[]{0, 0, 0, 0};
			} else{
				roomB = FindClosestRoom(roomA);
			}

            Point pointA = new Point(
                    randInt(roomA[0], roomA[0] + roomA[2]),
                    randInt(roomA[1], roomA[1] + roomA[3]));
            Point pointB = new Point(
                    randInt(roomB[0], roomB[0] + roomB[2]),
                    randInt(roomB[1], roomB[1] + roomB[3]));

			while ((pointB.x != pointA.x) || (pointB.y != pointA.y)){
				if (pointB.x != pointA.x){
					if (pointB.x > pointA.x) pointB.x--;
					else pointB.x++;
				} else {
					if (pointB.y > pointA.y) pointB.y--;
					else pointB.y++;
				}
				if (!currentPointSet){
					currentPoint = pointB;
					currentPointSet = true;
				}
				map[pointB.x][pointB.y] = 1;
			}
		}

		for(int i = 0; i < room_count; i++){
			int[] room = rooms.get(i);
			for(int x = room[0]; x < room[0] + room[2]; x++){
				for(int y = room[1]; y < room[1] + room[3]; y++){
					map[x][y] = 1;
				}
			}
		}

		for(int x = 0; x < map_size; x++){
			for(int y = 0; y < map_size; y++){
				if (map[x][y] == 1){
					for(int xx = x - 1; xx <= x + 1; xx++){
						for(int yy = y - 1; yy <= y + 1; yy++){
							if (map[xx][yy] == 0) map[xx][yy] = 2;
						}
					}
				}
			}
		}
	}

	private boolean DoesCollide(int[] room, int ignore){
		for(int i = 0; i < rooms.size(); i++){
			if (i == ignore) continue;
			int[] check = rooms.get(i);
			if (!((room[0] + room[2] < check[0]) || (room[0] > check[0] + check[2]) || (room[1] + room[3] < check[1]) || (room[1] > check[1] + check[3])))
				return true;
		}

		return false;
	}

	private void SquashRooms(){
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < rooms.size(); j++){
				int[] room = rooms.get(j);
				while (true){
					Point old_position = new Point(
							room[0],
							room[1]
					);
					if (room[0] > 1) room[0]--;
					if (room[1] > 1) room[1]--;
					if ((room[0] == 1) && (room[1] == 1)) break;
					if (DoesCollide(room, j)){
						room[0] = old_position.x;
						room[1] = old_position.y;
						break;
					}
                }
            }
        }
    }

	private int[] FindClosestRoom(int[] room){
		int[] mid = {
                room[0] + (room[2] / 2),
                room[1] + (room[3] / 2)
        };
        int[] closest = null;
        int closest_distance = 1000;
        for (int[] check : rooms) {
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

	public void setLocation(int x, int y){
		currentPoint.move(x, y);
    }

    public Point getLocation() {
        return currentPoint;
    }

    public void move(int x, int y) {
        currentPoint.translate(x, y);
    }

    public int[] getSurrounding() {
        int[] temp = new int[9];
        temp[0] = map[(int) currentPoint.getX()][(int) currentPoint.getY() + 1];
        temp[1] = map[(int) currentPoint.getX() + 1][(int) currentPoint.getY() + 1];
        temp[2] = map[(int) currentPoint.getX() + 1][(int) currentPoint.getY()];
        temp[3] = map[(int) currentPoint.getX() + 1][(int) currentPoint.getY() - 1];
        temp[4] = map[(int) currentPoint.getX()][(int) currentPoint.getY() - 1];
        temp[5] = map[(int) currentPoint.getX() - 1][(int) currentPoint.getY() - 1];
        temp[6] = map[(int) currentPoint.getX() - 1][(int) currentPoint.getY()];
        temp[7] = map[(int) currentPoint.getX() - 1][(int) currentPoint.getY() + 1];
        temp[8] = map[(int) currentPoint.getX()][(int) currentPoint.getY()];
        return temp;
    }

    public int[][] getMap() {
        return map;
    }

    public int getMap_size() {
        return map_size;
    }

}
