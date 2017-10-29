package org.olumide.adebayo.spaceshooter;

import android.graphics.Bitmap;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import org.olumide.adebayo.spaceshooter.R;

import java.util.ArrayList;

/**
 * Created by Carlitos on 10/16/2017.
 */

public class SpriteHelper {
    //this class will help with getting all the pieces from the spritesheet located
    // in the drawable-nodpi folder
    Bitmap space;   //a reference to the bitmap that has all the images
    Rect[] shipVSprites,shipHSprites,enemy1sprites,enemy2sprites,meteorsprites,boomsprites;
    Rect shipBulletSprite,enemybulletSprite;
    float shipVOriginalHeight,shipVOriginalWidth; //from the sprite
    float shipHOriginalHeight,shipHOriginalWidth;
    float  enemyOriginalHeight,enemyOriginalWidth;
    float meteorOriginalSize,boomOriginalSize;
    float bulletOriginalHeight,bulletOriginalWidth;
    int shipVHeight,shipVWidth,shipHHeight,shipHWidth,enemyWidth,enemyHeight,enemybulletWidth,
            enemybulletHeight,meteorSize,boomSize,shipBulletSize; //resized for the screen

    ArrayList<Rect> enemySprites = new ArrayList<Rect>();

    SpriteHelper(Resources r)
    {
        space = BitmapFactory.decodeResource(r, R.drawable.spritesheet);
        setSpaceValues();
    }
    public void setSpaceValues()
    {
        shipVOriginalHeight = 64;
        shipVOriginalWidth = 29;
        shipVSprites = new Rect[4];
        shipVSprites[0] = new Rect(30,228,30+29,228+64);
        shipVSprites[1] = new Rect(0,228,0+29,228+64);
        shipVSprites[2] = new Rect(204,163,204+29,163+64);
        shipVSprites[3] = new Rect(60,228,60+29,228+64);

        shipHOriginalHeight =29;
        shipHOriginalWidth = 64;
        shipHSprites = new Rect[4];
        shipHSprites[0] = new Rect(105,0,105+64,0+29);
        shipHSprites[1] = new Rect(0,30,0+64,30+29);
        shipHSprites[2] = new Rect(170,0,170+64,0+29);
        shipHSprites[3] = new Rect(40,0,40+64,0+29);

        enemyOriginalHeight = 40;
        enemyOriginalWidth = 40;
        enemy1sprites = new Rect[6];
        enemy1sprites[0] = new Rect(123,71,40+123,40+71);
        enemy1sprites[1] = new Rect(164,71,40+164,40+71);
        enemy1sprites[2] = new Rect(41,112,40+41,40+112);
        enemy1sprites[3] = new Rect(82,112,40+82,40+112);
        enemy1sprites[4] = new Rect(0,112,40+0,40+112);
        enemy1sprites[5] = new Rect(188,30,40+188,40+30);

        enemy2sprites = new Rect[6];
        enemy2sprites[0] = new Rect(65,30,40+65,40+30);
        enemy2sprites[1] = new Rect(82,71,40+82,40+71);
        enemy2sprites[2] = new Rect(106,30,40+106,40+30);
        enemy2sprites[3] = new Rect(147,30,40+147,40+30);
        enemy2sprites[4] = new Rect(0,71,40+0,40+71);
        enemy2sprites[5] = new Rect(41,71,40+41,40+71);

        /* olumide */
        for(Rect r: enemy1sprites){
            enemySprites.add(r);
        }
        for(Rect r: enemy2sprites){
            enemySprites.add(r);
        }


        enemybulletSprite = new Rect(20,0,11+20,11+0);

        meteorOriginalSize = 75;
        meteorsprites = new Rect[4];
        meteorsprites[0] = new Rect(152,304,75+152,75+304);
        meteorsprites[1] = new Rect(0,304,75+0,75+304);
        meteorsprites[2] = new Rect(90,228,75+90,75+228);
        meteorsprites[3] = new Rect(76,304,75+76,75+304);

        boomOriginalSize = 50;
        boomsprites = new Rect[6];
        boomsprites[0] = new Rect(51,163,50+51,50+163);
        boomsprites[1] = new Rect(174,112,50+174,50+112);
        boomsprites[2] = new Rect(123,112,50+123,50+112);
        boomsprites[3] = new Rect(0,163,50+0,50+163);
        boomsprites[4] = new Rect(102,163,50+102,50+163);
        boomsprites[5] = new Rect(153,163,50+153,50+163);

        bulletOriginalHeight = 11;
        bulletOriginalWidth = 11;
        shipBulletSprite = new Rect(0,0,11+0,11+0);


    }
    public void setSizes(int screenWidth,int screenHeight){
        //these sizes can be changed if you dont like them
        int ratio = (int)(screenHeight*.1);
        double val = shipVOriginalHeight/ratio;
        shipVWidth =  (int)(shipVOriginalWidth/val);
        enemyWidth = enemyHeight = (int)(screenWidth*.15);
        enemybulletWidth = enemybulletHeight = (int)(screenWidth*.03);
        meteorSize = (int)(screenWidth*.1);
        boomSize = (int)(screenWidth*.2);
        shipBulletSize = (int)(screenWidth*.03);

        int hratio = (int)(screenWidth*.1);
        double hval = shipHOriginalHeight/ratio;
        shipHHeight = (int)(shipHOriginalHeight/hval);

    }

}