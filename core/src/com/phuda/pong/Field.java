package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

// Class that controls game field.
public class Field {
	int screenWidth, screenHeight;
	public Board player1Board, player2Board;
	public Ball[] balls;
	public Bonus[] bonuses = new Bonus[2];
	// Map for names of effects on the field
	public final String effectsMap[] = {"timeSlower", "ballSplitter"};
	
	Field(String mode, int ballsCount, int ai, int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		// Balls generation
		balls = new Ball[ballsCount];
		for (int i = 0; i < balls.length; i++)
			balls[i] = new Ball(this, screenWidth, screenHeight, i);
		if (mode.equals("pvc")) {
			// Player 1 aka top player
			player1Board = new Board(screenWidth, screenHeight, "top", this, ai);
		}
		else { 
			// Player vs player
			player1Board = new Board(screenWidth, screenHeight, "top", this, 0);
		}
		// Player 2 aka bottom player.
		player2Board = new Board(Gdx.graphics.getWidth()/2 - 50,
								 Gdx.graphics.getHeight()/12, "bottom", this, 0);
		// Balls generation.
		for (int i = 0; i < balls.length; i++){
			balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
					(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
							Gdx.graphics.getHeight() / 4), 12, i);
		}
		// Bonuses generation.
		for (int i = 0; i < bonuses.length; i++) {
			bonuses[i] = newBonus();
		}
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
        int y = Gdx.graphics.getHeight() / 2;
        for (int i = 0; i < balls.length; i++) {
            if (balls[i] != null) {
                if (balls[i].outOfField()) {
                    // Who is winner ?
                    if (balls[i].bounds.y > Gdx.graphics.getHeight() / 2) {
                        player1Board.score += balls[i].bounds.radius;
                    }
					else {
                        player2Board.score += balls[i].bounds.radius;
                    }
                    // Replacing this ball with a new one
                    balls[i] = new Ball(this, (int)(Math.random() * Gdx.graphics.getWidth()),
							(int)(Math.random() * Gdx.graphics.getHeight() / 2 +
									Gdx.graphics.getHeight() / 4), 12, i);
                    continue;
                }
                else if (  player1Board.abilities[1].isActive && balls[i].justTouchedBoard
                        && balls[i].bounds.y > y
                        || player2Board.abilities[1].isActive && balls[i].justTouchedBoard
                        && balls[i].bounds.y < y) {
                    balls[i] = new Ball(this, (int)balls[i].bounds.x, (int)balls[i].bounds.y,
                            (int)(balls[i].bounds.radius * 0.8f), i);
                    System.out.println("There are i really want a three small balls");
                }
				// Slowing ball if necessary
                if       ( (balls[i].bounds.y > y && player1Board.abilities[0].isActive)
                        || (balls[i].bounds.y < y && player2Board.abilities[0].isActive) )
						delta *= 0.4f;
                balls[i].updateState(delta);
			}
		}
	}

	private void updateFeatures(float delta) {
		// Bonuses update
		updateBonuses(delta);
	}

	private void updateBonuses(float delta) {
		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] == null || bonuses[i].bounds.radius < 1)
				bonuses[i] = newBonus();
			bonuses[i].updateState(delta);
		}
	}

    private Bonus newBonus() {
        String bonusType;
        if (MathUtils.random() >= 0.5)
            bonusType = "timeSlower";
        else
            bonusType = "ballSplitter";
        Bonus bonus = new Bonus(this, (int) (Math.random() * Gdx.graphics.getWidth()),
                    (int) (Math.random() * Gdx.graphics.getHeight() / 2 +
                            Gdx.graphics.getHeight() / 4), bonusType);
        return bonus;
    }
}
