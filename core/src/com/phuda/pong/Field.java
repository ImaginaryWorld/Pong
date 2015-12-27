package com.phuda.pong;

import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

// Class that controls game field
public class Field {
	
	public Board playerBoard;
	public Ball[] balls = new Ball[200];
	
	Field()
	{
		playerBoard = new Board();
		for (int i = 0; i < 200; i++){
			balls[i] = new Ball(this, 50 + i*2, 300);
		}
	}
	
	public void updateState(float time)
	{
		playerBoard.updateState(time);
		for (int i = 0; i < 100; i++){
			balls[i].updateState(time);
		}
	}
}
