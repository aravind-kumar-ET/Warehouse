package com.example.srijithaugustine.warehouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CargoActivity extends AppCompatActivity {
    TextView Customer, Description, Jobno, PickUpDate, DocumentNo, Remarks;
    GeneralFN.PickUpNote selectedItem = new GeneralFN.PickUpNote();
    public static ArrayList<GeneralFN.PickUpCargo> cargoArrayList = new ArrayList<>();
    private ListView LVCargoItems;
    private ProgressDialog progressDialog;
    private int pickupPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo);
        pickupPosition = getIntent().getIntExtra("position", Integer.parseInt("0"));
        selectedItem = PickupNoteActivity.ItemListArrayList.get(pickupPosition);
        initView();
    }

    private void initView() {
        Customer = (TextView) findViewById(R.id.TVCustomer);
        Description = (TextView) findViewById(R.id.TvDescription);
        Jobno = (TextView) findViewById(R.id.TVJobNo);
        PickUpDate = (TextView) findViewById(R.id.TVPickUpDate);
        DocumentNo = (TextView) findViewById(R.id.TVPickUpDocumentNo);
        Remarks = (TextView) findViewById(R.id.TVRemarks);

        if (selectedItem != null) {
            Customer.setText(selectedItem.getCustomer());
            Description.setText(selectedItem.getDescription());
            Jobno.setText(selectedItem.getJobNo());
            PickUpDate.setText(selectedItem.getPickUpDate());
            DocumentNo.setText(selectedItem.getPickUpdocumentNo());
            Remarks.setText(selectedItem.getRemarks());
        }
        final CargoCustomAdapter cargoCustomAdapter = new CargoCustomAdapter();
        LVCargoItems = (ListView) this.findViewById(R.id.LVCargoItems);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CargoActivity.this, android.R.style.Theme_Material_Light_Dialog);
                progressDialog.setMessage("Please Wait..!");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cargoArrayList = GeneralFN.GetPickUpCargo(selectedItem.getPHId(), getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                LVCargoItems.setAdapter(cargoCustomAdapter);
                super.onPostExecute(aVoid);
                progressDialog.dismiss();

            }
        }.execute();

        LVCargoItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(CargoActivity.this, CargoScanning.class).putExtra("CargoPosition", position).putExtra("pickupPosition",pickupPosition ));
                finish();
            }
        });

    }

    class CargoCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cargoArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return cargoArrayList.get(position).getItemDesc();
        }

        @Override
        public long getItemId(int position) {
            return cargoArrayList.get(position).getPHId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.cargoitem, null);
            TextView CargoItem = (TextView) convertView.findViewById(R.id.TVItemName);
            TextView units = (TextView) convertView.findViewById(R.id.TVUnit);
            CargoItem.setText(cargoArrayList.get(position).getItemDesc());
            units.setText(String.valueOf(cargoArrayList.get(position).Units));
            return convertView;
        }
    }

}
