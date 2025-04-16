package io.github.tesgame.Combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;

public class AOEEffect {
    private Vector2 position;
    private float radius;
    private Texture texture;
    private float duration;
    private float currentTime = 0;
    private float damageInterval = 1.0f; // Damage every half second
    private float lastDamageTime = 0;
    private int damage = 2;

    public AOEEffect(float x, float y, float radius, float duration) {
        this.position = new Vector2(x, y);
        this.radius = radius;
        this.duration = duration;

        // Create a circular texture for the AOE zone
        createAOETexture();
    }

    private void createAOETexture() {
        int size = (int)(radius * 2) + 4; // Add some padding
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Set a semi-transparent blue color for the effect
        pixmap.setColor(0.2f, 0.4f, 0.9f, 0.5f); // Light blue fill
        pixmap.fillCircle(size/2, size/2, (int)radius);

        // Add border
        pixmap.setColor(0, 0.2f, 0.8f, 0.8f); // Darker blue border
        pixmap.drawCircle(size/2, size/2, (int)radius);

        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float delta, Player player) {
        currentTime += delta;

        // Check if player is in the AOE
        if (isPlayerInside(player)) {
            // Apply damage on intervals
            lastDamageTime += delta;
            if (lastDamageTime >= damageInterval) {
                player.takeDamage(damage);
                player.applySlowEffect(2f, 0.5f); // 2 seconds, 50% slow
                lastDamageTime = 0;
            }
        }
    }

    public boolean isPlayerInside(Player player) {
        Vector2 playerPos = player.getPosition();
        Circle aoeCircle = new Circle(position.x, position.y, radius);
        return aoeCircle.contains(playerPos.x, playerPos.y);
    }

    public void draw(SpriteBatch batch) {
        // Calculate pulsing effect based on time
        float scale = 1.0f + 0.1f * (float)Math.sin(currentTime * 5);

        batch.draw(texture,
            position.x - radius * scale,
            position.y - radius * scale,
            radius * 2 * scale,
            radius * 2 * scale);
    }

    public boolean isFinished() {
        return currentTime >= duration;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
