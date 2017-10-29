package org.olumide.adebayo.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.bottom;
import static android.R.attr.left;
import static android.R.attr.max;
import static android.R.attr.min;
import static android.R.attr.right;
import static android.R.attr.top;
import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by oadebayo on 10/28/17.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback , View.OnTouchListener{

    private Context ctx;
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    int orientation = 1;//default
    Paint paint =new Paint();

    SpriteHelper spriteHelper= null;

    Resources res = getResources();
    int sWidth =0;
    int sHeight = 0;
    boolean change = true;
    boolean setupDone = false;

    float yTouch, xTouch;

    int enemyCount = 10;
    Point boom,shipBullet;
    Point ship=new Point(0,0);
    ArrayList<Point> enemies = new ArrayList<Point>();
    ArrayList<Point> enemyBullets = new ArrayList<Point>();

    ArrayList<Rect> enemySprite = new ArrayList<Rect>();

    Thread gameThread ;
    long enemyMoveTime = 0;
    //15 frames per seconds
    //float skipTime =1000.0f/15.0f; //setting 30fps
    float skipTime = 2000.0f/1.0f;

    long lastUpdate;
    float dt;


    public MySurfaceView(Context ctx,int orientation, SpriteHelper sp){

        super(ctx);
        this.ctx = ctx;
        this.orientation=orientation;

        Log.d("Olu","orientation is "+this.orientation);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        spriteHelper=sp;
        gameThread = new Thread(runn);

    }

    public void setTouchLocation(float x,float y){
        xTouch=x;
        yTouch=y;
    }

    public void setSpriteHelper(SpriteHelper sp){
        spriteHelper=sp;
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
            }
        }

        for(int i=0;i<enemies.size();i++){
            Point p= enemies.get(i);
            Rect place = new Rect(p.x,p.y,p.x+spriteHelper.enemyWidth,p.y+spriteHelper.enemyHeight);

            canvas.drawBitmap(spriteHelper.space,enemySprite.get(i),place,null);


            //draw its bullet
            Point bullet =null;

            if( enemyBullets.size()>i) {
           //     Log.d("Olu","not calculating bullet Loc");
                bullet = enemyBullets.get(i);
            }
            if( bullet == null) {
            //    Log.d("Olu","calculating bullet loc");
                bullet = getEnemyBullet(p);
                enemyBullets.add(i,bullet);

            }
            place = new Rect(bullet.x,bullet.y,bullet.x+ spriteHelper.enemybulletWidth,
                    bullet.y+spriteHelper.enemybulletHeight);
            canvas.drawBitmap(spriteHelper.space,spriteHelper.enemybulletSprite,place,null);

//            Log.d("Olu","adding enemy "+p.y+":  "+p.x);

     //       Log.d("Olu","adding bullet "+bullet.y+":  "+bullet.x);
        }




    }

    public void drawBackground(){
        if(orientation==1){
            drawPortraitBackground();
        }
    }

    private void drawPortraitBackground(){
        //canvas = surfaceHolder.lockCanvas();

        Log.d("Olu","drawing bg image");

        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bkg1);
        canvas.drawARGB(255, 0, 0, 0);
        canvas.drawBitmap(bitmap,0,0,paint);

        //surfaceHolder.unlockCanvasAndPost(canvas);
    }

    Runnable runn = new Runnable() {
        @Override
        public void run() {
            while(change) {
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
                    if( checkHits()){
                        change = false;
                    }else {
                        doSetup();
                    }
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
        int x = p.x + spriteHelper.shipVHeight/2 - spriteHelper.shipBulletSize/2;
        _p.x = x;
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

        shipBullet= getShipBullet(new Point((int)xTouch,(int)yTouch));
        place = new Rect(shipBullet.x,shipBullet.y,shipBullet.x+spriteHelper.shipBulletSize,shipBullet.y+spriteHelper.shipBulletSize);
        canvas.drawBitmap(spriteHelper.space,spriteHelper.shipBulletSprite,place,null);

    }

    private void moveEnemyBullets(){

        for(Point bullet: enemyBullets){
                bullet.y+=sHeight/10;
       //     Log.d("Olu","new bullet LOC "+bullet.y+" :"+bullet.x);
        }

        /*
        Log.d("Olu","count of enemies "+enemies.size());
        Log.d("Olu","count of bullets "+enemyBullets.size());
        Log.d("Olu","count of sprite "+enemySprite);
        Log.d("Olu","count of bullets "+enemyBullets);
        Log.d("Olu","count of enemies "+enemies);
*/

        for(int i=0;i<enemyBullets.size();i++){
            Point bullet = enemyBullets.get(i);
            if( bullet.y >= sHeight) {//reset
            //    Log.d("Olu","index is "+i);
                bullet = getEnemyBullet(enemies.get(i));
                enemyBullets.set(i,bullet);
            }

        }

    }


    public boolean checkHits()    {

        for(int i=0;i<enemyBullets.size();i++){
            Point bullet = enemyBullets.get(i);
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

            //if(sRect.intersect(bRect)){

            if( ship.x <= bullet.x && (ship.x+spriteHelper.shipVWidth)>=bullet.x
                    ||
                    ( ship.x>bullet.x && ship.x < bullet.x+spriteHelper.enemybulletWidth)

                    ) {//smack down
            //  if( Rect.intersects(sRect,bRect)){

                showShipExplosion();
                bullet = getEnemyBullet(enemies.get(i));
                enemyBullets.set(i,bullet);


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
    }
}
