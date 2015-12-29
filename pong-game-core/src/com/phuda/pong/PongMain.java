package com.phuda.pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Main class that launches everything
public class PongMain extends ApplicationAdapter {
	
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () 
	{
		batch = new SpriteBatch();
		img = new Texture("chev.jpg");
	}

	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, -100, -200);
		batch.end();
	}
}
