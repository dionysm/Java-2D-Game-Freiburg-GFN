package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
    private static final int SPRITESHEET_COLS = 20;
    private static final int SPRITESHEET_ROWS = 9;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int INITIAL_WIDTH = 64;
    private static final int INITIAL_HEIGHT = 36;
    private final List<Vector2> treePositions = new ArrayList<>();
    private final Texture spriteSheet;
    private final TextureRegion[][] tiles;
    private TextureRegion[][] mapTiles;
    private int offsetX = 0;
    private int offsetY = 0;
    private int mapWidth = INITIAL_WIDTH;
    private int mapHeight = INITIAL_HEIGHT;
    // Erlaubte Tilesets TODO: Auto Tile Mapping
    private final int[] allowedTileCols = {0, 1, 2};
    private final int[] allowedTileRows = {2, 3, 4, 5};

    public Map() {
        spriteSheet = new Texture("sprites/environment/environment.png");

        tiles = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / SPRITESHEET_COLS,
                spriteSheet.getHeight() / SPRITESHEET_ROWS);

        generateMap();
        offsetX = mapWidth / 2;
        offsetY = mapHeight / 2;
    }

    private void generateMap() {
        mapTiles = new TextureRegion[mapHeight][mapWidth];
        treePositions.clear();

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (y < mapHeight - 1 && ThreadLocalRandom.current().nextDouble() < 0.02) {
                    treePositions.add(new Vector2(x, y));
                }

                mapTiles[y][x] = randomTile();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                batch.draw(mapTiles[y][x], (x - offsetX) * TILE_WIDTH, (y - offsetY) * TILE_HEIGHT);
            }
        }
    }

    public void drawTreeStems(SpriteBatch batch) {
        for (Vector2 pos : treePositions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            batch.draw(tiles[1][0], (x - offsetX) * TILE_WIDTH, (y - offsetY) * TILE_HEIGHT);
        }
    }

    public void drawTreeCrowns(SpriteBatch batch) {
        for (Vector2 pos : treePositions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            batch.draw(tiles[0][0], (x - offsetX) * TILE_WIDTH, ((y + 1) - offsetY) * TILE_HEIGHT);
        }
    }

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

    public void expandMapIfNeeded(Vector2 playerPosition) {
        int tileX = (int)(playerPosition.x / TILE_WIDTH) + offsetX;
        int tileY = (int)(playerPosition.y / TILE_HEIGHT) + offsetY;

        if (tileX >= mapWidth - 24) {
            expandRight();
        } else if (tileX <= 24) {
            expandLeft();
        }

        if (tileY >= mapHeight - 24) {
            expandTop();
        } else if (tileY <= 24) {
            expandBottom();
        }
    }

    private void expandRight() {
        mapWidth += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            System.arraycopy(mapTiles[y], 0, newMap[y], 0, mapTiles[y].length);
            newMap[y][mapWidth - 1] = randomTile();

            if (y < mapHeight - 1 && ThreadLocalRandom.current().nextDouble() < 0.02) {
                treePositions.add(new Vector2(mapWidth - 1, y));
            }
        }
        mapTiles = newMap;
    }

    private void expandLeft() {
        mapWidth += 1;
        offsetX += 1;
        for (Vector2 pos : treePositions) {
            pos.x += 1;
        }
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            System.arraycopy(mapTiles[y], 0, newMap[y], 1, mapTiles[y].length);
            newMap[y][0] = randomTile();

            if (y < mapHeight - 1 && ThreadLocalRandom.current().nextDouble() < 0.02) {
                treePositions.add(new Vector2(0, y));
            }
        }
        mapTiles = newMap;
    }

    private void expandTop() {
        mapHeight += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        System.arraycopy(mapTiles, 0, newMap, 0, mapTiles.length);
        for (int x = 0; x < mapWidth; x++) {
            newMap[mapHeight - 1][x] = randomTile();

            if (ThreadLocalRandom.current().nextDouble() < 0.02) {
                treePositions.add(new Vector2(x, mapHeight - 2));
            }
        }
        mapTiles = newMap;
    }

    private void expandBottom() {
        mapHeight += 1;
        offsetY += 1;
        for (Vector2 pos : treePositions) {
            pos.y += 1;
        }

        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        System.arraycopy(mapTiles, 0, newMap, 1, mapTiles.length);
        for (int x = 0; x < mapWidth; x++) {
            newMap[0][x] = randomTile();

            if (ThreadLocalRandom.current().nextDouble() < 0.02) {
                treePositions.add(new Vector2(x, 0));
            }
        }
        mapTiles = newMap;
    }

    private TextureRegion randomTile() {
        int indexCol = ThreadLocalRandom.current().nextInt(allowedTileCols.length);
        int indexRow = ThreadLocalRandom.current().nextInt(allowedTileRows.length);
        int tileCol = allowedTileCols[indexCol];
        int tileRow = allowedTileRows[indexRow];
        return tiles[tileRow][tileCol];
    }
}
