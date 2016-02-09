package com.phuda.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.phuda.pong.Units.Ball;
import com.phuda.pong.Units.Board;
import com.phuda.pong.Units.Bonus;

public class FieldRenderer {
    final int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    final int boardRedTexture = 0, boardBlueTexture = 1, ballTexture = 2,
            bonusTimeTexture = 3, bonusSplitterTexture = 4, bonusControllerTexture = 5,
            backGround = 6, winnerTexture = 7;
    Texture textures[];
    float backGroundRotation;
    float previousScreenDark = 1f;
    Field field;
    // Score variables
    float scoreShift;
    BitmapFont score_font;

    FieldRenderer(Field field) {
        // Initialize some stuff
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        // Textures initialisation
        textures = new Texture[8];
        String images_path = "images/";
        // Background
        textures[backGround] = new Texture(Gdx.files.internal(images_path + "background.png"));
        textures[backGround].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        // Units
        textures[boardRedTexture] = new Texture(Gdx.files.internal(images_path + "board_red.png"));
        textures[boardBlueTexture] = new Texture(Gdx.files.internal(images_path + "board_blue.png"));
        textures[ballTexture] = new Texture(Gdx.files.internal(images_path + "particle.png"));
        textures[ballTexture].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures[bonusTimeTexture] = new Texture(Gdx.files.internal(images_path + "bonus_time.png"));
        textures[bonusTimeTexture].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures[bonusSplitterTexture] = new Texture(Gdx.files.internal(images_path + "bonus_splitter.png"));
        textures[bonusSplitterTexture].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures[bonusControllerTexture] = new Texture(Gdx.files.internal(images_path + "bonus_controller.png"));
        textures[bonusControllerTexture].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        // Inscriptions
        textures[winnerTexture] = new Texture(Gdx.files.internal(images_path + "winner.png"));
        textures[winnerTexture].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        // Physical field
        this.field = field;
        // Fonts
        score_font = new BitmapFont();
        score_font.setColor(Color.WHITE);
    }

