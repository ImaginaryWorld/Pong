package com.phuda.pong;

import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

// Class that controls game field
public class Field {
	
	public Board player1Board, player2Board;
	public Ball[] balls = new Ball[200];
	
	Field()
	{
		player1Board = new Board(250, 400);
		player2Board = new Board(250, 50);
		for (int i = 0; i < 3; i++){
			balls[i] = new Ball(this, 50 + i*5, 300);
		}
	}
	
	public void updateState(float time)
	{
		player1Board.updateState(time, balls);
		player2Board.updateState(time, balls);
		for (int i = 0; i < 100; i++){
			if (balls[i] != null){
				if (balls[i].outOfField()){ // we lost this ball
					balls[i] = null;
					continue;
				}
				balls[i].updateState(time);
			}
		}
	}
}
