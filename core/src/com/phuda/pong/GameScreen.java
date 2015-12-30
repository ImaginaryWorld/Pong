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
		System.out.println("init GameScreen");
	}
	
	public void show()
	{
		field = new Field();
		renderer = new FieldRenderer(field);
	}

	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//delta = Math.min(0.07f, Gdx.graphics.getDeltaTime());
		field.updateState(delta);
		renderer.render(delta);
	}
	
}
