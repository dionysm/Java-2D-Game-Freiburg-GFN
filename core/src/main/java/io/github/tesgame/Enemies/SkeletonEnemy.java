package io.github.tesgame.Enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class SkeletonEnemy extends Enemy {
    private ArrayList<EnemyProjectile> projectiles;

    public SkeletonEnemy(float x, float y) {
        super(x, y, 60f, 5, "sprites/chars/Walk-Skeleton.png", 4, 4, "sprites/chars/Dead-Skeleton.png");
        attackRange = Float.MAX_VALUE; // Can attack from far away
        attackCooldown = 4f; // 4 second cooldown
        isRangedAttacker = true;
        projectiles = new ArrayList<>();
    }

    @Override
    protected void performAttack(Player player) {
        // Launch a bone projectile toward the player
        Vector2 playerPos = player.getPosition();
        EnemyProjectile projectile = new EnemyProjectile(position.x, position.y,
            playerPos.x, playerPos.y, "bone");
        projectiles.add(projectile);
        System.out.println("Skeleton shot a bone at player!");
    }

    @Override
    public void update(float delta, Vector2 playerPosition, Player player) {
        super.update(delta, playerPosition, player);

        // Wenn tot und Death Effect angezeigt wird, führe keine weiteren Updates durch
        if (isDead() && !isDeathEffectFinished()) {
            return;
        }

        // Update projectiles
        Iterator<EnemyProjectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile projectile = iterator.next();
            projectile.update(delta);

            // Check if projectile hit player
            if (projectile.checkCollision(player)) {
                player.takeDamage(1);
                System.out.println("Bone hit player! Player health: " + player.getHealth());
                iterator.remove();
            }

            // Remove projectiles that have traveled too far
            else if (projectile.getDistanceTraveled() > 500) {
                iterator.remove();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        // Draw projectiles only if not dead
        if (!isDead()) {
            for (EnemyProjectile projectile : projectiles) {
                projectile.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (EnemyProjectile projectile : projectiles) {
            projectile.dispose();
        }
    }
}
