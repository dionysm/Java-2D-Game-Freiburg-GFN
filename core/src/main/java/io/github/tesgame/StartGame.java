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
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Controller.CameraController;
import io.github.tesgame.Enemies.EnemyManager;
import io.github.tesgame.Environment.Map;
import io.github.tesgame.GUI.GameOverScreen;
import io.github.tesgame.GUI.ScoreDisplay;

public class StartGame extends InputAdapter implements Screen {
    SpriteBatch batch;
    Player player;
    CameraController cameraController;
    EnemyManager enemyManager;
    Map map;
    ScoreDisplay scoreDisplay;
    Sound bgMusic;
    private boolean musicOn;
    private boolean gameOver = false;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private ShapeRenderer shapeRenderer;

    public StartGame(boolean musicOn) {
        this.musicOn = musicOn;
    }

    @Override
    public void show() {
        if (musicOn){
            bgMusic = Gdx.audio.newSound(Gdx.files.internal("sfx/FinalArea.ogg"));
            bgMusic.play();
        }

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
            int finalScore = scoreDisplay.getScore();
            ((Main)Gdx.app.getApplicationListener()).setScreen(new GameOverScreen((Main)Gdx.app.getApplicationListener(), finalScore));
            return;
        }
        ScreenUtils.clear(0, 198, 10, 0);

        // Get mouse position for aiming (since crosshair is removed)
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        Vector2 mousePos = cameraController.screenToWorld(mouseX, mouseY);

        // Pass camera controller and mouse position to player
        player.update(delta, cameraController, mousePos, map);

        cameraController.update(player.getPosition(), delta);
        enemyManager.update(delta, player.getPosition(), player.getWeapon(), player);

        // Set batch to use camera
        batch.setProjectionMatrix(cameraController.getCamera().combined);

        batch.begin();

        // Draw game objects
        map.draw(batch);
        map.drawTreeStems(batch);
        player.draw(batch);
        map.drawTreeCrowns(batch);
        enemyManager.draw(batch);
        map.expandMapIfNeeded(player.getPosition());
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
