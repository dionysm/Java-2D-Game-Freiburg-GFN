package io.github.tesgame.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DeathEffect {
    private Vector2 position;
    private Texture texture;
    private float timeToLive;
    private float maxTimeToLive;
    private float alpha = 1.0f;
    private float width, height;

    public DeathEffect(float x, float y, String texturePath, float timeToLive) {
        this.position = new Vector2(x, y);
        this.texture = new Texture(texturePath);
        this.timeToLive = timeToLive;
        this.maxTimeToLive = timeToLive;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void update(float delta) {
        timeToLive -= delta;

        // Berechne Alpha-Wert basierend auf der verbleibenden Zeit
        alpha = timeToLive / maxTimeToLive;
    }

    public void draw(SpriteBatch batch) {
        // Speichere den aktuellen Batch-Color
        float prevColor = batch.getColor().a;

        // Setze Alpha-Wert für Fade-Out
        batch.setColor(1, 1, 1, alpha);

        // Zeichne den Todeseffekt
        batch.draw(texture,
            position.x - width / 2, position.y - height / 2,
            width, height);

        // Stelle den ursprünglichen Alpha-Wert wieder her
        batch.setColor(1, 1, 1, prevColor);
    }

    public boolean isFinished() {
        return timeToLive <= 0;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
