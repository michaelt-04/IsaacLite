package github.tankgame.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import github.tankgame.characters.Character;

// The Item class represents a power-up item that modifies certain stats of a character, and applies a change in appearance
public class Item extends PowerUp {
    private String stats[]; // The stats to modify
    private float value; // The amount to modify the stat by
    private Texture appearance; // New appearance to apply to the character
    private Texture icon;

    // Constructor to initialize the item with its position, stats, and value.
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
                System.out.println("Unknown item stat: " + stats[0]);
        }
    }

    // Applies the effect of the item on the given character.
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

    // Renders the item on the screen using the given SpriteBatch
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(icon, x - 12, y, 40, 40);
    }

    @Override
    public void dispose() {
    }
}
