package com.maximilian.flappybirdclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture backgound;
    Texture[] bird;
    int birdStateFlag = 0;
    float flyHeight;
    float fallingSpeed = 0;
    int gameStateFlag = 0;

    Texture bottomTube;
    Texture topTube;
    int spaceBetweenTubes = 500;
    Random random;
    int tubeSpeed = 5;
    int tubesNumber = 5;
    float tubeX[] = new float[tubesNumber];
    float tubeShift[] = new float[tubesNumber];
    float distanceBetweenTubes;

    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    //ShapeRenderer shapeRenderer;

    int gameScore;
    int passedTubeIndex = 0;
    BitmapFont scoreFront;

    Texture gameOver;

    @Override
    public void create() {
        batch = new SpriteBatch();
        backgound = new Texture("background.png");
        gameOver = new Texture("game_over.png");
        //shapeRenderer = new ShapeRenderer();
        scoreFront = new BitmapFont();
        scoreFront.setColor(Color.CYAN);
        scoreFront.getData().setScale(10);

        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[tubesNumber];
        bottomTubeRectangles = new Rectangle[tubesNumber];

        bird = new Texture[2];
        bird[0] = new Texture("bird_wings_up.png");
        bird[1] = new Texture("bird_wings_down.png");
        bottomTube = new Texture("bottom_tube.png");
        topTube = new Texture("top_tube.png");
        random = new Random();
        distanceBetweenTubes = (Gdx.graphics.getWidth() / 2) *1.5f;

        initGame();
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(backgound, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameStateFlag == 1) {

            Gdx.app.log("GameScore", String.valueOf(gameScore));
            if (tubeX[passedTubeIndex] < Gdx.graphics.getWidth()/2) {
                gameScore++;
                if (passedTubeIndex < tubesNumber -1) {
                    passedTubeIndex++;
                } else {
                    passedTubeIndex = 0;
                }
            }

            if (Gdx.input.justTouched()) {
                fallingSpeed = -20;
            }
            for (int i = 0; i < tubesNumber; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] = tubesNumber * distanceBetweenTubes;
                } else {
                    tubeX[i] -= tubeSpeed;
                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() + tubeShift[i]);
                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() + tubeShift[i], bottomTube.getWidth(), bottomTube.getHeight());
            }
            if (flyHeight > 0 ) {
                fallingSpeed++;
                flyHeight -= fallingSpeed;
            } else {
                gameStateFlag = 2;
            }
         } else if (gameStateFlag == 0){
            if (Gdx.input.justTouched()) {
                gameStateFlag = 1;
            }
        } else if (gameStateFlag ==2) {
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth()/2,Gdx.graphics.getHeight() / 2 - gameOver.getHeight()/2 );
            if (Gdx.input.justTouched()) {
                gameStateFlag = 1;
                initGame();
            }
        }

        if (birdStateFlag == 0) {
            birdStateFlag = 1;
        } else {
            birdStateFlag = 0;
        }
        Texture birdState = bird[birdStateFlag];
        batch.draw(birdState, Gdx.graphics.getWidth() / 2 - birdState.getWidth() / 2, flyHeight);
        scoreFront.draw(batch,String.valueOf(gameScore),100,200);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, flyHeight + bird[birdStateFlag].getHeight() / 2, bird[birdStateFlag].getWidth() / 2);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.CYAN);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < tubesNumber; i++) {
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() + tubeShift[i], bottomTube.getWidth(), bottomTube.getHeight());
            if (Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i])) {
                Gdx.app.log("Intersection", "Fuck");
                gameStateFlag = 2;
            }
        }
       // shapeRenderer.end();
    }

    private void initGame() {
        gameScore = 0;
        passedTubeIndex = 0;
        fallingSpeed = 0;
        flyHeight = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;
        for (int i = 0; i < tubesNumber; i++) {
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() +i * distanceBetweenTubes;
            tubeShift[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - spaceBetweenTubes - 200);
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }

    }
    @Override
    public void dispose() {
    }
}
