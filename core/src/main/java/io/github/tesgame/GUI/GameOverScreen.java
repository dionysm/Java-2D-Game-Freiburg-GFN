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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
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
    private TextButton submitButton;
    private Sound gameOverSound;
    private boolean soundPlayed = false;
    private boolean highscoreSaved = false;
    private final String message = "GAME OVER";
    private TextField nameField;
    private Skin skin;
    private String playerName = "Spieler";
    private boolean musicStarted = false;
    private float musicDelay = 2.5f; // 2,5 Sekunden Verzögerung für die Musik
    private float soundPlayedTime = 0;

    public GameOverScreen(Main game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(4);
        glyphLayout = new GlyphLayout(font, message);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Farben für Buttons definieren
        Color normalColor = new Color(0.4f, 0.6f, 1f, 1f); // Hellblau
        Color pressedColor = new Color(0.8f, 0.2f, 0.2f, 1f); // Rot
        Color hoverColor = new Color(1f, 0.6f, 0.6f, 1f); // Rosa

        Texture buttonTexture = createButtonTexture(400, 80, normalColor);
        Texture buttonPressedTexture = createButtonTexture(400, 80, pressedColor);
        Texture buttonHoverTexture = createButtonTexture(400, 80, hoverColor);

        // Stil für Buttons definieren
        final BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2.1f);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.RED;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonHoverTexture));

        // Buttons erstellen
        restartButton = new TextButton("Neues Spiel", buttonStyle);
        menuButton = new TextButton("Hauptmenü", buttonStyle);

        restartButton.setSize(400, 80);
        menuButton.setSize(400, 80);

        // Positionen der Buttons festlegen - diese werden später angepasst
        restartButton.setPosition(Gdx.graphics.getWidth()/2f - 200f, Gdx.graphics.getHeight()/2f - 150f);
        menuButton.setPosition(Gdx.graphics.getWidth()/2f - 200f, Gdx.graphics.getHeight()/2f - 250f);

        // Listener für Buttons hinzufügen
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

        stage.addActor(restartButton);
        stage.addActor(menuButton);

        // Game Over Sound laden
        try {
            gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover-86548.ogg"));
        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Sound konnte nicht geladen werden", e);
        }

        // Skin für UI-Elemente laden
        skin = new Skin(Gdx.files.internal("db/uiskin.json"));

        // Stil für TextField definieren (weißes Feld mit größerer Schrift)
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        BitmapFont inputFont = new BitmapFont();
        inputFont.getData().setScale(2f);
        textFieldStyle.font = inputFont;
        textFieldStyle.fontColor = Color.BLACK;
        Pixmap white = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        white.setColor(Color.WHITE);
        white.fill();
        textFieldStyle.background = new TextureRegionDrawable(new Texture(white));

        // TextField für den Namen erstellen
        nameField = new TextField("", textFieldStyle);
        nameField.setMessageText("Dein Name");
        nameField.setSize(300, 60);

        // Submit Button für Highscore erstellen
        submitButton = new TextButton("Highscore speichern", buttonStyle);
        submitButton.setSize(300, 60);

        // Positionierung der Elements berechnen
        float textY = Gdx.graphics.getHeight() - 100f;
        float scoreY = textY - 80f;

        // Score und Eingabefeld positionieren - genau zwischen Score und erstem Button
        float inputY = scoreY - 100f;  // Genügend Abstand zum Score

        nameField.setPosition(Gdx.graphics.getWidth()/2f - 310f, inputY);
        submitButton.setPosition(Gdx.graphics.getWidth()/2f + 10f, inputY);

        // Buttons nach unten verschieben um Platz für das Eingabefeld zu schaffen
        restartButton.setPosition(Gdx.graphics.getWidth()/2f - 200f, inputY - 100f);
        menuButton.setPosition(Gdx.graphics.getWidth()/2f - 200f, inputY - 200f);

        // Listener für Submit Button
        submitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!highscoreSaved) {
                    playerName = nameField.getText().trim().isEmpty() ? "Spieler" : nameField.getText();
                    saveHighscore();
                    highscoreSaved = true;
                    nameField.remove();
                    submitButton.remove();
                }
                return true;
            }
        });

        stage.addActor(nameField);
        stage.addActor(submitButton);

        // Hintergrundmusik stoppen
        AudioController.getInstance().stopMusic("backgroundMusic");
    }

    // Highscore in die Datenbank speichern
    private void saveHighscore() {
        HighscoreManager.getInstance().saveHighscore(playerName, finalScore);
        System.out.println("Highscore gespeichert für: " + playerName);
    }

    @Override
    public void render(float delta) {
        // Hintergrund löschen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Zuerst den Game Over Sound abspielen
        if (!soundPlayed && gameOverSound != null) {
            gameOverSound.play(0.7f);
            soundPlayed = true;
            soundPlayedTime = 0; // Timer zur Verzögerung der Musik zurücksetzen
        }

        // Nach einem Zeitintervall die Hintergrundmusik starten
        if (soundPlayed && !musicStarted) {
            soundPlayedTime += delta;
            if (soundPlayedTime >= musicDelay) {
                // Game Over Musik nach der Verzögerung starten
                AudioController.getInstance().loadMusic("GameOver", "GameOver.ogg");
                AudioController.getInstance().playMusic("GameOver");
                musicStarted = true;
            }
        }

        // Texte zeichnen
        batch.begin();
        font.setColor(1, 0.2f, 0.2f, 1);
        float textX = Gdx.graphics.getWidth()/2f - glyphLayout.width/2f;
        float textY = Gdx.graphics.getHeight() - 100f;
        font.draw(batch, message, textX, textY);

        font.getData().setScale(2);
        String scoreText = "SCORE: " + finalScore;
        float scoreWidth = new GlyphLayout(font, scoreText).width;
        font.setColor(1, 1, 1, 1);
        font.draw(batch, scoreText, (Gdx.graphics.getWidth() - scoreWidth) / 2f, textY - 80f);
        font.getData().setScale(4);
        batch.end();

        // Bühne aktualisieren und zeichnen
        stage.act(delta);
        stage.draw();
    }

    // Texture für Buttons erzeugen
    private Texture createButtonTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);

        pixmap.setColor(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, 1);
        pixmap.drawRectangle(0, 0, width, height);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    // Neues Spiel starten
    private void startNewGame() {
        AudioController.getInstance().stopMusic("GameOver");
        game.setScreen(new StartGame(true));
        dispose();
    }

    // Zurück zum Hauptmenü
    private void returnToMainMenu() {
        AudioController.getInstance().stopMusic("GameOver");
        game.setScreen(new MainMenu(game));
        dispose();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    // Ressourcen freigeben wenn Bildschirm geschlossen wird
    @Override public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (stage != null) stage.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
    }
}
