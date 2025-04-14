package io.github.tesgame;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class FirstScreen extends InputAdapter implements Screen {
    SpriteBatch batch;
    Player player;
    CameraController cameraController;
    EnemyManager enemyManager;

    @Override
    public void show() {
        batch = new SpriteBatch();
        player = new Player();
        cameraController = new CameraController(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        enemyManager = new EnemyManager();

        // Set up input processor
        Gdx.input.setInputProcessor(this);

        // Debug message to confirm initialization
        System.out.println("Game initialization complete");
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Update game objects
        player.update(delta);
        cameraController.update(player.getPosition(), delta);
        enemyManager.update(delta, player.getPosition(), player.getWeapon());

        // Set batch to use camera
        batch.setProjectionMatrix(cameraController.getCamera().combined);

        // Draw everything
        batch.begin();
        player.draw(batch);
        enemyManager.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize camera viewport if needed
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        enemyManager.dispose();
    }
}
