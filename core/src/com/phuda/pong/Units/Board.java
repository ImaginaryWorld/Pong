package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.Field;
import com.phuda.pong.AI.AIBoardController;

import java.util.ArrayList;

public class Board extends Unit {
	// Slows the board's movement
	final int SLOWER = 2000 / Gdx.graphics.getWidth(),
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
	// Ability variables
	final int TimeSlower = 0, BallSplitter = 1, Controller = 2;
    public Effect[] abilities = {new Effect("timeSlower"), new Effect("ballSplitter"),
                                 new Effect("controller")};
	// Sound
	Sound sound_reflect;

	public Board(int screenWidth, int screenHeight, String name, Field field, int difficultyLevel) {
		super();
		this.name = name;
		// Creating rectangle that describes board as physical item
		setBounds(screenWidth, screenHeight);
		// Setting points of board's bounds
		setBoundsPoints();
		this.field = field;
		this.sound_reflect = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
		// Set difficulty level to 0 to create human player
		if (difficultyLevel != 0)
			this.contr = new AIBoardController(this, field.balls, difficultyLevel);
	}

	public void updateState(float delta, ArrayList<Ball> balls) {
		// Checking collisions with the balls (first one - after balls "turn")
		checkAllBounds(balls);
		// Changing x coordinate and processing touches
		processAction(delta);
		// Checking collisions with the balls (second - after board "turn")
		checkAllBounds2(balls);
		// Updating ability
		updateAbility(delta);
	}

