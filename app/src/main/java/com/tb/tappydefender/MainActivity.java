package com.tb.tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tb.tappydefender.utilities.Utilities;

public class MainActivity extends Activity implements View.OnClickListener {

    // ThIS IS THE ENTRY POINT TO OUR GAME
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Load fastest time
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences(Utilities.HIGH_SCORES_FILE_ID, MODE_PRIVATE);

        final Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);

        final TextView textFastestTime = (TextView)findViewById(R.id.textHiScore);

        long fastestTime = prefs.getLong(Utilities.FASTEST_SCORE_ID, 1000000);
        textFastestTime.setText("Fastest Time: " + fastestTime);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);

        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }

        return false;
    }
}