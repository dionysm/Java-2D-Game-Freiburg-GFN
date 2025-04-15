package io.github.tesgame;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Game;
import io.github.tesgame.highscore.HighscoreManager;
import io.github.tesgame.highscore.HighscoreScreen;

public class MainMenu implements Screen {
    Game game;
    SpriteBatch batch;
    Texture bg;
    BitmapFont font;

    // Menüinhalte
    String[] mainMenu = {"Start Game", "Highscore", "Settings", "Exit"};
    String[] settingsMenu = {"Music ON", "Music OFF", "Back"};
    // TODO MUSIC ON/OFF in einem und SOUND ON/OF auch in einem Menüpunkt
    int selected = 0;
    boolean inSettings = false;

    // TODO statt einfachem bool für musicON ein Feld für mehre Settings
    boolean[] settings = { true, true };   // First is Music Second is SFX
    boolean musicOn = true;

    public MainMenu(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        bg = new Texture("MainMenuBackground.png");
        font = new BitmapFont();
        font.getData().setScale(2f);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Menü-Auswahl bewegen
                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    selected = (selected + 1) % currentMenu().length;
                }
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    selected = (selected - 1 + currentMenu().length) % currentMenu().length;
                }

                // Auswahl bestätigen
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    if (!inSettings) {
                        // Hauptmenüaktionen
                        switch (selected) {
                            case 0:
                                game.setScreen(new StartGame(musicOn));
                                break;
                            case 1:
                                game.setScreen(new HighscoreScreen(game)); // Highscore anzeigen
                                break;
                            case 2:
                                inSettings = true;
                                selected = 0;
                                break;
                            case 3:
                                Gdx.app.exit();
                                break;
                        }
                    } else {
                        // Settings-Menüaktionen
                        switch (selected) {
                            case 0:
                                musicOn = true;
                                System.out.println("Music ON");
                                break;
                            case 1:
                                musicOn = false;
                                System.out.println("Music OFF");
                                break;
                            case 2:
                                inSettings = false;
                                selected = 0;
                                break;
                        }

                    }
                }
                return true;
            }
        });
    }

    private String[] currentMenu() {
        return inSettings ? settingsMenu : mainMenu;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        String[] menu = currentMenu();
        for (int i = 0; i < menu.length; i++) {
            if (i == selected) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);

            String text = (i == selected ? "- " : "  ") + menu[i];

            // Zeige Musikstatus an
            if (inSettings && (menu[i].startsWith("Music"))) {
                text += musicOn && i == 0 ? " (active)" : (!musicOn && i == 1 ? " (active)" : "");
            }

            font.draw(batch, text, w / 2f - 150, h / 2f + 80 - i * 40);
        }

        batch.end();
    }

    @Override public void dispose() { batch.dispose(); bg.dispose(); font.dispose(); }
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}
}
