package com.example.srijithaugustine.warehouse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GeneralFN {

    private static String baseURL = "http://192.168.15.100:17694/Home/";

    public final class LoginData {
        public String message;
        public String userID;
        public boolean IsRegistered;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public boolean isRegistered() {
            return IsRegistered;
        }

        public void setRegistered(boolean registered) {
            IsRegistered = registered;
        }
    }

    public static final class PickUpNote {
        public int CustomerId;
        public int PHId;
        public String PickUpdocumentNo;
        public String PickUpDate;
        public String Customer;
        public String Remarks;
        public String Description;
        public String JobNo;
        public int ItemCount;

        public int getCustomerId() {
            return CustomerId;
        }

        public void setCustomerId(int customerId) {
            CustomerId = customerId;
        }

        public int getPHId() {
            return PHId;
        }

        public void setPHId(int PHId) {
            this.PHId = PHId;
        }

        public String getPickUpdocumentNo() {
            return PickUpdocumentNo;
        }

        public void setPickUpdocumentNo(String pickUpdocumentNo) {
            PickUpdocumentNo = pickUpdocumentNo;
        }

        public String getPickUpDate() {
            return PickUpDate;
        }

        public void setPickUpDate(String pickUpDate) {
            PickUpDate = pickUpDate;
        }

        public String getCustomer() {
            return Customer;
        }

        public void setCustomer(String customer) {
            Customer = customer;
        }

        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getJobNo() {
            return JobNo;
        }

        public void setJobNo(String jobNo) {
            JobNo = jobNo;
        }

        public int getItemCount() {
            return ItemCount;
        }

        public void setItemCount(int itemCount) {
            ItemCount = itemCount;
        }
    }

    public static final class PickUpCargo {
        public String getItemDesc() {
            return ItemDesc;
        }

        public void setItemDesc(String itemDesc) {
            ItemDesc = itemDesc;
        }

        public int getUnits() {
            return Units;
        }

        public void setUnits(int units) {
            Units = units;
        }

        public int getScanned() {
            return Scanned;
        }

        public void setScanned(int scanned) {
            Scanned = scanned;
        }

        public int getNotScanned() {
            return NotScanned;
        }

        public void setNotScanned(int notScanned) {
            NotScanned = notScanned;
        }

        public int getItemId() {
            return ItemId;
        }

        public void setItemId(int itemId) {
            ItemId = itemId;
        }

        public int getPHId() {
            return PHId;
        }

        public void setPHId(int PHId) {
            this.PHId = PHId;
        }

        public String ItemDesc;
        public int Units;
        public int Scanned;
        public int NotScanned;
        public int ItemId;
        public int PHId;
    }

    public static final class CargoPackageDtl {
        public int PackageID;
        public String SerialNo;
        public boolean IsNew;

        public boolean isNew() {
            return IsNew;
        }

        public void setIsNew(boolean aNew) {
            IsNew = aNew;
        }

        public int getPackageID() {
            return PackageID;
        }

        public void setPackageID(int packageID) {
            PackageID = packageID;
        }

        public String getSerialNo() {
            return SerialNo;
        }

        public void setSerialNo(String serialNo) {
            SerialNo = serialNo;
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            isConnected = true;
        } else {
            Toast.makeText(context, "Please connect to a network or connection unavailable", Toast.LENGTH_LONG);
        }
        return isConnected;
    }

    public LoginData Login(String uname, String password, Context context) {
        String data = "", dataparsed = "";
        LoginData loginData = new LoginData();
        String valuestring = ConvertToHex("username=" + uname + "&password=" + password);

        if (checkNetworkConnection(context)) {
            try {
                URL url = new URL(baseURL + "Login/" + valuestring);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                data = data + line;
                JSONObject JO = new JSONObject(data);
                loginData.setUserID(JO.get("userId").toString());
                loginData.setMessage(JO.get("message").toString());
                if (loginData.getMessage().equals("success") && !loginData.getUserID().equals("0")) {
                    loginData.setRegistered(true);
                } else {
                    loginData.setRegistered(false);
                }

            } catch (MalformedURLException e) {
                loginData.setMessage(e.getMessage());
                loginData.setRegistered(false);
                e.printStackTrace();
            } catch (IOException e) {
                loginData.setMessage(e.getMessage());
                loginData.setRegistered(false);
                e.printStackTrace();
            } catch (JSONException e) {
                loginData.setMessage(e.getMessage());
                loginData.setRegistered(false);
                e.printStackTrace();
            }
        } else {
            loginData.setRegistered(false);
        }
        return loginData;
    }

    private String ConvertToHex(String Str) {
        char[] chars = Str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static boolean CheckForElement(List<String> list, String value) {
        boolean result = false;
        if (list != null) {
            for (String elem : list) {
                if (elem.equals(value)) return true;
            }
        }
        return result;
    }

    public static ArrayList<PickUpNote> GetPickUpNote(Context context) {
        PickUpNote PUN = new PickUpNote();
        String data = "";
        URL url = null;
        ArrayList<PickUpNote> ItemListArrayList = new ArrayList<PickUpNote>();
        if (checkNetworkConnection(context)) {
            try {
                url = new URL(baseURL + "GetPickUpNotes");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                data = data + line;
                JSONArray JA = new JSONArray(data);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JObj = JA.getJSONObject(i);
                    PickUpNote PUC = new PickUpNote();
                    PUC.setCustomer(JObj.getString("Customer"));
                    PUC.setCustomerId(JObj.getInt("CustomerId"));
                    PUC.setDescription(JObj.getString("Description"));
                    PUC.setPHId(JObj.getInt("PHId"));
                    PUC.setPickUpdocumentNo(JObj.getString("PickUpdocumentNo"));
                    PUC.setPickUpDate(JObj.getString("PickUpDate"));
                    PUC.setRemarks(JObj.getString("Remarks"));
                    PUC.setJobNo(JObj.getString("JobNo"));
                    PUC.setItemCount(JObj.getInt("ItemCount"));
                    ItemListArrayList.add(PUC);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ItemListArrayList;

    }

    public static ArrayList<PickUpCargo> GetPickUpCargo(long id, Context context) {
        ArrayList<PickUpCargo> ItemArrayListOfCargo = new ArrayList<>();
        String data = "";
        URL url = null;
        if (checkNetworkConnection(context)) {
            try {
                url = new URL(baseURL + "GetPickUpCargo?PHID=" + id);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                data = data + line;
                JSONArray JA = new JSONArray(data);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = JA.getJSONObject(i);
                    PickUpCargo PUC = new PickUpCargo();
                    PUC.setItemDesc(JO.getString("ItemDesc"));
                    PUC.setPHId(JO.getInt("PHId"));
                    PUC.setItemId(JO.getInt("ItemId"));
                    PUC.setUnits(JO.getInt("Units"));
                    PUC.setScanned(JO.getInt("Scanned"));
                    PUC.setNotScanned(JO.getInt("NotScanned"));
                    ItemArrayListOfCargo.add(PUC);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ItemArrayListOfCargo;
    }

    public static ArrayList<CargoPackageDtl> GetCargoPackageDtl(Context context, long PHId, long ItemId, List<Integer> PackageIDList, List<String> SerialNoList) {
        ArrayList<CargoPackageDtl> ItemArrayListOfCargoPackageDtl = new ArrayList<>();
        String data = "";
        URL url = null;
        if (checkNetworkConnection(context)) {
            try {
                url = new URL(baseURL + "GetCargoPackageDtl?PHId=" + PHId + "&ItemId=" + ItemId + "");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                data = data + line;
                JSONArray JA = new JSONArray(data);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = JA.getJSONObject(i);
                    CargoPackageDtl CPD = new CargoPackageDtl();
                    if (!JO.getString("SerialNo").isEmpty()) {
                        CPD.setSerialNo(JO.getString("SerialNo"));
                        CPD.setPackageID(JO.getInt("PackageID"));
                        CPD.setIsNew(false);
                        ItemArrayListOfCargoPackageDtl.add(CPD);
                        SerialNoList.add(JO.getString("SerialNo"));
                    } else {
                        PackageIDList.add(JO.getInt("PackageID"));
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ItemArrayListOfCargoPackageDtl;
    }

    public static String UpdatePakageDetails(final Context context, final ArrayList<CargoPackageDtl> listCargoPackageNewDtl) {
        String data = "";
        URL url = null;
        for (CargoPackageDtl cpd : listCargoPackageNewDtl) {
            try{
                url = new URL(baseURL + "UpdateCargoSerialNo?packageID="+cpd.getPackageID()+"&SerialNo="+URLEncoder.encode(cpd.getSerialNo(),"UTF-8") );
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                data = data + line;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return data;
    }

}

