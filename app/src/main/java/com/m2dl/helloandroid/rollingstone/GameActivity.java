package com.m2dl.helloandroid.rollingstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.m2dl.helloandroid.rollingstone.model.Score;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {
    public static final String PREFS_NAME = "StonePrefs";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final double SENSITIVITY = 0.2;
    private Uri imageUri;
    private ImageView imageView;
    private GameActivity activity;
    //private ImageView stoneView;

    BallView mBallView = null;
    TrailView mTrailView = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    int mScrWidth, mScrHeight;
    android.graphics.PointF mBallPos, mBallSpd;
    private boolean cheat_activated = false;
    boolean gameStarted;
    boolean endGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            hideSystemUI();
        }

        setContentView(R.layout.activity_game);
        imageView = (ImageView) findViewById(R.id.imageView);
//        stoneView = (ImageView) findViewById(R.id.stone);
//        stoneView.setVisibility(View.INVISIBLE);
        final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.tiltball_view);

        //get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScrWidth = displayMetrics.widthPixels;
        mScrHeight = displayMetrics.heightPixels;
        mBallPos = new android.graphics.PointF();
        mBallSpd = new android.graphics.PointF();

        //create variables for ball position and speed
        mBallPos.x = mScrWidth / 2;
        mBallPos.y = mScrHeight / 2;
        mBallSpd.x = 0;
        mBallSpd.y = 0;

        endGame = false;
        //create initial ball
        mBallView = new BallView(this, mBallPos.x, mBallPos.y);
        mTrailView = new TrailView(this);
        mTrailView.touch_start(mBallPos.x, mBallPos.y);

        mainView.addView(mTrailView); //add trail to main screen
        mainView.addView(mBallView); //add ball to main screen

        mBallView.invalidate(); //call onDraw in BallView
        mTrailView.invalidate();
        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        //set ball speed based on phone tilt (ignore Z axis)
                        if (gameStarted) {
                            if (Math.abs(event.values[0]) > SENSITIVITY) {
                                mBallSpd.x = -event.values[0];
                            } else {
                                mBallSpd.x = 0;
                            }
                            if (Math.abs(event.values[1]) > SENSITIVITY) {
                                mBallSpd.y = event.values[1];
                            } else {
                                mBallSpd.y = 0;
                            }
                        }
                        //timer event will redraw ball
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore this event
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);

        activity = this;

//        //listener for touch event
//        mainView.setOnTouchListener(new android.view.View.OnTouchListener() {
//            public boolean onTouch(android.view.View v, android.view.MotionEvent e) {
//                //set ball position based on screen touch
//                mBallPos.x = e.getX();
//                mBallPos.y = e.getY();
//                //timer event will redraw ball
//                return true;
//            }});

        //listener for touch event
        mainView.setOnTouchListener(new android.view.View.OnTouchListener() {
            public boolean onTouch(android.view.View v, android.view.MotionEvent e) {

                //set ball position based on screen touch
                if (e.getAction() == MotionEvent.ACTION_MOVE && cheat_activated) {
                    mBallPos.y = e.getY();
                    mBallPos.x = e.getX();
                }

                if (e.getAction() == MotionEvent.ACTION_POINTER_UP && e.getPointerCount() == 2) {
                    cheat_activated = !cheat_activated;
                    Toast.makeText(GameActivity.this, cheat_activated ? "CHEAT ON (petit coquin)" : "CHEAT OFF", Toast.LENGTH_SHORT).show();
                }

                //timer event will redraw ball
                return true;
            }});
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        takePhoto();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        imageUri = Uri.fromFile(photo);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(bitmap);
                        gameStarted = true;
                        //stoneView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    //For state flow see http://developer.android.com/reference/android/app/Activity.html
    @Override
    public void onPause() //app moved to background, stop background threads
    {
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;
        super.onPause();
    }

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        //create timer to move ball to new position
        mTmr = new Timer();
        mTsk = new TimerTask() {
            public void run() {
                //if debugging with external device,
                //  a cat log viewer will be needed on the device
//                android.util.Log.d(
//                        "TiltBall","Timer Hit - " + mBallPos.x + ":" + mBallPos.y);
                //move ball based on current speed
                mTrailView.touch_move(mBallPos.x, mBallPos.y);
                mBallPos.x += mBallSpd.x;
                mBallPos.y += mBallSpd.y;
                //if ball goes off screen, reposition to opposite side of screen
                if (mBallPos.x > mScrWidth || mBallPos.y > mScrHeight || mBallPos.x < 0 || mBallPos.y < 0) {
                    endGame = true;
                    callbackEndGame();
                }
                //update ball class instance
                mBallView.setmX(mBallPos.x);
                mBallView.setmY(mBallPos.y);

                //redraw ball. Must run in background thread to prevent thread lock.
                RedrawHandler.post(new Runnable() {
                    public void run() {
                        mBallView.invalidate();
                        mTrailView.invalidate();
                    }
                });
            }
        }; // TimerTask

        mTmr.schedule(mTsk, 5, 5); //start timer
        super.onResume();
    } // onResume

    //listener for config change.
    //This is called when user tilts phone enough to trigger landscape view
    //we want our app to stay in portrait view, so bypass event
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(endGame) {
            callbackEndGame();
        }
    }

    public void callbackEndGame() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor  = settings.edit();
        settings.edit().putLong("Score",100);
        editor.commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Game over")
                        .setMessage("you score is 100. would you like to retry?")
                        .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("Nope", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recreate();
                            }

                        })
                        .show();
            }
        });
    }
}
