package io.github.tesgame.Controller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;

import java.util.HashMap;

public class AudioController {

    private static AudioController instance;

    private float sfxVolume = 1f;
    private float musicVolume = 1f;

    private final HashMap<String, Sound> sfxMap = new HashMap<>();
    private final HashMap<String, Music> musicMap = new HashMap<>();

    private AudioController() {}

    public static AudioController getInstance() {
        if (instance == null) {
            instance = new AudioController();
        }
        return instance;
    }

    public void loadSfx(String name, String filename) {
        sfxMap.put(name, Gdx.audio.newSound(Gdx.files.internal("sfx/" + filename)));
    }

    public void loadMusic(String name, String filename) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("sfx/" + filename));
        music.setLooping(true);
        music.setVolume(musicVolume);
        musicMap.put(name, music);
    }

    public void playSfx(String name) {
        Sound sfx = sfxMap.get(name);
        if (sfx != null) sfx.play(sfxVolume);
    }

    public void playMusic(String name) {
        Music music = musicMap.get(name);
        if (music != null && !music.isPlaying()) {
            music.setVolume(musicVolume);
            music.play();
        }
    }

    public void stopMusic(String name) {
        Music music = musicMap.get(name);
        if (music != null) music.stop();
    }

    // Getter/Setter
    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        for (Music music : musicMap.values()) {
            if (music.isPlaying()) {
                music.setVolume(volume);
            }
        }
    }
}
