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


    public Character(float startX, float startY, int width, int height) {
        this.positionX = startX;
        this.positionY = startY;
        this.width = width;
        this.height = height;
        this.hitBoxWidth = width-10;
        this.hitBoxHeight = height-10;
        this.projectiles = new Array<>();

        this.bounds = new Rectangle(this.positionX, this.positionY, this.hitBoxWidth, this.hitBoxHeight);
    }

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

    public void shoot(float deltaX, float deltaY) {
        isShooting = (deltaX != 0 || deltaY != 0);
    }

    public void takeDamage(float damage) {
        this.health -= damage;
        System.out.println(this.health);
        if (this.health <= 0) {
            this.health = 0;
            this.isDead = true;
        }
    }

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

    public void renderProjectiles(Batch batch) {
        for (Projectile projectile : this.projectiles) {
            projectile.draw(batch);
        }
    }

    public boolean isDead() {
        return this.isDead;
    }

    public void playDeathAnimation() {
    }

    // Method to update the bounds
    protected void updateBounds() {
        this.bounds.set(this.positionX, this.positionY, this.hitBoxWidth, this.hitBoxHeight);
    }

    // Getter for the bounding rectangle
    public Rectangle getBounds() {
        return this.bounds;
    }


    public float getHealth() {
        return this.health;
    }

    public void increaseHealth(float value) {
        System.out.println("Health increased by: " + value);

        this.maxHealth += value;
        this.health += value;
        System.out.println(this.health + "/" + this.maxHealth);
    }

    public void increaseSpeed(float value) {
        System.out.println("Speed increased by: " + value*33);
        this.speed += value*33;
        System.out.println(this.speed);
    }

    public void increaseTears(float value){}

    public void increaseDamage(float value) {}

    public void increaseBombs(float value) {}

    public void setHealth(float health) {
        this.health = health;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
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

    public abstract void render(SpriteBatch batch, float scale);

    public abstract void dispose();

    public abstract void disposeProjectiles();

    public void setAppearance(Texture appearance, String currentCostume) {
    }

    public void setHasTripleShot(boolean hasTripleShot) {}

}
