package github.tankgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StartScreen {

    private final SpriteBatch batch;
    private final Texture spriteSheet, exitButton;
    private Texture titleTexture;
    private TextureRegion titleRegion;
    private final TextureRegion backgroundRegion;
    private final TextureRegion startOptionRegion;
    private final TextureRegion exitOptionRegion;
    private final TextureRegion selectorRegion;

    private boolean startSelected; // Tracks which option is selected

    public StartScreen() {
        batch = new SpriteBatch();

        // Load the sprite sheet
        titleTexture = new Texture(Gdx.files.internal("hud/menus/title.png"));
        spriteSheet = new Texture(Gdx.files.internal("hud/menus/gamemenu.png"));
        exitButton = new Texture(Gdx.files.internal("hud/menus/endingsmenu.png"));

        // Define regions for the background and options
        titleRegion = new TextureRegion(titleTexture);
        backgroundRegion = new TextureRegion(spriteSheet, 0, 0, 480, 270);
        startOptionRegion = new TextureRegion(spriteSheet, 32, 283, 116, 59);
        selectorRegion = new TextureRegion(spriteSheet, 0, 304, 32, 32);
        exitOptionRegion = new TextureRegion(exitButton, 415, 96, 45, 30);

        startSelected = true; // Default selection
    }

    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw the background
        batch.draw(backgroundRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the title with rotation
        float titleX = Gdx.graphics.getWidth() / 4;
        float titleY = Gdx.graphics.getHeight() - 225;
        float titleOriginX = titleTexture.getWidth() / 2f; // Center of the title
        float titleOriginY = titleTexture.getHeight() / 2f; // Center of the title
        float titleScaleX = 1f;
        float titleScaleY = 1f;
        float titleRotation = 7f; // Rotate counterclockwise by 10 degrees

        batch.draw(
            titleRegion,
            titleX,
            titleY,
            titleOriginX,
            titleOriginY,
            titleTexture.getWidth(),
            titleTexture.getHeight(),
            titleScaleX,
            titleScaleY,
            titleRotation
        );

        // Draw the "New Run" option
        batch.draw(startOptionRegion, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 30,
            116, 59);

        // Draw the "Exit" option with rotation
        float exitX = Gdx.graphics.getWidth() / 2 - 90;
        float exitY = Gdx.graphics.getHeight() / 2 - 10;
        float originX = 22.5f;
        float originY = 15f;
        float width = 45;
        float height = 30;
        float scaleX = 1f;
        float scaleY = 1f;
        float rotation = 15f; // Rotate counterclockwise by 15 degrees

        batch.draw(exitOptionRegion, exitX, exitY, originX, originY, width, height, scaleX, scaleY, rotation);

        // Draw the selector arrow next to the selected option
        float arrowX = Gdx.graphics.getWidth() / 2 - 140;
        float arrowY = startSelected
            ? Gdx.graphics.getHeight() / 2 + 30 // Align with "New Run" option
            : Gdx.graphics.getHeight() / 2 - 15; // Align with "Exit" option

        batch.draw(selectorRegion, arrowX, arrowY, 32, 32);

        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            startSelected = !startSelected; // Toggle selection
        }
    }

    public boolean isStartKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && startSelected;
    }

    public boolean isExitKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !startSelected;
    }

    public void dispose() {
        batch.dispose();
        spriteSheet.dispose();
    }
}
