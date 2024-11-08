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

public class LoadingPageActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private String targetDeviceName;
    private BluetoothDevice targetDevice;
    private boolean isDiscovering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        targetDeviceName = Objects.requireNonNull(getIntent().getStringExtra("TRAY_ID")).trim();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            showToast("이 기기는 블루투스를 지원하지 않습니다. 앱을 종료합니다.");
            finish();
        } else {
            checkPermissionsAndRequest();
        }
    }

    private void checkPermissionsAndRequest() {
        boolean bluetoothPermissions = hasPermissions(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN);
        boolean locationPermission = hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !bluetoothPermissions) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN});
        } else if (!locationPermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            startBluetoothDiscovery();
        }
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (allPermissionsGranted(grantResults)) {
                startBluetoothDiscovery();
            } else {
                showToast("앱에서 Bluetooth와 위치 권한이 필요합니다. 설정에서 권한을 허용한 후 다시 시도해주세요.");
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private void startBluetoothDiscovery() {
        if (!isDiscovering && hasPermissions(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)) {
            isDiscovering = true;
            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.startDiscovery();
                showToast("Bluetooth 장치 검색을 시작합니다...");
            } else {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN});
            }
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (device != null && targetDeviceName.equals(device.getName())) {
                        targetDevice = device;
                        bluetoothAdapter.cancelDiscovery();
                        isDiscovering = false;
                        showToast("목표 장치(" + device.getName() + ")를 찾았습니다. 연결을 시도합니다.");
                        connectToDevice();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT});
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                isDiscovering = false;
                showToast("장치 검색을 완료했습니다. 목표 장치를 찾지 못했거나 연결이 실패했습니다. 다시 시도해 주세요.");
            }
        }
    };

    private void connectToDevice() {
        new Thread(() -> {
            int retryCount = 0;
            while (retryCount < 3) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    try (BluetoothSocket socket = targetDevice.createRfcommSocketToServiceRecord(MY_UUID)) {
                        bluetoothAdapter.cancelDiscovery();
                        socket.connect();
                        runOnUiThread(() -> showToast("목표 장치와 성공적으로 연결되었습니다!"));
                        runOnUiThread(() -> goToNextPage(socket));
                        return;
                    } catch (IOException e) {
                        retryCount++;
                        if (retryCount >= 3) showToast("목표 장치와 연결할 수 없습니다. Bluetooth를 확인한 후 다시 시도해 주세요.");
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT});
                    return;
                }
            }
        }).start();
    }

    private void goToNextPage(BluetoothSocket socket) {
        Intent intent = new Intent(LoadingPageActivity.this, ConnectedPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDiscovering) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.cancelDiscovery();
            }
        }
        unregisterReceiverSafely(bluetoothReceiver);
    }

    private void unregisterReceiverSafely(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
