package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Board {

	public int x, y;
	double speed;
	public Rectangle bounds;
	
	public Board()
	{
		bounds = new Rectangle();
		x = 250;
		y = 60;
		bounds.x = x;
		bounds.y = y;
		bounds.width = 100;
		bounds.height = 20;
	}
	
	public void updateState(float time)
	{
		processAction();
	}
	
	 private void processAction()
	 {
		if (Gdx.input.isTouched())
		{
			setSpeed();
			setX();
		}
	 }
	 
	 void setSpeed()
	 {
		 speed = Gdx.input.getX() - x;
	 }
	 
	 void setX()
	 {
		 x = Gdx.input.getX();
	 	 bounds.x = x;
	 } 
}
