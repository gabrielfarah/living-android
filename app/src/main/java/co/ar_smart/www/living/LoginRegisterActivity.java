package co.ar_smart.www.living;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.register.LivingLocalConfigurationActivity;

/**
 * This activity allows a user to either login in or to register a new Living account.
 */
public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        Button loginButton = (Button) findViewById(R.id.toLoginButton);
        Button registerButton = (Button) findViewById(R.id.toRegisterButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLoginActivity();
                }
            });
        }
        if (registerButton != null) {
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openRegisterActivity();
                }
            });
        }
    }

    /**
     * This method opens the login activity for the user to input an email and password fields
     */
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * This method opens the register activity for the user to register
     */
    public void openRegisterActivity() {
        //TODO finish
        Intent intent = new Intent(this, LivingLocalConfigurationActivity.class);
        startActivity(intent);
    }
}
