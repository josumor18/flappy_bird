package cr.ac.itcr.andreifuentes.flappybirdclase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
	ShapeRenderer shapeRenderer;

	Texture levelTitle;
    Texture easy;
    Texture medium;
    Texture hard;
    Sound wing, point, die;

	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture[] birds, birdsC;
	Texture gameOver;
	int birdState;
	float gap;
	float birdY;
	float velocity;
	float gravity;
	int numberOfPipes = 4;
	float pipeX[] = new float[numberOfPipes];
	float pipeYOffset[] = new float[numberOfPipes];
	float distance;
	float pipeVelocity = 5;
	Random random;
	float maxLine;
	float minLine;
	int score;
	int pipeActivo;
	BitmapFont font;
	int game_state;
	int birdSize;
	boolean fTouch = false;
	boolean dieSound = true;

	Circle birdCircle;
	Rectangle[] topPipes;
	Rectangle[] bottomPipes;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		background = new Texture("bg.png");
		levelTitle = new Texture("level.png");
        easy = new Texture("easy.png");
        medium = new Texture("medium.png");
        hard= new Texture("hard.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
        birdsC = new Texture[2];
        birdsC[0] = new Texture("bird_v.png");
        birdsC[1] = new Texture("bird_v2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameOverOriginal.png");
        wing = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
        point = Gdx.audio.newSound(Gdx.files.internal("sfx_point.ogg"));
        die = Gdx.audio.newSound(Gdx.files.internal("sfx_die.ogg"));

		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];

		birdState = 0;
		game_state = 0;
		gap = 500;
		velocity = 0;
		gravity = 0.5f;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		score = 0;
		pipeActivo = 0;
		birdSize = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[birdState].getHeight()/2;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() + Gdx.graphics.getWidth() + distance*i;

			// inicializamos cada uno de los Shapes
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (game_state == -1){

        }
        // no iniciado
		else if (game_state == 0){

            batch.draw(levelTitle, 150, 900);
            batch.draw(easy, 120, (Gdx.graphics.getHeight() / 3)*2);
            batch.draw(medium, 120, (Gdx.graphics.getHeight() / 3));
            batch.draw(hard, 120, 0);

			if (Gdx.input.justTouched()){

                if (Gdx.input.getY() > (Gdx.graphics.getHeight() / 3) * 2) {
                    gravity = 1.0f;
                    gap = 250;
                    game_state = 0;
                    fTouch = true;
                } else if (Gdx.input.getY() < (Gdx.graphics.getHeight() / 3)) {
                    gravity = 0.2f;
                    gap = 600;
                    game_state = 0;
                    fTouch = true;
                } else {
                    gravity = 0.5f;
                    gap = 500;
                    game_state = 0;
                    fTouch = true;
                }


                if(fTouch){
                    game_state = 1;
                }

			}
		}
		// jugando
		else if (game_state == 1){
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				score++;
				point.play();

				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}


			birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[birdState].getHeight()/2, birds[birdState].getWidth()/2);

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.MAGENTA);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//

			// Posicionamiento de los pipes
			for (int i = 0; i<numberOfPipes; i++) {

				if (pipeX[i] < -topTube.getWidth()){
					pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
					pipeX[i] += distance*(numberOfPipes);
				}
				else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topTube,
						pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				batch.draw(bottomTube,
						pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				bottomPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

//				shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube.getWidth(),
//						bottomTube.getHeight());

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");
					game_state = 2;
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");
					game_state = 2;
				}
			}

			if (Gdx.input.justTouched()){
				velocity = velocity - 30;
				wing.play();
				//arriba = true;
			}

			birdState = birdState == 0 ? 1 : 0;


			velocity = velocity + gravity;


			if (birdY < 0){
				game_state = 2;
			}
			else {
				birdY = birdY - velocity;
			}

//			shapeRenderer.end();


		}
		// game over
		else if (game_state == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if(dieSound){
                die.play();
                dieSound = false;
            }
			if (Gdx.input.justTouched()){
				game_state = 0;
				score = 0;
				pipeActivo = 0;
				velocity = 0;
				dieSound = true;
				startGame();
			}
		}

		if(velocity <= 0){
            batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2,  birdY,  birds[birdState].getWidth() + birdSize, birds[birdState].getHeight() + birdSize);
        }else{
            batch.draw(birdsC[birdState], Gdx.graphics.getWidth() / 2 - birdsC[birdState].getWidth()/2,  birdY,  birdsC[birdState].getWidth() + birdSize, birdsC[birdState].getHeight() + birdSize);
        }

		font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/8, Gdx.graphics.getHeight()*9/10);
		//birdSize += 1;
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		wing.dispose();
		point.dispose();
		die.dispose();
	}
}
