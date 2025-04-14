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
    private static final int FRAME_ROWS = 7;
    private Texture spriteSheet;
    private float stateTime;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> currentAnimation;
    private boolean isMoving;
    private float width, height;

    private Weapon weapon;

    public Player() {
        spriteSheet = new Texture("sprites/SpriteSheet.png");

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Store dimensions
        width = tmp[0][0].getRegionWidth();
        height = tmp[0][0].getRegionHeight();

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

        walkDown = new Animation<>(0.1f, downFrames);
        walkLeft = new Animation<>(0.1f, leftFrames);
        walkRight = new Animation<>(0.1f, rightFrames);
        walkUp = new Animation<>(0.1f, upFrames);

        currentAnimation = walkDown;
        stateTime = 0f;

        x = 280;
        y = 200;

        weapon = new Weapon(); // Initialize weapon
    }

    public void update(float delta, CameraController cameraController) {
        isMoving = false;
        Vector2 direction = new Vector2(0, 0); // Default direction

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

        if (direction.x != 0 && direction.y != 0) {
            direction.nor();
        }

        x += direction.x * speed * delta;
        y += direction.y * speed * delta;

        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Get mouse position in screen coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Convert to world coordinates
        Vector2 worldMouse = cameraController.screenToWorld(mouseX, mouseY);

        // Update weapon with world coordinates
        weapon.update(delta, x + width/2, y + height/2, worldMouse);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);

        weapon.draw(batch);
    }

    public void dispose() {
        spriteSheet.dispose();
        weapon.dispose();
    }

    public Vector2 getPosition() {
        return new Vector2(x + width/2, y + height/2);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
