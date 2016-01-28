package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends PongScreen
{
	Field field;
	FieldRenderer renderer;
    int ballsCount, ai, screenWidth, screenHeight;

	GameScreen(Game game, int _ballsCount, int _ai)
	{
		super(game);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
        ballsCount = _ballsCount;
        ai = _ai;
		System.out.println("init GameScreen");
	}
	
	public void show()
	{
		field = new Field(ballsCount, ai, screenWidth, screenHeight);
		renderer = new FieldRenderer(field);
	}

	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		field.updateState(delta);
		renderer.render(delta);
	}
	
}
