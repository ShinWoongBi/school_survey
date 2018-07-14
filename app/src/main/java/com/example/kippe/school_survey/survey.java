package com.example.kippe.school_survey;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.kippe.school_survey.R.id.url;

public class survey extends Fragment {
    Uri mlmageCaptureUri = null;
    ListView listView;
    Adapter adapter;
    ArrayList<Data> datalist;
    ArrayList<Bitmap> bitmaps;
    ProgressDialog progressDialog;
    EditText title_E,explain_E,url_E;
    ImageButton imageButton;
    String file_path = "";
    Button button;

    String title = "",explain = "", url_T = "";

    boolean button_switch = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survey, container, false);


        verifyStoragePermissions(getActivity());

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new Adapter(getContext());
        datalist = new ArrayList<>();
        title_E = (EditText) view.findViewById(R.id.title);
        explain_E = (EditText)view.findViewById(R.id.explain);
        imageButton = (ImageButton)view.findViewById(R.id.imagebtn);
        url_E = (EditText)view.findViewById(url);
        button = (Button)view.findViewById(R.id.upload);
        bitmaps = new ArrayList<>();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button_switch) {

                    title = title_E.getText().toString();
                    explain = explain_E.getText().toString();
                    url_T = url_E.getText().toString();



                    (new Write_item()).execute();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("알림");
                builder.setMessage("삭제하시겠습니까?");
                builder.setNegativeButton("취소",null);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "삭제", Toast.LENGTH_SHORT).show();
                        Delete_survey delete_survey = new Delete_survey(datalist.get(position).num);
                        delete_survey.execute();
                    }
                });
                builder.show();
            }
        });



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
        explain_E.addTextChangedListener(new TextWatcher() {
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
        url_E.addTextChangedListener(new TextWatcher() {
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

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        Get_itme get_itme = new Get_itme();
        get_itme.execute();
    }




    void edit_text_check(){
        title = title_E.getText().toString();
        explain = explain_E.getText().toString();
        url_T = url_E.getText().toString();
        if(!title.equals("") && !explain.equals("") && !url_T.equals("") && !file_path.equals("")){
            button.setTextColor(Color.parseColor("#000000"));
            button_switch = true;
        }else{
            button.setTextColor(Color.parseColor("#ffffff"));
            button_switch = false;
        }
    }

    void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)return;

        switch (requestCode){
            case 1:
                mlmageCaptureUri = data.getData();
            case 0:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mlmageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 2);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 2);
                break;
            case 2:

                Bundle extras = data.getExtras();

                if(extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    imageButton.setImageBitmap(photo);

                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/school_survey";
                    File file = new File(dirPath);
                    String name = "COPY.jpg";
                    if(!file.exists())
                        file.mkdir();
                    File copy = new File(dirPath+"/."+name);
                    Log.e("copyfile", dirPath+"/."+name);
                    BufferedOutputStream out = null;
                    try {
                        copy.createNewFile();
                        out = new BufferedOutputStream(new FileOutputStream(copy));
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copy)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    file_path = dirPath+"/."+name;
                    edit_text_check();


                }


                break;
        }
    }



    class Get_itme extends AsyncTask<String, String, String> {



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("로딩중입니다..");

//            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {


            adapter.Delete_Item();
            Log.d("size123", datalist.size()+" asdf");
            Log.d("size123", bitmaps.size()+" asdf");

            URL url = null;
            HttpURLConnection httpURLConnection = null;


            try{

                url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/get_survey.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String buffer = "";
                while((buffer = bufferedReader.readLine()) != null){
                    Log.d("buffer", buffer);
                    String[] bufs = buffer.split("@@!@!@");
                    for(int i = 0; i < bufs.length; i++){
                        System.out.println(bufs[i]);
                    }

                    // bufs[0] = 제목  1 = 설명, 2 = 사진, 3 = 주소
                    adapter.Add_Item(bufs[2],bufs[0],bufs[1],bufs[3], Integer.parseInt(bufs[4]));

                }




            }catch (Exception e){
                e.printStackTrace();
            }




            try{

                Log.d("size", datalist.size()+"");
                for(int i = 0; i < datalist.size(); i++) {


                    url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/survey_item/"+datalist.get(i).picture);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);


                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap bitmap = null;
                    bitmap = BitmapFactory.decodeStream(inputStream);


                    bitmaps.add(bitmap);
                    Log.d("Add bitmap", i+"번");


                }

            }catch (Exception e){
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(adapter);


            Log.d("read", "end");


//            progressDialog.dismiss();
        }
    }


    class Write_item extends AsyncTask<String, String, String>{



        @Override
        protected void onProgressUpdate(String... values) {
//            progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setMessage("로딩중입니다..");
//            progressDialog.show();



            super.onProgressUpdate(values);

        }

        @Override
        protected String doInBackground(String... strings) {



            HttpURLConnection connection = null;
            URL url = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            File file = new File(file_path);
            if (!file.isFile()) {

            }else{


                int num = 0;
                try{

                    url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/upload_survey.php");
                    HttpURLConnection httpURLConnection;
                    String param = "title="+title+"&explain="+explain+"&url="+url_T;
                    Log.d("param", "title="+title+"&explain="+explain+"&url="+url_T);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);


                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(param.getBytes("UTF-8"));
                    outputStream.flush();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    num = Integer.parseInt(bufferedReader.readLine());

//                    String buffer = "";
//                    while((buffer = bufferedReader.readLine()) != null){
//                        Log.d("buffer", buffer);
//                    }


                }catch (Exception e){
                    e.printStackTrace();
                }





                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/upload_survey_image.php");

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", file_path);


                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
//                    outputStream.write(("mail="+sharedPreferences.getString("mail","")).getBytes("UTF-8"));
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""+num+"\""+lineEnd);
                    outputStream.writeBytes(lineEnd);

                    int available = fileInputStream.available(); // 파일 크기
                    int bufferSize = Math.min(available, 1 * 1024 * 1024); // 1M와 선택한 파일 크기 비교해서 작은쪽 반환
                    byte[] buffer = new byte[bufferSize];

                    while((fileInputStream.read(buffer, 0 ,bufferSize)) > 0){
                        outputStream.write(buffer, 0 ,bufferSize);
                        available = fileInputStream.available();
                        bufferSize = Math.min(available, 1*1024*1024);
                        buffer = new byte[bufferSize];
                        Log.e("asfd","asdf");
                    }
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    outputStream.flush();
                    outputStream.close();

                    File copy = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/school_survey/COPY.php");
                    copy.delete();


                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String a = reader.readLine();
                    Log.d("a", ":::" + a);
                    while((a=reader.readLine()) != null){
                        Log.d("a", ":::" + a);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            progressDialog.dismiss();
            super.onPostExecute(s);
            file_path = "";
            title_E.setText("");
            explain_E.setText("");
            url_E.setText("");
            imageButton.setImageBitmap(null);
            Toast.makeText(getActivity(), "업로드 완료!", Toast.LENGTH_SHORT).show();

            Get_itme get_itme = new Get_itme();
            get_itme.execute();
        }
    }


    class Delete_survey extends AsyncTask<String,String,String>{
        int num;

        Delete_survey(int num){
            this.num = num;
        }


        @Override
        protected String doInBackground(String... strings) {

            String param = "num="+num;
            Log.d("num", ":"+num);

            URL url = null;
            HttpURLConnection httpURLConnection = null;


            try{
                url = new URL("http://tlsdndql27.vps.phps.kr/taereung_school/delete_survey.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(param.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String buffer = "";
                while((buffer = bufferedReader.readLine()) != null){
                    Log.d("buffer",buffer);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Get_itme get_itme = new Get_itme();
            get_itme.execute();
        }
    }


    class Data {
        String picture;
        String title;
        String explain;
        String url;
        int num;


        Data(String picture, String title, String explain, String url, int num){
            this.picture =  picture;
            this.title = title;
            this.explain = explain;
            this.url = url;
            this.num = num;
        }
    }



    class Adapter extends BaseAdapter {
        Context context;

        Adapter(Context context){
            this.context = context;
        }

        public void Add_Item(String picture, String title, String explain, String url, int num){
            Data data = new Data(picture, title, explain, url, num);

            datalist.add(data);
        }

        public void Delete_Item(){
            datalist.clear();
            bitmaps.clear();
        }

        @Override
        public int getCount() {
            return datalist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.survey_listview_item, parent, false);
            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView explain = (TextView)convertView.findViewById(R.id.explain);

            imageView.setImageBitmap(bitmaps.get(position));
            title.setText(datalist.get(position).title);
            explain.setText(datalist.get(position).explain);


            return convertView;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
