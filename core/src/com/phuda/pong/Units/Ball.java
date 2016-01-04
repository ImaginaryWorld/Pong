package com.phuda.pong.Units;

import com.phuda.pong.Field;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Ball {

	public int xSpeed, ySpeed, num;
	public Circle bounds;
	Field field;
	Vector2 vector;
	float lifeTime;
	
	public Ball(Field field, int _x, int _y, int num)
	{
		this.field = field;
		while (xSpeed == 0)
			xSpeed = (int)(Math.random() * 3 - 6);
		while (ySpeed == 0)
			ySpeed = (int)(Math.random() * 3 - 6);
		bounds = new Circle();
		bounds.setPosition(_x, _y);
		bounds.radius = 12;
		vector = new Vector2();
		this.num = ++num;
	}
	
	public void updateState(float time)
	{
		bounds.x = bounds.x + xSpeed;
		bounds.y = bounds.y + ySpeed;
		vector.add((float)bounds.x, (float)bounds.y);
		lifeTime += time;
		System.out.println("Ball " + num + " is alive for " + lifeTime);
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
}
