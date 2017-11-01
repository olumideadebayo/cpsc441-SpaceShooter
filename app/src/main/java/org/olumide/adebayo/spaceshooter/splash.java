package org.olumide.adebayo.spaceshooter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class splash extends Activity {
    long startTime = 0l;

    public static final int COMM_CODE = 201;

    int status = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        long  _now = System.currentTimeMillis();
       // showDialog=true;

        long diff = _now - startTime;

        long minutes = (diff / 1000)  / 60;
        long seconds = (diff / 1000) % 60;
        //Log.d("OLUU","started at "+_now);
        //Log.d("OLUU","ended at "+startTime);
        Log.d("OLUU","time taken "+diff);

        if (requestCode == COMM_CODE) {
            if(resultCode == RESULT_OK) {
                status = data.getIntExtra("status", 0);

                String msg = "in "+minutes+" and "+seconds+" seconds";
                if( status == 1) {
                    drawDialog("You Won",msg);
                }else {
                    drawDialog("You Lost",msg);
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.continent_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(splash.this, Stage1.class);
        startTime = System.currentTimeMillis();

        startActivityForResult(intent,201);
        return super.onOptionsItemSelected(item);
    }


    private void drawDialog(String title,String msg){


        final AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
        builder.setTitle(title);
           builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();
        return;

    }

}