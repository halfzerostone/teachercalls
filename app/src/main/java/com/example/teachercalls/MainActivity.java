package com.example.teachercalls;


import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private DatabaseReference dataRef;
    private ValueEventListener valueEventListener;
    private static final String CHANNEL_ID = "my_channel_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.TextView);

        // Firebase Realtime Database 데이터 읽기
        String databaseUrl = "https://teachercalls-default-rtdb.firebaseio.com/";
        String dataPath = "4";
        String apiUrl = databaseUrl + dataPath;

        // Firebase Realtime Database 데이터 요청
        // 백그라운드 스레드에서 네트워크 요청을 수행해야 합니다.
        dataRef = FirebaseDatabase.getInstance().getReferenceFromUrl(apiUrl);

        // ValueEventListener 등록
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 변경 이벤트 발생 시 호출됨
                String data = dataSnapshot.getValue(String.class);
                if (data != null) {
                    // 데이터를 TextView에 설정
                    textView.setText(data);
                    NotificationHelper.showNotification(MainActivity.this,"선생님이 부르신다",data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
                System.out.println("Firebase 데이터 읽기 에러: " + databaseError.getMessage());
            }
        };

        // ValueEventListener를 Firebase Realtime Database 참조에 연결
        dataRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ValueEventListener 제거
        if (dataRef != null && valueEventListener != null) {
            dataRef.removeEventListener(valueEventListener);
        }
    }
}