package com.phuda.pong.Units;

import com.phuda.pong.Field;
import com.phuda.pong.Exc.TouchException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;

public class Ball extends Unit{

	public Circle bounds;
	float lifeTime;
	Sound sound_bump;
	
	public Ball(Field field, int _x, int _y, int _radius, int num)
	{
		super();
		this.field = field;
		
		while (Math.abs(xSpeed) < 3)
			xSpeed = (int)(Math.random() * 10 - 5);
		while (Math.abs(ySpeed) < 3)
			ySpeed = (int)(Math.random() * 10 - 5);
		
		bounds = new Circle();
		bounds.setPosition(_x, _y);
		bounds.radius = 12;
		
		sound_bump = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
		this.name = Integer.toString(++num);

	}
	
	public void updateState(float delta)
	{
		bounds.x += (float)xSpeed * 50 * delta;
		bounds.y += (float)ySpeed * 50 * delta;
		vector.add((float)bounds.x, (float)bounds.y);
		lifeTime += delta;
		
		// others balls collide
		for (int i = 0; i < field.balls.length; i++)
			if (this != field.balls[i])
				if (bounds.overlaps(field.balls[i].bounds))
				{
					double xTemp = field.balls[i].xSpeed;
					double yTemp = field.balls[i].ySpeed;
					field.balls[i].xSpeed = xSpeed;
					field.balls[i].ySpeed = ySpeed;
					this.xSpeed = xTemp;
					this.ySpeed = yTemp;
					field.balls[i].lastTouched = this;
					this.lastTouched = field.balls[i];
					
					playSound();
				}
		
		// walls collide
		if ( (bounds.x < 0 && xSpeed < 0) || 
				(bounds.x > Gdx.graphics.getWidth() - bounds.radius * 2 && xSpeed > 0) )
		{
			xSpeed = -xSpeed;
			playSound();
		}
		releaseSpeed();
		touchTime += delta;
	}
	
	private void playSound()
	{
		long s = sound_bump.play(0.5f);
		sound_bump.setPitch(s, (float) ((ySpeed + xSpeed) * 0.1f + 0.5f));
	}

	public boolean outOfField()
	{
		return (bounds.y < 0 - bounds.radius) || (bounds.y > Gdx.graphics.getHeight());
	}
	
	private void releaseSpeed()
	{
		if (xSpeed > 10)
		{
			xSpeed -= 1;
		}
	}

	public void checkBound(Board board) throws TouchException
	{
		double xMeter, yMeter;
		if (xSpeed > 0 && ySpeed > 0) // ball goes right and up
		{
			xMeter = (bounds.x + bounds.radius);
			yMeter = (bounds.y + bounds.radius);
			while (xMeter > board.bounds.x || yMeter > board.bounds.y)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x - bounds.radius*2), 
					(int)(board.bounds.y - bounds.radius*2), xMeter, yMeter);
			System.out.println("Case 1");

		}
		else if (xSpeed > 0 && ySpeed < 0) // ball goes right and down
		{
			xMeter = (int)(bounds.x + bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			while (xMeter > board.bounds.x ||
					yMeter < board.bounds.y + board.bounds.height)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x - bounds.radius*2), 
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
		}
		else if (xSpeed < 0 && ySpeed > 0) // ball goes left and up
		{
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y + bounds.radius);
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter > board.bounds.y)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x + board.bounds.width), 
					(int)(board.bounds.y - bounds.radius*2), xMeter, yMeter);
			System.out.println("Case 3");

		}
		else if (xSpeed < 0 && ySpeed < 0) // ball goes left and down
		{
			xMeter = (int)(bounds.x - bounds.radius);
			yMeter = (int)(bounds.y - bounds.radius);
			while (xMeter < board.bounds.x + board.bounds.width ||
					yMeter < board.bounds.y + board.bounds.height)
			{
				xMeter -= xSpeed;
				yMeter -= ySpeed;
			}
			changeSpeed(board, (int)(board.bounds.x + board.bounds.width), 
					(int)(board.bounds.y + board.bounds.height), xMeter, yMeter);
		}
		else /* maybe it's strange, but we can't have x speed on 0. 
				Except the cases when something goes wrong*/
		{
			handleErr(0);
			xSpeed = board.xSpeed;
			ySpeed = - ySpeed; // anyway
		}
	}
	
	private void changeSpeed(Board board, int boundX, int boundY, double xMeter, double yMeter)
	{
		// ball collide with left or right side
		if ((boundX - xMeter) / xSpeed > (boundY - yMeter) / ySpeed)
		{
			xSpeed *= -1;
			// It's not looking very good, because bounds.x is ball's center, not bound
			// Also it's not gonna work now, when I'm changed boundX meaning
			// bounds.x = boundX; // don't overlap board
			if (Math.abs(xSpeed) < Math.abs(board.xSpeed))
				xSpeed = board.xSpeed;
		}
		// ball collide with top or bottom side
		else if ((boundX - xMeter) / xSpeed < (boundY - yMeter) / ySpeed)
		{
			ySpeed = - ySpeed;
			xSpeed += board.xSpeed / 5; // give some speed by friction
		}
		// ball collide with edge
		else
		{
			System.out.println("Edge collide");
			xSpeed = - xSpeed;
			ySpeed = - ySpeed;

		}
	}
	
	private void handleErr(int errCode) throws TouchException
	{
		String err[] =
			{
					"Touching error"
			};
		throw new TouchException(err[errCode]);
	}
}
