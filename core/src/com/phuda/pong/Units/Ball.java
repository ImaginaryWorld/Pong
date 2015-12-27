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
		xSpeed = (Math.random() * 6 - 3);
		ySpeed = (Math.random() * 6 - 3);
		bounds = new Circle();
		bounds.radius = 19;
	}
	
	public void updateState(float time)
	{
		x = x + xSpeed;
		y = y + ySpeed;
		/*System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("bounds x = " + (field.playerBoard.bounds.x));*/
		if (hitBall())
		{
			ySpeed = - ySpeed;
			System.out.println(field.playerBoard.speed);
			xSpeed += field.playerBoard.speed / 10;
		}
		if ( (x < 0 && xSpeed < 0) || (x > 610 && xSpeed > 0) )
		{
			xSpeed = -xSpeed;
		}
		if (y > 450 && ySpeed > 0)
		{
			ySpeed = - ySpeed;
		}
	}
	
	boolean hitBall()
	{
		if ( (y - bounds.radius) <= (field.playerBoard.bounds.y + 10)
				&& ( (y - bounds.radius) >= (field.playerBoard.bounds.y - 10) )
				&& ( ( field.playerBoard.bounds.x < (x + 19) )
				&& (field.playerBoard.bounds.x + 100) > (x - 19) )
				&& (ySpeed < 0) )
		{
			return true;
		}
		return false;
	}
}
