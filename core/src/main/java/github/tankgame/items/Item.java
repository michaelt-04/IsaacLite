package github.tankgame.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import github.tankgame.characters.Character;

public class Item extends PowerUp {
    private String stats[]; // The stat to modify
    private float value; // The amount to modify the stat by
    private Texture appearance; // New appearance to apply to the character
    private Texture icon;

    public Item(float x, float y, String[] stats, float value) {
        super(x, y);
        this.stats = stats;
        this.value = value;

        switch (stats[0]){
            case "health":
                this.appearance = new Texture(Gdx.files.internal("characters/player/costumes/meat_costume.png"));
                this.icon = new Texture(Gdx.files.internal("items/items/collectibles_193_meat.png"));
                break;
            case "bomb":
                this.appearance = new Texture(Gdx.files.internal("characters/player/costumes/pyro_costume.png"));
                this.icon = new Texture(Gdx.files.internal("items/items/collectibles_190_pyro.png"));
                break;
            case "triple":
                this.appearance = new Texture(Gdx.files.internal("characters/player/costumes/theinnereye_costume.png"));
                this.icon = new Texture(Gdx.files.internal("items/items/collectibles_002_theinnereye.png"));
            default:
                System.out.println("Unknown stat: " + stats[0]);
        }
    }

    @Override
    public void applyEffect(Character character) {
        // Apply stat changes
        for(String stat : stats) {
            switch (stat.toLowerCase()) {
                case "health":
                    character.increaseHealth(value);
                    break;
                case "bomb":
                    character.increaseBombs(value);
                    break;
                case "damage":
                    character.increaseDamage(value);
                    break;
                case "triple":
                    character.setHasTripleShot(true);
                    break;
                default:
                    System.out.println("Unknown stat: " + stat);
            }
        }

        // Change appearance
        character.setAppearance(appearance, stats[0]);
    }

    public String[] getStats(){
        return this.stats;
    }

    @Override
    public void render(SpriteBatch batch) {
        //System.out.println("x: " + x + " y: " + y);
        batch.draw(icon, x - 12, y, 40, 40);
    }

    @Override
    public void dispose() {
    }
}
