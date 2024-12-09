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

public class EndScreen {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Texture endMenuTexture;
    private final Texture selectorTexture;
    private TextureRegion endMenu;
    private TextureRegion endText;
    private TextureRegion selector;
    private final Texture exitRestartTexture;
    private TextureRegion restartButton;
    private TextureRegion exitButton;
    private boolean isEndActive;
    private boolean retrySelected;
    private final GameScreen gameScreen;

    public EndScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        endMenuTexture = new Texture(Gdx.files.internal("hud/menus/endingsmenu.png"));

        endMenu = new TextureRegion(endMenuTexture,0,0, 220,210);
        endText = new TextureRegion(endMenuTexture,62,225,50,32);

        selectorTexture = new Texture(Gdx.files.internal("hud/menus/gamemenu.png"));

        exitRestartTexture = new Texture(Gdx.files.internal("hud/menus/backselectwidget.png"));
        exitButton = new TextureRegion(exitRestartTexture, 0, 126, 96, 86);
        restartButton = new TextureRegion(exitRestartTexture, 95, 125, 111, 122);

        selector = new TextureRegion(selectorTexture, 0, 304, 32, 32);
        isEndActive = false;
        retrySelected = true; // Default selection
    }

    public void render() {
        // Draw a semi-transparent overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw the end menu
        batch.begin();
        float menuX = (Gdx.graphics.getWidth() - 220) / 2f;
        float menuY = (Gdx.graphics.getHeight() - 210) / 2f;
        batch.draw(endMenu, menuX, menuY, 220, 210);
        batch.draw(endText, menuX+90, menuY+105, 50, 32);

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

        // Handle user input
        handleInput();
    }

    private void handleInput() {
        // Toggle selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            retrySelected = !retrySelected;
        }

        // Handle selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (retrySelected) {
                isEndActive = false;
                gameScreen.restartGame();
            } else {
                Gdx.app.exit(); // Exit game
            }
        }
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
        selectorTexture.dispose();
    }
}
