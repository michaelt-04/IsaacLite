package github.tankgame.characters.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;

// Extends Boss, manages chub-specific actions and stats
public class Chub extends Boss {
    private Texture chubTexture;
    private TextureRegion idleHeadRegion, idleHeadUpRegion;
    private TextureRegion[] attackHeadRegions;
    private TextureRegion seg1Region, seg2Region, upDownSegRegion;
    private TextureRegion currentHead;
    private TextureRegion currentBodyFrame;
    private boolean isCharging;
    private float chargeCooldown;
    private float chargeDuration;
    private boolean isFacingLeft;
    private boolean isFacingUp;
    private boolean isFacingRight, isFacingDown;
    private float chargeX, chargeY; // To track the current charge direction
    private boolean isMovingHorizontal; // Flag to track if Chub is moving horizontally
    private float chargeStartX, chargeStartY;

    private static final float CHARGE_INTERVAL = 3.0f; // Time between charges
    private static final float CHARGE_SPEED = 400f; // Speed of charge
    private static final float CHARGE_RANGE = 500f; // Range of charge attack
    private static final float RANDOM_MOVEMENT_SPEED = 250f; // Random movement speed
    private static final float RANDOM_MOVEMENT_INTERVAL = 1.0f; // Interval for random movement in seconds
    private float chargeTimeElapsed;

    private float randomMovementTimer; // Timer for random movement
    private float randomMoveX, randomMoveY; // Movement in random direction

    /**
     * Initializes Chub with position and default properties.
     *
     * @param positionX The x-coordinate of Chub's position.
     * @param positionY The y-coordinate of Chub's position.
     */
    public Chub(float positionX, float positionY) {
        super(positionX, positionY, 160, 160);
        this.width = 160;
        this.height = 160;
        this.damage = 2f; // Damage for contact (full heart)
        this.chargeCooldown = CHARGE_INTERVAL;
        this.chargeDuration = 1.0f; // Duration of the charge
        this.chargeTimeElapsed = 0.0f; // Initialize charge time elapsed
        this.randomMovementTimer = RANDOM_MOVEMENT_INTERVAL; // Initialize the timer
        this.isDead = false;

        chubTexture = new Texture(Gdx.files.internal("characters/boss/boss_032_chub.png"));
        loadSprites();
    }

    /**
     * Loads sprite textures and regions for Chub's animations and appearance.
     */
    private void loadSprites() {
        int frameWidth = 64;
        int frameHeight = 64;

        idleHeadRegion = new TextureRegion(chubTexture, 0, 0, frameWidth, frameHeight);
        idleHeadUpRegion = new TextureRegion(chubTexture, 128, 0, frameWidth, frameHeight);
        attackHeadRegions = new TextureRegion[]{new TextureRegion(chubTexture, 256, 0, frameWidth, frameHeight), new TextureRegion(chubTexture, 256, 256, frameWidth, frameHeight), new TextureRegion(chubTexture, 192,0, frameWidth, frameHeight)};
        seg1Region = new TextureRegion(chubTexture, 0, 256, frameWidth, frameHeight);
        seg2Region = new TextureRegion(chubTexture, 64, 256, frameWidth, frameHeight);
        upDownSegRegion = new TextureRegion(chubTexture, 256, 320, frameWidth, frameHeight);

        currentHead = idleHeadRegion;
    }

    /**
     * Handles Chub's movement, including charging and random wandering.
     *
     * @param playerX   The x-coordinate of the player's position.
     * @param playerY   The y-coordinate of the player's position.
     * @param deltaTime The time elapsed since the last frame, used for smooth movement.
     */
    @Override
    public void move(float playerX, float playerY, float deltaTime) {
        if (this.isDead) return;

        chargeCooldown -= deltaTime;

        // If charge is ready, check if Isaac is aligned in cardinal direction
        if (chargeCooldown <= 0) {
            chargeCooldown = CHARGE_INTERVAL;
            checkForCharge(playerX, playerY, deltaTime);
        }

        if (isCharging) {
            // Continue moving in the charging direction
            moveCharging(deltaTime);
            return;
        } else {
            // Handle random movement
            randomMovement(deltaTime);
        }
    }

