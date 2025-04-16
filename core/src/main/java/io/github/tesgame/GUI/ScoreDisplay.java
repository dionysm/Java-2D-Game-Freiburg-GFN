package io.github.tesgame.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

public class ScoreDisplay {
    private final BitmapFont font;
    private int score = 0;

    public ScoreDisplay() {
        font = new BitmapFont(); // Uses default LibGDX font
        font.setColor(Color.WHITE);
        font.getData().setScale(2); // Make the text larger
    }

    public void draw(SpriteBatch batch) {
        // Use a fresh SpriteBatch for HUD elements
        SpriteBatch hudBatch = new SpriteBatch();
        hudBatch.begin();

        // Draw score in top right corner
        String scoreText = "SCORE: " + score;
        font.draw(hudBatch, scoreText,
            Gdx.graphics.getWidth() - 150,
            Gdx.graphics.getHeight() - 20);

        hudBatch.end();

    }

    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public void dispose() {
        font.dispose();
    }
}
