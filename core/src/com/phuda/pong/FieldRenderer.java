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
		
		// player 1
		batch.draw(boardTexture, field.player1Board.x, 
							     field.player1Board.y,             
							     0f, 0f,
				                 100f, 30f,    // there is better way to argument only what i want??!
				                 1f, 1f,
				                 0f,
				                 0, 0, 100, 30, false, true); // i need only last true that do flip
		// player 2
		batch.draw(boardTexture, field.player2Board.x,
							     field.player2Board.y);
		// balls
		for (int i = 0; i < field.balls.length; i++){
			if (field.balls[i] != null){
				batch.draw(ballTexture, (float)field.balls[i].x, (float)field.balls[i].y);
			}
		}
		
		batch.end();
	}
	
}
