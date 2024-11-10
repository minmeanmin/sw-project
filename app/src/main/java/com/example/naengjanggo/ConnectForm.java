// ConnectForm.java
package com.example.naengjanggo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConnectForm extends AppCompatActivity {

    private EditText trayIdEditText;
    private Button registerButton;
    private View backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_form);

        trayIdEditText = findViewById(R.id.trayIdEditText);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        registerButton.setOnClickListener(v -> {
            String trayId = trayIdEditText.getText().toString();
            if (!trayId.isEmpty()) {
                Intent intent = new Intent(ConnectForm.this, LoadingPageActivity.class);
                intent.putExtra("TRAY_ID", trayId);
                startActivity(intent);
            } else {
                Toast.makeText(ConnectForm.this, "트레이 ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
