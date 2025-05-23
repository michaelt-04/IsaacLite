package github.tankgame.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Character;
import github.tankgame.characters.Monster;
import github.tankgame.characters.Player;
import com.badlogic.gdx.audio.Sound;

// Represents projectiles (tears) with properties like speed, direction, damage
public class Projectile {
    // Enum representing the state of the projectile (MOVING or COLLIDING)
    private enum State {
        MOVING, COLLIDING
    }

    private Character character;

    private State state = State.MOVING;
    private float collisionTimeElapsed = 0;
    private boolean isCollided = false;
    private float damage;

    private Texture texture;
    private TextureRegion projectile;
    private Animation<TextureRegion> collisionAnimation;
    private float speed = 200*1.25f; // Speed of projectile
    private float directionX, directionY;
    private float positionX, positionY;
    private int projWidth = 20*2, projHeight = 20*2;
    private float deltaX, deltaY;

    private Sound collisionSound;

    // Constructor to initialize a projectile
    public Projectile(float startX, float startY, float dirX, float dirY, float damage, Character character) {
        this.character = character;
        if(character instanceof Player) {
            texture = new Texture(Gdx.files.internal("projectiles/tear_sprite_sheet.png"));
        }else if(character instanceof Monster) {
            texture = new Texture(Gdx.files.internal("projectiles/monster_tear_sprite_sheet.png"));
        }
        loadSprites();
        this.collisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tear.wav"));
        this.positionX = startX;
        this.positionY = startY;
        setDirection(dirX, dirY);
        this.deltaX = dirX;
        this.deltaY = dirY;
        this.damage = damage;
    }

    public void loadSprites() {
        projectile = new TextureRegion(texture, 0, 0, 68, 68);
        collisionAnimation = createAnimation(texture, 0, 0, 68, 68, 4, 0.05f);
    }

    // Creates an animation from a texture
    private Animation<TextureRegion> createAnimation(Texture texture, int startX, int startY, int frameWidth, int frameHeight, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();

        for (int row = 0; row < frameCount; row++) {
            for (int col = 0; col < frameCount; col++) {
                frames.add(new TextureRegion(texture, startX + col * 64, startY, frameWidth, frameHeight));
            }
            startY += 64;
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
    }

    // Sets the direction vector for the projectile and normalizes it
    public void setDirection(float deltaX, float deltaY) {
        float magnitude = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        this.directionX = deltaX / magnitude;
        this.directionY = deltaY / magnitude;
    }

    // Updates the projectile's position and state
    public void update(float delta) {
        if (this.state == State.MOVING) {
            this.positionX += directionX * speed * delta;
            this.positionY += directionY * speed * delta;
        } else if (this.state == State.COLLIDING) {
            this.collisionTimeElapsed += delta;
            this.damage = 0;

            if (collisionAnimation.isAnimationFinished(this.collisionTimeElapsed)) {
                this.isCollided = true; // Mark projectile for removal
            }
        }
    }

    // Triggers the collision state and plays the collision sound
    public void onCollision() {
        if (this.state == State.MOVING) {
            this.state = State.COLLIDING;
            this.collisionSound.play(0.6f);
            this.collisionTimeElapsed = 0;
            if(this.character instanceof Player) {
                this.damage = 0;
            }
        }
    }

    public String getState(){
        return this.state.toString();
    }

    public float getDamage(){
        return this.damage;
    }

    public boolean isCollided() {
        return isCollided;
    }

    public float getPositionX() {
        return this.positionX;
    }

    public float getPositionY() {
        return this.positionY;
    }

    public float getDeltaX() {
        return this.deltaX;
    }

    public float getDeltaY() {
        return this.deltaY;
    }

    // Draws the projectile or its collision animation
    public void draw(Batch batch) {
        if (this.state == State.MOVING) {
            batch.draw(projectile, this.positionX, this.positionY, 68*1.5f, 68*1.5f);
        } else if (this.state == State.COLLIDING) {
            TextureRegion frame = collisionAnimation.getKeyFrame(this.collisionTimeElapsed, true);
            batch.draw(frame, this.positionX, this.positionY, 68*1.5f, 68*1.5f);
        }
    }

    public void dispose() {
        texture.dispose();
    }

    public int getProjWidth() {
        return projWidth;
    }

    public int getProjHeight() {
        return projHeight;
    }

    public Texture getTexture() {
        return texture;
    }
}
