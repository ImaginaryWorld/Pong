package com.phuda.pong.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.PongScreen;

// custom Button class, i not like libgdx method of buttons
public class Button {

    private float x, y;
	private int target_x, target_y, init_x, init_y;
	private int height, width;
	private float scale = 0f;
	private float target_scale = 1f;
	public Rectangle bounds;
	private Texture texture;
	private boolean press, over;
	public boolean isActive;
	private PongScreen screen;
	int buttonSound;
    // Getting ratio of screen in desktop base (500 x 700)
    private float hAspect = (Gdx.graphics.getWidth() + Gdx.graphics.getHeight())
            / (float)(500 + 700);

	public Button(int _x, int _y, String img_source, boolean isActive, PongScreen screen) {
		texture = new Texture(Gdx.files.internal(img_source));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear); // smooth resizing
		width = (int)(texture.getWidth() * hAspect);
		height = (int)(texture.getHeight() * hAspect);
		init_x = _x - width/2;
		init_y = _y - height/2;
        // popup buttons from screen center
		x = Gdx.graphics.getWidth()/2 - width/2;
		y = Gdx.graphics.getHeight()/2 - height/2;
		target_x = init_x;
		target_y = init_y;
		// Activating button if it's needed already after creating
		this.isActive = isActive;
        // touch zone
		bounds = new Rectangle(init_x, init_y, width, height);
		this.screen = screen;
		// Number of button's sound
		buttonSound = screen.soundHandler.buttonSound;
	}

    public void setPos(int _x, int _y) {
        init_x = _x - width/2;
        init_y = _y - height/2;
		target_x = init_x;
		target_y = init_y;
        bounds = new Rectangle(init_x, init_y, width, height);
    }
	
	public void draw(SpriteBatch batch) {
		scale = MathUtils.lerp(scale, target_scale, .2f);
		x = MathUtils.lerp(x, target_x, .1f);
		y = MathUtils.lerp(y, target_y, .1f);
		batch.draw(texture, x - (width/2 * (scale - 1)),  y - (height/2 * (scale - 1)),
                width * scale, height * scale);
	}
	
	public boolean isPressed() {
		if (isActive) {
			if (Gdx.input.isTouched()) {
				int x = Gdx.input.getX();
				int y = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;
				over = (bounds.contains(x, y));
				if (bounds.contains(x, y)) {
					press = true;
				}
				if (press) {
					target_scale = .7f;
					target_x = init_x + (Gdx.input.getDeltaX() * 6);
					target_y = init_y + (Gdx.input.getDeltaY() * -6);
				}
			}
			else if (press) {
				target_scale = 1f;
				press = false;
				if (over) {
					screen.soundHandler.playSound(buttonSound, 1);
					over = false;
					return true;
				}
			} else {
				target_scale = 1f;
				target_x = init_x;
				target_y = init_y;
			}
		}
		return false;
	}

	public void disposeTexture() {
		texture.dispose();
	}
}
