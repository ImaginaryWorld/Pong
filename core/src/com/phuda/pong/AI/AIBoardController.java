package com.phuda.pong.AI;

import com.badlogic.gdx.Gdx;
import com.phuda.pong.Exc.AIException;
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
			// Exception handling
			try {
				handleErr(0);
			} catch (AIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		// More than 5 renders before ball will be passed
		if ((yBoardBound - yBallBound) / (float)ball.ySpeed > 5) {
			/* 
			 * AI start moving only if random number is more than 3, 6 or 9
			 * for each difficulty level. Mark that catching must become true anyway,
			 * because we actually need AI to pass this ball.
			 * In easy words AI will be thinking that he'll catch the ball, but won't
			 */
			if (Math.random() * 10 <= difficultyLevel * 3)
				board.target_x = 
					calculateXTouchPoint((int)(yBoardBound - yBallBound), ball);
			/*
			 * Sooo, I just thinking why stops on that?
			 * Let's give him wrong target_x in other cases
			 */
			else
				/*
				 *  Random from board's center in left-most position to center in right
				 *  (not just from 0 to width of the screen)
				 */
				board.target_x =(int)(board.bounds.width / 2 + Math.random() 
					* (Gdx.graphics.getWidth() - board.bounds.width));
			catching = true;
			prepareTime = (board.bounds.y - ball.bounds.y) / (float)ball.ySpeed * time;
		}
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
	
	private void handleErr(int errcode) throws AIException
	{
		String[] err = 
			{
					"Difficulty level's error"
			};
		
		throw new AIException(err[errcode]);
	}
}
