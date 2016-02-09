package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends PongScreen
{
	Field field;
	FieldRenderer renderer;
    int ballsCount, ai, screenWidth, screenHeight;

	GameScreen(PongGame game, int _ballsCount, int _ai, PongSoundHandler soundHandler) {
		super(game, soundHandler);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
        ballsCount = _ballsCount;
        ai = _ai;
		System.out.println("init GameScreen");
	}
	
	public void show()
	{
		field = new Field(this, ballsCount, ai, screenWidth, screenHeight);
		renderer = new FieldRenderer(field);
	}

	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		field.updateState(delta);
		if (game.soundHandler.gameMusic.isPlaying() && field.paused)
			soundHandler.pauseMusic(soundHandler.gameMusic);
		else if (!field.paused && field.startTimer > 3)
			soundHandler.playMusic(soundHandler.gameMusic);
		renderer.render(delta);
	}

	public void dispose() {
		// Stopping music
		soundHandler.stopMusic(soundHandler.gameMusic);
		// Disposing fonts and textures
		disposeRendererParts();
	}

	/*
     * I can't dispose batch and shapeRenderer because of errors happens.
     * In case with batch there even some serious memory errors
     */
	private void disposeRendererParts() {
		renderer.score_font.dispose();
		for (int i = 0; i < renderer.textures.length; i++)
			renderer.textures[i].dispose();
	}
}
