package co.ar_smart.www.register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.R;

public class CreatedUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_user);
    }

    /**
     * Redirects to Hub Register
     * @param v - View needed for onClick property
     */
    public void registerHub(View v)
    {
        //TODO fix to redirect to hub register
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

}

