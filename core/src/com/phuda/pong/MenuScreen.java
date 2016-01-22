package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.phuda.pong.UI.Button;
import com.phuda.pong.UI.Slider;

public class MenuScreen extends PongScreen
{
	//Menu menu;
	//MenuRenderer menu;
	SpriteBatch batch;
	Button start_pvp_button, start_pvc_button, other2_button;
    Slider balls_slider, ai_mode_slider;

	MenuScreen(Game game)
	{
		super(game);
        int y = (int) (Gdx.graphics.getHeight() / 3 * 1.5);
        int x = Gdx.graphics.getWidth() / 4;
		start_pvp_button = new Button(x,   y,       "pvp.png");
		start_pvc_button = new Button(x*2, y + y/3, "pvc.png");
		other2_button =    new Button(x*3, y,       "undef.png");

        balls_slider = new Slider(x*2, y - y/2, 1, 10, 2, "Balls count: ");
        ai_mode_slider = new Slider(x*2, y - (int)(y/1.2), 1, 3, 2, "AI strength: ");
		System.out.println("init MenuScreen");
	}
	
	public void show()
	{
		batch = new SpriteBatch();
	}

	public void render(float delta)
	{

		if (start_pvp_button.isPressed()){
			game.setScreen(new GameScreen(game, "pvp", balls_slider.value, 0));
		}
		
		if (start_pvc_button.isPressed()){
			game.setScreen(new GameScreen(game, "pvc", balls_slider.value, ai_mode_slider.value));
		}
		
		if (other2_button.isPressed()){  System.out.println("other2_button pressed");  }

        balls_slider.isPressed();
        ai_mode_slider.isPressed();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
		
		batch.begin();
		start_pvp_button.draw(batch);
		start_pvc_button.draw(batch);
		other2_button.draw(batch);

        balls_slider.draw(batch);
        ai_mode_slider.draw(batch);
		batch.end();
	}
	
}
