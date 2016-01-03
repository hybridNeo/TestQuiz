package rahul.mahadev.com.testquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent i = getIntent();
        int num_ques = Integer.parseInt(i.getStringExtra("num"));
        float score = Integer.parseInt(i.getStringExtra("score"));
        TextView num_ans = (TextView) findViewById(R.id.numAnswered);
        TextView sc_view = (TextView)findViewById(R.id.Score);
        num_ans.setText(score + "");
        float percent = score/ num_ques * 100;
        sc_view.setText( percent + "%");
        Button portal = (Button)findViewById(R.id.main);
        portal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }
}
