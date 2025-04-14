package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
    private static final int SPRITESHEET_COLS = 20;
    private static final int SPRITESHEET_ROWS = 9;
    private static final int TILE_WIDTH = 32; //Kachelbreite Pixel
    private static final int TILE_HEIGHT = 32;
    private static final int INITIAL_WIDTH = 40;
    private static final int INITIAL_HEIGHT = 25;

    private Texture spriteSheet;
    private TextureRegion[][] tiles;
    private TextureRegion[][] mapTiles;

    private int mapWidth = INITIAL_WIDTH;
    private int mapHeight = INITIAL_HEIGHT;

    private final int[] allowedTileCols = {0, 1, 2}; // nur diese vier Spalten benutzen
    private final int[] allowedTileRows = {2,3,4,5}; // nur die dritte Zeile (Gras, dreck wasser)

    public Map() {
        spriteSheet = new Texture("sprites/environment.png");

        // Kacheln extrahieren
        tiles = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / SPRITESHEET_COLS,
            spriteSheet.getHeight() / SPRITESHEET_ROWS);

        // Map zufällig mit den erlaubten Kacheln füllen
        generateMap();
    }

    private void generateMap() {
        mapTiles = new TextureRegion[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int indexCol = ThreadLocalRandom.current().nextInt(allowedTileCols.length);
                int indexRow = ThreadLocalRandom.current().nextInt(allowedTileRows.length);
                int tileCol = allowedTileCols[indexCol];
                int tileRow = allowedTileRows[indexRow];
               // System.out.println("Ergebnis Random: " + index);

                mapTiles[y][x] = tiles[tileRow][tileCol];
            }
        }
    }
    public void draw(SpriteBatch batch) {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                batch.draw(mapTiles[y][x], x * TILE_WIDTH, y * TILE_HEIGHT);
            }
        }
    }

    public void dispose() {
        // Dispose of the sprite sheet texture to prevent memory leaks
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

    public void expandMapIfNeeded(Vector2 playerPosition) {
        int tileX = (int)(playerPosition.x / TILE_WIDTH);
        int tileY = (int)(playerPosition.y / TILE_HEIGHT);

        boolean expanded = false;

        if (tileX >= mapWidth - 8) {
            expandRight();
            expanded = true;
        } else if (tileX <= 8) {
            expandLeft();
            expanded = true;
        }

        if (tileY >= mapHeight - 8) {
            expandTop();
            expanded = true;
        } else if (tileY <= 8) {
            expandBottom();
            expanded = true;
        }

        if (expanded) {
           // System.out.println("Map expanded to: " + mapWidth + "x" + mapHeight);
        }
    }

    private void expandRight() {
        mapWidth += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            System.arraycopy(mapTiles[y], 0, newMap[y], 0, mapTiles[y].length);
            newMap[y][mapWidth - 1] = randomTile();
        }
        mapTiles = newMap;
    }

    private void expandLeft() {
        mapWidth += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            System.arraycopy(mapTiles[y], 0, newMap[y], 1, mapTiles[y].length);
            newMap[y][0] = randomTile();
        }
        mapTiles = newMap;
    }

    private void expandTop() {
        mapHeight += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        System.arraycopy(mapTiles, 0, newMap, 0, mapTiles.length);
        for (int x = 0; x < mapWidth; x++) {
            newMap[mapHeight - 1][x] = randomTile();
        }
        mapTiles = newMap;
    }

    private void expandBottom() {
        mapHeight += 1;
        TextureRegion[][] newMap = new TextureRegion[mapHeight][mapWidth];
        System.arraycopy(mapTiles, 0, newMap, 1, mapTiles.length);
        for (int x = 0; x < mapWidth; x++) {
            newMap[0][x] = randomTile();
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
