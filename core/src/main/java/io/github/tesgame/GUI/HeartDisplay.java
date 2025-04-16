package io.github.tesgame.GUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HeartDisplay {
    private Texture fullHeart;
    private Texture halfHeart;
    private Texture emptyHeart;
    private int maxHearts;
    private int heartSize = 32;
    private int padding = 4;

    public HeartDisplay() {
        maxHearts = 5;

        // Create hearts programmatically
        fullHeart = createHeartTexture(Color.RED);
        halfHeart = createHalfHeartTexture();
        emptyHeart = createHeartTexture(Color.DARK_GRAY);
    }

    private Texture createHeartTexture(Color color) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);

        // Draw a simple heart shape
        pixmap.fillCircle(8, 8, 8);
        pixmap.fillCircle(24, 8, 8);
        pixmap.fillTriangle(0, 12, 32, 12, 16, 32);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createHalfHeartTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);

        // Draw background half
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fillCircle(8, 8, 8);
        pixmap.fillCircle(24, 8, 8);
        pixmap.fillTriangle(0, 12, 32, 12, 16, 32);

        // Draw foreground half
        pixmap.setColor(Color.RED);
        pixmap.fillCircle(8, 8, 8);
        pixmap.fillTriangle(0, 12, 16, 12, 16, 32);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void draw(SpriteBatch batch, int health) {
        // Save the original batch projection matrix (for restoring later)
        batch.end();

        // Use a fresh SpriteBatch for HUD elements that doesn't use the camera transformation
        SpriteBatch hudBatch = new SpriteBatch();
        hudBatch.begin();

        for (int i = 0; i < maxHearts; i++) {
            int heartHealth = health - (i * 2);
            Texture heartTexture;

            if (heartHealth >= 2) {
                heartTexture = fullHeart;
            } else if (heartHealth == 1) {
                heartTexture = halfHeart;
            } else {
                heartTexture = emptyHeart;
            }

            hudBatch.draw(heartTexture, padding + i * (heartSize + padding),
                padding, heartSize, heartSize);
        }

        hudBatch.end();

        // Restore the original batch
        batch.begin();
    }

    public void dispose() {
        fullHeart.dispose();
        halfHeart.dispose();
        emptyHeart.dispose();
    }
}
