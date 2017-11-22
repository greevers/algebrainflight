package sgreevers.depaul.csc472.algebrainflight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.realm.Realm;

public class SettingsActivity extends AppCompatActivity {
    private Rect blFrame = new Rect();
    private Rect brFrame = new Rect();
    private boolean leftAcquired = false;
    private boolean rightAcquired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpSpinners();
        setUpLeftGesture();
        setUpRightGesture();
    }

    private void setUpSpinners() {
        final Spinner topicSpinner = findViewById(R.id.leaderboard_topic_spinner);
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Settings.TOPICS);
        topicAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setSelection(Settings.getCurrentTopicIndex(this));
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Settings.setCurrentTopic(SettingsActivity.this, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        final Spinner gameModeSpinner = findViewById(R.id.game_mode_spinner);
        ArrayAdapter<String> gameModeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Settings.GAME_MODES);
        gameModeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gameModeSpinner.setAdapter(gameModeAdapter);
        gameModeSpinner.setSelection(Settings.getCurrentGameModeIndex(this));
        gameModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Settings.setCurrentGameMode(SettingsActivity.this, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void setUpLeftGesture() {
        View topLeft = findViewById(R.id.multi_touch_left_top);
        final View bottomLeft = findViewById(R.id.multi_touch_left_bottom);
        topLeft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getActionMasked()) {
                    case (MotionEvent.ACTION_MOVE):
                        int xPos = (int)event.getRawX();
                        int yPos = (int)event.getRawY();

                        int[] blLoc = new int[2];
                        bottomLeft.getLocationInWindow(blLoc);
                        blFrame = new Rect(blLoc[0], blLoc[1], bottomLeft.getWidth(), bottomLeft.getHeight());

                        leftAcquired = blFrame.contains(xPos, yPos);
                        checkResetStatus();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void setUpRightGesture() {
        View topRight = findViewById(R.id.multi_touch_right_top);
        final View bottomRight = findViewById(R.id.multi_touch_right_bottom);
        topRight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getActionMasked()) {
                    case (MotionEvent.ACTION_MOVE):
                        int xPos = (int)event.getRawX();
                        int yPos = (int)event.getRawY();

                        int[] brLoc = new int[2];
                        bottomRight.getLocationInWindow(brLoc);
                        brFrame = new Rect(brLoc[0], brLoc[1], bottomRight.getWidth(), bottomRight.getHeight());

                        rightAcquired = brFrame.contains(xPos, yPos);
                        checkResetStatus();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void checkResetStatus() {
        if (leftAcquired && rightAcquired) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.confirm_reset_leaderboard));

            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Realm.deleteRealm(Realm.getDefaultConfiguration());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }
    }
}
