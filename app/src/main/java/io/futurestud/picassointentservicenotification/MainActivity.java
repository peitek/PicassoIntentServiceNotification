package io.futurestud.picassointentservicenotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    private void startIntentService(int option) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GcmIntentService.class);
        intent.putExtra(GcmIntentService.NOTIFICATION_OPTION, option);
        startService(intent);
    }

    @OnClick(R.id.button_picasso_crash)
    public void picassoCrash(){
        startIntentService(0);
    }


    @OnClick(R.id.button_double_sound)
    public void doubleSound(){
        startIntentService(1);
    }


    @OnClick(R.id.button_double_display)
    public void doubleDisplay(){
        startIntentService(2);
    }
}
