package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

// Class that controls game field
public class Field {
	
	public Board player1Board, player2Board;
	public Ball[] balls = new Ball[2];
	
	Field(String mode)
	{
		if (mode.equals("pvc"))
		{
			// player 1 aka top player
			player1Board = new Board(Gdx.graphics.getWidth()/2 - 50,
									 Gdx.graphics.getHeight()/12 * 11 - 20, "top", this, true);
		}
		else { // player vs player
			player1Board = new Board(Gdx.graphics.getWidth()/2 - 50,
					 Gdx.graphics.getHeight()/12 * 11 - 20, "top", this, false);
		}
		
		// player 2 aka bottom player
		player2Board = new Board(Gdx.graphics.getWidth()/2 - 50,
								 Gdx.graphics.getHeight()/12, "bottom", this, false);
		
		for (int i = 0; i < balls.length; i++){
			balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
					(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
							Gdx.graphics.getHeight() / 4), 12, i);
		}
	}
	
	public void updateState(float delta)
	{
		if (Gdx.input.isKeyPressed(Input.Keys.S)) // toggle slow-motion
			delta = delta * 0.2f;
		
		player1Board.updateState(delta, balls);
		player2Board.updateState(delta, balls);
		
		for (int i = 0; i < balls.length; i++){
			if (balls[i] != null){
				if (balls[i].outOfField()){ // we lost this ball
					
					if (balls[i].bounds.y > Gdx.graphics.getHeight() / 2){ // who is winner ?
					     	player1Board.score += balls[i].bounds.radius;
					} else  player2Board.score += balls[i].bounds.radius;
					
					balls[i] = null;
					balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
							(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
									Gdx.graphics.getHeight() / 4), 12, i);
					continue;
				}
				balls[i].updateState(delta);
			}
		}
	}
}
