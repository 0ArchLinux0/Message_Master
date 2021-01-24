package com.archlinux.message_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button b1;
    private Button b2;
    private TextView tv;
    private TextView tv2;
    private EditText et1;
    private EditText et2;
    private Context context;
    private boolean isNumber = false;

    private SmsBroadcastReceiver smsBroadcastReceiver;
    static SmsManager smsManager = SmsManager.getDefault();
    private String show_master_number;
    private String show_master_number2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences( getString(R.string.pref_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor = sharedPref.edit();

        b2 = (Button) findViewById(R.id.button2);
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);

        show_master_number = sharedPref.getString(getString(R.string.key_master_number1), getString(R.string.no_registered_number1));
        show_master_number2 = sharedPref.getString(getString(R.string.key_master_number2), getString(R.string.no_registered_number2));


        tv.setText(show_master_number);
        tv2.setText(show_master_number);
        //Get persmission at runtime
       /* String[] PERMISSIONS={
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        }*/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS}, 1000);
        /*ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);*/

        smsBroadcastReceiver = new SmsBroadcastReceiver("", "");
        //smsBroadcastReceiver = new SmsBroadcastReceiver(BuildConfig.SERVICE_NUMBER, BuildConfig.SERVICE_CONDITION);
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String smsSender, String smsBody){ //smsSender has no hyphen
                //production
                //String numberWithoutHyphen = show_master_number.substring(0,4)+show_master_number.substring(5,8)+show_master_number.substring(9,12);
                //String numberWithoutCountryCode = "" + show_master_number.substring(1);
                //test
                //String numberWithoutHyphen = show_master_number.substring(0,3)+show_master_number.substring(4,8)+show_master_number.substring(9,13);
                //Log.d("smsSender number", smsSender);
                //Log.d("numberwithoutHyphen", numberWithoutHyphen);
                String numberWithoutCountryCode = show_master_number.substring(3);
                String numberWithout = smsSender.substring(1);
                //tv.setText(smsSender);
                Toast.makeText(context, "const:"+smsSender+",sender:"+show_master_number+",eval:"+smsSender.equals(show_master_number), Toast.LENGTH_LONG).show();

                if((smsSender.equals(show_master_number) || smsSender.equals(show_master_number2))){
                    //production
                    String clientNumber = smsBody.substring(0,smsBody.indexOf("@"));
                    String bodyText = smsBody.substring(smsBody.indexOf("@") + 1);
                    String toast = "From master to client: " + clientNumber;
                    Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
                    //String clientNumber = smsBody.substring(0,4)+"-"+smsBody.substring(4,7)+"-"+smsBody.substring(7,10);
                    sendSMS(clientNumber, bodyText);
                    //test
                    /*String Body = smsBody.substring(12);
                    Body += "success!";
                    String clientNumber = smsBody.substring(0,3)+"-"+smsBody.substring(3,7)+"-"+smsBody.substring(7,11);
                    sendSMS(clientNumber, Body);*/
                }
                else{
                    //Toast.makeText(context, "From " + smsSender, Toast.LENGTH_LONG).show();
                    smsBody = smsSender +"@"+ smsBody;
                    sendSMS(show_master_number, smsBody);
                    //Toast noticeToast = Toast.makeText(context, getString(R.string.notify_received), Toast.LENGTH_SHORT)
                }
            }
        });

        final boolean permissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        //Log.d("permission", String.valueOf(permissionGranted));
        /*b1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("button clicked", "--");
                Log.d("permission", String.valueOf(permissionGranted));
                //smsManager.sendTextMessage("070-3966-9382", null, "testing", null, null);
                String number = "070-3966-9382";
                String text = "button clicked: test message ----!";
                sendSMS(number, text);
                *//*try {
                    Runtime.getRuntime().exec("am start -a android.intent.action.SENDTO -d sms:+1-222-333-444 --es sms_body 'aaa'  --ez exit_on_sent true");
                    Runtime.getRuntime().exec("input keyevent 22");
                    Runtime.getRuntime().exec("input keyevent 22");
                    Runtime.getRuntime().exec("input keyevent 66");
                } catch(Exception e){
                    e.printStackTrace();
                }*//*
            }*/

            /*private void sendSMS(String number, String text){
                smsManager.sendTextMessage( number, null, text, null, null);
            }*/
        //});

        b2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String raw_masterNumber = et1.getText().toString();
                String raw_masterNumber2 = et2.getText().toString();
                //production
                //String numberWithHyphen =  raw_masterNumber.substring(0,4)+"-"+raw_masterNumber.substring(4,7)+"-"+raw_masterNumber.substring(7,10);
                String numberWithCountryCode = "+" + raw_masterNumber;
                String numberWithCountryCode2 = "+" + raw_masterNumber2;
                //test
                //String numberWithHyphen =  raw_masterNumber.substring(0,3)+"-"+raw_masterNumber.substring(3,7)+"-"+raw_masterNumber.substring(7,11);
                show_master_number = numberWithCountryCode;
                show_master_number2 = numberWithCountryCode2;
                if(show_master_number!="" || show_master_number == null){
                    prefEditor.putString(getString(R.string.key_master_number1), numberWithCountryCode);
                    tv.setText(numberWithCountryCode);
                }
                if(show_master_number2!="" || show_master_number2 == null){
                    prefEditor.putString(getString(R.string.key_master_number2), numberWithCountryCode2);
                    tv2.setText(numberWithCountryCode2);
                }
                prefEditor.apply();
                et1.setText("");
                et2.setText("");
            }
        });


    }

    public void sendSMS(String number, String text) {
        smsManager.sendTextMessage(number, null, text, null, null);
        //smsManager.sendTextMessage("070-3966-9382", null, "testing", null, null);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(smsBroadcastReceiver);
        super.onDestroy();
    }
}
