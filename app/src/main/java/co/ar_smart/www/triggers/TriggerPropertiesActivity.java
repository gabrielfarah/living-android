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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Calendar;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.Trigger;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class TriggerPropertiesActivity extends AppCompatActivity
{

    boolean[] selected;
    TextView txv_begin_time;
    TextView txv_end_time;
    TextView txv_days;
    Calendar horaInicial;
    Calendar horaFinal;
    LinearLayout lyo_hour;
    LinearLayout lyo_day;
    private String API_TOKEN;
    ArrayList<Mode> modes;
    private Trigger trigger;
    private boolean binary_sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modes = getIntent().getParcelableArrayListExtra("modes");
        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);
        trigger = getIntent().getParcelableExtra(EXTRA_OBJECT);
        binary_sensor = getIntent().getBooleanExtra("binary_sensor", false);
        RangeSeekBar rsb_range = (RangeSeekBar) findViewById(R.id.rsb_range);
        if (rsb_range != null)
        {
            if (binary_sensor)
            {
                rsb_range.setVisibility(RangeSeekBar.GONE);
            }
            else
            {
                //TODO find range maximum and minimun
                //rsb_range.setRangeValues(trigger.getEndpoint().getMaxValue(), trigger.getEndpoint().getMinValue());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_listview, modesToArray());
        ListView listView = (ListView) findViewById(R.id.lsv_modes);
        if (listView != null)
        {
            listView.setAdapter(adapter);
        }
        selected = Constants.boolSelectedDaysArray;
        horaInicial = Calendar.getInstance();
        horaFinal = Calendar.getInstance();
        lyo_hour = (LinearLayout) findViewById(R.id.lyo_hour);
        lyo_day = (LinearLayout) findViewById(R.id.lyo_day);
        int hours;
        if (savedInstanceState != null)
        {
            restoreSavedInstance(savedInstanceState);
            hours = horaFinal.get(Calendar.HOUR_OF_DAY);
        }
        else
        {
            horaFinal.add(Calendar.HOUR_OF_DAY, 1);
            hours = horaFinal.get(Calendar.HOUR_OF_DAY);
        }
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
        Button btn_day = (Button) findViewById(R.id.btn_day);
        Button btn_hour = (Button) findViewById(R.id.btn_hour);
        txv_begin_time = (TextView) findViewById(R.id.txv_begin_time);
        txv_end_time = (TextView) findViewById(R.id.txv_end_time);
        txv_days = (TextView) findViewById(R.id.txv_days);
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
                    setTime(txv_begin_time, "Select beginning time:", horaInicial);
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
                    setTime(txv_end_time, "Select ending time:", horaFinal);
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
                    builder.setTitle("Select Days");
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
                                Log.d("DEBUG", respuesta);
                                txv_days.setText(respuesta.substring(0, respuesta.length() - 2));
                            }

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_trigger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.save:
                saveTrigger();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTrigger()
    {
        //TODO make sure there is no missing for saving the trigger
    }

    private String[] modesToArray()
    {
        String[] result = new String[modes.size()];
        for (int i = 0; i < modes.size(); i++)
        {
            result[i] = modes.get(i).getName();
        }
        return result;
    }

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
