package io.github.tesgame;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Game;

public class MainMenu implements Screen {
    Game game;
    SpriteBatch batch;
    Texture bg;
    BitmapFont font;
    String[] menu = {"Start Game", "Highscore", "Exit"};
    int selected = 0;

    public MainMenu(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        bg = new Texture("MainMenuBackground.png");
        font = new BitmapFont();
        font.getData().setScale(2f);  // Doppelt so große Font

        // Key Abfrage
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    selected = (selected + 1) % menu.length;
                }
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    selected = (selected - 1 + menu.length) % menu.length;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    if (selected == 0) game.setScreen(new StartGame());; // START GAME
                    if (selected == 1) System.out.println("Highscore");
                    if (selected == 2) Gdx.app.exit();
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        for (int i = 0; i < menu.length; i++) {
            if (i == selected) {
                font.setColor(Color.RED);
            }
            else {
                font.setColor(Color.WHITE);
            }
            String text = (i == selected ? "- " : "  ") + menu[i];
            font.draw(batch, text, w / 2f - 100, h / 2f + 80 - i * 40);
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
