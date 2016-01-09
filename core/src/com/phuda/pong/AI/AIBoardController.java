package com.phuda.pong.AI;

import com.badlogic.gdx.Gdx;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

public class AIBoardController {
	Board board;
	Ball[] balls;
	public boolean catching;
	public float prepareTime;
	
	public AIBoardController(Board board, Ball[] balls)
	{
		this.board = board;
		this.balls = balls;
		catching = false;
	}
	
	public void prepare(float time)
	{
		for (int i = 0; i < balls.length; i++)
		{
			if (board.name.equals("top"))
			{
				if (balls[i].ySpeed > (board.bounds.y + balls[i].bounds.radius - balls[i].bounds.y) / 50)
				{
					if (balls[i].bounds.y + balls[i].bounds.radius < board.bounds.y)
					{
						startPreparing(balls[i], balls[i].bounds.y + balls[i].bounds.radius,
								board.bounds.y, time);
						break;
					}
				}
			}
			else if (balls[i].ySpeed < (board.bounds.y - balls[i].bounds.y) / 50)
			{
				if (balls[i].bounds.y - balls[i].bounds.radius > board.bounds.y + board.bounds.height)
				{
					startPreparing(balls[i], balls[i].bounds.y - balls[i].bounds.radius,
							board.bounds.y + board.bounds.height, time);
					break;
				}
			}
		}
	}
	
	void startPreparing(Ball ball, float yBallBound, float yBoardBound, float time)
	{
		changeSpeed(ball, yBallBound, yBoardBound);
		catching = true;
		if ((yBoardBound - yBallBound) / (float)ball.ySpeed > 0)
			prepareTime = (board.bounds.y - ball.bounds.y) / (float)ball.ySpeed * time;
	}
	
	public void changeSpeed(Ball ball, float yBallBound, float yBoardBound)
	{
		board.xSpeed = 
				(calculateXTouchPoint((int)(yBoardBound - yBallBound), ball) - 
				(board.bounds.x + board.bounds.width / 2)) / ((yBoardBound -
						yBallBound) / ball.ySpeed) * 3;
	}
	
	private int calculateXTouchPoint(int yDist, Ball ball)
	{
		int x = (int)(ball.bounds.x + ball.xSpeed * (yDist / ball.ySpeed));
		if (x > Gdx.graphics.getWidth())
			x = Gdx.graphics.getWidth() * 2 - x;
		else if (x < 0)
			x = - x;
		return x;
	}
}
