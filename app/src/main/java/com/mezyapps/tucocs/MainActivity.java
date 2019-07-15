package com.mezyapps.tucocs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
ConnectionClass connectionClass;
LicenseSession licenseSession;
private String stringlicense,stringmac,satus,status_check;
SQLiteDatabase db;
SessionManager sessionManager;
private EditText ed_remarks,ed_adhar_no,ed_pan_no,ed_name,ed_mobile_no;
private TextView tv_date_view,name_user_login;
private Button submit_btn_form,client_delete_button,client_update_button;
private String spinner_statusstring="",display_name;
Spinner spinner_status;
ImageView add_clinet_detail;
LinearLayout form_layout,gridlinearlayout,update_delete_layout_client;
SearchView searchautocompleteforclient;
GridView gridviewreport;
    SimpleAdapter ADA;
    Integer entryid=0;
    List<Map<String, String>> data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        name_user_login=findViewById(R.id.name_user_login);
        SharedPreferences pref = this.getSharedPreferences("CON", Context.MODE_PRIVATE);
        display_name=pref.getString("print","");
        name_user_login.setText(display_name);
        ed_remarks=findViewById(R.id.ed_remarks);
        spinner_status=findViewById(R.id.spinner_status);
        ed_adhar_no=findViewById(R.id.ed_adhar_no);
        ed_pan_no=findViewById(R.id.ed_pan_no);
        ed_name=findViewById(R.id.ed_name);
        tv_date_view=findViewById(R.id.tv_date_view);
        ed_mobile_no=findViewById(R.id.ed_mobile_no);
        submit_btn_form=findViewById(R.id.submit_btn_form);
        add_clinet_detail=findViewById(R.id.add_clinet_detail);
        form_layout=findViewById(R.id.form_layout);
        gridlinearlayout=findViewById(R.id.gridlinearlayout);
        searchautocompleteforclient=findViewById(R.id.searchautocompleteforclient);
        gridviewreport=findViewById(R.id.gridviewreport);
        update_delete_layout_client=findViewById(R.id.update_delete_layout_client);
        client_delete_button=findViewById(R.id.client_delete_button);
        client_update_button=findViewById(R.id.client_update_button);

        db = openOrCreateDatabase("MY_TUCOCS", Context.MODE_PRIVATE, null);
        connectionClass = new ConnectionClass(MainActivity.this);
        sessionManager = new SessionManager(getApplicationContext());
        licenseSession = new LicenseSession(getApplicationContext());
        licenseSession.checkLogin();
        sessionManager.isLoggedIn();



        // get user data from session
        HashMap<String, String> user = licenseSession.getUserDetails();
        stringlicense = user.get(LicenseSession.NAM);
        stringmac = user.get(LicenseSession.password);
        checkConnection();




        add_clinet_detail.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
        showdetailmethod();
        add_clinet_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (form_layout.getVisibility() == View.VISIBLE) {
                    form_layout.setVisibility(View.GONE);
                    gridlinearlayout.setVisibility(View.VISIBLE);
                    add_clinet_detail.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                } else {
                    gridlinearlayout.setVisibility(View.GONE);
                    form_layout.setVisibility(View.VISIBLE);
                    submit_btn_form.setVisibility(View.VISIBLE);
                    update_delete_layout_client.setVisibility(View.GONE);
                    add_clinet_detail.setBackgroundResource(R.drawable.ic_close_black_24dp);
                    ed_name.setText("");
                    ed_pan_no.setText("");
                    ed_adhar_no.setText("");
                    ed_mobile_no.setText("");
                    spinner_status.setSelection(0);
                    tv_date_view.setText("");
                    ed_remarks.setText("");
                }

            }
        });

        client_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Are you sure want to UPDATE ?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {




                                String name=ed_name.getText().toString().trim();
                                String pan=ed_pan_no.getText().toString().trim();
                                String adhar=ed_adhar_no.getText().toString().trim();
                                String mobile=ed_mobile_no.getText().toString().trim();
                                String stauts=spinner_statusstring;
                                String date=tv_date_view.getText().toString().trim();
                                String remarks=ed_remarks.getText().toString().trim();

                                String str = date;
                                String[] temp;
                                String delimiter = "-";
                                temp = str.split(delimiter);
                                String day = temp[0];
                                String month = temp[1];
                                String year = temp[2];
                                String ymd_dt=year+"/"+month+"/"+day;

                                try {
                                    Connection con = connectionClass.CONN();
                                    if (con == null) {
                                        Toast.makeText(MainActivity.this, "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                                    } else {
                                        String query = "UPDATE  dt_prc SET VCHDT='" + date + "',VCHDT_Y_M_D='" + ymd_dt + "',PAN_NO='" + pan + "',ADHAAR_CARD_NO='" + adhar + "',STATUS='" + stauts + "',MOBILE_NO='" + mobile + "',GROUPNAME='" + name + "',REMARKS='"+remarks+"',DISPLAY_NAME='"+display_name+"' WHERE ENTRYID="+entryid;
                                        Statement stmt = con.createStatement();
                                        int a= stmt.executeUpdate(query);
                                        if (a == 1){
                                            Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            ed_name.setText("");
                                            ed_pan_no.setText("");
                                            ed_adhar_no.setText("");
                                            ed_mobile_no.setText("");
                                            spinner_status.setSelection(0);
                                            tv_date_view.setText("");
                                            entryid=0;
                                            ed_remarks.setText("");
                                            showdetailmethod();
                                            form_layout.setVisibility(View.GONE);
                                            gridlinearlayout.setVisibility(View.VISIBLE);
                                            add_clinet_detail.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                                        }else {
                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(MainActivity.this, "Failed Please Try Again", Toast.LENGTH_SHORT).show();

                                }


               }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();




            }
        });

        client_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Are you sure want to DELETE ?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


                                try {
                                    Connection con = connectionClass.CONN();
                                    if (con == null) {
                                        Toast.makeText(MainActivity.this, "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String query = "DELETE FROM dt_prc WHERE ENTRYID=" + entryid;
                                        Statement stmt = con.createStatement();
                                        int a = stmt.executeUpdate(query);
                                        if (a == 1) {
                                            Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            ed_name.setText("");
                                            entryid = 0;
                                            ed_pan_no.setText("");
                                            ed_adhar_no.setText("");
                                            ed_mobile_no.setText("");
                                            spinner_status.setSelection(0);
                                            tv_date_view.setText("");
                                            ed_remarks.setText("");
                                            showdetailmethod();
                                            form_layout.setVisibility(View.GONE);
                                            gridlinearlayout.setVisibility(View.VISIBLE);
                                            add_clinet_detail.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(MainActivity.this, "Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        submit_btn_form.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you sure want to ADD ?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String name=ed_name.getText().toString().trim();
                        String pan=ed_pan_no.getText().toString().trim();
                        String adhar=ed_adhar_no.getText().toString().trim();
                        String mobile=ed_mobile_no.getText().toString().trim();
                        String stauts=spinner_statusstring;
                        String date=tv_date_view.getText().toString().trim();
                        String remarks=ed_remarks.getText().toString().trim();
if ( pan.isEmpty() &&  adhar.isEmpty() ){
    Toast.makeText(MainActivity.this, "PAN or ADHAR Required", Toast.LENGTH_SHORT).show();
} else if (name.isEmpty() || mobile.isEmpty() || stauts.isEmpty() || date.isEmpty() || remarks.isEmpty()){
            Toast.makeText(MainActivity.this, "ALL Fileds Required", Toast.LENGTH_SHORT).show();
        }else {
    if (pan.isEmpty()){
        pan="-";
    }if (adhar.isEmpty()){
        adhar="-";
    }
            savedataOnSumbitBtn(date,pan,adhar,stauts,mobile,name,remarks);

        }
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
});



        //start searchview *****************************
        searchautocompleteforclient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                ADA.getFilter().filter(newText);
                return false;
            }
        });
