package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Board {

	final int SLOWER = 7;
	
	public int x, y, target_x;
	double speed;
	public Rectangle bounds;
	
	public Board(int _x, int _y)
	{
		bounds = new Rectangle();
		x = _x;
		y = _y;
		target_x = 250;
		bounds.x = x;
		bounds.y = y;
		bounds.width = 100;
		bounds.height = 20;
	}
	
	public void updateState(float time, Ball[] balls)
	{
		processAction();
		checkBalls(balls);
	}
	
	private void processAction() {
		x -= (x - target_x) / SLOWER;
		bounds.x = x;
		speed = target_x - x;

		if (Gdx.input.isTouched()) {
			if (Gdx.input.getY() > y - 200 && Gdx.input.getY() < y + 200){
				target_x = Gdx.input.getX() - (int) (bounds.width / 2); // set x
																		// into
																		// center of
																		// board
			}
		}
	}
	private void checkBalls(Ball[] balls){
		for (int i = 0; i < balls.length; i++){
			if (balls[i] != null){
				if (((balls[i].y - balls[i].bounds.radius) <= (bounds.y + bounds.height)) &&
						((balls[i].y + balls[i].bounds.radius) >= (bounds.y - bounds.height)) &&
						(bounds.x < balls[i].x + balls[i].bounds.radius*2) &&
						((bounds.x + bounds.width) > x)){
					balls[i].ySpeed = - balls[i].ySpeed;
					System.out.println(speed);
					balls[i].xSpeed += speed / 10;
				}
			}
		}
	}
	 
}
