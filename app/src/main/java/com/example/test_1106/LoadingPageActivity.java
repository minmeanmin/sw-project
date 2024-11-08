package com.example.test_1106;

import android.Manifest;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import android.util.Log; // 로그 추가

public class LoadingPageActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID
    private BluetoothAdapter bluetoothAdapter;
    private String targetDeviceName;
    private BluetoothDevice targetDevice; // 연결할 장치 객체 추가
    private boolean isDiscovering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        targetDeviceName = Objects.requireNonNull(getIntent().getStringExtra("TRAY_ID")).trim();
        Toast.makeText(this, "장치 이름 수신: " + targetDeviceName, Toast.LENGTH_SHORT).show();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        checkBluetoothPermissions();
    }

    private void checkBluetoothPermissions() {
        Log.d("LoadingPageActivity", "checkBluetoothPermissions called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("LoadingPageActivity", "Android 12 이상");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d("LoadingPageActivity", "블루투스 권한 요청");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                startBluetoothDiscovery();
            }
        } else {
            Log.d("LoadingPageActivity", "Android 11 이하");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("LoadingPageActivity", "위치 권한 요청");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                startBluetoothDiscovery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("LoadingPageActivity", "onRequestPermissionsResult called");

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    Log.d("LoadingPageActivity", "권한 거부됨: " + permissions[result]);
                    break;
                }
            }
            if (allPermissionsGranted) {
                Log.d("LoadingPageActivity", "모든 권한 허용됨");
                startBluetoothDiscovery();
            } else {
                Toast.makeText(this, "Bluetooth와 위치 권한이 필요합니다. 앱 설정에서 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startBluetoothDiscovery() {
        if (isDiscovering) return;

        Toast.makeText(this, "Bluetooth 검색 시작", Toast.LENGTH_SHORT).show();
        Log.d("LoadingPageActivity", "Bluetooth 검색 시작");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothReceiver, filter);

            bluetoothAdapter.startDiscovery();
            isDiscovering = true;
            Log.d("LoadingPageActivity", "Bluetooth 장치 검색 시작");
        } else {
            Toast.makeText(this, "Bluetooth 권한 부족으로 검색 불가", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (device.getName() != null && device.getName().equals(targetDeviceName)) {
                        Toast.makeText(LoadingPageActivity.this, "연결 가능한 장치를 찾았습니다: " + device.getName(), Toast.LENGTH_SHORT).show();
                        targetDevice = device; // 장치 객체 저장
                        bluetoothAdapter.cancelDiscovery();
                        isDiscovering = false;
                        connectToDevice(targetDevice); // 연결 메소드 호출
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                isDiscovering = false;
                Toast.makeText(LoadingPageActivity.this, "장치 검색이 완료되었습니다. 목표 장치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // 권한 요청
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_BLUETOOTH_PERMISSIONS);
                    return;
                }

                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect(); // 연결 시도

                // 연결 성공 시
                runOnUiThread(() -> {
                    Toast.makeText(LoadingPageActivity.this, "장치에 연결되었습니다!", Toast.LENGTH_SHORT).show();
                    goToNextPage(); // 연결 후 다음 페이지로 이동
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoadingPageActivity.this, "장치 연결 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void goToNextPage() {
        Intent intent = new Intent(LoadingPageActivity.this, ConnectedPageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDiscovering) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
