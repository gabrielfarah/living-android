package co.ar_smart.www.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.User;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;
import static co.ar_smart.www.helpers.Constants.PREF_JWT;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;

public class ManagementUserActivity extends AppCompatActivity {

    private String API_TOKEN;
    private User USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_user);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        USER = intent.getParcelableExtra(EXTRA_OBJECT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_user_management_title));
        }

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyticsApplication.getInstance().trackEvent("User Action", "Logout", "The user logged out");
                    successfulLogout();
                }
            });
        }

        Button profileButton = (Button) findViewById(R.id.edit_profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEditProfileActivity();
                }
            });
        }

        Button changePasswordButton = (Button) findViewById(R.id.change_password_button);
        if (changePasswordButton != null) {
            changePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChangePasswordActivity();
                }
            });
        }
    }

    private void openChangePasswordActivity() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, USER);
        startActivity(intent);
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(this, EditAccountActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, USER);
        startActivityForResult(intent, RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will clear the saved credentials of the user in the shared preferences.
     * It will also redirect the user to the login activity.
     */
    private void successfulLogout() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_EMAIL);
        editor.remove(PREF_PASSWORD);
        editor.remove(PREF_JWT);
        editor.remove(PREF_HUB);
        editor.apply();
        openLoginActivity();
    }

    /**
     * This method will open the login activity.
     */
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) { //TODO why the fuck is -1 (RESULT_CANCELED)?
            USER = data.getExtras().getParcelable(EXTRA_OBJECT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
