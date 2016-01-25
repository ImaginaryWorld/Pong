package com.phuda.pong.Units;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Field;

public class Bonus extends Unit {

    public Circle bounds;
    float time;
    float initialRotation = MathUtils.random(120.0f);
    public float rotation;
    float target_radius = (float) (25.0f + Math.random() * 20);


    public Bonus(Field field, int _x, int _y, String _type)
    {
        super();
        this.field = field;

        name = _type;
        bounds = new Circle(_x, _y, 0);
    }

    public void updateState(float delta) {
        time += delta / 5;
        rotation = MathUtils.sin(time) * 180 + initialRotation;
        bounds.radius += (target_radius - bounds.radius) * 5 * delta;
    }
}
