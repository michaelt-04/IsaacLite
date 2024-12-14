package github.tankgame.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import github.tankgame.environment.Room;
import github.tankgame.projectiles.Projectile;
import github.tankgame.utils.CollisionDetector;

import java.util.Iterator;

// Base class for characters with shared properties (position, speed, etc.)
public abstract class Character {
    protected float positionX, positionY;
    protected float speed; // Speed of movement
    protected boolean isMoving;
    protected boolean isShooting;
    protected float stateTime = 0f; // Time elapsed for animation
    protected int width, height;
    protected int hitBoxWidth, hitBoxHeight;
    protected Room room;
    protected float health;
    protected float maxHealth;
    protected boolean isDead;
    protected Rectangle bounds;

    protected Array<Projectile> projectiles;
    protected CollisionDetector collisionDetector;
    protected float shootCooldown;

    /**
     * Initializes a Character with a starting position and size.
     *
     * @param startX Starting x-coordinate of the character.
     * @param startY Starting y-coordinate of the character.
     * @param width  Width of the character.
     * @param height Height of the character.
     */
    public Character(float startX, float startY, int width, int height) {
        this.positionX = startX;
        this.positionY = startY;
        this.width = width;
        this.height = height;
        this.hitBoxWidth = width - 10;
        this.hitBoxHeight = height - 10;
        this.projectiles = new Array<>();

        this.bounds = new Rectangle(this.positionX, this.positionY, this.hitBoxWidth, this.hitBoxHeight);
    }

    /**
     * Moves the character and updates its position and state.
     *
     * @param delta  Time elapsed since the last frame.
     * @param newX   The new x-coordinate for the character.
     * @param newY   The new y-coordinate for the character.
     * @param deltaX Change in x-coordinate.
     * @param deltaY Change in y-coordinate.
     */
    public void move(float delta, float newX, float newY, float deltaX, float deltaY) {
        this.isMoving = (deltaX != 0 || deltaY != 0);

        if (isMoving) {

            this.setPosition(newX, newY);
            this.updateBounds();

            stateTime += delta;  // Update animation time
        } else {
            stateTime = 0f;  // Reset animation time when not moving
        }
    }

    /**
     * Handles shooting logic for the character.
     *
     * @param deltaX Change in x-coordinate for projectile direction.
     * @param deltaY Change in y-coordinate for projectile direction.
     */
    public void shoot(float deltaX, float deltaY) {
        isShooting = (deltaX != 0 || deltaY != 0);
    }

    /**
     * Reduces the character's health by a specified amount and checks if it is dead.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            this.isDead = true;
        }
    }

    /**
     * Updates all active projectiles, checking for collisions and removing inactive projectiles.
     *
     * @param delta Time elapsed since the last frame.
     */
    public void updateProjectiles(float delta) {

        this.shootCooldown -= delta;
        this.collisionDetector = new CollisionDetector(this.room);

        for (Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext(); ) {
            Projectile projectile = iterator.next();

            projectile.update(delta);

            // Check if the projectile collides with an obstacle or goes out of bounds
            if (this.collisionDetector.checkProjectileTileCollision(projectile)) {
                projectile.onCollision();
            }

            // Remove the projectile if it has completed the collision animation
            if (projectile.isCollided()) {
                projectile.dispose();
                iterator.remove();
            }
        }
    }

    /**
     * Renders all active projectiles.
     *
     * @param batch The batch used to draw the projectiles.
     */
    public void renderProjectiles(Batch batch) {
        for (Projectile projectile : this.projectiles) {
            projectile.draw(batch);
        }
    }

    /**
     * Checks if the character is dead.
     *
     * @return True if the character is dead, otherwise false.
     */
    public boolean isDead() {
        return this.isDead;
    }

    /**
     * Plays the character's death animation (if applicable).
     */
    public void playDeathAnimation() {
    }

    /**
     * Updates the character's bounding box to match its current position.
     */
    protected void updateBounds() {
        this.bounds.set(this.positionX, this.positionY, this.hitBoxWidth, this.hitBoxHeight);
    }

    /**
     * Gets the bounding rectangle of the character.
     *
     * @return The bounding rectangle.
     */
    public Rectangle getBounds() {
        return this.bounds;
    }

    /**
     * Gets the current health of the character.
     *
     * @return The current health value.
     */
    public float getHealth() {
        return this.health;
    }

    /**
     * Increases the character's maximum health and restores the same amount to current health.
     *
     * @param value The amount to increase health by.
     */
    public void increaseHealth(float value) {
        this.maxHealth += value;
        this.health += value;
    }

    /**
     * Increases the character's speed.
     *
     * @param value The amount to increase speed by.
     */
    public void increaseSpeed(float value) {
        this.speed += value * 33;
    }

    /**
     * Increases the character's firing rate.
     *
     * @param value The amount to increase.
     */
    public void increaseTears(float value) {
    }

    /**
     * Increases the character's damage.
     *
     * @param value The amount to increase damage by.
     */
    public void increaseDamage(float value) {
    }

    /**
     * Increases the character's bomb count.
     *
     * @param value The amount to increase bombs by.
     */
    public void increaseBombs(float value) {
    }

    public float getSpeed() {
        return this.speed;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public float getPositionX() {
        return this.positionX;
    }

    public float getPositionY() {
        return this.positionY;
    }

    /**
     * Sets the character's position.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public void setPosition(float x, float y) {
        positionX = x;
        positionY = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getHitBoxWidth() {
        return this.hitBoxWidth;
    }

    public int getHitBoxHeight() {
        return this.hitBoxHeight;
    }

    /**
     * Renders the character. Must be implemented by subclasses.
     *
     * @param batch The SpriteBatch used for rendering.
     * @param scale The scale factor for rendering the character.
     */
    public abstract void render(SpriteBatch batch, float scale);

    public abstract void dispose();

    public abstract void disposeProjectiles();

    /**
     * Sets the character's appearance using a texture
     */
    public void setAppearance(Texture appearance, String currentCostume) {
    }

    public void setHasTripleShot(boolean hasTripleShot) {
    }

}
