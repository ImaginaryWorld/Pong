package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

// Class that creates screen with game
public class PongScreen implements Screen {

	PongGame game;
	public PongSoundHandler soundHandler;

	PongScreen(PongGame game, PongSoundHandler soundHandler) {
		System.out.println("init PongScreen");
		this.game = game;
		this.soundHandler = soundHandler;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
