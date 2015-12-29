package com.phuda.pong;

import com.badlogic.gdx.Game;

// Class that creates game process
public class PongGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}
}
