package io.github.tesgame.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class SkeletonEnemy extends Enemy {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;
    private ArrayList<EnemyProjectile> projectiles;

    public SkeletonEnemy(float x, float y) {
        super(x, y, 60f, 5); // Speed: 60, Health: 5
        attackRange = Float.MAX_VALUE; // Can attack from far away
        attackCooldown = 4f; // 4 second cooldown
        isRangedAttacker = true;
        projectiles = new ArrayList<>();
    }

    @Override
    protected void loadSprites() {
        spriteSheet = new Texture("sprites/chars/Skeleton-Walk.png");

        // Hier ändern wir die Berechnungen, damit wir die Zeilen (für Richtungen) und Spalten (für Animationen) richtig trennen
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,  // Jede Frame-Spalte
            spriteSheet.getHeight() / FRAME_ROWS); // Jede Frame-Reihe

        animations = new java.util.HashMap<>();

        // Für jede Richtung eine eigene Animation aus der jeweiligen Spalte erstellen
        for (int col = 0; col < FRAME_COLS; col++) {  // Zeilenweise Animation, nach Spalten
            TextureRegion[] directionFrames = new TextureRegion[FRAME_ROWS];
            for (int row = 0; row < FRAME_ROWS; row++) {
                directionFrames[row] = tmp[row][col];  // Zeilenwerte, die zu einer Richtung gehören
            }

            Animation<TextureRegion> dirAnimation = new Animation<>(0.15f, directionFrames);

            // Die Animationen den Richtungen zuordnen
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
