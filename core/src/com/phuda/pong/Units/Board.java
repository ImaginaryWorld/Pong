package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
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

	public Board(int screenWidth, int screenHeight, String name, Field field, int difficultyLevel) {
		super(field);
		this.name = name;
		// Creating rectangle that describes board as physical item
		setBounds(screenWidth, screenHeight);
		// Setting points of board's bounds
		setBoundsPoints();
		// Set difficulty level to 0 to create human player
		if (difficultyLevel != 0)
			this.contr = new AIBoardController(this, field.balls, difficultyLevel);
	}

	public void updateState(float delta, ArrayList<Ball> balls) {
		// Checking collisions with the balls (first one - after balls "turn")
		checkAllBounds(balls, delta);
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
			if (Math.abs(speed.x) < 1)
				speed.x = 0;
		}
		// AI
		else
			contr.update(delta);


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
		leftBounds = new Vector2[5];
		for (int i = 0; i < leftBounds.length; i++)
			leftBounds[i] = new Vector2(bounds.x, bounds.y + bounds.height / (leftBounds.length + 1) * (i + 1));
		rightBounds = new Vector2[5];
		for (int i = 0; i < rightBounds.length; i++)
			rightBounds[i] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height / (rightBounds.length + 1) * (i + 1));
		angles = new Vector2[4];
		angles[0] = new Vector2(bounds.x, bounds.y);
		angles[1] = new Vector2(bounds.x, bounds.y + bounds.height);
		angles[2] = new Vector2(bounds.x + bounds.width, bounds.y);
		angles[3] = new Vector2(bounds.x + bounds.width, bounds.y + bounds.height);
	}

	// Checking bounds after ball's "turn"
	private void checkAllBounds(ArrayList<Ball> balls, float delta) {
		// Number of vector that refers to the point which ball contains

		for (Ball ball : balls) {
			if (!(ball.states[ball.Ethereal].isActive && ball.lastTouchedUnit == this)) {
				if (checking(ball, delta))
					continue;
				// If ball is inside board's bound - slightly return ball back and check again
				if (bounds.contains(ball.bounds.x, ball.bounds.y)) {
					Gdx.app.log("Debugging", "the case");
					ball.bounds.x -= ball.speed.x / 2;
					ball.bounds.y -= ball.speed.y / 2;
					checking(ball, delta);
				}
			}
		}
	}

	private boolean checking(Ball ball, float delta) {
		if (ball.positionsHistory.size() < 2)
			return false;
		// Setting ball in previous position
		Circle previousPoint = new Circle();
		previousPoint.setRadius(ball.bounds.radius);
		previousPoint.setPosition(ball.positionsHistory.get(ball.positionsHistory.size() - 2));
		float xStep, yStep, divisor;
		divisor = 5;
		xStep = ball.speed.x / divisor * delta * 70;
		yStep = ball.speed.y / divisor * delta * 70;
		for (float i = 0; i < divisor; i++) {
			previousPoint.x += xStep;
			previousPoint.y += yStep;
			// Setting variables so later won't need to call method every time
			int anglePoint = checkBallCollision(angles, previousPoint);
			boolean topCheck = (checkBallCollision(topBounds, previousPoint) != 0),
					bottomCheck = (checkBallCollision(bottomBounds, previousPoint) != 0),
					rightCheck = (checkBallCollision(rightBounds, previousPoint) != 0),
					leftCheck = (checkBallCollision(leftBounds, previousPoint) != 0);
			// Checking conditions
			if (anglePoint != 0 && !((enterSide(previousPoint)) || enterSide(previousPoint))) {
				switch (anglePoint) {
					case 1:
						if (ball.speed.x > 0 && ball.speed.y > 0) {
							ball.angleBoardCollision(this, true);
							ball.bounds.setPosition(previousPoint.x * 2 - ball.bounds.x, previousPoint.y * 2 - ball.bounds.y);
							return true;
						}
						break;
					case 2:
						if (ball.speed.x > 0 && ball.speed.y < 0) {
							ball.angleBoardCollision(this, true);
							ball.bounds.setPosition(previousPoint.x * 2 - ball.bounds.x, previousPoint.y * 2 - ball.bounds.y);
							return true;
						}
						break;
					case 3:
						if (ball.speed.x < 0 && ball.speed.y > 0) {
							ball.angleBoardCollision(this, true);
							ball.bounds.setPosition(previousPoint.x * 2 - ball.bounds.x, previousPoint.y * 2 - ball.bounds.y);
							return true;
						}
						break;
					case 4:
						if (ball.speed.x < 0 && ball.speed.y < 0) {
							ball.angleBoardCollision(this, true);
							ball.bounds.setPosition(previousPoint.x * 2 - ball.bounds.x, previousPoint.y * 2 - ball.bounds.y);
							return true;
						}
						break;
				}
			}
			if (topCheck) {
				if (ball.speed.y < 0 && !(enterSide(previousPoint))) {
					ball.boardCollision(this, previousPoint.y * 2 - ball.bounds.y);
					return true;
				}
			}
			if (bottomCheck) {
				if (ball.speed.y > 0 && !(enterSide(previousPoint))) {
					ball.boardCollision(this, previousPoint.y * 2 - ball.bounds.y);
					return true;
				}
			}
			if (rightCheck) {
				if (ball.speed.x < 0) {
					ball.sideBoardCollision(this, previousPoint.x * 2 - ball.bounds.x);
					return true;
				}
			}
			if (leftCheck) {
				if (ball.speed.x > 0) {
					ball.sideBoardCollision(this, previousPoint.x * 2 - ball.bounds.x);
					return true;
				}
			}
		}
		return false;
	}

	// Checking bounds after board's "turn"
	private void checkAllBounds2(ArrayList<Ball> balls) {
		// Vector that refers to the point that ball contains
		int anglePoint;
		boolean rightCheck, leftCheck;

		for (Ball ball : balls) {
			// Cases when board very close to screen bounds excepted
			if (bounds.x - ball.bounds.radius > 0 && bounds.x + bounds.width + ball.bounds.radius < field.screenWidth) {
				// Setting variables so later won't need to call method every time
				anglePoint = checkBallCollision(angles, ball.bounds);
				rightCheck = (checkBallCollision(rightBounds, ball.bounds) != 0);
				leftCheck = (checkBallCollision(leftBounds, ball.bounds) != 0);
				if (anglePoint != 0 && !enterSide(ball.bounds))
					handleAngleCase(ball, anglePoint % 2 == 0);
				else if (rightCheck)
					ball.sideBoardCollision(this, bounds.x + bounds.width + ball.bounds.radius);
				else if (leftCheck)
					ball.sideBoardCollision(this, bounds.x - ball.bounds.radius);
			}
		}
	}

	boolean enterSide(Circle ball) {
		return bounds.y + bounds.height - ball.y > 0 && ball.y - bounds.y > 0;
	}

	// Angle cases are very special, so there's an extra method for them
	void handleAngleCase(Ball ball, boolean topHit) {
		// If ball goes up
		if (ball.speed.y > 0) {
			// Faces top side
			if (topHit)
				ball.angleBoardCollision(this, false);
			// Faces bottom side
			else {
				ball.angleBoardCollision(this, true);
			}
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
	private int checkBallCollision(final Vector2[] bounds, Circle ball) {
		for (int j = 0; j < bounds.length; j++)
			if (ball.contains(bounds[j])) {
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