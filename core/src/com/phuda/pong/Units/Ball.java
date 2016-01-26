package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.Exc.TouchException;
import com.phuda.pong.Field;

import java.util.ArrayList;

public class Ball extends Unit {
	// Ball's disposition variable
	public Circle bounds;
    Board lastTouchedBoard;
    public boolean justTouchedBoard;
	// Effects
	final int Ethereal = 0, Slowed = 1;
	public Effect[] states = {new Effect("Ethereal"), new Effect("Slowed")};
	// Sound
	Sound sound_bump;
	
	public Ball(Field field, int screenWidth, int screenHeight, int num) {
		super();
		// Multipliers that depends on screens width and height
		int wm = 2 + screenWidth / 200;
		int hm = 2 + screenHeight / 300;
		this.name = Integer.toString(++num);
		setBounds(screenWidth, screenHeight);
		this.field = field;
		// Randomizing ball's x and y axle speed with using multipliers
		xSpeed = (int)(Math.random() * 16 - 8);
		while (Math.abs(ySpeed) < 4)
			ySpeed = (int)(Math.random() * 16 - 8);
		sound_bump = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
	}

    public static Ball newBall(Field field, int screenWidth, int screenHeight, float x, float y,
                double xSpeed, double ySpeed, float radius, int num) {
        Ball ball = new Ball(field, screenWidth, screenHeight, num);
        ball.bounds.x = x;
        ball.bounds.y = y;
        ball.bounds.radius = radius;
        ball.xSpeed = xSpeed;
        ball.ySpeed = ySpeed;

        return ball;
    }

	// Updating methods
	public void updateState(float delta) {
		touchTime += delta;
        justTouchedBoard = false;
		// Updating ball's position
		updatePosition(delta);
		// Updating effects
		updateStates(delta);
		// Check collisions with balls
		if (!states[Ethereal].isActive)
			checkCollidesWithBalls(field.balls);
		// Check collisions with bonuses
		checkCollidesWithBonuses(field.bonuses);
		// Checking if ball hit the wall
		checkCollidesWithWalls();
		// Speed decreasing
		releaseSpeed();
	}

	private void updatePosition(float delta) {
		if (states[Slowed].isActive) {
			bounds.x += xSpeed * delta * 70 * 0.4;
			bounds.y += ySpeed * delta * 70 * 0.4;
		}
		else {
			bounds.x += xSpeed * delta * 70;
			bounds.y += ySpeed * delta * 70;
		}
		vector.add(bounds.x, bounds.y);
	}

	private void updateStates(float delta) {
		if (!states[Slowed].isActive)
			checkSlowing();
		for (Effect state: states) {
			if (state.isActive)
				state.timer -= delta;
			if (state.timer < 0)
				disengageState(state.name);
		}
	}

	// Methods checking collisions
	private void checkCollidesWithBalls(ArrayList<Ball> balls) {
		for (Ball ball : balls)
			if (this != ball && !ball.states[Ethereal].isActive)
				if (bounds.overlaps(ball.bounds)) {
					// Balls exchange their speeds and sets last touched unit
					ballsExchange(ball);
					// Try to prevent sticking with that
					engageState(states[Ethereal].name, 0.2f);
					// Sound
					playSound();
				}
	}

	private void checkCollidesWithBonuses(Bonus[] bonuses) {
		if (lastTouchedBoard == null)
			return;
		for (int i = 0; i < bonuses.length; i++)
			if (bonuses[i] != null)
				if (bounds.overlaps(bonuses[i].bounds))
				{
					// Somebody got a bonus!
					lastTouchedBoard.engageAbility(bonuses[i].name);
					// Deleting bonus
					bonuses[i] = null;
				}
	}

	private void checkCollidesWithWalls() {
		if ( (bounds.x - bounds.radius < 0 && xSpeed < 0) ||
				(bounds.x + bounds.radius > Gdx.graphics.getWidth() && xSpeed > 0) ) {
			xSpeed = -xSpeed;
			// A little change in ySpeed for the ones that sticks on crossing field by x axle
			if (Math.abs(ySpeed) < 4)
				ySpeed += ySpeed / Math.abs(ySpeed);
			playSound();
		}
	}

	// Slowing ball if necessary
	private void checkSlowing() {
		Board player1 = field.player1Board, player2 = field.player2Board;
		if ((bounds.y > field.screenHeight - field.screenHeight / 3 && player1.abilities[player1.TimeSlower].isActive && ySpeed > 0)
				|| (bounds.y < field.screenHeight / 3 && field.player2Board.abilities[player2.TimeSlower].isActive && ySpeed < 0))
			engageState(this.states[Slowed].name, 10);
	}

