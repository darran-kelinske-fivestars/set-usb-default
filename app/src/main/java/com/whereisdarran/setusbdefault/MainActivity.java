package com.whereisdarran.setusbdefault;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whereisdarran.setusbdefault.util.PermissionUtil;
import com.whereisdarran.setusbdefault.util.PermissionUtil.DeviceFilter;
import com.whereisdarran.setusbdefault.util.RootUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.HashMap;

import static com.whereisdarran.setusbdefault.util.PermissionUtil.COMMAND_CHOWN_USB_FILE;
import static com.whereisdarran.setusbdefault.util.PermissionUtil.COMMAND_COPY_USB_FILE;

public class MainActivity extends AppCompatActivity {

    private static final String MANUFACTURER = "Magtek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantUSBPermission();
            }
        });

    }

    private void grantUSBPermission() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (UsbDevice usbDevice : deviceList.values()) {
            if (usbDevice.getManufacturerName() != null && usbDevice.getManufacturerName().equalsIgnoreCase(MANUFACTURER)) {
                Boolean hasPermission = usbManager.hasPermission(usbDevice);
                // Log if USB manager explicitly reports no permission.
                if (!hasPermission) {
                    Log.i("DARRAN", "USB Manager reporting no permission to reader.");
                    DeviceFilter deviceFilter = new DeviceFilter(usbDevice);
                    writeSettingsFile(deviceFilter);
                }
            }
        }
    }

    private void writeSettingsFile(DeviceFilter deviceFilter) {
        PermissionUtil.writeSettingsLocked(getApplicationContext(), deviceFilter);
        RootUtil.executeAsRoot(COMMAND_COPY_USB_FILE);
        RootUtil.executeAsRoot(COMMAND_CHOWN_USB_FILE);
        RootUtil.executeAsRoot("reboot");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
