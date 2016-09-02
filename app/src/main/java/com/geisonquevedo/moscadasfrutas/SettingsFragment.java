package com.geisonquevedo.moscadasfrutas;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartilhaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartilhaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SeekBar seekBarH;
    private SeekBar seekBarS;
    private SeekBar seekBarV;

    private SeekBar seekBarEH;
    private SeekBar seekBarES;
    private SeekBar seekBarEV;

    private TextView textViewHV;
    private TextView textViewSV;
    private TextView textViewVV;

    private TextView textViewEHV;
    private TextView textViewESV;
    private TextView textViewEVV;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartilhaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settins, container, false);

        this.initializeVariables(v);

        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        seekBarH.setProgress(pref.getInt("strH", 11));
        textViewHV.setText(Integer.toString(pref.getInt("strH", 11)));
        seekBarS.setProgress(pref.getInt("strS", 38));
        textViewSV.setText(Integer.toString(pref.getInt("strS", 18)));
        seekBarV.setProgress(pref.getInt("strV", 50));
        textViewVV.setText(Integer.toString(pref.getInt("strV", 50)));
        seekBarEH.setProgress(pref.getInt("endH", 40));
        textViewEHV.setText(Integer.toString(pref.getInt("endH", 40)));
        seekBarES.setProgress(pref.getInt("endS", 255));
        textViewESV.setText(Integer.toString(pref.getInt("endS", 255)));
        seekBarEV.setProgress(pref.getInt("endV", 255));
        textViewEVV.setText(Integer.toString(pref.getInt("endV", 255)));

        seekBarH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewHV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewHV.setText(progress+"");
                saveSettings();
            }
        });
        seekBarS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewSV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewSV.setText(progress+"");
                saveSettings();
            }
        });
        seekBarV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewVV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewVV.setText(progress+"");
                saveSettings();
            }
        });
        seekBarEH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewEHV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewEHV.setText(progress+"");
                saveSettings();
            }
        });
        seekBarES.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewESV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewESV.setText(progress+"" );
                saveSettings();
            }
        });
        seekBarEV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewEVV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewEVV.setText(progress+"");
                saveSettings();
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initializeVariables(View v) {
        seekBarH = (SeekBar) v.findViewById(R.id.seekBarH);
        seekBarS = (SeekBar) v.findViewById(R.id.seekBarS);
        seekBarV = (SeekBar) v.findViewById(R.id.seekBarV);

        seekBarEH = (SeekBar) v.findViewById(R.id.seekBarEH);
        seekBarES = (SeekBar) v.findViewById(R.id.seekBarES);
        seekBarEV = (SeekBar) v.findViewById(R.id.seekBarEV);

        textViewHV = (TextView) v.findViewById(R.id.textViewHV);
        textViewSV = (TextView) v.findViewById(R.id.textViewSV);
        textViewVV = (TextView) v.findViewById(R.id.textViewVV);

        textViewEHV = (TextView) v.findViewById(R.id.textViewEHV);
        textViewESV = (TextView) v.findViewById(R.id.textViewESV);
        textViewEVV = (TextView) v.findViewById(R.id.textViewEVV);
    }

    public void setColor(TextView textViewColor) {

    }

    public void saveSettings(){
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = pref.edit();

        edit.putInt("strH", seekBarH.getProgress());
        edit.putInt("strS", seekBarS.getProgress());
        edit.putInt("strV", seekBarV.getProgress());
        edit.putInt("endH", seekBarEH.getProgress());
        edit.putInt("endS", seekBarES.getProgress());
        edit.putInt("endV", seekBarEV.getProgress());

        edit.commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
