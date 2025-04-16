package io.github.tesgame.Combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Controller.CameraController;

public class Crosshair {
    private Texture texture;
    private Vector2 position;
    private float scale = 0.5f;
    private float width, height;

    public Crosshair() {
        texture = new Texture("sprites/crosshair.png");
        position = new Vector2(0, 0);
        width = texture.getWidth() * scale;
        height = texture.getHeight() * scale;
    }

    public void update(CameraController cameraController) {
        // Get mouse position in screen coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Convert to world coordinates using the camera
        Vector2 worldPos = cameraController.screenToWorld(mouseX, mouseY);
        position.x = worldPos.x;
        position.y = worldPos.y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture,
            position.x - width/2, position.y - height/2,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            scale, scale,
            0,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        texture.dispose();
    }
}
