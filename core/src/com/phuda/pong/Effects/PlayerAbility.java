package com.phuda.pong.Effects;

public class PlayerAbility {
    public String name;
    public boolean isActive;
    public float timer;
    public PlayerAbility(String name) {
        this.name = name;
        isActive = false;
        timer = 0;
    }
}
