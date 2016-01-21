package com.phuda.pong.Units;

import com.phuda.pong.Field;
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
		xSpeed = (Math.random() * 6 - 6);
		ySpeed = (Math.random() * 6 - 6);
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
		if ( (x < 0 + this.bounds.radius && xSpeed <= 0) 
				|| (x >= 640 - this.bounds.radius && xSpeed > 0) )
		{
			xSpeed = -xSpeed;
		}
		/*if (y > 450 && ySpeed > 0)
		{
			ySpeed = - ySpeed;
		}*/
	}

	public boolean outOfField()
	{
		return (y < 0);
	}
}
