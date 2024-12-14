package github.tankgame.environment.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * The Rock class represents a destructible rock block in the game. It includes properties to track
 * the rock's position, size, state (whether it is solid or destroyed), and its collision bounds.
 * The rock can be rendered, destroyed, and checked for collisions.
 */
public class Rock {
    private Texture texture;
    private float x, y;
    private float width, height;
    private boolean isSolid; // Whether the rock is solid
    private boolean isDestroyed; // Whether the rock has been destroyed
    private Rectangle bounds;

    /**
     * Constructor for creating a new rock at the specified position.
     *
     * @param x The x-coordinate of the rock.
     * @param y The y-coordinate of the rock.
     */
    public Rock(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.isSolid = true;
        this.isDestroyed = false;
        this.texture = new Texture("rooms/blocks/rock1.png");


        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Renders the rock if it is not destroyed. The rock's texture is drawn at its position.
     *
     * @param batch The SpriteBatch used for drawing the rock's texture.
     */
    public void render(SpriteBatch batch) {
        if (!isDestroyed) {
            batch.draw(texture, x, y, 60, 60);
        }
    }

    /**
     * Breaks the rock, making it non-solid and marking it as destroyed. The bounds of the rock
     * are set to (0, 0, 0, 0) to indicate it is no longer interactable.
     */
    public void breakRock() {
        if (!isDestroyed) {
            isDestroyed = true;
            isSolid = false;

            bounds.set(0, 0, 0, 0);
        }
    }

    /**
     * Returns the collision bounds of the rock, which is used for checking if the rock intersects
     * with other game objects.
     *
     * @return The bounds of the rock as a Rectangle.
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns whether the rock is solid. Solid rocks cannot be passed through.
     *
     * @return True if the rock is solid, false if it is destroyed.
     */
    public boolean isSolid() {
        return isSolid;
    }

    /**
     * Returns whether the rock is destroyed. Destroyed rocks can no longer be interacted with.
     *
     * @return True if the rock is destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Disposes of the rock's resources, including the texture, when it is no longer needed.
     */
    public void dispose() {
        texture.dispose();
    }
}
