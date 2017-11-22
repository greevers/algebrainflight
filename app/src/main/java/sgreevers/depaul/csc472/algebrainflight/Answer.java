package sgreevers.depaul.csc472.algebrainflight;

public class Answer {
    private String value;
    private boolean isCorrect;

    public Answer(String value, boolean isCorrect) {
        this.value = value;
        this.isCorrect = isCorrect;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isCorrect() {
        return this.isCorrect;
    }
}
