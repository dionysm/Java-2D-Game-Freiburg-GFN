package io.github.tesgame.Enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.*;
import io.github.tesgame.Combat.Projectile;
import io.github.tesgame.Combat.Weapon;
import io.github.tesgame.GUI.ScoreDisplay;

import java.util.ArrayList;
import java.util.Iterator;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private float spawnTimer = 0;
    private float initialDelay = 2.0f; // 2-second initial delay
    private boolean initialSpawnDone = false; // Track if initial spawn happened
    private float spawnRate = 2.0f;
    private float baseSpawnRate = 2.0f; // Store the initial spawn rate
    private float spawnDistance = 300;
    private int maxEnemies = 50;
    private ScoreDisplay scoreDisplay;
    private int previousScore = 0; // Track previous score to detect changes
    private int difficultyLevel = 0; // Track how many times we've increased difficulty

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

        // Check if score has increased by 10 since last difficulty increase
        int currentScore = scoreDisplay.getScore();
        if (currentScore >= (difficultyLevel + 1) * 10) {
            // Increase difficulty
            difficultyLevel++;

            // Decrease spawn rate (make enemies spawn faster)
            spawnRate = Math.max(baseSpawnRate - (difficultyLevel * 0.1f), 0.5f);

            // Increase spawn count (more enemies at once)
            maxEnemies = Math.min(50 + (difficultyLevel * 2), 100);

            System.out.println("Difficulty increased! Level: " + difficultyLevel +
                ", Spawn rate: " + spawnRate +
                ", Max enemies: " + maxEnemies);
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

            // Handle scoring and healing when enemy dies
            if (enemy.isDead() && !enemy.isRewardGiven()) {
                player.heal(1);
                scoreDisplay.incrementScore();
                enemy.setRewardGiven(true); // Mark rewards as given
            }

            // Only remove enemies when they're fully dead AND their death effect is finished
            if (enemy.isDead() && enemy.isDeathEffectFinished()) {
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
        // Adjust enemy type probabilities based on difficulty
        float skeletonChance = 0.33f;
        float goblinChance = 0.33f;
        float orcChance = 0.33f;

        // As difficulty increases, spawn more difficult enemies
        if (difficultyLevel > 5) {
            skeletonChance = 0.3f;
            goblinChance = 0.3f;
            orcChance = 0.4f;
        }
        if (difficultyLevel > 10) {
            skeletonChance = 0.25f;
            goblinChance = 0.35f;
            orcChance = 0.4f;
        }

        // Choose enemy type based on probabilities
        float roll = MathUtils.random();
        Enemy enemy;

        if (roll < skeletonChance) {
            enemy = new SkeletonEnemy(x, y);
        } else if (roll < skeletonChance + goblinChance) {
            enemy = new GoblinEnemy(x, y);
        } else {
            enemy = new OrcEnemy(x, y);
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
