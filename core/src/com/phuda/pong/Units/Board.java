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
	int touchNum;
	
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
		int touchPosY = (Gdx.input.getY(0) - Gdx.graphics.getHeight()) * -1;  // invert )_)
		
		if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1))
		{
			
			// multitouch and first touch is on this board's side
			if (touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE)
			{
				processTouchTable();
			}
			
			// multitouch and second touch is on this board's side
			else
			{
				touchPosY = (Gdx.input.getY(1) - Gdx.graphics.getHeight()) * -1;
				if (touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE)
				{
					processTouchTable();
				}
			}
		}
	
		// only one touch - other board's player
		else if (Gdx.input.isTouched(0) && field.touchTable[0] == otherBoard())
		{
			return;
		}
		// only one touch - this board's player
		else if ((Gdx.input.isTouched(0) && field.touchTable[0] == this)
				|| (Gdx.input.isTouched(0) && 
						(touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE)))
		{
			field.touchTable[0] = this;
			field.touchTable[1] = null;
		}
		// no touches
		else if (!Gdx.input.isTouched())
		{
			for (int i = 0; i < field.touchTable.length; i++)
				field.touchTable[i] = null;
			return;
		}
		// multitouch with touching out of touchzone and other errors not processing
		
		if (Gdx.input.isTouched(touchNum)) 
		{
			
			if (touchPosY > y - TOUCHZONE && touchPosY < y + TOUCHZONE){
				target_x = Gdx.input.getX(touchNum) - (int) (bounds.width / 2); // set x into center of board
			}
		}
	}
	
	private void processTouchTable()
	{
		for (int i = 0; i < field.touchTable.length; i++)
		{
			// element is our board
			if (field.touchTable[i] == this)
			{
				touchNum = i;
				return;
			}
		}
		// if first place are not used
		if (field.touchTable[touchNum] == null)
			{
				field.touchTable[0] = this;
				return;
			}
		// otherwise - using second element to write our board in it
		++touchNum;
		field.touchTable[touchNum] = this;
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
	
	private Board otherBoard()
	{
		if (this.name.equals("top"))
			return field.player2Board;
		else 
			return field.player1Board;
	}
}
