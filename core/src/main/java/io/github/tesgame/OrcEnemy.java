package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OrcEnemy extends Enemy {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;

    public OrcEnemy(float x, float y) {
        super(x, y, 30f, 8); // Speed: 30 (slower), Health: 8 (tougher)
        attackRange = 60f; // Slightly larger attack range
        attackCooldown = 5f; // 5 second cooldown
        isMeleeAttacker = true;
    }

    @Override
    protected void loadSprites() {
        // Load enemy sprite sheet
        spriteSheet = new Texture("sprites/Walk-Enemy3.png");

        // Set up animation with the sprite sheet
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);

        // Create animation frames (using the first row for walking animation)
        TextureRegion[] frames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) {
            frames[i] = tmp[0][i]; // Using first row (index 0)
        }

        animation = new Animation<>(0.15f, frames);

        // Set dimensions
        width = tmp[0][0].getRegionWidth();
        height = tmp[0][0].getRegionHeight();
    }

    @Override
    protected void performAttack(Player player) {
        // Heavy melee attack
        player.takeDamage(1);
    }
}
