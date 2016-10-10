package co.ar_smart.www.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.living.LoginRegisterActivity;
import co.ar_smart.www.living.R;

public class RegisteredHubActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_hub);
        final Context mContext = this;
        Button btnEndRegister = (Button) findViewById(R.id.btnEndRegister);
        if(btnEndRegister!=null)
        {
            btnEndRegister.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(mContext, LoginRegisterActivity.class);
                    startActivity(i);
                }
            });
        }
    }
}
