package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Iterator;

public class Weapon {
    private ArrayList<Projectile> projectiles;

    public Weapon() {
        projectiles = new ArrayList<>();
    }

    public void update(float delta, float playerX, float playerY) {
        // Check if Space key is pressed to fire a projectile
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float mouseX = Gdx.input.getX(); // Get the mouse X position
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Get the mouse Y position (adjusted for screen coordinates)
            projectiles.add(new Projectile(playerX, playerY, mouseX, mouseY)); // Create a new projectile pointing towards the mouse cursor
        }

        // Update all projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);

            // Remove projectiles that are off-screen or out of bounds
            if (p.getPosition().x < 0 || p.getPosition().x > Gdx.graphics.getWidth() ||
                p.getPosition().y < 0 || p.getPosition().y > Gdx.graphics.getHeight()) {
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
