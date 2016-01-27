package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Effects.Effect;
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
		bonuses = new Bonus[3];
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
			balls.get(i).updateState(delta);
		}
	}

	private void updateFeatures(float delta) {
		// Bonuses update
		updateBonuses(delta);
	}

	private void updateBonuses(float delta) {
		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] != null) {
				bonuses[i].updateState(delta);
				// Deleting bonuses which time has elapsed
				if (bonuses[i].time < 0)
					bonuses[i] = null;
			}
		}
		for (int i = 0; i < bonuses.length; i++) {
			// Creating new ones with 0.3% probability
			if (bonuses [i] == null && MathUtils.random(1000) > 997) {
				bonuses[i] = new Bonus(this, this.screenWidth, this.screenHeight, 16);
				checkBonusOverlaps(bonuses[i]);
			}
		}
	}

	private void checkBonusOverlaps(Bonus bonus) {
		for (int j = 0; j < bonuses.length; j++) {
			if (bonuses[j] != null)
				if (bonuses[j] != bonus)
					// Calculating if bonus will overlaps with some other in full size
					if (bonus.vector.dst(bonuses[j].vector) <
							Math.sqrt(bonus.fullRadius * bonus.fullRadius * 2)) {
						System.out.println("New bonus will overlaps other one");
						bonus = null;
					}
		}
	}
}
