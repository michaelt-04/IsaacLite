package github.tankgame.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Monster;
import github.tankgame.characters.Player;
import github.tankgame.environment.MapGenerator;
import github.tankgame.screens.DeathScreen;
import github.tankgame.screens.EndScreen;
import github.tankgame.screens.PauseScreen;
import github.tankgame.screens.StartScreen;
import github.tankgame.utils.CollisionDetector;
import github.tankgame.environment.Room;


// The main gameplay screen that updates and renders game elements
public class GameScreen extends ApplicationAdapter {
    private Music titleMusic;
    private Music introBackgroundMusic;
    private boolean isIntroCompleted; // To check if intro music is finished
    private Music backgroundMusic; // Declare Music variable
    private Music bossMusic;
    private Music bossOutroMusic;
    private Music deathMusicIntro, deathMusic;
    private int endCount = 0;
    private StartScreen startScreen;
    private boolean isGameStarted;
    private PauseScreen pauseScreen;
    private DeathScreen deathScreen;
    private EndScreen endScreen;
    private SpriteBatch batch;
    private Player player;
    private CollisionDetector collisionDetector;
    private Room room;
    private GameManager gameManager;
    private RoomManager roomManager;
    private HudManager hudManager;

    // Add monsters to the game
    private Array<Monster> monsters;

