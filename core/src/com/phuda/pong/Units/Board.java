package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Board {

	final int SLOWER = 7;
	
	public int x, y, target_x;
	double speed;
	public Rectangle bounds;
	
	public Board()
	{
		bounds = new Rectangle();
		x = 250;
		y = 60;
		target_x = 250;
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
		x -= (x - target_x) / SLOWER;
	 	bounds.x = x;
	 	speed = target_x - x;
	 	
		if (Gdx.input.isTouched())
		{
			target_x = Gdx.input.getX() - (int)(bounds.width/2); // set x into center of board
		}
	 }
}
