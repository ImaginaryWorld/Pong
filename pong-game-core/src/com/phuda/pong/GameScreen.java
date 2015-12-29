package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends PongScreen
{
	Field field;
	FieldRenderer renderer;

	GameScreen(Game game)
	{
		super(game);
	}
	
	public void show()
	{
		field = new Field();
		renderer = new FieldRenderer(field);
	}

	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// I steal this construction from one of the demo-codes
		// Just wondering why it needs to be done this way
		delta = Math.min(0.07f, Gdx.graphics.getDeltaTime());
		// Updating
		field.updateState(delta);
		renderer.render(delta);
	}
	
}
