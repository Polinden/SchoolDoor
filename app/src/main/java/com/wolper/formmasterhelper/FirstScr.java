package com.wolper.formmasterhelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.Date;
import org.apache.commons.validator.routines.UrlValidator;






public class FirstScr extends AppCompatActivity {

    private volatile boolean commingin=true;
    private TextView textView_invite;
    private final int SERVER_SETUP_REQUEST=25;
    private final int SERVER_SECURE_REQUEST=26;
    MainStorageSingleton mainStorageSingleton;
    HttpRequestTask httpTask;
    SharedPreferences sPref;


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

        //CreateStorage
        mainStorageSingleton=MainStorageSingleton.getInstance();


        //Restore view content after phone changing or waking up
        textView_invite = (TextView) findViewById(R.id.text_scanned);
        if (savedInstanceState!=null) {
            textView_invite.setText(savedInstanceState.getString("uniqID", ""));
            commingin=savedInstanceState.getBoolean("inout", true);
        }

        //Set animation
        showQueueLength();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        textView_invite.setAnimation(anim);


        //Create or restore AsyncAnction infinite cycle
        httpTask = (HttpRequestTask) getLastCustomNonConfigurationInstance();
        if (httpTask==null) {
            httpTask = new HttpRequestTask();
            httpTask.execute();
            restoreSettings();
        }
        httpTask.link(this);
    }



    //Setting button reaction
    public void onClicButtons(View target) {
        switch (target.getId()) {
            case R.id.button_scan_out:
                setComming(false);
                break;
            case R.id.button_scan_in:
                setComming(true);
                break;
        }
        doScan();
    }


    //Save link to external AsyncTask
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (httpTask!=null) httpTask.unlink();
        return httpTask;
    }


    //Save configuration
    @Override
    public void onPause() {
        super.onPause();
        saveSettings();
    }


    //Save view content when phone stops or changes position
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("uniqID", (String) textView_invite.getText());
        bundle.putBoolean("inout", commingin);
    }


    //Stop the infinite cycle on finishing the app
    @Override
    protected void onDestroy(){
        if (isFinishing())
            if (httpTask!=null) httpTask.cancel(false);
        super.onDestroy();
    }



    //Getting result from called activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != Activity.RESULT_OK)
            return;

        //Getting Server Setup result
        if (requestCode==SERVER_SETUP_REQUEST) {
            String s_srv=intent.getStringExtra("server");
            UrlValidator urlValidator = new UrlValidator(new String [] {"http","https"});
            if (!urlValidator.isValid(s_srv))
                    Toast.makeText(FirstScr.this, "???????????????? ???????????? ???????????? ??????????????", Toast.LENGTH_SHORT).show();
                else {
                    mainStorageSingleton.server=s_srv;
                    Toast.makeText(FirstScr.this, "???????????? - "+s_srv, Toast.LENGTH_SHORT).show();
                    httpTask.cleanError();
                }
        }

        //Getting Password Setup result
        if (requestCode==SERVER_SECURE_REQUEST) {
            String s_psw=intent.getStringExtra("password");
            mainStorageSingleton.password=s_psw;
            Toast.makeText(FirstScr.this, "???????????? - "+s_psw, Toast.LENGTH_LONG).show();
        }


        //getting Scan Result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();

            //showing Scan Result on the screen and sending to server
            sendScanResult(commingin, scanContent);
            showQueueLength();
        }
    }


    //Changing in-out mode
    private void setComming(boolean b) {
        commingin=b;
    }


    //Show Queue Length
    private void showQueueLength() {
        textView_invite.setText(customAnimation(mainStorageSingleton.getQueue().size()));
    }


    //Scanninig barcode
    private void doScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(FirstScr.this);
        scanIntegrator.initiateScan();
    }



    //Sending scan results to server
    private void sendScanResult(boolean commingin, String scanContent) {
        PlayLoad playLoad = new PlayLoad();
        playLoad.setCode(scanContent);
        playLoad.setInout(commingin?"in": "out");
        playLoad.setDate(new Date().getTime());
        mainStorageSingleton.getQueue().offer(playLoad);
        httpTask.cleanError();
    }



    //Save and restore settings persistently
    private void saveSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("fm_settings1", mainStorageSingleton.server);
        ed.putString("fm_settings2", mainStorageSingleton.password);
        ed.commit();
    }


    //Restore after waking up
    private void restoreSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        mainStorageSingleton.server = sPref.getString("fm_settings1", "");
        mainStorageSingleton.password = sPref.getString("fm_settings2", "");
    }


    //Animation for status string
    private String customAnimation(int x){
        String s="";
        if (x==0) {s="?????????????? ??????????"; textView_invite.setTextColor(Color.GREEN);}
        if (x>0) {s=">"+x+"??????"; textView_invite.setTextColor(Color.RED);}
        if (x>2) s=">>>"+x+"??????";
        if (x>4) s=">>>>"+x+"??????";
        if (x>6) s=">>>>>"+x+"??????";
        return s;
    }




    //Nested class
    //Class for sending scan result (POST) to REST service asyncronously
    private class HttpRequestTask extends AsyncTask<Void, String, Void> {

        private FirstScr activity;
        private volatile boolean hasError;

        public void link(FirstScr activity){
            this.activity=activity;
        }

        public void unlink(){
            this.activity=null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {

                    //check if interrupted and finish if work is done
                    if (isCancelled())
                        if (mainStorageSingleton.getQueue().size()==0) return null;

                    PlayLoad playload=mainStorageSingleton.getQueue().poll();
                    if (playload==null) continue;
                    SendRest sendRest = SendRest.initWithServerName(mainStorageSingleton.server).
                            setPassword(mainStorageSingleton.password).prepareForSend(playload);

                    //on any error we will return playload to the queue and signal for error (only once)
                    if (!sendRest.senmMe()) {
                        mainStorageSingleton.getQueue().offer(playload);
                        publishProgress(sendRest.getError());
                    }
                    else hasError=false;
            }
        }

        @Override
        protected void onProgressUpdate (String ...v) {
            if (!hasError) Toast.makeText(FirstScr.this, "???????????? ??????????????-"+v[0], Toast.LENGTH_LONG).show();
            hasError=true;
        }

        public void cleanError(){
            hasError=false;
        }

    }

}








