package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GoblinEnemy extends Enemy {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;

    public GoblinEnemy(float x, float y) {
        super(x, y, 60f, 3); // Speed: 60 (faster), Health: 3 (weaker)
        attackRange = 50f;
        attackCooldown = 2f; // Short cooldown
        isMeleeAttacker = true;
    }

    @Override
    protected void loadSprites() {
        // Load enemy sprite sheet
        spriteSheet = new Texture("sprites/Walk-Enemy2.png");

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
        // Simple melee attack that damages player immediately
        player.takeDamage(1);
    }
}
