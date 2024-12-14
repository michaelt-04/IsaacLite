package github.tankgame.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    private static final int WIDTH = 5;
    private static final int HEIGHT = 4;
    private static final int START_ROOM = 10;

    // Fields for room grid, special room markers, and random number generator
    private boolean[] roomGrid; // Array representing the rooms on the grid
    private boolean[] itemRooms; // Array to mark item rooms
    private boolean[] bossRooms; // Array to mark boss rooms
    private List<Integer> roomQueue; // Queue for room generation
    private List<Integer> deadEnds; // List of dead end rooms
    private Random random;

    // Constructor that initializes the map generator and generates the map
    public MapGenerator() {
        roomGrid = new boolean[WIDTH * HEIGHT];
        itemRooms = new boolean[WIDTH * HEIGHT];
        bossRooms = new boolean[WIDTH * HEIGHT];
        roomQueue = new ArrayList<>();
        deadEnds = new ArrayList<>();
        random = new Random();
        generateMap();
    }

    // Public methods to get the generated room data
    public boolean[] getRoomGrid() {
        return roomGrid;
    }

    public boolean[] getItemRooms() {
        return itemRooms; // Array of booleans marking item rooms
    }

    public boolean[] getBossRooms() {
        return bossRooms; // Array of booleans marking boss rooms
    }

    // Map generation method
    private void generateMap() {
        roomGrid[START_ROOM] = true;
        roomQueue.add(START_ROOM);

        int numRooms = random.nextInt(2) + 5 + 2; // 7 or 8 rooms

        // Generate rooms until the numRooms is reached or roomQueue is empty
        while (!roomQueue.isEmpty() && numRooms > countRooms()) {
            int currentCell = roomQueue.remove(0);
            boolean addedRoom = false;

            for (int direction : new int[]{-WIDTH, WIDTH, -1, 1}) {
                int neighborCell = currentCell + direction;
                if (isValidCell(neighborCell) && canAddRoom(neighborCell) && random.nextInt(3) > 0) {
                    roomGrid[neighborCell] = true;
                    roomQueue.add(neighborCell);
                    addedRoom = true;
                }
            }

            // Mark the current room as a dead end if no room was added from it
            if (!addedRoom) {
                deadEnds.add(currentCell);
            }
        }

        // Retry map generation if not all rooms are connected or if there aren't enough rooms
        if (!allRoomsConnected() || countRooms() < numRooms) {
            //System.out.println("Retrying map generation...");
            roomGrid = new boolean[WIDTH * HEIGHT];
            roomQueue.clear();
            deadEnds.clear();
            generateMap();
        } else {
            //System.out.println("Map generated successfully with " + countRooms() + " rooms.");
            calculateDeadEnds();
            placeSpecialRooms();
        }
    }

    // Calculate dead-end rooms (rooms with only one connection)
    private void calculateDeadEnds() {
        deadEnds.clear(); // Reset the list of dead ends

        for (int i = 0; i < roomGrid.length; i++) {
            if (!roomGrid[i]) continue; // Skip cells without rooms

            int connections = 0;
            for (int direction : new int[]{-WIDTH, WIDTH, -1, 1}) {
                int neighborCell = i + direction;

                // Prevent horizontal wrapping
                if (direction == -1 && i % WIDTH == 0) continue; // Prevent left wrapping
                if (direction == 1 && (i + 1) % WIDTH == 0) continue; // Prevent right wrapping

                if (neighborCell >= 0 && neighborCell < roomGrid.length && roomGrid[neighborCell]) {
                    connections++;
                }
            }

            if (connections == 1) { // Dead end: only one connection
                deadEnds.add(i);
            }
        }
    }

    // Place special rooms (boss room and item rooms)
    private void placeSpecialRooms() {

        // Place the boss room at a deep end room, keep trying until it's placed
        boolean bossRoomPlaced = false;
        while (!bossRoomPlaced && !deadEnds.isEmpty()) {
            int bossRoom = deadEnds.remove(deadEnds.size() - 1); // Get the last dead-end room
            if (bossRoom != START_ROOM-1) { // Ensure the boss room is not the start room
                bossRooms[bossRoom] = true;
                roomGrid[bossRoom] = true;
                bossRoomPlaced = true; // Mark as successfully placed
            }
        }

        // Place item rooms in any empty cell that borders a normal room
        List<Integer> validItemRoomCells = new ArrayList<>();
        for (int i = 0; i < roomGrid.length; i++) {
            if (!roomGrid[i] && i != START_ROOM) { // Ensure the item room is not the start room
                for (int direction : new int[]{-WIDTH, WIDTH, -1, 1}) {
                    int neighborCell = i + direction;
                    if (neighborCell >= 0 && neighborCell < WIDTH * HEIGHT && roomGrid[neighborCell] && !bossRooms[neighborCell]) {
                        validItemRoomCells.add(i);
                        break;
                    }
                }
            }
        }

        int itemRoomsToPlace = 1; // how many item rooms to place
        int itemRoomsPlaced = 0;

        while (!validItemRoomCells.isEmpty() && itemRoomsPlaced < itemRoomsToPlace) {
            int randomIndex = random.nextInt(validItemRoomCells.size());
            int itemRoomCell = validItemRoomCells.remove(randomIndex);
            if (itemRoomCell != START_ROOM - 1) {
                itemRooms[itemRoomCell] = true;
                roomGrid[itemRoomCell] = true;
                itemRoomsPlaced++;
            }
        }
    }

    // Check if a given cell is valid (within bounds and not wrapped around)
    private boolean isValidCell(int cell) {
        if (cell < 0 || cell >= WIDTH * HEIGHT) {
            return false;
        }

        // Ensure no horizontal wrapping:
        if ((cell % WIDTH == 0 && cell - 1 >= 0) || // Moving left from the start of a row
            (cell % WIDTH == WIDTH - 1 && cell + 1 < WIDTH * HEIGHT)) { // Moving right to the next row
            return false;
        }

        return true;
    }

    // Check if a given cell can have a room added to it (based on neighboring rooms)
    private boolean canAddRoom(int cell) {
        int filledNeighbors = 0;
        int currentRow = cell / WIDTH;
        for (int direction : new int[]{-WIDTH, WIDTH, -1, 1}) {
            int neighborCell = cell + direction;

            // Prevent horizontal wrapping
            if (direction == -1 && cell % WIDTH == 0) continue; // Prevent left wrapping
            if (direction == 1 && (cell + 1) % WIDTH == 0) continue; // Prevent right wrapping

            if (neighborCell >= 0 && neighborCell < WIDTH * HEIGHT && roomGrid[neighborCell]) {
                filledNeighbors++;
            }
        }
        return filledNeighbors < 2 && !roomGrid[cell];
    }

    // Count the number of rooms in the grid
    private int countRooms() {
        int count = 0;
        for (boolean isRoom : roomGrid) {
            if (isRoom) {
                count++;
            }
        }
        return count;
    }

    // Check if all rooms are connected
    private boolean allRoomsConnected() {
        boolean[] visited = new boolean[WIDTH * HEIGHT];
        floodFill(START_ROOM, visited);
        for (int i = 0; i < roomGrid.length; i++) {
            if ((roomGrid[i] || bossRooms[i] || itemRooms[i]) && !visited[i]) {
                return false;
            }
        }
        return true;
    }

    // Recursive checking to check connectivity of rooms
    private void floodFill(int cell, boolean[] visited) {
        if (cell < 0 || cell >= WIDTH * HEIGHT || visited[cell] || !roomGrid[cell]) {
            return;
        }
        visited[cell] = true;
        for (int direction : new int[]{-WIDTH, WIDTH, -1, 1}) {
            floodFill(cell + direction, visited);
        }
    }
}
