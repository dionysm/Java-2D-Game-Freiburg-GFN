package io.github.tesgame.Combat;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 200f;
    private Texture texture;
    private float width, height;
    private float scale = 0.2f; // Scale factor for the sprite size
    private boolean isPlayerProjectile; // Whether the projectile is from player or enemy
    private Rectangle bounds; // For collision detection
    Sound projectileSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/ThrowStar.wav"));

    public Projectile(float x, float y, float targetX, float targetY, boolean isPlayerProjectile) {
        projectileSFX.play();
        position = new Vector2(x, y);
        this.isPlayerProjectile = isPlayerProjectile;

        // Load the projectile texture
        texture = new Texture("sprites/items/red-star.png");

        // Get dimensions and apply scaling
        width = texture.getWidth() * scale;
        height = texture.getHeight() * scale;

        // Create collision bounds
        bounds = new Rectangle(position.x - width/2, position.y - height/2, width, height);

        // Calculate direction towards the cursor
        Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
        velocity = direction.scl(speed);
    }

    // Constructor overload for backward compatibility
    public Projectile(float x, float y, float targetX, float targetY) {
        this(x, y, targetX, targetY, true); // By default, projectiles are from player
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);

        // Update collision bounds
        bounds.setPosition(position.x - width/2, position.y - height/2);
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

    // Get collision bounds
    public Rectangle getBounds() {
        return bounds;
    }

    // Check if this is a player projectile
    public boolean isPlayerProjectile() {
        return isPlayerProjectile;
    }

    public void dispose() {
        texture.dispose();
        projectileSFX.dispose();
    }
}