//end search view##############################################3



        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.status));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(myAdapter);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    spinner_statusstring="Y";
                    curentdateMethod();
                } else if (i == 2) {
                    spinner_statusstring="N";
                    curentdateMethod();
                } }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void savedataOnSumbitBtn(String date, String pan, String adhar, String stauts, String mobile, String name, String remarks) {
        String str = date;
        String[] temp;
        String delimiter = "-";
        temp = str.split(delimiter);
        String day = temp[0];
        String month = temp[1];
        String year = temp[2];
       String ymd_dt=year+"/"+month+"/"+day;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                Toast.makeText(this, "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                    String query = "INSERT INTO  dt_prc(VCHDT,VCHDT_Y_M_D,PAN_NO,ADHAAR_CARD_NO,STATUS,MOBILE_NO,GROUPNAME,REMARKS,DISPLAY_NAME) VALUES('" + date + "','" + ymd_dt + "','" + pan + "','" + adhar + "','" + stauts + "','" + mobile + "','" + name + "','"+remarks+"','"+display_name+"')";
                    Statement stmt = con.createStatement();
                   int a= stmt.executeUpdate(query);
                   if (a == 1){
                       Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();

                       ed_name.setText("");
                       ed_pan_no.setText("");
                       ed_adhar_no.setText("");
                       ed_mobile_no.setText("");
                       spinner_status.setSelection(0);
                       tv_date_view.setText("");
                       ed_remarks.setText("");
                       showdetailmethod();
                       form_layout.setVisibility(View.GONE);
                       gridlinearlayout.setVisibility(View.VISIBLE);
                       add_clinet_detail.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                   }else {
                       Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();

                   }


            }

        } catch (Exception ex) {
            Toast.makeText(this, "Failed Please Try Again", Toast.LENGTH_SHORT).show();

        }


    }

    private void curentdateMethod() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String todate= dateFormat.format(currentdate());
        String  currentdate = todate.toString(); //here you get current dat
        tv_date_view.setText(currentdate);

    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
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
                sessionManager.logoutUser();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }





        //    check mac statuss start***************************
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void checkConnection(){
        if(isOnline()){

            macMethod();

        }else{

            macStatus();
        }
    }




    private void macMethod() {

        String url = "http://registermykenan.com/api.php?key="+ stringlicense;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status=jsonResponse.toString().split("\\{|\\}")[1];
                    String temst=status.toString().replaceAll(":",",").replaceAll("\"","");
                    String[] namesList = temst.split(",");
                    ArrayList<String> sss=new ArrayList<>();
                    for (int i = 0; i < namesList.length; i++){
                        sss.add(namesList[i]);

                    }
                    if (sss.contains(stringmac)){
                        satus="true";
                        macStatusupdate();
                    }else {
                        satus="false";
                        macStatusupdate();
                        startActivity(new Intent(MainActivity.this,  LincenseActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            //utoCompleteTextView_customerName.setError("Not found Customer");
                            // Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }
    private void macStatusupdate() {

        db.execSQL("UPDATE MAC  SET status ='" + satus + "' WHERE license ='" + stringlicense + "'");


    }

    private void macStatus() {
        Cursor c1 = db.rawQuery("SELECT * FROM MAC ", null);
        if (c1.moveToFirst()) {
            do {
                status_check=  c1.getString(c1.getColumnIndex("status"));
            } while (c1.moveToNext());

            macMethod();
        }else{
            //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        }
        c1.close();
        if (status_check.equals("false")){
            startActivity(new Intent(MainActivity.this,  LincenseActivity.class));
            finish();
        }else {

        }

    }

//    end mac


    private Date currentdate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTime();
    }




    public void showdetailmethod() {
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {
                String quuerycalender="SELECT ENTRYID,VCHDT,VCHDT_Y_M_D,PAN_NO,ADHAAR_CARD_NO,STATUS,MOBILE_NO,GROUPNAME,REMARKS FROM dt_prc  ORDER BY GROUPNAME";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(quuerycalender);

                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("A", String.valueOf(rs.getInt("ENTRYID")));
                    datanum.put("B", "Date: "+rs.getString("VCHDT"));
                    datanum.put("C", "PAN : "+rs.getString("PAN_NO"));
                    datanum.put("D", "Adhar: "+rs.getString("ADHAAR_CARD_NO"));
                    datanum.put("E", "Status: "+rs.getString("STATUS"));
                    datanum.put("F", "Mob.: "+rs.getString("MOBILE_NO"));
                    datanum.put("G", "Name: "+rs.getString("GROUPNAME"));
                    datanum.put("H", "Remarks: "+rs.getString("REMARKS"));
                   // Toast.makeText(this, ""+ String.valueOf(rs.getInt("ENTRYID")), Toast.LENGTH_SHORT).show();
                    data.add(datanum);
                }
                String[] from = {"G", "F","C","D","B","E","H"};
                final int[] views = {R.id.gr_voucherno, R.id.gr_date,R.id.gr_code,R.id.gr_parcelno, R.id.gr_name, R.id.gr_narretion,R.id.gr_remarks};
                ADA = new SimpleAdapter(MainActivity.this,
                        data, R.layout.grid_view_layout, from, views);
                gridviewreport.setAdapter(ADA);
                gridviewreport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
                        form_layout.setVisibility(View.VISIBLE);
                        gridlinearlayout.setVisibility(View.GONE);
                        update_delete_layout_client.setVisibility(View.VISIBLE);
                        submit_btn_form.setVisibility(View.GONE);
                        entryid=0;
                        add_clinet_detail.setBackgroundResource(R.drawable.ic_close_black_24dp);
                        HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
                        entryid=Integer.valueOf(map.get("A"));
                        ed_name.setText(map.get("G").replaceAll("Name: ",""));
                        ed_pan_no.setText(map.get("C").replaceAll("PAN : ",""));
                        ed_adhar_no.setText(map.get("D").replaceAll("Adhar: ",""));
                        ed_mobile_no.setText(map.get("F").replaceAll("Mob.: ",""));
                        tv_date_view.setText(map.get("B").replaceAll("Date: ",""));
                        ed_remarks.setText(map.get("H").replaceAll("Remarks: ",""));
                        if (map.get("E").replaceAll("Status: ","").matches("Y")) {
                           spinner_status.setSelection(1);
                        } else if (map.get("E").replaceAll("Status: ","").matches("N")) {
                            spinner_status.setSelection(2);
                        }


                    }
                });



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
