package io.github.tesgame.Enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.tesgame.Combat.AOEEffect;
import io.github.tesgame.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class GoblinEnemy extends Enemy {
    private ArrayList<AOEEffect> aoeEffects;
    private float aoeAttackCooldown = 3f; // Separate cooldown for AOE attack
    private float aoeAttackTimer = 0f;
    private float aoeChance = 0.8f; // 80% chance to use AOE attack when in range

    // For player movement prediction
    private Vector2 lastPlayerPosition = new Vector2();
    private Vector2 playerVelocity = new Vector2();
    private float updateTimer = 0f;
    private float predictionTime = 1.0f; // How far ahead to predict (seconds)

    public GoblinEnemy(float x, float y) {
        super(x, y, 60f, 3, "sprites/chars/Walk-Tengu.png", 4, 4, "sprites/chars/Dead-Tengu.png");
        attackRange = 50f;
        attackCooldown = 2f; // Short cooldown
        isMeleeAttacker = true;
        aoeEffects = new ArrayList<>();
    }

    @Override
    protected void performAttack(Player player) {
        // Simple melee attack that damages player immediately
        player.takeDamage(1);
    }

    @Override
    public void update(float delta, Vector2 playerPosition, Player player) {
        super.update(delta, playerPosition, player);

        // Wenn tot und Death Effect angezeigt wird, führe keine weiteren Updates durch
        if (isDead() && !isDeathEffectFinished()) {
            return;
        }

        // Update timer to track player movement
        updateTimer += delta;

        // Calculate player velocity for prediction (every 0.1 seconds for stability)
        if (updateTimer >= 0.1f) {
            if (lastPlayerPosition.x != 0 || lastPlayerPosition.y != 0) {
                playerVelocity.x = (playerPosition.x - lastPlayerPosition.x) / updateTimer;
                playerVelocity.y = (playerPosition.y - lastPlayerPosition.y) / updateTimer;
            }
            lastPlayerPosition.set(playerPosition);
            updateTimer = 0;
        }

        // Update AOE attack timer
        aoeAttackTimer += delta;

        // Check if can perform AOE attack
        float distanceToPlayer = position.dst(playerPosition);
        if (aoeAttackTimer >= aoeAttackCooldown && distanceToPlayer <= attackRange * 5f) {
            // Random chance to use AOE
            if (MathUtils.random() < aoeChance) {
                createPredictiveAOE(playerPosition);
                aoeAttackTimer = 0;
            }
        }

        // Update and remove finished AOE effects
        Iterator<AOEEffect> iterator = aoeEffects.iterator();
        while (iterator.hasNext()) {
            AOEEffect aoe = iterator.next();
            aoe.update(delta, player);

            if (aoe.isFinished()) {
                aoe.dispose();
                iterator.remove();
            }
        }
    }

    private void createPredictiveAOE(Vector2 playerPosition) {
        // Predict where the player will be based on their velocity
        Vector2 predictedPosition = new Vector2();

        // Calculate magnitude of player velocity
        float playerSpeed = playerVelocity.len();

        if (playerSpeed > 10) { // Player is moving significantly
            // Place AOE ahead of player's path with some randomness
            predictedPosition.set(
                playerPosition.x + playerVelocity.x * predictionTime * 1.5f,
                playerPosition.y + playerVelocity.y * predictionTime * 1.5f
            );

            // Debug message for predicted position
            System.out.println("Player velocity: " + playerVelocity);
            System.out.println("Predicting player at: " + predictedPosition);
        } else {
            // Player moving slow or standing still - place near them with randomness
            predictedPosition.set(
                playerPosition.x + MathUtils.random(-60, 60),
                playerPosition.y + MathUtils.random(-60, 60)
            );
        }

        // Create AOE at predicted position
        aoeEffects.add(new AOEEffect(predictedPosition.x, predictedPosition.y, 40f, 4f));
        System.out.println("Goblin created predictive AOE at " + predictedPosition.x + ", " + predictedPosition.y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        // Draw all AOE effects only if not dead
        if (!isDead()) {
            for (AOEEffect aoe : aoeEffects) {
                aoe.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (AOEEffect aoe : aoeEffects) {
            aoe.dispose();
        }
    }
}
