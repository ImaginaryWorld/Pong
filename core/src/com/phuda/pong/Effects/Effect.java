package com.phuda.pong.Effects;

public class Effect {
    public String name;
    public boolean isActive;
    public float timer;
    public Effect(String name) {
        this.name = name;
        isActive = false;
        timer = 0;
    }
}