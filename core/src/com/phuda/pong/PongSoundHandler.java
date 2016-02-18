package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class PongSoundHandler {
    private Music music[];

    private Sound sounds[];
    final public int bump = 0, reflect = 1, wallHit = 2, bonusSound = 3,
    goalSound = 4, buttonSound = 5, sliderSound = 6, winnerSound = 7,
    menuMusic = 0, gameMusic = 1;
    private float soundsVolume;

    PongSoundHandler() {
        // Music
        music = new Music[2];
        music[menuMusic] = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu-song.ogg"));
        music[menuMusic].setLooping(true);
        music[gameMusic] = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song.ogg"));
        music[gameMusic].setLooping(true);
        // Sounds
        sounds = new Sound[8];
        sounds[bump] = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
        sounds[reflect] = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
        sounds[wallHit] = Gdx.audio.newSound(Gdx.files.internal("sounds/wallHit.wav"));
        sounds[bonusSound] = Gdx.audio.newSound(Gdx.files.internal(("sounds/bonusSound.wav")));
        sounds[goalSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/goalSound.wav"));
        sounds[buttonSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonSound.wav"));
        sounds[sliderSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/sliderSound.wav"));
        sounds[winnerSound] = Gdx.audio.newSound(Gdx.files.internal("sounds/winnerSound.wav"));
    }

    public void playSound(int soundNum, float pitch) {
        long s = sounds[soundNum].play(soundsVolume);
        sounds[soundNum].setPitch(s, pitch);
    }

    public void playMusic(int musicNum) {
        music[musicNum].play();
    }

    public void pauseMusic(int musicNum) {
        music[musicNum].pause();
    }

    public void stopMusic(int musicNum) {
        music[musicNum].stop();
    }

    public void setSoundsVolume(int volume) {
        soundsVolume = (float)volume / 100;
    }

    public void setMusicVolume(int volume) {
        for (Music track : music)
            track.setVolume((float)volume / 100);
    }

    public boolean isPlaying(int musicNum) {
        return music[musicNum].isPlaying();
    }
}
