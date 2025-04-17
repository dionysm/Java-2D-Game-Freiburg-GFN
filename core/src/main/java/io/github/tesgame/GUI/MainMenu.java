package io.github.tesgame.GUI;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Game;
import io.github.tesgame.Highscore.HighscoreScreen;
import io.github.tesgame.StartGame;

public class MainMenu implements Screen {

    Game game;
    SpriteBatch batch;
    Texture bg;
    BitmapFont font;
    Sound cursorSFX;
    float sfxVolume = 0.2f;

    // Hintergrundfarbe
    Color backgroundColor = new Color(0.07f, 0.07f, 0.2f, 1);

    // Menüoptionen
    String[] mainMenu = {"Start Game", "Highscore", "Settings", "Exit"};
    String[] settingsMenu = {"Music ON", "Music OFF", "Back"};
    int selected = -1; // Keine Auswahl beim Start
    boolean inSettings = false;
    boolean musicOn = true;

    // Mausinteraktion
    int mouseHoverIndex = -1;

    public MainMenu(Game game) {
        this.game = game;
        batch = new SpriteBatch();

        // Hintergrundbild laden
        try {
            if (Gdx.files.internal("sprites/Splash.png").exists()) {
                bg = new Texture(Gdx.files.internal("sprites/Splash.png"));
            }
            else {
                System.out.println("Hintergrundbild nicht gefunden.");
                bg = null;
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden des Hintergrunds: " + e.getMessage());
            bg = null;
        }

        // Soundeffekte laden
        try {
            cursorSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/MenuMove.wav"));
        } catch (Exception e) {
            System.out.println("Sound konnte nicht geladen werden: " + e.getMessage());
            cursorSFX = null;
        }

        // Schriftart initialisieren
        font = new BitmapFont();
        font.getData().setScale(3f); // Grundgröße
        font.setColor(Color.WHITE);

        // Tastatureingaben
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    if (cursorSFX != null) cursorSFX.play(sfxVolume);
                    if (selected == -1) selected = 0;
                    else selected = (selected + 1) % currentMenu().length;
                }
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    if (cursorSFX != null) cursorSFX.play(sfxVolume);
                    if (selected == -1) selected = 0;
                    else selected = (selected - 1 + currentMenu().length) % currentMenu().length;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    if (selected != -1) activateSelection();
                }
                return true;
            }
        });
    }

    private String[] currentMenu() {
        return inSettings ? settingsMenu : mainMenu;
    }

    private void activateSelection() {
        int selection = mouseHoverIndex >= 0 ? mouseHoverIndex : selected;

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

    @Override
    public void render(float delta) {
        // Bildschirm löschen
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mouseHoverIndex = -1;

        batch.begin();
        // Hintergrund zeichnen falls vorhanden
        if (bg != null) {
            batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        int menuStartY = h / 2 + 100;

        String[] menu = currentMenu();

        // Menütexte zeichnen
        batch.begin();

        for (int i = 0; i < menu.length; i++) {
            String item = menu[i];
            float x = w / 2f - 150;
            float y = menuStartY - i * 60;

            // Textdimensionen berechnen für präzise Hover-Erkennung
            font.getData().setScale(3f);
            GlyphLayout layout = new GlyphLayout(font, item);
            float textWidth = layout.width;
            float textHeight = layout.height;

            // Hover-Status prüfen mit exakten Textdimensionen
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            boolean isHovered = (mouseX >= x && mouseX <= x + textWidth &&
                mouseY >= y - textHeight && mouseY <= y);

            if (isHovered) {
                mouseHoverIndex = i;
            }

            boolean isSelected = (i == selected) || isHovered;

            // Textstil anpassen
            if (isSelected) {
                font.getData().setScale(3.5f); // Größer wenn ausgewählt
                font.setColor(Color.RED); // Rote Farbe bei Auswahl
            } else {
                font.getData().setScale(3f); // Normale Größe
                font.setColor(Color.WHITE);
            }

            // Text zeichnen (korrekt zentriert)
            layout.setText(font, item);
            textWidth = layout.width;
            font.draw(batch, item, x, y); // Originalposition (nicht zentriert)
        }

        batch.end();

        // Mausklick verarbeiten
        if (Gdx.input.justTouched() && mouseHoverIndex >= 0) {
            selected = mouseHoverIndex;
            activateSelection();
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
