package com.phuda.pong.Units;

import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Field;

public class Unit {
	public Vector2 vector;
	public String name;
	public double xSpeed, ySpeed;
	public Field field;

	Unit() {
		vector = new Vector2();
	}
}
