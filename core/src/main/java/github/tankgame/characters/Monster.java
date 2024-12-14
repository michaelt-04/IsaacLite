package github.tankgame.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.projectiles.Projectile;

/**
 * Abstract class representing a monster. Extends Character with monster-specific behavior.
 */
public abstract class Monster extends Character {
    protected Texture deathTexture;
    protected Animation<TextureRegion> deathAnim;
    protected float damage;

    protected float deathTime;

    /**
     * Monster instance with specified position and size.
     *
     * @param positionX Starting x-coordinate.
     * @param positionY Starting y-coordinate.
     * @param width     Width of the monster.
     * @param height    Height of the monster.
     */
    public Monster(float positionX, float positionY, int width, int height) {
        super(positionX, positionY, width, height);
        this.projectiles = new Array<>();
        this.shootCooldown = 0;
        deathTexture = new Texture(Gdx.files.internal("projectiles/monster_death_sheet.png"));
        loadSprites();
    }

    /**
     * Loads the death animation from the sprite sheet.
     */
    private void loadSprites() {
        deathAnim = createAnimation(deathTexture, 0, 0, 69, 69, 4, 0.04f, 63);
    }

    /**
     * Creates an animation from a sprite sheet.
     *
     * @param texture       The sprite sheet texture.
     * @param startX        Starting x-coordinate of the first frame.
     * @param startY        Starting y-coordinate of the first frame.
     * @param frameWidth    Width of a single frame.
     * @param frameHeight   Height of a single frame.
     * @param frameCount    Total number of frames.
     * @param frameDuration Duration of each frame in seconds.
     * @param frameJump     Vertical spacing between rows in the sheet.
     * @return The animation object.
     */
    private Animation<TextureRegion> createAnimation(Texture texture, int startX, int startY, int frameWidth, int frameHeight, int frameCount, float frameDuration, int frameJump) {
        Array<TextureRegion> frames = new Array<>();

        for (int row = 0; row < frameCount - 1; row++) {
            for (int col = 0; col < frameCount; col++) {
                frames.add(new TextureRegion(texture, startX + col * 65, startY, frameWidth, frameHeight));
            }
            startY += 65;
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Moves the monster based on player's position and elapsed time.
     *
     * @param playerX   Player's x-coordinate.
     * @param playerY   Player's y-coordinate.
     * @param deltaTime Time elapsed since last frame.
     */
    public void move(float playerX, float playerY, float deltaTime) {
    }

    /**
     * Handles damage taken by the monster.
     *
     * @param damage The amount of damage to apply.
     */
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
    }

    /**
     * Checks if the death animation is finished.
     *
     * @return True if the death animation has completed.
     */
    public boolean isDeathAnimationFinished() {
        return this.deathAnim.isAnimationFinished(deathTime);
    }

    public void playDeathSound(){
    }

    /**
     * Gets the damage dealt by the monster.
     *
     * @return The monster's damage value.
     */
    public float getDamage() {
        return this.damage;
    }

    @Override
    public void render(SpriteBatch batch, float scale) {
    }

    @Override
    public void dispose() {
        disposeProjectiles();
    }

    public void disposeProjectiles() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
    }

    /**
     * Gets the monster's projectiles.
     *
     * @return An array of active projectiles.
     */
    public Array<Projectile> getProjectiles() {
        return this.projectiles;
    }
}
