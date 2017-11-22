package sgreevers.depaul.csc472.algebrainflight;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Leader extends RealmObject {
    @Required
    private String name;

    private int score;

    @Required
    private String gameTopic;

    public Leader() { }

    public Leader(String name, int score, String gameTopic) {
        this.name = name;
        this.score = score;
        this.gameTopic = gameTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public String getGameTopic() {
        return gameTopic;
    }

    public void setGameTopic(final String gameTopic) {
        this.gameTopic = gameTopic;
    }
}
