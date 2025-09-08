package com.tb.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.logging.Logger;

public class PlayerShip {

    private Bitmap bitmap;

    // hit box for collision detection
    private Rect hitbox;

    private int x, y;

    private int speed = 0;
    private boolean boosting;

    private int shieldStrenght;

    private final int GRAVITY = -8;

    // Stop ship leaving the screen
    private int maxY;
    private int minY;

    // Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    private Logger LOGGER = Logger.getLogger("PlayerShip");

    public PlayerShip(Context context, int screenX, int screenY){
        x = 50;
        y = 50;
        boosting = false;
        shieldStrenght = 2;

        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ship);

        maxY = screenY - bitmap.getHeight();
        minY = 0;

        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(){
        if(boosting){
            speed += 2;
        }else{
            speed -=5;
        }

        if(speed > MAX_SPEED){
            speed = MAX_SPEED;
        }

        if(speed < MIN_SPEED){
            speed = MIN_SPEED;
        }

        // move the ship up or down
        y -= speed + GRAVITY;

        if(y < minY){
            y = minY;
        }

        if(y > maxY){
            y = maxY;
        }

        // refresh hit box location
        hitbox.left = x;
        hitbox.top = y;
        hitbox.right = x + bitmap.getWidth();
        hitbox.bottom = y + bitmap.getHeight();


        LOGGER.info("{x,y} = "+ x +", "+ y + " speed: " + speed );
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting(){
        boosting = false;
    }

    public void reduceShieldStrength(){
        shieldStrenght --;
    }

    //Getters
    public Bitmap getBitmap(){
        return bitmap;
    }

    public int getSpeed(){
        return speed;
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

    public int getShieldStrenght(){
        return shieldStrenght;
    }
}