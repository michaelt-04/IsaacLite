package github.tankgame.environment.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Rock {
    private Texture texture;
    private float x, y;
    private float width, height;
    private boolean isSolid; // Whether the rock is solid
    private boolean isDestroyed; // Whether the rock has been destroyed
    private Rectangle bounds;

    public Rock(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.isSolid = true;
        this.isDestroyed = false;
        this.texture = new Texture("rooms/blocks/rock1.png");


        // adjust rock size
        this.bounds = new Rectangle(x, y, width, height);
        //System.out.println(this.bounds);
    }

    public void render(SpriteBatch batch) {
        if (!isDestroyed) {
            batch.draw(texture, x, y, 60, 60);
        }
    }

    public void breakRock() {
        if (!isDestroyed) {
            isDestroyed = true;
            isSolid = false;
            //Play a sound effect

            bounds.set(0, 0, 0, 0);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }


    public boolean isSolid() {
        return isSolid;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void dispose() {
        texture.dispose();
    }
}
