package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

// Class that creates game process
public class PongGame extends Game {

	@Override
	public void create() {
		System.out.println("start Game");
		System.out.println("init PongGame");

		//uncomment this to start game without menu
		//setScreen(new GameScreen(this));
		setScreen(new MenuScreen(this));
	}
}
