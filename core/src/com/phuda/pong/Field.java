package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.Effects.Effect;
import com.phuda.pong.UI.Button;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

import java.util.ArrayList;

// Class that controls game field.
public class Field {
	GameScreen screen;
	public int screenWidth, screenHeight, ballsCount;
	public Board player1Board, player2Board;
	public ArrayList<Ball> balls;
	public Bonus[] bonuses;
	public Button pauseButton, resumeButton, menuButton;
    float startTimer = 0f;
	boolean paused;
    Music music;

	Field(GameScreen screen, int ballsCount, int ai, int screenWidth, int screenHeight)
	{
		this.screen = screen;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.ballsCount = ballsCount;
		// Balls generation
		balls = new ArrayList<Ball>();
		// Boards generation
		if (ai != 0) {
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
		// Buttons generation
		pauseButton = new Button(screenWidth / 6 * 5, screenHeight / 2, "images_hi/pause.png");
		resumeButton = new Button(screenWidth / 2, screenHeight - screenHeight / 3, "images_hi/play.png");
		menuButton = new Button(screenWidth / 2, screenHeight / 3, "images_hi/menu.png");
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/pong-song.ogg"));
        music.setLooping(true);
	}
	
	public void updateState(float delta) {
		// Buttons
		processButtons();
		if (!paused) {
			if (startTimer < 3) {
				startTimer += delta;
				// no movement until timer
				delta = 0;
			}
            else if (!music.isPlaying())
                music.play();
			// Toggle slow-motion
			if (Gdx.input.isKeyPressed(Input.Keys.S))
				delta = delta * 0.2f;
			// Updating state of boards
			processBoards(delta);
			// Updating state of balls
			processBalls(delta);
			// Updating bonuses
			updateFeatures(delta);
		} else if (music != null && music.isPlaying())
            music.pause();
	}

	private void processButtons() {
		if (!paused) {
			if (pauseButton.isPressed())
				paused = true;
		} else {
			if (resumeButton.isPressed())
				paused = false;
			if (menuButton.isPressed()) {
				music.stop();
				music.dispose();
				music = null; // trying to avoid audio error
				screen.game.setScreen(new MenuScreen(screen.game));
			}
		}
	}

	private void processBoards(float delta) {
		player1Board.updateState(delta, balls);
		player2Board.updateState(delta, balls);
	}

	private void processBalls(float delta) {
		// Creating new balls
		createNewBalls();
		updateBalls(delta);
		removeBalls();
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
				checkBonusOverlaps(bonuses[i], i);
			}
		}
	}

	private void createNewBalls() {
		// Creating new ball with probability that depends on number available max balls and actual balls on field
		if (1000 - ballsCount + balls.size() < MathUtils.random(1000) || balls.size() == 0)
			balls.add(new Ball(this, screenWidth, screenHeight, balls.size() - 1));
	}

	private void updateBalls(float delta) {
		for (int i = 0; i < balls.size(); i++)
			balls.get(i).updateState(delta);
	}

	private void removeBalls() {
		for (int i = 0; i < balls.size(); i++) {
			//int y = Gdx.graphics.getHeight() / 2;
			if (balls.get(i).outOfField()) {
				// Who is winner ?
				if (balls.get(i).bounds.y > Gdx.graphics.getHeight() / 2) {
					player1Board.score += balls.get(i).bounds.radius;
				} else {
					player2Board.score += balls.get(i).bounds.radius;
				}
				// Deleting this ball
				balls.remove(i);
			}
		}
	}

	private void checkBonusOverlaps(Bonus bonus, int elementNumber) {
		for (int j = 0; j < bonuses.length; j++) {
			if (bonuses[j] != null && bonuses[j] != bonus)
					// Calculating if bonus will be too close to other one
					if (bonus.vector.dst(bonuses[j].vector) < bonus.fullRadius * 4) {
						System.out.println("New bonus too close to another one");
						// Deleting new bonus
						bonuses[elementNumber] = null;
						return;
					}
		}
	}
}
