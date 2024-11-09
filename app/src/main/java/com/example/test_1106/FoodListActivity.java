package com.example.test_1106;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_main);

        View backButton = findViewById(R.id.backButtonInFoodListPage);
        Button tray1Button = findViewById(R.id.tray1selectButtonInFoodListPage);
        Button tray2Button = findViewById(R.id.tray2selectButtonInFoodListPage);
        Button tray3Button = findViewById(R.id.tray3selectButtonInFoodListPage);
        Button tray4Button = findViewById(R.id.tray4selectButtonInFoodListPage);
        Button tray5Button = findViewById(R.id.tray5selectButtonInFoodListPage);

        // 뒤로 가기 버튼 이벤트
        backButton.setOnClickListener(v -> finish());

//        tray1Button.setTag(""); // DB에서 목록 불어와서 태그값 등록할 때 사용
        tray1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tag에 있는 tray 이름(=bluetooth 이름) 읽기
                String trayId = (String) v.getTag(); // TODO: bluetooth 이름 tray1752로 바꾸기
                Intent intent = new Intent(FoodListActivity.this, LoadingPageActivity.class); // TODO: 나중에 민주 화면으로 이동하도록
                intent.putExtra("TRAY_ID", trayId);
                Toast.makeText(FoodListActivity.this, "선택한 트레이: " + trayId, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
