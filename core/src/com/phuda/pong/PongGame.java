package com.phuda.pong;

import com.badlogic.gdx.Game;

// Class that creates game process
public class PongGame extends Game {
	public PongSoundHandler soundHandler;
	@Override
	public void create() {
		System.out.println("start Game");
		System.out.println("init PongGame");
		soundHandler = new PongSoundHandler();
		//uncomment this to start game without menu
		//setScreen(new GameScreen(this));
		launchMenu();
	}

	public void launchMenu() {
		setScreen(new MenuScreen(this, soundHandler));
	}
}
