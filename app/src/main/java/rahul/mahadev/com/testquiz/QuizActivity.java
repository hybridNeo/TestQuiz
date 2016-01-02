package rahul.mahadev.com.testquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        Log.d("link", link);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(link, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // If the response is JSONObject instead of expected JSONArray
                int duration = 0;
                JSONArray questions = null;
                try {
                    duration = Integer.parseInt(response.getString("duration"));
                    questions = response.getJSONArray("questions");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON ERROR", "sector 2");
                } finally {
                    startQuiz(questions,duration);
                }

                }
            }

            );
        }
    public void startQuiz(JSONArray questions,int duration){
//        Log.d("HERE","here");

            String quesText = "";
            String answer = "";
            String aText = "";
            String bText = "";
            String cText = "";
            String dText = "";
            try {
                JSONObject question = questions.getJSONObject(0);
                JSONArray options = question.getJSONArray("options");
                quesText = question.getString("question");
                answer = question.getString("answer");
                Log.d("question",quesText);
            }catch (JSONException e){
                e.printStackTrace();
            }finally {
                TextView tv = (TextView)findViewById(R.id.tv);
                tv.setText(quesText);

            }

        }

        @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }
}
