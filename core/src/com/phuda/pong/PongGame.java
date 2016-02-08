package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

// Class that creates game process
public class PongGame extends Game {
	public Music menuMusic, gameMusic;
	@Override
	public void create() {
		System.out.println("start Game");
		System.out.println("init PongGame");
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-menusong.ogg"));
		menuMusic.setLooping(true);
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song2.ogg"));
		gameMusic.setLooping(true);
		//uncomment this to start game without menu
		//setScreen(new GameScreen(this));
		launchMenu();
	}

	public void launchMenu() {
		setScreen(new MenuScreen(this));
	}
}
