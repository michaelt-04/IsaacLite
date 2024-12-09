package github.tankgame.characters.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Monster;
import github.tankgame.projectiles.Projectile;
import github.tankgame.utils.CollisionDetector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Clotty extends Monster {
    private Texture bodyTexture;
    private Texture bloodTexture;
    private Animation<TextureRegion> bodyAnimation, hopAnimation, attackAnimation;
    private TextureRegion currentBodyFrame;
    private boolean isFacingLeft = false; // Tracks whether Clotty is facing left

    private List<BloodTrail> bloodTrails;
    private float trailCooldown;
    private static final float TRAIL_INTERVAL = 0.3f;

    protected static final float SHOOT_INTERVAL = 1.10f;

    private boolean isAttacking;

    public Clotty(float positionX, float positionY) {
        super(positionX, positionY, 40*2, 40*2);
        this.width = 64*2;
        this.height = 64*2;
        this.speed = 50*2;
        this.health = 3.5f;
        this.damage = 1f;
        this.bloodTrails = new ArrayList<>();
        this.trailCooldown = 0;

        bodyTexture = new Texture(Gdx.files.internal("characters/monsters/clotty_sheet.png"));
        bloodTexture = new Texture(Gdx.files.internal("characters/monsters/red_creep.png"));
        loadSprites();
    }

    private void loadSprites() {
        int frameWidth = 64;
        int frameHeight = 64;

        hopAnimation = createHopAnimation(bodyTexture);

        attackAnimation = createAttackAnimation(bodyTexture);

        currentBodyFrame = hopAnimation.getKeyFrame(0);
    }

    private Animation<TextureRegion> createHopAnimation(Texture texture) {
        Array<TextureRegion> frames = new Array<>();

        int[][] frameDetails = {
            {0, 0, 64, 64},   // Frame 1 (XCrop, YCrop, Width, Height)
            {64, 0, 64, 64},  // Frame 2
            {128, 0, 64, 64}, // Frame 3
            {192, 0, 64, 64}, // Frame 4
            {0, 64, 64, 64},  // Frame 5
            {64, 64, 64, 64}, // Frame 6
            {128, 64, 64, 64},// Frame 7
            {192, 64, 64, 64},// Frame 8
            {0, 0, 64, 64},  // Frame 9
            {0, 0, 64, 64},   // Frame 10
            {0, 0, 64, 64}    // Frame 11
        };

        for (int[] details : frameDetails) {
            int xCrop = details[0];
            int yCrop = details[1];
            int width = details[2];
            int height = details[3];
            frames.add(new TextureRegion(texture, xCrop, yCrop, width, height));
        }

        return new Animation<>(0.09f, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> createAttackAnimation(Texture texture) {
        Array<TextureRegion> frames = new Array<>();

        // Define the frame details from the XML
        int[][] frameDetails = {
            {0, 0, 64, 64},   // Frame 1 (XCrop, YCrop, Width, Height)
            {0, 128, 64, 64},  // Frame 2
            {64, 64, 64, 64}, // Frame 3
            {128, 64, 64, 64}, // Frame 4
            {64, 64, 64, 64},  // Frame 5
            {64, 0, 64, 64}, // Frame 6
            {64, 128, 64, 64},// Frame 7
            {128, 128, 64, 64},// Frame 8
            {64, 0, 64, 64},  // Frame 9
            {0, 128, 64, 64},   // Frame 10
            {0, 0, 64, 64},   // Frame 11
            {0, 0, 64, 64},   // Frame 12
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

    private void shoot() {
        if (this.shootCooldown <= 0) {
            //System.out.println("SHOOT");
            //System.out.println(this.shootCooldown);
            // Cardinal directions
            float[][] directions = {
                {1, 0},   // Right
                {-1, 0},  // Left
                {0, 1},   // Up
                {0, -1}   // Down
            };

            // Spawn a projectile in each direction
            for (float[] dir : directions) {
                this.projectiles.add(new Projectile(
                    this.positionX,
                    this.positionY,
                    dir[0],
                    dir[1],
                    1.0F,
                    this
                ));
            }

            // Reset the cooldown
            this.shootCooldown = SHOOT_INTERVAL;
        }
        //System.out.println(this.shootCooldown);

    }

    @Override
    public void move(float playerX, float playerY, float deltaTime) {
        if (this.isDead) return;

        float attackRange = 150f; // Range within which Clotty attacks the player
        float attackDuration = attackAnimation.getAnimationDuration(); // Time for one attack cycle

        // Calculate direction and distance to the player
        float directionX = playerX - this.positionX;
        float directionY = playerY - this.positionY;
        float distanceToPlayer = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        if (isAttacking) {
            // Play attack animation and stop moving
            currentBodyFrame = attackAnimation.getKeyFrame(stateTime, false);
            stateTime += deltaTime;

            this.shoot();
            // Check if attack animation is finished
            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
                stateTime = 0; // Reset state time for next animation cycle
            }
            return;
        } else {
            // Normal hopping movement when not attacking
            currentBodyFrame = hopAnimation.getKeyFrame(stateTime, true);
            stateTime += deltaTime;
        }

        // If within attack range, start attacking
        if (distanceToPlayer <= attackRange) {
            isAttacking = true;
            stateTime = 0; // Reset state time for attack animation
            return;
        }


        if (distanceToPlayer == 0) return; // Avoid division by zero

        // Normalize direction
        float normalizedX = directionX / distanceToPlayer;
        float normalizedY = directionY / distanceToPlayer;

        // Flip the animation based on movement direction
        if (normalizedX < 0 && !isFacingLeft) {
            // Moving left, flip if currently facing right
            isFacingLeft = true;
        } else if (normalizedX > 0 && isFacingLeft) {
            // Moving right, flip if currently facing left
            isFacingLeft = false;
        }

        CollisionDetector collisionDetector = new CollisionDetector(this.room);

        normalizedX = normalizedX * this.speed * deltaTime;
        normalizedY = normalizedY * this.speed * deltaTime;

        // Check collision and move Clotty
        float[] collisionMove = collisionDetector.checkCharacterTileCollision(this, normalizedX, normalizedY);
        float newX = collisionMove[0];
        float newY = collisionMove[1];

        // Update Clotty position
        setPosition(newX, newY);
        this.updateBounds();

        // Drop blood trail periodically
        trailCooldown -= deltaTime;
        if (trailCooldown <= 0) {
            trailCooldown = TRAIL_INTERVAL;
            bloodTrails.add(new BloodTrail(this.positionX, this.positionY, bloodTexture));
        }
    }

    @Override
    public void render(SpriteBatch batch, float scale) {
        for (BloodTrail trail : bloodTrails) {
            trail.render(batch, scale);
        }

        float scaledWidth = this.width * scale;
        float scaledHeight = this.height * scale;

        // Flip the frame during rendering based on direction
        boolean flipX = isFacingLeft;

        // Draw the current frame with flipping
        batch.draw(
            currentBodyFrame,
            flipX ? this.positionX + scaledWidth : this.positionX, // Adjust position if flipped
            this.positionY,
            flipX ? -scaledWidth : scaledWidth, // Flip width if necessary
            scaledHeight
        );
        this.renderProjectiles(batch);
        this.updateBounds();
    }

    @Override
    public void playDeathAnimation() {
        if (this.deathAnim != null) {
            this.deathTime += Gdx.graphics.getDeltaTime();
            this.currentBodyFrame = deathAnim.getKeyFrame(deathTime, true);
        }

    }

    @Override
    public void dispose() {
        bodyTexture.dispose();
        bloodTexture.dispose();
        for (BloodTrail trail : bloodTrails) {
            trail.dispose();
        }
    }

    private static class BloodTrail {
        private float x, y;
        private Texture texture;
        private TextureRegion bloodTexture;
        private float duration;

        public BloodTrail(float x, float y, Texture texture) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.bloodTexture = new TextureRegion(texture, 0, 0, 32, 32);
            this.duration = 3.0f; // Blood trail lasts for 3 seconds
        }

        public void render(SpriteBatch batch, float scale) {
            duration -= Gdx.graphics.getDeltaTime();
            if (duration > 0) {
                float alpha = duration / 3.0f; // Opacity decreases over 3 seconds
                float scaledWidth = 32 * scale;
                float scaledHeight = 32 * scale;

                batch.setColor(1, 1, 1, alpha); // Set color with adjusted alpha
                batch.draw(bloodTexture, x, y, scaledWidth*2, scaledHeight*2);
                batch.setColor(1, 1, 1, 1); // Reset color to full opacity after drawing
            }
        }

        public void dispose() {
            texture.dispose();
        }
    }
}

