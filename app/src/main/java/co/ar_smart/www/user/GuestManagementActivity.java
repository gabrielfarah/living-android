package co.ar_smart.www.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.ar_smart.www.helpers.GuestManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Guest;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;

public class GuestManagementActivity extends AppCompatActivity {

    private String API_TOKEN;
    private int PREFERRED_HUB_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_management);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        setUI();
    }

    private void setUI() {
        GuestManager.getGuests(PREFERRED_HUB_ID, API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(Guest guest) {

            }

            @Override
            public void onSuccessCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }
}
