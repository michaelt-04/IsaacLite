package github.tankgame.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Character;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.Objects;

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

    public Door(Room room, String orientation, String targetRoomPath) {
        //this.room = currentRoom;
        this.orientation = orientation;
        this.targetRoomPath = targetRoomPath;

        //loadSprites();
        this.linkedRoom = room;

        int wallThickness = 40*2;
        int doorOffset = 8*2; // Adjust

        // Set door position based on orientation
        switch (orientation) {
            case "top":
                this.x = (room.getRoomWidth() - width*4) / 2;
                this.y = room.getRoomHeight() - wallThickness - doorOffset*2;
                break;
            case "bottom":
                this.x = (room.getRoomWidth() + width) / 2;
                this.y = wallThickness + (height/4);
                break;
            case "left":
                this.x = wallThickness + (doorOffset/5);
                this.y = (room.getRoomHeight() - height*3) / 2;
                break;
            case "right":
                this.x = room.getRoomWidth() - wallThickness - width - doorOffset/2;
                this.y = (room.getRoomHeight() + height) / 2;
                break;
        }

        // fix door collision
        this.bounds = new Rectangle(this.x, this.y - 10, this.width + 18, this.height);
    }

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
            65*2,
            50*2,
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
                65*2,
                50*2,
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
                65*2,
                50*2,
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
            65*2,
            50*2,
            1,
            1,
            rotation
        );
    }

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

    public void setSpecialType(String specialType) {
        this.specialType = specialType;
    }

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

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isPlayerColliding(Character player) {
        if (locked) {
            return false; // Ignore collisions if the door is locked
        }
        // Check if the player's bounds overlap with the door's bounds
        return bounds.overlaps(player.getBounds());
    }

    public void setLinkedDoor(Door linkedDoor) {
        this.linkedDoor = linkedDoor;
    }

    public Door getLinkedDoor() {
        return linkedDoor;
    }

    public String getTargetRoomPath() {
        return targetRoomPath;
    }

    public void dispose() {
        texture.dispose();
    }

    @Override
    public String toString() {
        return "Door is from: " + this.linkedRoom.getTexturePath() + ", door leads to: " + this.targetRoomPath + ", orientation: " + this.orientation;
    }
}
