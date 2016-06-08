package co.ar_smart.www.register;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.living.R;

/**
 * Class for the activity that tells the user to connect the hub to the energy outlet and to press the back button
 */
public class ConnectOutletActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_outlet);
        setTitle(R.string.title_activity_connect_outlet);

        final Context mContext = this;

        //when the user clicks in the button the application goes to the activity that gets the network information
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
