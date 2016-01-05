package com.phuda.pong.Units;

import com.badlogic.gdx.math.Vector2;

public class Unit {
	float touchTime;
	Unit lastTouched;
	Vector2 vector;
	
	Unit()
	{
		vector = new Vector2();
	}
	
	public boolean noStick(Unit touched)
	{
		if (touchTime > 0.5 || lastTouched != touched)
			return true;
		return false;
	}
}
