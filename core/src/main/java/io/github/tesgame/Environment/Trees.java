package io.github.tesgame.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Trees {
    private final List<Vector2> treePositions = new ArrayList<>();
    private final List<Rectangle> treeColliders = new ArrayList<>();
    private final TextureRegion treeStem;
    private final TextureRegion treeCrown;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private final float COLLIDER_WIDTH = 24; // Smaller than visual size for better gameplay
    private final float COLLIDER_HEIGHT = 16; // Just the trunk part should block

    public Trees(TextureRegion[][] tiles) {
        // Store tree textures
        treeStem = tiles[1][0];
        treeCrown = tiles[0][0];
    }

    public void addTree(int x, int y, int offsetX, int offsetY) {
        treePositions.add(new Vector2(x, y));

        // Create collision box for the tree
        Rectangle collider = new Rectangle(
            (x - offsetX) * TILE_WIDTH + (TILE_WIDTH - COLLIDER_WIDTH) / 2, // Centered
            (y - offsetY) * TILE_HEIGHT, // Bottom of tile
            COLLIDER_WIDTH,
            COLLIDER_HEIGHT
        );
        treeColliders.add(collider);
    }

    public void updateTreePositions(int offsetX, int offsetY) {
        // Update all tree collider positions when map offset changes
        for (int i = 0; i < treePositions.size(); i++) {
            Vector2 pos = treePositions.get(i);
            Rectangle collider = treeColliders.get(i);

            collider.x = (pos.x - offsetX) * TILE_WIDTH + (TILE_WIDTH - COLLIDER_WIDTH) / 2;
            collider.y = (pos.y - offsetY) * TILE_HEIGHT;
        }
    }

    public void drawTreeStems(SpriteBatch batch, int offsetX, int offsetY) {
        for (Vector2 pos : treePositions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            batch.draw(treeStem, (x - offsetX) * TILE_WIDTH, (y - offsetY) * TILE_HEIGHT);
        }
    }

    public void drawTreeCrowns(SpriteBatch batch, int offsetX, int offsetY) {
        for (Vector2 pos : treePositions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            batch.draw(treeCrown, (x - offsetX) * TILE_WIDTH, ((y + 1) - offsetY) * TILE_HEIGHT);
        }
    }

    public boolean isColliding(Rectangle playerBounds) {
        // Check if player collides with any tree
        for (Rectangle treeCollider : treeColliders) {
            if (treeCollider.overlaps(playerBounds)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        treePositions.clear();
        treeColliders.clear();
    }

    public void adjustTreePositions(int offsetXChange, int offsetYChange) {
        // Adjust all tree positions when expanding the map
        for (Vector2 pos : treePositions) {
            pos.x += offsetXChange;
            pos.y += offsetYChange;
        }
    }
}
