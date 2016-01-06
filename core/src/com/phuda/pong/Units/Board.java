package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.phuda.pong.Field;
import com.phuda.pong.AI.AIBoardController;

public class Board extends Unit{

	final int SLOWER = 3, TOUCHZONE = 200;
	
	public int x, y, target_x;
	public Rectangle bounds;
	private AIBoardController contr;
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
	}
	
	public void updateState(float time, Ball[] balls)
	{
		checkBalls(balls, time);
		processAction(time);
		touchTime += time;
	}
	
	private void processAction(float time) {
		
		x += xSpeed / SLOWER;
		bounds.x = x;
		
		if (contr == null)
		{
			checkTouch();
			xSpeed = target_x - x;
		}
		else
		{
			if (!contr.catching)
			{
				contr.prepare(time);
			}
			else
			{
				contr.prepareTime -= time;
				if (contr.prepareTime < 0)
				{
					xSpeed = 0;
					contr.catching = false;
				}
			}
		}
	}

	private void checkTouch()
	{
		if (Gdx.input.isTouched()) {
			int touchPosY = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1; // invert )_)
			
			if (touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE){
				target_x = Gdx.input.getX() - (int) (bounds.width / 2); // set x into center of board
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
					balls[i].checkBound(this);
					touchTime = 0;
					balls[i].touchTime = 0;
					balls[i].lastTouched = this;
					this.lastTouched = balls[i];
					
					long s = sound_reflect.play(0.6f);
					sound_reflect.setPitch(s, (float) ((balls[i].ySpeed + balls[i].ySpeed) * 0.1f + 0.5f));
					// System.out.println(balls[i].lastTouched.name);
				}
			}
		}
	}
}
