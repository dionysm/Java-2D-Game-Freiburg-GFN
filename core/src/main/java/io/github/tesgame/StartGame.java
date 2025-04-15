package io.github.tesgame;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StartGame extends InputAdapter implements Screen {
    SpriteBatch batch;
    Player player;
    CameraController cameraController;
    EnemyManager enemyManager;
    Map map;
    ScoreDisplay scoreDisplay;
    Sound bgMusic;
    private boolean gameOver = false;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        bgMusic = Gdx.audio.newSound(Gdx.files.internal("sfx/FinalArea.ogg"));
        bgMusic.play();
        batch = new SpriteBatch();
        player = new Player();
        cameraController = new CameraController(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scoreDisplay = new ScoreDisplay();
        enemyManager = new EnemyManager(scoreDisplay); // Pass scoreDisplay to EnemyManager
        map = new Map();

        // Set up input processor
        Gdx.input.setInputProcessor(this);

        // Debug message to confirm initialization
        System.out.println("Game initialization complete");
    }

    @Override
    public void render(float delta) {

        if(player.isDead() && !gameOver){
            gameOver=true;
            ((Main)Gdx.app.getApplicationListener()).setScreen(new GameOverScreen((Main)Gdx.app.getApplicationListener()));
            return;
        }
        ScreenUtils.clear(0, 198, 10, 0);

        // Pass camera controller to player for coordinate conversion
        player.update(delta, cameraController);

        cameraController.update(player.getPosition(), delta);
        enemyManager.update(delta, player.getPosition(), player.getWeapon(), player);

        // Set batch to use camera
        batch.setProjectionMatrix(cameraController.getCamera().combined);

        // Draw game objects
        batch.begin();
        map.draw(batch);          // zuerst Boden
        map.drawTrees(batch);     // dann Bäume
        player.draw(batch);       // dann Spieler
        map.expandMapIfNeeded(player.getPosition());
        enemyManager.draw(batch);
        batch.end();

        // Draw HUD (score)
        scoreDisplay.draw(batch);
    }

    @Override
    public void resize(int width, int height) {}

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
        scoreDisplay.dispose();
        bgMusic.dispose();
    }

}
