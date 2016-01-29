package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.UI.Button;
import com.phuda.pong.UI.Slider;

public class MenuScreen extends PongScreen
{
	//Menu menu;
	//MenuRenderer menu;
    final int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
	SpriteBatch batch;
	Button start_pvp_button, start_pvc_button, other2_button;
    Slider balls_slider, ai_mode_slider;
    Texture backGround;
    float backGroundRotation;

	MenuScreen(Game game)
	{
		super(game);
        int y = (int) (Gdx.graphics.getHeight() / 1.7);
        int x = Gdx.graphics.getWidth() / 4;

        String images_path = "images_hi/";
		start_pvp_button = new Button(x,   y,      images_path + "pvp.png");
		start_pvc_button = new Button(x*2, y + y/3,images_path + "pvc.png");
		other2_button =    new Button(x*3, y,      images_path + "undef.png");
        backGround = new Texture(Gdx.files.internal(images_path + "background.png"));
        backGround.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        balls_slider = new Slider(x*2, y - y/2, 1, 12, 2, "Balls count: ");
        ai_mode_slider = new Slider(x*2, y - (int)(y/1.2), 1, 3, 2, "AI strength: ");
		System.out.println("init MenuScreen");
	}
	
	public void show()
	{
		batch = new SpriteBatch();
	}

	public void render(float delta)
	{
        backGroundRotation += delta * 2;

		if (start_pvp_button.isPressed()){
			game.setScreen(new GameScreen(game, balls_slider.value, 0));
		}
		
		if (start_pvc_button.isPressed()){
			game.setScreen(new GameScreen(game, balls_slider.value, ai_mode_slider.value));
		}
		
		if (other2_button.isPressed()){  System.out.println("other2_button pressed");  }

        balls_slider.isPressed();
        ai_mode_slider.isPressed();

        Gdx.gl.glClearColor(.15f, .05f, .05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
        batch.setColor(MathUtils.sin(backGroundRotation/2)/2 + 0.5f, 1f, 1f, 1f);
        batch.draw(backGround, w/2 - backGround.getWidth()/2, h/2 - backGround.getHeight()/2,
                backGround.getWidth()/2, backGround.getHeight()/2,
                backGround.getWidth(), backGround.getHeight(),
                1, 1, backGroundRotation, 0, 0, backGround.getWidth(), backGround.getHeight(),
                false, false);
        batch.setColor(1f, 1f, 1f, 1f);
		start_pvp_button.draw(batch);
		start_pvc_button.draw(batch);
		other2_button.draw(batch);

        balls_slider.draw(batch);
        ai_mode_slider.draw(batch);
		batch.end();
	}
	
}
