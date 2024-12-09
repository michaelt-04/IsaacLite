package github.tankgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

// Handles input processing and key bindings
public class InputHandler {

    // Method to get movement deltas based on key presses
    public float[] getMovementDeltas() {
        float deltaX = 0, deltaY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) deltaX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) deltaX = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) deltaY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) deltaY = -1;

        return new float[]{deltaX, deltaY};
    }

    public boolean isBombKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }

    public float[] getShootDeltas() {
        float deltaX = 0, deltaY = 0;

        // Detect key presses for shooting directions
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) deltaX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) deltaX = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) deltaY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) deltaY = -1;

        // Restrict to cardinal directions only
        if (deltaX != 0 && deltaY != 0) {
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                deltaY = 0;
            } else {
                deltaX = 0;
            }
        }

        return new float[]{deltaX, deltaY};
    }

}