	private void processAction(float delta) {
		// Human
		if (contr == null) {
			checkTouch();
			speed.x = (target_x - bounds.x) / SLOWER;
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
					speed.x = 0;
					contr.prepareTime = 0;
					contr.catching = false;
				}
			}
			// Calculate speed of AI board if it have some time to throw back the ball
			if (contr.prepareTime != 0 && delta != 0 && speed.x == 0)
				speed.x = (target_x - (bounds.x + bounds.width / 2))
						/ (contr.prepareTime / delta);
		}


		// If board goes beyond the left or right bound - no movement to this bound side
		if (!((bounds.x <= 0 && speed.x <= 0) || (bounds.x >=
				Gdx.graphics.getWidth() - bounds.width && speed.x >= 0))) {
			bounds.x += speed.x;
			updateVectors();
		}
		// Stops the board if it goes out of bound (after x changing but before rendering!)
		outOfBoundStop();
	}

	private void updateVectors() {
		for (int i = 0; i < topBounds.length; i++)
			topBounds[i].x = bounds.x + bounds.width / (topBounds.length + 1) * (i + 1);
		for (int i = 0; i < bottomBounds.length; i++)
			bottomBounds[i].x = bounds.x + bounds.width / (bottomBounds.length + 1) * (i + 1);
		for (Vector2 bound : leftBounds)
			bound.x = bounds.x;
		for (Vector2 bound : rightBounds)
			bound.x = bounds.x + bounds.width;
		angles[0].x = bounds.x;
		angles[1].x = bounds.x;
		angles[2].x = bounds.x + bounds.width;
		angles[3].x = bounds.x + bounds.width;
	}

	private void updateAbility(float delta) {
		for (Effect ability: abilities) {
			if (ability.isActive)
				ability.timer -= delta;
			if (ability.timer < 0)
				ability.isActive = false;
		}
	}

	private void checkTouch() {
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

	private void setBounds(int screenWidth, int screenHeight) {
		bounds = new Rectangle();
		bounds.width = screenWidth / 5;
		bounds.height = screenHeight / 24;
		bounds.x = screenWidth / 2 - bounds.width / 2;
		target_x = (int)bounds.x;
		// Top board
		if (name.equals("top"))
			bounds.y = screenHeight / 12 * 11 - bounds.height;
		// Bottom board
		else
			bounds.y = screenHeight / 12;
	}

	private void setBoundsPoints() {
		topBounds = new Vector2[18];
		for (int i = 0; i < topBounds.length; i++)
			topBounds[i] = new Vector2(bounds.x + bounds.width / 19 * (i + 1), bounds.y + bounds.height);
		bottomBounds = new Vector2[18];
		for (int i = 0; i < bottomBounds.length; i++)
			bottomBounds[i] = new Vector2(bounds.x + bounds.width / 19 * (i + 1), bounds. y);
		leftBounds = new Vector2[4];
		for (int i = 0; i < leftBounds.length; i++)
			leftBounds[i] = new Vector2(bounds.x, bounds.y + bounds.height / 5 * (i + 1));
		rightBounds = new Vector2[4];
		for (int i = 0; i < rightBounds.length; i++)
			rightBounds[i] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height / 5 * (i + 1));
		angles = new Vector2[4];
		angles[0] = new Vector2(bounds.x, bounds.y);
		angles[1] = new Vector2(bounds.x, bounds.y + bounds.height);
		angles[2] = new Vector2(bounds.x + bounds.width, bounds.y);
		angles[3] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height);
	}

	// Checking bounds after ball's "turn"
	private void checkAllBounds(ArrayList<Ball> balls) {
		// Number of vector that refers to the point which ball contains
		int point;

		for (Ball ball : balls) {
			if (!ball.states[ball.Ethereal].isActive) {
				// Collision with board's top bound points
				if (checkBallCollision(topBounds, ball) != 0)
					ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
					// Collision with board's bottom bound points
				else if (checkBallCollision(bottomBounds, ball) != 0)
					ball.boardCollision(this, bounds.y - ball.bounds.radius);
					// Collision with board's left bound points
				else if (checkBallCollision(leftBounds, ball) != 0)
					ball.sideBoardCollision(this, bounds.x - ball.bounds.radius);
					// Collision with board's right bound points
				else if (checkBallCollision(rightBounds, ball) != 0)
					ball.sideBoardCollision(this, bounds.x + bounds.width + ball.bounds.radius);
					// Collision with angle points
				else if ((point = checkBallCollision(angles, ball)) != 0)
					handleAngleCase(point, ball);
			}
		}
	}

	// Checking bounds after board's "turn"
	private void checkAllBounds2(ArrayList<Ball> balls) {
		// Vector that refers to the point that ball contains
		int point;

		for (Ball ball : balls) {
			if (!ball.states[ball.Ethereal].isActive) {
				// Collision with board's top bound points
				if (checkBallCollision(topBounds, ball) != 0) {
					if (enterSideFromTop(ball))
						spotXBound(ball);
					else
						handleAngleCase2(true, ball);
				}
				// Collision with board's bottom bound points
				else if (checkBallCollision(bottomBounds, ball) != 0) {
					if (enterSideFromBottom(ball))
						spotXBound(ball);
					else
						handleAngleCase2(false, ball);
				}
				// Collision with board's left bound points
				else if (checkBallCollision(leftBounds, ball) != 0) {
					if (enterSideFromTop(ball) || enterSideFromBottom(ball))
						spotXBound(ball);
					else {
						if (bounds.y > ball.bounds.y)
							handleAngleCase2(false, ball);
						else
							handleAngleCase2(true, ball);
					}
				}
				// Collision with board's right bound points
				else if (checkBallCollision(rightBounds, ball) != 0)
					if (enterSideFromTop(ball) || enterSideFromBottom(ball))
						spotXBound(ball);
					else {
						if (bounds.y > ball.bounds.y)
							handleAngleCase2(false, ball);
						else
							handleAngleCase2(true, ball);
					}
				// Collision with angle points
				else if ((point = checkBallCollision(angles, ball)) != 0) {
					if (point == 1 || point == 2)
						handleAngleCase2(false, ball);
					else
						handleAngleCase2(true, ball);
				}
			}
		}
	}

	// Defines two cases of side hit - for right and left bound
	void spotXBound(Ball ball) {
		if (speed.x > 0)
			ball.sideBoardCollision(this, bounds.x + bounds.width + ball.bounds.radius);
		else if (speed.x < 0)
			ball.sideBoardCollision(this, bounds.x - ball.bounds.radius);
	}

	// Defines if ball was really hit by board's side not angle (calculations for cases when ball's center are above the board's top)
	boolean enterSideFromTop(Ball ball) {
		return bounds.y + bounds.height - ball.bounds.y > 0;
	}

	// Defines if ball was really hit by board's side not angle (calculations for cases when ball's center are below the board's bottom)
	boolean enterSideFromBottom(Ball ball) {
		return ball.bounds.y - bounds.y > 0;
	}

	// Angle cases are very special, so there's an extra method for them
	void handleAngleCase(int point, Ball ball) {
		// Would-be left bound's angles cases - when ball really came in point through board's top or bottom
		if ((point == 1 || point == 2) && ball.speed.x < 0) {
			if (point == 1)
				ball.boardCollision(this, bounds.y - ball.bounds.radius);
			else
				ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
		}
		// Would-be right bound's angles cases - when ball really came in point through board's top or bottom
		else if ((point == 3 || point == 4) && ball.speed.x > 0) {
			if (point == 3)
				ball.boardCollision(this, bounds.y - ball.bounds.radius);
			else
				ball.boardCollision(this, bounds.y + bounds.height + ball.bounds.radius);
		}
		// True angle cases
		else
			ball.angleBoardCollision(this, true);
	}

	// Angle cases are very special, so there's an extra method for them
	void handleAngleCase2(boolean topHit, Ball ball) {
		// If ball goes up
		if (ball.speed.y > 0) {
			// Faces top side
			if (topHit)
				ball.angleBoardCollision(this, false);
			// Faces bottom side
			else
				ball.angleBoardCollision(this, true);
		}
		// If ball goes down
		else {
			// Faces top side
			if (topHit)
				ball.angleBoardCollision(this, true);
			// Faces bottom side
			else
				ball.angleBoardCollision(this, false);
		}
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
				sound_reflect.setPitch(s, (ball.speed.y + ball.speed.y) * 0.1f + 0.5f);
				// Setting this board to as last one that ball touches
				ball.saveLastBoard(this);
				return j + 1;
			}
		return 0;
	}

	void outOfBoundStop() {
		if ((bounds.x <= 0 && speed.x < 0) || (bounds.x >=
				Gdx.graphics.getWidth() - bounds.width && speed.x > 0)) {
			speed.x = 0;
			// Beyond left
			if (bounds.x < 0)
				bounds.x = 0;
			// Beyond right
			else if (bounds.x > Gdx.graphics.getWidth() - bounds.width)
				bounds.x = Gdx.graphics.getWidth() - bounds.width;
		}
	}
}
