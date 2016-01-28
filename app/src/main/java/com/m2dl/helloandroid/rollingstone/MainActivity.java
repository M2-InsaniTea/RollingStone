package com.m2dl.helloandroid.rollingstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity {
    public static final String PREFS_NAME = "StonePrefs";
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            hideSystemUI();
        }
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, 0);
        EditText text = (EditText) findViewById(R.id.username);
        String content = settings.getString("username","Choose a username");
        if(content.equals("Choose a username")) {
            text.setHint(content);
        } else {
            text.setText(content);
        }

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void startGame(View view) {
        EditText text = (EditText) findViewById(R.id.username);
        String content = text.getText().toString();
        if(content.equals("")) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle("Missing username")
                    .setMessage("Please choose a username")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    })
                    .show();
        } else {
            SharedPreferences.Editor editor = settings.edit();
            settings.edit().putString("username", content);
            editor.commit();
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }
    }

    public void startLeaderboard(View view) {
            Intent intent = new Intent(this,LeaderBoardActivity.class);
            startActivity(intent);
    }

}
