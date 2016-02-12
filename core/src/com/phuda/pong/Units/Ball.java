package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.Exc.TouchException;
import com.phuda.pong.Field;

import java.util.ArrayList;

public class Ball extends Unit {
	// Ball's disposition variable
	public Circle bounds;
	public Board lastTouchedBoard;
	// Ball's trails
	float historyTimer;
	final int HISTORY_LENGTH = 12, speedRegulator = 175;
	public ArrayList<Vector2> positionsHistory;
	// Effects
	final int Ethereal = 0, Slowed = 1, Split = 2, Controlled = 3;
	public int sound_bump, sound_reflect, sound_wallHit;
	public Effect[] states = {new Effect("Ethereal"), new Effect("Slowed"), new Effect("Split"),
			new Effect("Controlled")};

	public Ball(Field field, int screenWidth, int screenHeight, int num) {
		super();
		// Multipliers that depends on screens width and height
		int wm = (int)(screenWidth * 1.6f / speedRegulator);
		int hm = screenHeight / (int)(speedRegulator * 0.8f);
		this.name = Integer.toString(++num);
		setBounds(screenWidth, screenHeight);
		this.field = field;
		this.positionsHistory = new ArrayList<Vector2>();
		// Randomizing ball's x and y axle speed with using multipliers
		speed.x = MathUtils.random(-wm * 2, wm * 2);
		speed.y = MathUtils.random(hm * 1.5f, hm * 2);
		if (bounds.y > screenHeight / 2)
			speed.y *= -1;
		// Sounds numbers
		sound_bump = field.screen.soundHandler.bump;
		sound_reflect = field.screen.soundHandler.reflect;
		sound_wallHit = field.screen.soundHandler.wallHit;
	}

	// Updating methods
	public void updateState(float delta) {
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
		handleSpeed(delta);
		// Save position
		historyTimer += delta;
		if (historyTimer >= 0.008f) {
			historyTimer = 0.0f;
			Vector2 vec = new Vector2(bounds.x, bounds.y);
			positionsHistory.add(vec);
			while (positionsHistory.size() > HISTORY_LENGTH) {
				positionsHistory.remove(0);
			}
		}
	}

