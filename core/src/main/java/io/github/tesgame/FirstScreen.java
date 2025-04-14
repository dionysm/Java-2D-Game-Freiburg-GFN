package io.github.tesgame;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;

public class FirstScreen  extends InputAdapter implements Screen {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private final Main game;
    Stage stage;
    SpriteBatch batch;
    Texture player;
    float Speed = 50.0f;
    float playerx = 280;
    float playery = 200;

    public FirstScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }
    @Override
    public void show() {
        player = new Texture("sprites/SpriteSheet.png");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
       Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
       ScreenUtils.clear(0, 0, 0, 0);
       camera.update();
       batch.setProjectionMatrix(camera.combined);
       batch.begin();
       stage.draw();
       batch.draw(player, playerx, playery);
       if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
           playery += Gdx.graphics.getDeltaTime() * Speed;

       }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            playery -= Gdx.graphics.getDeltaTime() * Speed;

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            playerx -= Gdx.graphics.getDeltaTime() * Speed;

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            playerx += Gdx.graphics.getDeltaTime() * Speed;

        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize logic (optional)
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
