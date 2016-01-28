package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.Exc.TouchException;
import com.phuda.pong.Field;
import com.sun.javafx.geom.Vec2f;

import java.util.ArrayList;

public class Ball extends Unit {
	// Ball's disposition variable
	public Circle bounds;
    public Board lastTouchedBoard;
    public boolean justTouchedBoard;
	// Ball's trails
    float historyTimer;
	final int HISTORY_LENGTH = 12;
    public ArrayList<Vector2> positionsHistory;
	// Effects
	final int Ethereal = 0, Slowed = 1, Split = 2, Controlled = 3;
	public Effect[] states = {new Effect("Ethereal"), new Effect("Slowed"), new Effect("Split"),
                              new Effect("Controlled")};
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
        this.positionsHistory = new ArrayList<Vector2>();
		// Randomizing ball's x and y axle speed with using multipliers
		speed.x = MathUtils.random(-8, 8);
		speed.y = MathUtils.random(5, 8) * MathUtils.randomSign();
		sound_bump = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
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
		releaseSpeed(delta);
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
        justTouchedBoard = false;
    }

	private void updatePosition(float delta) {
		if (states[Slowed].isActive) {
			bounds.x += speed.x * delta * 70 * 0.4;
			bounds.y += speed.y * delta * 70 * 0.4;
		}
		else {
			bounds.x += speed.x * delta * 70;
			bounds.y += speed.y * delta * 70;
		}
        if (states[Controlled].isActive && lastTouchedBoard.abilities[lastTouchedBoard.Controller].isActive) {
            bounds.x += lastTouchedBoard.speed.x;
            System.out.println(lastTouchedBoard.speed.x);
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
        checkController();
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
		if ( (bounds.x - bounds.radius < 0 && speed.x < 0) ||
				(bounds.x + bounds.radius > Gdx.graphics.getWidth() && speed.x > 0) ) {
			speed.x = -speed.x;
			// A little change in speed.y for the ones that sticks on crossing field by x axle
			if (Math.abs(speed.y) < 4)
				speed.y += speed.y / Math.abs(speed.y);
			playSound();
		}
	}

	// Slowing ball if necessary
	private void checkSlowing() {
		if (!states[Slowed].isActive) {
			Board player1 = field.player1Board, player2 = field.player2Board;
			if ((bounds.y > field.screenHeight - field.screenHeight / 3 && player1.abilities[player1.TimeSlower].isActive && speed.y > 0)
					|| (bounds.y < field.screenHeight / 3 && field.player2Board.abilities[player2.TimeSlower].isActive && speed.y < 0))
				states[Slowed].engage(10);
		}
	}

    private void checkController() {
        if (!states[Controlled].isActive && lastTouchedBoard != null){
            if (lastTouchedBoard.abilities[lastTouchedBoard.Controller].isActive){
                states[Controlled].engage(10);
                System.out.println("controller on");
            }
        }
    }

	private void checkSplitting() {
		if (states[Split].isActive && !states[Split].eternal)
			split(field.balls);
	}

	public void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		speed.y = - speed.y;
		// Give some speed by friction
		speed.x += board.speed.x / 5;
		changeStatesHitByBoard(board);
	}

	public void sideBoardCollision(Board board, float xBound) {
		speed.x *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(speed.x) < Math.abs(board.speed.x))
			speed.x = board.speed.x;
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
		changeStatesHitByBoard(board);
	}

	// Disposition methods
	private void setBounds(int screenWidth, int screenHeight) {
		bounds = new Circle((float)MathUtils.random(screenWidth),
				(float)MathUtils.random(screenHeight * 2 / 5, screenHeight - screenHeight * 2 / 5),
				screenWidth / 100 + screenHeight / 100);
	}

	public boolean outOfField() {
		return (bounds.y < 0) || (bounds.y > Gdx.graphics.getHeight());
	}

	// Speed handling methods
	private void releaseSpeed(float delta) {
		if (Math.abs(speed.x) > field.screenWidth / 60) {
			speed.x -= delta * speed.x * 6;
			System.out.println("speed.x: " + speed.x);
		}
		if (Math.abs(speed.y) > field.screenHeight / 90) {
			speed.y -= delta * speed.y * 6;
			System.out.println("speed.y: " + speed.y);
		}
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
			System.out.println("Balls weights are different");
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
	}

	// Methods that handles special fields
	public void saveLastBoard(Board board) {
		lastTouchedBoard = board;
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
		balls.get(balls.size() - 1).changeSpeedAfterSplit(speed.x, speed.y, 30);
		// Turn on some split consequences
		balls.get(balls.size() - 1).handleAfterSplit();
		balls.get(balls.size() - 1).lastTouchedBoard = this.lastTouchedBoard;
		// 3rd ball
		balls.add(new Ball(field, field.screenWidth, field.screenHeight, balls.size()));
		// Same bounds
		balls.get(balls.size() - 1).bounds.x = bounds.x;
		balls.get(balls.size() - 1).bounds.y = bounds.y;
		// Speed changing
		balls.get(balls.size() - 1).changeSpeedAfterSplit(speed.x, speed.y, -30);
		// Turn on some split consequences
		balls.get(balls.size() - 1).handleAfterSplit();
		balls.get(balls.size() - 1).lastTouchedBoard = this.lastTouchedBoard;
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
		bounds.radius = bounds.radius / 1.5f;
		// Ethereal for a few seconds, so the balls can fly apart
		states[Ethereal].engage(0.5f);
		// Set split to eternal
		states[Split].engage(0);
	}

	// Methods that handles sounds
	private void playSound() {
		long s = sound_bump.play(0.5f);
		sound_bump.setPitch(s,  (speed.y + speed.x) * 0.1f + 0.5f);
	}

	// Methods that handles exceptions
	private void handleErr(int errCode) throws TouchException {
		// Later there can be more touching errors in array
		String err[] = { "Touching error" };
		throw new TouchException(err[errCode]);
	}
}
