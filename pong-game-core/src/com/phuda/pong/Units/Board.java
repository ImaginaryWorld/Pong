package com.phuda.pong.Units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Board {

	final int SLOWER = 7;
	
	public int x, y, target_x;
	public double speed;
	public Rectangle bounds;
	boolean reverse;
	
	public Board(int _x, int _y, boolean reverse)
	{
		bounds = new Rectangle();
		x = _x;
		y = _y;
		target_x = 320;
		bounds.x = x - 50;
		bounds.y = y - 15;
		bounds.width = 100;
		bounds.height = 30;
		this.reverse = reverse;
	}
	
	public void updateState(float time, Ball[] balls)
	{
		processAction();
		// checkBalls(balls);
	}
	
	private void processAction() {
		x -= (x - target_x) / SLOWER;
		bounds.x = x;
		speed = target_x - x;

		// Fix this one
		
		// 1st player
		if (Gdx.input.isTouched() && reverse) {
			if (Gdx.input.getY() < 120){
				// set center of board in X
				target_x = Gdx.input.getX() - (int) (bounds.width / 2);
				// System.out.println("x = " + Gdx.input.getX() + ", y = " + Gdx.input.getY());
			}
		}
		
		//2nd player
		if (Gdx.input.isTouched() && !reverse) {
			if (Gdx.input.getY() > 360){
				// set center of board in X
				target_x = Gdx.input.getX() - (int) (bounds.width / 2);
				// System.out.println("x = " + Gdx.input.getX() + ", y = " + Gdx.input.getY());
			}
		}
	} 
}
