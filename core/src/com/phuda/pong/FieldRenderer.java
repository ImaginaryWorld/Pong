package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class FieldRenderer {
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture boardTexture, ballTexture;
	Field field;
	
	FieldRenderer(Field field)
	{
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		boardTexture = new Texture(Gdx.files.internal("board.png"));
		ballTexture = new Texture(Gdx.files.internal("particle.png"));
		ballTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.field = field;
	}
	
	public void render(float time)
	{
		// Board and ball appears here
		Gdx.gl.glClearColor(.25f, .25f, .3f, 1f);
		
		float allScores = field.player1Board.score + field.player2Board.score;
		float p1 = field.player1Board.score / allScores;
		float p2 = field.player2Board.score / allScores;
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getWidth();
		
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1); // blue player 1
		shapeRenderer.rect(0, h/2 + 50, w * p1, 100);
		
		shapeRenderer.setColor(0.2f, 0.2f, 0.5f, 1); // red player 2
		shapeRenderer.rect(w, h/2 + 50, -w * p2, 100);
		
		shapeRenderer.end();
		
		batch.begin();
		
		// player 1
		batch.draw(boardTexture, field.player1Board.x, 
							     field.player1Board.y,             
							     0f, 0f, 100f, 30f, 1f, 1f, 0f,
				                 0, 0, 100, 30, false, true); // i need only last true that do flip
		// player 2
		batch.draw(boardTexture, field.player2Board.x,
							     field.player2Board.y);
		// balls
		for (int i = 0; i < field.balls.length; i++){
			if (field.balls[i] != null){
				//batch.draw(ballTexture, (float)(field.balls[i].bounds.x), 
				//		(float)(field.balls[i].bounds.y));
				batch.draw(ballTexture, field.balls[i].bounds.x, field.balls[i].bounds.y, 
						field.balls[i].bounds.radius*2, field.balls[i].bounds.radius*2);
			}
		}
		
		batch.end();
	}
	
}
