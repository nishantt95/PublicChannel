package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Nishant on 12/28/2015.
 */
public class FbLogin extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView tv;

    AccessToken accessToken = null;
    com.facebook.Profile profile = null;

    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    String name=null;
    Uri imageUri=null;
    Bitmap bm=null;

    Button bt;
    ImageView im;
    String id="";
    String email=null;
    String gender=null;
    GraphRequest request=null;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    String userSap=null;

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    String error="";

    ProgressDialog pd,pdInc;

    String isUpdated="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.fblogin);


        Log.e("FB Login", "#1");

        pd = new ProgressDialog(FbLogin.this);
        pd.setMessage("Synchronizing the data with Facebook...");
        pd.setCancelable(false);
        pd.dismiss();


        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        pdInc = new ProgressDialog(FbLogin.this);
        pdInc.setMessage("Adding 50 Views to your account...");
        pd.setCancelable(false);
        pdInc.dismiss();



        userSap = sp.getString("sapid", "0");


        //Facebook------------

        tv = (TextView) findViewById(R.id.textViewpw);
        bt = (Button) findViewById(R.id.button);
        im = (ImageView) findViewById(R.id.imageView);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, mCallBack);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setVisibility(View.INVISIBLE);
            }
        });

        Log.e("FB Login", "#3");


        bt.setVisibility(View.INVISIBLE);
        im.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);


        Log.e("FB Login", "#4");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setVisibility(View.INVISIBLE);
            }
        });



        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
                tv.setText("Please Wait...");
                getData();
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {
                profile = currentProfile;
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            accessToken = loginResult.getAccessToken();
            profile = com.facebook.Profile.getCurrentProfile();


                request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError() != null) {
                            //error
                            Toast.makeText(FbLogin.this, "Graph error!", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                email = object.getString("email").toString();
                                gender = object.getString("gender").toString();
                                getData();
                            } catch (Exception e) {
                                Toast.makeText(FbLogin.this, "GE Exc - " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,gender");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }

    };


    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    //Not needed here................................




    //Not needed here................................

    public void getData()
    {
        if (profile != null) {
            name = profile.getName();
            //loginButton.setVisibility(View.INVISIBLE);
            imageUri = profile.getProfilePictureUri(400,400);
        }else{
            profile = Profile.getCurrentProfile();
        }

        //String sap, String email, String gender, String piclink

        if(email!=null && gender!=null && imageUri!=null)
        {
            new Update().execute(userSap,email,gender,imageUri.toString());
        }
        else
        {
            bt.setVisibility(View.VISIBLE);
        }

    }


    public class Update extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //String sap, String email, String gender, String piclink
            isUpdated = updateServer(params[0],params[1],params[2],params[3]);
            isUpdated = String.valueOf(isUpdated.charAt(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(isUpdated.equalsIgnoreCase("1"))
            {
                ed.putString("email",email);
                ed.putString("imageUri",imageUri.toString());
                ed.putString("facebook", "yes");
                ed.commit();

                AlertDialog.Builder adb = new AlertDialog.Builder(FbLogin.this);
                adb.setMessage("Connected to Facebook Successfully!")
                        .setTitle("Connected!")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(FbLogin.this,MainActivity.class);
                                startActivity(it);
                                finish();
                            }
                        });
                AlertDialog al = adb.create();
                al.show();

                // add 50 views to the user...
            }else{
                AlertDialog.Builder adb = new AlertDialog.Builder(FbLogin.this);
                adb.setMessage("Cannot connect to the facebook!\n"+error)
                        .setTitle("Something went wrong!")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }
        }
    }


    @SuppressLint("NewApi")
    public String updateServer(String sap, String email, String gender, String piclink)
    {
        int z=1;
        try {

            URL url = new URL(Global.fbLoginUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(sap,"UTF-8")+"&"+
                    URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"+
                    URLEncoder.encode("gender", "UTF-8")+"="+URLEncoder.encode(gender, "UTF-8")+"&"+
                    URLEncoder.encode("piclink", "UTF-8")+"="+URLEncoder.encode(piclink, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"ISO-8859-1"));

            String response ="";

            String line="";

            while((line=bufferedReader.readLine())!=null)
            {
                response+=line;
            }

            inputStream.close();

            bufferedReader.close();

            return response;

            //ps = con.prepareStatement("update stu set email='"+email+"', gender='"+gender+"', piclink='"+piclink+"' where sapid='"+sap+"'");

        }
        catch (Exception e){ return "#"; }

    }


}
