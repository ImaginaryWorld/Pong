package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

// Class that controls game field
public class Field {
	
	public Board player1Board, player2Board;
	public Ball[] balls = new Ball[5];
	
	Field()
	{
		// new boards, centered
		player1Board = new Board(Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight()/9 * 8 - 20);
		player2Board = new Board(Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight()/9);
		for (int i = 0; i < 5; i++){
			balls[i] = new Ball(this, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		}
	}
	
	public void updateState(float time)
	{
		player1Board.updateState(time, balls);
		player2Board.updateState(time, balls);
		
		for (int i = 0; i < balls.length; i++){
			if (balls[i] != null){
				if (balls[i].outOfField()){ // we lost this ball
					balls[i] = null;
					balls[i] = new Ball(this, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
					continue;
				}
				balls[i].updateState(time);
			}
		}
	}
}
