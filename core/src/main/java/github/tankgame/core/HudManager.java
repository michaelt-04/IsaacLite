package github.tankgame.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import github.tankgame.characters.Player;

public class HudManager {
    private Texture heartTexture, pickupsTexture, fontTexture;
    private TextureRegion fullHeartTexture, halfHeartTexture, emptyHeartTexture;
    private TextureRegion bombTexture;
    private TextureRegion zeroTexture, oneTexture, twoTexture, threeTexture,
        fourTexture, fiveTexture, sixTexture,
        sevenTexture, eightTexture, nineTexture;
    private Player player;

    private static final int HEART_WIDTH = 28; // Width of a heart icon
    private static final int HEART_HEIGHT = 28; // Height of a heart icon
    private static final int HUD_MARGIN = 80; // Distance from the screen edges
    private static final int BOMB_ICON_SIZE = 36; // Bomb icon size

    public HudManager() {
        heartTexture = new Texture(Gdx.files.internal("hud/ui_hearts.png"));
        pickupsTexture = new Texture(Gdx.files.internal("hud/hudpickups.png"));
        fontTexture = new Texture(Gdx.files.internal("hud/terminus8_0.png"));

        fullHeartTexture = new TextureRegion(heartTexture, 0, 0, 15, 15);
        halfHeartTexture = new TextureRegion(heartTexture, 16, 0, 15, 15);
        emptyHeartTexture = new TextureRegion(heartTexture, 31, 0, 15, 15);

        bombTexture = new TextureRegion(pickupsTexture, 0, 16, 16, 16);
        zeroTexture = new TextureRegion(fontTexture, 87, 23, 8, 12);
        oneTexture = new TextureRegion(fontTexture, 104, 45, 7, 12);
        twoTexture = new TextureRegion(fontTexture, 95, 23, 8, 12);
        threeTexture = new TextureRegion(fontTexture, 103, 23, 8, 12);
        fourTexture = new TextureRegion(fontTexture, 112, 23, 8, 12); // Example position
        fiveTexture = new TextureRegion(fontTexture, 119, 23, 8, 12); // Example position
        sixTexture = new TextureRegion(fontTexture, 127, 23, 8, 12); // Example position
        sevenTexture = new TextureRegion(fontTexture, 135, 23, 8, 12); // Example position
        eightTexture = new TextureRegion(fontTexture, 143, 23, 8, 12); // Example position
        nineTexture = new TextureRegion(fontTexture, 151, 34, 8, 12); // Example position
    }

    public void render(Batch batch, Player player) {
        this.player = player; // Store reference for access

        // Render hearts
        renderHearts(batch);

        // Render bombs
        renderBombs(batch);
    }

    private void renderHearts(Batch batch) {
        float playerHealth = player.getHealth();
        int maxHealth = (int) player.getMaxHealth(); // Maximum health, replace with your actual value

        int heartsToDisplay = (int) Math.ceil(maxHealth / 2.0); // Number of heart slots

        for (int i = 0; i < heartsToDisplay; i++) {
            TextureRegion currentHeart;

            if (playerHealth >= (i + 1) * 2) {
                currentHeart = fullHeartTexture;
            } else if (playerHealth > i * 2) {
                currentHeart = halfHeartTexture;
            } else {
                currentHeart = emptyHeartTexture;
            }

            // Calculate heart position
            int x = HUD_MARGIN + i * (HEART_WIDTH - 5);
            int y = Gdx.graphics.getHeight() - 5 - HEART_HEIGHT;

            // Draw the heart
            batch.draw(currentHeart, x, y, HEART_WIDTH, HEART_HEIGHT);
        }
    }

    private void renderBombs(Batch batch) {
        int bombCount = player.getCurrentBombs();

        // Bomb icon position
        int bombX = 0;
        int bombY = Gdx.graphics.getHeight() - (HEART_HEIGHT + 30 + BOMB_ICON_SIZE); // Position below hearts

        // Draw the bomb icon
        batch.draw(bombTexture, bombX, bombY, BOMB_ICON_SIZE, BOMB_ICON_SIZE);

        // Number position
        int numberX = bombX + BOMB_ICON_SIZE;
        int numberY = bombY + 5;

        // Get the tens and units digits
        int tens = bombCount / 10; // Tens digit
        int units = bombCount % 10; // Units digit

        // Select textures for each digit
        TextureRegion tensTexture = getDigitTexture(tens);
        TextureRegion unitsTexture = getDigitTexture(units);

        // Draw the tens digit
        batch.draw(tensTexture, numberX, numberY, 16, 24);

        // Draw the units digit
        batch.draw(unitsTexture, numberX + 14, numberY, 16, 24);
    }

    private TextureRegion getDigitTexture(int digit) {
        switch (digit) {
            case 0: return zeroTexture;
            case 1: return oneTexture;
            case 2: return twoTexture;
            case 3: return threeTexture;
            case 4: return fourTexture;
            case 5: return fiveTexture;
            case 6: return sixTexture;
            case 7: return sevenTexture;
            case 8: return eightTexture;
            case 9: return nineTexture;
            default: return zeroTexture; // Fallback
        }
    }

    public void dispose() {
        heartTexture.dispose();
        pickupsTexture.dispose();
        fontTexture.dispose();
    }
}
