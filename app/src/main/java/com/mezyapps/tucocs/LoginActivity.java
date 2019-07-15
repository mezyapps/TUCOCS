package com.mezyapps.tucocs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText textInputEditTextname,textInputEditTextpass;
    SessionManager sessionManager;
    ConectionAdmin connectionClass;
    ProgressBar pbbar;
    String pass,user_id;
    Connection con;
    SharedPreferences pref;

    String ASCII_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_login);
        textInputEditTextname = findViewById(R.id.ed_username_signin);
        textInputEditTextpass = findViewById(R.id.ed_user_pass_signin);
        Button login = findViewById(R.id.btn_signin);
        connectionClass = new ConectionAdmin();
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);



if (sessionManager.isLoggedIn()){
    sessionManager=new SessionManager(getApplicationContext());
    HashMap<String, String> user = sessionManager.getUserDetails();
    String userid=  user.get(SessionManager.KEY_password);
    String name=  user.get(SessionManager.KEY_NAME);

    dbconnectionmethod(name,userid);
}





            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ASCII_VALUE="";
                    pass=textInputEditTextpass.getText().toString().trim();

                    DoLogin  doLogin = new DoLogin();
                    doLogin.execute("");
                }
            });
        }




        public class DoLogin extends AsyncTask<String,String,String>
        {
            String z = "";
            String isSuccess = "";

            String name=textInputEditTextname.getText().toString();

            @Override
            protected void onPreExecute() {
                pbbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String r) {
                pbbar.setVisibility(View.GONE);
               // Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();

                if(isSuccess.equals("true")) {
                    Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
                    dbconnectionmethod(name,user_id);
                }else {
                    Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                if(name.trim().equals("")|| pass.trim().equals(""))
                    z = "Please enter User Name and Password";
                else
                {
                    try {
                        Connection con = connectionClass.CONNN();
                        if (con == null) {
                            z = "Error in connection with SQL server";
                        } else {
                            String query = "select USERID from UACCESS where USERNAME='" + name + "' and USERPASSWORD='" + pass + "'";
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(query);

                            if(rs.next())
                            {
                                user_id=  String.valueOf(rs.getInt("USERID"));
                               // sessionManager.createLoginSession(name,abbb);
                                z = "Successfuly";
                                isSuccess="true";

                            }
                            else
                            {
                                z = "Invalid Usename Or Password";
                                isSuccess = "false";
                            }

                        }
                    }
                    catch (Exception ex)
                    {
                        isSuccess = "false";
                        z = "Exceptions";
                    }
                }
                return z;
            }
        }




    private void dbconnectionmethod(String name,String abbb) {
        pref = this.getSharedPreferences("CON", Context.MODE_PRIVATE);
        String    quuerycalender = "Select DB.*,UA.DISPLAY_NAME from DB_TABLE AS DB INNER JOIN UACCESS AS UA ON UA.USERID=DB.USERID WHERE DB.USERID="+Integer.parseInt(abbb);
        try {
            Connection con = connectionClass.CONNN();
            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(quuerycalender);

                while (rs.next()) {
                    pref.edit().clear();
                    pref.edit().putString("print",  rs.getString("DISPLAY_NAME")).apply();
                    pref.edit().putString("ip", rs.getString("SERVER_IP")).apply();
                    pref.edit().putString("db", rs.getString("DB_NAME")).apply();
                    pref.edit().putString("us", rs.getString("DB_USERNAME")).apply();
                    pref.edit().putString("ps", rs.getString("DB_PASSWARD")).apply();
                    pref.edit().commit();
                    sessionManager.createLoginSession(name,abbb);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                    pbbar.setVisibility(View.GONE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
