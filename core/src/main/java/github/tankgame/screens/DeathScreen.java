package github.tankgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import github.tankgame.core.GameScreen;

public class DeathScreen {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Texture endMenuTexture;
    private final Texture selectorTexture;

    private TextureRegion isaacRegion;
    private TextureRegion uglyRegion;
    private TextureRegion basementRegion;

    private final Texture exitRestartTexture;
    private TextureRegion endMenu;
    private TextureRegion restartButton;
    private TextureRegion exitButton;
    private TextureRegion selector;
    private boolean isEndActive;
    private boolean retrySelected;

    private GameScreen gameScreen;

    public DeathScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        endMenuTexture = new Texture(Gdx.files.internal("hud/menus/deathscreen.png"));
        selectorTexture = new Texture(Gdx.files.internal("hud/menus/gamemenu.png"));

        endMenu = new TextureRegion(endMenuTexture, 195, 0, 220, 256);
        isaacRegion = new TextureRegion(endMenuTexture, 0, 0, 32, 32);
        uglyRegion = new TextureRegion(endMenuTexture, 32, 32, 32, 32);
        basementRegion = new TextureRegion(endMenuTexture, 126, 0, 63, 32);

        exitRestartTexture = new Texture(Gdx.files.internal("hud/menus/backselectwidget.png"));
        exitButton = new TextureRegion(exitRestartTexture, 0, 126, 96, 86);
        restartButton = new TextureRegion(exitRestartTexture, 95, 125, 111, 122);


        selector = new TextureRegion(selectorTexture, 0, 304, 32, 32);
        isEndActive = false;
        retrySelected = true;
    }

    public void render(SpriteBatch gameBatch) {
        // Draw the gameplay overlay first, then the end menu

        // Draw a semi-transparent overlay to tint the game background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f)); // Black with 50% transparency
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw the end menu in the center
        batch.begin();
        float menuX = (Gdx.graphics.getWidth() - 385) / 2f;
        float menuY = (Gdx.graphics.getHeight() - 448) / 2f;
        batch.draw(endMenu, menuX, menuY, 385, 448);

        batch.draw(isaacRegion, menuX + 285, menuY + 320, 64, 64);
        batch.draw(uglyRegion, menuX + 220, menuY + 215, 64, 64);

        // Draw exit and restart buttons
        batch.draw(exitButton, 0, -20);
        batch.draw(restartButton, 850, -25);

        // Draw the selector with rotation based on selection
        float selectorX = retrySelected ? 890 : 25;
        float selectorY = retrySelected ? 7 : 0;

        // If "Retry" is selected, no rotation, if "Exit" is selected, rotate 90 degrees
        float rotationAngle = retrySelected ? 220 : 180;

        batch.draw(selector, selectorX, selectorY, 16, 16, 32, 32, 1, 1, rotationAngle); // Rotating the selector
        batch.end();

        // Handle input
        handleInput();
    }

    public void handleInput() {
        // Toggle selection with up/down arrow keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            retrySelected = !retrySelected;
        }

        // Handle enter key to select option
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (retrySelected) {
                isEndActive = false; // Hide the end screen
                restartGame();
            } else {
                Gdx.app.exit(); // Quit the game
            }
        }
    }

    private void restartGame() {
        gameScreen.restartGame();
    }

    public boolean isEndActive() {
        return isEndActive;
    }

    public void setEndActive(boolean isEndActive) {
        this.isEndActive = isEndActive;
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        endMenuTexture.dispose();
    }
}
