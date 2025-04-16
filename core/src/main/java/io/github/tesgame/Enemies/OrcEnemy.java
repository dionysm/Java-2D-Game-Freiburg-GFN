package io.github.tesgame.Enemies;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class OrcEnemy extends Enemy {
    // Eine Liste für eventuelle projizierte Angriffe (falls gewünscht)
    private ArrayList<EnemyProjectile> projectiles;

    public OrcEnemy(float x, float y) {
        super(x, y, 60f, 8, "sprites/chars/Walk-Noble.png", 4, 4, "sprites/chars/Dead-Noble.png");
        attackRange = 100f; // Etwas größere Reichweite
        attackCooldown = 2f; // 2 Sekunden Cooldown
        isMeleeAttacker = true;
        projectiles = new ArrayList<>();
    }

    @Override
    protected void performAttack(Player player) {
        // Nahkampfangriff - schwerer Angriff
        player.takeDamage(3);
    }

    @Override
    public void update(float delta, Vector2 playerPosition, Player player) {
        super.update(delta, playerPosition, player);

        // Wenn tot und Death Effect angezeigt wird, führe keine weiteren Updates durch
        if (isDead() && !isDeathEffectFinished()) {
            return;
        }

        // Beispielhafte Projektile (falls Orc auch projizierte Angriffe hat)
        Iterator<EnemyProjectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile projectile = iterator.next();
            projectile.update(delta);

            // Prüfe, ob das Projektil den Spieler getroffen hat
            if (projectile.checkCollision(player)) {
                player.takeDamage(2); // Beispielhafter Schaden durch Projektil
                System.out.println("Projektil traf den Spieler! Spielerleben: " + player.getHealth());
                iterator.remove();
            }

            // Entferne Projektile, die zu weit geflogen sind
            else if (projectile.getDistanceTraveled() > 500) {
                iterator.remove();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        // Zeichne die Projektile nur, wenn nicht tot
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
