package com.example.test_1106;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class LoadingPageActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private BluetoothAdapter bluetoothAdapter;
    private String targetDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        // MainActivity에서 전달받은 장치 이름 가져오기
        targetDeviceName = Objects.requireNonNull(getIntent().getStringExtra("TRAY_ID")).trim();
        Toast.makeText(this, "장치 이름 수신: " + targetDeviceName, Toast.LENGTH_SHORT).show();

        // BluetoothAdapter 초기화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Toast.makeText(this, "BluetoothAdapter 초기화 성공", Toast.LENGTH_SHORT).show();

        // Bluetooth 권한 확인 및 요청
        checkBluetoothPermissions();
    }

    // Bluetooth 권한을 확인하고 필요한 경우 요청
    private void checkBluetoothPermissions() {
        Toast.makeText(this, "Bluetooth 권한 확인 시작", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_BLUETOOTH_PERMISSIONS);
                Toast.makeText(this, "Bluetooth 권한 요청 완료", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Bluetooth 권한 이미 있음", Toast.LENGTH_SHORT).show();
                startBluetoothDiscovery(); // 권한이 이미 있는 경우 Bluetooth 검색 시작
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_BLUETOOTH_PERMISSIONS);
                Toast.makeText(this, "위치 권한 요청 완료", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "위치 권한 이미 있음", Toast.LENGTH_SHORT).show();
                startBluetoothDiscovery(); // 권한이 이미 있는 경우 Bluetooth 검색 시작
            }
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Toast.makeText(this, "모든 권한 승인됨", Toast.LENGTH_SHORT).show();
                startBluetoothDiscovery(); // 권한 승인 후 Bluetooth 검색 시작
            } else {
                Toast.makeText(this, "Bluetooth와 위치 권한이 필요합니다. 앱 설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
                finish(); // 권한이 거부된 경우 액티비티 종료
            }
        }
    }

    // Bluetooth 장치 검색 시작
    private void startBluetoothDiscovery() {
        Toast.makeText(this, "Bluetooth 검색 시작", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {

            // 검색을 위한 리시버 등록
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothReceiver, filter);
            Toast.makeText(this, "Bluetooth 리시버 등록 완료", Toast.LENGTH_SHORT).show();

            bluetoothAdapter.startDiscovery();
            Toast.makeText(this, "Bluetooth 장치 검색 시작", Toast.LENGTH_SHORT).show();
        }
    }

    // BroadcastReceiver를 통해 검색된 장치를 처리
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (device.getName() != null && device.getName().equals(targetDeviceName)) {
                        Toast.makeText(LoadingPageActivity.this, "연결 가능한 장치를 찾았습니다: " + device.getName(), Toast.LENGTH_SHORT).show();
                        bluetoothAdapter.cancelDiscovery(); // 검색 중지
                        goToNextPage(); // 다음 페이지로 이동
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(LoadingPageActivity.this, "장치 검색이 완료되었습니다. 목표 장치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 다음 페이지로 이동하는 메서드
    private void goToNextPage() {
        Toast.makeText(this, "다음 페이지로 이동", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoadingPageActivity.this, ConnectedPageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        Toast.makeText(this, "onDestroy 호출됨", Toast.LENGTH_SHORT).show();
    }
}