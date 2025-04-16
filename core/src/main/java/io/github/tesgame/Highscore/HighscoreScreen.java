package io.github.tesgame.Highscore;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import io.github.tesgame.GUI.MainMenu;

import java.util.List;

public class HighscoreScreen implements Screen {
    private final Game game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final HighscoreManager highscoreManager;

    public HighscoreScreen(Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(2f);
        this.highscoreManager = new HighscoreManager();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACKSPACE) {
                    game.setScreen(new MainMenu(game));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, "Top 10 Highscores:", 100, Gdx.graphics.getHeight() - 50);

        List<String> top10 = highscoreManager.getTop10Highscores();
        for (int i = 0; i < top10.size(); i++) {
            font.draw(batch, (i + 1) + ". " + top10.get(i), 100, Gdx.graphics.getHeight() - 100 - i * 30);
        }

        font.setColor(Color.YELLOW);
        font.draw(batch, "Drücke ESC zum Zurückgehen", 100, 50);

        batch.end();
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); font.dispose(); }
}
