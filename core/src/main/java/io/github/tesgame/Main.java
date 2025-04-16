package io.github.tesgame;

import com.badlogic.gdx.Game;
import io.github.tesgame.GUI.MainMenu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void  create() {
        setScreen(new MainMenu(this));
    }
}
