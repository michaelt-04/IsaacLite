package github.tankgame.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Monster;
import github.tankgame.characters.Player;
import github.tankgame.environment.Door;
import github.tankgame.environment.Room;
import github.tankgame.items.Bomb;
import github.tankgame.projectiles.Projectile;
import github.tankgame.utils.CollisionDetector;
import github.tankgame.utils.InputHandler;

import java.util.ArrayList;
import java.util.Objects;

// Manages game state, transitions, and score
public class GameManager {
    private RoomManager roomManager;
    private Array<Room> allRooms;
    private CollisionDetector collisionDetector;
    private Player player;
    private Room currentRoom;
    private InputHandler inputHandler;

    public GameManager(Player player, RoomManager roomManager) {
        this.player = player;
        this.roomManager = roomManager;

        inputHandler = new InputHandler();

        // Set initial room
        Room startRoom = roomManager.getStartRoom();
        allRooms = roomManager.getAllRooms();
        this.player.setRoom(startRoom);
        this.currentRoom = player.getRoom();

        // Initialize collision detection for the starting room
        this.collisionDetector = new CollisionDetector(startRoom);
    }

    public void updateGame(float delta) {

        if (player.isDead()) {
            player.playDeathAnimation();
            return;
        }

        // Handle bomb placement
        if (inputHandler.isBombKeyPressed()) {
            player.placeBomb(this.currentRoom.getRocks());
        }

        player.update(delta, this.currentRoom.getRocks());

        currentRoom.checkPillPickup(player);

        if(currentRoom.isItemRoom()){
            currentRoom.checkItemPickup(player);
        }

        player.updateInvulnerability(delta);

        // Get movement deltas
        float[] movementDeltas = inputHandler.getMovementDeltas();
        float movementDeltaX = movementDeltas[0];
        float movementDeltaY = movementDeltas[1];

        // Scale movement by delta time to maintain frame-rate independence
        float scaledMovementDeltaX = movementDeltaX * player.getSpeed() * delta;
        float scaledMovementDeltaY = movementDeltaY * player.getSpeed() * delta;

        float[] collisionMove = this.collisionDetector.checkCharacterTileCollision(player, scaledMovementDeltaX, scaledMovementDeltaY);

        float newX = collisionMove[0];
        float newY = collisionMove[1];

        player.move(Gdx.graphics.getDeltaTime(), newX, newY, movementDeltaX, movementDeltaY);
        //player.move(Gdx.graphics.getDeltaTime(), newX - player.getPositionX(), newY - player.getPositionY(), player.getSpeed());
        for (Monster monster : this.currentRoom.getMonsters()) {
            monster.move(player.getPositionX(), player.getPositionY(), Gdx.graphics.getDeltaTime());
            monster.updateProjectiles(delta);
        }

        if(currentRoom.isBossRoom() && currentRoom.getBoss() != null){
            currentRoom.getBoss().move(player.getPositionX(), player.getPositionY(), Gdx.graphics.getDeltaTime());
        }

        float[] shootDeltas = inputHandler.getShootDeltas();
        float shootDeltaX = shootDeltas[0];
        float shootDeltaY = shootDeltas[1];
        player.shoot(shootDeltaX, shootDeltaY);

        player.updateProjectiles(delta);

        // Check for monster-projectile collisions
        for (Monster monster : this.currentRoom.getMonsters()) {

            if (collisionDetector.checkCharCollisionWithChar(player, monster)) {
                // adjust rectangle size, too far to the right of monster or player
                player.takeDamage(monster.getDamage());
            }

            for (Projectile projectile : player.getProjectiles()) {
                if (projectile.isCollided()) {

                } else if (collisionDetector.checkCharCollisionWithProj(monster, projectile)) {
                    // Handle collision between monster and projectile
                    monster.takeDamage(projectile.getDamage());
                    //System.out.println(monster.getHealth());
                    projectile.onCollision();
                }
            }

            for(Projectile projectile : monster.getProjectiles()) {
                if (projectile.isCollided()) {

                }else if(collisionDetector.checkCharCollisionWithProj(player, projectile)) {
                    player.takeDamage(projectile.getDamage());
                    projectile.onCollision();
                }
            }
        }

        if(currentRoom.isBossRoom() && currentRoom.getBoss() != null){
            if(collisionDetector.checkCharCollisionWithChar(player, currentRoom.getBoss())) {
                player.takeDamage(currentRoom.getBoss().getDamage());
            }

            for (Projectile projectile : player.getProjectiles()) {
                if (projectile.isCollided()) {

                } else if (collisionDetector.checkCharCollisionWithProj(currentRoom.getBoss(), projectile)) {
                    // Handle collision between monster and projectile
                    currentRoom.getBoss().takeDamage(projectile.getDamage());
                    //System.out.println(monster.getHealth());
                    projectile.onCollision();
                }
            }
        }

        // After updating player and monsters
        for (Bomb bomb : player.getBombs()) { // Assuming Player has a getter for bombs
            if (bomb.isHasExploded()) {
                // Check collision with the player
                if (bomb.getBounds().overlaps(player.getBounds())) {
                    player.takeDamage(bomb.getDamage());
                }

                // Check collision with monsters
                for (Monster monster : currentRoom.getMonsters()) {
                    if (bomb.getBounds().overlaps(monster.getBounds())) {
                        monster.takeDamage(bomb.getDamage());
                    }
                }
            }
        }

    }

