package com.arnoldiii.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;

    //array of images bird_wings up and down for animation
    Texture[] bird;

    Texture topTube;
    Texture bottomTube;
    Texture gameOver;
    int spaceBetweenTubes = 550;

    Random random;

    int birdStateFlag = 0;
    float flyHeight;
    float fallingSpeed = 0;

    //gameStateFlag = 0 - game is paused,gameStateFlag = 1 - game is started
    int gameStateFlag = 0;

    //variable that is responsible for the speed of movement for tubeX
    int tubeSpeed = 7;

    //amount of tubes that repeated
    int tubesNumber = 5;

    //variable for the movement of tubes along the x coordinate
    float tubeX[] = new float[tubesNumber];

    //random tube shifting
    float tubeShift[] = new float[tubesNumber];
    float distanceBetweenTubes;

    //a circle that represents the outline of a bird
    Circle birdCircle;

    //a rectangles that represents the outline of a top and bottom tubes
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;


    //ShapeRenderer shapeRenderer;

    int gameScore = 0;
    int passedTubeIndex = 0;
    //variable of score display
    BitmapFont scoreFont;


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.png");
        //shapeRenderer = new ShapeRenderer();

        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[tubesNumber];
        bottomTubeRectangles = new Rectangle[tubesNumber];

        bird = new Texture[2];
        bird[0] = new Texture("bird_wings_up.png");
        bird[1] = new Texture("bird_wings_down.png");
        topTube = new Texture("top_tube.png");
        bottomTube = new Texture("bottom_tube.png");
        gameOver = new Texture("game_over.png");


        random = new Random();
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(10);

        //distance between tubes is a half of display
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        initGame();

    }

    //init method is restart game after game over
    public void initGame() {
        //initial fly height of bird(center of display)
        flyHeight = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;

        //loop for randomly generating tubes witch moves from right side to left
        for (int i = 0; i < tubesNumber; i++) {
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2
                    + Gdx.graphics.getWidth() + i * distanceBetweenTubes * 1.1f;
            tubeShift[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()
                    - spaceBetweenTubes - 700);
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {
        //launching our images
        batch.begin();
        //draw our image from x0,y0 and full width and height
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //if user touched the screen,the code below and the game will start.
        if (gameStateFlag == 1) {

            Gdx.app.log("Score", String.valueOf(gameScore));

            /*logic of receiving score,if the bird flies halfway through the tube,
             one point will be added */
            if (tubeX[passedTubeIndex] < Gdx.graphics.getWidth() / 2) {
                gameScore++;

                if (passedTubeIndex < tubesNumber - 1) {
                    passedTubeIndex++;
                } else {
                    passedTubeIndex = 0;
                }
            }

			/*if user touched screen again,the falling speed will be negative and the bird's flight
			height will increase and bird fall again
			 */
            if (Gdx.input.justTouched()) {
                fallingSpeed = -20;

            }

            for (int i = 0; i < tubesNumber; i++) {

                //if the tubes go beyond the screen, they are created again
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] = tubesNumber * distanceBetweenTubes;
                } else {
                    tubeX[i] -= tubeSpeed;
                }

                //draw tubes and randoms position for tubes
                batch.draw(topTube, tubeX[i],
                        Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i]);
                batch.draw(bottomTube, tubeX[i],
                        Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() +
                                tubeShift[i]);

                //create a rectangles around tubes,if the bird touches the tube, the user will lose
                topTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i],
                        topTube.getWidth(), topTube.getHeight());

                bottomTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() +
                                tubeShift[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

            //if bird fall down,game will stop
            if (flyHeight > 0) {
                fallingSpeed++;//an increasing this variable indicates a decreasing bird flyheight
                flyHeight -= fallingSpeed;//
            } else {
                gameStateFlag = 2;
            }

            /*This logic implements the game in pause mode (gameStateFlag = 0)
            ,if the user touches the screen, the game will start(gameStateFlag = 1) and if he loses
            ,then a picture will be displayed on the screen and gameStateFlag = 2*/
        } else if (gameStateFlag == 0) {
            if (Gdx.input.justTouched()) {
                Gdx.app.log("tap", "Touched");
                gameStateFlag = 1;
            }
        } else if (gameStateFlag == 2) {
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

            if (Gdx.input.justTouched()) {
                Gdx.app.log("tap", "Touched");
                gameStateFlag = 1;
                initGame();
                gameScore = 0;
                passedTubeIndex = 0;
                fallingSpeed = 0;
            }
        }

        //for bird wings changing
        if (birdStateFlag == 0) {
            birdStateFlag = 1;
        } else {
            birdStateFlag = 0;
        }


        //to display the bird in the middle of the screen
        batch.draw(bird[birdStateFlag], Gdx.graphics.getWidth() / 2 - bird[birdStateFlag].getWidth() / 2,
                flyHeight);

        //logic for score displaying
        scoreFont.draw(batch, String.valueOf(gameScore), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 1000);

        batch.end();

        //create a bird outline that will respond to touch with tubes
        birdCircle.set(Gdx.graphics.getWidth() / 2, flyHeight + bird[birdStateFlag].getHeight() / 2,
                bird[birdStateFlag].getWidth() / 2);

        /*The code below is for drawing a circle around the bird, for testing
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);*/

        for (int i = 0; i < tubesNumber; i++) {
        /* The code below is for drawing a rectangles around the tubes, for testing
		shapeRenderer.rect(tubeX[i],
				Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + tubeShift[i],
				topTube.getWidth(),topTube.getHeight());
		    shapeRenderer.rect(tubeX[i],
				Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() +
						tubeShift[i],bottomTube.getWidth(),bottomTube.getHeight());*/

            //creates logic when birds and tubes intersect,and then game stop
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i])
                    || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                Gdx.app.log("intersected", "Failed");
                gameStateFlag = 2;
            }
        }

//		shapeRenderer.end();
    }


}
