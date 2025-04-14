package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private float x, y;
    private float speed = 100f;
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;
    private Texture spriteSheet;
    private float stateTime;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> currentAnimation;
    private boolean isMoving;

    private Weapon weapon;  // Weapon instance

    public Player() {
        spriteSheet = new Texture("sprites/Walk.png");

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Extracting frames for each direction
        TextureRegion[] downFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] leftFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] rightFrames = new TextureRegion[FRAME_ROWS];
        TextureRegion[] upFrames = new TextureRegion[FRAME_ROWS];

        for (int i = 0; i < FRAME_ROWS; i++) {
            downFrames[i] = tmp[i][0]; // Column 0 = down
            upFrames[i] = tmp[i][1];   // Column 1 = up
            leftFrames[i] = tmp[i][2]; // Column 2 = left
            rightFrames[i] = tmp[i][3]; // Column 3 = right
        }

        walkDown = new Animation<TextureRegion>(0.1f, downFrames);
        walkLeft = new Animation<TextureRegion>(0.1f, leftFrames);
        walkRight = new Animation<TextureRegion>(0.1f, rightFrames);
        walkUp = new Animation<TextureRegion>(0.1f, upFrames);

        currentAnimation = walkDown;
        stateTime = 0f;

        x = 280;
        y = 200;

        weapon = new Weapon();  // Initialize weapon
    }

    public void update(float delta) {
        isMoving = false;
        Vector2 direction = new Vector2(0, 0); // Default direction is (0, 0)

        // Movement logic (normalizing diagonal movement)
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction.y = 1;
            currentAnimation = walkUp;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction.y = -1;
            currentAnimation = walkDown;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -1;
            currentAnimation = walkLeft;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = 1;
            currentAnimation = walkRight;
            isMoving = true;
        }

        // Normalize diagonal movement
        if (direction.x != 0 && direction.y != 0) {
            direction.nor();
        }

        // Update position based on normalized direction
        x += direction.x * speed * delta;
        y += direction.y * speed * delta;

        // Update animation state
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Update weapon (handles shooting)
        weapon.update(delta, x + 32, y + 32);  // Offset to center of player sprite
    }

    public void draw(SpriteBatch batch) {
        // Draw player animation
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);

        // Draw weapon projectiles
        weapon.draw(batch);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2 getPosition() {
        return new Vector2(x + 32, y + 32);  // Return center position
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void dispose() {
        spriteSheet.dispose();
        weapon.dispose();
    }
}