	public void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		ySpeed = - ySpeed;
		// Give some speed by friction
		xSpeed += board.xSpeed / 5;
		// Deactivating slowing
		if (states[Slowed].isActive)
			disengageState(states[Slowed].name);
	}

	public void sideBoardCollision(Board board, float xBound) {
		xSpeed *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
			xSpeed = board.xSpeed;
		// Deactivating slowing
		if (states[Slowed].isActive)
			disengageState(states[Slowed].name);
	}

	public void angleBoardCollision(Board board, boolean ySpeedChange) {
		// Changing xSpeed
		if (board.xSpeed == 0)
			xSpeed = -xSpeed;
		else if ((board.xSpeed > 0 && xSpeed > 0) || (board.xSpeed < 0 && xSpeed < 0))
			xSpeed += board.xSpeed;
		else
			xSpeed = -xSpeed + board.xSpeed;
		// Changing ySpeed
		if (ySpeedChange)
			ySpeed = -ySpeed;
		// Deactivating slowing
		if (states[Slowed].isActive)
			disengageState(states[Slowed].name);
	}

	// Disposition methods
	private void setBounds(int screenWidth, int screenHeight) {
		bounds = new Circle((float)Math.random() * screenWidth,
				(float)Math.random() * screenHeight / 2 + screenHeight / 4,
				screenWidth / 100 + screenHeight / 100);
	}

	public boolean outOfField() {
		return (bounds.y < 0) || (bounds.y > Gdx.graphics.getHeight());
	}

	// Speed handling methods
	private void releaseSpeed() {
		if (xSpeed > field.screenWidth / 40) {
			xSpeed--;
		}
	}

	private void ballsExchange(Ball ball) {
		double xTemp = ball.xSpeed;
		double yTemp = ball.ySpeed;
		// Speed exchanging
		ball.xSpeed = xSpeed;
		ball.ySpeed = ySpeed;
		this.xSpeed = xTemp;
		this.ySpeed = yTemp;
		// Setting fields that refers to last touched unit
		ball.lastTouched = this;
		this.lastTouched = ball;
	}

	// Methods that handles special fields
	public void saveLastBoard(Board board) {
		lastTouchedBoard = board;
        justTouchedBoard = true;
	}

	// Methods that handles cases of using bonuses
	public void split(ArrayList <Ball> balls) {
		if (bounds.radius == field.screenWidth / 100 + field.screenHeight / 100) {
			// Radius
			bounds.radius = bounds.radius / 1.5f;
			// Ethereal for a few seconds, so the ball can fly apart
			this.engageState(this.states[Ethereal].name, 0.5f);
			System.out.println("xSpeed: " + xSpeed);
			System.out.println("ySpeed: " + ySpeed);
			// 2nd ball
			balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
			// Same radius and bounds
			balls.get(balls.size() - 1).bounds.radius = bounds.radius;
			balls.get(balls.size() - 1).bounds.x = bounds.x;
			balls.get(balls.size() - 1).bounds.y = bounds.y;
			// Speed formula
			balls.get(balls.size() - 1).xSpeed = xSpeed * MathUtils.cosDeg(45) - ySpeed * MathUtils.sinDeg(45);
			balls.get(balls.size() - 1).ySpeed = xSpeed * MathUtils.sinDeg(45) + ySpeed * MathUtils.cosDeg(45);
			// Ethereal for a few seconds, so the ball can fly apart
			balls.get(balls.size() - 1).engageState(this.states[Ethereal].name, 0.5f);
			System.out.println("2.xSpeed: " + balls.get(balls.size() - 1).xSpeed);
			System.out.println("2.ySpeed: " + balls.get(balls.size() - 1).ySpeed);
			// 3rd ball
			balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
			// Same radius and bounds
			balls.get(balls.size() - 1).bounds.radius = bounds.radius;
			balls.get(balls.size() - 1).bounds.x = bounds.x;
			balls.get(balls.size() - 1).bounds.y = bounds.y;
			// Speed formula
			balls.get(balls.size() - 1).xSpeed = xSpeed * MathUtils.cosDeg(-45) - ySpeed * MathUtils.sinDeg(-45);
			balls.get(balls.size() - 1).ySpeed = xSpeed * MathUtils.sinDeg(-45) + ySpeed * MathUtils.cosDeg(-45);
			// Etheral for a few seconds, so the ball can fly apart
			balls.get(balls.size() - 1).engageState(this.states[Ethereal].name, 0.5f);
			System.out.println("3.xSpeed: " + balls.get(balls.size() - 1).xSpeed);
			System.out.println("3.ySpeed: " + balls.get(balls.size() - 1).ySpeed);
		}
	}

	// Methods that handles effects
	private void engageState(String effectName, float time) {
		for (Effect state : states) {
			if (state.name.equals(effectName)) {
				state.timer = time;
				state.isActive = true;
				System.out.println("Ball #" + this.name + " is set to " + effectName);
			}
		}
	}

	private void disengageState(String effectName) {
		for (Effect state : states) {
			if (state.name.equals(effectName)) {
				state.timer = 0;
				state.isActive = false;
				System.out.println("Ball #" + this.name + " set off " + effectName);
			}
		}
	}

	// Methods that handles sounds
	private void playSound() {
		long s = sound_bump.play(0.5f);
		sound_bump.setPitch(s, (float) ((ySpeed + xSpeed) * 0.1f + 0.5f));
	}

	// Methods that handles exceptions
	private void handleErr(int errCode) throws TouchException {
		// Later there can be more touching errors in array
		String err[] = { "Touching error" };
		throw new TouchException(err[errCode]);
	}
}
