package io.github.tesgame.Combat;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Environment.Trees;
import java.util.ArrayList;
import java.util.Iterator;

public class Weapon {
    private ArrayList<Projectile> projectiles;

    public Weapon() {
        projectiles = new ArrayList<>();
    }

    public void update(float delta, float playerX, float playerY, Vector2 targetPosition, Trees trees) {
        // Check if Space key is pressed to fire a projectile
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            // Create a new projectile pointing towards the target position in world coordinates
            // Specifying true means this is a player projectile
            projectiles.add(new Projectile(playerX, playerY, targetPosition.x, targetPosition.y, true));
        }

        // Update all projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);

            // Check for collisions with trees (only player projectiles will be blocked)
            if (trees != null && trees.checkProjectileCollision(p)) {
                iterator.remove(); // Remove the projectile if it hit a tree
                continue;
            }

            // Remove projectiles that are far off-screen
            if (p.getPosition().x < playerX - 1000 || p.getPosition().x > playerX + 1000 ||
                p.getPosition().y < playerY - 1000 || p.getPosition().y > playerY + 1000) {
                iterator.remove();
            }
        }
    }

    // Overload for backward compatibility
    public void update(float delta, float playerX, float playerY, Vector2 targetPosition) {
        update(delta, playerX, playerY, targetPosition, null);
    }

    public void draw(SpriteBatch batch) {
        // Draw all projectiles
        for (Projectile p : projectiles) {
            p.draw(batch);
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void dispose() {
        // Dispose of all projectiles' resources
        for (Projectile p : projectiles) {
            p.dispose();
        }
    }
}
