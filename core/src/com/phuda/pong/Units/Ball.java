package com.phuda.pong.Units;

import com.phuda.pong.Field;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;

public class Ball {

	public double xSpeed, ySpeed, x, y;
	public Circle bounds;
	Field field;
	
	public Ball(Field field, int _x, int _y)
	{
		this.field = field;
		x = _x; 
		y = _y;
		xSpeed = (Math.random() * 12 - 6);
		ySpeed = (Math.random() * 6 - 3);
		bounds = new Circle();
		bounds.radius = 12;
	}
	
	public void updateState(float time)
	{
		x = x + xSpeed;
		y = y + ySpeed;
		/*System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("bounds x = " + (field.playerBoard.bounds.x));*/
		if ( (x < 0 && xSpeed < 0) || (x > Gdx.graphics.getWidth() - bounds.radius*2 && xSpeed > 0) )
		{
			xSpeed = -xSpeed;
		}
	}

	public boolean outOfField()
	{
		return (y < 0 - bounds.radius) || (y > Gdx.graphics.getHeight());
	}
}
