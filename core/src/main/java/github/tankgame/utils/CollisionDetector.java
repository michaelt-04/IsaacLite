package github.tankgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import github.tankgame.characters.Character;
import github.tankgame.environment.blocks.Rock;
import github.tankgame.environment.Room;
import github.tankgame.environment.blocks.Wall;
import github.tankgame.projectiles.Projectile;

import java.util.Objects;

public class CollisionDetector {
    private Room room;

    public CollisionDetector(Room room) {
        this.room = room;
    }

    // Combined collision check for tiles and rocks
    public boolean checkTileCollision(Rectangle bounds) {
        for (Rectangle solidTile : room.getSolidTiles()) {
            if (solidTile.overlaps(bounds)) {
                //System.out.println("solidTile: " + solidTile);
                return true; // Collision with tile
            }
        }

        for (Rock rock : room.getRocks()) {
            if (rock.isSolid() && rock.getBounds().overlaps(bounds)) {
                return true; // Collision with rock
            }
        }

        for(Wall wall : room.getWalls()){
            if(wall.getBounds().overlaps(bounds)){
                return true; // Collision with wall
            }
        }
        return false;
    }



    /**
     * Check for character collisions with tiles and rocks during movement.
     */
    public float[] checkCharacterTileCollision(Character character, float deltaX, float deltaY) {
        float newX = character.getPositionX() + deltaX;
        float newY = character.getPositionY() + deltaY;

        // Create a temporary bounds rectangle for the character
        Rectangle futureBoundsX = new Rectangle(newX, character.getPositionY(), character.getHitBoxWidth(), character.getHitBoxHeight());
        //System.out.println("futureBoundsX: " + futureBoundsX);
        Rectangle futureBoundsY = new Rectangle(character.getPositionX(), newY, character.getHitBoxWidth(), character.getHitBoxHeight());

        // Check for X-axis collisions
        if (checkTileCollision(futureBoundsX)) {
            newX = character.getPositionX();

        }

        // Check for Y-axis collisions
        if (checkTileCollision(futureBoundsY)) {
            newY = character.getPositionY();
        }

        return new float[]{newX, newY};
    }

    /**
     * Check if a projectile's bounds intersect with any solid tile or rock.
     */
    public boolean checkProjectileTileCollision(Projectile projectile) {
        Rectangle projectileBounds = new Rectangle(
            projectile.getPositionX() + projectile.getDeltaX() + 25,
            projectile.getPositionY() + projectile.getDeltaY() + 25,
            projectile.getProjWidth(),
            projectile.getProjHeight()
        );

        if(Objects.equals(projectile.getState(), "COLLIDING")){
            projectileBounds.set(0,0,0,0);
        }


        // Check for collisions with tiles or rocks
        return checkTileCollision(projectileBounds);
    }

    /**
     * Check if two characters' bounds intersect.
     */
    public boolean checkCharCollisionWithChar(Character character1, Character character2) {
        return character1.getBounds().overlaps(character2.getBounds());
    }

    /**
     * Check if a character's bounds intersect with a projectile's bounds.
     */
    public boolean checkCharCollisionWithProj(Character character, Projectile projectile) {
        Rectangle projectileBounds = new Rectangle(
            projectile.getPositionX(),
            projectile.getPositionY(),
            projectile.getProjWidth(),
            projectile.getProjHeight()
        );

        if(Objects.equals(projectile.getState(), "COLLIDING")){
            projectileBounds.set(0,0,0,0);
        }

        return character.getBounds().overlaps(projectileBounds);
    }

    public Room getRoom() {
        return this.room;
    }
}
