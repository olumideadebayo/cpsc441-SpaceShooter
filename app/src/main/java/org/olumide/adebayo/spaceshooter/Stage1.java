package org.olumide.adebayo.spaceshooter;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.os.Handler;

public class Stage1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpriteHelper spriteHelper = new SpriteHelper(getResources());

        hideAndFull();

        final MySurfaceView mySurfaceView = new MySurfaceView(this,1,spriteHelper);
        setContentView(mySurfaceView);
        mySurfaceView.setOnTouchListener(mySurfaceView);
        mySurfaceView.startGame();

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                    while( mySurfaceView.getLife()>0 && mySurfaceView.getEnemyCount()>0){
                        //keep waiting
                        try {
                            Thread.sleep(10000);
                        }catch (Exception e){}
                        }


                    Log.d("Olu","GAME ENDED");

                    //go back
                    int status = 0;
                    if( mySurfaceView.getLife() > 0){
                        status =1;
                    }
                //won
                Intent intent = new Intent();
                intent.putExtra("status", status);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        };

        //start a listening thread ...when the thread sees the game is over it goes to previous activity
        Thread _t = new Thread(timerRunnable);
        Log.d("Olu","starting monitor");
        _t.start();

    }
    public void hideAndFull()    {
        ActionBar bar = getActionBar();
        bar.hide();
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }



}
