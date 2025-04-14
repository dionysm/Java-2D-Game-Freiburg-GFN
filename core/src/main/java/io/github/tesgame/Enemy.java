package io.github.tesgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    protected int maxHealth;
    protected int health;
    protected boolean isDead = false;
    protected ShapeRenderer shapeRenderer;

    // Attack properties
    protected boolean isRangedAttacker = false;
    protected boolean isMeleeAttacker = false;
    protected float attackRange;
    protected float attackCooldown;
    protected float attackTimer = 0f;
    protected boolean isAttacking = false;

    // For animation
    protected Texture spriteSheet;
    protected Animation<TextureRegion> animation;
    protected float stateTime = 0;
    protected float width, height;

    // Health bar dimensions
    protected float healthBarWidth = 40;
    protected float healthBarHeight = 5;
    protected float healthBarOffsetY = 5; // Distance above enemy

    public Enemy(float x, float y, float speed, int maxHealth) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        shapeRenderer = new ShapeRenderer();

        // Load sprites and set up animations (implemented by subclasses)
        loadSprites();
    }

    // Abstract method to be implemented by each enemy type
    protected abstract void loadSprites();

    // Abstract attack method for enemy types to implement
    protected abstract void performAttack(Player player);

    public void update(float delta, Vector2 playerPosition, Player player) {
        if (isDead) return;

        // Update animation time
        stateTime += delta;

        // Update attack timer
        attackTimer += delta;

        // Calculate distance to player
        float distanceToPlayer = position.dst(playerPosition);

        // Attack logic
        if (distanceToPlayer <= attackRange && attackTimer >= attackCooldown) {
            // Start attack
            isAttacking = true;
            attackTimer = 0;
            stateTime = 0; // Reset animation time for attack animation

            // Perform the attack based on enemy type
            performAttack(player);
        }

        // Normal movement logic if not attacking or if ranged attacker (can move while attacking)
        if (!isAttacking || isRangedAttacker) {
            Vector2 direction = new Vector2(
                playerPosition.x - position.x,
                playerPosition.y - position.y
            ).nor();

            velocity.set(direction.x * speed, direction.y * speed);
            position.add(velocity.x * delta, velocity.y * delta);
        }

        // Reset attack state after a short time (for melee attackers)
        if (isAttacking && isMeleeAttacker && stateTime > 0.5f) {
            isAttacking = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (isDead) return;

        // Get current frame
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        // Draw centered at position
        batch.draw(currentFrame, position.x - width/2, position.y - height/2);

        // Draw the health bar
        drawHealthBar(batch);
    }

    private void drawHealthBar(SpriteBatch batch) {
        // We need to end the SpriteBatch before using ShapeRenderer
        batch.end();

        // Calculate health bar position
        float barX = position.x - healthBarWidth / 2;
        float barY = position.y + height / 2 + healthBarOffsetY;

        // Set projection matrix to match batch
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Draw health bar background (gray)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, healthBarWidth, healthBarHeight);

        // Calculate current health width
        float currentHealthWidth = (float)health / maxHealth * healthBarWidth;

        // Set color based on health level
        if (health > (int)(maxHealth * 0.6f)) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (health > (int)(maxHealth * 0.2f)) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Draw current health
        shapeRenderer.rect(barX, barY, currentHealthWidth, healthBarHeight);
        shapeRenderer.end();

        // Resume the SpriteBatch
        batch.begin();
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

    public void takeDamage(int damage) {
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
        shapeRenderer.dispose();
    }
}
