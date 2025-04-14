package io.github.tesgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraController {
    private OrthographicCamera camera;
    private float lerpSpeed = 5f; // Speed of camera movement
    private float viewportWidth;
    private float viewportHeight;

    public CameraController(float width, float height) {
        // Initialize the camera
        camera = new OrthographicCamera();
        viewportWidth = width;
        viewportHeight = height;
        camera.setToOrtho(false, viewportWidth, viewportHeight);  // Set the camera's viewport size
    }

    public void update(Vector2 playerPosition, float delta) {
        // Calculate target position (center the player)
        float targetX = playerPosition.x + viewportWidth / 2;
        float targetY = playerPosition.y + viewportHeight / 2;

        // Smoothly interpolate camera movement
        camera.position.x += (targetX - camera.position.x) * lerpSpeed * delta;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed * delta;

        // Optional: clamp the camera's position to certain boundaries
        camera.position.x = Math.max(viewportWidth / 2, Math.min(camera.position.x, 1600 - viewportWidth / 2));
        camera.position.y = Math.max(viewportHeight / 2, Math.min(camera.position.y, 1200 - viewportHeight / 2));

        // Update the camera
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
