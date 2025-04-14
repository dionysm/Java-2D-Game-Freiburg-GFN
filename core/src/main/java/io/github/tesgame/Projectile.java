package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 400f;
    private Texture texture;
    private float width, height;
    private float scale = 0.5f; // Add scale factor (0.5 = half size)

    public Projectile(float x, float y, float targetX, float targetY) {
        position = new Vector2(x, y);

        // Load the projectile texture
        texture = new Texture("sprites/red-star.png");

        // Get dimensions and apply scaling
        width = texture.getWidth() * scale;
        height = texture.getHeight() * scale;

        // Calculate direction towards the cursor
        Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
        velocity = direction.scl(speed);
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void draw(SpriteBatch batch) {
        // Draw the projectile centered on its position with scaling
        batch.draw(texture,
            position.x - width/2, position.y - height/2, // Position (centered)
            0, 0,                                        // Origin for rotation
            texture.getWidth(), texture.getHeight(),     // Original dimensions
            scale, scale,                                // Scale factors
            0,                                           // Rotation
            0, 0,                                        // Source rectangle position
            texture.getWidth(), texture.getHeight(),     // Source rectangle size
            false, false);                               // Flip horizontally/vertically
    }

    // Get position for collision detection
    public Vector2 getPosition() {
        return position;
    }

    // Get width for collision detection
    public float getWidth() {
        return width;
    }

    // Get height for collision detection
    public float getHeight() {
        return height;
    }

    public void dispose() {
        texture.dispose();
    }
}
