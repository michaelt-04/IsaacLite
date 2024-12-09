package github.tankgame.characters.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import github.tankgame.characters.Monster;

public class Boss extends Monster {

    // Boss-specific properties
    private Texture healthBarTexture;
    private float maxHealth;
    private float currentHealth;
    private boolean isAttacking;
    private Animation<TextureRegion> attackAnimation;
    private float attackCooldown;
    private float attackTime;

    public Boss(float x, float y, int width, int height) {
        super(x, y, width, height);

        // Set up health values
        this.maxHealth = 40;
        this.health = maxHealth;

        // Load boss texture and animations
        healthBarTexture = new Texture(Gdx.files.internal("hud/ui_bosshealthbar.png"));

    }

    @Override
    public void move(float playerX, float playerY, float deltaTime) {
    }

    @Override
    public void render(SpriteBatch batch, float scale) {
    }

    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
    }

    @Override
    public void dispose() {
        super.dispose();
        healthBarTexture.dispose();
    }
}
