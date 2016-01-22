package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.phuda.pong.Field;
import com.phuda.pong.AI.AIBoardController;
import com.phuda.pong.Exc.TouchException;

public class Board extends Unit {
	// Slows the board's movement
	final int SLOWER = Gdx.graphics.getWidth() / 400,
	// Zone on y axis in which player can affect board
			TOUCHZONE = Gdx.graphics.getHeight() / 5;
	// Board's disposition variables
    public int target_x;
	public Rectangle bounds;
	// AI
	private AIBoardController contr;
	// Score of this board
	public int score = 0;
	// Sound
	Sound sound_reflect;
	
	public Board(int _x, int _y, String name, Field field, int difficultyLevel) {
		super();
		target_x = _x;
		bounds = new Rectangle();
		bounds.x = _x;
		bounds.y = _y;
		bounds.width = 100;
		bounds.height = 30;
		this.name = name;
		sound_reflect = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
		this.field = field;
		// Set difficulty level to 0 to create human player
		if (difficultyLevel != 0)
			contr = new AIBoardController(this, field.balls, difficultyLevel);
	}
	
	public void updateState(float delta, Ball[] balls) {
		/*
		 * Checking if any ball overlaps with board. If so - changing meaning of
		 * touchTime (both board's and ball's) and create collision sound
		 */
		checkBalls(balls, delta);
		// Changing x coordinate and processing touches
		processAction(delta);
	}
	
	private void processAction(float delta) {
		touchTime += delta;
		// Human
		if (contr == null) {
			checkTouch();
			xSpeed = target_x - bounds.x;
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
				/ (contr.prepareTime / delta) * SLOWER;
		}
		

		// If board goes beyond the left or right bound - no movement
		if (!((bounds.x <= 0 && xSpeed <= 0) || (bounds.x >= 
				Gdx.graphics.getWidth() - bounds.width && xSpeed >= 0))) {
			bounds.x += xSpeed / SLOWER * 50 * delta;
		}
		// Stops the board if it goes out of bound (after x changing but before rendering!)
		outOfBoundStop();
	}

	void checkTouch() {
		for (int i = 0; i < 2; i++) {
			if (!Gdx.input.isTouched(i))
				continue;
			// Invert )_)
			int touchPosY = (Gdx.input.getY(i) - Gdx.graphics.getHeight()) * -1;
			
			if (touchPosY > bounds.y - TOUCHZONE && touchPosY < bounds.y + TOUCHZONE) {
				// set x into center of board
				target_x = Gdx.input.getX(i) - (int) (bounds.width / 2);
			}
		}
	}
	
	private void checkBalls(Ball[] balls, float time) {
		for (int i = 0; i < balls.length; i++) {
			if (balls[i] != null){
                float r = balls[i].bounds.radius;
				if (    (balls[i].bounds.y - r    <=   bounds.y + bounds.height) &&
						(balls[i].bounds.y + r    >=   bounds.y) &&
						(bounds.x                 <=   balls[i].bounds.x + r) &&
						(bounds.x + bounds.width  >=   balls[i].bounds.x - r) &&
						balls[i].noStick(this))
				{
					try {
						balls[i].checkBound(this);
					} catch (TouchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Changing of touchTime
					touchTime = 0;
					balls[i].touchTime = 0;
					balls[i].lastTouched = this;
					this.lastTouched = balls[i];
					// Sounds
					long s = sound_reflect.play(0.6f);
					sound_reflect.setPitch(s, (float) ((balls[i].ySpeed + balls[i].ySpeed) * 0.1f + 0.5f));
				}
			}
		}
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