    public Room handleRoomTransition() {
        for (Door door : currentRoom.getDoors()) {
            if (door.isPlayerColliding(player)) {
                // Transition to the new room
                return transitionToRoom(door.getTargetRoomPath(), door.getOrientation(), currentRoom.getTexturePath(), door);
            }
        }
        return currentRoom;
    }

    private Room transitionToRoom(String roomPath, String enteringDoorOrientation, String prevPath, Door door) {

        System.out.println("Previous Room: " + prevPath);

        Room newRoom = null;
        for (Room r : allRooms) {
            if (r.getTexturePath().equals(roomPath)) {
                newRoom = r;
                System.out.println("new Room: " + newRoom.getTexturePath());
                break;
            }
        }

        // Find the corresponding door in the new room
        Door correspondingDoor = null;
        for (Door newRoomDoor : newRoom.getDoors()) {
            if (newRoomDoor.getTargetRoomPath().equals(prevPath) &&
                newRoomDoor.getOrientation().equals(door.oppositeOrientation(enteringDoorOrientation))) {
                correspondingDoor = newRoomDoor;
                break;
            }
        }

        // modify item room doors here?? if no corresponding door, make one and delete the other!

        // Place the player near the corresponding door
        if (correspondingDoor != null) {
            switch (correspondingDoor.getOrientation()) {
                case "top":
                    player.setPosition(player.getPositionX(), correspondingDoor.getY() - player.getHeight() - 30);
                    break;
                case "bottom":
                    player.setPosition(player.getPositionX(), correspondingDoor.getY() + correspondingDoor.getHeight() + 30);
                    break;
                case "left":
                    player.setPosition(correspondingDoor.getX() + correspondingDoor.getWidth() + 30, player.getPositionY());
                    break;
                case "right":
                    player.setPosition(correspondingDoor.getX() - player.getWidth() - 25, player.getPositionY());
                    break;
                default:
                    System.err.println("Unexpected door orientation: " + correspondingDoor.getOrientation());
                    break;
            }
        }else{

            //System.out.println("NEW DOOR ADDED BC THERE WASNT A CORRESPONDING DOORs");

            // for testing
            for(int i = newRoom.getDoors().size-1; i >= 0; i--) {
                newRoom.getDoors().removeIndex(0);
            }
            Door newRoomDoor = new Door(currentRoom, door.oppositeOrientation(door.getOrientation()), prevPath);
            newRoom.addDoor(newRoomDoor);

            roomManager.setSpecialDoors(newRoom, "item");

            switch (newRoomDoor.getOrientation()) {
                case "top":
                    player.setPosition(player.getPositionX(), newRoomDoor.getY() - player.getHeight() - 30);
                    break;
                case "bottom":
                    player.setPosition(player.getPositionX(), newRoomDoor.getY() + newRoomDoor.getHeight() + 30);
                    break;
                case "left":
                    player.setPosition(newRoomDoor.getX() + newRoomDoor.getWidth() - 25, player.getPositionY());
                    break;
                case "right":
                    player.setPosition(newRoomDoor.getX() - player.getWidth() - 25, player.getPositionY());
                    break;
                default:
                    System.err.println("Unexpected door orientation: " + newRoomDoor.getOrientation());
                    break;
            }

            if(newRoomDoor == null){
                player.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 4);
            }

        }


        // Update references
        player.disposeProjectiles();
        player.disposeBombs();
        player.setRoom(newRoom);
        collisionDetector = new CollisionDetector(newRoom);
        this.currentRoom = newRoom;
        return newRoom;
    }
}

