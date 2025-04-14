package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

    private final int[] allowedTileCols = {0, 1, 2}; // nur diese drei Spalten benutzen
    private final int allowedTileRow = 3; // nur die dritte Zeile (Gras, dreck wasser)

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
                int tileCol = allowedTileCols[(int) (Math.random() * allowedTileCols.length)];
                mapTiles[y][x] = tiles[allowedTileRow][tileCol];
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
}
