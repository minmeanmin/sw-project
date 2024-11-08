package com.example.test_1106;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice device;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 표준 SPP UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BluetoothAdapter 초기화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "이 기기는 Bluetooth를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bluetooth 활성화 요청
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            // Bluetooth 권한 체크 및 요청
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            checkPermissionsAndConnect();
        }
    }

    private void checkPermissionsAndConnect() {
        // Bluetooth 연결에 필요한 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            // 이미 페어링된 장치 가져오기
            device = bluetoothAdapter.getRemoteDevice("DEVICE_MAC_ADDRESS"); // 여기에 연결할 장치의 MAC 주소를 넣으세요.
            connectToDevice(device);
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                // Bluetooth 연결 권한 확인 및 요청
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                    return;
                }
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                bluetoothSocket.connect();
                runOnUiThread(() -> Toast.makeText(this, "블루투스 연결 성공", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "블루투스 연결 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionsAndConnect();
            } else {
                Toast.makeText(this, "Bluetooth 권한이 필요합니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
