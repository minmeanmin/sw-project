package com.example.naengjanggo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    private Button button1, button2, button3;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.rootLayout);

        button1 = findViewById(R.id.startButton);
        button2 = findViewById(R.id.registrationButton);
        button3 = findViewById(R.id.descriptionButton);

        TextView textView = findViewById(R.id.anotherTextView);
        String text = "냉장고를 \n부탁해";

        SpannableString spannableString = new SpannableString(text);
// "냉"이라는 글자에 지정된 색상 적용
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5414")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);


        button1.setOnClickListener(v -> {
                    Intent intent = new Intent(Main2Activity.this, FoodListActivity.class);
                    startActivity(intent);
                }
        );

        button2.setOnClickListener(v -> {
                    Intent intent = new Intent(Main2Activity.this, ConnectForm.class);
                    startActivity(intent);
                }
        );

        //나중에 설명서 페이지 만들어서 연결하기
        button3.setOnClickListener(v -> {
                    Intent intent = new Intent(Main2Activity.this, DescriptionActivity.class);
                    startActivity(intent);
                }
        );
    }

}