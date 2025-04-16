package io.github.tesgame.Controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraController {
    private OrthographicCamera camera;

    public CameraController(float width, float height) {
        // Initialize the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    public void update(Vector2 playerPosition, float delta) {
        // Directly follow player without lerp effect
        camera.position.x = playerPosition.x;
        camera.position.y = playerPosition.y;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    // Method to convert screen to world coordinates
    public Vector2 screenToWorld(float screenX, float screenY) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(worldCoords.x, worldCoords.y);
    }
}
