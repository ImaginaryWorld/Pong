package com.phuda.pong.Units;

import com.badlogic.gdx.math.Circle;
import com.phuda.pong.Field;

public class Bonus {

    public Circle bounds;
    Field field;
    float time;
    float target_radius = 32.0f, radius = 0.0f;


    public Bonus(Field field, int _x, int _y)
    {
        super();
        this.field = field;

        bounds = new Circle(_x, _y, (int)radius);
    }

    public boolean gotBonus(float delta)
    {
        bounds.radius += (target_radius - bounds.radius) * 0.2;
        // balls collide
        for (int i = 0; i < field.balls.length; i++)
            if (bounds.overlaps(field.balls[i].bounds))
            {
                // there are bonus get
                System.out.println("Player got a bonus!");
                System.out.println(field.balls[i].lastTouched);
                return true;
            }
        return false;
    }
}
