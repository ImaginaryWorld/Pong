package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class PongSoundHandler {
    public Music menuMusic, gameMusic;
    private Sound sounds[];
    final public int bump = 0, reflect = 1, wallHit = 2, buttonSound = 3, sliderSound = 4;
    private float soundsVolume;

    PongSoundHandler() {
        // Music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-menusong.ogg"));
        menuMusic.setLooping(true);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song.ogg"));
        gameMusic.setLooping(true);
        // Sounds
        sounds = new Sound[5];
        sounds[bump] = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
        sounds[reflect] = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
        sounds[wallHit] = Gdx.audio.newSound(Gdx.files.internal("sounds/wallHit.wav"));
        sounds[buttonSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonSound.wav"));
        sounds[sliderSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/sliderSound.wav"));
    }

    public void playSound(int soundNum, float pitch) {
        long s = sounds[soundNum].play(soundsVolume);
        sounds[soundNum].setPitch(s, pitch);
    }

    public void playMusic(Music music) {
        music.play();
    }

    public void pauseMusic(Music music) {
        music.pause();
    }

    public void stopMusic(Music music) {
        music.stop();
    }

    public void setSoundsVolume(int volume) {
        soundsVolume = (float)volume / 100;
    }

    public void setMusicVolume(int volume) {
        menuMusic.setVolume((float)volume / 100);
        gameMusic.setVolume((float)volume / 100);
    }
}
