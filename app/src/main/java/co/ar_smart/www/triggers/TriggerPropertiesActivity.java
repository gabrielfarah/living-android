package co.ar_smart.www.triggers;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.Trigger;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class TriggerPropertiesActivity extends AppCompatActivity
{

    /**
     * Array of booleans that represents the days selected for the trigger
     */
    boolean[] selected;
    /**
     * Text view for the start time for the trigger
     */
    TextView txv_begin_time;
    /**
     * Text view for the end time for the trigger
     */
    TextView txv_end_time;
    /**
     * TextView that shows the selected days for the trigger
     */
    TextView txv_days;
    /**
     * Calendar that represents the start time for the trigger
     */
    Calendar horaInicial;
    /**
     * Calendar that represents the end time for the trigger
     */
    Calendar horaFinal;
    /**
     * Linear Layout that shows the UI to give the trigger an specific start and end hour
     */
    LinearLayout lyo_hour;
    /**
     * Linear layout that shows the UI to give the trigger an array of days to execute
     */
    LinearLayout lyo_day;
    /**
     * List of modes of the user
     */
    ArrayList<Mode> modes;
    /**
     * RangeSeekBar that gives the range for the trigger in case the sensor is not binary
     */
    RangeSeekBar rsb_range;
    /**
     * Current API TOKEN for the user
     */
    private String API_TOKEN;
    /**
     * Trigger that have the lastest value
     */
    private Trigger trigger;
    /**
     * Boolean that represent if the sensor of the trigger is binary or not
     */
    private boolean binary_sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_properties);
        //Get the initial values for the atributes form the last activity from Intent
        modes = getIntent().getParcelableArrayListExtra("modes");
        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);
        trigger = getIntent().getParcelableExtra(EXTRA_OBJECT);
        // This variable is sent from the parent activity to flag if the sensor is binary or multilevel
        binary_sensor = getIntent().getBooleanExtra(Constants.EXTRA_ADITIONAL_MESSAGE, false);
        rsb_range = (RangeSeekBar) findViewById(R.id.rsb_range);
        //makes the rangeSeekBar invincible if the sensor is binary
        if (rsb_range != null)
        {
            if (binary_sensor)
            {
                rsb_range.setVisibility(RangeSeekBar.GONE);
            }
            else
            {
                rsb_range.setRangeValues(-50, 100);
            }
        }
        //Adapter to show the available modes
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_listview, modesToArray());
        ListView listView = (ListView) findViewById(R.id.lsv_modes);
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    trigger.setPayload(modes.get(position).getPayload());
                }
            });
        }
        selected = Constants.boolSelectedDaysArray;
        horaInicial = Calendar.getInstance();
        horaFinal = Calendar.getInstance();
        lyo_hour = (LinearLayout) findViewById(R.id.lyo_hour);
        lyo_day = (LinearLayout) findViewById(R.id.lyo_day);
        int hours;
        // Verifies if there is a savedInstance, used when the user turn the device to portrait o landscape
        if (savedInstanceState != null)
        {
            restoreSavedInstance(savedInstanceState);
        }
        else
        {
            horaFinal.add(Calendar.HOUR_OF_DAY, 1);
        }

        hours = horaFinal.get(Calendar.HOUR_OF_DAY);
        String respuesta = "";
        for (int i = 0; i < selected.length; i++)
        {
            if (selected[i])
            {
                respuesta += Constants.getHashMapDaysFromString().get(Constants.daysArray[i]) + ", ";
            }
        }
        respuesta = respuesta.substring(0, respuesta.length() - 2);
        int hour = horaInicial.get(Calendar.HOUR_OF_DAY);
        int minute = horaInicial.get(Calendar.MINUTE);
        // Initialize the atributes with their corresponding UI part
        Button btn_day = (Button) findViewById(R.id.btn_day);
        Button btn_hour = (Button) findViewById(R.id.btn_hour);
        txv_begin_time = (TextView) findViewById(R.id.txv_begin_time);
        txv_end_time = (TextView) findViewById(R.id.txv_end_time);
        txv_days = (TextView) findViewById(R.id.txv_days);
        // Set the listeners for the different parts of the UI
        if (btn_day != null && lyo_day != null)
        {
            btn_day.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (lyo_day.getVisibility() == LinearLayout.GONE)
                    {
                        lyo_day.setVisibility(LinearLayout.VISIBLE);
                    }
                    else
                    {
                        lyo_day.setVisibility(LinearLayout.GONE);
                    }
                }
            });
        }
        if (btn_hour != null && lyo_hour != null)
        {
            btn_hour.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (lyo_hour.getVisibility() == LinearLayout.GONE)
                    {
                        lyo_hour.setVisibility(LinearLayout.VISIBLE);
                    }
                    else
                    {
                        lyo_hour.setVisibility(LinearLayout.GONE);
                    }
                }
            });
        }
        if (txv_begin_time != null)
        {
            txv_begin_time.setText(hour + ":" + minute);
            txv_begin_time.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setTime(txv_begin_time, getString(R.string.select_beginning_time), horaInicial);
                }
            });
        }
        if (txv_end_time != null)
        {

            txv_end_time.setText(hours + ":" + minute);
            txv_end_time.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setTime(txv_end_time, getString(R.string.select_ending_time), horaFinal);
                }
            });
        }
        if (txv_days != null)
        {
            txv_days.setText(respuesta);
            txv_days.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TriggerPropertiesActivity.this);
                    builder.setTitle(R.string.select_days);
                    builder.setMultiChoiceItems(Constants.daysArray, selected, new DialogInterface.OnMultiChoiceClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which,
                                            boolean isChecked)
                        {
                            selected[which] = isChecked;
                        }
                    });
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            String respuesta = "";
                            for (int i = 0; i < Constants.daysArray.length; i++)
                            {
                                if (selected[i])
                                {
                                    respuesta += Constants.getHashMapDaysFromString().get(Constants.daysArray[i]) + ", ";
                                }
                            }
                            if (respuesta.length() != 0)
                            {
                                txv_days.setText(respuesta.substring(0, respuesta.length() - 2));
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });
                    AlertDialog abc = builder.create();
                    abc.show();
                }
            });
        }
    }

    /**
     * Method called when the UI has a menu in the toolbar
     * @param menu - Menu to add
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_trigger_menu, menu);
        return true;
    }

    /**
     * Method called when the user click in the menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.save:
                if (!binary_sensor)
                {
                    trigger.setPrimary_value(rsb_range.getSelectedMinValue().intValue());
                    trigger.setSecondary_value(rsb_range.getSelectedMaxValue().intValue());
                }
                if (lyo_hour.getVisibility() == LinearLayout.VISIBLE)
                {
                    trigger.setMinute_of_day(new int[]{(horaInicial.get(Calendar.HOUR_OF_DAY) * 60) + horaInicial.get(Calendar.MINUTE), (horaFinal.get(Calendar.HOUR_OF_DAY) * 60) + horaFinal.get(Calendar.MINUTE)});
                }
                if (lyo_day.getVisibility() == LinearLayout.VISIBLE)
                {
                    int amountDays = 0;
                    for (int i = 0; i < selected.length; i++)
                    {
                        if (selected[i])
                        {
                            amountDays++;
                        }
                    }
                    int[] days = new int[amountDays];
                    int iterador = 0;
                    for (int i = 0; i < selected.length; i++)
                    {
                        if (selected[i])
                        {
                            days[iterador] = i;
                            iterador++;
                        }
                    }
                    trigger.setDays_of_the_week(days);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.notification_for_trigger)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                trigger.setNotify(true);
                                saveTrigger();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                trigger.setNotify(false);
                                saveTrigger();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method that make the requesto to save the trigger in the server
     */
    private void saveTrigger()
    {
        TriggerManager.addMode(trigger.getHubId(), trigger.getEndpoint().getId(), trigger, API_TOKEN, new TriggerManager.TriggerCallbackInterface()
        {
            @Override
            public void onFailureCallback()
            {
                Log.d("UPS","Something went wrong");
            }

            @Override
            public void onSuccessCallback(List<Mode> modes)
            {
                Log.d("UPS","Unexpected answer");
            }

            @Override
            public void onSuccessCallback()
            {
                Toast.makeText(getApplicationContext(), R.string.successfully_added_trigger, Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onUnsuccessfulCallback()
            {
                Toast.makeText(getApplicationContext(), R.string.error_doing_request, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * MEthod that returns the array of modes as a array of Strings
     * @return
     */
    private String[] modesToArray()
    {
        String[] result = new String[modes.size()];
        for (int i = 0; i < modes.size(); i++)
        {
            result[i] = modes.get(i).getName();
        }
        return result;
    }

    /**
     * Method that change the text of the given TextView for the time selected in the TimePicker
     * @param pTextView
     * @param title
     * @param customHour
     */
    public void setTime(final TextView pTextView, String title, Calendar customHour)
    {
        TimePickerDialog abc = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes)
            {
                pTextView.setText(hourOfDay + ":" + minutes);
                if (pTextView.equals(txv_begin_time))
                {
                    horaInicial.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    horaInicial.set(Calendar.MINUTE, minutes);
                }
                else
                {
                    horaFinal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    horaFinal.set(Calendar.MINUTE, minutes);
                }
            }
        }, customHour.get(Calendar.HOUR_OF_DAY), customHour.get(Calendar.MINUTE), true);
        abc.setTitle(title);
        abc.show();
    }

    /**
     * Method that save the Instance when the user rotate the device
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("fechaInicial", horaInicial.get(Calendar.HOUR_OF_DAY) + ":" + horaInicial.get(Calendar.MINUTE));
        outState.putString("fechaFinal", horaFinal.get(Calendar.HOUR_OF_DAY) + ":" + horaFinal.get(Calendar.MINUTE));
        outState.putBooleanArray("selected", selected);
        outState.putInt("specificHour", lyo_hour.getVisibility());
        outState.putInt("specificDay", lyo_day.getVisibility());
    }

    /**
     * Method that restore the values when the user rotate the device
     * @param savedInstanceState
     */

    public void restoreSavedInstance(Bundle savedInstanceState)
    {
        String[] horaTempInicial = savedInstanceState.getString("fechaInicial").split(":");
        horaInicial = Calendar.getInstance();
        horaInicial.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaTempInicial[0]));
        horaInicial.set(Calendar.MINUTE, Integer.parseInt(horaTempInicial[1]));
        String[] horaTempFinal = savedInstanceState.getString("fechaFinal").split(":");
        horaFinal = Calendar.getInstance();
        horaFinal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaTempFinal[0]));
        horaFinal.set(Calendar.MINUTE, Integer.parseInt(horaTempFinal[1]));
        selected = savedInstanceState.getBooleanArray("selected");
        if (savedInstanceState.getInt("specificHour") == LinearLayout.VISIBLE)
        {
            lyo_hour.setVisibility(LinearLayout.VISIBLE);
        }
        else
        {
            lyo_hour.setVisibility(LinearLayout.GONE);
        }
        if (savedInstanceState.getInt("specificDay") == LinearLayout.VISIBLE)
        {
            lyo_day.setVisibility(LinearLayout.VISIBLE);
        }
        else
        {
            lyo_day.setVisibility(LinearLayout.GONE);
        }
    }
}
