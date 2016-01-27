package com.phuda.pong.Units;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Field;

public class Bonus extends Unit {
    final int timeSlower = 0, ballSplitter = 1;
    public final int fullRadius = 24;
    public String types[] = {"timeSlower", "ballSplitter"};
    public Circle bounds;
    public float time;
    float initialRotation = MathUtils.random(120.0f);
    public float rotation;
    float target_radius = (float) (25.0f + Math.random() * 20);


    public Bonus(Field field, int screenWidth, int screenHeight, String _type)
    {
        super();
        setBounds(screenWidth, screenHeight);
        this.field = field;
        name = _type;
    }

    public Bonus(Field field, int screenWidth, int screenHeight, float time)
    {
        super();
        setBounds(screenWidth, screenHeight);
        this.field = field;
        this.time = time;
        name = types[(int)(MathUtils.random(types.length - 1))];
    }

    public void updateState(float delta) {
        time -= delta;
        rotation = MathUtils.sin(time) * 180 + initialRotation;
        handleRadius(delta);
    }

    private void setBounds(int screenWidth, int screenHeight) {
        bounds = new Circle((float) (MathUtils.random(fullRadius, screenWidth - fullRadius)),
                (float) (MathUtils.random(screenHeight / 4, screenHeight - screenHeight / 4)),
                0);
        vector.add(bounds.x, bounds.y);
    }

    // Method that allows to get int detector of bonus
    public int getIndex() {
        if (name.equals(types[timeSlower]))
            return 0;
        else if (name.equals(types[ballSplitter]))
            return 1;
        // For exception cases
        return -1;
    }

    private void handleRadius(float delta) {
        if (time > 2 && bounds.radius < fullRadius)
            bounds.radius += 0.5;
        else if (time < 2) {
            bounds.radius -= bounds.radius * delta;
            System.out.println("time: " + time);
            System.out.println("radius: " + bounds.radius);
        }
    }
}
