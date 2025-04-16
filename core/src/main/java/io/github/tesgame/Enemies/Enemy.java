package io.github.tesgame.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;
import io.github.tesgame.Combat.Projectile;
import io.github.tesgame.Enemies.DeathEffect;

import java.util.Map;
import java.util.HashMap;

public abstract class Enemy {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    protected int maxHealth;
    protected int health;
    protected boolean isDead = false;
    protected boolean rewardGiven = false; // Track if healing and score have been given
    protected ShapeRenderer shapeRenderer;

    // Attack properties
    protected boolean isRangedAttacker = false;
    protected boolean isMeleeAttacker = false;
    protected float attackRange;
    protected float attackCooldown;
    protected float attackTimer = 0f;
    protected boolean isAttacking = false;

    // Animation
    protected Texture spriteSheet;
    protected Map<Direction, Animation<TextureRegion>> animations;
    protected float stateTime = 0f;
    protected float width, height;
    protected int FRAME_COLS;
    protected int FRAME_ROWS;
    protected String spriteSheetPath;
    protected float animationSpeed = 0.15f;

    // Death effect
    protected DeathEffect deathEffect;
    protected String deathEffectPath;
    protected float deathEffectDuration = 2.0f;
    protected boolean showingDeathEffect = false;

    // Health bar
    protected float healthBarWidth = 40;
    protected float healthBarHeight = 5;
    protected float healthBarOffsetY = 5;

    // Direction
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    protected Direction currentDirection = Direction.DOWN;

    // Sound
    protected static final Sound deathSoundFX = Gdx.audio.newSound(Gdx.files.internal("sfx/EnemyDead.wav"));

    public Enemy(float x, float y, float speed, int maxHealth, String spriteSheetPath, int frameCols, int frameRows, String deathEffectPath) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.shapeRenderer = new ShapeRenderer();
        this.spriteSheetPath = spriteSheetPath;
        this.FRAME_COLS = frameCols;
        this.FRAME_ROWS = frameRows;
        this.deathEffectPath = deathEffectPath;

        loadSprites(); // Load sprites with default implementation
    }

    protected void loadSprites() {
        spriteSheet = new Texture(spriteSheetPath);

        // Split the sprite sheet into frames
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Create animations
        animations = new HashMap<>();

        // Animations for movement directions - standard layout
        for (int col = 0; col < FRAME_COLS; col++) {
            TextureRegion[] directionFrames = new TextureRegion[FRAME_ROWS];
            for (int row = 0; row < FRAME_ROWS; row++) {
                directionFrames[row] = tmp[row][col];
            }

            Animation<TextureRegion> dirAnimation = new Animation<>(animationSpeed, directionFrames);

            // Map animations to directions (standard layout)
            switch (col) {
                case 0:
                    animations.put(Direction.DOWN, dirAnimation);
                    break;
                case 1:
                    animations.put(Direction.UP, dirAnimation);
                    break;
                case 2:
                    animations.put(Direction.LEFT, dirAnimation);
                    break;
                case 3:
                    animations.put(Direction.RIGHT, dirAnimation);
                    break;
            }
        }

        width = tmp[0][0].getRegionWidth();
        height = tmp[0][0].getRegionHeight();
    }

    // Abstract method to be implemented by subclasses
    protected abstract void performAttack(Player player);

    public void update(float delta, Vector2 playerPosition, Player player) {
        // Update death effect if active
        if (showingDeathEffect && deathEffect != null) {
            deathEffect.update(delta);

            if (deathEffect.isFinished()) {
                deathEffect.dispose();
                deathEffect = null;
                showingDeathEffect = false;
            }
            return; // Skip normal update if dead and showing death effect
        }

        if (isDead) return;

        stateTime += delta;
        attackTimer += delta;

        float distanceToPlayer = position.dst(playerPosition);

        // Start attack
        if (distanceToPlayer <= attackRange && attackTimer >= attackCooldown) {
            isAttacking = true;
            attackTimer = 0;
            stateTime = 0;
            performAttack(player);
        }

        // Movement (if allowed)
        Vector2 direction = new Vector2(
            playerPosition.x - position.x,
            playerPosition.y - position.y
        ).nor();

        if (!isAttacking || isRangedAttacker) {
            velocity.set(direction.x * speed, direction.y * speed);
            position.add(velocity.x * delta, velocity.y * delta);
        }

        // Direction for animation
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            currentDirection = (direction.x > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = (direction.y > 0) ? Direction.UP : Direction.DOWN;
        }

        // End attack (for melee)
        if (isAttacking && isMeleeAttacker && stateTime > 0.5f) {
            isAttacking = false;
        }
    }

    public void draw(SpriteBatch batch) {
        // Zeichne den Todeseffekt, wenn aktiv
        if (showingDeathEffect && deathEffect != null) {
            deathEffect.draw(batch);
            return; // Wenn tot, zeige nur den Death Effect
        }

        if (isDead) return;

        TextureRegion currentFrame = animations != null
            ? animations.get(currentDirection).getKeyFrame(stateTime, true)
            : null;

        if (currentFrame != null) {
            batch.draw(currentFrame, position.x - width / 2, position.y - height / 2);
        }
        drawHealthBar(batch);
    }

    private void drawHealthBar(SpriteBatch batch) {
        batch.end();

        float barX = position.x - healthBarWidth / 2;
        float barY = position.y + height / 2 + healthBarOffsetY;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, healthBarWidth, healthBarHeight);

        float currentHealthWidth = (float) health / maxHealth * healthBarWidth;

        if (health > maxHealth * 0.6f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (health > maxHealth * 0.2f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        shapeRenderer.rect(barX, barY, currentHealthWidth, healthBarHeight);
        shapeRenderer.end();

        batch.begin();
    }

    public boolean checkCollision(Projectile projectile) {
        Rectangle enemyRect = new Rectangle(
            position.x - width / 2, position.y - height / 2,
            width, height
        );

        Rectangle projectileRect = new Rectangle(
            projectile.getPosition().x - projectile.getWidth() / 2,
            projectile.getPosition().y - projectile.getHeight() / 2,
            projectile.getWidth(), projectile.getHeight()
        );

        return enemyRect.overlaps(projectileRect);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;

            // Erstelle den Death Effect
            deathEffect = new DeathEffect(position.x, position.y, deathEffectPath, deathEffectDuration);
            showingDeathEffect = true;

            // Spiele Sound
            deathSoundFX.play();
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isDeathEffectFinished() {
        return isDead && !showingDeathEffect;
    }

    public boolean isRewardGiven() {
        return rewardGiven;
    }

    public void setRewardGiven(boolean given) {
        this.rewardGiven = given;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (deathEffect != null) deathEffect.dispose();
    }
}