    /**
     * Determines if Chub should start charging based on the player's position.
     *
     * @param playerX   The x-coordinate of the player's position.
     * @param playerY   The y-coordinate of the player's position.
     * @param deltaTime The time elapsed since the last frame.
     */
    private void checkForCharge(float playerX, float playerY, float deltaTime) {
        if (Math.abs(playerX - this.positionX) > Math.abs(playerY - this.positionY)) {
            if (playerX > this.positionX) {
                startCharging(1, 0);  // Move right
                isFacingLeft = false;
                isFacingRight = true;
                isFacingUp = false;
                isFacingDown = false;
            } else {
                startCharging(-1, 0); // Move left
                isFacingLeft = true;
                isFacingRight = false;
                isFacingUp = false;
                isFacingDown = false;
            }
        } else {
            if (playerY > this.positionY) {
                startCharging(0, 1);  // Move up
                isFacingUp = true;
                isFacingDown = false;
                isFacingLeft = false;
                isFacingRight = false;
            } else {
                startCharging(0, -1); // Move down
                isFacingUp = false;
                isFacingDown = true;
                isFacingLeft = false;
                isFacingRight = false;
            }
        }
    }

    /**
     * Initiates Chub's charging behavior in a given direction.
     *
     * @param dirX Direction of charge along the x-axis (-1, 0, or 1).
     * @param dirY Direction of charge along the y-axis (-1, 0, or 1).
     */
    private void startCharging(int dirX, int dirY) {
        isCharging = true;
        chargeTimeElapsed = 0.0f;

        // Set the direction and starting position for charge
        chargeStartX = this.positionX;
        chargeStartY = this.positionY;

        chargeX = dirX * CHARGE_SPEED;
        chargeY = dirY * CHARGE_SPEED;

        isMovingHorizontal = (dirY == 0); // Horizontal move if Y direction is zero
    }

    /**
     * Handles Chub's movement during a charge attack.
     *
     * @param deltaTime The time elapsed since the last frame.
     */
    private void moveCharging(float deltaTime) {
        if (isCharging) {
            // Increment charge time elapsed
            chargeTimeElapsed += deltaTime;

            // Check if the charge time has exceeded the max duration
            if (chargeTimeElapsed >= chargeDuration) {
                isCharging = false;  // Stop charging if duration is exceeded
                chargeTimeElapsed = 0f;  // Reset the charge time
                return;
            }

            if (isMovingHorizontal) {
                // Horizontal movement
                this.positionX += chargeX * deltaTime;

                if (this.positionX < 80) {
                    this.positionX = 80; // Prevent going off the left side
                    isCharging = false;  // Stop charging
                } else if (this.positionX > room.getRoomWidth() - this.width - 80) {
                    this.positionX = room.getRoomWidth() - this.width - 80; // Prevent going off the right side
                    isCharging = false;  // Stop charging
                }
            } else {
                // Vertical movement
                this.positionY += chargeY * deltaTime;

                if (this.positionY < 80) {
                    this.positionY = 80; // Prevent going off the bottom
                    isCharging = false;  // Stop charging
                } else if (this.positionY > room.getRoomHeight() - this.height - 80) {
                    this.positionY = room.getRoomHeight() - this.height - 80; // Prevent going off the top
                    isCharging = false;  // Stop charging
                }
            }

            // Check if the charge has gone beyond its range
            if (Math.abs(this.positionX - chargeStartX) > CHARGE_RANGE ||
                Math.abs(this.positionY - chargeStartY) > CHARGE_RANGE) {
                isCharging = false;  // Stop charging if the range is exceeded
                chargeTimeElapsed = 0f; // Reset charge time when the charge ends
            }

            updateBounds(); // Update bound box after movement
        }
    }

    /**
     * Handles Chub's random wandering behavior when not charging.
     *
     * @param deltaTime The time elapsed since the last frame.
     */
    private void randomMovement(float deltaTime) {
        randomMovementTimer -= deltaTime;

        if (randomMovementTimer <= 0) {
            Random rand = new Random();

            // Random direction: 0 = Right, 1 = Left, 2 = Up, 3 = Down
            int direction = rand.nextInt(4);

            float moveDistance = RANDOM_MOVEMENT_SPEED; // Movement per frame
            randomMoveX = 0;
            randomMoveY = 0;

            switch (direction) {
                case 0:
                    randomMoveX = moveDistance; // Move right
                    isFacingRight = true;
                    isFacingLeft = false;
                    isFacingUp = false;
                    isFacingDown = false;
                    break;
                case 1:
                    randomMoveX = -moveDistance; // Move left
                    isFacingLeft = true;
                    isFacingRight = false;
                    isFacingUp = false;
                    isFacingDown = false;
                    break;
                case 2:
                    randomMoveY = moveDistance; // Move up
                    isFacingUp = true;
                    isFacingDown = false;
                    isFacingLeft = false;
                    isFacingRight = false;
                    break;
                case 3:
                    randomMoveY = -moveDistance; // Move down
                    isFacingDown = true;
                    isFacingUp = false;
                    isFacingLeft = false;
                    isFacingRight = false;
                    break;
            }

            randomMovementTimer = RANDOM_MOVEMENT_INTERVAL; // Reset the timer
        }

        // smooth movement for random movement
        this.positionX += randomMoveX * deltaTime;
        this.positionY += randomMoveY * deltaTime;

        if (this.positionX < 80) {
            this.positionX = 80; // Prevent going off the left side
        } else if (this.positionX > room.getRoomWidth() - this.width - 80) {
            this.positionX = room.getRoomWidth() - this.width - 80; // Prevent going off the right side
        }

        if (this.positionY < 80) {
            this.positionY = 80; // Prevent going off the bottom
        } else if (this.positionY > room.getRoomHeight() - this.height - 80) {
            this.positionY = room.getRoomHeight() - this.height - 80; // Prevent going off the top
        }

        updateBounds(); // Update the bounds after movement
    }

