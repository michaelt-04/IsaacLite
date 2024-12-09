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

import java.util.Objects;

// Extends Monster, contains horf behaviors and stats
public class Horf extends Monster {
    private Texture headTexture;
    private Texture bodyTexture;
    private Texture deathTexture;
    private Animation<TextureRegion> bodyUpAnim, bodyDownAnim, bodyLeftAnim, bodyRightAnim;
    private TextureRegion head;
    private TextureRegion shootHead;
    private TextureRegion currentBody;
    private int width, height;
    private CollisionDetector collisionDetector;

    private Array<Projectile> projectiles;
    private float shootCooldown;
    private static final float SHOOT_INTERVAL = 0.3f;
    private float shootingStateTimer;
    private float shootDirectionTimer;
    private static final float SHOOTING_STATE_DURATION = 0.2f;
    private static final float POST_SHOOT_DIRECTION_DURATION = 0.4f;

    public Horf(float positionX, float positionY) {
        super(positionX, positionY, 32*2, 32*2);
        this.speed = 80*1.5f;
        this.health = 3;
        this.damage = 1;

        headTexture = new Texture(Gdx.files.internal("characters/monsters/horf_head_sheet.png"));
        bodyTexture = new Texture(Gdx.files.internal("characters/monsters/horf_body_sheet.png"));
        loadSprites();

        currentBody = bodyDownAnim.getKeyFrame(0);
    }

    private void loadSprites() {
        int headWidth = 32;
        int headHeight = 32;
        int bodyWidth = 32;
        int bodyHeight = 32;

        head = new TextureRegion(headTexture, 0, 0, headWidth, headHeight);
        bodyDownAnim = createAnimation(bodyTexture, 0, 0, bodyWidth, bodyHeight, 4, 0.1f, bodyWidth);
        bodyUpAnim = createAnimation(bodyTexture, 0, 32, bodyWidth, bodyHeight, 4, 0.1f, bodyWidth);
        bodyLeftAnim = createAnimation(bodyTexture, 0, 128, bodyWidth, bodyHeight, 4, 0.1f, bodyWidth);
        bodyRightAnim = createAnimation(bodyTexture, 0, 96, bodyWidth, bodyHeight, 4, 0.1f, bodyWidth);


    }

    private Animation<TextureRegion> createAnimation(Texture texture, int startX, int startY, int frameWidth, int frameHeight, int frameCount, float frameDuration, int frameJump) {
        Array<TextureRegion> frames = new Array<>();


        for (int i = 0; i < frameCount; i++) {
            if ((startX == 0 && startY == 128) || (startX == 0 && startY == 0)) {
                TextureRegion flipped = new TextureRegion(texture, startX + i * frameJump, startY, frameWidth, frameHeight);
                flipped.flip(true, false);
                frames.add(flipped);
            } else {
                frames.add(new TextureRegion(texture, startX + i * frameJump, startY, frameWidth, frameHeight));
            }
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void move(float playerX, float playerY, float deltaTime) {
        if (this.isDead) return;
        // Increment animation state time
        stateTime += deltaTime;

        // Calculate direction
        float directionX = playerX - this.positionX;
        float directionY = playerY - this.positionY;

        // Determine if moving
        isMoving = directionX != 0 || directionY != 0;

        // Calculate distance
        float magnitude = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Avoid division by zero
        if (magnitude == 0) return;

        // Normalize direction
        float normalizedX = directionX / magnitude;
        float normalizedY = directionY / magnitude;

        normalizedX = normalizedX * this.speed * deltaTime;
        normalizedY = normalizedY * this.speed * deltaTime;

        //System.out.println("normalizedX: " + normalizedX + " normalizedY: " + normalizedY);

        CollisionDetector collisionDetector = new CollisionDetector(this.room);

        // Check collision and move the horf
        float[] collisionMove = collisionDetector.checkCharacterTileCollision(this, normalizedX, normalizedY);
        //System.out.println("newX: " + collisionMove[0] + " newY: " + collisionMove[1]);
        float newX = collisionMove[0];
        float newY = collisionMove[1];

        // Stop chasing if close enough
        float stopDistance = 5.0f;
        if (magnitude > stopDistance) {
            // Update monster position

            setPosition(newX, newY);

            this.updateBounds();
        }

        // Animation logic
        if (isMoving) {
            if (Math.abs(directionX) > Math.abs(directionY)) {
                if (directionX > 0) {
                    currentBody = bodyRightAnim.getKeyFrame(stateTime, true);
                } else {
                    currentBody = bodyLeftAnim.getKeyFrame(stateTime, true);
                }
            } else {
                if (directionY > 0) {
                    currentBody = bodyUpAnim.getKeyFrame(stateTime, true);
                } else {
                    currentBody = bodyDownAnim.getKeyFrame(stateTime, true);
                }
            }
        } else {
            // Reset to standing pose or idle animation
            currentBody = bodyDownAnim.getKeyFrame(0); // or bodyIdleAnim.getKeyFrame(stateTime, true);
        }
    }

    @Override
    public void render(SpriteBatch batch, float scale) {

        // Calculate offsets and scaling
        float windowWidth = Gdx.graphics.getWidth();
        float windowHeight = Gdx.graphics.getHeight();

        float roomWidth = room.getRoomTexture().getWidth();
        float roomHeight = room.getRoomTexture().getHeight();

        // Scaled dimensions of the room based on window size
        float scaledWidth = roomWidth * scale;
        float scaledHeight = roomHeight * scale;

        // Centering offsets for the room
        float offsetX = (windowWidth - scaledWidth) / 2f;
        float offsetY = (windowHeight - scaledHeight) / 2f;

        // Draw the player (scaled)
        float playerScaledX = this.getPositionX() * scale;
        float playerScaledY = this.getPositionY() * scale;
        float playerScaledWidth = this.getWidth() * scale;
        float playerScaledHeight = this.getHeight() * scale;

        if (this.isDead) {
            batch.draw(this.currentBody, this.positionX, this.positionY, playerScaledWidth, playerScaledHeight);
        } else {
            batch.draw(this.currentBody, this.positionX, this.positionY, playerScaledWidth, playerScaledHeight);
            batch.draw(this.head, this.positionX, this.positionY + this.currentBody.getRegionHeight() - 4, playerScaledWidth, playerScaledHeight);
        }

    }

    @Override
    public void playDeathAnimation() {
        if (this.deathAnim != null) {
            this.deathTime += Gdx.graphics.getDeltaTime();
            this.currentBody = deathAnim.getKeyFrame(deathTime, true);
        }

    }

    @Override
    public void dispose() {
        headTexture.dispose();
        bodyTexture.dispose();

        if (projectiles != null) {
            disposeProjectiles();
        }

    }

    @Override
    public void disposeProjectiles() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
    }
}

