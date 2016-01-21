package com.phuda.pong;

import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

// Class that controls game field
public class Field {
	
	public Board player1Board, player2Board;
	Board boards[];
	// Why so many balls?))
	public Ball[] balls  = new Ball[200];
	
	Field()
	{
		player1Board = new Board(320, 420, true);
		player2Board = new Board(320, 60, false);
		Board temp[] = {player1Board, player2Board};
		boards = temp;
		for (int i = 0; i < 3; i++){
			balls[i] = new Ball(this, 50 + i*5, 300);
		}
	}
	
	public void updateState(float time)
	{
		player1Board.updateState(time, balls);
		player2Board.updateState(time, balls);
		checkBalls(balls);
		
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
	

	private void checkBalls(Ball[] balls){
		for (int b = 0; b < boards.length; b++)
			for (int i = 0; i < balls.length; i++){
				if (balls[i] != null){
					// New checking method
					
					/*if ((balls[i].bounds.radius + 
							Math.sqrt(boards[b].x * boards[b].x + boards[b].y * boards[b].y))
							> balls[i].x - boards[b].x)
					{
						// hit on left/right board's side
						if (balls[i].x + balls[i].bounds.radius > 
						boards[b].x - boards[b].bounds.width / 2 ||
						balls[i].x - balls[i].bounds.radius > 
						boards[b].x + boards[b].bounds.width / 2)
							balls[i].xSpeed += boards[b].speed / 10;
						// hit on board's top/bottom
						else if (balls[i].y - balls[i].bounds.radius > 
						boards[b].y + boards[b].bounds.height / 2 ||
						balls[i].y + balls[i].bounds.radius > 
						boards[b].y - boards[b].bounds.height / 2)
							balls[i].ySpeed = - balls[i].ySpeed;
						// Hit on angle?
						else
						{
							balls[i].xSpeed += boards[b].speed / 10;
							balls[i].ySpeed = - balls[i].ySpeed;
						}
					}*/
						
					// Old checking method
					
					if (((balls[i].y - balls[i].bounds.radius) <= 
							(boards[b].bounds.y + boards[b].bounds.height)) 
							&&
						((balls[i].y + balls[i].bounds.radius) >= boards[b].bounds.y) 
							&&
						(boards[b].bounds.x < balls[i].x + balls[i].bounds.radius*2) 
							&&
						((boards[b].bounds.x + boards[b].bounds.width) > boards[b].x)){
						balls[i].ySpeed = - balls[i].ySpeed;
						// System.out.println(speed);
						// balls[i].xSpeed += boards[b].speed / 10;
				}
			}
		}
	}
}
