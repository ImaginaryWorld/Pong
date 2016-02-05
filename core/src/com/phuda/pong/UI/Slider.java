package com.phuda.pong.UI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Slider {

    public int value;
    private int ux, sx, x, y, min, max, screenWidth, screenHeight;
    private float offset, space;
    private String label;
    private Rectangle bounds;
    private Texture base, unit;
    private BitmapFont font;

    public Slider(int _x, int _y, int _min, int _max, int defValue, String _label){
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        String images_path = "images_hi/";
        base = new Texture(Gdx.files.internal(images_path + "slider_base.png"));
        unit = new Texture(Gdx.files.internal(images_path + "slider_unit.png"));
        // Disposition
        x = _x;      y = _y;
        min = _min;  max = _max;
        offset = screenWidth / 20;
        space = ((x - screenWidth / 4) * 2 - offset * 2) / (float)(max - min);
        // Slider's subscription
        label = _label;
        // Touch zone
        bounds = new Rectangle(x - screenWidth / 4 + offset, y - screenHeight / 20,
                (x - screenWidth / 4) * 2 - offset * 2, screenHeight / 10);
        // Set default value
        ux = (int)MathUtils.lerp(bounds.x, bounds.x + screenWidth / 2, (float) (defValue - min) / (max - min) );
        sx = ux;
        // Fonts
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void isPressed(){
        if (Gdx.input.isTouched()) {
            int ix = Gdx.input.getX();
            int iy = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;
            // Slider will be affected only if touch is inside slider
            if (bounds.contains(ix, iy))
                ux = ix;
        }
        // Received value
        float slide = (ux - bounds.x) / bounds.width;
        value = MathUtils.round((max - 1) * slide) + 1;
        // Rigorous point
        ux = (int)(bounds.x + ((value - 1) * space));
        // Position of slider's pointer
        sx += (ux - sx) / 5;
    }

    public void draw(SpriteBatch batch){
        batch.draw(base, x - screenWidth / 4, y - screenHeight / 20, (x - screenWidth / 4) * 2, screenHeight / 10);
        batch.draw(unit, sx - screenWidth / 20, y - screenHeight / 20, screenWidth / 10, screenHeight / 10);
        font.draw(batch, label + Integer.toString(value), x - base.getWidth() / 3,
                y + (int)(base.getHeight() * 0.7));
    }
}
