package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.phuda.pong.Field;
import com.phuda.pong.AI.AIBoardController;
import com.phuda.pong.Exc.TouchException;

public class Board extends Unit{

	final int SLOWER = 3, TOUCHZONE = 200;
	
	public int x, y, target_x;
	public Rectangle bounds;
	
	private AIBoardController contr;
	int touchNum;
	
	public int score = 1;
	
	Sound sound_reflect;
	
	public Board(int _x, int _y, String name, Field field, boolean isAI)
	{
		super();
		bounds = new Rectangle();
		x = _x;
		y = _y;
		target_x = _x;
		bounds.x = x;
		bounds.y = y;
		bounds.width = 100;
		bounds.height = 30;
		sound_reflect = Gdx.audio.newSound(Gdx.files.internal("sounds/reflect.wav"));
		this.name = name;
		this.field = field;
		if(isAI)
			contr = new AIBoardController(this, field.balls);
		touchNum = 0;
	}
	
	public void updateState(float delta, Ball[] balls)
	{
		checkBalls(balls, delta);
		processAction(delta);
		touchTime += delta;
	}
	
	private void processAction(float delta) {
		
		if (contr == null)
		{
			checkTouch();
			xSpeed = target_x - x;
		}
		else
		{
			if (!contr.catching)
			{
				contr.prepare(delta);
			}
			else
			{
				contr.prepareTime -= delta;
				if (contr.prepareTime < 0)
				{
					xSpeed = 0;
					contr.catching = false;
				}
			}
		}

		// if board goes beyond the left or right bound - no movement
		if ((bounds.x <= 0 && xSpeed <= 0) || (bounds.x >= 
		Gdx.graphics.getWidth() - bounds.width && xSpeed >= 0))
		{
			xSpeed = 0;
			// beyond left
			if (bounds.x < 0)
				bounds.x = 0;
			// beyond right
			else if (bounds.x > Gdx.graphics.getWidth() - bounds.width)
				bounds.x = Gdx.graphics.getWidth() - bounds.width;
		}
		else
		{
			x += xSpeed / SLOWER * 50 * delta;
			bounds.x = x;
		}
	}

	void checkTouch()
	{
		for (int i = 0; i < 2; i++)
		{
			if (!Gdx.input.isTouched(i))
				continue;
			int touchPosY = (Gdx.input.getY(i) - Gdx.graphics.getHeight()) * -1;  // invert )_)
			
			if (touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE)
			{
				target_x = Gdx.input.getX(i) - (int) (bounds.width / 2); // set x into center of board
			}
		}
	}
	
	
	private void checkBalls(Ball[] balls, float time){
		for (int i = 0; i < balls.length; i++){
			if (balls[i] != null){
				if (    (balls[i].bounds.y                      <=   bounds.y + bounds.height) &&
						(balls[i].bounds.y + balls[i].bounds.radius * 2  >=   bounds.y) &&
						(bounds.x                               <=   balls[i].bounds.x + balls[i].bounds.radius * 2) &&
						(bounds.x + bounds.width                >=   balls[i].bounds.x) &&
						balls[i].noStick(this))
				{

					try {
						balls[i].checkBound(this);
					} catch (TouchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					touchTime = 0;
					balls[i].touchTime = 0;
					balls[i].lastTouched = this;
					this.lastTouched = balls[i];
					
					long s = sound_reflect.play(0.6f);
					sound_reflect.setPitch(s, (float) ((balls[i].ySpeed + balls[i].ySpeed) * 0.1f + 0.5f));
				}
			}
		}
	}
	
	// Maybe it will be useful later
	/*private Board otherBoard()
	{
		if (this.name.equals("top"))
			return field.player2Board;
		else 
			return field.player1Board;
	}*/
}
