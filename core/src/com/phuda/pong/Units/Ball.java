package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.phuda.pong.Exc.TouchException;
import com.phuda.pong.Field;

public class Ball extends Unit {
	// Ball's disposition variable
	public Circle bounds;
	// Sound
	Sound sound_bump;
	
	public Ball(Field field, int x, int y, int radius, int num) {
		super();
		// Multipliers that depends on screens width and height
		int wm = 2 + Gdx.graphics.getWidth() / 200;
		int hm = 2 + Gdx.graphics.getHeight() / 300;
		this.field = field;
		bounds = new Circle(x, y, radius);
		// Randomizing ball's x and y axle speed with using multipliers
		while (Math.abs(xSpeed) < wm)
			xSpeed = (int)(Math.random() * wm * 4 - wm * 2);
		while (Math.abs(ySpeed) < hm)
			ySpeed = (int)(Math.random() * hm * 4 - hm * 2);
		this.name = Integer.toString(++num);
		sound_bump = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
	}
	
	public void updateState(float delta) {
		// Updating ball's position
		bounds.x += xSpeed;
		bounds.y += ySpeed;
		vector.add(bounds.x, bounds.y);
		// Checking if this ball collides with others
		for (int i = 0; i < field.balls.length; i++)
			if (this != field.balls[i])
				if (bounds.overlaps(field.balls[i].bounds)) {
					// Balls exchange their speeds
					double xTemp = field.balls[i].xSpeed;
					double yTemp = field.balls[i].ySpeed;
					field.balls[i].xSpeed = xSpeed;
					field.balls[i].ySpeed = ySpeed;
					this.xSpeed = xTemp;
					this.ySpeed = yTemp;
					// Setting fields that refers to last touched unit
					field.balls[i].lastTouched = this;
					this.lastTouched = field.balls[i];
					// Sound
					playSound();
				}
		// Walls collide
		if ( (bounds.x - bounds.radius < 0 && xSpeed < 0) ||
				(bounds.x + bounds.radius > Gdx.graphics.getWidth() && xSpeed > 0) ) {
			xSpeed = -xSpeed;
			playSound();
		}
		releaseSpeed();
		touchTime += delta;
	}

	public boolean outOfField() {
		return (bounds.y < 0) || (bounds.y > Gdx.graphics.getHeight());
	}

	private void releaseSpeed() {
		if (xSpeed > Gdx.graphics.getWidth() / 50) {
			xSpeed -= 1;
		}
	}

	void boardCollision(Board board, float yBound) {
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.y = yBound;
		ySpeed = - ySpeed;
		// Give some speed by friction
		xSpeed += board.xSpeed / 5;
	}

	void sideBoardCollision(Board board, float xBound) {
		xSpeed *= -1;
		// Don't overlap board - new center of ball in radius distance from board's side
		bounds.x = xBound;
		// If ball's speed are too low it receives a board's speed
		if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
			xSpeed = board.xSpeed;
	}

	void angleBoardCollision(Board board, boolean ballTurn) {
		if (ballTurn)
			xSpeed = -xSpeed;
		else
			xSpeed = board.xSpeed;
		ySpeed = -ySpeed;
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
