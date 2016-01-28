package com.phuda.pong.AI;

import com.badlogic.gdx.Gdx;
import com.phuda.pong.Exc.AIException;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;

import java.util.ArrayList;

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
	ArrayList<Ball> balls;
	public boolean catching;
	public float prepareTime;
	
	public AIBoardController(Board board, ArrayList<Ball> balls, int difficultyLevel)
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
		for (int i = 0; i < balls.size(); i++)
		{
			if (board.name.equals("top"))
			{
				if (balls.get(i).speed.y > (board.bounds.y - balls.get(i).bounds.y + balls.get(i).bounds.radius) / 50)
				{
					if (balls.get(i).bounds.y + balls.get(i).bounds.radius < board.bounds.y)
					{
						startPreparing(balls.get(i), balls.get(i).bounds.y + balls.get(i).bounds.radius,
								board.bounds.y, time);
						break;
					}
				}
			}
			else if (balls.get(i).speed.y < (board.bounds.y + board.bounds.height - balls.get(i).bounds.y
					+ balls.get(i).bounds.radius) / 50)
			{
				if (balls.get(i).bounds.y - balls.get(i).bounds.radius > board.bounds.y
						+ board.bounds.height)
				{
					startPreparing(balls.get(i), balls.get(i).bounds.y - balls.get(i).bounds.radius,
							board.bounds.y + board.bounds.height, time);
					break;
				}
			}
		}
	}
	
	void startPreparing(Ball ball, float yBallBound, float yBoardBound, float time)
	{
		// More than 5 renders before ball will be passed
		if ((yBoardBound - yBallBound) / ball.speed.y > 5) {
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
			prepareTime = (board.bounds.y - ball.bounds.y) / ball.speed.y * time;
		}
	}
	
	private int calculateXTouchPoint(int yDist, Ball ball)
	{
		int x = (int)(ball.bounds.x + ball.speed.x * (yDist / ball.speed.y));
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
