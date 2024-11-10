package com.example.naengjanggo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectedPageActivity extends AppCompatActivity {
    private Button homeButton;
    private View backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connected_page);

        // View 연결
        homeButton = findViewById(R.id.homeButton);
        backButton = findViewById(R.id.backButton);

        // 뒤로 가기 버튼 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectedPageActivity.this, ConnectForm.class);
                startActivity(intent);
            }
        });

        // 등록하기 버튼 클릭 이벤트
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 초기화면으로 돌아가도록 연결
                Intent intent = new Intent(ConnectedPageActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

    }
}
