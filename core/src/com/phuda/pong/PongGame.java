package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

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
		// Gdx.graphics.setContinuousRendering(false);
	}

	public void launchMenu() {
		setScreen(new MenuScreen(this, soundHandler));
	}

	/*@Override
	public void render() {
		//super.screen.render(0.005f);
		Gdx.app.log("FPS", Integer.toString(Gdx.graphics.getFramesPerSecond()));
		//super.render();
		Gdx.graphics.requestRendering();
		Gdx.graphics.requestRendering();
	}*/
}
