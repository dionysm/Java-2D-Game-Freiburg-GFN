package io.github.tesgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private Sprite sprite;

    public Player() {
        Texture texture = new Texture("walk.png"); // Bild im assets-Ordner
        sprite = new Sprite(texture);
        sprite.setPosition(100, 100); // Anfangsposition
        sprite.setSize(64, 64); // Größe anpassen (optional)
    }

    public void update(float deltaTime) {
        // Hier könntest du Bewegungslogik einbauen
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
