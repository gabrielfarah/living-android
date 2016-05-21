package co.ar_smart.www.register;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.R;

public class ConnectOutletActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_outlet);
        setTitle(R.string.title_activity_connect_outlet);
        final Context mContext = this;
        Button btn_co_continue = (Button)findViewById(R.id.btn_co_continue);
        if (btn_co_continue != null)
        {
            btn_co_continue.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(mContext, LivingLocalConfigurationActivity.class);
                    startActivity(i);
                }
            });
        }

    }
}
