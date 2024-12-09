package github.tankgame.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.projectiles.Projectile;
import github.tankgame.utils.CollisionDetector;

import java.util.Iterator;
import java.util.Objects;

// Extends character, contains monster-specific methods
public abstract class Monster extends Character {
    protected Texture deathTexture;
    protected Animation<TextureRegion> deathAnim;
    protected float damage;

    protected float deathTime;

    public Monster(float positionX, float positionY, int width, int height) {
        super(positionX, positionY, width, height);
        this.projectiles = new Array<>();
        this.shootCooldown = 0;
        deathTexture = new Texture(Gdx.files.internal("projectiles/monster_death_sheet.png"));
        loadSprites();
    }

    private void loadSprites() {
        deathAnim = createAnimation(deathTexture, 0, 0, 69, 69, 4, 0.04f, 63);
    }

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

    public void move(float playerX, float playerY, float deltaTime) {
    }

    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
    }

    public boolean isDeathAnimationFinished() {
        return this.deathAnim.isAnimationFinished(deathTime);
    }

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

    public Array<Projectile> getProjectiles() {
        return this.projectiles;
    }
}
