package com.example.kippe.school_survey;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
//    Uri mlmageCaptureUri = null;
//    ListView listView;
//    Adapter adapter;
//    ArrayList<Data> datalist;
//    ArrayList<Bitmap> bitmaps;
//    ProgressDialog progressDialog;
//    EditText title_E,explain_E,url_E;
//    ImageButton imageButton;
//    String file_path = "";
//    Button button;
//
//    String title = "",explain = "", url_T = "";
//
//    boolean button_switch = false;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(MainActivity.this);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        final ArrayList<Fragment> arrayList = new ArrayList<>();
        arrayList.add(new Fcm());
        arrayList.add(new survey());


        viewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return arrayList.get(position);
            }

            @Override
            public int getCount() {
                return arrayList.size();
            }
        });


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("알림");
        tabLayout.getTabAt(1).setText("설문 조사");


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
