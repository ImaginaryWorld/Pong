package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Bonus;

public class FieldRenderer {
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture boardTexture, ballTexture, bonusTimeTexture;
	Field field;
	
	float scoreShift, target_scoreShift;
	BitmapFont score_font;
	
	FieldRenderer(Field field)
	{
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		boardTexture = new Texture(Gdx.files.internal("board.png"));
		ballTexture = new Texture(Gdx.files.internal("particle.png"));
		bonusTimeTexture = new Texture(Gdx.files.internal("bonus_time.png"));
		bonusTimeTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.field = field;
		
		score_font = new BitmapFont();
		score_font.setColor(Color.WHITE);
	}
	
	public void render(float time)
	{
		// Clear screen
		Gdx.gl.glClearColor(.25f, .25f, .3f, 1f);

		// Score bar
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		target_scoreShift = -field.player2Board.score + field.player1Board.score;
		scoreShift += (target_scoreShift - scoreShift) * 0.06;
		
		shapeRenderer.begin(ShapeType.Filled);
        // blue player 1
		shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1);
		shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // red player 2
		shapeRenderer.setColor(0.2f, 0.2f, 0.5f, 1);
		shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
		shapeRenderer.end();


		batch.begin();
		
		score_font.draw(batch, Integer.toString(field.player1Board.score), 40, h/2);
		score_font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/2);
		
		// player 1
		batch.draw(boardTexture, field.player1Board.bounds.x, 
				field.player1Board.bounds.y,             
							     0f, 0f, 100f, 30f, 1f, 1f, 0f,
				                 0, 0, 100, 30, false, true);
		// player 2
		batch.draw(boardTexture, field.player2Board.bounds.x, 
				field.player2Board.bounds.y);

		// bonuses
		for (Bonus bonus : field.bonuses){
			if (bonus != null){
				float r = bonus.bounds.radius;
				batch.draw(bonusTimeTexture, bonus.bounds.x - r, bonus.bounds.y - r,
						r*2, r*2);
			}
		}

		// balls
		for (Ball ball : field.balls){
			if (ball != null){
				float r = ball.bounds.radius;
				batch.draw(ballTexture, ball.bounds.x - r, ball.bounds.y - r,
						r*2, r*2);
			}
		}
		
		batch.end();
	}
	
}
