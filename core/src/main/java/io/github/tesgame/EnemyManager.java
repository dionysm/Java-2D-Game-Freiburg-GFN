package io.github.tesgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private float spawnTimer = 0;
    private float initialDelay = 2.0f; // 2-second initial delay
    private boolean initialSpawnDone = false; // Track if initial spawn happened
    private float spawnRate = 2.0f;
    private float spawnDistance = 300;
    private int maxEnemies = 50;
    private ScoreDisplay scoreDisplay;

    public EnemyManager(ScoreDisplay scoreDisplay) {
        this.scoreDisplay = scoreDisplay;
        enemies = new ArrayList<>();
        // No initial enemies - will spawn after delay
    }

    public void update(float delta, Vector2 playerPosition, Weapon playerWeapon, Player player) {
        // Update spawn timer
        spawnTimer += delta;

        // Handle initial delay before spawning enemies
        if (!initialSpawnDone) {
            if (spawnTimer >= initialDelay) {
                initialSpawnDone = true;
                spawnTimer = 0;

                // Spawn initial batch of enemies after delay
                for (int i = 0; i < 5; i++) {
                    float x = MathUtils.random(100, 700);
                    float y = MathUtils.random(100, 500);
                    spawnRandomEnemy(x, y);
                }
                System.out.println("Initial enemies spawned after delay!");
            }
            return; // Skip rest of update until initial spawn happens
        }

        // Regular enemy spawning after initial batch
        if (spawnTimer >= spawnRate && enemies.size() < maxEnemies) {
            spawnTimer = 0;
            spawnEnemy(playerPosition);
        }

        // Update all enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(delta, playerPosition, player);

            // Remove dead enemies, heal player, and increment score
            if (enemy.isDead()) {
                player.heal(1);
                scoreDisplay.incrementScore();
                iterator.remove();
            }
        }

        // Check collisions with player weapons
        ArrayList<Projectile> projectiles = playerWeapon.getProjectiles();
        if (projectiles != null) {
            Iterator<Projectile> projectileIterator = projectiles.iterator();
            while (projectileIterator.hasNext()) {
                Projectile projectile = projectileIterator.next();
                boolean hitEnemy = false;

                for (Enemy enemy : enemies) {
                    if (!enemy.isDead() && enemy.checkCollision(projectile)) {
                        enemy.takeDamage(1);
                        hitEnemy = true;
                        break;
                    }
                }

                if (hitEnemy) {
                    projectileIterator.remove();
                }
            }
        }
    }

    private void spawnEnemy(Vector2 playerPosition) {
        float angle = MathUtils.random(360f);
        float radians = (float) Math.toRadians(angle);

        float spawnX = playerPosition.x + (float) Math.cos(radians) * spawnDistance;
        float spawnY = playerPosition.y + (float) Math.sin(radians) * spawnDistance;

        spawnRandomEnemy(spawnX, spawnY);
    }

    private void spawnRandomEnemy(float x, float y) {
        int enemyType = MathUtils.random(2);

        Enemy enemy;
        switch (enemyType) {
            case 0:
                enemy = new SkeletonEnemy(x, y);
                break;
            case 1:
                enemy = new GoblinEnemy(x, y);
                break;
            case 2:
                enemy = new OrcEnemy(x, y);
                break;
            default:
                enemy = new SkeletonEnemy(x, y);
        }

        enemies.add(enemy);
    }

    public void draw(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
