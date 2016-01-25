package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Exc.TouchException;
import com.phuda.pong.Field;

public class Ball extends Unit {
	// Ball's disposition variable
	public Circle bounds;
    Board lastTouchedBoard;
    public boolean justTouchedBoard;
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
		xSpeed = (int)(Math.random() * wm * 4 - wm * 2);
		while (Math.abs(ySpeed) < hm)
			ySpeed = (int)(Math.random() * hm * 4 - hm * 2);
		sound_bump = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
	}
	
	public void updateState(float delta) {
		touchTime += delta;
        justTouchedBoard = false;
		// Updating ball's position
		updatePosition(delta);
		// Check collisions with balls
		checkCollidesWithBalls(field.balls);
		// Check collisions with bonuses
		checkCollidesWithBonuses(field.bonuses);
		// Checking if ball hit the wall
		checkCollidesWithWalls();
		// Speed decreasing
		releaseSpeed();
	}

	private void updatePosition(float delta) {
		bounds.x += xSpeed * delta * 70;
		bounds.y += ySpeed * delta * 70;
		vector.add(bounds.x, bounds.y);
	}

	private void checkCollidesWithBalls(Ball[] balls) {
		for (Ball ball : balls)
			if (this != ball)
				if (bounds.overlaps(ball.bounds)) {
					// Balls exchange their speeds and sets last touched unit
					ballsExchange(ball);
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
			playSound();
		}
	}

	private void setBounds(int screenWidth, int screenHeight) {
		bounds = new Circle((float)Math.random() * screenWidth,
				(float)Math.random() * screenHeight / 2 + screenHeight / 4,
				screenWidth / 100 + screenHeight / 100);
		System.out.println("radius: " + bounds.radius);
	}

	private void releaseSpeed() {
		if (xSpeed > Gdx.graphics.getWidth() / 40) {
			xSpeed -= 1;
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

	public boolean outOfField() {
		return (bounds.y < 0) || (bounds.y > Gdx.graphics.getHeight());
	}

	public void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		ySpeed = - ySpeed;
		// Give some speed by friction
		xSpeed += board.xSpeed / 5;
	}

	public void sideBoardCollision(Board board, float xBound) {
		xSpeed *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
			xSpeed = board.xSpeed;
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
	}

	public void saveLastBoard(Board board) {
		lastTouchedBoard = board;
        justTouchedBoard = true;
	}

	private void playSound() {
		long s = sound_bump.play(0.5f);
		sound_bump.setPitch(s, (float) ((ySpeed + xSpeed) * 0.1f + 0.5f));
	}

	private void handleErr(int errCode) throws TouchException {
		// Later there can be more touching errors in array
		String err[] = { "Touching error" };
		throw new TouchException(err[errCode]);
	}
}
