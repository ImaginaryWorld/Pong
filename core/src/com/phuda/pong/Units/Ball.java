package com.phuda.pong.Units;

import com.phuda.pong.Field;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;

public class Ball extends Unit{

	public int xSpeed, ySpeed, num;
	public Circle bounds;
	Field field;
	float lifeTime;
	
	public Ball(Field field, int _x, int _y, int num)
	{
		super();
		this.field = field;
		while (Math.abs(xSpeed) < 2)
			xSpeed = (int)(Math.random() * 10 - 5);
		while (Math.abs(ySpeed) < 2)
			ySpeed = (int)(Math.random() * 10 - 5);
		bounds = new Circle();
		bounds.setPosition(_x, _y);
		bounds.radius = 12;
		this.num = ++num;
	}
	
	public void updateState(float time)
	{
		bounds.x = bounds.x + xSpeed;
		bounds.y = bounds.y + ySpeed;
		vector.add((float)bounds.x, (float)bounds.y);
		lifeTime += time;
		
		for (int i = 0; i < field.balls.length; i++)
			if (this != field.balls[i])
				if (bounds.overlaps(field.balls[i].bounds))
				{
					int xTemp = field.balls[i].xSpeed;
					int yTemp = field.balls[i].ySpeed;
					field.balls[i].xSpeed = xSpeed;
					field.balls[i].ySpeed = ySpeed;
					this.xSpeed = xTemp;
					this.ySpeed = yTemp;
				}
		
		if ( (bounds.x < 0 && xSpeed < 0) || 
				(bounds.x > Gdx.graphics.getWidth() - bounds.radius * 2 && xSpeed > 0) )
		{
			xSpeed = -xSpeed;
		}
		releaseSpeed();
		touchTime += time;
	}

	public boolean outOfField()
	{
		return (bounds.y < 0 - bounds.radius) || (bounds.y > Gdx.graphics.getHeight());
	}
	
	private void releaseSpeed()
	{
		if (xSpeed > 5)
		{
			xSpeed -= xSpeed / 5;
		}
	}

	public void checkBound(Board board)
	{
		int xMeter, yMeter;
		if (xSpeed > 0 && ySpeed > 0)
		{
			xMeter = (int)(bounds.x + bounds.radius);
			yMeter = (int)(bounds.y + bounds.radius);
			while (xMeter > board.bounds.x || yMeter > board.bounds.y)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed((int)board.bounds.x, 
					(int)board.bounds.y, xMeter, yMeter);
			System.out.println("Case 1");
		}
		else if (xSpeed > 0 && ySpeed < 0)
		{
			xMeter = (int)(bounds.x + bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			while (xMeter > board.bounds.x ||
					yMeter < board.bounds.y + board.bounds.height)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed((int)board.bounds.x, 
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
			System.out.println("Case 2");
		}
		else if (xSpeed < 0 && ySpeed > 0)
		{
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y + bounds.radius);
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter > board.bounds.y)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed((int)(board.bounds.x + board.bounds.width), 
					(int)board.bounds.y, xMeter, yMeter);
			System.out.println("Case 3");
		}
		else if (xSpeed < 0 && ySpeed < 0)
		{
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter < board.bounds.y + board.bounds.height)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed((int)(board.bounds.x + board.bounds.width), 
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
			System.out.println("Case 4");
		}
		else
		{
			System.out.println("Error in checkBound method");
		}
	}
	private void changeSpeed(int boundX, int boundY, int xMeter, int yMeter)
	{
		if ((boundX - xMeter) / xSpeed > (boundY - yMeter) / ySpeed)
		{
			xSpeed = - xSpeed;
		}
		else if ((boundX - xMeter) / xSpeed < (boundY - yMeter) / ySpeed)
		{
			ySpeed = - ySpeed;
		}
		else
		{
			strangeCase();
		}
	}
	private void strangeCase()
	{
		System.out.println("That's exactly that strange case");
		xSpeed = - xSpeed;
		ySpeed = - ySpeed;
	}
}
