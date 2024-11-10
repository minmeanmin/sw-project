package com.example.naengjanggo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

public class LoadingPageActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private View backButton;
    private BluetoothAdapter bluetoothAdapter;
    private String targetDeviceName;
    private BluetoothDevice targetDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private boolean isDiscovering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        backButton = findViewById(R.id.backButtonInLoadingPage);

        // 2초 뒤에 다음 화면으로 자동 전환
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(LoadingPageActivity.this, ConnectedPageActivity.class);
            startActivity(intent);
            finish(); // 현재 화면 종료
        }, 2000); // 2000 밀리초 = 2초

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 Activity를 종료하고 이전 화면으로 돌아감
            }
        });
    }

//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.loading_page);
//
//        backButton = findViewById(R.id.backButtonInLoadingPage);
//        targetDeviceName = Objects.requireNonNull(getIntent().getStringExtra("TRAY_ID")).trim();
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (bluetoothAdapter == null) {
//            showToast("이 기기는 블루투스를 지원하지 않습니다. 앱을 종료합니다.");
//            finish();
//        } else {
//            checkPermissionsAndRequest();
//        }
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // 현재 Activity를 종료하고 이전 화면으로 돌아감
//            }
//        });
//    }

//    private void checkPermissionsAndRequest() {
//        // 권한 상태 확인(권한 있으면 true, 없으면 false)
//        boolean bluetoothPermissions = hasPermissions(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN);
//        boolean locationPermission = hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // 권한 요청 분기
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (!bluetoothPermissions && !locationPermission) { // Android 12 이상 + Bluetooth와 위치 권한이 모두 없는 경우
//                requestPermissions(new String[]{
//                        Manifest.permission.BLUETOOTH_CONNECT,
//                        Manifest.permission.BLUETOOTH_SCAN,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                });
//            } else if (!bluetoothPermissions) { // Android 12 이상 +  Bluetooth 권한만 없는 경우
//                requestPermissions(new String[]{
//                        Manifest.permission.BLUETOOTH_CONNECT,
//                        Manifest.permission.BLUETOOTH_SCAN
//                });
//            } else if (!locationPermission) { // Android 12 이상 + 위치 권한만 없는 경우
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
//            } else { // 모든 권한이 있는 경우
//                startBluetoothDiscovery();
//            }
//        } else {
//            if (!locationPermission) { // Android 12 미만: 위치 권한만 필요한 경우 요청
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
//            } else { // 모든 권한이 있는 경우
//                startBluetoothDiscovery();
//            }
//        }
//    }
//
//    private boolean hasPermissions(String... permissions) {
//        for (String permission : permissions) {
//            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void requestPermissions(String[] permissions) {
//        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSIONS);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
//            if (allPermissionsGranted(grantResults)) {
//                startBluetoothDiscovery();
//            } else {
//                showToast("앱에서 Bluetooth와 위치 권한이 필요합니다. 설정에서 권한을 허용한 후 다시 시도해주세요.");
//                finish();
//            }
//        }
//    }
//
//    private boolean allPermissionsGranted(int[] grantResults) {
//        for (int result : grantResults) {
//            if (result != PackageManager.PERMISSION_GRANTED) return false;
//        }
//        return true;
//    }
//
//    private void startBluetoothDiscovery() {
//        if (!isDiscovering && hasPermissions(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)) {
//            isDiscovering = true;
//            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
//                bluetoothAdapter.startDiscovery();
//                showToast("Bluetooth 장치 검색을 시작합니다...");
//            } else {
//                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN});
//            }
//        }
//    }
//
//    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) { // bluetooth 장치가 검색될 때
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // 발견된 장치 정보 추출
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                    if (device != null && targetDeviceName.equals(device.getName())) { // 목표 장치가 발견되었을 때 실행
//                        targetDevice = device;
//                        bluetoothAdapter.cancelDiscovery();
//                        isDiscovering = false;
//                        showToast("목표 장치(" + device.getName() + ")를 찾았습니다. 연결을 시도합니다.");
//                        connectToDevice();
//                    }
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT});
//                }
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && isDiscovering) {
//                // 검색 완료 시 검색이 이미 중단되지 않은 상태(isDiscovering이 true일 때)만 실행
//                isDiscovering = false;
//                showToast("목표 장치를 찾지 못했거나 연결이 실패했습니다. 다시 시도해 주세요.");
//            }
//        }
//    };
//
//
//    private void connectToDevice() {
//        new Thread(() -> {
//            try {
//                // 권한 확인
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT});
//                    return;
//                }
//
//                // 소켓 생성 및 연결 시도
//                bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                bluetoothAdapter.cancelDiscovery();
//                bluetoothSocket.connect();
//
//                // 스트림 초기화
//                outputStream = bluetoothSocket.getOutputStream();
//
//                // UI 업데이트 및 데이터 전송
//                runOnUiThread(() -> {
//                    showToast("목표 장치와 성공적으로 연결되었습니다!");
//                    sendDataToDevice("연결 성공"); // 연결 후 데이터 전송
//                    BluetoothConnectionManager.getInstance().setOutputStream(outputStream);
//                    // TODO: 연결된 Device를 DB에 저장 or localStorage에 저장 (나중에 냉장고 트레이 선택 화면에서 선택하기 위해)
//                    Intent intent = new Intent(LoadingPageActivity.this, ConnectedPageActivity.class);
//                    startActivity(intent);
//                });
//
//            } catch (IOException e) {
//                // 예외 처리 및 소켓 닫기
//                runOnUiThread(() -> showToast("목표 장치와 연결할 수 없습니다. Bluetooth를 확인한 후 다시 시도해 주세요."));
//                closeSocket();
//            }
//        }).start();
//    }
//
//    // 데이터 전송 메서드
//    private void sendDataToDevice(String data) {
//        if (outputStream != null) {
//            try {
//                outputStream.write(data.getBytes());
//                outputStream.flush();
////                runOnUiThread(() -> showToast("데이터 전송: " + data));
//            } catch (IOException e) {
////                runOnUiThread(() -> showToast("데이터 전송 실패"));
//                e.printStackTrace();
//            }
//        } else {
//            showToast("연결된 장치가 없습니다.");
//        }
//    }
//
//    private void closeSocket() {
//        try {
//            if (bluetoothSocket != null) bluetoothSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onDestroy() { // 로딩 페이지 파괴할 때 사용
//        super.onDestroy();
//        if (isDiscovering) { // 장치 검색이 진행 중인지 확인
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
//                bluetoothAdapter.cancelDiscovery(); // 검색 중단
//            }
//        }
//    }

//    // bluetooth 해지할 때 사용
//    private void unregisterReceiverSafely(BroadcastReceiver receiver) {
//        try {
//            unregisterReceiver(receiver);
//        } catch (IllegalArgumentException ignored) {
//        }
//    }

}
