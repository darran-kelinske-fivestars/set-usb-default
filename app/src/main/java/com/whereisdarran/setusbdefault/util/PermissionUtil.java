package com.whereisdarran.setusbdefault.util;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.AtomicFile;
import android.util.Log;


import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();
    public static final String COMMAND_COPY_USB_FILE = "cp /sdcard/Android/data/com.whereisdarran.setusbdefault/files/usb_device_manager.xml /data/system/users/0/usb_device_manager.xml";
    public static final String COMMAND_CHOWN_USB_FILE = "chown system:system /data/system/users/0/usb_device_manager.xml";

    public static void writeSettingsLocked(Context context, DeviceFilter filter) {

        AtomicFile mSettingsFile = new AtomicFile(new File(context.getExternalFilesDir(null), "usb_device_manager.xml"));

        FileOutputStream fos = null;
        try {
            fos = mSettingsFile.startWrite();

            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(fos, StandardCharsets.UTF_8.name());
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "settings");

                serializer.startTag(null, "preference");
                serializer.attribute(null, "package", "fs.instore");
                filter.write(serializer);
                serializer.endTag(null, "preference");


            serializer.endTag(null, "settings");
            serializer.endDocument();

            mSettingsFile.finishWrite(fos);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write settings", e);
            if (fos != null) {
                mSettingsFile.failWrite(fos);
            }
        }
    }

    // This class is used to describe a USB device.
    // When used in HashMaps all values must be specified,
    // but wildcards can be used for any of the fields in
    // the package meta-data.
    public static class DeviceFilter {
        // USB Vendor ID (or -1 for unspecified)
        public final int mVendorId;
        // USB Product ID (or -1 for unspecified)
        public final int mProductId;
        // USB device or interface class (or -1 for unspecified)
        public final int mClass;
        // USB device subclass (or -1 for unspecified)
        public final int mSubclass;
        // USB device protocol (or -1 for unspecified)
        public final int mProtocol;
        // USB device manufacturer name string (or null for unspecified)
        public final String mManufacturerName;
        // USB device product name string (or null for unspecified)
        public final String mProductName;
        // USB device serial number string (or null for unspecified)
        public final String mSerialNumber;

        public DeviceFilter(int vid, int pid, int clasz, int subclass, int protocol,
                            String manufacturer, String product, String serialnum) {
            mVendorId = vid;
            mProductId = pid;
            mClass = clasz;
            mSubclass = subclass;
            mProtocol = protocol;
            mManufacturerName = manufacturer;
            mProductName = product;
            mSerialNumber = serialnum;
        }

        public DeviceFilter(UsbDevice device) {
            mVendorId = device.getVendorId();
            mProductId = device.getProductId();
            mClass = device.getDeviceClass();
            mSubclass = device.getDeviceSubclass();
            mProtocol = device.getDeviceProtocol();
            mManufacturerName = device.getManufacturerName();
            mProductName = device.getProductName();
            mSerialNumber = device.getSerialNumber();
        }

        public void write(XmlSerializer serializer) throws IOException {
            serializer.startTag(null, "usb-device");
            if (mVendorId != -1) {
                serializer.attribute(null, "vendor-id", Integer.toString(mVendorId));
            }
            if (mProductId != -1) {
                serializer.attribute(null, "product-id", Integer.toString(mProductId));
            }
            if (mClass != -1) {
                serializer.attribute(null, "class", Integer.toString(mClass));
            }
            if (mSubclass != -1) {
                serializer.attribute(null, "subclass", Integer.toString(mSubclass));
            }
            if (mProtocol != -1) {
                serializer.attribute(null, "protocol", Integer.toString(mProtocol));
            }
            if (mManufacturerName != null) {
                serializer.attribute(null, "manufacturer-name", mManufacturerName);
            }
            if (mProductName != null) {
                serializer.attribute(null, "product-name", mProductName);
            }
            if (mSerialNumber != null) {
                serializer.attribute(null, "serial-number", mSerialNumber);
            }
            serializer.endTag(null, "usb-device");
        }

        @Override
        public boolean equals(Object obj) {
            // can't compare if we have wildcard strings
            if (mVendorId == -1 || mProductId == -1 ||
                    mClass == -1 || mSubclass == -1 || mProtocol == -1) {
                return false;
            }
            if (obj instanceof DeviceFilter) {
                DeviceFilter filter = (DeviceFilter)obj;

                if (filter.mVendorId != mVendorId ||
                        filter.mProductId != mProductId ||
                        filter.mClass != mClass ||
                        filter.mSubclass != mSubclass ||
                        filter.mProtocol != mProtocol) {
                    return(false);
                }
                if ((filter.mManufacturerName != null &&
                        mManufacturerName == null) ||
                        (filter.mManufacturerName == null &&
                                mManufacturerName != null) ||
                        (filter.mProductName != null &&
                                mProductName == null)  ||
                        (filter.mProductName == null &&
                                mProductName != null) ||
                        (filter.mSerialNumber != null &&
                                mSerialNumber == null)  ||
                        (filter.mSerialNumber == null &&
                                mSerialNumber != null)) {
                    return(false);
                }
                if  ((filter.mManufacturerName != null &&
                        mManufacturerName != null &&
                        !mManufacturerName.equals(filter.mManufacturerName)) ||
                        (filter.mProductName != null &&
                                mProductName != null &&
                                !mProductName.equals(filter.mProductName)) ||
                        (filter.mSerialNumber != null &&
                                mSerialNumber != null &&
                                !mSerialNumber.equals(filter.mSerialNumber))) {
                    return(false);
                }
                return(true);
            }
            if (obj instanceof UsbDevice) {
                UsbDevice device = (UsbDevice)obj;
                if (device.getVendorId() != mVendorId ||
                        device.getProductId() != mProductId ||
                        device.getDeviceClass() != mClass ||
                        device.getDeviceSubclass() != mSubclass ||
                        device.getDeviceProtocol() != mProtocol) {
                    return(false);
                }
                if ((mManufacturerName != null && device.getManufacturerName() == null) ||
                        (mManufacturerName == null && device.getManufacturerName() != null) ||
                        (mProductName != null && device.getProductName() == null) ||
                        (mProductName == null && device.getProductName() != null) ||
                        (mSerialNumber != null && device.getSerialNumber() == null) ||
                        (mSerialNumber == null && device.getSerialNumber() != null)) {
                    return(false);
                }
                if ((device.getManufacturerName() != null &&
                        !mManufacturerName.equals(device.getManufacturerName())) ||
                        (device.getProductName() != null &&
                                !mProductName.equals(device.getProductName())) ||
                        (device.getSerialNumber() != null &&
                                !mSerialNumber.equals(device.getSerialNumber()))) {
                    return(false);
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (((mVendorId << 16) | mProductId) ^
                    ((mClass << 16) | (mSubclass << 8) | mProtocol));
        }

        @Override
        public String toString() {
            return "DeviceFilter[mVendorId=" + mVendorId + ",mProductId=" + mProductId +
                    ",mClass=" + mClass + ",mSubclass=" + mSubclass +
                    ",mProtocol=" + mProtocol + ",mManufacturerName=" + mManufacturerName +
                    ",mProductName=" + mProductName + ",mSerialNumber=" + mSerialNumber +
                    "]";
        }
    }
}
