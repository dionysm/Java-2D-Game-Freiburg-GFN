package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

public class SkeletonEnemy extends Enemy {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;
    private ArrayList<EnemyProjectile> projectiles;

    public SkeletonEnemy(float x, float y) {
        super(x, y, 40f, 5); // Speed: 40, Health: 5
        attackRange = 250f; // Can attack from far away
        attackCooldown = 5f; // 5 second cooldown
        isRangedAttacker = true;
        projectiles = new ArrayList<>();
    }

    @Override
    protected void loadSprites() {
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

        // Draw projectiles
        for (EnemyProjectile projectile : projectiles) {
            projectile.draw(batch);
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
