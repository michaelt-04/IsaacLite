package github.tankgame.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.environment.blocks.Rock;
import github.tankgame.items.Bomb;
import github.tankgame.projectiles.Projectile;

import java.util.Objects;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Represents the player character. Extends Character and implements player-specific behavior.
 */
public class Player extends Character {
    private Texture texture;
    private TextureRegion textureRegion;
    private TextureRegion blankHead, headUp, headDown, headLeft, headRight;
    private TextureRegion shootUp, shootDown, shootLeft, shootRight;
    private TextureRegion hurtFrame, dead;
    private Animation<TextureRegion> bodyUpAnim, bodyDownAnim, bodyLeftAnim, bodyRightAnim, deathAnim;
    private TextureRegion currentHead;
    private TextureRegion currentBody;
    private Texture costumeTexture;
    private boolean hasCostume;
    private boolean hasTripleShot;
    private String currentCostume;
    private TextureRegion costumeUp, costumeDown, costumeLeft, costumeRight;
    private TextureRegion costumeShootUp, costumeShootDown, costumeShootLeft, costumeShootRight;
    private TextureRegion sparksUpDown, sparksLeft, sparksRight;

    private float deathTime;
    private float invulnerableTimer = 0;
    private static final float INVULNERABLE_TIMER = 1f;
    private Sound hurtSound, hurtSound2, hurtSound3;

    private float shootInterval = 0.3f;
    private float shootingStateTimer;
    private float shootDirectionTimer;
    private float shootStateDuration = 0.2f;
    private float postShootDirectionDuration = 0.4f;

    private Array<Bomb> bombs;
    private int maxBombs; // Maximum bombs the player can carry
    private int currentBombs; // Current available bombs
    private float damage;

    /**
     * Initializes a Player with a starting position and size.
     *
     * @param positionX Starting x-coordinate of the character.
     * @param positionY Starting y-coordinate of the character.
     *
     */
    public Player(float positionX, float positionY) {
        super(positionX, positionY, 64, 64);
        this.speed = 135 * 2;
        this.health = 6;
        this.maxHealth = health;
        this.damage = 1.0F;

        this.maxBombs = 3;
        this.currentBombs = maxBombs;
        this.bombs = new Array<>();

        projectiles = new Array<>();
        shootCooldown = 0;

        texture = new Texture(Gdx.files.internal("characters/player/isaac_sprite_sheet.png"));
        this.hurtSound = Gdx.audio.newSound(Gdx.files.internal("sounds/isaachurt.wav"));
        this.hurtSound2 = Gdx.audio.newSound(Gdx.files.internal("sounds/isaachurt2.wav"));
        this.hurtSound3 = Gdx.audio.newSound(Gdx.files.internal("sounds/isaachurt3.wav"));
        this.hasCostume = false;
        loadSprites();

        // Initial direction (facing down)
        currentHead = headDown;
        currentBody = bodyDownAnim.getKeyFrame(0);  // Standing down
    }

    /**
     * Resets the player's stats to their default values.
     */
    public void setDefaultStats(){
        this.speed = 270;
        this.health = 6;
        this.damage = 1.0F;
        this.currentBombs = 3;
        this.shootInterval = 0.3f;
        this.postShootDirectionDuration = 0.4f;
    }

    /**
     * Updates the player's state, including bombs.
     *
     * @param delta Time elapsed since last frame.
     * @param rocks Array of rocks to check for interactions with bombs.
     */
    public void update(float delta, Array<Rock> rocks) {
        // Update bombs
        for (int i = bombs.size - 1; i >= 0; i--) {
            Bomb bomb = bombs.get(i);
            bomb.update(delta, rocks);

            if (bomb.hasExploded()) {
                bombs.removeIndex(i); // Remove bomb after explosion
            }
        }

    }

