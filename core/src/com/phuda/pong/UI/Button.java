package com.phuda.pong.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

// custom Button class, i not like libgdx method of buttons
public class Button {
	
	private int x, y, target_x, target_y, init_x, init_y;
	private int height, width;
	private float scale = 0f;
	private float target_scale = 1f;
	private Rectangle rect;
	private Texture texture;
	private boolean press, over;
	
	public Button(int _x, int _y, String img_source) {
		texture = new Texture(Gdx.files.internal(img_source));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear); // smooth resizing
		height = texture.getHeight();
		width = texture.getWidth();
		init_x = _x - width/2;
		init_y = _y - height/2;
		x = Gdx.graphics.getWidth()/2 - width/2;
		y = Gdx.graphics.getHeight()/2 - height/2;
		rect = new Rectangle(init_x, init_y, width, height);
	}
	
	public void setPos(int _x, int _y) {
		init_x = _x;
		init_y = _y;
		rect.x = init_x;
		rect.y = init_y;
	}
	
	public void draw(SpriteBatch batch){
		scale = MathUtils.lerp(scale, target_scale, .2f);
		x = (int) MathUtils.lerp(x, target_x, .1f);
		y = (int) MathUtils.lerp(y, target_y, .1f);
		batch.draw(texture, x - (width/2 * (scale - 1)),  y - (height/2 * (scale - 1)), width * scale, height * scale);
	}
	
	public boolean isPressed() {
		if (Gdx.input.isTouched()) {
			int x = Gdx.input.getX();
			int y = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;
			over = (rect.contains(x, y));
			if (rect.contains(x, y)){ press = true; }
			if (press) {
				target_scale = .7f;
				target_x = init_x + (Gdx.input.getDeltaX() * 6);
				target_y = init_y + (Gdx.input.getDeltaY() * -6);
			}
		}
		else if (press){
				target_scale = 1f;
				press = false;
				if (over) { 
					over = false;
					return true; 
				}
		}
		else { 
			target_scale = 1f;
			target_x = init_x;
			target_y = init_y;
		}
		return false;
	}

}
