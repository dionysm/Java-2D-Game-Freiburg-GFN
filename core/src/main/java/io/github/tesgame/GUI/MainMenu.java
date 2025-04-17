package io.github.tesgame.GUI;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Game;
import io.github.tesgame.Highscore.HighscoreScreen;
import io.github.tesgame.StartGame;

public class MainMenu implements Screen {

    final Game game;
    SpriteBatch batch;
    Texture bg;
    BitmapFont font;
    Sound cursorSFX;
    float sfxVolume = 0.2f;

    // Farbpalette für modernes Design
    Color backgroundColor = new Color(0.07f, 0.07f, 0.2f, 1); // Dunkelblauer Hintergrund
    Color normalColor = new Color(0.8f, 0.8f, 0.8f, 1);      // Hellgrau für normale Texte
    Color hoverColor = new Color(0.2f, 0.6f, 1f, 1);        // Hellblau für Hover-Effekt
    Color pressedColor = new Color(0.1f, 0.4f, 0.8f, 1);    // Dunkleres Blau für Klick-Effekt
    Color titleColor = new Color(1f, 0.7f, 0.2f, 1);        // Orange für Titel

    // Menüoptionen
    String[] mainMenu = {"Start Game", "Highscore", "Settings", "Exit"};
    String[] settingsMenu = {"Music ON", "Music OFF", "Back"};
    int selected = -1;
    boolean inSettings = false;
    boolean musicOn = true;

    // Animationseigenschaften
    float pulseTime = 0;
    final float PULSE_DURATION = 1.5f;
    float titleYOffset = 0;

    public MainMenu(Game game) {
        this.game = game;
        batch = new SpriteBatch();

        // Ressourcen laden mit Fehlerbehandlung
        try {
            bg = new Texture(Gdx.files.internal("sprites/Splash.png"));
            cursorSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/MenuMove.wav"));
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Ressourcen: " + e.getMessage());
            bg = null;
            cursorSFX = null;
        }

        // Schriftart mit besserer Lesbarkeit
        font = new BitmapFont();
        font.getData().setScale(3f);
        font.setColor(normalColor);

        // Eingabeverarbeitung
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                handleKeyInput(keycode);
                return true;
            }
        });
    }

    private void handleKeyInput(int keycode) {
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            playSound();
            selected = (selected + 1) % currentMenu().length;
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            playSound();
            selected = (selected - 1 + currentMenu().length) % currentMenu().length;
        }
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            activateSelection();
        }
    }

    private void playSound() {
        if (cursorSFX != null) cursorSFX.play(sfxVolume);
    }

    @Override
    public void render(float delta) {
        updateAnimations(delta);
        clearScreen();
        drawBackground();
        drawMenu();
        handleMouseInput();
    }

    private void updateAnimations(float delta) {
        // Pulsierende Animation für Titel
        pulseTime += delta;
        titleYOffset = (float) Math.sin(pulseTime * 2) * 5f;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawBackground() {
        batch.begin();
        if (bg != null) {
            // Hintergrund mit leichter Transparenz für moderneren Look
            batch.setColor(1, 1, 1, 0.9f);
            batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
        }
        batch.end();
    }

    private void drawMenu() {
        batch.begin();


        // Menüoptionen zeichnen
        drawMenuOptions();

        batch.end();
    }


    private void drawMenuOptions() {
        String[] menu = currentMenu();
        int centerX = Gdx.graphics.getWidth() / 2;
        int startY = Gdx.graphics.getHeight() / 2 + 50;

        for (int i = 0; i < menu.length; i++) {
            String item = menu[i];
            GlyphLayout layout = new GlyphLayout(font, item);

            // Position berechnen (zentriert)
            float x = centerX - layout.width / 2f;
            float y = startY - i * 60;

            // Hover/Selection Effekte
            if (i == selected || isMouseHovering(item, x, y, layout)) {
                font.setColor(hoverColor);
                font.getData().setScale(3.2f);
            } else {
                font.setColor(normalColor);
                font.getData().setScale(3f);
            }

            // Text zeichnen
            font.draw(batch, item, x, y);
        }
    }

    private boolean isMouseHovering(String text, float x, float y, GlyphLayout layout) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mouseX >= x && mouseX <= x + layout.width &&
            mouseY >= y - layout.height && mouseY <= y;
    }

    private void handleMouseInput() {
        if (Gdx.input.justTouched()) {
            String[] menu = currentMenu();
            int centerX = Gdx.graphics.getWidth() / 2;
            int startY = Gdx.graphics.getHeight() / 2 + 50;

            for (int i = 0; i < menu.length; i++) {
                GlyphLayout layout = new GlyphLayout(font, menu[i]);
                float x = centerX - layout.width / 2f;
                float y = startY - i * 60;

                if (isMouseHovering(menu[i], x, y, layout)) {
                    selected = i;
                    activateSelection();
                    break;
                }
            }
        }
    }

    // Restliche Methoden bleiben unverändert...
    private String[] currentMenu() { return inSettings ? settingsMenu : mainMenu; }

    private void activateSelection() {
        int selection = selected;
        if (selection == -1) return;

        if (!inSettings) {
            switch (selection) {
                case 0: game.setScreen(new StartGame(musicOn)); break;
                case 1: game.setScreen(new HighscoreScreen(game)); break;
                case 2: inSettings = true; selected = -1; break;
                case 3: Gdx.app.exit(); break;
            }
        } else {
            switch (selection) {
                case 0: musicOn = true; break;
                case 1: musicOn = false; break;
                case 2: inSettings = false; selected = -1; break;
            }
        }
    }

    @Override public void dispose() {
        batch.dispose();
        if (bg != null) bg.dispose();
        font.dispose();
        if (cursorSFX != null) cursorSFX.dispose();
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}
}
