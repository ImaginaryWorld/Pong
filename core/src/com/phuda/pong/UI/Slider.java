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
    // Real sizes that are used
    float baseWidth, baseHeight, unitWidth, unitHeight;
    private BitmapFont font;
    private float left, right;
    private float hAspect;
    private PongScreen screen;
    public int sliderSound;
    private boolean pressed;
    public boolean update;

    public Slider(int _x, int _y, int _min, int _max, int defValue, String _label, PongScreen screen) {
        String images_path = "images/";
        base = new Texture(Gdx.files.internal(images_path + "slider_base.png"));
        base.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        unit = new Texture(Gdx.files.internal(images_path + "slider_unit.png"));
        unit.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        baseWidth = base.getWidth() / 1.5f;
        baseHeight = base.getHeight() / 1.5f;
        unitWidth = unit.getWidth() / 1.5f;
        unitHeight = unit.getHeight() / 1.5f;
        System.out.println(baseWidth);
        // Getting ratio of screen in desktop base (500 x 700)
        hAspect = (Gdx.graphics.getWidth() + Gdx.graphics.getHeight())
                / (float)(500 + 700);
        x = _x;      y = _y;
        min = _min;  max = _max;
        label = _label;
        // Distance from start to active zone
        offset = unitWidth / 2 * hAspect;
        // Bounds
        left = (x - baseWidth / 2 * hAspect + offset);
        right = (x + baseWidth / 2 * hAspect - offset);
        // Touch zone
        bounds = new Rectangle(left, y - baseHeight / 2 * hAspect,
                right - left, baseHeight * hAspect);
        // Setup default value
        ux = MathUtils.lerp(left, right, (float) (defValue - min) / (max - min));
        sx = ux;
        value = defValue;
        // Distance between two points of slider
        space = (bounds.width / (float)(max - min));
        // Fonts
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        // Screen
        this.screen = screen;
        // Sound number
        sliderSound = screen.soundHandler.sliderSound;
    }

    public void isPressed() {
        if (update) {
            update = false;
            screen.soundHandler.playSound(sliderSound, 1);
        }
        if (Gdx.input.isTouched()) {
            int ix = Gdx.input.getX();
            int iy = (Gdx.input.getY() - Gdx.graphics.getHeight()) * -1;
            // Press on slider
            if (bounds.contains(ix, iy)) {
                ux = ix;
                pressed = true;
            }
        }
        else if (pressed) {
            pressed = false;
            update = true;
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

    public void setValue(int value) {
        this.value = value;
        ux = (value - min) * space + left;
        sx = ux;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(base, x - baseWidth/2 * hAspect, y - baseHeight/2 * hAspect,
                baseWidth * hAspect, baseHeight * hAspect);
        batch.draw(unit, sx - unitWidth/2 * hAspect, y - unitHeight/2 * hAspect,
                unitWidth * hAspect, unitHeight * hAspect);
        // AI strength label
        if (label.equals("AI strength: ")) {
            String strength = null;
            switch (value) {
                case 1: strength = "Weak";
                    break;
                case 2: strength = "Medium";
                    break;
                case 3: strength = "Strong";
                    break;
            }
            drawLabel(batch, label + strength);
        }
        else if (label.equals("Balls speed: "))
            drawLabel(batch, label + Integer.toString(value) + "%");
        else
            drawLabel(batch, label + Integer.toString(value));
    }

    private void drawLabel(SpriteBatch batch , String text) {
        font.draw(batch, text, x - baseWidth * 5 / 12 * hAspect, y + (int)(baseHeight * 1.2 * hAspect));
    }

    public void disposeTextures() {
        base.dispose();
        unit.dispose();
    }
}
