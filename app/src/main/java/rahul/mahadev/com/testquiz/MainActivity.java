package rahul.mahadev.com.testquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean internetPresent = cd.isConnectingToInternet();
        if(internetPresent){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://192.168.0.8/main.json",null, new JsonHttpResponseHandler(){
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    ArrayList<String> quiz_names = new ArrayList<String>();
                    final ArrayList<String> urls = new ArrayList<String>();
                    ArrayAdapter<String> listAdapter;
                    // Pull out the first event on the public timeline
                    try {
                        File file = new File(getApplicationContext().getFilesDir(), "main");
                        FileWriter fw = new FileWriter(file);
                        fw.write(timeline.toString());
                        fw.close();
                        for (int i = 0; i < timeline.length(); ++i) {


                            JSONObject temp = timeline.getJSONObject(i);
                            String quiz_name = temp.getString("quiz_name");
                            String link = temp.getString("link");
                            System.out.println("QUIZ_NAME:" + quiz_name);
                            quiz_names.add(quiz_name);
                            urls.add(link);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                        System.out.println("FILE error");
                    }
                    finally {
                        ListView mainListView = (ListView) findViewById(R.id.mainListView);
                        listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simplerow, quiz_names);

                        mainListView.setAdapter(listAdapter);
                        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Log.d("Test", "position" + urls.get(position));
                                String link = urls.get(position);
                                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                                intent.putExtra("link", link);
                                startActivity(intent);

                            }
                        });

                        AsyncHttpClient dlClient = new AsyncHttpClient();

                        for(int i = 0; i < urls.size();++i){
                            String[] arr = urls.get(i).split("/");
                            final String temp = arr[arr.length -1];

                            dlClient.get(urls.get(i),null, new JsonHttpResponseHandler() {
                                public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {

                                    try{
                                        File newfile = new File(getApplicationContext().getFilesDir(), temp);
                                        System.out.println("Link is " + temp);
                                        System.out.println(timeline.toString());
                                        FileWriter fw = new FileWriter(newfile);
                                        fw.write(timeline.toString());
                                        fw.close();

                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);


                    }
                }
            });
        }else{
            Log.d("TEST","INTERNET not there");
            File file = new File(getApplicationContext().getFilesDir(), "main");
            ArrayList<String> quiz_names = new ArrayList<String>();
            final ArrayList<String> urls = new ArrayList<String>();
            ArrayAdapter<String> listAdapter;
            try{
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while ((line = bufferedReader.readLine()) != null)
                {
                   sb.append(line);
                }
                String content = sb.toString();

                JSONArray timeline = new JSONArray(content);
                for (int i = 0; i < timeline.length(); ++i) {


                    JSONObject temp = timeline.getJSONObject(i);
                    String quiz_name = temp.getString("quiz_name");
                    String link = temp.getString("link");
                    System.out.println("QUIZ_NAME:" + quiz_name);
                    quiz_names.add(quiz_name);
                    urls.add(link);
                }
                System.out.println(content);
            }catch (IOException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            finally {
                ListView mainListView = (ListView) findViewById(R.id.mainListView);
                listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simplerow, quiz_names);
                mainListView.setAdapter(listAdapter);
                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Log.d("Test", "position" + urls.get(position));
                        String link = urls.get(position);
                        Intent intent = new Intent(getApplicationContext(),QuizActivity.class);
                        intent.putExtra("link",link);
                        startActivity(intent);

                    }
                });
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }
        }


    }
}
