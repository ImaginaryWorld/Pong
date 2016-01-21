package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.phuda.pong.UI.Button;

public class MenuScreen extends PongScreen
{
	//Menu menu;
	//MenuRenderer menu;
	SpriteBatch batch;
	Button start_pvp_button, start_pvc_button, other2_button;

	MenuScreen(Game game)
	{
		super(game);
		start_pvp_button = new Button(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() / 4 * 3, "pvp.png");
		start_pvc_button = new Button(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() / 4 * 2, "pvc.png");
		other2_button = new Button(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() / 4, "undef.png");
		System.out.println("init MenuScreen");
	}
	
	public void show()
	{
		batch = new SpriteBatch();
	}

	public void render(float delta)
	{

		if (start_pvp_button.isPressed()){
			game.setScreen(new GameScreen(game, "pvp")); // start player versus player
		}
		
		if (start_pvc_button.isPressed()){
			game.setScreen(new GameScreen(game, "pvc")); // start player versus computer
		}
		
		if (other2_button.isPressed()){  System.out.println("other2_button pressed");  }
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
		
		batch.begin();
		start_pvp_button.draw(batch);
		start_pvc_button.draw(batch);
		other2_button.draw(batch);
		batch.end();
	}
	
}
