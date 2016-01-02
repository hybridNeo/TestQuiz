package rahul.mahadev.com.testquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

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
                aText = "A: "+options.getJSONObject(0).getString("A");
                bText = "B: "+options.getJSONObject(1).getString("B");
                cText = "C: "+options.getJSONObject(2).getString("C");
                dText = "D: "+options.getJSONObject(3).getString("D");
                quesText = question.getString("question");
                answer = question.getString("answer");
                Log.d("question",quesText);
            }catch (JSONException e){
                e.printStackTrace();
            }finally {
                TextView tv = (TextView)findViewById(R.id.tv);
                tv.setText(quesText);

                Button a = (Button)findViewById(R.id.A);
                Button b = (Button)findViewById(R.id.B);
                Button c = (Button)findViewById(R.id.C);
                Button d = (Button)findViewById(R.id.D);
                a.setText(aText);
                b.setText(bText);
                c.setText(cText);
                d.setText(dText);


            }

        }

        @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

    }
}
