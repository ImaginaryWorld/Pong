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
		boardTexture = new Texture(Gdx.files.internal("board.png"));
		ballTexture = new Texture(Gdx.files.internal("particle.png"));
		this.field = field;
	}
	
	public void render(float time)
	{
		// Board and ball appears here
		Gdx.gl.glClearColor(.1f, .3f, .3f, 1);
		batch.begin();
		batch.draw(boardTexture, field.playerBoard.x,
							     field.playerBoard.y);
		for (int i = 0; i < 100; i++){
			if (field.balls[i] != null){
				batch.draw(ballTexture, (float)field.balls[i].x, (float)field.balls[i].y);
			}
		}
		batch.end();
	}
	
}
