package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {

    private float x, y;
    private float speed = 100f;

    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 7;

    private Texture spriteSheet;
    private float stateTime;

    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> currentAnimation;

    private boolean isMoving;

    public Player() {
        spriteSheet = new Texture("sprites/SpriteSheet.png");

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Spaltenweise Animationen extrahieren
        TextureRegion[] downFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] leftFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] rightFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] upFrames = new TextureRegion[FRAME_ROWS];

        for (int i = 0; i < FRAME_ROWS; i++) {
            downFrames[i] = tmp[i][0]; // Spalte 0 = runter
            upFrames[i] = tmp[i][1]; // Spalte 1 = hoch
            leftFrames[i] = tmp[i][2]; // Spalte 2 = links
            rightFrames[i] = tmp[i][3]; // Spalte 3 = rechts
        }

        walkDown = new Animation<TextureRegion>(0.1f, downFrames);
        walkLeft = new Animation<TextureRegion>(0.1f, leftFrames);
        walkRight = new Animation<TextureRegion>(0.1f, rightFrames);
        walkUp = new Animation<TextureRegion>(0.1f, upFrames);

        currentAnimation = walkDown;
        stateTime = 0f;

        x = 280;
        y = 200;
    }

    public void update(float delta) {
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * delta;
            currentAnimation = walkUp;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * delta;
            currentAnimation = walkDown;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
            currentAnimation = walkLeft;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
            currentAnimation = walkRight;
            isMoving = true;
        }

        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
