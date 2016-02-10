package com.phuda.pong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.phuda.pong.UI.Button;
import com.phuda.pong.UI.Slider;

import java.io.*;

public class MenuScreen extends PongScreen
{
	// Menu menu;
	// MenuRenderer menu;
    final int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight(),
    ball_sliderNum = 0, ai_mode_sliderNum = 1, sound_volume_sliderNum = 2, music_volume_sliderNum = 3;
	SpriteBatch batch;
	Button start_pvp_button, start_pvc_button, other2_button, title_button;
    Slider balls_slider, ai_mode_slider, sound_volume_slider, music_volume_slider;
    float nextScreenDark = 0f;
    GameScreen nextScreen = null;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Texture backGround;
    float backGroundRotation;
    // Activating settings screen
    private boolean settingsMenu;

	MenuScreen(PongGame game, PongSoundHandler soundHandler) {
		super(game, soundHandler);
        int x = w / 4;
        int y = (int)(h / 1.7);
        // Images path
        String images_path = "images/";
        // Buttons
		start_pvp_button = new Button(x,     y + y / 3, images_path + "pvp.png", true, this);
		start_pvc_button = new Button(x * 3, y + y / 3, images_path + "pvc.png", true, this);
		other2_button =    new Button(x * 2,     y / 3, images_path + "settings.png", true, this);
        title_button =     new Button(x * 2, y - y / 9, images_path + "title.png", true, this);
        // Background
        backGround = new Texture(Gdx.files.internal(images_path + "background.png"));
        backGround.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Sliders
        balls_slider = new Slider(x * 2, h * 4 / 7, 1, 12, 2, "Balls max count: ", this);
        ai_mode_slider = new Slider(x * 2, h * 3 / 7, 1, 3, 2, "AI strength: ", this);
        sound_volume_slider = new Slider(x * 2, h * 2 / 7, 0, 100, 40, "Sound volume: ", this);
        music_volume_slider = new Slider(x * 2, h / 7, 0, 100, 40, "Music volume: ", this);
        // Loading saved configuration
        loadConfiguration();
        // Music
        soundHandler.setSoundsVolume(sound_volume_slider.value);
        soundHandler.setMusicVolume(music_volume_slider.value);
        soundHandler.playMusic(soundHandler.menuMusic);
		System.out.println("init MenuScreen");
	}
	
	public void show() {
		batch = new SpriteBatch();
	}

	public void render(float delta) {
        if (!settingsMenu)
            processMainMenu(delta);
        else
            processSettingsMenu();
        renderMenu();
	}

    private void processMainMenu(float delta) {
        backGroundRotation += delta * 2;
        // If screen not changing already
        if (nextScreen == null) {
            // Player vs player button
            if (start_pvp_button.isPressed()) {
                //game.setScreen(new GameScreen(game, balls_slider.value, 0));
                nextScreen = new GameScreen(game, balls_slider.value, 0, game.soundHandler);
            }
            // Player vs AI button
            if (start_pvc_button.isPressed()) {
                //game.setScreen(new GameScreen(game, balls_slider.value, ai_mode_slider.value));
                nextScreen = new GameScreen(game, balls_slider.value, ai_mode_slider.value, game.soundHandler);
            }
            // Settings button
            if (other2_button.isPressed()) {
                processMenuSwitch();
            }
        }
        // Between screens event handling
        if (nextScreen != null) {
            if (nextScreenDark > 1) {
                dispose();
                game.setScreen(nextScreen);
            }
            nextScreenDark += delta * 2;
        }
    }

    private void processSettingsMenu() {
        // Settings
        if (other2_button.isPressed()) {
            saveConfiguration();
            processMenuSwitch();
        }
        // Sliders processing
        balls_slider.isPressed();
        ai_mode_slider.isPressed();
        sound_volume_slider.isPressed();
        music_volume_slider.isPressed();
        if (sound_volume_slider.update)
            soundHandler.setSoundsVolume(sound_volume_slider.value);
        if (music_volume_slider.update)
            soundHandler.setMusicVolume(music_volume_slider.value);
    }

