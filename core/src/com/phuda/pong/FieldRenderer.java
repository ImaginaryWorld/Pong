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
    Texture bonusTimeTexture, bonusSplitterTexture, bonusControllerTexture;
    Texture backGround;
    float backGroundRotation;
    Field field;

	float scoreShift, target_scoreShift;
	BitmapFont score_font;
	
	FieldRenderer(Field field)
	{
        // Initialize some stuff
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

        String images_path = "images_hi/";
        backGround = new Texture(Gdx.files.internal(images_path + "background.png"));
        backGround.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		boardRedTexture = new Texture(Gdx.files.internal(images_path + "board_red.png"));
        boardBlueTexture = new Texture(Gdx.files.internal(images_path + "board_blue.png"));
		ballTexture = new Texture(Gdx.files.internal(images_path + "particle.png"));
        ballTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bonusTimeTexture = new Texture(Gdx.files.internal(images_path + "bonus_time.png"));
        bonusTimeTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        bonusSplitterTexture = new Texture(Gdx.files.internal(images_path + "bonus_splitter.png"));
        bonusSplitterTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        bonusControllerTexture = new Texture(Gdx.files.internal(images_path + "bonus_controller.png"));
        bonusControllerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		this.field = field;
		
		score_font = new BitmapFont();
		score_font.setColor(Color.WHITE);
	}
	
	public void render(float time)
	{
        // Clear screen
        Gdx.gl.glClearColor(.15f, .05f, .05f, 1f);
        // Draw back-ground
        backGroundRotation += time * 2;
        batch.begin();
        batch.setColor(MathUtils.sin(backGroundRotation)/2 + 0.5f, 1f, 1f, 0.7f);
        batch.draw(backGround, w/2 - backGround.getWidth()/2, h/2 - backGround.getHeight()/2,
                backGround.getWidth()/2, backGround.getHeight()/2,
                backGround.getWidth(), backGround.getHeight(),
                1, 1, backGroundRotation, 0, 0, backGround.getWidth(), backGround.getHeight(),
                false, false);
        batch.setColor(1f, 1f, 1f, 1f); // reset colors for next graphics
        batch.end();

        // Score bar
        target_scoreShift = -field.player2Board.score + field.player1Board.score;
        scoreShift += (target_scoreShift - scoreShift) * 0.06;
        float diff = target_scoreShift - scoreShift;

        shapeRenderer.begin(ShapeType.Filled);
        // score difference
        shapeRenderer.setColor(1.0f, 1.0f, 0.5f, 0.8f);
        shapeRenderer.rect((-w/2 + scoreShift) + w, h/4, diff, h/2);
        // blue player 1
        shapeRenderer.setColor(0.2f, 0.2f, 1.0f, 0.5f);
        shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // red player 2
        shapeRenderer.setColor(1.0f, 0.2f, 0.2f, 0.5f);
        shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
        // abilities bars
        // time ability
        shapeRenderer.setColor(0.2f, 0.5f, 1f, 0.4f); // blue
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[0].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[0].timer / 10);
        // splitter ability
        shapeRenderer.setColor(0.2f, 1f, 0.5f, 0.4f); // green
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[1].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[1].timer / 10);
        // controller ability
        shapeRenderer.setColor(1f, 0.5f, 0.2f, 0.4f); // orange
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[2].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[2].timer / 10);
        shapeRenderer.setColor(1f, 0.5f, 0.2f, 0.5f); // orange
        for (Ball ball : field.balls){
            if (ball.states[3].isActive && ball.lastTouchedBoard.abilities[2].isActive){
                shapeRenderer.rectLine(ball.bounds.x, ball.bounds.y,
                        ball.lastTouchedBoard.bounds.x + ball.lastTouchedBoard.bounds.getWidth()/2,
                        ball.lastTouchedBoard.bounds.y, ball.bounds.radius);
            }
        }
        // toggle alpha blending. end function uses this to draw transparency
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        batch.begin();


        score_font.draw(batch, Integer.toString(field.player1Board.score), 40, h/2);
        score_font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/2);
        // Boards
        Board p1 = field.player1Board, p2 = field.player2Board;
        batch.draw(boardRedTexture, p1.bounds.x, p1.bounds.y,
                p1.bounds.width, p1.bounds.height);
        batch.draw(boardBlueTexture, p2.bounds.x, p2.bounds.y,
                p2.bounds.width, p2.bounds.height);


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
                Texture tex;
                if (bonus.name.equals("timeSlower"))
                    tex = bonusTimeTexture;
                else if (bonus.name.equals("ballSplitter"))
                    tex = bonusSplitterTexture;
                else if (bonus.name.equals("controller"))
                    tex = bonusControllerTexture;
                else continue; // exception case

				float r = bonus.bounds.radius;
                batch.draw(tex, bonus.bounds.x - r, bonus.bounds.y - r,
                        r, r, r*2, r*2, 1, 1, bonus.rotation, 0, 0,
                        bonusTimeTexture.getWidth(), bonusTimeTexture.getHeight(), false, false);
			}
		}
		batch.end();
	}
}
