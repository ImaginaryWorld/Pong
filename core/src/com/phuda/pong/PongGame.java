package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

// Class that creates game process
public class PongGame extends Game {
	public Music music;
	@Override
	public void create() {
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song.ogg"));
		music.setLooping(true);
		System.out.println("start Game");
		System.out.println("init PongGame");

		//uncomment this to start game without menu
		//setScreen(new GameScreen(this));
		setScreen(new MenuScreen(this));
	}
}
