package com.example.srijithaugustine.warehouse;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity implements BarcodeReader.BarcodeListener {
    private Button ScanBtn, updateBtn;
    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private AidcManager manager;
    private ListView barcodeDataList;
    public static List<String> list = new ArrayList<String>();
    private int counter = 0, number = 1;

    //private static BarcodeReader inbarcodeReader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ScanBtn = (Button) findViewById(R.id.BtnSCan);
        updateBtn = (Button) findViewById(R.id.BtnUpdate);
        updateBtn.setVisibility(View.INVISIBLE);
        barcodeDataList = (ListView) findViewById(R.id.listViewBarcodeData);
        String noscan = "0";
        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            noscan = (extra.get("NoScan").toString() == null) ? "0" : extra.get("NoScan").toString();
        }
        counter = Integer.parseInt(noscan);
        barcodeReader = PickupNoteActivity.getBarcodeObject();
        ScanBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (barcodeReader != null) {
                    try {
                        barcodeReader.claim();
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                barcodeReader.aim(true);
                                barcodeReader.light(true);
                                barcodeReader.decode(true);
                                return true;
                            case MotionEvent.ACTION_UP:
                                return true;
                        }
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Scanner unavailable", Toast.LENGTH_SHORT).show();
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Scanner is not claimed", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Unable to define BarcodeReder or null", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        if (barcodeReader != null) {
            barcodeReader.addBarcodeListener(this);
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Apply the settings
            barcodeReader.setProperties(properties);

        } else
            Toast.makeText(this, "BarcodeReder is null", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            //barcodeReader.removeTriggerListener(this);
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                number = list.size() + 1;
                if (!GeneralFN.CheckForElement(list, barcodeReadEvent.getBarcodeData().toString())) {
                    list.add("Barcode data - " + number + " : " + barcodeReadEvent.getBarcodeData());
                    counter--;
                } else {
                    Toast.makeText(ScannerActivity.this, "Already Scanned", Toast.LENGTH_SHORT).show();
                }

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        ScannerActivity.this, android.R.layout.simple_list_item_1, list);
                barcodeDataList.setAdapter(dataAdapter);
                barcodeDataList.setSelection(dataAdapter.getCount() - 1);

                if (counter == 0) {
                    ScanBtn.setVisibility(View.GONE);
                    updateBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ScannerActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
