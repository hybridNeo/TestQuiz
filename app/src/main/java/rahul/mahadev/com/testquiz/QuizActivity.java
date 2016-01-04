package rahul.mahadev.com.testquiz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    int ques_no = 0;
    int num_ques = 0;
    String answer = "";
    JSONArray questions = null;
    Drawable color;
    int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        Log.d("link", link);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean internetPresent = cd.isConnectingToInternet();
        if(internetPresent){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(link, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            // If the response is JSONObject instead of expected JSONArray
                            int duration = 0;
                            try {
                                duration = Integer.parseInt(response.getString("duration"));
                                questions = response.getJSONArray("questions");
                                num_ques = questions.length();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("JSON ERROR", "sector 2");
                            } finally {
                                startQuiz(duration);
                            }

                        }
                    }

            );
        }else{
            String[] arr = link.split("/");
            final String temp = arr[arr.length -1];
            System.out.println("File is " + temp);
            JSONObject response = null;
            try{
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader bufferedReader = new BufferedReader(new FileReader( new File(getApplicationContext().getFilesDir(), temp)));
                while ((line = bufferedReader.readLine()) != null)
                {
                    sb.append(line);
                }
                String content = sb.toString();

                response = new JSONObject(content);
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            } finally {
                int duration = 0;
                try {
                    duration = Integer.parseInt(response.getString("duration"));
                    questions = response.getJSONArray("questions");
                    num_ques = questions.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON ERROR", "sector 2");
                } finally {
                    startQuiz(duration);
                }
            }
        }

        }
    public void startQuiz(int duration){
//        Log.d("HERE","here");
        color = ((Button)findViewById(R.id.A)).getBackground();

        new CountDownTimer(duration * 1000, 1000){
            public void onTick(long millisUntilFinished){
                //Do something in every tick

                TextView tView = (TextView)findViewById(R.id.timer);

                int total_secs = (int) (millisUntilFinished/1000);
                int mins = total_secs/60;
                int secs = total_secs % 60;
                tView.setText("Time: " + mins + ":" + String.format("%02d", secs));
                    //Put count down timer remaining time in a variable
//                    timeRemaining = millisUntilFinished;

            }
            public void onFinish(){
                //Do something when count down finished
                Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                intent.putExtra("score",score+"");
                intent.putExtra("num",num_ques+"");
                startActivity(intent);


            }
        }.start();

        nextQuestion();
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }
    public void nextQuestion(){
        String quesText = "";

        String aText = "";
        String bText = "";
        String cText = "";
        String dText = "";
        TextView qNum = (TextView)findViewById(R.id.qNo);
        qNum.setText("Question " + (1+ques_no) + ":");
        try {
            JSONObject question = questions.getJSONObject(ques_no);
            JSONArray options = question.getJSONArray("options");
            aText = " "+options.getJSONObject(0).getString("A");
            bText = " "+options.getJSONObject(1).getString("B");
            cText = " "+options.getJSONObject(2).getString("C");
            dText = " "+options.getJSONObject(3).getString("D");
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
            a.setBackgroundDrawable(color);
            b.setBackgroundDrawable(color);
            c.setBackgroundDrawable(color);
            d.setBackgroundDrawable(color);
            a.setText(aText);
            b.setText(bText);
            c.setText(cText);
            d.setText(dText);
            a.setOnClickListener(this);
            b.setOnClickListener(this);
            c.setOnClickListener(this);
            d.setOnClickListener(this);

        }
    }

        @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
//        Log.d("here","hello there button here");
        disableClick();
        ques_no++;
        String choice = "";
        switch(v.getId()) {
            case R.id.A:
                choice = "A";
                break;
            case R.id.B:
                choice = "B";
                break;
            case R.id.C:
                choice = "C";
                break;
            case R.id.D:
                choice = "D";
                break;
        }
        if(answer.equals(choice)){
            Button correctAnswer = (Button)findViewById(v.getId());
            correctAnswer.setBackgroundColor(correctAnswer.getContext().getResources().getColor(R.color.correctAnswer));
            score+=1;

            Log.d("DBG","correct "+ choice);
        }else {
            Log.d("DBG", "incorrect" + answer + choice);
            Button wrongAnswer = (Button) findViewById(v.getId());
            wrongAnswer.setBackgroundColor(wrongAnswer.getContext().getResources().getColor(R.color.wrongAnswer));
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if(ques_no < num_ques){
                    nextQuestion();
                }else{
                    Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                    intent.putExtra("score",score+"");
                    intent.putExtra("num",num_ques+"");
                    startActivity(intent);
                    Log.d("TEST",ques_no+" quiz over "+num_ques);
                }
            }
        }, 1000);

    }
    /*
        Function to disable click listeners for all the buttons once the answer is selected
     */
    public void disableClick(){
        Button a = (Button)findViewById(R.id.A);
        Button b = (Button)findViewById(R.id.B);
        Button c = (Button)findViewById(R.id.C);
        Button d = (Button)findViewById(R.id.D);
        a.setOnClickListener(null);
        b.setOnClickListener(null);
        c.setOnClickListener(null);
        d.setOnClickListener(null);
    }
}
