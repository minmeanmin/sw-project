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

public class MainActivity extends AppCompatActivity {

    private Button button1, button2, button3;
    private RelativeLayout rootLayout;
    private TextView splashText;
    private static final long INITIAL_DELAY = 600; // 1초 초기 지연
    private static final long ANIMATION_DURATION = 800; // 1.5초 애니메이션 지속 시간
    private static final long BACKGROUND_DELAY = 900; // 1초 배경 변경 지연
    private static final long COLOR_CHANGE_DURATION = 700; // 1초 색상 변경 애니메이션 지속 시간
    private static final int HIGHLIGHT_COLOR = Color.parseColor("#FF5414");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        rootLayout = findViewById(R.id.rootLayout);
        splashText = findViewById(R.id.splashText);

        button1 = findViewById(R.id.startButton);
        button2 = findViewById(R.id.registrationButton);
        button3 = findViewById(R.id.descriptionButton);

        // 초기 상태 설정
        rootLayout.setBackgroundColor(Color.BLACK);
        splashText.setTextColor(Color.WHITE);

        // 애니메이션 시작을 지연
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        }, INITIAL_DELAY);

        // 애니메이션 끝난 후 버튼 등장 애니메이션
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showButtons();
            }
        }, COLOR_CHANGE_DURATION + ANIMATION_DURATION);

        button1.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, FoodListActivity.class);
                    startActivity(intent);
                }
        );

        button2.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ConnectForm.class);
                    startActivity(intent);
                }
        );

        //나중에 설명서 페이지 만들어서 연결하기
        button3.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
                startActivity(intent);
            }
        );
    }

    private void showButtons() {
        // 버튼 1 애니메이션
        ObjectAnimator button1Animator = ObjectAnimator.ofFloat(button1, "alpha", 0f, 1f);
        button1Animator.setDuration(2000);
        button1Animator.setStartDelay(1000);
        // 버튼 2 애니메이션
        ObjectAnimator button2Animator = ObjectAnimator.ofFloat(button2, "alpha", 0f, 1f);
        button2Animator.setDuration(2000);
        button2Animator.setStartDelay(1000);

        ObjectAnimator button3Animator = ObjectAnimator.ofFloat(button3, "alpha", 0f, 1f);
        button3Animator.setDuration(2000);
        button3Animator.setStartDelay(1000);

        // 애니메이션 시작
        button1Animator.start();
        button2Animator.start();
        button3Animator.start();
    }

    private void startAnimation() {
        // 텍스트 위로 올리기 애니메이션
        ObjectAnimator moveUpAnimator = ObjectAnimator.ofFloat(splashText, "translationY", 0f, -450f);
        moveUpAnimator.setDuration(ANIMATION_DURATION);
        moveUpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // 텍스트 색상 및 그림자 색상 변경 애니메이션
        ValueAnimator textColorAnimator = ValueAnimator.ofArgb(Color.WHITE, Color.BLACK);
        textColorAnimator.setDuration(ANIMATION_DURATION);
        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentColor = (int) animator.getAnimatedValue();
                splashText.setTextColor(currentColor);

                // 그림자 색상 변경 (투명도 25%)
                int shadowColor = interpolateColor(Color.WHITE, Color.BLACK, animator.getAnimatedFraction());
                shadowColor = (shadowColor & 0x00FFFFFF) | (0x40 << 24); // 알파 값 25% 적용
                splashText.setShadowLayer(1.5f, 3.0f, 5.0f, shadowColor);
            }
        });

        // 배경색 변경 애니메이션
        ValueAnimator backgroundColorAnimator = ValueAnimator.ofArgb(Color.BLACK, Color.parseColor("#FCFBF7"));
        backgroundColorAnimator.setDuration(ANIMATION_DURATION);
        backgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rootLayout.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        // 애니메이션 세트 생성
        AnimatorSet animatorSet = new AnimatorSet();

        // 텍스트 애니메이션 먼저 시작, 1초 후 배경 애니메이션 시작
        animatorSet.play(moveUpAnimator).with(textColorAnimator);
        animatorSet.play(backgroundColorAnimator).after(BACKGROUND_DELAY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateFirstCharacterColor();
            }
        });

        animatorSet.start();
    }

    private void animateFirstCharacterColor() {
        final String text = splashText.getText().toString();
        final SpannableString spannableString = new SpannableString(text);

        ValueAnimator colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration(COLOR_CHANGE_DURATION);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = animator.getAnimatedFraction();
                int currentColor = interpolateColor(Color.BLACK, HIGHLIGHT_COLOR, fraction);

                spannableString.removeSpan(ForegroundColorSpan.class);
                spannableString.setSpan(new ForegroundColorSpan(currentColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                splashText.setText(spannableString);
            }
        });
        colorAnimator.start();
    }

    private int interpolateColor(int startColor, int endColor, float fraction) {
        int startA = (startColor >> 24) & 0xff;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;

        int endA = (endColor >> 24) & 0xff;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
}