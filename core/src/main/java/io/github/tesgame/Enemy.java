package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 40f; // Slower than player
    private float health = 100;
    private boolean isDead = false;

    // For animation
    private Texture spriteSheet;
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private float width, height;

    // Animation constants based on the sprite sheet
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;

    public Enemy(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);

        // Load enemy sprite sheet
        spriteSheet = new Texture("sprites/Skeleton-Walk.png");

        // Set up animation with the 4x4 sprite sheet
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Create animation frames (using the first row for walking animation)
        TextureRegion[] frames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            frames[i] = tmp[0][i]; // Using first row (index 0)
        }

        animation = new Animation<>(0.15f, frames);

        // Set dimensions - scale down if needed
        width = tmp[0][0].getRegionWidth();
        height = tmp[0][0].getRegionHeight();
    }

    public void update(float delta, Vector2 playerPosition) {
        if (isDead) return;

        // Update animation time
        stateTime += delta;

        // Move toward player
        Vector2 direction = new Vector2(
            playerPosition.x - position.x,
            playerPosition.y - position.y
        ).nor();

        // Set velocity based on direction and speed
        velocity.set(direction.x * speed, direction.y * speed);

        // Update position
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void draw(SpriteBatch batch) {
        if (isDead) return;

        // Get current frame
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        // Draw centered at position
        batch.draw(currentFrame, position.x - width/2, position.y - height/2);
    }

    public boolean checkCollision(Projectile projectile) {
        // Simple rectangle-based collision detection
        Rectangle enemyRect = new Rectangle(
            position.x - width/2, position.y - height/2,
            width, height
        );

        Rectangle projectileRect = new Rectangle(
            projectile.getPosition().x - projectile.getWidth()/2,
            projectile.getPosition().y - projectile.getHeight()/2,
            projectile.getWidth(), projectile.getHeight()
        );

        return enemyRect.overlaps(projectileRect);
    }

    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