    /**
     * Loads all the player's sprite regions and animations.
     */
    private void loadSprites() {
        int headWidth = 32;
        int headHeight = 32;
        int bodyWidth = 32;
        int bodyHeight = 32;

        // Load head regions
        blankHead = new TextureRegion(texture, 170, 25, headWidth, headHeight);
        headUp = new TextureRegion(texture, 170, 25, headWidth, headHeight);
        headDown = new TextureRegion(texture, 10, 25, headWidth, headHeight);
        headLeft = new TextureRegion(texture, 250, 25, headWidth, headHeight);
        headRight = new TextureRegion(texture, 90, 25, headWidth, headHeight);

        // Load shoot head regions
        shootUp = new TextureRegion(texture, 210, 25, headWidth, headHeight);
        shootDown = new TextureRegion(texture, 50, 25, headWidth, headHeight);
        shootLeft = new TextureRegion(texture, 290, 25, headWidth, headHeight);
        shootRight = new TextureRegion(texture, 130, 25, headWidth, headHeight);

        // Create animations for body in each direction
        bodyUpAnim = createAnimation(texture, 10, 70, bodyWidth, bodyHeight, 5, 0.1f, bodyWidth);
        bodyDownAnim = createAnimation(texture, 170, 70, bodyWidth, bodyHeight, 5, 0.1f, bodyWidth);
        bodyLeftAnim = createAnimation(texture, 7, 113, bodyWidth, bodyHeight, 5, 0.1f, bodyWidth);
        bodyRightAnim = createAnimation(texture, 170, 113, bodyWidth, bodyHeight, 5, 0.1f, bodyWidth);

        hurtFrame = new TextureRegion(texture, 71, 288, 38, 38);

        deathAnim = createAnimation(texture, 5, 288, 38, 38, 3, 0.3f, 63);
        dead = new TextureRegion(texture, 132, 288, 38, 38);
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
     * @param frameJump     Horizontal spacing between frames in the sheet.
     * @return The animation object.
     */
    private Animation<TextureRegion> createAnimation(Texture texture, int startX, int startY, int frameWidth, int frameHeight, int frameCount, float frameDuration, int frameJump) {
        Array<TextureRegion> frames = new Array<>();

        for (int i = 0; i < frameCount; i++) {
            if (startX == 7 && startY == 113) {
                TextureRegion flipped = new TextureRegion(texture, startX + i * frameJump, startY, frameWidth, frameHeight);
                flipped.flip(true, false);
                frames.add(flipped);
            } else {
                frames.add(new TextureRegion(texture, startX + i * frameJump, startY, frameWidth, frameHeight));
            }
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Places a bomb at the player's current position if available.
     *
     * @param rocks Array of rocks to check for interactions with the bomb.
     */
    public void placeBomb(Array<Rock> rocks) {
        if (currentBombs > 0) {
            Bomb newBomb = new Bomb(this.positionX, this.positionY, 64, true); // Example radius 64
            bombs.add(newBomb);
            currentBombs--; // Decrease bomb count
        }
    }

    /**
     * Updates the player's movement and animations based on input.
     *
     * @param delta  Time elapsed since last frame.
     * @param newX   Target x-coordinate for movement.
     * @param newY   Target y-coordinate for movement.
     * @param deltaX Change in x-direction.
     * @param deltaY Change in y-direction.
     */
    @Override
    public void move(float delta, float newX, float newY, float deltaX, float deltaY) {
        super.move(delta, newX, newY, deltaX, deltaY);

        if (this.shootingStateTimer > 0) {
            // Decrement the timer
            this.shootingStateTimer -= Gdx.graphics.getDeltaTime();
        } else {
            // Update direction and animation frame based on movement direction
            if (isMoving) {
                if (deltaX > 0) {
                    currentHead = headRight;
                    currentBody = bodyRightAnim.getKeyFrame(stateTime, true);
                } else if (deltaX < 0) {
                    currentHead = headLeft;
                    currentBody = bodyLeftAnim.getKeyFrame(stateTime, true);
                } else if (deltaY > 0) {
                    currentHead = headUp;
                    currentBody = bodyUpAnim.getKeyFrame(stateTime, true);
                } else if (deltaY < 0) {
                    currentHead = headDown;
                    currentBody = bodyDownAnim.getKeyFrame(stateTime, true);
                }
            } else {
                // Reset to standing pose in last direction
                currentBody = bodyDownAnim.getKeyFrame(0);
                currentHead = headDown;
            }
        }
    }

    /**
     * Shoots a projectile in the specified direction if the cooldown allows.
     *
     * @param deltaX Change in x-direction.
     * @param deltaY Change in y-direction.
     */
    @Override
    public void shoot(float deltaX, float deltaY) {
        if (this.shootCooldown <= 0 && (deltaX != 0 || deltaY != 0)) {
            // Primary projectile
            projectiles.add(new Projectile(
                positionX - 18,
                positionY - 2,
                deltaX,
                deltaY,
                this.damage,
                this
            ));

            // Check for triple-shot effect
            if (hasTripleShot) {
                float divergence = 0.3f;
                float leftDeltaX = deltaX - divergence;
                float leftDeltaY = deltaY - divergence;
                float rightDeltaX = deltaX + divergence;
                float rightDeltaY = deltaY + divergence;

                float leftMagnitude = (float) Math.sqrt(leftDeltaX * leftDeltaX + leftDeltaY * leftDeltaY);
                leftDeltaX /= leftMagnitude;
                leftDeltaY /= leftMagnitude;

                float rightMagnitude = (float) Math.sqrt(rightDeltaX * rightDeltaX + rightDeltaY * rightDeltaY);
                rightDeltaX /= rightMagnitude;
                rightDeltaY /= rightMagnitude;

                // Add projectiles
                projectiles.add(new Projectile(
                    positionX - 18,
                    positionY - 2,
                    leftDeltaX,
                    leftDeltaY,
                    this.damage,
                    this
                ));
                projectiles.add(new Projectile(
                    positionX - 18,
                    positionY - 2,
                    rightDeltaX,
                    rightDeltaY,
                    this.damage,
                    this
                ));
            }

            // Set cooldown and update head sprite
            this.shootCooldown = shootInterval;
            if (deltaX == 1) {
                currentHead = shootRight;
            } else if (deltaX == -1) {
                currentHead = shootLeft;
            } else if (deltaY == 1) {
                currentHead = shootUp;
            } else if (deltaY == -1) {
                currentHead = shootDown;
            }

            // Start shooting animation
            this.shootingStateTimer = this.shootStateDuration;
            this.shootDirectionTimer = this.postShootDirectionDuration;
        }
    }

    @Override
    public void takeDamage(float damage) {
        if (this.invulnerableTimer <= 0) { // Only take damage if not invulnerable
            super.takeDamage(damage);

            int rand = random.nextInt(3);

            switch (rand) {
                case 0:
                    this.hurtSound.play(0.6f);
                    break;
                case 1:
                    this.hurtSound2.play(0.6f);
                    break;
                case 2:
                    this.hurtSound3.play(0.6f);
                    break;
            }

            this.invulnerableTimer = INVULNERABLE_TIMER;
        }
    }

    public void updateInvulnerability(float delta) {
        if (this.invulnerableTimer > 0) {
            this.invulnerableTimer -= delta;
            if (this.invulnerableTimer < 0) {
                this.invulnerableTimer = 0;
            }
        }
    }

    /**
     * Plays the death animation of the player and updates the current head texture accordingly.
     */
    @Override
    public void playDeathAnimation() {
        if (deathAnim != null) {
            // Check if animation has completed
            deathTime += Gdx.graphics.getDeltaTime();
            if (deathAnim.isAnimationFinished(deathTime)) {
                // Hold the final frame of the animation
                currentHead = dead;
            } else {
                // Play the death animation
                currentHead = deathAnim.getKeyFrame(deathTime, true);
            }
        }
    }

    /**
     * Returns the list of projectiles fired by the player.
     *
     * @return An array of projectiles.
     */
    public Array<Projectile> getProjectiles() {
        return this.projectiles;
    }

    /**
     * Renders the player character and its accessories, such as bombs, projectiles, or costume overlays.
     *
     * @param batch The SpriteBatch used for drawing.
     * @param scale The scaling factor for rendering the player.
     */
    @Override
    public void render(SpriteBatch batch, float scale) {
        float playerScaledX = this.getPositionX() * scale;
        float playerScaledY = this.getPositionY() * scale;
        float playerScaledWidth = this.getWidth() * scale;
        float playerScaledHeight = this.getHeight() * scale;

        for (Bomb bomb : bombs) {
            bomb.render(batch);
        }

        TextureRegion headToRender = Objects.equals("health", currentCostume) ? blankHead : currentHead;
        TextureRegion bodyToRender = currentBody;
        TextureRegion costumeOverlay = hasCostume ? costumeOverlayForCurrentState() : null;
        TextureRegion costumeOverlay2 = null;

        if (Objects.equals("bomb", currentCostume)) {
            if (this.shootingStateTimer > 0) {
                if (Objects.equals(costumeOverlay, costumeShootDown)) {
                    costumeOverlay2 = sparksUpDown;
                } else if (Objects.equals(costumeOverlay, costumeShootLeft)) {
                    costumeOverlay2 = sparksLeft;
                } else {
                    costumeOverlay2 = sparksRight;
                }
            } else {

                if (Objects.equals(costumeOverlay, costumeDown)) {
                    costumeOverlay2 = sparksUpDown;
                } else if (Objects.equals(costumeOverlay, costumeLeft)) {
                    costumeOverlay2 = sparksLeft;
                } else {
                    costumeOverlay2 = sparksRight;
                }
            }
        }

        if (isDead) {
            batch.draw(headToRender, positionX, positionY + bodyToRender.getRegionHeight() - 12, playerScaledWidth, playerScaledHeight);
        } else if (invulnerableTimer > 0) {
            if (((int) (invulnerableTimer * 10)) % 2 == 0) {
                batch.draw(hurtFrame, positionX, positionY + bodyToRender.getRegionHeight() - 12, playerScaledWidth, playerScaledHeight);
            } else {
                batch.draw(bodyToRender, positionX, positionY, playerScaledWidth, playerScaledHeight);
                batch.draw(headToRender, positionX, positionY + bodyToRender.getRegionHeight() - 12, playerScaledWidth, playerScaledHeight);

                if (costumeOverlay != null) {
                    batch.draw(costumeOverlay, positionX, positionY + bodyToRender.getRegionHeight() - 12, playerScaledWidth, playerScaledHeight);
                }
            }
        } else {
            batch.draw(bodyToRender, positionX, positionY, playerScaledWidth, playerScaledHeight);
            batch.draw(headToRender, positionX, positionY + bodyToRender.getRegionHeight() - 12, playerScaledWidth, playerScaledHeight);

            if (costumeOverlay != null) {

                if (Objects.equals(currentCostume, "bomb")) {

                    if (this.shootingStateTimer > 0) {
                        batch.draw(costumeOverlay, positionX - 36, positionY + bodyToRender.getRegionHeight() - 38, costumeOverlay.getRegionWidth() * 2, costumeOverlay.getRegionHeight() * 2);
                    } else {
                        batch.draw(costumeOverlay, positionX - 36, positionY + bodyToRender.getRegionHeight() - 34, costumeOverlay.getRegionWidth() * 2, costumeOverlay.getRegionHeight() * 2);
                    }

                    if (costumeOverlay2 != null) {
                        if (this.shootingStateTimer > 0) {
                            batch.draw(costumeOverlay2, positionX - 36, positionY + bodyToRender.getRegionHeight() - 38, costumeOverlay.getRegionWidth() * 2, costumeOverlay.getRegionHeight() * 2);
                        } else {
                            batch.draw(costumeOverlay2, positionX - 36, positionY + bodyToRender.getRegionHeight() - 34, costumeOverlay.getRegionWidth() * 2, costumeOverlay.getRegionHeight() * 2);
                        }
                    }


                } else {
                    batch.draw(costumeOverlay, positionX - 4, positionY + bodyToRender.getRegionHeight() - 6, playerScaledWidth, playerScaledHeight);
                }
            }
        }
    }

    /**
     * Determines the appropriate costume overlay based on the current state and animation.
     *
     * @return The appropriate costume overlay texture region.
     */
    private TextureRegion costumeOverlayForCurrentState() {
        if (this.shootingStateTimer > 0) {
            if (currentHead == shootRight) return costumeShootRight;
            if (currentHead == shootLeft) return costumeShootLeft;
            if (currentHead == shootUp) return costumeShootUp;
            if (currentHead == shootDown) return costumeShootDown;
        } else {
            if (currentHead == headRight) return costumeRight;
            if (currentHead == headLeft) return costumeLeft;
            if (currentHead == headUp) return costumeUp;
            if (currentHead == headDown) return costumeDown;
        }
        return null;
    }

    /**
     * Returns the list of bombs available for the player.
     *
     * @return An array of bombs.
     */
    public Array<Bomb> getBombs() {
        return this.bombs;
    }

    /**
     * Returns the current number of bombs the player has.
     *
     * @return The current number of bombs.
     */
    public int getCurrentBombs() {
        return this.currentBombs;
    }

    /**
     * Sets whether the player has the triple shot ability.
     *
     * @param hasTripleShot True if the player has triple shot ability, false otherwise.
     */
    public void setHasTripleShot(boolean hasTripleShot) {
        this.hasTripleShot = hasTripleShot;
    }

    /**
     * Increases the number of bombs the player has by a specified value.
     *
     * @param value The amount to increase the number of bombs by.
     */
    public void increaseBombs(float value) {
        this.currentBombs = (int) value;
    }


    /**
     * Returns the maximum health of the player.
     *
     * @return The maximum health value.
     */
    public float getMaxHealth() {
        return this.maxHealth;
    }

    /**
     * Reduces the shooting interval and direction timers based on the specified value.
     *
     * @param value The value by which to reduce the shoot timers.
     */
    public void increaseTears(float value) {
        this.shootInterval -= value;
        this.shootDirectionTimer -= value;
        this.postShootDirectionDuration -= value;
    }

    public void increaseDamage(float value) {
        this.damage += value;
    }


    /**
     * Sets the appearance of the player, including the costume texture and specific costume parts.
     *
     * @param appearance The texture to be used for the costume.
     * @param currentCostume The name of the current costume (e.g., "health", "bomb", "triple").
     */
    public void setAppearance(Texture appearance, String currentCostume) {
        this.hasCostume = true;
        this.costumeTexture = appearance;
        this.currentCostume = currentCostume;


        // Load head regions
        if (Objects.equals("health", currentCostume)) {
            costumeUp = null;
            costumeDown = new TextureRegion(costumeTexture, 0, 15, 32, 32);
            costumeRight = new TextureRegion(costumeTexture, 64, 15, 32, 32);
            costumeLeft = new TextureRegion(costumeRight);
            costumeLeft.flip(true, false);

            costumeShootUp = null;
            costumeShootDown = new TextureRegion(costumeTexture, 32, 15, 32, 32);
            costumeShootRight = new TextureRegion(costumeTexture, 96, 15, 32, 32);
            costumeShootLeft = new TextureRegion(costumeShootRight);
            costumeShootLeft.flip(true, false);
        } else if (Objects.equals("bomb", currentCostume)) {
            costumeDown = new TextureRegion(costumeTexture, 0, 128, 64, 64);
            costumeUp = costumeDown;
            costumeLeft = new TextureRegion(costumeTexture, 128, 128, 64, 64);
            costumeRight = new TextureRegion(costumeLeft);
            costumeRight.flip(true, false);
            sparksUpDown = new TextureRegion(costumeTexture, 0, 0, 64, 64);
            sparksLeft = new TextureRegion(costumeTexture, 128, 0, 64, 64);
            sparksRight = new TextureRegion(sparksLeft);
            sparksRight.flip(true, false);

            costumeShootDown = new TextureRegion(costumeTexture, 0, 128, 64, 64);
            costumeShootUp = costumeShootDown;
            costumeShootLeft = new TextureRegion(costumeTexture, 128, 128, 64, 64);
            costumeShootRight = new TextureRegion(costumeShootLeft);
            costumeShootRight.flip(true, false);
        }else if (Objects.equals("triple", currentCostume)) {
            costumeUp = null;
            costumeDown = new TextureRegion(costumeTexture, 0, 0, 32, 32);
            costumeRight = new TextureRegion(costumeTexture, 64, 0, 32, 32);
            costumeLeft = new TextureRegion(costumeRight);
            costumeLeft.flip(true, false);

            costumeShootUp = null;
            costumeShootDown = new TextureRegion(costumeTexture, 32, 0, 32, 32);
            costumeShootRight = new TextureRegion(costumeTexture, 96, 0, 32, 32);
            costumeShootLeft = new TextureRegion(costumeShootRight);
            costumeShootLeft.flip(true, false);
        }
    }

    public void dispose() {
        texture.dispose();
        disposeProjectiles();
        disposeBombs();
    }

    public void disposeProjectiles() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
    }

    public void disposeBombs(){
        for(Bomb bomb : bombs){
            bomb.dispose();
        }
        bombs.clear();
    }
}
