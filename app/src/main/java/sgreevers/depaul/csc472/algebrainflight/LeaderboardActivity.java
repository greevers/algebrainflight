package sgreevers.depaul.csc472.algebrainflight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView leaderList;
    private RecyclerView.Adapter leaderboardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Leader> LEADERS = new ArrayList<>();
    private String selectedTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setUpTopicSpinner();
        setUpLeaderboard();
    }

    private void setUpTopicSpinner() {
        final Spinner topicSpinner = findViewById(R.id.leaderboard_topic_spinner);
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Settings.TOPICS);
        topicAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        topicSpinner.setAdapter(topicAdapter);
        int selectedTopicIndex = Settings.getCurrentTopicIndex(this);
        selectedTopic = Settings.TOPICS[selectedTopicIndex];
        topicSpinner.setSelection(selectedTopicIndex);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reloadLeaderboard(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void reloadLeaderboard(int i) {
        selectedTopic = Settings.TOPICS[i];
        LEADERS.clear();
        loadLeaders();
        leaderboardAdapter.notifyDataSetChanged();
    }

    private void setUpLeaderboard() {
        leaderList = findViewById(R.id.leaderboard);
        leaderList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        leaderList.setLayoutManager(layoutManager);
        leaderboardAdapter = new LeaderboardAdapter();
        leaderList.setAdapter(leaderboardAdapter);
    }

    private void loadLeaders() {
        try {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Leader> results = realm.where(Leader.class).equalTo("gameTopic", selectedTopic).findAllSorted("score", Sort.DESCENDING);
            LEADERS.addAll(results);

            if (LEADERS.isEmpty()) {
                String message =  "No leaders exist for this topic. Play game to add your name to the leaderboard.";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Something went wrong retrieving the leaders
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView score;

        public ViewHolder(ViewGroup row) {
            super(row);
            name = row.findViewById(R.id.player_name);
            score = row.findViewById(R.id.player_score);
        }
    }

    class LeaderboardAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup row = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
            ViewHolder vh = new ViewHolder(row);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Leader leader = LEADERS.get(position);
            holder.name.setText(leader.getName());
            holder.score.setText(Integer.toString(leader.getScore()));
        }

        @Override
        public int getItemCount() {
            return LEADERS.size();
        }
    }
}
