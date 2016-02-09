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
	
	public AIBoardController(Board board, ArrayList<Ball> balls, int difficultyLevel) {
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

	public void update(float delta) {
		// If AI doing nothing
		if (!catching)
			checkBallsPresence(delta);
			// If AI already in motion
		else
			updatePrepareTime(delta);
		// Calculate speed of AI board if it have some time to throw back the ball
		if (prepareTime != 0 && delta != 0 && board.speed.x == 0)
			board.speed.x = (board.target_x - (board.bounds.x + board.bounds.width / 2))
					/ (prepareTime / delta);
	}

	public void updatePrepareTime(float delta) {
		prepareTime -= delta;
		if (prepareTime < 0) {
			// Stop
			board.speed.x = 0;
			prepareTime = 0;
			catching = false;
		}
	}
	
	private void checkBallsPresence(float time) {
		for (int i = 0; i < balls.size(); i++) {
			// Top board's AI
			if (board.name.equals("top")) {
				if (balls.get(i).speed.y > (board.bounds.y - balls.get(i).bounds.y + balls.get(i).bounds.radius) / 50) {
					if (balls.get(i).bounds.y + balls.get(i).bounds.radius < board.bounds.y) {
						startPreparing(balls.get(i), balls.get(i).bounds.y + balls.get(i).bounds.radius,
								board.bounds.y, time);
						break;
					}
				}
			}
			// Bottom board's AI
			else if (balls.get(i).speed.y < (board.bounds.y + board.bounds.height - balls.get(i).bounds.y
					+ balls.get(i).bounds.radius) / 50) {
				if (balls.get(i).bounds.y - balls.get(i).bounds.radius > board.bounds.y
						+ board.bounds.height) {
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
			float multiplier = (float)Math.random() * 10;
			if (multiplier <= difficultyLevel * 3)
				board.target_x = 
					calculateXTouchPoint((int)(yBoardBound - yBallBound), ball);
			/*
			 * Sooo, I'm just thinking why stopping on that?
			 * Let's give him wrong target_x in other cases
			 */
			else
				board.target_x = (int)(calculateXTouchPoint((int)(yBoardBound - yBallBound), ball) *
						difficultyLevel * 3 / multiplier);
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
