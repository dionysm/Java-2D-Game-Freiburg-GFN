package io.github.tesgame;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstScreen extends InputAdapter implements Screen {
    SpriteBatch batch;
    Player player;
    CameraController cameraController;
    EnemyManager enemyManager;
    Map map;

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
        map = new Map();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 198, 10, 0);

        // Pass camera controller to player for coordinate conversion
        player.update(delta, cameraController);

        cameraController.update(player.getPosition(), delta);
        enemyManager.update(delta, player.getPosition(), player.getWeapon());

        // Set batch to use camera
        batch.setProjectionMatrix(cameraController.getCamera().combined);

        // Draw everything
        batch.begin();
        map.draw(batch);
        player.draw(batch);
        enemyManager.draw(batch); // Added this line to draw enemies
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize logic (optional)
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
        map.dispose();
    }
}
