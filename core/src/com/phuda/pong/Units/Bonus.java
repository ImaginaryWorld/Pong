package com.phuda.pong.Units;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Field;

public class Bonus {

    public Circle bounds;
    public String type;
    Field field;
    float time;
    float initialRotation = MathUtils.random(120.0f);
    public float rotation;
    float target_radius = (float) (25.0f + Math.random() * 20);


    public Bonus(Field field, int _x, int _y, String _type)
    {
        super();
        this.field = field;

        type = _type;
        bounds = new Circle(_x, _y, 0);
    }

    public boolean gotBonus(float delta)
    {
        time += delta / 5;
        rotation = MathUtils.sin(time) * 180 + initialRotation;
        bounds.radius += (target_radius - bounds.radius) * 5 * delta;

        // Check bonus collide
        for (Ball ball : field.balls)
            if (ball.lastTouchedBoard != null && bounds.overlaps(ball.bounds))
            {
                // Somebody got a bonus!
                ball.lastTouchedBoard.ability = type;
                ball.lastTouchedBoard.abilityTimer = 10.0f;
                System.out.printf(ball.lastTouchedBoard.name);
                System.out.printf(" got a bonus: ");
                System.out.println(ball.lastTouchedBoard.ability);
                return true;
            }
        return false;
    }
}
