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

public class PauseScreen {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Texture pauseMenuTexture;
    private TextureRegion pauseMenu;
    private Texture restartButtonTexture;
    private TextureRegion restartButton;
    private TextureRegion selector;
    private boolean isPauseActive;
    private int selectedOption; // 0 for "Resume", 1 for "Exit", 2 for "Restart"
    private GameScreen gameScreen;

    public PauseScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        pauseMenuTexture = new Texture(Gdx.files.internal("hud/menus/pausescreen.png"));
        pauseMenu = new TextureRegion(pauseMenuTexture, 0, 0, 236, 205);

        restartButtonTexture = new Texture(Gdx.files.internal("hud/menus/backselectwidget.png"));
        restartButton = new TextureRegion(restartButtonTexture, 95, 125, 111, 122);

        selector = new TextureRegion(pauseMenuTexture, 240, 1, 16, 16);
        isPauseActive = false;
        selectedOption = 0; // Default selection is "Resume"
    }

    public void render(SpriteBatch gameBatch) {
        // Draw a semi-transparent overlay to tint the game background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f)); // Black with 50% transparency
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw the pause menu in the center
        batch.begin();
        float menuX = (Gdx.graphics.getWidth() - 354) / 2f;
        float menuY = (Gdx.graphics.getHeight() - 308) / 2f;
        batch.draw(pauseMenu, menuX, menuY, 354, 308);

        // Draw options: "Resume", "Exit", "Restart"
        float optionX = menuX + 100;
        float resumeY = menuY + 135 - 60;
        float exitY = resumeY - 60;
        float restartY = 7;

        // Draw the restart button (which is the third option)
        batch.draw(restartButton, 850, -25);

        // Draw the selector next to the selected option
        if (selectedOption == 0) { // "Resume" selected
            batch.draw(selector, optionX - 30, resumeY - 5, 16, 16);
        } else if (selectedOption == 1) { // "Exit" selected
            batch.draw(selector, menuX + 85, resumeY - 40, 16, 16);
        } else if (selectedOption == 2) { // "Restart" selected
            batch.draw(selector, 890, 7, 32, 32);
        }

        batch.end();

        // Handle input
        handleInput();
    }

    public void handleInput() {
        // Move the selector with up/down arrow keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectedOption > 0) {
                selectedOption--; // Move selector up
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectedOption < 2) {
                selectedOption++; // Move selector down
            }
        }

        // Handle enter key to select option
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedOption == 0) {
                isPauseActive = false; // Resume the game
            } else if (selectedOption == 1) {
                Gdx.app.exit(); // Exit the game
            } else if (selectedOption == 2) {
                restartGame(); // Restart the game
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPauseActive = false;
        }
    }

    private void restartGame() {
        gameScreen.restartGame();
    }

    public boolean isPauseActive() {
        return isPauseActive;
    }

    public void togglePause() {
        isPauseActive = !isPauseActive;
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        pauseMenuTexture.dispose();
    }
}
