package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class FieldRenderer {
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture boardTexture, ballTexture;
	Field field;
	private BitmapFont font;
	
	FieldRenderer(Field field)
	{
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		boardTexture = new Texture(Gdx.files.internal("board.png"));
		ballTexture = new Texture(Gdx.files.internal("particle.png"));
		ballTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.field = field;
		
		font = new BitmapFont();
        font.setColor(Color.WHITE);
	}
	
	public void render(float time)
	{
		// Board and ball appears here
		Gdx.gl.glClearColor(.25f, .25f, .3f, 1f);
		
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		float scoreShift = -field.player2Board.score + field.player1Board.score;
		
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1); // blue player 1
		shapeRenderer.rect(0, h/2 - 50 , w/2 + scoreShift, 100);
		
		shapeRenderer.setColor(0.2f, 0.2f, 0.5f, 1); // red player 2
		shapeRenderer.rect(w, h/2 - 50, -w/2 + scoreShift, 100);
		
		shapeRenderer.end();
		
		batch.begin();
		
		font.draw(batch, Integer.toString(field.player1Board.score), 40, h/2);
		font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/2);
		
		// player 1
		batch.draw(boardTexture, field.player1Board.bounds.x, 
				field.player1Board.bounds.y,             
							     0f, 0f, 100f, 30f, 1f, 1f, 0f,
				                 0, 0, 100, 30, false, true); // i need only last true that do flip
		// player 2
		batch.draw(boardTexture, field.player2Board.bounds.x, 
				field.player2Board.bounds.y);
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
