package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnemyProjectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 300f;
    private Texture texture;
    private float width, height;
    private float scale = 0.5f;
    private float distanceTraveled = 0;
    private Vector2 startPosition;

    public EnemyProjectile(float x, float y, float targetX, float targetY, String type) {
        position = new Vector2(x, y);
        startPosition = new Vector2(x, y);

        // Create a simple bone texture programmatically
        Pixmap pixmap = new Pixmap(16, 8, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, 16, 8);
        texture = new Texture(pixmap);
        pixmap.dispose();

        // Get dimensions and apply scaling
        width = texture.getWidth() * scale;
        height = texture.getHeight() * scale;

        // Calculate direction towards the target
        Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
        velocity = direction.scl(speed);
    }

    public void update(float delta) {
        // Update position
        position.add(velocity.x * delta, velocity.y * delta);

        // Track distance traveled
        distanceTraveled = position.dst(startPosition);
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

    public boolean checkCollision(Player player) {
        // Calculate player's bounding box
        Rectangle playerRect = new Rectangle(
            player.getX(), player.getY(),
            player.getWidth(), player.getHeight()
        );

        // Calculate projectile's bounding box
        Rectangle projectileRect = new Rectangle(
            position.x - width/2, position.y - height/2,
            width, height
        );

        return projectileRect.overlaps(playerRect);
    }

    public float getDistanceTraveled() {
        return distanceTraveled;
    }

    public void dispose() {
        texture.dispose();
    }
}
