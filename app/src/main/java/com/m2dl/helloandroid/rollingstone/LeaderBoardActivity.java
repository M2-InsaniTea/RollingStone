package com.m2dl.helloandroid.rollingstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.m2dl.helloandroid.rollingstone.database.ScoreDbHelper;
import com.m2dl.helloandroid.rollingstone.model.Score;

import java.util.Iterator;
import java.util.Map;

import static android.widget.TableRow.LayoutParams;
import static com.m2dl.helloandroid.rollingstone.model.Score.ScoreEntry;

public class LeaderBoardActivity extends Activity {
    ScoreDbHelper mDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            hideSystemUI();
        }
        setContentView(R.layout.activity_leader_board);

        // Filling the table
        TableLayout headerTable = (TableLayout) this.findViewById(R.id.header_table);
        headerTable = addRowToTable(headerTable, "Name", "Score", true);
        int rowHeightInPixels = 0;
        float scale = this.getResources().getDisplayMetrics().density;
        int rowHeighInDp = (int) (rowHeightInPixels * scale + 0.5f);
        String longestRow = "";

        mDbHelper = new ScoreDbHelper(this);
        db = mDbHelper.getWritableDatabase();

        String[] projection = {
                ScoreEntry._ID,
                ScoreEntry.COLUMN_NAME_USERNAME,
                ScoreEntry.COLUMN_NAME_SCORE_VALUE,
        };


        String sortOrder = ScoreEntry.COLUMN_NAME_SCORE_VALUE + " DESC";

        Cursor cursor = db.query(ScoreEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

        int lengthRow = 0;
        TableLayout contentTable = (TableLayout) this.findViewById(R.id.content_table);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ScoreEntry.COLUMN_NAME_USERNAME));
            String score = Integer.toString(cursor.getInt(cursor.getColumnIndex(ScoreEntry.COLUMN_NAME_SCORE_VALUE)));
            contentTable = addRowToTable(contentTable, name, score);
            lengthRow = name.length() + score.length();
            if (longestRow.isEmpty() || lengthRow > (longestRow.length() - 1)) //Include -1 for subtracting the space occupied by "-"
                longestRow = name + "-" + score;
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private TableLayout addRowToTable(TableLayout table, String contentCol1, String contentCol2) {
        return addRowToTable(table, contentCol1, contentCol2, false);
    }

    private TableLayout addRowToTable(TableLayout table, String contentCol1, String contentCol2, boolean important) {
        Context context = getApplicationContext();
        TableRow row = new TableRow(context);

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams();
        // Wrap-up the content of the row
        rowParams.height = LayoutParams.WRAP_CONTENT;
        rowParams.width = LayoutParams.WRAP_CONTENT;

        // The simplified version of the table of the picture above will have two columns
        // FIRST COLUMN
        TableRow.LayoutParams col1Params = new TableRow.LayoutParams();
        // Wrap-up the content of the row
        col1Params.height = LayoutParams.WRAP_CONTENT;
        col1Params.width = LayoutParams.WRAP_CONTENT;
        // Set the gravity to center the gravity of the column
        col1Params.gravity = Gravity.CENTER;
        TextView col1 = new TextView(context);
        col1.setText(contentCol1);
        col1.setTextColor(0xFF000000);
        if (important) {
            col1.setTypeface(null, Typeface.BOLD);
        }
        row.addView(col1, col1Params);

        // SECOND COLUMN
        TableRow.LayoutParams col2Params = new TableRow.LayoutParams();
        // Wrap-up the content of the row
        col2Params.height = LayoutParams.WRAP_CONTENT;
        col2Params.width = LayoutParams.WRAP_CONTENT;
        // Set the gravity to center the gravity of the column
        col2Params.gravity = Gravity.CENTER;
        TextView col2 = new TextView(context);
        col2.setTextColor(0xFF000000);
        if (important) {
            col2.setTypeface(null, Typeface.BOLD);
        }
        col2.setText(contentCol2);
        row.addView(col2, col2Params);

        table.addView(row, rowParams);

        return table;
    }

    public void resetLeaderboard(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset leaderboard")
                .setMessage("Do you really want to reset the leaderboard?")
                .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("delete from " + ScoreEntry.TABLE_NAME);
                        TableLayout contentTable = (TableLayout) findViewById(R.id.content_table);
                        contentTable.removeAllViews();
                    }

                })
                .setNegativeButton("Nope", null)
                .show();
    }
}
