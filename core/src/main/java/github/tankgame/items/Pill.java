package github.tankgame.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import github.tankgame.characters.Character;

import java.util.Objects;

public class Pill extends PowerUp {
    private String stat; // The stat to modify
    private float value; // The amount to modify the stat by
    private Texture texture = new Texture(Gdx.files.internal("items/pills/pickup_007_pill.png"));
    private TextureRegion textureRegion;
    private Sound pillSound;

    public Pill(float x, float y, String stat, float value) {
        super(x, y);

        if(Objects.equals("health", stat)) {
            this.textureRegion = new TextureRegion(texture, 34, 0, 28, 28);
            this.pillSound = Gdx.audio.newSound(Gdx.files.internal("sounds/healthup.wav"));
        } else if (Objects.equals("speed", stat)) {
            this.textureRegion = new TextureRegion(texture, 34, 64, 28, 28);
            this.pillSound = Gdx.audio.newSound(Gdx.files.internal("sounds/speedup.wav"));
        }else if (Objects.equals("tears", stat)) {
            this.textureRegion = new TextureRegion(texture, 67, 32, 28, 28);
            this.pillSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tearsup.wav"));
        }

        this.stat = stat;
        this.value = value;
    }

    public String getStat(){
        return this.stat;
    }

    public void playPillSound(){
        this.pillSound.play();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, x, y, 40, 40);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void applyEffect(Character character) {
        switch (stat.toLowerCase()) {
            case "health":
                character.increaseHealth(value);
                break;
            case "speed":
                character.increaseSpeed(value);
                break;
            case "tears":
                character.increaseTears(value);
                break;
            default:
                System.out.println("Unknown pill stat: " + stat);
        }
    }
}

