package org.olumide.adebayo.spaceshooter;

import android.os.Bundle;
import android.app.Activity;

public class Stage1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpriteHelper spriteHelper = new SpriteHelper(getResources());

        MySurfaceView mySurfaceView = new MySurfaceView(this,1,spriteHelper);
        setContentView(mySurfaceView);
        mySurfaceView.setOnTouchListener(mySurfaceView);
        mySurfaceView.startGame();

    }

}