    /**
     * Draws Chub on the screen.
     *
     * @param batch The SpriteBatch used for rendering.
     * @param scale The scale factor for rendering the boss.
     */
    @Override
    public void render(SpriteBatch batch, float scale) {
        float scaledWidth = this.width * scale;
        float scaledHeight = this.height * scale;

        // Flip the frame if Chub is moving left
        boolean flipX = isFacingLeft;

        if(isDead){
            batch.draw(currentBodyFrame, positionX, positionY, scaledWidth, scaledHeight);
        }else {

            // If Chub is charging, use the corresponding attack head
            if (isCharging) {
                if (isFacingUp) {
                    currentHead = attackHeadRegions[2]; // Up
                } else if (isFacingLeft) {
                    currentHead = attackHeadRegions[1]; // Flip of right
                } else if (isFacingRight) {
                    currentHead = attackHeadRegions[1]; // Right
                } else if (isFacingDown) {
                    currentHead = attackHeadRegions[0]; // Down
                }
            } else {
                // If not charging, use the idle head
                if (isFacingUp) {
                    currentHead = idleHeadUpRegion;
                } else {
                    currentHead = idleHeadRegion;
                }
            }


            if (isFacingUp) {
                // Draw the head frame (idle or charge animation)
                batch.draw(
                    currentHead,
                    flipX ? positionX + scaledWidth : positionX, // Adjust position if flipped
                    positionY,
                    flipX ? -scaledWidth : scaledWidth, // Flip width if necessary
                    scaledHeight
                );
                drawBodySegments(batch, scaledWidth, scaledHeight);

            } else {

                // Body segments (seg1, seg2) drawn based on facing direction
                drawBodySegments(batch, scaledWidth, scaledHeight);

                // Draw the head frame (idle or charge animation)
                batch.draw(
                    currentHead,
                    flipX ? positionX + scaledWidth : positionX, // Adjust position if flipped
                    positionY,
                    flipX ? -scaledWidth : scaledWidth, // Flip width if necessary
                    scaledHeight
                );
            }
        }

    }

    private void drawBodySegments(SpriteBatch batch, float scaledWidth, float scaledHeight) {
        float segment1X = positionX;
        float segment1Y = positionY;

        float segment2X = positionX;
        float segment2Y = positionY;

        if (isFacingLeft) {
            segment1X += 80;  // seg1 is to the left of the head
            segment2X += scaledWidth - 10; // seg2 is further left
            // Draw the body segments
            batch.draw(seg2Region, segment2X, segment2Y, scaledWidth, scaledHeight);
            batch.draw(seg1Region, segment1X, segment1Y, scaledWidth, scaledHeight);
        } else if (isFacingRight) {
            segment1X -= 80;  // seg1 is to the right of the head
            segment2X -= scaledWidth - 30; // seg2 is further right
            // Draw the body segments
            batch.draw(seg2Region, segment2X, segment2Y, scaledWidth, scaledHeight);
            batch.draw(seg1Region, segment1X, segment1Y, scaledWidth, scaledHeight);
        }else if(isFacingUp){
            upDownSegRegion.flip(false, true);  // Flip vertically
            batch.draw(upDownSegRegion, positionX, positionY - 94, scaledWidth, scaledHeight);
            upDownSegRegion.flip(false, true);  // Restore original orientation after drawing
        }else{
            batch.draw(upDownSegRegion, positionX, positionY + 60, scaledWidth, scaledHeight);
        }
    }

    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
        if (this.health <= 0) {
            this.isDead = true;
        }
    }

    @Override
    public void playDeathAnimation() {
        if (this.deathAnim != null) {
            this.deathTime += Gdx.graphics.getDeltaTime();
            this.currentBodyFrame = deathAnim.getKeyFrame(deathTime, true);
        }
    }

    /**
     * Disposes of Chub's resources.
     */
    @Override
    public void dispose() {
        chubTexture.dispose();
    }
}
