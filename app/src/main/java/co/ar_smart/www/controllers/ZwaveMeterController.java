package co.ar_smart.www.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.EndpointManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

/**
 * Created by Gabriel on 8/31/2016.
 * https://static-s.aa-cdn.net/img/ios/978311570/0475f85bf180dc955f6c8f6a377c0a25
 */
public class ZwaveMeterController extends AppCompatActivity {

    private String API_TOKEN = "";
    private Endpoint endpoint;
    private int PREFERRED_HUB_ID = -1;
    private ArrayList<MeterData> measurements = new ArrayList<>();
    private LineChart mChart;
    private double ENERGY_COST = 452.81; //TODO validar si este es el precio que es
    private ArrayList<Float> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zwave_meter_controller);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(endpoint.getName());
        }
        mChart = (LineChart) findViewById(R.id.meterChart);
        //Get the endpoint data of the last month
        getEndpointData(30);
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
     * This method obtains all the measurements of the endpoint from the API
     *
     * @param days_delta
     */
    private void getEndpointData(int days_delta) {
        EndpointManager.getEndpointData(API_TOKEN, PREFERRED_HUB_ID, endpoint.getId(), days_delta, new EndpointManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONArray data) {
                Type listType = new TypeToken<ArrayList<MeterData>>() {
                }.getType();
                measurements = new Gson().fromJson(data.toString(), listType);
                drawUI();
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToEndpointData", "Was a bad request?:" + API_TOKEN + " " + endpoint.toString());
            }
        });
    }

    /**
     * This method perform all the UI operations on the main thread
     */
    private void drawUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Entry> entries = new ArrayList<>();
                double sum = 0;
                long min = measurements.get(0).getCreated_at().getTime();
                for (MeterData dp : measurements) {
                    // turn your data into Entry objects
                    Entry e = new Entry((float) (dp.getCreated_at().getTime() - min), (int) dp.getMeasurement());
                    entries.add(e);
                    labels.add((float) dp.getCreated_at().getTime());
                    sum += dp.getMeasurement();
                }

                LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                dataSet.setColor(R.color.activar);
                dataSet.setLineWidth(3);
                dataSet.setColor(R.color.principal);
                dataSet.setValueTextSize(11);
                //dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setValueTextColor(R.color.alertasCerrar); // styling, ...

                //Delete the "description" from the chart
                mChart.setDescription("");

                LineData lineData = new LineData(dataSet);
                mChart.setData(lineData);

                //Format the labels of the X Axis
                HourAxisValueFormatter formatter = new HourAxisValueFormatter(min);
                XAxis xAxis = mChart.getXAxis();
                xAxis.setValueFormatter(formatter);

                //Format the labels of the Y Axis
                YAxis yAxis = mChart.getAxisLeft();
                YAxis yAxis2 = mChart.getAxisRight();
                yAxis.setValueFormatter(new CustomYAxisValueFormatter());
                yAxis2.setDrawLabels(false);

                mChart.invalidate(); // refresh

                // setting energy cost
                sum = sum * ENERGY_COST;
                TextView cost = (TextView) findViewById(R.id.valueCurrent);
                cost.setText("$ " + sum);
            }
        });
    }

    private class HourAxisValueFormatter implements AxisValueFormatter {

        private long referenceTimestamp; // minimum timestamp in your data set
        private DateFormat mDataFormat;
        private Date mDate;

        public HourAxisValueFormatter(long referenceTimestamp) {
            this.referenceTimestamp = referenceTimestamp;
            this.mDataFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); //TODO definir si va por dia para que sea asi: dd/MM/yy
            this.mDate = new Date();
        }

        /**
         * Called when a value from an axis is to be formatted
         * before being drawn. For performance reasons, avoid excessive calculations
         * and memory allocations inside this method.
         *
         * @param value the value to be formatted
         * @param axis  the axis the value belongs to
         * @return
         */
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // convertedTimestamp = originalTimestamp - referenceTimestamp
            long convertedTimestamp = (long) value;

            // Retrieve original timestamp
            long originalTimestamp = referenceTimestamp + convertedTimestamp;

            // Convert timestamp to hour:minute
            return getHour(originalTimestamp);
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }

        private String getHour(long timestamp) {
            try {
                mDate.setTime(timestamp);
                return mDataFormat.format(mDate);
            } catch (Exception ex) {
                return "xx";
            }
        }
    }

    /**
     * This class is the formatter of the Y axis of the chart
     */
    private class CustomYAxisValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return (int) value + "kW";
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    /**
     * This class represents the Meter Data of the endpoint
     */
    private class MeterData {
        private Date created_at;
        private double measurement;

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public double getMeasurement() {
            return measurement;
        }

        public void setMeasurement(double measurement) {
            this.measurement = measurement;
        }

        @Override
        public String toString() {
            return "{" + measurement + "}";
        }
    }
}
