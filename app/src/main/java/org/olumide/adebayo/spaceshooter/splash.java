package org.olumide.adebayo.spaceshooter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class splash extends Activity {
    long startTime = 0l;

    public static final int COMM_CODE = 201;

    long endTime = 0l;
    int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long  _now = System.currentTimeMillis();
       // showDialog=true;

        long diff = _now - startTime;
        long minutes = diff/60000;

        if (requestCode == COMM_CODE) {
            if(resultCode == RESULT_OK) {
                status = data.getIntExtra("status", 0);

                String msg = "";
                if( status == 1) {
                    drawDialog("You Won","in "+minutes +" minutes");
                }else {
                    drawDialog("You Lost",""+minutes+ " minutes");
                }

            }
        }
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

            }
        });
        builder.show();


    }

}