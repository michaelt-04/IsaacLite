package github.tankgame.core;

import github.tankgame.environment.Door;
import github.tankgame.environment.MapGenerator;
import github.tankgame.environment.Room;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

                // FOR TESTING ROOM LAYOUTS
                //String roomPath = firstRoomCreated ? "rooms/room_" + i + ".png" : "rooms/room_5.png";

                boolean isItemRoom = itemRooms[i];
                boolean isBossRoom = bossRooms[i];
                Room room = new Room(roomPath, roomGrid, i, isItemRoom, isBossRoom);

                addDoorsToRoom(room, i);
                allRooms.add(room);

                if (!firstRoomCreated) {
                    firstRoomCreated = true;
                    startRoom = room;
                    startRoomIndex = room.getRoomIndex();
                    System.out.println("startRoomIndex: " + startRoomIndex);

                    if (itemRooms[startRoomIndex]) {
                        itemRooms[startRoomIndex] = false; // Unmark as item room
                        room.setItemRoom(false); // Update the room to not be an item room

                        // Re-add an item room to another valid, non-start room
                        addItemRoom();

                    }
                }

                // Set special doors for item rooms and boss rooms
                if (itemRooms[i]) {
                    setSpecialDoors(room, "item");

                    // DOUBLE CHECK THIS, SOMETIMES IT REMOVES THE WRONG DOOR, IT SHOULD ONLY REMOVE THE ONE THAT DOESNT
                    // HAVE ANOTHER DOOR
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

        // Print the room grid for debugging
        System.out.println("Room grid layout (S = Start room, C = Connected room, I = Item room, B = Boss room, . = Empty space):");
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                int index = y * 5 + x;
                if (index == startRoomIndex) {
                    System.out.print("S "); // Mark the starting room
                } else if (itemRooms[index]) {
                    System.out.print("I "); // Mark item rooms
                } else if (bossRooms[index]) {
                    System.out.print("B "); // Mark boss rooms
                } else if (roomGrid[index]) {
                    System.out.print("R "); // Regular room
                } else {
                    System.out.print(". "); // Empty space
                }
            }
            System.out.println();
        }

        /*System.out.println("Rooms and their doors:");
        for (int i = 0; i < allRooms.size; i++) {
            Room room = allRooms.get(i);
            System.out.println("Room " + room.getRoomIndex() + ": " + room.getTexturePath());
            System.out.println("isItemRoom: " + room.isItemRoom() + " isBossRoom: " + room.isBossRoom());

            System.out.println("Tile width: " + room.getTileWidth() + " Tile height: " + room.getTileHeight());

            Array<Door> doors = room.getDoors();
            if (doors.isEmpty()) {
                System.out.println("  No doors.");
            } else {
                System.out.println("  Doors:");
                for (int j = 0; j < doors.size; j++) {
                    Door door = doors.get(j);
                    System.out.println("    Door " + j + ": leads to room index " + door.getTargetRoomPath());
                }
            }
        }*/
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

            //System.out.println("Placing item room again...");
            itemRooms[itemRoomCell] = true;
            roomGrid[itemRoomCell] = true;
            itemRoomsPlaced++;

            System.out.println("itemRoom location: " + itemRoomCell);
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
