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
    String images_path;
	ShapeRenderer shapeRenderer;
    Texture boardRedTexture, boardBlueTexture, ballTexture, bonusTimeTexture;
    Field field;

	float scoreShift, target_scoreShift;
	BitmapFont score_font;
	
	FieldRenderer(Field field)
	{
        // Initialize some stuff
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

        if (Gdx.graphics.getWidth() >= 999)
            images_path = "images_hi/";
        else
            images_path = "images_low/";
		boardRedTexture = new Texture(Gdx.files.internal(images_path + "board_red.png"));
        boardBlueTexture = new Texture(Gdx.files.internal(images_path + "board_blue.png"));
		ballTexture = new Texture(Gdx.files.internal(images_path + "particle.png"));
		bonusTimeTexture = new Texture(Gdx.files.internal(images_path + "bonus_time.png"));
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
        shapeRenderer.setColor(0.2f, 0.2f, 0.5f, 1);
        shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // red player 2
        shapeRenderer.setColor(0.5f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
        // abilities bars
        shapeRenderer.setColor(0.2f, 0.5f, 0.7f, 1);
		// That's just a crutch, but later there can be more than one bonus
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[0].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[0].timer / 10);
        shapeRenderer.end();


		batch.begin();
		
		score_font.draw(batch, Integer.toString(field.player1Board.score), 40, h/2);
		score_font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/2);
		
		// player 1
		batch.draw(boardRedTexture, field.player1Board.bounds.x,
				field.player1Board.bounds.y);
		// player 2
		batch.draw(boardBlueTexture, field.player2Board.bounds.x,
				field.player2Board.bounds.y);

		// balls
		for (Ball ball : field.balls){
			if (ball != null){
				float r = ball.bounds.radius;
				batch.draw(ballTexture, ball.bounds.x - r, ball.bounds.y - r, r*2, r*2);
			}
		}

		// bonuses
		for (Bonus bonus : field.bonuses){
			if (bonus != null){
                Texture tex = bonusTimeTexture;
                if (bonus.name.equals("timeSlower"))
                    tex = bonusTimeTexture;

				float r = bonus.bounds.radius;
                batch.draw(tex, bonus.bounds.x - r, bonus.bounds.y - r,
                        r, r, r*2, r*2, 1, 1, bonus.rotation, 0, 0,
                        bonusTimeTexture.getWidth(), bonusTimeTexture.getHeight(), false, false);
			}
		}
		batch.end();
	}
	
}
