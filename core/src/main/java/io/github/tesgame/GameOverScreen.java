package io.github.tesgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.audio.Sound;

public class GameOverScreen extends InputAdapter implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private final Main game; // Annahme: Deine Hauptklasse heißt TesGame

    private final String message = "GAME OVER";
    private final String restartMessage = "Drücke LEERTASTE zum Neustart";
    private GlyphLayout restartLayout;

    private final float padding = 20f;
    private final float boxAlpha = 0.8f;

    private Sound gameOverSound;
    private boolean soundPlayed = false;

    public GameOverScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().setScale(3); // Größere Schrift für GAME OVER

        glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);

        // Kleinere Schrift für den Neustart-Text
        restartLayout = new GlyphLayout();
        font.getData().setScale(1.5f);
        restartLayout.setText(font, restartMessage);
        font.getData().setScale(3); // Zurück zur größeren Schrift für das Rendering

        // Setze Input Processor
        Gdx.input.setInputProcessor(this);

        // Lade den Game Over Sound (falls vorhanden)
        try {
            gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover.ogg"));
        } catch (Exception e) {
            System.out.println("Game over sound konnte nicht geladen werden: " + e.getMessage());
        }
    }

    @Override
    public void render(float delta) {
        // Spiele den Sound einmal ab
        if (!soundPlayed && gameOverSound != null) {
            gameOverSound.play();
            soundPlayed = true;
        }

        // Bildschirm leeren
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Position und Größe der Box berechnen
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float messageX = (screenWidth - glyphLayout.width) / 2;
        float messageY = (screenHeight + glyphLayout.height) / 2 + 30; // Etwas höher platzieren

        float boxWidth = Math.max(glyphLayout.width, restartLayout.width) + padding * 2;
        float boxHeight = glyphLayout.height + restartLayout.height + padding * 3; // Extra padding für den zweiten Text
        float boxX = (screenWidth - boxWidth) / 2;
        float boxY = messageY - glyphLayout.height - padding * 2 - restartLayout.height;

        // Position für den Neustart-Text
        float restartX = (screenWidth - restartLayout.width) / 2;
        float restartY = messageY - glyphLayout.height - padding;

        // Blending aktivieren für transparente Box
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Box zeichnen
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, boxAlpha);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Rand zeichnen
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Roter Rand
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Blending deaktivieren
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Text zeichnen
        batch.begin();
        font.setColor(1, 0, 0, 1); // Roter Text für GAME OVER
        font.draw(batch, message, messageX, messageY);

        // Neustart Text
        font.getData().setScale(1.5f);
        font.setColor(1, 1, 1, 1); // Weißer Text für Neustart-Anweisung
        font.draw(batch, restartMessage, restartX, restartY);
        font.getData().setScale(3); // Zurück zur größeren Schrift

        batch.end();

        // Auch auf Tastendruck prüfen (falls InputProcessor nicht richtig funktioniert)
        checkInput();
    }

    private void checkInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            restartGame();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            restartGame();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        restartGame();
        return true;
    }

    private void restartGame() {
        // Zurück zum ersten Screen
        game.setScreen(new FirstScreen());
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        // Nichts zu tun
    }

    @Override
    public void pause() {
        // Nichts zu tun
    }

    @Override
    public void resume() {
        // Nichts zu tun
    }

    @Override
    public void hide() {
        // Nichts zu tun
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        if (gameOverSound != null) {
            gameOverSound.dispose();
        }
    }
}
