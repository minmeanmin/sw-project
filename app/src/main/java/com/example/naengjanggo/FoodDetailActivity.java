package com.example.naengjanggo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class FoodDetailActivity extends AppCompatActivity {

    private static String IP_ADDDRESS = "10.0.2.2";

    private TextView productNameText, expirationDateText, memoText;
    private EditText productNameEdit, expirationDateEdit, memoEdit;
    private Button editButton, saveButton;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        productNameText = findViewById(R.id.productNameText);
        expirationDateText = findViewById(R.id.expirationDateText);
        memoText = findViewById(R.id.memoText);
        productNameEdit = findViewById(R.id.productNameEdit);
        expirationDateEdit = findViewById(R.id.expirationDateEdit);
        memoEdit = findViewById(R.id.memoEdit);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);

        // Set up navigation
        backButton.setOnClickListener(v -> onBackPressed());

        // Get data from intent
        String productName = getIntent().getStringExtra("PRODUCT_NAME");
        if (productName != null) {
            productNameText.setText(productName);
            productNameEdit.setText(productName);
        }

        // Set initial text for other fields
        expirationDateText.setText("2024.11.07");
        expirationDateEdit.setText("2024.11.07");
        memoText.setText(getString(R.string.memo_content));
        memoEdit.setText(getString(R.string.memo_content));

        // Set up edit and save buttons
        editButton.setOnClickListener(v -> setEditMode(true));
        saveButton.setOnClickListener(v -> {

            setEditMode(false);

            // 사용자 입력 데이터 가져오기
            // String productName = productNameEdit.getText().toString();
            String expirationDate = expirationDateEdit.getText().toString();
            String memo = memoEdit.getText().toString();
            String trayId = "1"; // 필요에 따라 트레이 ID 설정 (예: 하드코딩 또는 동적으로 설정)

            // InsertData 객체 생성 및 execute 메서드 호출
            InsertData task = new InsertData();
            task.execute("http://" + IP_ADDDRESS + "/hello.php", productName, expirationDate, memo, trayId);
    });

        // Set up location grid click listeners
        CardView[] locationCards = {
                findViewById(R.id.locationCard1),
                findViewById(R.id.locationCard2),
                findViewById(R.id.locationCard3),
                findViewById(R.id.locationCard4)
        };

        for (CardView card : locationCards) {
            card.setOnClickListener(v -> {
                // Reset all cards
                for (CardView c : locationCards) {
                    c.setCardBackgroundColor(getResources().getColor(R.color.white));
                }
                // Highlight selected card
                card.setCardBackgroundColor(getResources().getColor(R.color.selected_color));
                sendCommandToMotor('1');
            });
        }
    }

    private void sendCommandToMotor(char command) {
        OutputStream outputStream = BluetoothConnectionManager.getInstance().getOutputStream();
        if (outputStream != null) {
            try {
                outputStream.write(command);
                Toast.makeText(this, "명령 전송됨: " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Bluetooth", "명령 전송 실패", e);
                Toast.makeText(this, "명령 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        productNameText.setVisibility(editMode ? View.GONE : View.VISIBLE);
        expirationDateText.setVisibility(editMode ? View.GONE : View.VISIBLE);
        memoText.setVisibility(editMode ? View.GONE : View.VISIBLE);
        productNameEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        expirationDateEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        memoEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        editButton.setVisibility(editMode ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(editMode ? View.VISIBLE : View.GONE);

        if (!editMode) {
            // Save the edited text
            productNameText.setText(productNameEdit.getText().toString());
            expirationDateText.setText(expirationDateEdit.getText().toString());
            memoText.setText(memoEdit.getText().toString());
        }
    }

    class InsertData extends AsyncTask<String,Void,String> { // 통신을 위한 InsertData 생성
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            progressDialog = ProgressDialog.show(FoodDetailActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss(); //onPostExcute 에 오게되면 진행 다이얼로그 취소
        }


        @Override
        protected String doInBackground(String... params) {
            /*
            PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비한다.
            POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않는다.
            이 값들은 InsertData 객체.excute 에서 매개변수로 준 값들이 배열 개념으로 차례대로 들어가
            값을 받아오는 개념이다.
             */
            Log.d("db parameter: ", Arrays.toString(params));
            String serverURL =  params[0];

            String product_name = params[1];
            String product_exp = params[2];
            String product_memo = params[3];
            String tray_id = params[4];

            /*
            HTTP 메세지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야한다.
            전송할 데이터는 "이름=값" 형식이며 여러 개를 보내야 할 경우에 항목 사이에 &를 추가해준다.
            여기에 적어준 이름들은 나중에 PHP에서 사용하여 값을 얻게 된다.
             */
            String postParameters ="product_name="+product_name+"&product_exp="+ product_exp
                    +"&product_memo="+product_memo+"&tray_id="+tray_id;

            try{ // HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송한다.
                URL url = new URL(serverURL); //주소가 저장된 변수를 이곳에 입력한다.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생한다.

                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생한다.

                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 한다.

                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();

                //전송할 데이터가 저장된 변수를 이곳에 입력한다. 인코딩을 고려해줘야 하기 때문에 UTF-8 형식으로 넣어준다.
                outputStream.write(postParameters.getBytes("UTF-8"));

                Log.d("php postParameters_데이터 : ",postParameters); //postParameters의 값이 정상적으로 넘어왔나 Log를 찍어줬다.

                outputStream.flush();//현재 버퍼에 저장되어 있는 내용을 클라이언트로 전송하고 버퍼를 비운다.
                outputStream.close(); //객체를 닫음으로써 자원을 반납한다.

                int responseStatusCode = httpURLConnection.getResponseCode(); //응답을 읽는다.

                InputStream inputStream;

                if(responseStatusCode == httpURLConnection.HTTP_OK){ //만약 정상적인 응답 데이터 라면
                    inputStream=httpURLConnection.getInputStream();
                    Log.d("php정상: ","정상적으로 출력"); //로그 메세지로 정상적으로 출력을 찍는다.
                }
                else {
                    Log.d("inputstream: ", ""+httpURLConnection.getErrorStream());
                    inputStream = httpURLConnection.getErrorStream(); //만약 에러가 발생한다면
                    Log.d("php비정상: ","비정상적으로 출력"); // 로그 메세지로 비정상적으로 출력을 찍는다.
                }

                // StringBuilder를 사용하여 수신되는 데이터를 저장한다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) !=null ) {
                    sb.append(line);
                }

                bufferedReader.close();

                Log.d("php 값 :", sb.toString());

                //저장된 데이터를 스트링으로 변환하여 리턴값으로 받는다.
                return  sb.toString();
            }

            catch (Exception e) {
                Log.e("db 연결 에러: ", "Error " + e.getMessage(),e);
                return "Error " + e.getMessage();

            }

        }
    }
}