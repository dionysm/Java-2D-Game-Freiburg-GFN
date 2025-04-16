package io.github.tesgame.Enemies;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class OrcEnemy extends Enemy {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;

    // Eine Liste für eventuelle projizierte Angriffe (falls gewünscht)
    private ArrayList<EnemyProjectile> projectiles;

    public OrcEnemy(float x, float y) {
        super(x, y, 60f, 8); // Speed: 60, Health: 8 (stärker)
        attackRange = 100f; // Etwas größere Reichweite
        attackCooldown = 2f; // 2 Sekunden Cooldown
        isMeleeAttacker = true;
        projectiles = new ArrayList<>();
    }

    @Override
    protected void loadSprites() {
        // Lade das SpriteSheet für den Orc
        spriteSheet = new Texture("sprites/chars/Walk-Enemy3.png");

        // Setze das SpriteSheet in Frames um
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Erstelle Animationen
        animations = new java.util.HashMap<>();

        // Animationen für die Bewegungsrichtungen
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
        // Nahkampfangriff - schwerer Angriff
        player.takeDamage(3);
    }

    @Override
    public void update(float delta, Vector2 playerPosition, Player player) {
        super.update(delta, playerPosition, player);

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

        // Zeichne die Projektile
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
