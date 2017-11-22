package sgreevers.depaul.csc472.algebrainflight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainGameplayActivity extends AppCompatActivity {
    private int playerScore = 0;
    private String gameTopic = "";
    private List<Problem> problems;
    private List<Problem> ecProblems;
    private Problem currentProblem;
    private GameplayView gameplayView;
    private Random rand = new Random();
    private TextView starterTV;
    private TextView countdownTV;
    private TextView scoreTV;
    private View flightControlView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gameplay);
        setUpViews();
        setUpPlayList();
        setUpTouchFlightControl();
        updateProblem();
        startGame();
    }

    private void setUpViews() {
        starterTV = findViewById(R.id.starter_textview);
        countdownTV = findViewById(R.id.countdown_textview);
        scoreTV = findViewById(R.id.score_tv);
        gameplayView = findViewById(R.id.gameplay_surfaceview);
        flightControlView = findViewById(R.id.flight_control_view);
    }

    private void setUpPlayList() {
        gameTopic = Settings.getCurrentTopic(this);
        problems = Problem.getAllProblems(this, Settings.getCurrentTopicFileName(this));
        ecProblems = new ArrayList<>();

        for (int i = 0; i < problems.size(); i++) {
            Problem problem = problems.get(i);
            if (problem.isExtraCredit()) {
                ecProblems.add(problem);
            }
        }
        problems.removeAll(ecProblems);

        String gameMode = Settings.getCurrentGameMode(this);
        if (Arrays.asList(Settings.GAME_MODES).indexOf(gameMode) != 0) {
            Collections.shuffle(problems);
            List<Problem> playList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                playList.add(problems.get(i));
            }
            problems = playList;
        }
    }

    private void setUpTouchFlightControl() {
        flightControlView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                int index = event.getActionIndex();
                int pointerId = event.getPointerId(index);

                switch(action) {
                    case (MotionEvent.ACTION_DOWN):
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        int yaw = (int)event.getX(pointerId);
                        gameplayView.setYaw(yaw);
                        return true;
                    case (MotionEvent.ACTION_UP):
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void updateProblem() {
        currentProblem = problems.get(rand.nextInt(problems.size()));

        if (currentProblem.isExtraCredit()) {
            // Start extra credit activity
        }

        TextView question = findViewById(R.id.question_textview);
        question.setText(currentProblem.getQuestion());
        gameplayView.updateProblem(currentProblem);
    }

    private void startGame() {
        CountDownTimer startGameTimer = new CountDownTimer(4000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int)(millisUntilFinished/1000);
                switch (seconds) {
                    case 3:
                        starterTV.setText(getString(R.string.ready));
                        break;
                    case 2:
                        starterTV.setText(getString(R.string.set));
                        break;
                    case 1:
                        starterTV.setText(getString(R.string.go));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFinish() {
                starterTV.setVisibility(View.INVISIBLE);
                startProblem();
            }
        };
        startGameTimer.start();
    }

    private void startProblem() {
        gameplayView.startAnimation();
        countDownTimer = new CountDownTimer(6000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int)(millisUntilFinished/1000);
                countdownTV.setText(Integer.toString(seconds));
            }

            @Override
            public void onFinish() {
                countdownTV.setText("5");
                endProblem();
            }
        };
        countDownTimer.start();
    }

    private void gameEnded() {
        CountDownTimer gameEndedTimer = new CountDownTimer(2000, 1000){
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                checkIfLeader();
            }
        };
        gameEndedTimer.start();
    }

    private void endProblem() {
        gameplayView.stopAnimation();
        playerScore += gameplayView.isAnswerCorrect() ? 1 : 0;
        String score = getString(R.string.score) + " " + Integer.toString(playerScore);
        scoreTV.setText(score);
        problems.remove(currentProblem);

        if (problems.isEmpty()) {
            starterTV.setText(getString(R.string.complete));
            starterTV.setVisibility(View.VISIBLE);
            gameEnded();
        } else {
            updateProblem();
            startProblem();
        }
    }

    private void checkIfLeader() {
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Leader> results = realm.where(Leader.class)
                    .equalTo("gameTopic", gameTopic)
                    .findAllSorted("score", Sort.DESCENDING);

            if (results.size() == 5) {
                boolean replaceLeader = false;
                for (Leader leader : results) {
                    if (playerScore > leader.getScore()) {
                        replaceLeader = true;
                        leaderDialog(results.last());
                    }
                }
                if (!replaceLeader) {
                    finish();
                }
            } else {
                leaderDialog(null);
            }
        } catch (Exception e) {
            // Something went wrong retrieving the leaders
        }
    }

    private void leaderDialog(final Leader replaceLeader) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_initials));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                replaceLeaderAndExit(input.getText().toString(), replaceLeader);
            }
        });

        builder.show();
    }

    private void replaceLeaderAndExit(String playerName, Leader replaceLeader) {
        Leader leader = new Leader(playerName, playerScore, gameTopic);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (replaceLeader != null) replaceLeader.deleteFromRealm();
        realm.copyToRealm(leader);
        realm.commitTransaction();
        realm.close();
        finish();
    }
}
