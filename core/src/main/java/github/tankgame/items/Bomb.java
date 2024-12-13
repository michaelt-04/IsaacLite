package github.tankgame.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import github.tankgame.environment.blocks.Rock;

public class Bomb {
    private float x, y; // Position of the bomb
    private Texture idleTexture, bombTexture, explosionTexture;
    private TextureRegion idleTextureRegion, darkSpotTextureRegion;
    private Animation<TextureRegion> explodingAnimation; // Animation for when the bomb is about to explode
    private Animation<TextureRegion> explosionAnimation; // Animation for the explosion
    private float explosionRadius; // Radius of the explosion
    private float explosionTime; // Timer for explosion
    private float elapsedTime; // Time tracker for animations
    private boolean isExploding; // Whether the bomb is in the "exploding" phase
    private boolean hasExploded; // Whether the bomb has exploded
    private Texture darkSpotTexture; // Texture for the dark spot
    private boolean leavesDarkSpot; // Whether the bomb leaves a dark spot after exploding
    private float darkSpotFadeDuration = 1.0f; // Duration for the dark spot to fade out
    private float darkSpotAlpha = 1.0f; // Alpha value for the dark spot
    private boolean fadingDarkSpot = false; // Whether the dark spot is fading
    private float damage = 0;
    private Rectangle bounds;
    private Sound explosionSound;
    private int soundCount = 0;

    public Bomb(float x, float y, float explosionRadius, boolean startExploding) {
        this.x = x;
        this.y = y;
        this.explosionRadius = explosionRadius;
        this.explosionTime = 2.0f; // Time before the bomb explodes (in seconds)
        this.elapsedTime = 0.0f;
        this.isExploding = startExploding; // Start in exploding phase if specified
        this.hasExploded = false;
        this.leavesDarkSpot = false;

        // Load textures and animations
        this.idleTexture = new Texture("items/bomb/bomb_sheet.png");
        this.idleTextureRegion = new TextureRegion(idleTexture, 0, 0, 32, 32);
        this.bombTexture = new Texture(Gdx.files.internal("items/bomb/bomb_sheet.png"));
        this.explosionTexture = new Texture(Gdx.files.internal("items/bomb/explosion_sheet.png"));
        this.darkSpotTexture = new Texture("items/bomb/dark_spot.png");
        this.explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));

        this.explodingAnimation = createExplodingAnimation(bombTexture);
        this.explosionAnimation = createExplosionAnimation(explosionTexture);
        this.darkSpotTextureRegion = new TextureRegion(darkSpotTexture, 0, 0, 96, 50);
    }

    private Animation<TextureRegion> createExplodingAnimation(Texture texture) {
        Array<TextureRegion> frames = new Array<>();

        // Define the frame details from the XML
        int[][] frameDetails = {
            {0,0,32,32},
            {0,0,32,32},
            {128,0,32,32},
            {128,0,32,32},
            {0,0,32,32},
            {0,0,32,32},
            {128,0,32,32},
            {128,0,32,32},
            {0,0,32,32},
            {128,0,32,32},
            {128,0,32,32},
            {0,0,32,32},
            {0,0,32,32},
            {128,0,32,32},
        };

        // Iterate over frame details to add each frame
        for (int[] details : frameDetails) {
            int xCrop = details[0];
            int yCrop = details[1];
            int width = details[2];
            int height = details[3];
            frames.add(new TextureRegion(texture, xCrop, yCrop, width, height));
        }

        return new Animation<>(0.09f, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> createExplosionAnimation(Texture texture) {
        Array<TextureRegion> frames = new Array<>();

        // Define the frame details from the XML
        int[][] frameDetails = {
            {0,0,96,96},
            {96,0,96,96},
            {192,0,96,96},
            {288,0,96,96},
            {0,96,96,96},
            {96,96,96,96},
            {192,96,96,96},
            {288,96,96,96},
            {0,192,96,96},
            {96,192,96,96},
            {192,192,96,96},
            {288,192,96,96},
        };

        // Iterate over frame details to add each frame
        for (int[] details : frameDetails) {
            int xCrop = details[0];
            int yCrop = details[1];
            int width = details[2];
            int height = details[3];
            frames.add(new TextureRegion(texture, xCrop, yCrop, width, height));
        }

        return new Animation<>(0.09f, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta, Array<Rock> rocks) {
        elapsedTime += delta;

        if (!hasExploded) {
            // Trigger explosion if time is up
            if (elapsedTime >= explosionTime && !hasExploded) {
                this.bounds = new Rectangle(this.x, this.y, explosionRadius, explosionRadius);
                hasExploded = true;
                damageRocks(rocks);
                this.damage = 2.0f;
                leavesDarkSpot = true; // Leave a dark spot after explosion
            } else if (!isExploding && elapsedTime >= explosionTime - 1.0f) {
                isExploding = true;
            }
        } else if (leavesDarkSpot && !fadingDarkSpot) {
            this.bounds.set(0,0,0,0);
            this.damage = 0;
            // Start fading the dark spot after explosion animation finishes
            if (isExplosionAnimationFinished()) {
                fadingDarkSpot = true;
                elapsedTime = 0; // Reset elapsed time for fade
            }
        } else if (fadingDarkSpot) {
            // Reduce alpha over time
            darkSpotAlpha -= delta / darkSpotFadeDuration;
            if (darkSpotAlpha <= 0) {
                darkSpotAlpha = 0; // Ensure it doesn't go below zero
                leavesDarkSpot = false; // Dark spot fade is complete
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (hasExploded) {

            if(this.soundCount == 0){
                this.explosionSound.play(0.6f);

                this.soundCount++;
            }

            if (leavesDarkSpot) {
                batch.setColor(1, 1, 1, darkSpotAlpha); // Set alpha for the dark spot
                batch.draw(darkSpotTextureRegion, x - explosionRadius / 2, y - explosionRadius / 2, explosionRadius * 2, explosionRadius * 2);
                batch.setColor(1, 1, 1, 1);
            }

            // Render explosion animation
            if (!fadingDarkSpot) {
                TextureRegion currentFrame = explosionAnimation.getKeyFrame(elapsedTime - explosionTime, false);
                batch.draw(currentFrame, x - explosionRadius / 2, y, explosionRadius * 2, explosionRadius * 2);
            }
        } else if (isExploding) {
            // Render exploding animation

            TextureRegion currentFrame = explodingAnimation.getKeyFrame(elapsedTime, false);
            batch.draw(currentFrame, x, y, 50, 50);
        }
    }

    private void damageRocks(Array<Rock> rocks) {
        for (Rock rock : rocks) {
            if (!rock.isDestroyed()) {
                Rectangle rockBounds = rock.getBounds();
                Rectangle explosionBounds = new Rectangle(
                    x - explosionRadius, y - explosionRadius, explosionRadius * 2, explosionRadius * 2);

                if (explosionBounds.overlaps(rockBounds)) {
                    rock.breakRock(); // Destroy the rock if within range
                }
            }
        }
    }

    public boolean isExplosionAnimationFinished() {
        return explosionAnimation.isAnimationFinished(elapsedTime - explosionTime);
    }

    public boolean isHasExploded() {
        return this.hasExploded;
    }

    public boolean hasExploded() {
        return this.hasExploded && !leavesDarkSpot;
    }

    public float getDamage() {
        return this.damage;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public void dispose() {
        idleTexture.dispose();
        bombTexture.dispose();
        explosionTexture.dispose();
        darkSpotTexture.dispose();
    }
}
