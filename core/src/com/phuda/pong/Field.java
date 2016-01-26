package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

import java.util.ArrayList;

// Class that controls game field.
public class Field {
	public int screenWidth, screenHeight, ballsCount;
	public Board player1Board, player2Board;
	public ArrayList<Ball> balls;
	public Bonus[] bonuses;
	// Map for names of effects on the field
	public final String effectsMap[] = {"timeSlower", "ballSplitter"};
	
	Field(String mode, int ballsCount, int ai, int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.ballsCount = ballsCount;
		// Balls generation
		balls = new ArrayList<Ball>();
		for (int i = 0; i < ballsCount; i++)
			balls.add(new Ball(this, screenWidth, screenHeight, i));
		// Boards generation
		if (mode.equals("pvc")) {
			// Player 1 aka top player
			player1Board = new Board(screenWidth, screenHeight, "top", this, ai);
		}
		else { 
			// Player vs player
			player1Board = new Board(screenWidth, screenHeight, "top", this, 0);
		}
		// Player 2 aka bottom player
		player2Board = new Board(screenWidth, screenHeight, "bottom", this, 0);
		// Bonuses generation
		bonuses = new Bonus[2];
		for (int i = 0; i < bonuses.length; i++)
			bonuses[i] = new Bonus(this, screenWidth, screenHeight, "timeSlower");
	}
	
	public void updateState(float delta) {
		// Toggle slow-motion
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			delta = delta * 0.2f;
		// Updating state of boards
		updateBoards(delta);
		// Updating state of balls
		updateBalls(delta);
		// Updating bonuses
		updateFeatures(delta);
	}

	private void updateBoards(float delta) {
		player1Board.updateState(delta, balls);
		player2Board.updateState(delta, balls);
	}

	private void updateBalls(float delta) {
		// Creating new balls
		if (balls.size() < ballsCount)
			for (int i = 0; i < ballsCount - balls.size(); i++)
				balls.add(new Ball(this, screenWidth, screenHeight, balls.size() - 1));
		for (int i = 0; i < balls.size(); i++) {
            int y = Gdx.graphics.getHeight() / 2;
			if (balls.get(i).outOfField()) {
				// Who is winner ?
				if (balls.get(i).bounds.y > Gdx.graphics.getHeight() / 2) {
					player1Board.score += balls.get(i).bounds.radius;
				}
				else {
					player2Board.score += balls.get(i).bounds.radius;
				}
				// Deleting this ball
				balls.remove(i);
				continue;
			}
			// Slowing ball if necessary
			if ((balls.get(i).bounds.y > y && player1Board.abilities[0].isActive)
					|| (balls.get(i).bounds.y < y && player2Board.abilities[0].isActive))
				delta *= 0.4f;
			// Split ball on three small if necessary
			if (player1Board.abilities[1].isActive && balls.get(i).justTouchedBoard
					&& balls.get(i).bounds.y > y
					|| player2Board.abilities[1].isActive && balls.get(i).justTouchedBoard
					&& balls.get(i).bounds.y < y) {
				balls.get(i).split(balls);
				System.out.println("There are i really want a three small balls");
				}
			balls.get(i).updateState(delta);
		}
	}

	private void updateFeatures(float delta) {
		// Bonuses update
		updateBonuses(delta);
	}

	private void updateBonuses(float delta) {
		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] == null || bonuses[i].bounds.radius < 1)
				bonuses[i] = newBonus(this.screenWidth, this.screenHeight);
			bonuses[i].updateState(delta);
		}
	}

    private Bonus newBonus(int sw, int sh) {
        String bonusType;
        if (MathUtils.random() >= 0.5)
            bonusType = effectsMap[0];
        else
            bonusType = effectsMap[1];
        Bonus bonus = new Bonus(this, sw, sh, bonusType);
        return bonus;
    }
}
