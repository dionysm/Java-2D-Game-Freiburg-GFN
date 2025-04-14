package io.github.tesgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector2;


public class Weapon {
    private ArrayList<Projectile> projectiles;

    public Weapon() {
        projectiles = new ArrayList<>();
    }

    public void update(float delta, float playerX, float playerY, Vector2 crosshairPosition) {
        // Check if Space key is pressed to fire a projectile
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Create a new projectile pointing towards the crosshair
            projectiles.add(new Projectile(playerX, playerY, crosshairPosition.x, crosshairPosition.y));
        }

        // Update all projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update(delta);

            // Remove projectiles that are far off-screen
            if (p.getPosition().x < -500 || p.getPosition().x > Gdx.graphics.getWidth() + 500 ||
                p.getPosition().y < -500 || p.getPosition().y > Gdx.graphics.getHeight() + 500) {
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
