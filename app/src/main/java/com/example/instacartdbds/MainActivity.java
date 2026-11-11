package com.example.instacartdbds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RadioGroup rgroup;
    EditText queryText;
    RadioButton rbutton;
    TextView answerText;
    TextView timeelapsed;
    TableLayout tableLayout;
    RadioGroup rgroup2;
    RadioButton rbutton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgroup = findViewById(R.id.radiogroup);
        queryText = findViewById(R.id.query);
        Button runButton = findViewById(R.id.button);
        answerText = findViewById(R.id.answer);
        timeelapsed = findViewById(R.id.timeelapsed);
        tableLayout = findViewById(R.id.tableLayout1);
        rgroup2 = findViewById(R.id.radiogroup2);
        answerText.setText("");
        timeelapsed.setText("");


        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioId = rgroup.getCheckedRadioButtonId();
                rbutton = findViewById(radioId);
                CharSequence rbuttonText = rbutton.getText().toString();
                String query = queryText.getText().toString();
                OkHttpClient okHttpClient = new OkHttpClient();
                tableLayout.removeAllViews();
                String url = "http://18.212.76.162:5000/send_query";

                int radioId2 = rgroup2.getCheckedRadioButtonId();
                rbutton2 = findViewById(radioId2);
                CharSequence rbuttonText2 = rbutton2.getText().toString();




                MediaType JSON = MediaType.parse("application/json");
                JSONObject data = new JSONObject();
                try {
                    data.put("database", rbuttonText);
                    data.put("query", query);
                    data.put("database_name",rbuttonText2);
                } catch (JSONException e){

                    Log.d("HTTP3", "onClick: database put error.");
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, data.toString());
                Request request = new Request.Builder().url(url).post(body).build();

                long start = System.currentTimeMillis();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        e.printStackTrace();
                        answerText.setText(e.toString());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            answerText.setText("");
                            String myresponse = response.body().string();
                            try {
                                JSONArray resp = new JSONArray(myresponse);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        long end = System.currentTimeMillis();
                                        //answerText.setText(myresponse);

                                        try {
                                            int rows = resp.length();
                                            Log.d("info",String.valueOf(rows));
                                            int cols = resp.getJSONArray(0).length();
                                            Log.d("info",String.valueOf(cols));
                                            createTable(rows, cols, resp);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        long elapsed = (end - start);
                                        timeelapsed.setText(String.valueOf(elapsed) + " ms");

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            String myresponse = response.body().string();
                            System.out.println(myresponse);
                            answerText.setText(myresponse);
                        }

                    }
                });




            }
        });



    }

    private void createTable(int rows, int cols, JSONArray jsonObj) throws JSONException {
        for (int i = 0; i <rows; i++) {

            TableRow row = new TableRow(this);
            JSONArray array = jsonObj.getJSONArray(i);
//            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                    TableRow.LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 0; j <cols; j++) {

                TextView tv = new TextView(this);
               // tv.setLayoutParams(new .LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                 //       TableRow.LayoutParams.WRAP_CONTENT));
                //tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setPadding(5, 5, 5, 5);

                tv.setText(array.getString(j));

                row.addView(tv);

            }

            tableLayout.addView(row);

        }
    }

    public void checkButton(View v){
        int radioID = rgroup.getCheckedRadioButtonId();
        rbutton = findViewById(radioID);
        Toast.makeText(this, "Selected Radio Button: " + rbutton.getText(),Toast.LENGTH_SHORT).show();

    }
}