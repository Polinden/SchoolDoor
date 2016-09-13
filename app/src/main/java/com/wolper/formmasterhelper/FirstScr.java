package com.wolper.formmasterhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class FirstScr extends AppCompatActivity {

    private boolean commingin=true;
    private TextView textView_invite;
    private final int SERVER_SETUP_REQUEST=25;
    private final int SERVER_SECURE_REQUEST=26;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //this menu item starts a new activity for server setting up
            case R.id.server_settings:
                Intent myIntent1 = new Intent(FirstScr.this, SecondScr.class);
                startActivityForResult(myIntent1, SERVER_SETUP_REQUEST);
                return true;
            case R.id.server_security:
                Intent myIntent2 = new Intent(FirstScr.this, ThirdScr.class);
                startActivityForResult(myIntent2, SERVER_SECURE_REQUEST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_scr);


        //Setting up listeners for button press events
        Button button_scan_in = (Button) findViewById(R.id.button_scan_in);
        button_scan_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setComming(true);
                doScan();
            }
        });


        Button button_scan_out = (Button) findViewById(R.id.button_scan_out);
        button_scan_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setComming(false);
                doScan();
            }
        });


        //Restore view content after phone changing or waking up
        textView_invite = (TextView) findViewById(R.id.text_scanned);
        if (savedInstanceState!=null) {
            textView_invite.setText(savedInstanceState.getString("uniqID", ""));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != Activity.RESULT_OK)
            return;

        //Getting Server Setup result
        if (requestCode==SERVER_SETUP_REQUEST) {
            Toast.makeText(FirstScr.this, "Сервер - "+intent.getStringExtra("server"), Toast.LENGTH_LONG).show();
        }

        if (requestCode==SERVER_SECURE_REQUEST) {
            Toast.makeText(FirstScr.this, "Пароль - "+intent.getStringExtra("password"), Toast.LENGTH_LONG).show();
        }

        //getting Scan Result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            //showing Scan Result on the screen and sending to server
            textView_invite.setText(scanContent);
            sendScanResult(commingin, scanContent);
        }
    }





    //Changing in/out mode
    private void setComming(boolean b) {
        commingin=b;
    }



    //Scanninig barcode
    private void doScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(FirstScr.this);
        scanIntegrator.initiateScan();
    }



    //Sending scan results to server
    private void sendScanResult(boolean commingin, String scanContent) {
        new HttpRequestTask().execute();
    }


    //Save view content when phone stops or changes position
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("uniqID", (String) textView_invite.getText());
    }





    //Nested class
    //Class for sending scan result (POST) to REST service asyncronously
    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            //toDo Spring REST
            return null;
        }

        @Override
        protected void onPostExecute(String greeting) {
            Toast.makeText(FirstScr.this, "Posting to server...", Toast.LENGTH_LONG).show();
            //toDo Spring REST
        }

    }

}








