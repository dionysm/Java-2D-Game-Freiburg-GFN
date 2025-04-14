package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

public class Weapon {
    private ArrayList<Projectile> projectiles;

    public Weapon() {
        projectiles = new ArrayList<>();
    }

    public void update(float delta, float playerX, float playerY, Vector2 targetPosition) {
        // Check if Space key is pressed to fire a projectile
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            // Create a new projectile pointing towards the target position in world coordinates
            projectiles.add(new Projectile(playerX, playerY, targetPosition.x, targetPosition.y));
        }

        // Update all projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);

            // Remove projectiles that are far off-screen
            if (p.getPosition().x < playerX - 1000 || p.getPosition().x > playerX + 1000 ||
                p.getPosition().y < playerY - 1000 || p.getPosition().y > playerY + 1000) {
                iterator.remove();
            }
        }
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
