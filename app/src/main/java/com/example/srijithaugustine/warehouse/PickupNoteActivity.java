package com.example.srijithaugustine.warehouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;

import java.util.ArrayList;
import java.util.List;

public class PickupNoteActivity extends AppCompatActivity {
    public static EditText ETNoScan;
    private Toolbar toolbar;
    private AidcManager manager;
    private ListView PickUpNotesListView;
    private FloatingActionButton SearchBtn, RefreshBtn;
    private static BarcodeReader barcodeReader;
    public static ArrayList<GeneralFN.PickUpNote> ItemListArrayList = new ArrayList<GeneralFN.PickUpNote>();
    public static ArrayList<GeneralFN.PickUpNote> SearchArrayList = new ArrayList<GeneralFN.PickUpNote>();
    public static List<String> list = new ArrayList<String>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_note);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
            }
        });
        final CustomeAdapter customeAdapter = new CustomeAdapter();
        PickUpNotesListView = (ListView) findViewById(R.id.LVPickUpNotes);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(PickupNoteActivity.this, android.R.style.Theme_Material_Light_Dialog);
                progressDialog.setMessage("Please Wait..!");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ItemListArrayList = GeneralFN.GetPickUpNote(getApplicationContext());
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                PickUpNotesListView.setAdapter(customeAdapter);
                progressDialog.dismiss();
            }
        }.execute();

        SearchBtn = (FloatingActionButton) findViewById(R.id.searchbtn);
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleAlert(v);
            }
        });

        RefreshBtn = (FloatingActionButton) findViewById(R.id.RefreshBtn);
        RefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickUpNotesListView.setAdapter(customeAdapter);
            }
        });


        PickUpNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(PickupNoteActivity.this, CargoActivity.class).putExtra("position", position));
            }
        });


    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ItemListArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return ItemListArrayList.get(position).getCustomer();
        }

        @Override
        public long getItemId(int position) {
            return ItemListArrayList.get(position).getPHId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.listitem, null);
            TextView customer = (TextView) convertView.findViewById(R.id.TVCustomer);
            TextView Description = (TextView) convertView.findViewById(R.id.TVDesc);
            TextView JobNo = (TextView) convertView.findViewById(R.id.TVJobNo);
            TextView Date = (TextView) convertView.findViewById(R.id.TVDate);
            TextView Remarks = (TextView) convertView.findViewById(R.id.TVRemarks);
            TextView Count = (TextView) convertView.findViewById(R.id.TVCount);
            TextView DocumentNo = (TextView) convertView.findViewById(R.id.TVDocumentNo);

            customer.setText(ItemListArrayList.get(position).getCustomer());
            Description.setText(ItemListArrayList.get(position).getDescription());
            JobNo.setText(ItemListArrayList.get(position).getJobNo());
            Date.setText(ItemListArrayList.get(position).getPickUpDate());
            Remarks.setText(ItemListArrayList.get(position).getRemarks());
            DocumentNo.setText(ItemListArrayList.get(position).getPickUpdocumentNo());
            Count.setText(String.valueOf(ItemListArrayList.get(position).getItemCount()));
            return convertView;
        }
    }

    public void simpleAlert(final View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.search_layout, null);
        final EditText EDTSearch = (EditText) alertView.findViewById(R.id.EDTSearch);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("Search for Customer");

        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchText = EDTSearch.getText().toString();
                SearchFromArrayList(searchText);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromInputMethod(EDTSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(((View) view.getParent()).getWidth(), 450);
    }

    public void SearchFromArrayList(String SearchText) {
        for (GeneralFN.PickUpNote PUN : ItemListArrayList) {
            if (PUN.getCustomer().contains(SearchText)) {
                SearchArrayList.add(PUN);
            }

        }
        final SearchAdapter SearchAdapter = new SearchAdapter();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(PickupNoteActivity.this, android.R.style.Theme_Material_Light_Dialog);
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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                PickUpNotesListView.setAdapter(SearchAdapter);
                progressDialog.dismiss();
            }
        }.execute();
    }

    class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return SearchArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return SearchArrayList.get(position).getCustomer();
        }

        @Override
        public long getItemId(int position) {
            return SearchArrayList.get(position).getPHId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.listitem, null);
            TextView customer = (TextView) convertView.findViewById(R.id.TVCustomer);
            TextView Description = (TextView) convertView.findViewById(R.id.TVDesc);
            TextView JobNo = (TextView) convertView.findViewById(R.id.TVJobNo);
            TextView Date = (TextView) convertView.findViewById(R.id.TVDate);
            TextView Remarks = (TextView) convertView.findViewById(R.id.TVRemarks);
            TextView Count = (TextView) convertView.findViewById(R.id.TVCount);
            TextView DocumentNo = (TextView) convertView.findViewById(R.id.TVDocumentNo);

            customer.setText(SearchArrayList.get(position).getCustomer());
            Description.setText(SearchArrayList.get(position).getDescription());
            JobNo.setText(SearchArrayList.get(position).getJobNo());
            Date.setText(SearchArrayList.get(position).getPickUpDate());
            Remarks.setText(SearchArrayList.get(position).getRemarks());
            DocumentNo.setText(SearchArrayList.get(position).getPickUpdocumentNo());
            Count.setText(String.valueOf(SearchArrayList.get(position).getItemCount()));
            return convertView;
        }
    }

}
