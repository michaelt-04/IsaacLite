package github.tankgame.environment.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    private Texture texture;
    private float x, y;
    private float width, height;
    private Rectangle bounds;

    public Wall(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.texture = new Texture("rooms/blocks/wall.png");

        // Initialize the wall's collision bounds
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, 60, 60);
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public boolean isSolid() {
        return true;
    }

    public void dispose() {
        texture.dispose();
    }
}
