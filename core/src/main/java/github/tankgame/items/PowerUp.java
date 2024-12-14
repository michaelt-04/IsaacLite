package github.tankgame.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import github.tankgame.characters.Character;

// Base class for all powerups (e.g., pills, items)
public abstract class PowerUp {
    protected float x, y; // Position of the power-up
    protected Rectangle bounds; // Collision bounds

    public PowerUp(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, 40, 40);
    }

    // Check if the power-up is picked up by a character
    public boolean isPickedUp(Character character) {
        return character.getBounds().overlaps(this.bounds);
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    // Abstract method to render the power-up
    public abstract void render(SpriteBatch batch);

    public abstract void dispose();

    // Abstract method to apply the effect of the power-up
    public abstract void applyEffect(Character character);
}
