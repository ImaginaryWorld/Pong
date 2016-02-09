package com.phuda.pong.UI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.phuda.pong.PongScreen;

public class Slider {

    public int value;
    private int x, y, min, max;
    private float ux, sx, offset, space;
    private String label;
    private Rectangle bounds;
    private Texture base, unit;
    private BitmapFont font;
    private float left, right;
    private float hAspect;
    private PongScreen screen;

    public Slider(int _x, int _y, int _min, int _max, int defValue, String _label, PongScreen screen) {
        String images_path = "images/";
        base = new Texture(Gdx.files.internal(images_path + "slider_base.png"));
        base.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        unit = new Texture(Gdx.files.internal(images_path + "slider_unit.png"));
        unit.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Getting ratio of screen in desktop base (500 x 700)
        hAspect = (Gdx.graphics.getWidth() + Gdx.graphics.getHeight())
                / (float)(500 + 700);
        x = _x;      y = _y;
        min = _min;  max = _max;
        label = _label;
        // Distance from start to active zone
        offset = unit.getWidth() / 2 * hAspect;
        // Bounds
        left = (x - base.getWidth()/2 * hAspect + offset);
        right = (x + base.getWidth()/2 * hAspect - offset);
        // Touch zone
        bounds = new Rectangle(left, y - base.getHeight()/2 * hAspect,
                right - left, base.getHeight() * hAspect);
        // Setup default value
        ux = MathUtils.lerp(left, right, (float) (defValue - min) / (max - min));
        sx = ux;
        // Distance between two points of slider
        space = (bounds.width / (float)(max - min));
        // Fonts
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        // Screen
        this.screen = screen;
    }

    public void isPressed() {
        if (Gdx.input.isTouched()) {
            int ix = Gdx.input.getX();
            int iy = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;
            // Press on slider
            if (bounds.contains(ix, iy)) {
                ux = ix;
            }
        }
        // Number of received point on slider
        int point = MathUtils.round((ux - left) / space);
        // Received value
        value = point + min;
        // Rigorous point
        ux = left + (point * space);
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

    public void disposeTextures() {
        base.dispose();
        unit.dispose();
    }
}
