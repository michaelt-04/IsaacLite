package github.tankgame.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import github.tankgame.characters.Character;
import com.badlogic.gdx.math.Rectangle;

import java.util.Objects;

/**
 * The Door class represents a door in the game that connects rooms. It has properties for
 * its position, orientation, and state (locked or unlocked). The door can be rendered with
 * different textures depending on whether it's a regular door, an item door, or a boss door.
 */
public class Door {
    private float x, y;
    private int width = 27, height = 27;
    private String targetRoomPath; // The file path or identifier for the target room
    private Door linkedDoor; // The path back to the previous room
    private String orientation;
    private Texture texture;
    private TextureRegion doorFrame, doorOpen;
    private TextureRegion leftDoor, rightDoor;
    private boolean locked;

    private Room linkedRoom;
    private String specialType;

    private Rectangle bounds;

    /**
     * Constructor to create a door for a given room and orientation, leading to a target room.
     *
     * @param room The room the door is in.
     * @param orientation The orientation of the door (top, bottom, left, right).
     * @param targetRoomPath The path to the room that this door leads to.
     */
    public Door(Room room, String orientation, String targetRoomPath) {
        this.orientation = orientation;
        this.targetRoomPath = targetRoomPath;

        this.linkedRoom = room;

        int wallThickness = 80;
        int doorOffset = 16;

        // Set door position based on orientation
        switch (orientation) {
            case "top":
                this.x = (room.getRoomWidth() - width*4) / 2;
                this.y = room.getRoomHeight() - wallThickness - doorOffset*2;
                this.bounds = new Rectangle(this.x, this.y - 10, this.width + 18, this.height);
                break;
            case "bottom":
                this.x = (room.getRoomWidth() + width) / 2;
                this.y = wallThickness + (height/4);
                this.bounds = new Rectangle(this.x - 10, this.y - 10, this.width + 18, this.height);
                break;
            case "left":
                this.x = wallThickness + (doorOffset/5);
                this.y = ((room.getRoomHeight() - height*3) / 2);
                this.bounds = new Rectangle(this.x, this.y, this.width + 18, this.height);
                break;
            case "right":
                this.x = room.getRoomWidth() - wallThickness - width - doorOffset/2;
                this.y = (room.getRoomHeight() + height) / 2;
                this.bounds = new Rectangle(this.x, this.y - 15, this.width + 18, this.height);
                break;
        }
    }

    /**
     * Returns the opposite orientation for a given door orientation.
     *
     * @param orientation The current orientation (top, bottom, left, right).
     * @return The opposite orientation (bottom, top, right, left).
     */
    public String oppositeOrientation(String orientation) {
        switch (orientation) {
            case "top":
                return "bottom";
            case "bottom":
                return "top";
            case "left":
                return "right";
            case "right":
                return "left";
            default:
                return "";
        }
    }

    /**
     * Loads the textures for the door based on its special type (e.g., boss or item).
     */
    private void loadSprites() {


        if (Objects.equals(this.specialType, "boss")) {
            this.texture = new Texture("rooms/doors/boss_door_texture.png");
        } else if (Objects.equals(this.specialType, "item")) {
            this.texture = new Texture("rooms/doors/item_door_texture.png");
        } else {
            this.texture = new Texture("rooms/doors/door_texture.png");
        }

        this.doorFrame = new TextureRegion(texture, 0, 0, 65, 50);
        this.doorOpen = new TextureRegion(texture, 64, 0, 65, 50);

        Texture leftDoorTexture = new Texture("rooms/doors/left_side.png");
        Texture rightDoorTexture = new Texture("rooms/doors/right_side.png");

        this.leftDoor = new TextureRegion(leftDoorTexture);
        this.rightDoor = new TextureRegion(rightDoorTexture);
    }

    /**
     * Renders the door with the appropriate textures and state (open, locked, etc.).
     *
     * @param batch The SpriteBatch used for rendering the door.
     */
    public void render(SpriteBatch batch) {
        loadSprites();

        float originX = (65 / 2f);
        float originY = (50 / 2f);
        float rotation = getRotationAngle();

        // Draw door open texture
        batch.draw(
            doorOpen,
            this.x - (width/2f),
            this.y - (height/2f),
            originX,
            originY,
            130,
            100,
            1,
            1,
            rotation
        );

        if(this.locked){

            int leftOffsetX = 0;
            int leftOffsetY = 0;

            int rightOffsetX = 0;
            int rightOffsetY = 0;

            // Use a switch expression for rotation angle
            switch ((int) rotation) {
                case 0 -> {
                    // Top orientation
                    rightOffsetX = 42;
                    leftOffsetX = -40;
                }
                case 90 -> {
                    // Left orientation
                    rightOffsetY = 41;
                    leftOffsetY = -40;
                }
                case 180 -> {
                    // Bottom orientation
                    rightOffsetX = -41;
                    leftOffsetX = 40;
                }
                case 270 -> {
                    // Right orientation
                    rightOffsetY = -42;
                    leftOffsetY = 40;
                }
            }

            batch.draw(
                leftDoor,
                this.x - (width/2f) + leftOffsetX,
                this.y - (height/2f) + leftOffsetY,
                originX,
                originY,
                130,
                100,
                1,
                1,
                rotation
            );
            batch.draw(
                rightDoor,
                this.x - (width/2f) + rightOffsetX,
                this.y - (height/2f) + rightOffsetY,
                originX,
                originY,
                130,
                100,
                1,
                1,
                rotation
            );
        }

        // Draw door frame
        batch.draw(
            doorFrame,
            this.x - (width/2f),
            this.y - (height/2f),
            originX,
            originY,
            130,
            100,
            1,
            1,
            rotation
        );
    }

    /**
     * Returns the rotation angle for the door based on its orientation.
     *
     * @return The rotation angle for the door (0, 180, 90, or 270 degrees).
     */
    private float getRotationAngle() {
        switch (orientation) {
            case "top":
                return 0; // No rotation for top
            case "bottom":
                return 180; // Rotate 180 degrees for bottom
            case "left":
                return 90; // Rotate 90 degrees for left
            case "right":
                return 270; // Rotate 270 degrees for right
            default:
                return 0; // Default to no rotation
        }
    }

    /**
     * Sets the special type for the door (e.g., "item", "boss").
     *
     * @param specialType The special type of the door.
     */
    public void setSpecialType(String specialType) {
        this.specialType = specialType;
    }

    // Getter and setter methods for door properties
    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Checks if the player is colliding with the door.
     *
     * @param player The player character to check for collision.
     * @return True if the player is colliding with the door and the door is not locked.
     */
    public boolean isPlayerColliding(Character player) {
        if (locked) {
            return false; // Ignore collisions if the door is locked
        }
        // Check if the player's bounds overlap with the door's bounds
        return bounds.overlaps(player.getBounds());
    }

    public String getTargetRoomPath() {
        return targetRoomPath;
    }

    /**
     * Disposes of the door's textures to free up resources when no longer needed.
     */
    public void dispose() {
        texture.dispose();
    }

    @Override
    public String toString() {
        return "Door is from: " + this.linkedRoom.getTexturePath() + ", door leads to: " + this.targetRoomPath + ", orientation: " + this.orientation;
    }
}
