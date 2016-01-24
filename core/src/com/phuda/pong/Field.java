package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

// Class that controls game field.
public class Field {
	
	public Board player1Board, player2Board;
	public Ball[] balls;
	public Bonus[] bonuses = new Bonus[2];
	
	Field(String mode, int ballsCount, int ai)
	{
        balls = new Ball[ballsCount];

		if (mode.equals("pvc")) {
			// Player 1 aka top player.
			player1Board = new Board(Gdx.graphics.getWidth()/2 - 50,
									 Gdx.graphics.getHeight()/12 * 11 - 30, "top", this, ai);
		}
		else { 
			// Player vs player.
			player1Board = new Board(Gdx.graphics.getWidth()/2 - 50,
					 Gdx.graphics.getHeight()/12 * 11 - 30, "top", this, 0);
		}
		// Player 2 aka bottom player.
		player2Board = new Board(Gdx.graphics.getWidth()/2 - 50,
								 Gdx.graphics.getHeight()/12, "bottom", this, 0);
		// Bonuses generation.
		for (int i = 0; i < bonuses.length; i++) {
			bonuses[i] = new Bonus(this, (int) (Math.random() * Gdx.graphics.getWidth()),
                    (int) (Math.random() * Gdx.graphics.getHeight() / 2 +
                            Gdx.graphics.getHeight() / 4), "timeSlower");
		}
		// Balls generation.
		for (int i = 0; i < balls.length; i++){
			balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
					(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
							Gdx.graphics.getHeight() / 4), 12, i);
		}
	}
	
	public void updateState(float delta) {
		// Toggle slow-motion.
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			delta = delta * 0.2f;
		// Updating state of boards
		player1Board.updateState(delta, balls);
		player2Board.updateState(delta, balls);
		// Updating state of balls
		for (int i = 0; i < balls.length; i++) {
			if (balls[i] != null) {
				if (balls[i].outOfField()) {
					// Who is winner ?
					if (balls[i].bounds.y > Gdx.graphics.getHeight() / 2) {
						player1Board.score += balls[i].bounds.radius;
					}
					else
						player2Board.score += balls[i].bounds.radius;
					// Replacing this ball with a new one
					balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
							(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
									Gdx.graphics.getHeight() / 4), 12, i);
					continue;
				}
                int y = Gdx.graphics.getHeight() / 2;
                if       ( (balls[i].bounds.y > y && player1Board.ability.equals("timeSlower"))
                        || (balls[i].bounds.y < y && player2Board.ability.equals("timeSlower")) )
				    balls[i].updateState(delta * 0.4f);
                else
                    balls[i].updateState(delta);
			}
		}

		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] != null) {
				if (bonuses[i].gotBonus(delta)) {
					bonuses[i] = null;
					bonuses[i] = new Bonus(this, (int) (Math.random() * Gdx.graphics.getWidth()),
							(int) (Math.random() * Gdx.graphics.getHeight() / 2 +
									Gdx.graphics.getHeight() / 4), "timeSlower");
				}

			}
		}
	}
}
