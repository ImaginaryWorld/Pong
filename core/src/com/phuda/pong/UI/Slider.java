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

    public Slider(int _x, int _y, int _min, int _max, int defValue, String _label){

        String images_path = "images_hi/";
        base = new Texture(Gdx.files.internal(images_path + "slider_base.png"));
        unit = new Texture(Gdx.files.internal(images_path + "slider_unit.png"));

        offset = unit.getWidth()/2;

        x = _x;      y = _y;
        min = _min;  max = _max;
        label = _label;

        // Setup default value
        ux = (int)MathUtils.lerp( x - base.getWidth()/2 + offset,
                x + base.getWidth()/2 - offset, (float)defValue/max);
        sx = ux;

        // Touch zone
        rect = new Rectangle(x - base.getWidth()/2, y - base.getHeight()/2,
                base.getWidth(), base.getHeight());

        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void isPressed(){
        if (Gdx.input.isTouched()) {
            int ix = Gdx.input.getX();
            int iy = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;

            if (rect.contains(ix, iy)) {
                ux = ix;
                if (ux < x - base.getWidth()/2 + offset)
                    ux = x - base.getWidth()/2 + offset;
                if (ux > x + base.getWidth()/2 - offset)
                    ux = x + base.getWidth()/2 - offset;
            }
        }
        sx += (ux - sx) / 5;

        float slide = (float) (ux - x + base.getWidth()/2 - offset) / (base.getWidth() - offset*2);
        value = (int)MathUtils.lerp(min, max , slide);
    }

    public void draw(SpriteBatch batch){
        batch.draw(base, x - base.getWidth()/2, y - base.getHeight()/2);
        batch.draw(unit, sx - unit.getWidth()/2, y - unit.getHeight()/2);
        font.draw(batch, label + Integer.toString(value), x, y + (int)(base.getHeight()*0.7));
    }
}
