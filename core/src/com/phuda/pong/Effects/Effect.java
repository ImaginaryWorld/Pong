package com.phuda.pong.Effects;

public class Effect {
    public String name;
    public boolean isActive;
    public boolean eternal;
    public float timer;
    public Effect(String name) {
        this.name = name;
        isActive = false;
        eternal = false;
        timer = 0;
    }

    public void engage(float time) {
        isActive = true;
        // Set time to 0 to engage endless state
        if (time == 0)
            eternal = true;
        timer = time;
    }

    public void disengage() {
        if (!eternal) {
            timer = 0;
            isActive = false;
        }
    }
}