    /**
     * Initializes the game screen, loads resources, and sets up initial game state, including music, player, and rooms.
     */
    @Override
    public void create() {

        startScreen = new StartScreen();
        isGameStarted = false;
        isIntroCompleted = false;
        pauseScreen = new PauseScreen(this);
        deathScreen = new DeathScreen(this);
        endScreen = new EndScreen(this);

        batch = new SpriteBatch();
        player = new Player(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        hudManager = new HudManager();

        // Initialize the starting room and other rooms
        MapGenerator mapGenerator = new MapGenerator();

        roomManager = new RoomManager(mapGenerator);
        gameManager = new GameManager(player, roomManager);
        room = roomManager.getStartRoom();

        // Set the player's starting room
        player.setRoom(room);

        // Load music files
        titleMusic = Gdx.audio.newMusic(Gdx.files.internal("music/title_screen.ogg"));
        introBackgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/intro_background_music.ogg"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/background_music.ogg"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/boss_fight.ogg"));
        bossOutroMusic = Gdx.audio.newMusic(Gdx.files.internal("music/boss_outro.ogg"));
        deathMusicIntro = Gdx.audio.newMusic(Gdx.files.internal("music/isaac_died.ogg"));
        deathMusic = Gdx.audio.newMusic(Gdx.files.internal("music/died.ogg"));

        // Play title music on the start screen
        titleMusic.setLooping(true);
        titleMusic.setVolume(0.2f);
        titleMusic.play();

        // Optionally, set background music volume
        backgroundMusic.setVolume(0.2f); // Main music volume
        introBackgroundMusic.setVolume(0.2f); // Intro music volume
        deathMusic.setVolume(0.2f);
        deathMusic.setVolume(0.2f);
    }

    /**
     * Restarts the game, reinitializing game entities and resetting game states.
     * This is used to reset the game after the player dies or finishes the game.
     */
    public void restartGame() {
        // Reinitialize player and other entities
        player = new Player(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        player.setDefaultStats();

        if (backgroundMusic.isPlaying() || introBackgroundMusic.isPlaying()) {
            introBackgroundMusic.stop();
            backgroundMusic.stop();
            introBackgroundMusic.play();
            introBackgroundMusic.setLooping(false);
        }

        if (deathMusicIntro.isPlaying() || deathMusic.isPlaying()) {
            deathMusicIntro.stop();
            deathMusic.stop();
        }

        if(bossMusic.isPlaying() || bossOutroMusic.isPlaying()) {
            bossMusic.stop();
            bossOutroMusic.stop();
        }

        // Reset game managers
        MapGenerator mapGenerator = new MapGenerator();
        roomManager = new RoomManager(mapGenerator);
        gameManager = new GameManager(player, roomManager);
        room = roomManager.getStartRoom();
        player.setRoom(room);

        pauseScreen.togglePause(); // This will set isPauseActive to false, hiding the pause screen
    }

    /**
     * The main render method that is called every frame. It handles different game states (start screen, pause, death, gameplay, etc.)
     * and renders the appropriate game elements to the screen.
     */
    @Override
    public void render() {

        if (!isGameStarted) {
            startScreen.render();
            if (startScreen.isStartKeyPressed()) {
                isGameStarted = true;
                // Stop title music and start the intro background music when the game starts
                titleMusic.stop();
                introBackgroundMusic.play(); // Play intro music
                introBackgroundMusic.setLooping(false); // No looping for intro music

            } else if (startScreen.isExitKeyPressed()) {
                Gdx.app.exit();
            }
        } else if (deathScreen.isEndActive()) {

            // Ensure the background music is stopped
            if (backgroundMusic.isPlaying() || introBackgroundMusic.isPlaying()) {
                introBackgroundMusic.stop();
                backgroundMusic.stop();
            }

            if(bossMusic.isPlaying() || bossOutroMusic.isPlaying()) {
                bossMusic.stop();
                bossOutroMusic.stop();
            }

            // Handle death intro music and death music
            if (!deathMusic.isPlaying()) {
                deathMusicIntro.play();
                deathMusic.play();
                deathMusic.setLooping(true);
            }

            // When the game ends, render the game elements and then the end screen
            batch.begin();
            room.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 960, 640, 1);
            hudManager.render(batch, player);
            player.render(batch, 1);
            player.renderProjectiles(batch);
            batch.end();

            // Render the end screen on top
            deathScreen.render(batch);
        } else if (endScreen.isEndActive()) {
            // Render game elements behind the end screen
            batch.begin();
            room.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 960, 640, 1);
            hudManager.render(batch, player);
            player.render(batch, 1);
            player.renderProjectiles(batch);
            batch.end();

            if (endCount == 0) {
                bossMusic.stop();
                bossOutroMusic.play();
                bossMusic.setLooping(false);
                bossOutroMusic.setVolume(0.2f);
                endCount++;
            }

            // Render the end screen on top
            endScreen.render();
        } else if (pauseScreen.isPauseActive()) {
            // When the game is paused, we render the gameplay and then overlay the pause screen
            batch.begin();
            room.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 960, 640, 1);
            hudManager.render(batch, player);
            player.render(batch, 1);
            player.renderProjectiles(batch);
            batch.end();

            // Render the pause screen on top
            pauseScreen.render(batch);
        } else {

            if (!introBackgroundMusic.isPlaying() && !backgroundMusic.isPlaying()) {
                backgroundMusic.play();
                backgroundMusic.setLooping(true);
            }

            if (room.isBossRoom()) {
                backgroundMusic.stop();
                bossMusic.setVolume(0.2f);
                bossMusic.play();
                bossMusic.setLooping(true);
            }

            // Handle the escape key to toggle the pause state
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                pauseScreen.togglePause(); // Toggle pause when Escape is pressed
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            float delta = Gdx.graphics.getDeltaTime();

            gameManager.updateGame(delta);

            room = gameManager.handleRoomTransition();

            // Render the room and the player
            float windowWidth = Gdx.graphics.getWidth();
            float windowHeight = Gdx.graphics.getHeight();

            // Get room texture dimensions
            float roomWidth = room.getRoomTexture().getWidth();
            float roomHeight = room.getRoomTexture().getHeight();

            // Calculate scaling factors
            float scaleX = windowWidth / roomWidth;
            float scaleY = windowHeight / roomHeight;

            // Choose the smaller scale to preserve aspect ratio
            float scale = Math.min(scaleX, scaleY);

            // Calculate scaled dimensions
            float scaledWidth = roomWidth * scale;
            float scaledHeight = roomHeight * scale;

            batch.begin();
            room.render(batch, windowWidth, windowHeight, scaledWidth, scaledHeight, scale);
            hudManager.render(batch, player);
            player.render(batch, scale);
            player.renderProjectiles(batch);
            batch.end();

            // Check if player is dead, and trigger the end screen
            if (player.isDead()) {
                player.playDeathAnimation();
                deathScreen.setEndActive(true); // Show end screen when player is dead
            }

            if (room.isBossRoom() && room.getBoss() == null) {
                endScreen.setEndActive(true);
            }

        }
    }

    /**
     * Disposes of all game resources to prevent memory leaks. This method is called when the game is closed or changed.
     */
    @Override
    public void dispose() {
        startScreen.dispose();
        pauseScreen.dispose();
        deathScreen.dispose();
        endScreen.dispose();
        batch.dispose();
        player.dispose();
        room.dispose();
        hudManager.dispose();
        for (Monster monster : monsters) {
            monster.dispose();
        }
    }
}
