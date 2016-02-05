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
import com.phuda.pong.UI.Button;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;
import com.sun.javafx.geom.Vec2f;

public class FieldRenderer {
	final int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
    Texture pauseButtonTexture, playButtonTexture, menuButtonTexture, boardRedTexture, boardBlueTexture, ballTexture,
    bonusTimeTexture, bonusSplitterTexture, bonusControllerTexture, backGround;
    float backGroundRotation;
    float previousScreenDark = 1f;
    float startTimer = 0f;
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

        pauseButtonTexture = new Texture(Gdx.files.internal(images_path + "pause.png"));
        pauseButtonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        playButtonTexture = new Texture(Gdx.files.internal(images_path + "play.png"));
        playButtonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        menuButtonTexture = new Texture(Gdx.files.internal(images_path + "menu.png"));
        menuButtonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
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
	
	public void render(float delta)
	{
        // Draw back-ground
        backGroundRotation += delta * 2;
        batch.begin();
        batch.setColor(MathUtils.sin(backGroundRotation)/2 + 0.5f, 1f, 1f, 0.7f);
        batch.draw(backGround, w/2 - backGround.getWidth(), h/2 - backGround.getHeight(),
                backGround.getWidth(), backGround.getHeight(),
                backGround.getWidth()*2, backGround.getHeight()*2,
                1, 1, -backGroundRotation, 0, 0, backGround.getWidth(), backGround.getHeight(),
                false, true);
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
        // Clear screen color
        Gdx.gl.glClearColor(0.2f - diff,
                (MathUtils.sin(backGroundRotation/4)) * 0.25f,
                0.2f + diff, 1f);

        if (previousScreenDark > 0)
            previousScreenDark -= delta;

        shapeRenderer.begin(ShapeType.Filled);
        // score difference
        shapeRenderer.setColor(1.0f, 1.0f, 0.5f, 1f);
        shapeRenderer.rect((-w/2 + scoreShift) + w, h/4, diff, h/2);
        // blue player 1
        shapeRenderer.setColor(0.1f, 0.1f, 0.5f, 1f);
        shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // red player 2
        shapeRenderer.setColor(0.5f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
        // abilities bars
        // time ability
        shapeRenderer.setColor(0.1f, 0.2f, 0.7f, 1f); // blue
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[0].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[0].timer / 10);
        // splitter ability
        shapeRenderer.setColor(0.1f, 0.7f, 0.2f, 1f); // green
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[1].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[1].timer / 10);
        // controller ability
        shapeRenderer.setColor(0.7f, 0.5f, 0.1f, 1f); // orange
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[2].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[2].timer / 10);
        shapeRenderer.setColor(0.7f, 0.5f, 0.1f, 1f); // orange
        for (Ball ball : field.balls){
            if (ball.states[3].isActive && ball.lastTouchedBoard.abilities[2].isActive){
                shapeRenderer.rectLine(ball.bounds.x, ball.bounds.y,
                        ball.lastTouchedBoard.bounds.x + ball.lastTouchedBoard.bounds.getWidth()/2,
                        ball.lastTouchedBoard.bounds.y, ball.bounds.radius);
            }
        }
        // toggle alpha blending. end function uses this to draw transparency
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_ONE, Gdx.gl.GL_DST_COLOR);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


        batch.begin();
        // Pause/resume button
        if (!field.paused)
            batch.draw(pauseButtonTexture, field.pauseButton.bounds.x, field.pauseButton.bounds.y,
                    field.pauseButton.bounds.width, field.pauseButton.bounds.height);
        // A little lazy - I'll use some menu button's sizes in this one
        else
            batch.draw(playButtonTexture, field.menuButton.bounds.x,
                    field.pauseButton.bounds.y - field.pauseButton.bounds.height / 2,
                    field.menuButton.bounds.width, field.menuButton.bounds.height);
        // Menu button
        batch.draw(menuButtonTexture, field.menuButton.bounds.x, field.menuButton.bounds.y,
                field.menuButton.bounds.width, field.menuButton.bounds.height);
        //Scores
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
                float color[] = {1f, 1f, 1f};
                // if ball is controlled
                if (ball.states[3].isActive){
                    color[1] -= 0.3f;
                    color[2] -= 0.7f;
                }
                // if ball is slowed
                if (ball.states[1].isActive){
                    color[0] -= 0.7f;
                    color[1] -= 0.7f;
                }
                // Trail rendering
                batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE);
                float r = ball.bounds.radius;
                float alpha = 0.2f;
                for (Vector2 vec : ball.positionsHistory) {
                    batch.setColor(color[0], color[1], color[2], alpha);
                    float size = alpha * 2;
                    batch.draw(ballTexture, vec.x - r * size, vec.y - r * size,
                            r * 2 * size, r * 2 * size);
                    alpha += 0.02f;
                }
                // Reset alpha
                batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
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

        if (previousScreenDark != 0) {
            if (previousScreenDark < 0){
                previousScreenDark = 0;
            }
            previousScreenDark -= delta / 2;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, previousScreenDark);
            shapeRenderer.rect(0, 0, w, h);
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
	}
}
