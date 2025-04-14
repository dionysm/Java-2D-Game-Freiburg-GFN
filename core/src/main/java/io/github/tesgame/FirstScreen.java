package io.github.tesgame;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;

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
        player.update(delta);
        cameraController.update(player.getPosition(), delta);


        enemyManager.update(delta, player.getPosition(), player.getWeapon());

        // Set batch to use camera
        batch.setProjectionMatrix(cameraController.getCamera().combined);

        // Draw everything
        batch.begin();
        map.draw(batch);
        player.draw(batch);
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
    }

}
