package io.github.tesgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.audio.Sound;

public class GameOverScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private Stage stage;
    private TextButton restartButton;
    private TextButton menuButton;
    private Sound gameOverSound;
    private boolean soundPlayed = false;
    private final String message = "GAME OVER";

    public GameOverScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Schriftart initialisieren
        font = new BitmapFont();
        font.getData().setScale(4); // Sehr große Schrift für "GAME OVER"
        glyphLayout = new GlyphLayout(font, message);

        // Stage für UI-Elemente erstellen
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Button-Texturen programmatisch generieren
        Texture buttonTexture = createButtonTexture(400, 80, new Color(0.2f, 0.6f, 1f, 1)); // Hellblau
        Texture buttonPressedTexture = createButtonTexture(400, 80, new Color(0.1f, 0.3f, 0.5f, 1)); // Dunkelblau

        // Button-Stil definieren
        TextButtonStyle style = new TextButtonStyle();
        style.font = new BitmapFont();
        style.font.getData().setScale(1.8f);
        style.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));

        // Buttons erstellen und positionieren
        restartButton = new TextButton("Neues Spiel", style);
        restartButton.setPosition(
            Gdx.graphics.getWidth()/2f - 200f,
            Gdx.graphics.getHeight()/2f - 50f
        );
        restartButton.setSize(400, 80);

        menuButton = new TextButton("Hauptmenü", style);
        menuButton.setPosition(
            Gdx.graphics.getWidth()/2f - 200f,
            Gdx.graphics.getHeight()/2f - 150f
        );
        menuButton.setSize(400, 80);

        // Button-Events hinzufügen
        restartButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startNewGame();
                return true;
            }
        });

        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                returnToMainMenu();
                return true;
            }
        });

        // Buttons zur Stage hinzufügen
        stage.addActor(restartButton);
        stage.addActor(menuButton);

        // Soundeffekt laden
        try {
            gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover.ogg"));
        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Sound konnte nicht geladen werden", e);
        }
    }

    @Override
    public void render(float delta) {
        // Hintergrund mit weichem Blau füllen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Game Over Sound einmalig abspielen
        if (!soundPlayed && gameOverSound != null) {
            gameOverSound.play(0.7f);
            soundPlayed = true;
        }

        // "GAME OVER" Text zeichnen
        batch.begin();
        font.setColor(1, 0.2f, 0.2f, 1); // Roter Text
        float textX = Gdx.graphics.getWidth()/2f - glyphLayout.width/2f;
        float textY = Gdx.graphics.getHeight() - 100f;
        font.draw(batch, message, textX, textY);
        batch.end();

        // UI aktualisieren und zeichnen
        stage.act(delta);
        stage.draw();
    }

    /**
     * Erstellt eine abgerundete Button-Textur
     * @param width Button-Breite
     * @param height Button-Höhe
     * @param color Button-Farbe
     * @return Die generierte Texture
     */
    private Texture createButtonTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height); // Yuvarlatma yok

        // Hafif kenar çizimi (gölge gibi)
        pixmap.setColor(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, 1);
        pixmap.drawRectangle(0, 0, width, height); // Normal dikdörtgen çizimi

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }


    private void startNewGame() {
        game.setScreen(new StartGame(true)); // Annahme: GameScreen existiert
        dispose();
    }

    private void returnToMainMenu() {
        game.setScreen(new MainMenu(game));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (stage != null) stage.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
    }
}
