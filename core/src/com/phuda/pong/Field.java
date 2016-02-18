package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.UI.Button;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

import java.util.ArrayList;

// Class that controls game field.
public class Field {
	public GameScreen screen;
	public int screenWidth, screenHeight, ballsCount, ballsSpeed;
	public float scoreShift;
	public Board player1Board, player2Board;
    public String winner = "none";
	public ArrayList<Ball> balls;
	public Bonus[] bonuses;
	public Button pauseButton, resumeButton, menuButton;
	float startTimer = 0f;
	boolean paused;

	Field(GameScreen screen) {
		this.screen = screen;
		this.screenWidth = screen.screenWidth;
		this.screenHeight = screen.screenHeight;
		this.ballsCount = screen.ballsCount;
		this.ballsSpeed = screen.ballsSpeed;
		// Balls generation
		balls = new ArrayList<Ball>();
		/*
		 * Boards generation
		 * Player 1 aka top player
		 */
		player1Board = new Board(screenWidth, screenHeight, "top", this, screen.ai);
		// Player 2 aka bottom player
		player2Board = new Board(screenWidth, screenHeight, "bottom", this, 0);
		// Bonuses generation
		bonuses = new Bonus[3];
		// Buttons generation
		pauseButton = new Button(screenWidth / 17 * 16, screenHeight / 2, "images/pause.png", true, screen);
		resumeButton = new Button(screenWidth / 2, screenHeight - screenHeight / 3, "images/play.png", false, screen);
		menuButton = new Button(screenWidth / 2, screenHeight / 3, "images/menu.png", false, screen);
	}

	public void updateState(float delta) {
		// Buttons
		processButtons();
		if (!paused) {
			if (startTimer < 3) {
				startTimer += delta;
				// no movement until timer
			}
			else {
				// Toggle slow-motion
				if (Gdx.input.isKeyPressed(Input.Keys.S))
					delta = delta * 0.2f;
				// Updating state of balls
				processBalls(delta);
				// Updating state of boards
				processBoards(delta);
				// Updating bonuses
				updateFeatures(delta);
			}
		}
	}

	// Buttons
	private void processButtons() {
		if (!paused) {
			if (pauseButton.isPressed())
				pauseStateChange();
		}
		else {
            if (winner.equals("none"))
                if (resumeButton.isPressed())
					pauseStateChange();
			if (menuButton.isPressed()) {
				screen.dispose();
				screen.game.launchMenu();
			}
		}
	}

	private void buttonsSwitch() {
		pauseButton.isActive = !pauseButton.isActive;
		resumeButton.isActive = !resumeButton.isActive;
		menuButton.isActive = !menuButton.isActive;
	}

	// Boards
	private void processBoards(float delta) {
		player1Board.updateState(delta, balls);
		player2Board.updateState(delta, balls);
	}

	// Balls
	private void processBalls(float delta) {
		// Creating new balls
		createNewBalls();
		updateBalls(delta);
		removeBalls();
	}

	private void updateBalls(float delta) {
		for (int i = 0; i < balls.size(); i++)
			balls.get(i).updateState(delta);
	}

	private void createNewBalls() {
		// Creating new ball with probability that depends on number available max balls and actual balls on field
		if (1000 - ballsCount + balls.size() < MathUtils.random(1000) || balls.size() == 0)
			balls.add(new Ball(this, screenWidth, screenHeight, balls.size() - 1));
	}

	private void removeBalls() {
		for (int i = 0; i < balls.size(); i++) {
			//int y = Gdx.graphics.getHeight() / 2;
			if (balls.get(i).outOfField()) {
				updateScore(balls.get(i));
				// Deleting this ball
				balls.remove(i);
			}
		}
	}

	// Features
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
				checkBonusOverlaps(bonuses[i], i);
			}
		}
	}

	private void checkBonusOverlaps(Bonus bonus, int elementNumber) {
		for (int j = 0; j < bonuses.length; j++) {
			if (bonuses[j] != null && bonuses[j] != bonus)
				// Calculating if bonus will be too close to other one
				if (bonus.vector.dst(bonuses[j].vector) < bonus.fullRadius * 4) {
					// Deleting new bonus
					bonuses[elementNumber] = null;
					return;
				}
		}
	}

	// Score
	private void updateScore(Ball ball) {
		// Calculate which board scores the goal
		if (ball.bounds.y > Gdx.graphics.getHeight() / 2)
			player2Board.score += ball.bounds.radius;
		else
			player1Board.score += ball.bounds.radius;
		// Checking if there's a winner
		scoreShift = (player2Board.score - player1Board.score) * 4;
		if (scoreShift >= screenWidth / 2) {
			pauseStateChange();
			menuButton.setPos(screenWidth / 2, screenHeight / 2);
			winner = player2Board.name;
			screen.soundHandler.playSound(ball.sound_winnerSound, 1);
		}
		else if (scoreShift <= -screenWidth / 2) {
			pauseStateChange();
			menuButton.setPos(screenWidth / 2, screenHeight / 2);
			winner = player1Board.name;
			screen.soundHandler.playSound(ball.sound_winnerSound, 1);
		}
		else
			// Just playing sound of scoring the goal
			screen.soundHandler.playSound(ball.sound_goalSound, 1);
	}

	// Pause
	private void pauseStateChange() {
		paused = !paused;
		buttonsSwitch();
	}
}