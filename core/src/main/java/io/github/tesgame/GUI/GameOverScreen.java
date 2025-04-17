package io.github.tesgame.GUI;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.audio.Sound;
import io.github.tesgame.Controller.AudioController;
import io.github.tesgame.Main;
import io.github.tesgame.StartGame;
import io.github.tesgame.Highscore.HighscoreManager;

public class GameOverScreen implements Screen {

    private final Main game;
    private final int finalScore;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private Stage stage;
    private TextButton restartButton;
    private TextButton menuButton;
    private Sound gameOverSound;
    private boolean soundPlayed = false;
    private boolean highscoreSaved = false;
    private final String message = "GAME OVER";
    private TextField nameField;
    private TextButton submitButton;
    private Skin skin;
    private String playerName="Player";
    public GameOverScreen(Main game, int finalScore) {
        AudioController.getInstance().stopMusic("backgroundMusic");
        AudioController.getInstance().loadMusic("GameOver", "GameOver.ogg");
        AudioController.getInstance().playMusic("GameOver");
        this.game = game;
        this.finalScore = finalScore;


        try {
            System.out.println("Initialisiere GameOverScreen...");
            HighscoreManager.getInstance().saveHighscore("Spieler", finalScore);
            System.out.println("Highscore gespeichert.");
        } catch (Exception e) {
            System.out.println("Fehler beim Initialisieren des GameOverScreens:");
            e.printStackTrace();
        }

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
        // Skin laden
        skin = new Skin(Gdx.files.internal("db/uiskin.json"));

        // TextField für Namen
        nameField = new TextField("", skin);
        nameField.setMessageText("Dein Name");
        nameField.setSize(400, 60);
        nameField.setPosition(
            Gdx.graphics.getWidth()/2f - 200f,
            Gdx.graphics.getHeight()/2f + 100f
        );

        // Button zum Speichern des Namens
        submitButton = new TextButton("Highscore speichern", skin);
        submitButton.setSize(400, 60);
        submitButton.setPosition(
            Gdx.graphics.getWidth()/2f - 200f,
            Gdx.graphics.getHeight()/2f + 30f
        );

        // Listener für Speichern-Button
        submitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!highscoreSaved) {
                    playerName = nameField.getText().trim().isEmpty() ? "Spieler" : nameField.getText();
                    saveHighscore();
                    highscoreSaved = true;

                    // Optional: UI-Elemente entfernen
                    nameField.remove();
                    submitButton.remove();
                }
                return true;
            }
        });

        // Elemente zur Stage hinzufügen
        stage.addActor(nameField);
        stage.addActor(submitButton);
    }

    private void saveHighscore() {
        HighscoreManager.getInstance().saveHighscore(playerName, finalScore);
        System.out.println("Highscore gespeichert für: " + playerName);
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

        // Highscore speichern
        /* if (!highscoreSaved) {
            HighscoreManager.getInstance().saveHighscore("Spieler", finalScore); // Spielername ggf. anpassen
            highscoreSaved = true;
        } */

        // "GAME OVER" Text zeichnen
        batch.begin();
        font.setColor(1, 0.2f, 0.2f, 1); // Roter Text
        float textX = Gdx.graphics.getWidth()/2f - glyphLayout.width/2f;
        float textY = Gdx.graphics.getHeight() - 100f;
        font.draw(batch, message, textX, textY);

        // Score anzeigen
        font.getData().setScale(2);
        String scoreText = "SCORE: " + finalScore;
        float scoreWidth = new GlyphLayout(font, scoreText).width;
        font.setColor(1, 1, 1, 1);
        font.draw(batch, scoreText, (Gdx.graphics.getWidth() - scoreWidth) / 2f, textY - 80f);
        font.getData().setScale(4);
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
        AudioController.getInstance().stopMusic("GameOver");
        game.setScreen(new StartGame(true)); // Annahme: GameScreen existiert
        dispose();
    }

    private void returnToMainMenu() {
        AudioController.getInstance().stopMusic("GameOver");
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
