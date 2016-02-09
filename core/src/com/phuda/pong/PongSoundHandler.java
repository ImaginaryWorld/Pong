package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class PongSoundHandler {
    public Music menuMusic, gameMusic;
    private Sound sounds[];
    final public int bump = 0, reflect = 1;

    PongSoundHandler() {
        // Music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-menusong.ogg"));
        menuMusic.setLooping(true);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song2.ogg"));
        gameMusic.setLooping(true);
        // Sounds
        sounds = new Sound[2];
        sounds[bump] = Gdx.audio.newSound(Gdx.files.internal("sounds/bump1.wav"));
        sounds[reflect] = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect1.wav"));
    }

    public void playSound(int soundNum, float pitch) {
        long s = sounds[soundNum].play(0.5f);
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
}
