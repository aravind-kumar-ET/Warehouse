package com.example.srijithaugustine.warehouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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

public class CargoScanning extends AppCompatActivity implements BarcodeReader.BarcodeListener {
    private TextView CargoItem, Units;
    ListView CodeScanned;
    Button ScanBTN;
    FloatingActionButton UpdateBTN, BackBtn, RefreshBTN;
    private GeneralFN.PickUpCargo selctedPickUpCargo = new GeneralFN.PickUpCargo();
    public static List<String> SerialNoList = new ArrayList<String>();
    public static List<Integer> PackageIdList = new ArrayList<Integer>();
    public static ArrayList<GeneralFN.CargoPackageDtl> listCargoPackageDtl = new ArrayList<>();
    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private AidcManager manager;
    private int counter = 0, number = 1;
    private ProgressDialog progressDialog;
    private int CargoPosition = 0, pickupPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo_scanning);
        CargoPosition = getIntent().getIntExtra("CargoPosition", Integer.parseInt("0"));
        pickupPosition = getIntent().getIntExtra("pickupPosition", Integer.parseInt("0"));
        selctedPickUpCargo = CargoActivity.cargoArrayList.get(CargoPosition);
        counter = selctedPickUpCargo.getNotScanned();
        CodeScanned = (ListView) findViewById(R.id.CargoScannedListView);
        BindlistView();
        InitView();
        BarcodeSetUp();
    }

    private void InitView() {
        CargoItem = (TextView) findViewById(R.id.TVCargoName);
        Units = (TextView) findViewById(R.id.TVCount);
        ScanBTN = (Button) findViewById(R.id.ScanBTN);
        UpdateBTN = (FloatingActionButton) findViewById(R.id.UpdateBTN);
        BackBtn = (FloatingActionButton) findViewById(R.id.BackBTN);
        RefreshBTN = (FloatingActionButton) findViewById(R.id.RefreshBTN);
        if (selctedPickUpCargo != null) {
            listCargoPackageDtl.size();
            CargoItem.setText(selctedPickUpCargo.getItemDesc());
            String unit = String.valueOf(selctedPickUpCargo.getScanned()) + " / " + String.valueOf(selctedPickUpCargo.Units);
            Units.setText(unit);
        }
        ScanBTN.setOnTouchListener(new View.OnTouchListener() {
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

        UpdateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePakageDetails();
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CargoScanning.this, CargoActivity.class).putExtra("position", pickupPosition));
                finish();
            }
        });
        RefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindlistView();
            }
        });


    }

    private void BindlistView() {
        final customAdapter customAdapter = new customAdapter();
        PackageIdList.clear();
        SerialNoList.clear();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CargoScanning.this, android.R.style.Theme_Material_Light_Dialog);
                progressDialog.setMessage("Please Wait..!");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                    listCargoPackageDtl = GeneralFN.GetCargoPackageDtl(getApplicationContext(), selctedPickUpCargo.getPHId(), selctedPickUpCargo.getItemId(), PackageIdList, SerialNoList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                CodeScanned.setAdapter(customAdapter);
                progressDialog.dismiss();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void UpdatePakageDetails() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CargoScanning.this, android.R.style.Theme_Material_Light_Dialog);
                progressDialog.setMessage("Please Wait..!");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String msg = "";
                try {
                    Thread.sleep(2000);
                    ArrayList<GeneralFN.CargoPackageDtl> listCargoPackageNewDtl = new ArrayList<>();
                    for (GeneralFN.CargoPackageDtl cpd : listCargoPackageDtl) {
                        if (cpd.isNew()) {
                            listCargoPackageNewDtl.add(cpd);
                        }
                    }
                    Gson gson = new GsonBuilder().create();
                    JsonArray jsonArray = gson.toJsonTree(listCargoPackageNewDtl).getAsJsonArray();
                    msg = GeneralFN.UpdatePakageDetails(getApplicationContext(), listCargoPackageNewDtl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
            }
        }.execute();

    }

    private void BarcodeSetUp() {
        barcodeReader = PickupNoteActivity.getBarcodeObject();
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
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!GeneralFN.CheckForElement(SerialNoList, barcodeReadEvent.getBarcodeData().toString())) {
                    number = selctedPickUpCargo.getScanned() + SerialNoList.size() + 1;
                    String unit = String.valueOf(number) + " / " + String.valueOf(selctedPickUpCargo.Units);
                    Units.setText(unit);
                    SerialNoList.add(barcodeReadEvent.getBarcodeData());

                    GeneralFN.CargoPackageDtl CPD = new GeneralFN.CargoPackageDtl();
                    CPD.setSerialNo(barcodeReadEvent.getBarcodeData().toString());
                    CPD.setPackageID(PackageIdList.get(0));
                    CPD.setIsNew(true);
                    listCargoPackageDtl.add(CPD);
                    PackageIdList.remove(PackageIdList.get(0));
                    counter--;
                } else {
                    Toast.makeText(CargoScanning.this, "Already Scanned", Toast.LENGTH_SHORT).show();
                }

                // final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CargoScanning.this, android.R.layout.simple_list_item_1, SerialNoList);

                customAdapter customAdapter = new customAdapter();
                CodeScanned.setAdapter(customAdapter);
                CodeScanned.setSelection(customAdapter.getCount() - 1);
            }
        });

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(CargoScanning.this, "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listCargoPackageDtl.size();
        }

        @Override
        public Object getItem(int position) {
            return listCargoPackageDtl.get(position).getSerialNo();
        }

        @Override
        public long getItemId(int position) {
            return listCargoPackageDtl.get(position).getPackageID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.cargoitem, null);
            TextView CargoItem = (TextView) convertView.findViewById(R.id.TVItemName);
            TextView units = (TextView) convertView.findViewById(R.id.TVUnit);
            CargoItem.setText(listCargoPackageDtl.get(position).getSerialNo());
            units.setText(String.valueOf(listCargoPackageDtl.get(position).getPackageID()));
            return convertView;
        }
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
        SerialNoList.clear();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                CargoScanning.this, android.R.layout.simple_list_item_1, SerialNoList);
        CodeScanned.setAdapter(dataAdapter);
        PackageIdList.clear();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            //barcodeReader.removeTriggerListener(this);
        }
    }
}
