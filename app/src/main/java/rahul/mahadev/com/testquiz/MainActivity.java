package rahul.mahadev.com.testquiz;

import android.content.Context;
import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import javax.security.auth.x500.X500Principal;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public void test(){
        Context ctx = getApplicationContext();
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.YEAR,1);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                .setAlias("key1")
                .setSubject(
                        new X500Principal(String.format("CN=%s, OU=%s", "sample",
                                ctx.getPackageName())))
                .setSerialNumber(BigInteger.ONE).setStartDate(notBefore.getTime())
                .setEndDate(notAfter.getTime()).build();

        try{
            KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            kpGenerator.initialize(spec);
            KeyPair kp = kpGenerator.generateKeyPair();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
        catch(NoSuchProviderException e)
        {
            e.printStackTrace();
        }
        KeyStore keyStore = null;
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("key1", null);
            RSAPublicKey pubKey = (RSAPublicKey)keyEntry.getCertificate().getPublicKey();
            RSAPrivateKey privKey = (RSAPrivateKey) keyEntry.getPrivateKey();
            Toast.makeText(getApplicationContext(),privKey.toString(),Toast.LENGTH_LONG).show();
        }catch (KeyStoreException e){
            e.printStackTrace();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(CertificateException e){
            e.printStackTrace();
        }catch(UnrecoverableEntryException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        final HashMap maxVals = getMarks();
        if(maxVals != null)
            System.out.println("MAXVALS "+ maxVals.toString());

        //Call test function
        //test();
        Boolean internetPresent = cd.isConnectingToInternet();
        if(internetPresent){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://192.168.0.8/main.json",null, new JsonHttpResponseHandler(){
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    final ArrayList<String> quiz_names = new ArrayList<String>();
                    final ArrayList<String> urls = new ArrayList<String>();
                    CustomAdapter listAdapter;
                    ArrayList<CustomObject> objects = new ArrayList<CustomObject>();

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
                            String max = "";
                            if(maxVals != null ){
                                if(maxVals.containsKey(quiz_name)){

                                    max = "Best Score = " + maxVals.get(quiz_name) + "%";

                                }
                            }
                            objects.add(new CustomObject(quiz_name,max));
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

                        listAdapter = new CustomAdapter(getApplicationContext(),objects );

                        mainListView.setAdapter(listAdapter);
                        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Log.d("Test", "position" + urls.get(position));
                                String link = urls.get(position);
                                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                                intent.putExtra("name",quiz_names.get(position));
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
            final ArrayList<String> quiz_names = new ArrayList<String>();
            final ArrayList<String> urls = new ArrayList<String>();
            CustomAdapter listAdapter;
            ArrayList<CustomObject> objects = new ArrayList<CustomObject>();

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
                    String max = "";
                    if(maxVals != null ){
                        if(maxVals.containsKey(quiz_name)){

                            max = "Best Score = " + maxVals.get(quiz_name) + "%";

                        }
                    }
                    objects.add(new CustomObject(quiz_name,max));
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
                listAdapter = new CustomAdapter(getApplicationContext(),objects );
                mainListView.setAdapter(listAdapter);
                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Log.d("Test", "position" + urls.get(position));
                        String link = urls.get(position);
                        Intent intent = new Intent(getApplicationContext(),QuizActivity.class);
                        intent.putExtra("link",link);
                        intent.putExtra("name",quiz_names.get(position));
                        startActivity(intent);

                    }
                });
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }
        }


    }
    private HashMap getMarks(){
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            File f = new File(getApplicationContext().getFilesDir(), "result");
            if(f.exists()){
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String content = sb.toString();
                String[] lines = content.split(";");
                HashMap<String,Double> hm = new HashMap<String,Double>();
                for(int i = 0;i < lines.length;++i){
                    String[] resPair = lines[i].split(":");
                    if(hm.containsKey(resPair[0])){
                        double curScore = hm.get(resPair[0]);
                        double newScore = Double.parseDouble(resPair[1]);
                        if(newScore > curScore){
                            hm.remove(resPair[0]);
                            hm.put(resPair[0],newScore);
                        }
                    }else{
                        hm.put(resPair[0],Double.parseDouble(resPair[1]));
                    }
                }
                return hm;
            }else{
                System.out.println("File does not exist\n");

            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