    public void render(float delta) {
        // Draw back-ground
        backGroundRotation += delta * 2;
        batch.begin();
        batch.setColor(MathUtils.sin(backGroundRotation)/2 + 0.5f, 1f, 1f, 0.7f);
        batch.draw(textures[backGround], w/2 - textures[backGround].getWidth(), h/2 - textures[backGround].getHeight(),
                textures[backGround].getWidth(), textures[backGround].getHeight(),
                textures[backGround].getWidth()*2, textures[backGround].getHeight()*2,
                1, 1, -backGroundRotation, 0, 0, textures[backGround].getWidth(), textures[backGround].getHeight(),
                false, true);
        batch.draw(textures[backGround], w/2 - textures[backGround].getWidth()/2, h/2 - textures[backGround].getHeight()/2,
                textures[backGround].getWidth()/2, textures[backGround].getHeight()/2,
                textures[backGround].getWidth(), textures[backGround].getHeight(),
                1, 1, backGroundRotation, 0, 0, textures[backGround].getWidth(), textures[backGround].getHeight(),
                false, false);
        batch.setColor(1f, 1f, 1f, 1f); // reset colors for next graphics
        batch.end();
        // Score bar
        scoreShift += (field.scoreShift - scoreShift) * 0.06;
        float diff = field.scoreShift - scoreShift;
        // Clear screen color
        Gdx.gl.glClearColor(0.2f - diff,
                (MathUtils.sin(backGroundRotation/4)) * 0.25f,
                0.2f + diff, 1f);
        // Previous screen legacy
        if (previousScreenDark > 0)
            previousScreenDark -= delta;
        // Scores and ability bars
        shapeRenderer.begin(ShapeType.Filled);
        // score difference
        shapeRenderer.setColor(1.0f, 1.0f, 0.5f, 1f);
        shapeRenderer.rect(w / 2 + scoreShift, h/4, diff, h/2);
        // red player 1
        shapeRenderer.setColor(0.5f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(w, h/4, -w/2 + scoreShift, h/2);
        // blue player 2
        shapeRenderer.setColor(0.1f, 0.1f, 0.5f, 1f);
        shapeRenderer.rect(0, h/4 , w/2 + scoreShift, h/2);
        // abilities bars
        // time ability
        shapeRenderer.setColor(0.1f, 0.2f, 0.7f, 1f); // blue
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[0].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[0].timer / 10);
        // splitter ability
        shapeRenderer.setColor(0.1f, 0.7f, 0.2f, 1f); // green
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[1].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[1].timer / 10);
        // controller ability
        shapeRenderer.setColor(0.7f, 0.5f, 0.1f, 1f); // orange
        shapeRenderer.rect(0, h-h/4, w, h/8 * field.player1Board.abilities[2].timer / 10);
        shapeRenderer.rect(0, h/4 , w, -h/8 * field.player2Board.abilities[2].timer / 10);
        shapeRenderer.setColor(0.7f, 0.5f, 0.1f, 1f); // orange
        for (Ball ball : field.balls){
            if (ball.states[3].isActive && ball.lastTouchedBoard.abilities[2].isActive){
                shapeRenderer.rectLine(ball.bounds.x, ball.bounds.y,
                        ball.lastTouchedBoard.bounds.x + ball.lastTouchedBoard.bounds.getWidth()/2,
                        ball.lastTouchedBoard.bounds.y, ball.bounds.radius);
            }
        }
        // toggle alpha blending. end function uses this to draw transparency
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_ONE, Gdx.gl.GL_DST_COLOR);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


        batch.begin();
        //Scores
        //score_font.draw(batch, Integer.toString(field.player1Board.score), 40, h/3);
        //score_font.draw(batch, Integer.toString(field.player2Board.score), w - 40, h/3);
        // Boards
        Board p1 = field.player1Board, p2 = field.player2Board;
        batch.draw(textures[boardRedTexture], p1.bounds.x, p1.bounds.y,
                p1.bounds.width, p1.bounds.height);
        batch.draw(textures[boardBlueTexture], p2.bounds.x, p2.bounds.y,
                p2.bounds.width, p2.bounds.height);


        // balls
        for (Ball ball : field.balls){
            if (ball != null){
                float color[] = {1f, 1f, 1f};
                // if ball is controlled
                if (ball.states[3].isActive){
                    color[1] -= 0.3f;
                    color[2] -= 0.7f;
                }
                // if ball is slowed
                if (ball.states[1].isActive){
                    color[0] -= 0.7f;
                    color[1] -= 0.7f;
                }
                // Trail rendering
                batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE);
                float r = ball.bounds.radius;
                float alpha = 0.2f;
                for (Vector2 vec : ball.positionsHistory) {
                    batch.setColor(color[0], color[1], color[2], alpha);
                    float size = alpha * 2;
                    batch.draw(textures[ballTexture], vec.x - r * size, vec.y - r * size,
                            r * 2 * size, r * 2 * size);
                    alpha += 0.02f;
                }
                // Reset alpha
                batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(1f, 1f, 1f, 1f);
                batch.draw(textures[ballTexture], ball.bounds.x - r, ball.bounds.y - r, r * 2, r * 2);
            }
        }

        // bonuses
        for (Bonus bonus : field.bonuses){
            if (bonus != null){
                Texture tex;
                if (bonus.name.equals("timeSlower")) {
                    tex = textures[bonusTimeTexture];
                }
                else if (bonus.name.equals("ballSplitter")) {
                    tex = textures[bonusSplitterTexture];
                }
                else if (bonus.name.equals("controller")) {
                    tex = textures[bonusControllerTexture];
                }
                else {
                    continue; // exception case
                }

                float r = bonus.bounds.radius;
                batch.draw(tex, bonus.bounds.x - r, bonus.bounds.y - r,
                        r, r, r*2, r*2, 1, 1, bonus.rotation, 0, 0,
                        textures[bonusTimeTexture].getWidth(), textures[bonusTimeTexture].getHeight(), false, false);
            }
        }
        // Pause/resume buttons
        if (field.winner.equals("none")){
            if (!field.paused)
                field.pauseButton.draw(batch);
            else {
                field.resumeButton.draw(batch);
                // Menu button
                field.menuButton.draw(batch);
            }
        }
        else { // Some-one wins
            int rotation = field.winner.equals(field.player1Board.name) ? 180 : 0;
            int ypos = field.winner.equals(field.player1Board.name) ? h - h/4 : h/4;
            Texture t = textures[winnerTexture];
            batch.draw(t, w/2 - t.getWidth()/2, ypos - t.getHeight()/2, t.getWidth()/2, t.getHeight()/2,
                    t.getWidth(), t.getHeight(), 1, 1, rotation, 0, 0, t.getWidth(), t.getHeight(), false, false);
            // Menu button
            field.menuButton.draw(batch);
        }
        batch.end();

        if (previousScreenDark != 0) {
            if (previousScreenDark < 0){
                previousScreenDark = 0;
            }
            previousScreenDark -= delta / 2;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, previousScreenDark);
            shapeRenderer.rect(0, 0, w, h);
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }
}