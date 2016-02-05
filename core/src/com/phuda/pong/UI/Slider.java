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
    private int ux, sx, x, y, min, max, offset;
    private String label;
    private Rectangle rect;
    private Texture base, unit;
    private BitmapFont font;
    // Getting ratio of screen in desktop base (500 x 700)
    private int left, right;
    private float hAspect = Gdx.graphics.getHeight() / 700.f;

    public Slider(int _x, int _y, int _min, int _max, int defValue, String _label){


        String images_path = "images_hi/";
        base = new Texture(Gdx.files.internal(images_path + "slider_base.png"));
        base.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        unit = new Texture(Gdx.files.internal(images_path + "slider_unit.png"));
        unit.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        x = _x;      y = _y;
        min = _min;  max = _max + 1;
        label = _label;

        left = (int)(x - (base.getWidth()/2) * hAspect);
        right = (int)(x + (base.getWidth()/2) * hAspect);
        // Distance from start to active zone
        offset = (int)(unit.getWidth()/2 * hAspect);

        // Setup default value
        ux = (int)MathUtils.lerp( left + offset , right - offset, (float)defValue/max );
        sx = ux;

        // Touch zone
        rect = new Rectangle( x - base.getWidth()/2 * hAspect, y - base.getHeight()/2 * hAspect,
                base.getWidth() * hAspect, base.getHeight() * hAspect);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void isPressed(){
        if (Gdx.input.isTouched()) {
            int ix = Gdx.input.getX();
            int iy = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;

            if (rect.contains(ix, iy)) {
                ux = ix;
                if (ux < left + offset)
                    ux = left + offset;
                if (ux > right - offset)
                    ux = right - offset;
            }
        }
        float slide = (float)(ux - left - offset) / (base.getWidth() * hAspect - offset * 2 + 1);
        value = (int)MathUtils.lerp(min, max, slide);
        // Graphical slider slow follow
        sx += (ux - sx) / 5;
    }

    public void draw(SpriteBatch batch){
        batch.draw(base, x - base.getWidth()/2 * hAspect, y - base.getHeight()/2 * hAspect,
                base.getWidth() * hAspect, base.getHeight() * hAspect);
        batch.draw(unit, sx - unit.getWidth()/2 * hAspect, y - unit.getHeight()/2 * hAspect,
                unit.getWidth() * hAspect, unit.getHeight() * hAspect);
        font.draw(batch, label + Integer.toString(value), x - base.getWidth()/3 * hAspect,
                y + (int)(base.getHeight()*0.7 * hAspect));
    }
}
