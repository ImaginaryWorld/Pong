package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.phuda.pong.Units.Button;

public class MenuScreen extends PongScreen
{
	//Menu menu;
	//MenuRenderer menu;
	SpriteBatch batch;
	Button start_button, other_button, other2_button;

	MenuScreen(Game game)
	{
		super(game);
		start_button = new Button(Gdx.graphics.getWidth()/2, 500, "start.png");
		other_button = new Button(Gdx.graphics.getWidth()/2, 350, "undef.png");
		other2_button = new Button(Gdx.graphics.getWidth()/2, 200, "undef.png");
		System.out.println("init MenuScreen");
	}
	
	public void show()
	{
		batch = new SpriteBatch();
	}

	public void render(float delta)
	{

		if (start_button.isPressed()){
			game.setScreen(new GameScreen(game)); // start game
		}
		
		if (other_button.isPressed()){  System.out.println("other_button pressed");  }
		
		if (other2_button.isPressed()){  System.out.println("other2_button pressed");  }
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
		
		batch.begin();
		start_button.draw(batch);
		other_button.draw(batch);
		other2_button.draw(batch);
		batch.end();
	}
	
}
