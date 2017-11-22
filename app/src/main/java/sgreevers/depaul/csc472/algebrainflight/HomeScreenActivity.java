package sgreevers.depaul.csc472.algebrainflight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        createRealm();
        initDefaultGameSettings();
    }

    private void createRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("leaders.realm").build();
        Realm.setDefaultConfiguration(config);
    }

    private void initDefaultGameSettings() {
        Settings.initGameSettings(this);
    }

    public void startGameplay(View view) {
        startNewActivity(MainGameplayActivity.class);
    }

    public void openLeaderboard(View view) {
        startNewActivity(LeaderboardActivity.class);
    }

    public void openSettings(View view) {
        startNewActivity(SettingsActivity.class);
    }

    private void startNewActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
