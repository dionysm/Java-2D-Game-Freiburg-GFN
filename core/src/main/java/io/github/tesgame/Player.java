package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
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
    private Texture whitePixel;
    private float stateTime;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> currentAnimation;
    private boolean isMoving;
    private float width, height;

    // Health system
    private int health = 10;
    private int maxHealth = 10;
    private float invincibilityTimer = 0;
    private float invincibilityDuration = 0.5f;
    private boolean isInvincible = false;

    private Weapon weapon;
    private HeartDisplay heartDisplay;

    public Player() {
        spriteSheet = new Texture("sprites/SpriteSheet.png");

        // Create a 1x1 white pixel for UI elements if needed
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Store the dimensions of a single frame
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

        weapon = new Weapon();
        heartDisplay = new HeartDisplay();
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

        // Update invincibility
        if (isInvincible) {
            invincibilityTimer += delta;
            if (invincibilityTimer >= invincibilityDuration) {
                isInvincible = false;
            }
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
        // Draw player
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // Flash if invincible
        if (isInvincible && ((int)(invincibilityTimer * 10) % 2 == 0)) {
            batch.setColor(1, 1, 1, 0.5f); // Semi-transparent when flashing
        }

        batch.draw(currentFrame, x, y);
        batch.setColor(1, 1, 1, 1); // Reset color

        weapon.draw(batch);

        // Draw heart display
        heartDisplay.draw(batch, health);
    }

    public void takeDamage(int damage) {
        if (isInvincible) return;

        health -= damage;
        isInvincible = true;
        invincibilityTimer = 0;

        // Check for game over
        if (health <= 0) {
            // Handle player death
            System.out.println("Player died!");
            health = 0; // Prevent negative health
        }
    }

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
        System.out.println("Player healed! Health: " + health);
    }

    public void dispose() {
        spriteSheet.dispose();
        whitePixel.dispose();
        weapon.dispose();
        heartDisplay.dispose();
    }

    public Vector2 getPosition() {
        return new Vector2(x + width/2, y + height/2);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getHealth() {
        return health;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
