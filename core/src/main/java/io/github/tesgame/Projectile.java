package io.github.tesgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 400f;
    private float radius = 5f;  // Use a circle for the projectile's shape
    private ShapeRenderer shapeRenderer;

    public Projectile(float x, float y, float targetX, float targetY) {
        position = new Vector2(x, y);
        shapeRenderer = new ShapeRenderer();

        // Calculate direction towards the cursor
        Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
        velocity = direction.scl(speed);
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void draw(SpriteBatch batch) {
        batch.end();  // End SpriteBatch before drawing with ShapeRenderer
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);  // Set the color of the projectile
        shapeRenderer.circle(position.x, position.y, radius);  // Draw a circle for the projectile
        shapeRenderer.end();
        batch.begin();  // Restart SpriteBatch after drawing the shape
    }

    // Add getPosition() method to return the position of the projectile
    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