	private void updatePosition(float delta) {
		delta *= 70;
		if (states[Slowed].isActive)
			delta *= field.screenHeight / Math.abs(field.screenHeight / 2 - bounds.y) / 9;
		// If ball is controlled by someone
		if (states[Controlled].isActive && lastTouchedBoard.abilities[lastTouchedBoard.Controller].isActive) {
			float boardSpeed = lastTouchedBoard.speed.x;
			speed.x += boardSpeed * 0.1f;
		}
		bounds.x += speed.x * delta;
		bounds.y += speed.y * delta;
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
					// Saving as last touched unit
					// Try to prevent sticking with that
					states[Ethereal].engage(0.2f);
					// Sound
					playSound(sound_bump);
				}
	}

	private void checkCollidesWithBonuses(Bonus[] bonuses) {
		if (lastTouchedBoard == null)
			return;
		for (int i = 0; i < bonuses.length; i++)
			if (bonuses[i] != null)
				if (bounds.overlaps(bonuses[i].bounds)) {
					// Somebody got a bonus!
					lastTouchedBoard.abilities[bonuses[i].getIndex()].engage(5);
					// Deleting bonus
					bonuses[i] = null;
				}
	}

	private void checkCollidesWithWalls() {
		if ( (bounds.x - bounds.radius < 0 && speed.x < 0) ||
				(bounds.x + bounds.radius > Gdx.graphics.getWidth() && speed.x > 0) ) {
			speed.x = -speed.x;
			playSound(sound_wallHit);
		}
	}

	// Slowing ball if necessary
	private void checkSlowing() {
		Board player1 = field.player1Board, player2 = field.player2Board;
		if (states[Slowed].isActive) {
			if (!inSlowingArea(player1) && !inSlowingArea(player2))
				states[Slowed].disengage();
		}
		else if ((inSlowingArea(player1) && player1.abilities[player1.TimeSlower].isActive && speed.y > 0)
					|| (inSlowingArea(player2) && player2.abilities[player2.TimeSlower].isActive && speed.y < 0))
				states[Slowed].engage(10);
	}

	private boolean inSlowingArea(Board player) {
		if (player.name.equals("top"))
			return bounds.y - field.screenHeight / 2 > field.screenHeight / 6 &&
				bounds.y - field.screenHeight / 2 <
						player.bounds.y - field.screenHeight / 2;
		else
			return field.screenHeight / 2 - bounds.y  > field.screenHeight / 6 &&
					field.screenHeight / 2 - bounds.y <
						field.screenHeight / 2 - player.bounds.y - player.bounds.height;
	}

	private void checkSplitting() {
		if (states[Split].isActive && !states[Split].eternal)
			split(field.balls);
	}

	public void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		speed.y = -speed.y;
		// Give some speed by friction
		speed.x += board.speed.x / 5;
		hitConsequenses(board);
		changeStatesHitByBoard(board);
	}

	public void sideBoardCollision(Board board, float xBound) {
		if (board.speed.x / speed.x <= 0)
			speed.x *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(speed.x) < Math.abs(board.speed.x))
			speed.x = board.speed.x;
		hitConsequenses(board);
		changeStatesHitByBoard(board);
	}

	public void angleBoardCollision(Board board, boolean ySpeedChange) {
		// Changing speed.x
		if (board.speed.x == 0)
			speed.x = -speed.x;
		else if ((board.speed.x > 0 && speed.x > 0) || (board.speed.x < 0 && speed.x < 0))
			speed.x += board.speed.x;
		else
			speed.x = -speed.x + board.speed.x;
		// Changing speed.y
		if (ySpeedChange)
			speed.y = -speed.y;
		hitConsequenses(board);
		changeStatesHitByBoard(board);
	}

	// Disposition methods
	private void setBounds(int screenWidth, int screenHeight) {
		float radius = screenWidth / 100 + screenHeight / 100;
		bounds = new Circle(MathUtils.random(radius, screenWidth - radius),
				screenHeight / 2 - MathUtils.random(screenHeight / 7, screenHeight / 3.5f)
				* MathUtils.randomSign(), radius);
	}

	public boolean outOfField() {
		return (bounds.y < 0) || (bounds.y > Gdx.graphics.getHeight());
	}

	// Speed handling methods
	private void handleSpeed(float delta) {
		if (Math.abs(speed.x) > field.screenWidth * 5 / speedRegulator)
			speed.x -= delta * speed.x * 6;
		if (Math.abs(speed.y) > field.screenHeight * 3 / speedRegulator)
			speed.y -= delta * speed.y * 6;
		else if (Math.abs(speed.y) < field.screenHeight / speedRegulator)
			speed.y += delta * speed.y * 6;
	}

	private void ballsExchange(Ball ball) {
		float xTemp = ball.speed.x;
		float yTemp = ball.speed.y;
		// If balls weights are same
		if (this.states[Split].isActive == ball.states[Split].isActive) {
			// Speed exchanging
			ball.speed.x = speed.x;
			ball.speed.y = speed.y;
			this.speed.x = xTemp;
			this.speed.y = yTemp;
		}
		// If balls weights are different
		else {
			if (this.states[Split].isActive) {
				// Speed exchanging
				ball.speed.x = speed.x / 3;
				ball.speed.y = speed.y / 3;
				this.speed.x = xTemp * 3;
				this.speed.y = yTemp * 3;
			}
			else {
				// Speed exchanging
				ball.speed.x = speed.x * 3;
				ball.speed.y = speed.y * 3;
				this.speed.x = xTemp / 3;
				this.speed.y = yTemp / 3;
			}
		}
		lastTouchedUnit = ball;
		ball.lastTouchedUnit = this;
	}

	// Methods that handles special fields
	public void saveLastBoard(Board board) {
		lastTouchedBoard = board;
		lastTouchedUnit = board;
	}

	// Methods that handles cases of using bonuses
	public void split(ArrayList <Ball> balls) {
		handleAfterSplit();
		// 2nd ball
		balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
		// Same bounds
		balls.get(balls.size() - 1).bounds.x = bounds.x;
		balls.get(balls.size() - 1).bounds.y = bounds.y;
		// Speed changing
		balls.get(balls.size() - 1).changeSpeedAfterSplit(speed.x, speed.y, 20);
		// Turn on some split consequences
		balls.get(balls.size() - 1).handleAfterSplit();
		if (this.states[Controlled].isActive)
			balls.get(balls.size() - 1).states[Controlled].engage(this.states[Controlled].timer);
		balls.get(balls.size() - 1).lastTouchedBoard = this.lastTouchedBoard;
		// 3rd ball
		balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
		// Same bounds
		balls.get(balls.size() - 1).bounds.x = bounds.x;
		balls.get(balls.size() - 1).bounds.y = bounds.y;
		// Speed changing
		balls.get(balls.size() - 1).changeSpeedAfterSplit(speed.x, speed.y, -20);
		// Turn on some split consequences
		balls.get(balls.size() - 1).handleAfterSplit();
		if (this.states[Controlled].isActive)
			balls.get(balls.size() - 1).states[Controlled].engage(this.states[Controlled].timer);
		balls.get(balls.size() - 1).lastTouchedBoard = this.lastTouchedBoard;
	}

	private void hitConsequenses(Board board) {
		// Sound of collision
		if (!states[Ethereal].isActive)
			playSound(sound_reflect);
		// Setting this board to as last one that ball touches
		saveLastBoard(board);
	}

	private void changeStatesHitByBoard(Board board) {
		// Engaging ethereal
		states[Ethereal].engage(0.2f);
		// Splitting ball if splitter bonus is active
		if (board.abilities[board.BallSplitter].isActive && !states[Split].isActive)
			states[Split].engage(1);
		// Engaging controller
		if (board.abilities[board.Controller].isActive && !states[Controlled].isActive){
			states[Controlled].engage(board.abilities[board.Controller].timer);
			System.out.println("controller on");
		}
		// Deactivating slowing
		if (states[Slowed].isActive)
			states[Slowed].disengage();
	}

	// Split methods
	private void changeSpeedAfterSplit(float orientingXSpeed, float orientingYSpeed, int degrees) {
		// Speed formula
		speed.x = orientingXSpeed * MathUtils.cosDeg(degrees) - orientingYSpeed * MathUtils.sinDeg(degrees);
		speed.y = orientingXSpeed * MathUtils.sinDeg(degrees) + orientingYSpeed * MathUtils.cosDeg(degrees);
		// If new ball's and orienting ball's speeds are with different signs
		if (speed.y * orientingYSpeed < 0) {
			float tempXSpeed = speed.x;
			// New direction of x speed tuning depends on old one direction
			if (speed.x * speed.y < 0)
				speed.x = -speed.y;
			else
				speed.x = speed.y;
			// New direction of y speed tuning depends on x speed and orienting y speed
			if (tempXSpeed * orientingYSpeed < 0)
				speed.y = -tempXSpeed;
			else
				speed.y = tempXSpeed;
		}
	}

	private void handleAfterSplit() {
		// Radius
		bounds.radius /= 1.5f;
		// Ethereal for a few seconds, so the balls can fly apart
		states[Ethereal].engage(0.5f);
		// Set split to eternal
		states[Split].engage(0);
	}

	// Methods that handles sounds
	public void playSound(int soundNum) {
		if (bounds.radius < field.screenWidth / 5 + field.screenHeight / 7)
			field.screen.soundHandler.playSound(soundNum, 1.2f);
		else
			field.screen.soundHandler.playSound(soundNum, 1);
	}

	// Methods that handles exceptions
	private void handleErr(int errCode) throws TouchException {
		// Later there can be more touching errors in array
		String err[] = { "Touching error" };
		throw new TouchException(err[errCode]);
	}
}