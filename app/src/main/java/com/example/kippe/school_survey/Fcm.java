package com.example.kippe.school_survey;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kippe on 2017-04-28.
 */

public class Fcm extends Fragment {
    boolean button_switch = false;
    Button push_btn;
    EditText title_E, message_E;
    String title, message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fcm, container, false);

        push_btn = (Button) view.findViewById(R.id.push_btn);
        title_E = (EditText)view.findViewById(R.id.title);
        message_E = (EditText)view.findViewById(R.id.message);

        title_E.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_text_check();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        message_E.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_text_check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        push_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(button_switch) {


                    final String f_title = title;
                    final String f_message = message;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                String param = "title=" + title + "&message=" + message;
                                URL url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/push_message.php");
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);

                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                outputStream.write(param.getBytes("UTF-8"));
                                outputStream.flush();
                                outputStream.close();

                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String buffer = "";
                                while ((buffer = bufferedReader.readLine()) != null) {
                                    Log.d("buffer", buffer);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                    title_E.setText("");
                    message_E.setText("");
                }
            }
        });

        return view;
    }

    void edit_text_check(){
        title = title_E.getText().toString();
        message = message_E.getText().toString();

        if(!title.equals("") && !message.equals("")){
            push_btn.setTextColor(Color.parseColor("#000000"));
            button_switch = true;
        }else{
            push_btn.setTextColor(Color.parseColor("#ffffff"));
            button_switch = false;
        }
    }
}
