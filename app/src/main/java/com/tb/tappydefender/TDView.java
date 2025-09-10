package com.tb.tappydefender;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tb.tappydefender.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TDView extends SurfaceView implements Runnable {

    //region Properties
    /*********************************************************************
     * Properties
     ********************************************************************/

    // For the FX
    private SoundPool soundPool;
    int startSoundID = -1;
    int bumpSoundID = -1;
    int destroyedSoundID = -1;
    int winSoundID = -1;

    volatile boolean playing;
    Thread gameThread = null;

    // Game objects
    private PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;

    // Make some random space dust
    private ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    // HUD objects
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private int screenX;
    private int screenY;

    private boolean gameEnded;

    private Context context;

    private long lastTouchUpTime = 0;
    private  boolean isDoubleClick = false;

    private Logger LOGGER = Logger.getLogger("TDView");
    //endregion

    //region Lifecycle
    /*********************************************************************
     * Lifecycle
     ********************************************************************/
    public TDView(Context ct, int x, int y){
        super(ct);

        this.context = ct;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("start.ogg");
            startSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            winSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bumpSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyedSoundID = soundPool.load(descriptor, 0);
        }catch (IOException e){
            LOGGER.info("error, failed to load sound files");
        }

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;

        startGame();
    }
    //endregion

    //region Overrides
    /*********************************************************************
     * Overrides
     ********************************************************************/
    @Override
    public void run() {
        while(playing){
            update();
            draw();
            control();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;

            case MotionEvent.ACTION_DOWN:
                long currentTime = System.currentTimeMillis();
                if(!isDoubleClick && currentTime - lastTouchUpTime < Utilities.DOUBLE_CLICK_TIME_DELTA){
                    // user is double tapping
                    isDoubleClick = true;
                    lastTouchUpTime = currentTime;
                    if(gameEnded){
                        player.stopBoosting();
                        startGame();
                    }else{
                        player.setBoosting();
                    }
                }
                else {
                    lastTouchUpTime = currentTime;
                    isDoubleClick = false;
                    if(!gameEnded){
                        player.setBoosting();
                    }
                }
                break;
        }

        return true;
    }
    //endregion

    //region Internals
    /*********************************************************************
     * Internals
     ********************************************************************/
    private void startGame() {
        LOGGER.info("Game just started!!");

        soundPool.play(startSoundID, 1,1,0,0,1);

        // Initialize our player ship
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY, 1);
        enemy2 = new EnemyShip(context, screenX, screenY, 2);
        enemy3 = new EnemyShip(context, screenX, screenY, 3);

        int numSpecs = 60;
        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        // reset time and distance
        distanceRemaining = 10000; // 10 km
        timeTaken = 0;

        // get start time
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }

    private void update(){
        // collision detection on new positions
        // before move because we are testing last frames
        boolean hitDetected = false;
        if(Rect.intersects(player.getHitbox(), enemy1.getHitbox())){
            hitDetected = true;
            enemy1.setX(-290);
        }

        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())){
            hitDetected = true;
            enemy2.setX(-290);
        }

        if(Rect.intersects(player.getHitbox(), enemy3.getHitbox())){
            hitDetected = true;
            enemy3.setX(-290);
        }

        if(hitDetected && !gameEnded){
            LOGGER.info("PlayerShip was hitten");
            soundPool.play(bumpSoundID, 1,1,0,0,1);

            player.reduceShieldStrength();
            if(player.getShieldStrenght() < 0){
                // game over so do something
                LOGGER.info("GAME OVER!!");

                soundPool.play(destroyedSoundID, 1,1,0,0,1);
                gameEnded = true;
            }
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        for(SpaceDust sd : dustList){
            sd.update(player.getSpeed());
        }

        if(!gameEnded){
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        // completed the game
        if(distanceRemaining < 0){
            LOGGER.info("You won!!");

            soundPool.play(winSoundID, 1,1,0,0,1);

            // check for new fastest time
            if(timeTaken < fastestTime){
                LOGGER.info("New fastestTime: "+ fastestTime);
                fastestTime = timeTaken;
            }

            distanceRemaining = 0;
            gameEnded = true;
        }
    }

    private void draw(){
        if(ourHolder.getSurface().isValid()){
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            if(Utilities.isDebuggerAttached()) {

                // for debugging
                paint.setColor(Color.RED);

                // draw hit boxes
                canvas.drawRect(
                        player.getHitbox().left,
                        player.getHitbox().top,
                        player.getHitbox().right,
                        player.getHitbox().bottom,
                        paint);

                canvas.drawRect(
                        enemy1.getHitbox().left,
                        enemy1.getHitbox().top,
                        enemy1.getHitbox().right,
                        enemy1.getHitbox().bottom,
                        paint);

                canvas.drawRect(
                        enemy2.getHitbox().left,
                        enemy2.getHitbox().top,
                        enemy2.getHitbox().right,
                        enemy2.getHitbox().bottom,
                        paint);

                canvas.drawRect(
                        enemy3.getHitbox().left,
                        enemy3.getHitbox().top,
                        enemy3.getHitbox().right,
                        enemy3.getHitbox().bottom,
                        paint);
            }

            // white specs of dust
            paint.setColor(Color.argb(255,255,255,255));

            // draw the dust from our arrayList
            for(SpaceDust sd : dustList){
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // draw spaceships and enemies
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(),
                    paint);

            // draw the hud

            if(!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(35);
                canvas.drawText("Fastest: " + fastestTime + "s", 10, 40, paint);
                canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 40, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 40, paint);
                canvas.drawText("Shield: " + player.getShieldStrenght(), 10, screenY - 40, paint);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 40, paint);
            }else{
                // show pause screen
                paint.setTextSize(90);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 110, paint);
                paint.setTextSize(35);
                canvas.drawText("Fastets: " + fastestTime + "s", screenX / 2, 170, paint);
                canvas.drawText("Time: " +  timeTaken + "s", screenX / 2, 210, paint);
                canvas.drawText("Distance remaining: " + distanceRemaining / 1000 + "KM", screenX / 2, 250, paint);
                paint.setTextSize(90);
                canvas.drawText("Double Tap to replay!", screenX / 2, 360, paint);
            }

            // unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control(){
        try{
            Thread.sleep(17); // (1000ms / 60 fps) =  ~17
        }catch (InterruptedException e){

        }
    }
    //endregion

    //region PublicMethods
    /*********************************************************************
     * Public Methods
     ********************************************************************/
    public void pause(){
        playing = false;
        try{
            gameThread.join();
        }catch (InterruptedException e){

        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public boolean getGameEnded(){
        return gameEnded;
    }
    //endregion
}
