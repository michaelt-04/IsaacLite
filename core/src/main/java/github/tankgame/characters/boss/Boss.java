package github.tankgame.characters.boss;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import github.tankgame.characters.Monster;

// Base class for bosses with shared properties
public class Boss extends Monster {

    // Boss-specific properties
    private float maxHealth;

    /**
     * Initializes the boss with position, size, and health.
     *
     * @param x      The x-coordinate of the boss's position.
     * @param y      The y-coordinate of the boss's position.
     * @param width  The width of the boss.
     * @param height The height of the boss.
     */
    public Boss(float x, float y, int width, int height) {
        super(x, y, width, height);

        // Set up health values
        this.maxHealth = 55;
        this.health = maxHealth;
    }

    /**
     * Handles how the boss moves relative to the player's position.
     *
     * @param playerX   The x-coordinate of the player's position.
     * @param playerY   The y-coordinate of the player's position.
     * @param deltaTime The time elapsed since the last frame, used for smooth movement.
     */
    @Override
    public void move(float playerX, float playerY, float deltaTime) {
    }

    /**
     * Draws the boss on the screen.
     *
     * @param batch The SpriteBatch used to render the boss.
     * @param scale The scale factor for rendering the boss.
     */
    @Override
    public void render(SpriteBatch batch, float scale) {
    }

    /**
     * Reduces the boss's health when it takes damage.
     *
     * @param damage The amount of damage to inflict on the boss.
     */
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
    }

    /**
     * Disposes of resources used by the boss.
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
