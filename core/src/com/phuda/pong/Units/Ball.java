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
	final int Ethereal = 0, Slowed = 1, Split = 2;
	public Effect[] states = {new Effect("Ethereal"), new Effect("Slowed"), new Effect("Split")};
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

	// Updating methods
	public void updateState(float delta) {
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
		// States timing
		for (Effect state: states) {
			if (!state.eternal) {
				if (state.isActive)
					state.timer -= delta;
				if (state.timer < 0)
					state.disengage();
			}
		}
		// Handling slowing
		checkSlowing();
		// Handling splitting
		checkSplitting();
	}

	// Methods checking collisions
	private void checkCollidesWithBalls(ArrayList<Ball> balls) {
		for (Ball ball : balls)
			if (this != ball && !ball.states[Ethereal].isActive)
				if (bounds.overlaps(ball.bounds)) {
					// Balls exchange their speeds and sets last touched unit
					ballsExchange(ball);
					// Try to prevent sticking with that
					states[Ethereal].engage(0.2f);
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
					lastTouchedBoard.abilities[bonuses[i].getIndex()].engage(5);
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
		if (!states[Slowed].isActive) {
			Board player1 = field.player1Board, player2 = field.player2Board;
			if ((bounds.y > field.screenHeight - field.screenHeight / 3 && player1.abilities[player1.TimeSlower].isActive && ySpeed > 0)
					|| (bounds.y < field.screenHeight / 3 && field.player2Board.abilities[player2.TimeSlower].isActive && ySpeed < 0))
				states[Slowed].engage(10);
		}
	}

	private void checkSplitting() {
		if (states[Split].isActive && !states[Split].eternal)
			split(field.balls);
	}

	public void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		ySpeed = - ySpeed;
		// Give some speed by friction
		xSpeed += board.xSpeed / 5;
		changeStatesHitByBoard(board);
	}

	public void sideBoardCollision(Board board, float xBound) {
		xSpeed *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
			xSpeed = board.xSpeed;
		changeStatesHitByBoard(board);
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
		changeStatesHitByBoard(board);
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
	}

	// Methods that handles special fields
	public void saveLastBoard(Board board) {
		lastTouchedBoard = board;
	}

	// Methods that handles cases of using bonuses
	public void split(ArrayList <Ball> balls) {
		if (bounds.radius == field.screenWidth / 100 + field.screenHeight / 100) {
			// Radius
			bounds.radius = bounds.radius / 1.5f;
			// Ethereal for a few seconds, so the balls can fly apart
			states[Ethereal].engage(0.5f);
			// Set split to eternal
			states[Split].engage(0);
			// 2nd ball
			balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
			// Same radius and bounds
			balls.get(balls.size() - 1).bounds.radius = bounds.radius;
			balls.get(balls.size() - 1).bounds.x = bounds.x;
			balls.get(balls.size() - 1).bounds.y = bounds.y;
			// Speed formula
			balls.get(balls.size() - 1).xSpeed = xSpeed * MathUtils.cosDeg(30) - ySpeed * MathUtils.sinDeg(30);
			balls.get(balls.size() - 1).ySpeed = xSpeed * MathUtils.sinDeg(30) + ySpeed * MathUtils.cosDeg(30);
			// Ethereal for a few seconds, so the balls can fly apart
			balls.get(balls.size() - 1).states[Ethereal].engage(0.5f);
			// Set split to eternal
			balls.get(balls.size() - 1).states[Split].engage(0);
			// 3rd ball
			balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
			// Same radius and bounds
			balls.get(balls.size() - 1).bounds.radius = bounds.radius;
			balls.get(balls.size() - 1).bounds.x = bounds.x;
			balls.get(balls.size() - 1).bounds.y = bounds.y;
			// Speed formula
			balls.get(balls.size() - 1).xSpeed = xSpeed * MathUtils.cosDeg(-30) - ySpeed * MathUtils.sinDeg(-30);
			balls.get(balls.size() - 1).ySpeed = xSpeed * MathUtils.sinDeg(-30) + ySpeed * MathUtils.cosDeg(-30);
			// Ethereal for a few seconds, so the balls can fly apart
			balls.get(balls.size() - 1).states[Ethereal].engage(0.5f);
			// Set split to eternal
			balls.get(balls.size() - 1).states[Split].engage(0);
		}
	}

	// Methods that handles states
	private void engageState(String effectName, float time) {
		for (Effect state : states) {
			if (state.name.equals(effectName)) {
				// Set time to 0 to engage endless state
				if (time == 0)
					state.eternal = true;
				state.timer = time;
				state.isActive = true;
				System.out.println("Ball #" + this.name + " is set to " + effectName);
			}
		}
	}

	private void changeStatesHitByBoard(Board board) {
		// Engage ethereal
		states[Ethereal].engage(0.2f);
		// Splitting ball if splitter bonus is active
		if (board.abilities[board.BallSplitter].isActive && !states[Split].isActive)
			states[Split].engage(1);
		// Deactivating slowing
		if (states[Slowed].isActive)
			states[Slowed].disengage();
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
