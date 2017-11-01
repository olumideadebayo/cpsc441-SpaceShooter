package org.olumide.adebayo.spaceshooter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;


/**
 * Created by oadebayo on 10/28/17.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback , View.OnTouchListener{

    private Context ctx;
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    int orientation = 1;//default
    Paint paint =new Paint();
    Paint textPaint = new Paint();

    int bulletHop = 10;
    SpriteHelper spriteHelper= null;

    Resources res = getResources();
    int sWidth =0;
    int sHeight = 0;
    boolean setupDone = false;

    float yTouch, xTouch;
    MediaPlayer mp1, mp2;
    //number of enemies
    int enemyCount = 10;
    Point boom,shipBullet;
    Point ship=new Point(0,0);
    ArrayList<Point> enemies = new ArrayList<Point>();
    ArrayList<Point> enemyBullets = new ArrayList<Point>();

    ArrayList<Rect> enemySprite = new ArrayList<Rect>();

    int life = 5;
    Thread gameThread ;
    long enemyMoveTime = 0;
    //15 frames per seconds
  //  float skipTime =1000.0f/8.0f; //setting 30fps
    float skipTime = 1000.0f/1.0f;

    long lastUpdate;
    float dt;


    public MySurfaceView(Context ctx,int orientation, SpriteHelper sp){

        super(ctx);
        this.ctx = ctx;
        this.orientation=orientation;

        textPaint.setColor(Color.WHITE);
     //   textPaint.setUnderlineText(true);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(50.0f);

        Log.d("Olu","orientation is "+this.orientation);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        spriteHelper=sp;
        gameThread = new Thread(runner);

    }

    public int getLife(){
        return life;
    }
    public int getEnemyCount(){
        return enemyCount;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Log.d("Olu","surface created");

        if( orientation == 1){
            Log.d("Olu"," calling portrait bg drawer");
     //       drawPortraitBackground();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format, int width,int height){
        Log.d("Olu","surface changed");

        sWidth=width;
        sHeight=height;
        spriteHelper.setSizes(width,height);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public void doSetup(){
        drawBackground();
        drawBattle();
        drawShip();
        drawStats();
        setupDone=true;
    }

    public void drawBattle(){

        if( enemies.size()>=2){

        }else {

            for (int i = 0; i < 2; i++) {

                Random rand = new Random();
                int _i = rand.nextInt(9);// * (0.9f - 0.2f) + 0.9f;
                if (_i < 1) {
                    _i = 1;
                }
                rand = new Random();
                int _j = rand.nextInt(5);//*(0.5f-0.1f)+0.5f;

                int xlocation = (int) (sWidth * (0.1f * _i));
                int ylocation = (int) (sHeight * (0.1f * _j));

                Point enemy = new Point(xlocation, ylocation);
                enemies.add(i,enemy);

                //save the sprite too
                 rand = new Random();
                int next = rand.nextInt(spriteHelper.enemySprites.size()); //*( spriteHelper.enemySprites.size()-0)+spriteHelper.enemySprites.size();
                Rect _rect = spriteHelper.enemySprites.remove(next);
                enemySprite.add(i,_rect);

                enemyCount--;

            }
        }

        for(int i=0;i<enemies.size();i++){
            Point p= enemies.get(i);
            if( p == null){
                continue;
            }
            Rect place = new Rect(p.x,p.y,p.x+spriteHelper.enemyWidth,p.y+spriteHelper.enemyHeight);

            canvas.drawBitmap(spriteHelper.space,enemySprite.get(i),place,null);


            //draw its bullet
            Point bullet =null;

            if( enemyBullets.size()>i) {
                bullet = enemyBullets.get(i);
            }
            if( bullet == null) {
                bullet = getEnemyBullet(p);
                enemyBullets.add(i,bullet);

            }
            place = new Rect(bullet.x,bullet.y,bullet.x+ spriteHelper.enemybulletWidth,
                    bullet.y+spriteHelper.enemybulletHeight);
            canvas.drawBitmap(spriteHelper.space,spriteHelper.enemybulletSprite,place,null);


        }

    }
    private void drawStats(){

        Log.d("Olu","Drawing stats");

        //print #enemies left
        String _text = "Enemies Left "+enemyCount;
        if( enemyCount==0){
            _text = "No more enemies :)";
        }

        Bitmap orgBit = BitmapFactory.decodeResource(res,R.drawable.enemy);
        android.graphics.Bitmap.Config bitmapConfig =   orgBit.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        Bitmap _b = orgBit.copy(bitmapConfig, true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(110,110, 110));
        // text size in pixels
      // paint.setTextSize((int) (12 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

        Rect src = new Rect(0,0,_b.getWidth(),_b.getHeight());
        Rect dest = new Rect(0,0,100, 100);
        canvas.drawBitmap(_b,src,dest,paint);
        canvas.drawText(enemyCount+"",110.0f,90.0f,textPaint);

        //draw life
        orgBit = BitmapFactory.decodeResource(res,R.drawable.life);
        _b = orgBit.copy(bitmapConfig,true);
        src = new Rect(0,0,_b.getWidth(),_b.getHeight());
        dest = new Rect(200,0,300,100);
        canvas.drawBitmap(_b,src,dest,paint);
        canvas.drawText(life+"",310.0f,90.0f,textPaint);

    }

    public void drawBackground(){
        if(orientation==1){
            drawPortraitBackground();
        }
    }

    private void drawPortraitBackground(){
        //canvas = surfaceHolder.lockCanvas();

        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bkg1);
        canvas.drawARGB(255, 0, 0, 0);
        canvas.drawBitmap(bitmap,0,0,paint);

        //surfaceHolder.unlockCanvasAndPost(canvas);
    }

    Runnable runner = new Runnable() {
        @Override
        public void run() {
            if( life == 0){
                //lost
            }
            if( enemyCount==0){
                //won
            }
            while(life >0 && enemyCount>0) {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }
                if (!setupDone) {
                    Log.d("Olu", "locking canvas");
                    canvas = surfaceHolder.lockCanvas();
                    doSetup();

                    Log.d("Olu", "unlocking canvas");
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                dt = System.currentTimeMillis() - lastUpdate;
                // Log.d("d", dt+" "+"latupdate: "+ lastUpdate);
                if (dt >= skipTime) {
                    canvas = surfaceHolder.lockCanvas();
                    moveEnemyBullets();
                    moveShipBullets();
                    doSetup();
                    boolean shipHit=  checkShipHits();
                    boolean enemyHit = checkEnemyHits();
                    if( enemyHit ){
                        enemyCount--;
                        if( enemyCount< 0)
                            enemyCount=0;
                    }
                    if( shipHit){
                        life--;
                    }
                    if( shipHit || enemyHit){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        try {
                            Log.d("Olu","about to sleep ....there was a HIT");

                            Thread.sleep(2000);
                            if( mp1 != null){
                                mp1.release();
                            }
                            if(mp2 != null){
                                mp2.release();
                            }
                        }catch(Exception e){}
                        canvas = surfaceHolder.lockCanvas();
                    }
                    doSetup();

                    surfaceHolder.unlockCanvasAndPost(canvas);
                   lastUpdate = System.currentTimeMillis();
                }

            }
        }
    };

    public void startGame()    {
        gameThread.start();
    }
    public Point getEnemyBullet(Point r){
        if( r == null){
            return null;
        }
        Point b = new Point();
        b.x = r.x + spriteHelper.enemyWidth/2 - spriteHelper.enemybulletWidth/2;
        b.y =  r.y+spriteHelper.enemyHeight;
        return b;
    }

    /* touch listener */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();

             //   Log.d("Olu","action down "+xTouch);

                break;
            case MotionEvent.ACTION_UP:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();
                view.performClick();//to get rid of the message, mimicking a click
                break;
            case MotionEvent.ACTION_MOVE:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();
                break;
        }
        return true;
    }

    private Point getShipBullet(Point p){

        Point _p = new Point(0, (int)(sHeight*0.8));
        /*
        int x = p.x + spriteHelper.shipVHeight/2 - spriteHelper.shipBulletSize/2;
        _p.x = x;
        */
        Random r = new Random();
        _p.x = r.nextInt(9);
        if( _p.x<1){
            _p.x=1;
        }
        _p.x =  (int)(_p.x *0.1 *sWidth);

        Log.d("OLUU","SHIP BULLET "+_p);
        return _p;
    }

    private void drawShip(){

      //  Log.d("Olu","drawing ship "+xTouch);

        yTouch = (int)(sHeight*0.9);
        if(xTouch ==0){
            Random rand = new Random();
            xTouch = rand.nextInt(sWidth);
        }
        //draw ship
        //drawinig ship vertical/
        Rect place = new Rect((int) xTouch,(int)yTouch,
                (int) xTouch + spriteHelper.shipVWidth, sHeight);
        canvas.drawBitmap(spriteHelper.space,spriteHelper.shipVSprites[0],place,null);

        ship.x=(int) xTouch;
        ship.y = (int)yTouch;

        //ship bullet
        if( shipBullet == null) {
            shipBullet = getShipBullet(new Point((int) xTouch, (int) yTouch));
        }

        place = new Rect(shipBullet.x,shipBullet.y,shipBullet.x+spriteHelper.shipBulletSize,shipBullet.y+spriteHelper.shipBulletSize);
        canvas.drawBitmap(spriteHelper.space,spriteHelper.shipBulletSprite,place,null);

    }

    private void moveEnemyBullets(){

        for(Point bullet: enemyBullets){
            if(bullet == null){
                continue;
            }

            bullet.y+=sHeight/bulletHop;
        }

        for(int i=0;i<enemyBullets.size();i++){
            Point bullet = enemyBullets.get(i);
            if( bullet == null){
                continue;
            }
            if( bullet.y >= sHeight) {//reset
                shiftEnemy(i);//relocate
                bullet = getEnemyBullet(enemies.get(i));
                enemyBullets.set(i,bullet);
            }

        }

    }
    private void moveShipBullets(){

        shipBullet.y -= sHeight/bulletHop;

        if( shipBullet.y <= 0) {//reset
            shipBullet = getShipBullet(ship);
        }
    }


    public boolean checkShipHits()    {

        for(int i=0;i<enemyBullets.size();i++){
            Point bullet = enemyBullets.get(i);
            if( bullet == null){
                continue;
            }
            if( bullet.y < ship.y){
                continue;//no chance of a hit
            }
            if( bullet.x+spriteHelper.enemybulletWidth < ship.x){
                continue;
            }
            if( bullet.x > ship.x+spriteHelper.shipVWidth){
                continue;
            }

            Log.d("Olu","chance of a hit!!!!");
            Log.d("Olu", ship.toString());
            Log.d("Olu",(ship.x+" to "+(ship.x+spriteHelper.shipVWidth)));
            Log.d("Olu",bullet.toString());

            Rect bRect = new Rect(bullet.x,bullet.y,bullet.x+ spriteHelper.enemybulletWidth,
                            bullet.y+spriteHelper.enemybulletHeight);
            Rect sRect = new Rect(ship.x,ship.y,ship.x+spriteHelper.shipVWidth,ship.y+spriteHelper.shipVHeight);

            if( ship.x <= bullet.x && (ship.x+spriteHelper.shipVWidth)>=bullet.x
                    ||
                    ( ship.x>bullet.x && ship.x < bullet.x+spriteHelper.enemybulletWidth)

                    ) {//smack down

                showShipExplosion();
                bullet = getEnemyBullet(enemies.get(i));
                enemyBullets.set(i,bullet);


                Log.d("Olu","Your ship is dead man");

                return true;

            }
        }
        return false;
    }

    public boolean checkEnemyHits()    {

        Log.d("Olu","ship "+ship.toString());

        for(int i=0;i<enemies.size();i++){
            Point enemy = enemies.get(i);

            if(enemy==null){
                continue;
            }

            Log.d("Olu","Enemy "+i+" "+enemy.toString());

            if( shipBullet.y > enemy.y){
                Log.d("Olu","A");
                continue;//no chance of a hit
            }

            Log.d("Olu","ENEMY WAS HIT");

            if(
                    enemy.x<shipBullet.x && shipBullet.x <(enemy.x+spriteHelper.enemyWidth)
                    ||
                            shipBullet.x<enemy.x && enemy.x < (shipBullet.x+spriteHelper.shipBulletSize)
                    || enemy.x<shipBullet.x && ((enemy.x+spriteHelper.enemyWidth)<  (shipBullet.x+spriteHelper.shipBulletSize))

                    ){
/*
            if( enemy.x <= shipBullet.x && (enemy.x+spriteHelper.enemyWidth)>=shipBullet.x
                    ||
                    ( ship.x>shipBullet.x && enemy.x < shipBullet.x+spriteHelper.shipBulletSize)

                    ) {//smack down
                */

                showEnemyExplosion(i);
                shipBullet = getShipBullet(ship);

                Log.d("Olu","Your ship is dead man");

                return true;

            }
        }

        return false;
    }

    private void showShipExplosion(){

        Log.d("Olu","drawing explosion");

        Rect place = new Rect(ship.x,ship.y,ship.x+spriteHelper.boomSize,ship.y+spriteHelper.boomSize);
        canvas.drawBitmap(spriteHelper.space,spriteHelper.boomsprites[2],place,null);

        mp1 = MediaPlayer.create(ctx,R.raw.shipboom);
        mp1.start();


    }
    private void showEnemyExplosion(int i){

        Point p = enemies.get(i);
        if( p == null){
            return;
        }
        Log.d("Olu","drawing explosion");

        Rect place = new Rect(p.x,p.y,p.x+spriteHelper.boomSize,p.y+spriteHelper.boomSize);
        canvas.drawBitmap(spriteHelper.space,spriteHelper.boomsprites[3],place,null);

        mp2 = MediaPlayer.create(ctx,R.raw.enemyboom);
        mp2.start();
        replaceEnemy(i);

    }

    public void replaceEnemy(int i) {

        if (enemies.size() < (i+1)) {
            Log.e("Olu", "can't replace requested enemy");
            return;
        }

        if( enemyCount <= 0){
            enemies.set(i,null);
            enemyBullets.set(i,null);
            enemySprite.set(i,null);
            Log.e("Olu","ENEMIES EXHAUSTED");
            return;
        }

        Random rand = new Random();
        int _i = rand.nextInt(9);// * (0.9f - 0.2f) + 0.9f;
        if (_i < 2) {
              _i = 2;
        }
        rand = new Random();
        int _j = rand.nextInt(5);//*(0.5f-0.1f)+0.5f;
        if(_j < 2){ _j=2;}

        int xlocation = (int) (sWidth * (0.1f * _i));
        int ylocation = (int) (sHeight * (0.1f * _j));

        //update to a new enemy
        enemies.set(i, new Point(xlocation,ylocation));

        //save the sprite too
        rand = new Random();
        int next = rand.nextInt(spriteHelper.enemySprites.size()); //*( spriteHelper.enemySprites.size()-0)+spriteHelper.enemySprites.size();
        Rect _rect = spriteHelper.enemySprites.remove(next);
        enemySprite.set(i, _rect);

    }

    public void shiftEnemy(int i) {

        if (enemies.size() < (i+1)) {
            Log.e("Olu", "can't shift requested enemy");
            return;
        }



        Random rand = new Random();
        int _i = rand.nextInt(9);// * (0.9f - 0.2f) + 0.9f;
        if (_i < 2) {
            _i = 2;
        }
        rand = new Random();
        int _j = rand.nextInt(5);//*(0.5f-0.1f)+0.5f;
        if(_j < 2){ _j=2;}

        int xlocation = (int) (sWidth * (0.1f * _i));
        int ylocation = (int) (sHeight * (0.1f * _j));

        //update to a new location
        enemies.set(i, new Point(xlocation,ylocation));
    }


}
