package com.example.test_1106;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_main);

        View backButton = findViewById(R.id.backButtonInFoodListPage);
        View tray1Button = findViewById(R.id.tray1selectButtonInFoodListPage);
        View tray2Button = findViewById(R.id.tray2selectButtonInFoodListPage);
        View tray3Button = findViewById(R.id.tray3selectButtonInFoodListPage);
        View tray4Button = findViewById(R.id.tray4selectButtonInFoodListPage);
        View tray5Button = findViewById(R.id.tray5selectButtonInFoodListPage);

        // 뒤로 가기 버튼 이벤트
        backButton.setOnClickListener(v -> finish());


    }
}
