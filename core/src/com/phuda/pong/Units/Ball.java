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
	
	public Ball(Field field, int _x, int _y, int _radius, int num) {
		super();
		// Multipliers that depends on screens width and height
		int wm = Gdx.graphics.getWidth() / 125;
		int hm = Gdx.graphics.getHeight() / 150;
		this.field = field;
		bounds = new Circle(_x, _y, _radius);
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
		bounds.x += xSpeed * 50 * delta;
		bounds.y += ySpeed * 50 * delta;
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
		if (xSpeed > 10) {
			xSpeed -= 1;
		}
	}

	public void checkBound(Board board) throws TouchException {
		double xMeter, yMeter;
		// Ball goes right and up
		if (xSpeed > 0 && ySpeed > 0) {
			// Right and top ball's sides coordinates
			xMeter = (bounds.x + bounds.radius);
			yMeter = (bounds.y + bounds.radius);
			 // Calculating last position in which ball not overlaps board
			while (xMeter > board.bounds.x || yMeter > board.bounds.y) {
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x - bounds.radius),
					(int)(board.bounds.y - bounds.radius), xMeter, yMeter);
		}
		// Ball goes right and down
		else if (xSpeed > 0 && ySpeed < 0) {
			// Right and bottom ball's sides coordinates
			xMeter = (int)(bounds.x + bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			// Calculating last position in which ball not overlaps board
			while (xMeter > board.bounds.x ||
					yMeter < board.bounds.y + board.bounds.height) {
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x - bounds.radius),
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
		}
		// Ball goes left and up
		else if (xSpeed < 0 && ySpeed > 0) {
			// Left and top ball's sides coordinates
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y + bounds.radius);
			// Calculating last position in which ball not overlaps board
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter > board.bounds.y) {
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x + board.bounds.width),
					(int)(board.bounds.y - bounds.radius), xMeter, yMeter);

		}
		// Ball goes left and down
		else if (xSpeed < 0 && ySpeed < 0) {
			// Left and bottom ball's sides coordinates
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			// Calculating last position in which ball not overlaps board
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter < board.bounds.y + board.bounds.height) {
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x + board.bounds.width), 
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
		}
		/*
		 * Maybe it's strange, but we can't have x speed on 0.
		 * Except the cases when something goes wrong
		 */
		else {
			handleErr(0);
			xSpeed = board.xSpeed;
			// Anyway
			ySpeed = - ySpeed;
		}
	}
	
	private void changeSpeed(Board board, int boundX, int boundY, double xMeter, double yMeter) {
		// Ball collide with left or right side
		if (Math.abs(boundX - xMeter) / (Math.abs(xSpeed) + Math.abs(board.xSpeed)) > (boundY - yMeter) / ySpeed) {
			xSpeed *= -1;
			// Don't overlap board - new center of ball in radius distance from board's side
			bounds.x = boundX + bounds.radius;
			// If ball speed are too slow it receives a board's speed
			if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
				xSpeed = board.xSpeed;
		}
		// Ball collide with top or bottom side
		else if (Math.abs(boundX - xMeter) / (Math.abs(xSpeed) + Math.abs(board.xSpeed)) < (boundY - yMeter) / ySpeed) {
			// Don't overlap board - new center of ball in radius distance from board's side
			bounds.y = boundY + bounds.radius;
			ySpeed = - ySpeed;
			// Give some speed by friction
			xSpeed += board.xSpeed / 5;
		}
		// Ball collide with edge
		else
		{
			System.out.println("Edge collide");
			xSpeed = - xSpeed;
			ySpeed = - ySpeed;

		}
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
