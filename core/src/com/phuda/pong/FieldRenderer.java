package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;
import com.sun.javafx.geom.Vec2f;

public class FieldRenderer {
	final int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
    Texture boardRedTexture, boardBlueTexture, ballTexture;
    Texture bonusTimeTexture, bonusSplitterTexture;
    Field field;

	float scoreShift, target_scoreShift;
	BitmapFont score_font;
	
	FieldRenderer(Field field)
	{
        // Initialize some stuff
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

        String images_path = "images_hi/";
		boardRedTexture = new Texture(Gdx.files.internal(images_path + "board_red.png"));
        boardBlueTexture = new Texture(Gdx.files.internal(images_path + "board_blue.png"));
		ballTexture = new Texture(Gdx.files.internal(images_path + "particle.png"));
        ballTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bonusTimeTexture = new Texture(Gdx.files.internal(images_path + "bonus_time.png"));
        bonusTimeTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        bonusSplitterTexture = new Texture(Gdx.files.internal(images_path + "bonus_splitter.png"));
        bonusSplitterTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

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
        shapeRenderer.setColor(0.2f, 0.2f, 1.0f, 0.5f);
        shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // red player 2
        shapeRenderer.setColor(1.0f, 0.2f, 0.2f, 0.5f);
        shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
        // abilities bars
        // time ability
        shapeRenderer.setColor(0.2f, 0.5f, 1f, 0.2f); // blue
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[0].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[0].timer / 10);
        // splitter ability
        shapeRenderer.setColor(0.2f, 1f, 0.5f, 0.2f); // green
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[1].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[1].timer / 10);
        // toggle alpha blending. end function uses this to draw transparency
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


		batch.begin();
		
		score_font.draw(batch, Integer.toString(field.player1Board.score), 40, h/2);
		score_font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/2);
		// Boards
		drawBoards(field.player1Board, field.player2Board);


		// balls
		for (Ball ball : field.balls){
			if (ball != null){
				float r = ball.bounds.radius;
                // Trail rendering
                float alpha = 0.2f;
                for (Vector2 vec : ball.positionsHistory) {
                    batch.setColor(1f, 1f, 1f, alpha);
                    float size = alpha * 2;
                    batch.draw(ballTexture, vec.x - r * size, vec.y - r * size,
                            r * 2 * size, r * 2 * size);
                    alpha += 0.02f;
                }
                // Reset alpha
                batch.setColor(1f, 1f, 1f, 1f);
                batch.draw(ballTexture, ball.bounds.x - r, ball.bounds.y - r, r * 2, r * 2);
            }
		}

		// bonuses
		for (Bonus bonus : field.bonuses){
			if (bonus != null){
                Texture tex = bonusTimeTexture;
                if (bonus.name.equals("timeSlower"))
                    tex = bonusTimeTexture;
                else if (bonus.name.equals("ballSplitter"))
                    tex = bonusSplitterTexture;

				float r = bonus.bounds.radius;
                batch.draw(tex, bonus.bounds.x - r, bonus.bounds.y - r,
                        r, r, r*2, r*2, 1, 1, bonus.rotation, 0, 0,
                        bonusTimeTexture.getWidth(), bonusTimeTexture.getHeight(), false, false);
			}
		}
		batch.end();
	}

	private void drawBoards(Board board1, Board board2) {
		// player 1
		batch.draw(boardRedTexture, board1.bounds.x, board1.bounds.y, board1.bounds.width, board1.bounds.height);
		// player 2
		batch.draw(boardBlueTexture, board2.bounds.x, board2.bounds.y, board2.bounds.width, board2.bounds.height);
	}

}
