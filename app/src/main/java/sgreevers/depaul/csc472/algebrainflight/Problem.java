package sgreevers.depaul.csc472.algebrainflight;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Problem {
    private String question;
    private boolean isExtraCredit;
    private Answer[] answers;

    public Problem(String question, boolean isExtraCredit, Answer[] answers) {
        this.question = question;
        this.isExtraCredit = isExtraCredit;
        this.answers = answers;
    }

    public static List<Problem> getAllProblems(Context context, String fileName) {
        String json;
        List<Problem> allProblems = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject problemObj = jsonArray.getJSONObject(i);

                JSONArray answersObjArray = problemObj.getJSONArray("answers");
                Answer[] answers = new Answer[3];
                for(int j = 0; j < answersObjArray.length(); j++) {
                    JSONObject answerObj = answersObjArray.getJSONObject(j);
                    Answer answer = new Answer(
                            answerObj.getString("value"),
                            answerObj.getBoolean("isCorrect")
                    );
                    answers[j] = answer;
                }

                Problem problem = new Problem(
                        problemObj.getString("question"),
                        problemObj.getBoolean("isExtraCredit"),
                        answers
                );
                allProblems.add(problem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allProblems;
    }

    public String getQuestion() {
        return this.question;
    }

    public boolean isExtraCredit() {
        return this.isExtraCredit;
    }

    public Answer[] getAnswers() {
        return this.answers;
    }
}
