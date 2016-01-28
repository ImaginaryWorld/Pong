package com.phuda.pong.Units;

import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Field;

public class Unit {
	public Vector2 vector;
	public Vector2 speed;
	public String name;
	public Field field;

	Unit() {
		vector = new Vector2();
		speed = new Vector2();
	}
}
