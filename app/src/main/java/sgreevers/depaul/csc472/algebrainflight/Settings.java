package sgreevers.depaul.csc472.algebrainflight;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

public class Settings {
    static final String PREFERENCES = "PREFERENCES";
    static final String[] TOPICS = {
            "Adding & Subtracting Integers",
            "Multiplying Fractions",
            "Finding Least Common Multiple"
    };
    private static final String[] TOPIC_FILES = {
            "add_subtract_integers.json",
            "multiplying_fractions.json",
            "least_common_multiple.json"
    };
    static final String[] GAME_MODES = {
            "Endless Play",
            "Best of Ten"
    };

    static void initGameSettings(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPref.contains(activity.getString(R.string.settings_topics))) {
            Settings.setCurrentTopic(activity, 0);
        }
        if (!sharedPref.contains(activity.getString(R.string.settings_game_modes))) {
            Settings.setCurrentGameMode(activity, 0);
        }
    }

    static void setCurrentTopic(Activity activity, int i) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.settings_topics), Settings.TOPICS[i]);
        editor.apply();
    }

    static int getCurrentTopicIndex(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        String currentTopic = sharedPref.getString(activity.getString(R.string.settings_topics), Settings.TOPICS[0]);
        return Arrays.asList(Settings.TOPICS).indexOf(currentTopic);
    }

    static String getCurrentTopic(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.settings_topics), Settings.TOPICS[0]);
    }

    static String getCurrentTopicFileName(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        String currentTopic = sharedPref.getString(activity.getString(R.string.settings_topics), Settings.TOPICS[0]);
        return Settings.TOPIC_FILES[Arrays.asList(Settings.TOPICS).indexOf(currentTopic)];
    }

    static void setCurrentGameMode(Activity activity, int i) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.settings_game_modes), Settings.GAME_MODES[i]);
        editor.apply();
    }

    static int getCurrentGameModeIndex(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        String currentGameMode = sharedPref.getString(activity.getString(R.string.settings_game_modes), Settings.GAME_MODES[0]);
        return Arrays.asList(Settings.GAME_MODES).indexOf(currentGameMode);
    }

    static String getCurrentGameMode(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Settings.PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.settings_game_modes), Settings.GAME_MODES[0]);
    }
}
