package co.ar_smart.www.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.CHANGE_PASSWORD_URL;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.JSON;

public class ChangePasswordActivity extends AppCompatActivity {

    private String API_TOKEN;
    private User USER;
    private EditText old_pass;
    private EditText new_pass;
    private EditText new_pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        USER = intent.getParcelableExtra(EXTRA_OBJECT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_user_management_title));
        }
        old_pass = (EditText) findViewById(R.id.edit_text_old_password);
        new_pass = (EditText) findViewById(R.id.edit_text_new_password);
        new_pass2 = (EditText) findViewById(R.id.edit_text_new_password_repeat);
        Button updateButton = (Button) findViewById(R.id.change_password_button);
        if (updateButton != null) {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                }
            });
        }
    }

    private void changePassword() {
        String nOld_pass = old_pass.getText().toString();
        String nNew_pass = new_pass.getText().toString();
        String nNew_pass2 = new_pass2.getText().toString();
        if ((nOld_pass.isEmpty()) || (nNew_pass.isEmpty()) || (nNew_pass2.isEmpty())) {
            Constants.showCustomMessage(getApplicationContext(), getResources().getString(R.string.label_error_message_fill_fields));
        } else {
            if (!nNew_pass.equals(nNew_pass2)) {
                Constants.showCustomMessage(getApplicationContext(), getResources().getString(R.string.label_error_message_passwords_missmatch));
            } else {
                changePassword(nOld_pass, nNew_pass, API_TOKEN, new ChangePasswordCallbackInterface() {
                    @Override
                    public void onFailureCallback() {
                        showNoInternet();
                    }

                    @Override
                    public void onSuccessCallback() {
                        runOnUI(getResources().getString(R.string.label_message_passwords_success));
                        ChangePasswordActivity.this.finish();
                    }

                    @Override
                    public void onUnsuccessfulCallback() {
                        runOnUI(getResources().getString(R.string.label_error_message_passwords_failed));
                    }
                });
            }
        }
    }

    private void runOnUI(final String message) {
        ChangePasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.showCustomMessage(getApplicationContext(), message);
            }
        });
    }

    private void showNoInternet() {
        ChangePasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.showNoInternetMessage(getApplicationContext());
            }
        });
    }

    private void changePassword(String nOld_pass, String nNew_pass, String API_JWT, final ChangePasswordCallbackInterface callback) {
        final String json = "{\"new_password\":\"" + nNew_pass + "\",\"old_password\":\"" + nOld_pass + "\"}";
        Log.d("CREDENTIALS", json);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(CHANGE_PASSWORD_URL)
                .header("Authorization", "JWT " + API_JWT)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                Log.d("R", jsonData);
                response.body().close();
                if (!response.isSuccessful()) {
                    callback.onUnsuccessfulCallback();
                } else {
                    callback.onSuccessCallback();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go back
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface ChangePasswordCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will contain the API token obtained from the server in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the credentials provided were not correct.
         */
        void onUnsuccessfulCallback();

    }
}
