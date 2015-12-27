package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FieldRenderer {
	
	SpriteBatch batch;
	Texture boardTexture, ballTexture;
	Field field;
	
	FieldRenderer(Field field)
	{
		batch = new SpriteBatch();
		boardTexture = new Texture(Gdx.files.internal("board.jpg"));
		ballTexture = new Texture(Gdx.files.internal("particle.png"));
		this.field = field;
	}
	
	public void render(float time)
	{
		// Board and ball appears here
		batch.begin();
		batch.draw(boardTexture, field.playerBoard.x,
							     field.playerBoard.y);
		for (int i = 0; i < 100; i++){
			batch.draw(ballTexture, (float)field.balls[i].x, (float)field.balls[i].y);
		}
		batch.end();
	}
	
}
