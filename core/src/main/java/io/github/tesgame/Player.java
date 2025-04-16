package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Combat.Weapon;
import io.github.tesgame.Controller.CameraController;
import io.github.tesgame.GUI.HeartDisplay;
import io.github.tesgame.Environment.Map;

public class Player {
    private float x, y;
    private float normalSpeed = 90f;
    private float speed = normalSpeed;
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;
    private Texture spriteSheet;
    private Texture whitePixel;
    private float stateTime;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> currentAnimation;
    private boolean isMoving;
    private float width, height;
    private Rectangle collisionBounds;

    // Health system
    private int health = 10;
    private int maxHealth = 10;
    private float invincibilityTimer = 0;
    private float invincibilityDuration = 0.5f;
    private boolean isInvincible = false;

    // Slow effect system
    private boolean isSlowed = false;
    private float slowDuration = 0;
    private float slowTimer = 0;
    private float slowFactor = 1.0f; // 1.0 = normal speed, 0.5 = half speed

    private Weapon weapon;
    private HeartDisplay heartDisplay;

    // Game Over
    private boolean isDead = false;

    public Player() {
        spriteSheet = new Texture("sprites/chars/Walk-Player-Red.png");

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

        // Create collision bounds slightly smaller than visual size
        collisionBounds = new Rectangle(x, y, width * 0.7f, height * 0.4f);

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

    public void update(float delta, CameraController cameraController, Vector2 crosshairPosition, Map map) {
        // Update slow effect timer
        if (isSlowed) {
            slowTimer += delta;
            if (slowTimer >= slowDuration) {
                isSlowed = false;
                speed = normalSpeed; // Reset to normal speed
                System.out.println("Slow effect wore off!");
            }
        }

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

        // Calculate potential new position
        float newX = x + direction.x * speed * delta;
        float newY = y + direction.y * speed * delta;

        // Update collision bounds for new position
        // Position the collision box at the bottom portion of the sprite
        float colliderOffsetX = (width - collisionBounds.width) / 2;
        float colliderOffsetY = height * 0.1f; // Slight offset from bottom

        // Try moving on both axes
        Rectangle newBounds = new Rectangle(
            newX + colliderOffsetX,
            newY + colliderOffsetY,
            collisionBounds.width,
            collisionBounds.height
        );

        boolean canMove = true;

        // Check for tree collisions if map is provided
        if (map != null && map.isCollidingWithTrees(newBounds)) {
            canMove = false;

            // Try moving only on X axis
            newBounds.x = newX + colliderOffsetX;
            newBounds.y = y + colliderOffsetY;
            if (!map.isCollidingWithTrees(newBounds)) {
                newY = y; // Keep Y the same
                canMove = true;
            } else {
                // Try moving only on Y axis
                newBounds.x = x + colliderOffsetX;
                newBounds.y = newY + colliderOffsetY;
                if (!map.isCollidingWithTrees(newBounds)) {
                    newX = x; // Keep X the same
                    canMove = true;
                }
            }
        }

        // Apply movement if allowed
        if (canMove) {
            x = newX;
            y = newY;
        }

        // Update collision bounds position
        collisionBounds.x = x + colliderOffsetX;
        collisionBounds.y = y + colliderOffsetY;

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

        // Update weapon with crosshair position and trees for collision
        if (map != null) {
            weapon.update(delta, x + width/2, y + height/2, crosshairPosition, map.getTrees());
        } else {
            weapon.update(delta, x + width/2, y + height/2, crosshairPosition);
        }
    }

    public void draw(SpriteBatch batch) {
        // Draw player
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // Apply visual effects based on status
        if (isSlowed) {
            batch.setColor(0.7f, 0.7f, 1.0f, 1.0f); // Blue tint when slowed
        } else if (isInvincible && ((int)(invincibilityTimer * 10) % 2 == 0)) {
            batch.setColor(1, 1, 1, 0.5f); // Semi-transparent when flashing
        }

        batch.draw(currentFrame, x, y);
        batch.setColor(1, 1, 1, 1); // Reset color

        weapon.draw(batch);

        // Draw heart display
        heartDisplay.draw(batch, health);

        // Debug: draw collision box
        /*
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
        shapeRenderer.end();
        batch.begin();
        */
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
            isDead = true;
        }
    }

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
        System.out.println("Player healed! Health: " + health);
    }

    public void applySlowEffect(float duration, float slowAmount) {
        // Only apply if longer than current slow or not slowed yet
        if (!isSlowed || duration > slowDuration - slowTimer) {
            isSlowed = true;
            slowDuration = duration;
            slowTimer = 0;
            slowFactor = 1.0f - slowAmount; // Convert % slow to multiplier
            speed = normalSpeed * slowFactor;
            System.out.println("Player slowed by " + (slowAmount*100) + "% for " + duration + " seconds!");
        }
    }

    public boolean isDead() {
        return isDead;
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

    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    public int getHealth() {
        return health;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