    private void processMenuSwitch() {
        settingsMenu = !settingsMenu;
        if (!settingsMenu) {
            int x = w / 4;
            int y = (int) (h / 1.7);
            other2_button.setPos(x * 2, y / 3);
        }
        else
            other2_button.setPos(w / 2, h * 6 / 7);
    }

    // Creating picture
    private void renderMenu() {
        Gdx.gl.glClearColor(.15f, .05f, .05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setColor(MathUtils.sin(backGroundRotation/2)/2 + 0.5f, 1f, 1f, 1f);
        // Common drawing
        batch.draw(backGround, w / 2 - backGround.getWidth()/2, h / 2 - backGround.getHeight()/2,
                backGround.getWidth()/2, backGround.getHeight()/2,
                backGround.getWidth(), backGround.getHeight(),
                1, 1, backGroundRotation, 0, 0, backGround.getWidth(), backGround.getHeight(),
                false, false);
        batch.setColor(1f, 1f, 1f, 1f);
        // Particular drawing
        if (!settingsMenu)
            drawMainMenu();
        else
            drawSettingsMenu();
        batch.end();
    }

    private void drawMainMenu() {
        // Buttons
        start_pvp_button.draw(batch);
        start_pvc_button.draw(batch);
        other2_button.draw(batch);
        title_button.draw(batch);
        // Drawing pre-game screen scene
        if (nextScreen != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, nextScreenDark);
            shapeRenderer.rect(0, 0, w, h);
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }

    private void drawSettingsMenu() {
        // Buttons
        other2_button.draw(batch);
        // Sliders
        balls_slider.draw(batch);
        ai_mode_slider.draw(batch);
        sound_volume_slider.draw(batch);
        music_volume_slider.draw(batch);
    }

    // Load/save configurations
    public void loadConfiguration() {
        int settings[];
        try {
            File file = new File(Gdx.files.getLocalStoragePath() + "config/settings.pon");
            if (file.exists()) {
                // Opening streams
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                settings = (int[])objectInputStream.readObject();
                // If config file not corresponding our demands - considering it's an old version file and deleting it
                if (settings.length != 4) {
                    deleteConfigFile("config/settings.pon");
                    return;
                }
                // Closing streams
                fileInputStream.close();
                objectInputStream.close();
                // Setting values
                balls_slider.setValue(settings[ball_sliderNum]);
                ai_mode_slider.setValue(settings[ai_mode_sliderNum]);
                sound_volume_slider.setValue(settings[sound_volume_sliderNum]);
                music_volume_slider.setValue(settings[music_volume_sliderNum]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveConfiguration() {
        int settings[] = {balls_slider.value, ai_mode_slider.value,
                sound_volume_slider.value, music_volume_slider.value};
        try {
            File dir = new File(Gdx.files.getLocalStoragePath() + "config");
            if (!dir.exists())
                // Creating directory if it doesn't exist
                dir.mkdirs();
            // Opening streams
            File file = new File(Gdx.files.getLocalStoragePath() + "config/settings.pon");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(settings);
            // Closing streams
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // But I guess without special rights app couldn't delete anything
    private void deleteConfigFile(String path) {
        FileHandle fileHandle = Gdx.files.getFileHandle(path, Files.FileType.Local);
        if (fileHandle.delete())
            Gdx.app.log("Deleting", "Config file was deleted");
        else
            Gdx.app.log("Deleting", "Couldn't delete config file");
    }

    public void dispose() {
        game.soundHandler.menuMusic.stop();
        disposeTextures();
    }

    private void disposeTextures() {
        backGround.dispose();
        start_pvc_button.disposeTexture();
        start_pvp_button.disposeTexture();
        other2_button.disposeTexture();
        ai_mode_slider.disposeTextures();
        balls_slider.disposeTextures();
    }
}
