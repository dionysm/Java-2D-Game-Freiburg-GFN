package io.github.tesgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private float spawnTimer = 0;
    private float spawnRate = 2.0f; // Seconds between spawns
    private float spawnDistance = 300; // Distance from player to spawn enemies
    private int maxEnemies = 50; // Maximum number of enemies at once

    public EnemyManager() {
        enemies = new ArrayList<>();
        // Spawn initial enemies
        for (int i = 0; i < 5; i++) {
            // Spawn at random positions on screen
            float x = MathUtils.random(100, 700);
            float y = MathUtils.random(100, 500);
            enemies.add(new Enemy(x, y));
        }

    }

    public void update(float delta, Vector2 playerPosition, Weapon playerWeapon) {
        // Update spawn timer
        spawnTimer += delta;

        // Spawn new enemies if it's time and we're under the limit
        if (spawnTimer >= spawnRate && enemies.size() < maxEnemies) {
            spawnTimer = 0;
            spawnEnemy(playerPosition);
        }

        // Update all enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(delta, playerPosition);

            // Remove dead enemies
            if (enemy.isDead()) {
                iterator.remove();
            }
        }

        // Check collisions with player weapons
        ArrayList<Projectile> projectiles = playerWeapon.getProjectiles();
        Iterator<Projectile> projectileIterator = projectiles.iterator();

        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            boolean hitEnemy = false;

            for (Enemy enemy : enemies) {
                if (!enemy.isDead() && enemy.checkCollision(projectile)) {
                    enemy.takeDamage(20); // Damage amount
                    hitEnemy = true;
                    break; // A projectile can only hit one enemy
                }
            }

            if (hitEnemy) {
                projectileIterator.remove(); // Only remove if it hit an enemy
            }
        }
    }

    private void spawnEnemy(Vector2 playerPosition) {
        // Generate a random angle
        float angle = MathUtils.random(360f);
        float radians = (float) Math.toRadians(angle);

        // Calculate spawn position at the given distance and angle from player
        float spawnX = playerPosition.x + (float) Math.cos(radians) * spawnDistance;
        float spawnY = playerPosition.y + (float) Math.sin(radians) * spawnDistance;

        // Debug output
        System.out.println("Spawning enemy at: " + spawnX + ", " + spawnY);

        // Create and add the enemy
        enemies.add(new Enemy(spawnX, spawnY));
        System.out.println("Total enemies: " + enemies.size());
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
