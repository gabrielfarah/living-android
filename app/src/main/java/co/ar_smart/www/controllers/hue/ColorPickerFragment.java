package co.ar_smart.www.controllers.hue;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;
import com.flask.colorpicker.slider.OnValueChangedListener;

import co.ar_smart.www.living.R;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link ColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorPickerFragment newInstance(String param1, String param2) {
        ColorPickerFragment fragment = new ColorPickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ColorPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);
        // Inflate the layout for this fragment

        ColorPickerView color_view = (ColorPickerView) rootView.findViewById(R.id.color_picker_view);
        LightnessSlider brightness = (LightnessSlider) rootView.findViewById(R.id.v_lightness_slider);
        AlphaSlider saturation = (AlphaSlider) rootView.findViewById(R.id.v_alpha_slider);
        if (color_view != null) {
            color_view.addOnColorSelectedListener(new OnColorSelectedListener() {
                @Override
                public void onColorSelected(int selectedColor) {
                    Log.d("RGB:", "" + Color.red(selectedColor) + " " + Color.green(selectedColor) + " " + Color.blue(selectedColor));
                    Toast.makeText(getActivity().getApplicationContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (brightness != null) {
            brightness.setOnValueChangedListener(new OnValueChangedListener() {
                @Override
                public void onValueChanged(float v) {
                    Log.d("BRIG", "" + Math.round(v * 254));
                    //Toast.makeText(getApplicationContext(),"LightnessSlider: " +  (v*254), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (saturation != null) {
            saturation.setOnValueChangedListener(new OnValueChangedListener() {
                @Override
                public void onValueChanged(float v) {
                    Log.d("SAT", "" + Math.round(v * 254));
                    //Toast.makeText(getApplicationContext(),"AlphaSlider: " +  (v*254), Toast.LENGTH_LONG).show();
                }
            });
        }
        //TODO if respnse fail it could be a registration fail

        return rootView;
    }
}
