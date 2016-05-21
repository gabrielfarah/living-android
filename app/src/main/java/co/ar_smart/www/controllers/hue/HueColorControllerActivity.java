package co.ar_smart.www.controllers.hue;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class HueColorControllerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Map.Entry<String, String>> scenes = new java.util.ArrayList<>();
    private String API_TOKEN;
    private int PREFERRED_HUB_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hue_color_controller);
        /*ColorPickerView color_view = (ColorPickerView)findViewById(R.id.color_picker_view);
        LightnessSlider brightness = (LightnessSlider)findViewById(R.id.v_lightness_slider);
        AlphaSlider saturation = (AlphaSlider)findViewById(R.id.v_alpha_slider);
        if (color_view != null) {
            color_view.addOnColorSelectedListener(new OnColorSelectedListener() {
                @Override
                public void onColorSelected(int selectedColor) {
                    Log.d("RGB:",""+ Color.red(selectedColor)+" "+Color.green(selectedColor)+ " "+Color.blue(selectedColor));
                    Toast.makeText(getApplicationContext(),"onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (brightness != null) {
            brightness.setOnValueChangedListener(new OnValueChangedListener() {
                @Override
                public void onValueChanged(float v) {
                    Log.d("BRIG",""+Math.round(v*254));
                    //Toast.makeText(getApplicationContext(),"LightnessSlider: " +  (v*254), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (saturation != null) {
            saturation.setOnValueChangedListener(new OnValueChangedListener() {
                @Override
                public void onValueChanged(float v) {
                    Log.d("SAT",""+Math.round(v*254));
                    //Toast.makeText(getApplicationContext(),"AlphaSlider: " +  (v*254), Toast.LENGTH_LONG).show();
                }
            });
        }
        //TODO if respnse fail it could be a registration fail*/
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        Endpoint endpoint = intent.getParcelableExtra(EXTRA_OBJECT);

        setTitle(endpoint.getName());

        toolbar = (Toolbar) findViewById(R.id.hue_color_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(endpoint.getName());
        }


        viewPager = (ViewPager) findViewById(R.id.hue_color_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.hue_color_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ColorPickerFragment.newInstance("T1", "T2"), "Lights");
        adapter.addFragment(SceneColorPickerFragment.newInstance("T1"), "Temperatures");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
}
