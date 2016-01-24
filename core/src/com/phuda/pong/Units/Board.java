package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Field;
import com.phuda.pong.AI.AIBoardController;

public class Board extends Unit {
	// Slows the board's movement
	final int SLOWER = 4000 / Gdx.graphics.getWidth(),
	// Zone on y axis in which player can affect board
	TOUCHZONE = Gdx.graphics.getHeight() / 5;
	// Board's disposition variables
	public int target_x;
	public Rectangle bounds;
	public Vector2 topBounds[], bottomBounds[], leftBounds[], rightBounds[], angles[];
	// AI
	private AIBoardController contr;
	// Score of this board
	public int score = 0;
	// Sound
	Sound sound_reflect;

	public Board(int x, int y, String name, Field field, int difficultyLevel) {
		super();
		target_x = x;
		bounds = new Rectangle();
		bounds.x = x;
		bounds.y = y;
		bounds.width = 100;
		bounds.height = 30;
		topBounds = new Vector2[9];
		for (int i = 0; i < topBounds.length; i++)
			topBounds[i] = new Vector2(bounds.x + bounds.width / 10 * (i + 1), bounds.y + bounds.height);
		bottomBounds = new Vector2[9];
		for (int i = 0; i < bottomBounds.length; i++)
			bottomBounds[i] = new Vector2(bounds.x + bounds.width / 10 * (i + 1), bounds. y);
		leftBounds = new Vector2[2];
		for (int i = 0; i < leftBounds.length; i++)
			leftBounds[i] = new Vector2(bounds.x, bounds.y + bounds.height / 3 * (i + 1));
		rightBounds = new Vector2[2];
		for (int i = 0; i < rightBounds.length; i++)
			rightBounds[i] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height / 3 * (i + 1));
		angles = new Vector2[4];
		angles[0] = new Vector2(bounds.x, bounds.y);
		angles[1] = new Vector2(bounds.x, bounds.y + bounds.height);
		angles[2] = new Vector2(bounds.x + bounds.width, bounds.y);
		angles[3] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height);
		this.name = name;
		sound_reflect = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
		this.field = field;
		// Set difficulty level to 0 to create human player
		if (difficultyLevel != 0)
			contr = new AIBoardController(this, field.balls, difficultyLevel);
	}

	public void updateState(float delta, Ball[] balls) {
		// Checking collisions with the balls (first one - after balls "turn")
		checkAllBounds(balls);
		// Changing x coordinate and processing touches
		processAction(delta);
		// Checking collisions with the balls (second - after board "turn")
		checkAllBounds2(balls);
	}

	private void processAction(float delta) {
		touchTime += delta;
		// Human
		if (contr == null) {
			checkTouch();
			xSpeed = (target_x - bounds.x) / SLOWER;
		}
		// AI
		else {
			// If AI doing nothing
			if (!contr.catching)
			{
				contr.prepare(delta);
			}
			// If AI already in motion
			else {
				contr.prepareTime -= delta;
				if (contr.prepareTime < 0)
				{
					xSpeed = 0;
					contr.prepareTime = 0;
					contr.catching = false;
				}
			}
			// Calculate speed of AI board if it have some time to throw back the ball
			if (contr.prepareTime != 0 && delta != 0 && xSpeed == 0)
				xSpeed = (target_x - (bounds.x + bounds.width / 2))
						/ (contr.prepareTime / delta);
		}


		// If board goes beyond the left or right bound - no movement to this bound side
		if (!((bounds.x <= 0 && xSpeed <= 0) || (bounds.x >=
				Gdx.graphics.getWidth() - bounds.width && xSpeed >= 0))) {
			bounds.x += xSpeed;
			updateVectors();
		}
		// Stops the board if it goes out of bound (after x changing but before rendering!)
		outOfBoundStop();
	}

	void updateVectors() {
		for (int i = 0; i < topBounds.length; i++)
			topBounds[i].x = bounds.x + 10 * (i + 1);
		for (int i = 0; i < bottomBounds.length; i++)
			bottomBounds[i].x = bounds.x + 10 * (i + 1);
		for (Vector2 bound : leftBounds)
			bound.x = bounds.x;
		for (Vector2 bound : rightBounds)
			bound.x = bounds.x + bounds.width;
		angles[0].x = bounds.x;
		angles[1].x = bounds.x;
		angles[2].x = bounds.x + bounds.width;
		angles[3].x = bounds.x + bounds.width;
	}

	void checkTouch() {
		for (int i = 0; i < 2; i++) {
			if (!Gdx.input.isTouched(i))
				continue;
			// Invert )_)
			int touchPosY = (Gdx.input.getY(i) - Gdx.graphics.getHeight()) * -1;
			// Checking if touch is on player's side
			if (touchPosY > bounds.y - TOUCHZONE && touchPosY < bounds.y + TOUCHZONE) {
				// Setting the point where board will move to
				target_x = Gdx.input.getX(i) - (int)(bounds.width / 2);
			}
		}
	}

	// Checking bounds after ball's "turn"
	private void checkAllBounds(Ball[] balls) {
		// Vector that refers to the point that ball contains
		int point;

		for (Ball ball : balls) {
			if (ball.lastTouched != this) {
				if (checkBallCollision(topBounds, ball) != 0)
					ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
				else if (checkBallCollision(bottomBounds, ball) != 0)
					ball.boardCollision(this, bounds.y - ball.bounds.radius);
				else if (checkBallCollision(leftBounds, ball) != 0)
					ball.sideBoardCollision(this, bounds.x - ball.bounds.radius);
				else if (checkBallCollision(rightBounds, ball) != 0)
					ball.sideBoardCollision(this, bounds.x + bounds.width + ball.bounds.radius);
				else if ((point = checkBallCollision(angles, ball)) != 0)
					handleAngleCase(point, ball);
			}
		}
	}

	// Checking bounds after board's "turn"
	private void checkAllBounds2(Ball[] balls) {
		// Vector that refers to the point that ball contains
		int point;

		for (Ball ball : balls) {
			if (ball.lastTouched != this) {
				if (checkBallCollision(topBounds, ball) != 0) {
					if (enterSideFromTop(ball))
						spotXBound(ball);
					else
						ball.angleBoardCollision(this, false);
				}
				else if (checkBallCollision(bottomBounds, ball) != 0) {
					if (enterSideFromBottom(ball))
						spotXBound(ball);
					else
						ball.angleBoardCollision(this, false);
				}
				else if (checkBallCollision(leftBounds, ball) != 0) {
					if (enterSideFromTop(ball) || enterSideFromBottom(ball))
						spotXBound(ball);
					else
						ball.angleBoardCollision(this, false);
				}
				else if (checkBallCollision(rightBounds, ball) != 0)
					if (enterSideFromTop(ball) || enterSideFromBottom(ball))
						spotXBound(ball);
					else
						ball.angleBoardCollision(this, false);
				else if ((point = checkBallCollision(angles, ball)) != 0) {
					ball.angleBoardCollision(this, false);
				}
			}
		}
	}

	//
	void spotXBound(Ball ball) {
		if (xSpeed > 0)
			ball.sideBoardCollision(this, bounds.x + bounds.width + ball.bounds.radius);
		else if (xSpeed < 0)
			ball.sideBoardCollision(this, bounds.x - ball.bounds.radius);
	}

	boolean enterSideFromTop(Ball ball) {
		return bounds.y + bounds.height - ball.bounds.y > ball.bounds.radius;
	}

	boolean enterSideFromBottom(Ball ball) {
		return ball.bounds.y - bounds.y > ball.bounds.radius;
	}

	void handleAngleCase(int point, Ball ball) {
		if ((point == 1 || point == 2) && ball.xSpeed < 0) {
			if (point == 1)
				ball.boardCollision(this, bounds.y - ball.bounds.radius);
			else
				ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
		}
		else if ((point == 3 || point == 4) && ball.xSpeed > 0) {
			if (point == 3)
				ball.boardCollision(this, bounds.y - ball.bounds.radius);
			else
				ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
		}
		else
			ball.angleBoardCollision(this, true);
	}

	/*
	 * Checking if ball overlaps with board's bound points. If so - changing meaning of
	 * touchTime (both board's and ball's) and creating collision sound
	 */
	private int checkBallCollision(final Vector2[] bounds, Ball ball) {
		for (int j = 0; j < bounds.length; j++)
			if (ball.bounds.contains(bounds[j])) {
				// Sound of collision
				long s = sound_reflect.play(0.6f);
				sound_reflect.setPitch(s, (float) ((ball.ySpeed + ball.ySpeed) * 0.1f + 0.5f));
				// Changing of touchTime
				touchTime = 0;
				ball.touchTime = 0;
				ball.lastTouched = this;
				this.lastTouched = ball;
				return j + 1;
			}
		return 0;
	}

	void outOfBoundStop() {
		if ((bounds.x <= 0 && xSpeed < 0) || (bounds.x >=
				Gdx.graphics.getWidth() - bounds.width && xSpeed > 0)) {
			xSpeed = 0;
			// beyond left
			if (bounds.x < 0)
				bounds.x = 0;
				// beyond right
			else if (bounds.x > Gdx.graphics.getWidth() - bounds.width)
				bounds.x = Gdx.graphics.getWidth() - bounds.width;
		}
	}
}
