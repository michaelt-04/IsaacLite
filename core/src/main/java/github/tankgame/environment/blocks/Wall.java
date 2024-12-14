package github.tankgame.environment.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * The Wall class represents a static, solid wall block in the game. The wall has properties to
 * track its position, size, and collision bounds. The wall is always solid and non-destructible.
 */
public class Wall {
    private Texture texture;
    private float x, y;
    private float width, height;
    private Rectangle bounds;

    /**
     * Constructor for creating a new wall at the specified position.
     *
     * @param x The x-coordinate of the wall.
     * @param y The y-coordinate of the wall.
     */
    public Wall(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.texture = new Texture("rooms/blocks/wall.png");

        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Renders the wall by drawing its texture at its position.
     *
     * @param batch The SpriteBatch used for drawing the wall's texture.
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, 60, 60);
    }

    /**
     * Returns the collision bounds of the wall, which is used for checking if the wall intersects
     * with other game objects.
     *
     * @return The bounds of the wall as a Rectangle.
     */
    public Rectangle getBounds() {
        return this.bounds;
    }

    /**
     * Returns whether the wall is solid. Solid walls cannot be passed through.
     *
     * @return Always returns true, as walls are always solid.
     */
    public boolean isSolid() {
        return true;
    }

    /**
     * Disposes of the wall's resources, including the texture, when it is no longer needed.
     */
    public void dispose() {
        texture.dispose();
    }
}
