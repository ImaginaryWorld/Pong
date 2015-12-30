package com.phuda.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScreen extends PongScreen
{
	//Menu menu;
	//MenuRenderer menu;
	BitmapFont font;
	final GlyphLayout layout;
	Texture button_tex;
	SpriteBatch batch;

	MenuScreen(Game game)
	{
		super(game);
		font = new BitmapFont();
		layout = new GlyphLayout(font, "START");
		button_tex = new Texture(Gdx.files.internal("booble.png"));
		System.out.println("init MenuScreen");
	}
	
	public void show()
	{
		batch = new SpriteBatch();
	}

	public void render(float delta)
	{
		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game)); // start game
		}
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
		
		batch.begin();
		batch.draw(button_tex, Gdx.graphics.getWidth()/2 - button_tex.getWidth()/2, 
				               Gdx.graphics.getHeight()/2 - button_tex.getHeight()/2);
		font.draw(batch, "START", Gdx.graphics.getWidth()/2 - layout.width/2, Gdx.graphics.getHeight()/2 + layout.height/2);
		batch.end();
	}
	
}
