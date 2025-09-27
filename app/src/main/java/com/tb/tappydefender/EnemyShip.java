package com.tb.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;
import java.util.logging.Logger;

public class EnemyShip {

    //region Properties
    /*********************************************************************
     * Properties
     ********************************************************************/
    // object image
    private Bitmap bitmap;

    // hit box for collision detection
    private Rect hitbox;

    private int x, y;
    private int speed = 1;

    // detect ebenues keavubg tge screen
    private int maxX;
    private int minX;

    // spawn enemies within scren bounds
    private int maxY;
    private int minY;

    // Logger
    private Logger LOGGER;
    //endregion

    //region Constructor
    /*********************************************************************
     * Constructor
     * ********************************************************************/
    public EnemyShip(Context context, int screenX, int screenY, int enemyId){
        Random generator = new Random();
        int whichBitmap = generator.nextInt(3);

        switch (whichBitmap){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
                break;

            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
                break;
        }

        scaleBitmap(screenX);

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        speed = generator.nextInt(6)+ 10;

        x = screenX;
        y = generator.nextInt(maxX) - bitmap.getHeight();

        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        LOGGER =  Logger.getLogger("EnemyShip #:"  +enemyId);
    }
    //endregion

    //region PublicMethods
    /*********************************************************************
     * Public Methods
     ********************************************************************/

    public void update(int playerSpeed){
        // move to the left
        x -= playerSpeed;
        x -= speed;

        boolean isOutOfBounds = x < minX - bitmap.getWidth();
        LOGGER.info("{minX - bitmap.getWidth = " +(minX - bitmap.getWidth()));
        LOGGER.info("{x < minX-bitmap.getWidth()} = " +isOutOfBounds);

        if(isOutOfBounds){
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        // refresh hit box location
        hitbox.left = x;
        hitbox.top = y;
        hitbox.right = x + bitmap.getWidth();
        hitbox.bottom = y + bitmap.getHeight();

        LOGGER.info("{x,y} = "+ x +", "+ y + " speed: " + speed );
    }

    // getters and setters
    public Bitmap getBitmap(){
        return bitmap;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Rect getHitbox(){
        return hitbox;
    }

    public void setX(int x){
        this.x = x;
    }
    //endregion

    //region Internals
    /*********************************************************************
     * Internals
     ********************************************************************/
    private void scaleBitmap(int x){
        if(x < 1000){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 3,
                    bitmap.getHeight() / 3,
                    false);
        }else if(x < 1200){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 2,
                    bitmap.getHeight() / 2,
                    false);
        }
    }
    //endregion
}