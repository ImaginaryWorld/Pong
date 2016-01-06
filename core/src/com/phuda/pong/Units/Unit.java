package com.phuda.pong.Units;

import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Field;

public class Unit {
	float touchTime;
	Unit lastTouched;
	Vector2 vector;
	public String name;
	public double xSpeed, ySpeed;
	public Field field;
	
	Unit()
	{
		vector = new Vector2();
	}
	
	public boolean noStick(Unit touched)
	{
		if (touchTime > 0.4 || lastTouched != touched)
			return true;
		return false;
	}
}
