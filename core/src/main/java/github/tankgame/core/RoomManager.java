package github.tankgame.core;

import github.tankgame.environment.Door;
import github.tankgame.environment.MapGenerator;
import github.tankgame.environment.Room;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

// Handles room generation and transitions
public class RoomManager {
    private Array<Room> allRooms = new Array<>();
    private Room startRoom;
    private int startRoomIndex;
    private boolean[] roomGrid;
    private boolean[] itemRooms;  // Array to track item rooms
    private boolean[] bossRooms;  // Array to track boss rooms
    private boolean[] hasDoor;

    public RoomManager(MapGenerator mapGenerator) {
        hasDoor = new boolean[mapGenerator.getRoomGrid().length];
        initializeRooms(mapGenerator);
    }

    private void initializeRooms(MapGenerator mapGenerator) {
        roomGrid = mapGenerator.getRoomGrid();
        itemRooms = mapGenerator.getItemRooms();
        bossRooms = mapGenerator.getBossRooms();

        boolean firstRoomCreated = false;

        for (int i = 0; i < roomGrid.length; i++) {
            if (roomGrid[i]) {
                String roomPath = firstRoomCreated ? "rooms/room_" + i + ".png" : "rooms/start_room.png";

                boolean isItemRoom = itemRooms[i];
                boolean isBossRoom = bossRooms[i];
                Room room = new Room(roomPath, roomGrid, i, isItemRoom, isBossRoom);

                addDoorsToRoom(room, i);
                allRooms.add(room);

                if (!firstRoomCreated) {
                    firstRoomCreated = true;
                    startRoom = room;
                    startRoomIndex = room.getRoomIndex();

                    if (itemRooms[startRoomIndex] || bossRooms[startRoomIndex]) {
                        bossRooms[startRoomIndex] = false;
                        itemRooms[startRoomIndex] = false; // Unmark as item room
                        room.setItemRoom(false); // Update the room to not be an item room

                        // Re-add an item room to another valid, non-start room
                        addItemRoom();
                    }
                }

                // Set special doors for item rooms and boss rooms
                if (itemRooms[i]) {
                    setSpecialDoors(room, "item");

                    while (room.getDoors().size > 1) {
                        room.getDoors().removeIndex(0);
                    }
                } else if (bossRooms[i]) {
                    setSpecialDoors(room, "boss");
                }

                // Mark the current room as having a door if it's an item or boss room
                if (isItemRoom || isBossRoom) {
                    hasDoor[i] = true;
                }
            }
        }
    }

    private void addItemRoom() {
        // Place item rooms in any empty cell that borders a normal room
        List<Integer> validItemRoomCells = new ArrayList<>();
        for (int i = 0; i < roomGrid.length; i++) {
            // Check if the cell is empty (no room) and if it's adjacent to a room
            if (!roomGrid[i] && i != startRoomIndex) {
                for (int direction : new int[]{-5, 5, -1, 1}) {
                    int neighborCell = i + direction;
                    if (neighborCell >= 0 && neighborCell < 5 * 4 && roomGrid[neighborCell] && !bossRooms[neighborCell]) {
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

            itemRooms[itemRoomCell] = true;
            roomGrid[itemRoomCell] = true;
            itemRoomsPlaced++;

        }
    }


    private void addDoorsToRoom(Room room, int roomIndex) {
        int[] directions = {-5, 5, -1, 1};
        String[] orientations = {"top", "bottom", "left", "right"};

        for (int i = 0; i < directions.length; i++) {
            int neighborIndex = roomIndex + directions[i];

            // Check if the neighboring index is valid and part of the room grid
            if (neighborIndex >= 0 && neighborIndex < roomGrid.length && roomGrid[neighborIndex]) {
                // Determine if the neighboring room is an item room or a boss room
                boolean isNextItemRoom = itemRooms[neighborIndex];
                boolean isNextBossRoom = bossRooms[neighborIndex];

                // Skip adding a door if the neighboring room is an item or boss room and already has a door
                if ((isNextItemRoom || isNextBossRoom) && hasDoor[neighborIndex]) {
                    continue;
                }

                String neighborRoomPath = (neighborIndex == startRoomIndex) ? "rooms/start_room.png" : "rooms/room_" + neighborIndex + ".png";

                // Create a door connecting the current room to the neighbor
                Door door = new Door(room, orientations[i], neighborRoomPath);

                // Set special door properties based on the type of neighboring room
                if (isNextBossRoom) {
                    door.setSpecialType("boss");
                } else if (isNextItemRoom) {
                    door.setSpecialType("item");
                }

                room.addDoor(door);

                // Mark the neighbor room as having a door if it's an item or boss room
                if (isNextItemRoom || isNextBossRoom) {
                    hasDoor[neighborIndex] = true;
                }
            }
        }
    }

    public void setSpecialDoors(Room room, String specialType) {
        Array<Door> doors = room.getDoors();
        for (Door door : doors) {
            door.setSpecialType(specialType);
        }
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Array<Room> getAllRooms() {
        return this.allRooms;
    }

}
