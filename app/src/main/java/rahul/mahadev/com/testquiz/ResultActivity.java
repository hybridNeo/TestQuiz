package rahul.mahadev.com.testquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ResultActivity extends AppCompatActivity {
    double totalPercent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent i = getIntent();
        float num_ques = Integer.parseInt(i.getStringExtra("num"));
        int score = Integer.parseInt(i.getStringExtra("score"));
        TextView num_ans = (TextView) findViewById(R.id.numAnswered);
        TextView sc_view = (TextView)findViewById(R.id.Score);
        String qname = i.getStringExtra("name");
        System.out.println("Quiz name is " + qname);

        num_ans.setText(score + "");
        double percent = score/ num_ques * 100;
        double totalPercent = (Math.round(percent * 100.0) / 100.0);
        writeResult(qname,totalPercent);
        sc_view.setText( totalPercent + "%");
        Button portal = (Button)findViewById(R.id.main);
        portal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void writeResult(String qname, double totalPercent){
        try{
            File newfile = new File(getApplicationContext().getFilesDir(), "result");
            FileWriter fw = new FileWriter(newfile,true);
            fw.write(qname+":" + totalPercent + ";");
            fw.close();
        }catch (IOException e){

        }

    }
    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }
}
