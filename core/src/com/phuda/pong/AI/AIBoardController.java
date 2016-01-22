package com.phuda.pong.AI;

import com.badlogic.gdx.Gdx;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

public class AIBoardController {
	/*
	 *  Constants to exclude mistakes related with wrong difficulty meaning
	 *  (easy, medium, hard)
	 */
	final int elvl = 1;
	final int mlvl = 2;
	final int hlvl = 3;
	
	int difficultyLevel;
	Board board;
	Ball[] balls;
	public boolean catching;
	public float prepareTime;
	
	public AIBoardController(Board board, Ball[] balls, int difficultyLevel)
	{
		if (difficultyLevel == elvl || difficultyLevel == mlvl || difficultyLevel == hlvl)
			this.difficultyLevel = difficultyLevel;
		else
		{
			// There will be exception handling... maybe
			System.out.println("Wrong difficulty level!");
		}
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
				if (balls[i].ySpeed > (board.bounds.y - balls[i].bounds.y + balls[i].bounds.radius) / 50)
				{
					if (balls[i].bounds.y + balls[i].bounds.radius < board.bounds.y)
					{
						startPreparing(balls[i], balls[i].bounds.y + balls[i].bounds.radius,
								board.bounds.y, time);
						break;
					}
				}
			}
			else if (balls[i].ySpeed < (board.bounds.y + board.bounds.height - balls[i].bounds.y 
					+ balls[i].bounds.radius) / 50)
			{
				if (balls[i].bounds.y - balls[i].bounds.radius > board.bounds.y 
						+ board.bounds.height)
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
		/* 
		 * AI start moving only if random number is more than 3, 6 or 9
		 * for each difficulty level. Mark that catching must become true anyway,
		 * because we actually need AI to pass this ball.
		 * In easy words AI will be thinking that he'll catch the ball, but won't
		 */
		if (Math.random() * 10 <= difficultyLevel * 3)
			board.target_x = 
				calculateXTouchPoint((int)(yBoardBound - yBallBound), ball);
		catching = true;
		if ((yBoardBound - yBallBound) / (float)ball.ySpeed > 0)
			prepareTime = (board.bounds.y - ball.bounds.y) / (float)ball.ySpeed * time;
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
