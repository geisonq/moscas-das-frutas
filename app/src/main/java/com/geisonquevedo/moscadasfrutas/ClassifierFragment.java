package com.geisonquevedo.moscadasfrutas;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.geisonquevedo.moscadasfrutas.Database.Coleta;
import com.geisonquevedo.moscadasfrutas.Database.DatabaseHandler;
import com.geisonquevedo.moscadasfrutas.utils.SaveBitmap;
import com.geisonquevedo.moscadasfrutas.OpenCVBusiness.OpenCVBusiness;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassifierFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassifierFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassifierFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    ImageButton imgBtnA;
    ImageButton imgBtnC;
    Button btnRecomecar;

    ImageView imageViewMsc;

    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView15;
    TextView textView16;

    File file[];
    private int imagePosition = 0;

    public int totalA = 0;
    public int totalC = 0;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassifierFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassifierFragment newInstance(String param1, String param2, String id) {
        ClassifierFragment fragment = new ClassifierFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public ClassifierFragment() {
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

        View v = inflater.inflate(R.layout.fragment_classifier, container, false);

        this.initializeVariables(v);

        imgBtnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalA++;
                nextImage(imageViewMsc);
            }
        });
        imgBtnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalC++;
                nextImage(imageViewMsc);
            }
        });

        btnRecomecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Fragment objFragment = new CameraFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayoutMainContent, objFragment).commit();*/

                Intent myIntent = new Intent(getActivity(), CameraActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        //Used to load the last id
        DatabaseHandler db = new DatabaseHandler(getActivity());

        SaveBitmap pictureFileDir = new SaveBitmap();
        File pathSubSubFolder = pictureFileDir.getDir();

        File dir = new File(pathSubSubFolder.getPath()+ File.separator + "mosca_" + db.getLastId() + "/");
        this.file = dir.listFiles();

        Log.i("Path mosca_FOLDER:", pathSubSubFolder.getPath() + File.separator + "mosca_" + db.getLastId() + "/--");

        if(this.file == null){
            this.finishClassifier();
        } else {
            nextImage(imageViewMsc);
        }



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


    public void nextImage(ImageView imageViewMsc){
        Log.i("Total:------", Integer.toString(this.imagePosition)+"-"+ Integer.toString(this.file.length));
        if(this.file.length>this.imagePosition){
            Log.i("nextImage:", this.file[this.imagePosition].toString());
            Bitmap myBitmap = BitmapFactory.decodeFile(this.file[this.imagePosition].toString());


            Context context = getActivity().getApplicationContext();
            OpenCVBusiness oCVBusiness = new OpenCVBusiness(context);
            try {
                double ret  =oCVBusiness.calcHistogram(myBitmap);
                Log.i("HISTOGRAMA", "HISTOGRAMA " + this.file[this.imagePosition].toString() +  " histo: " + ret);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageViewMsc.setImageBitmap(myBitmap);
            this.imagePosition++;
        } else {
            this.finishClassifier();
        }
    }

    public void finishClassifier(){
        imgBtnA.setVisibility(View.INVISIBLE);
        imgBtnC.setVisibility(View.INVISIBLE);

        textView15.setVisibility(View.INVISIBLE);
        textView16.setVisibility(View.INVISIBLE);

        imageViewMsc.setVisibility(View.INVISIBLE);

        textView4.setVisibility(View.VISIBLE);
        textView5.setVisibility(View.VISIBLE);
        textView6.setVisibility(View.VISIBLE);
        textView7.setVisibility(View.VISIBLE);
        textView8.setVisibility(View.VISIBLE);
        textView9.setVisibility(View.VISIBLE);
        textView10.setVisibility(View.VISIBLE);
        btnRecomecar.setVisibility(View.VISIBLE);

        textView7.setText(Integer.toString(totalA));
        textView8.setText(Integer.toString(totalC));
        textView9.setText(Integer.toString(totalC+totalA));


        DatabaseHandler db = new DatabaseHandler(getActivity());
        db.addColeta(new Coleta(1, db.getCurrDate(), totalA, totalC));
    }

    private void initializeVariables(View v ){
        imageViewMsc = (ImageView) v.findViewById(R.id.imageViewMsc);
        imgBtnA = (ImageButton) v.findViewById(R.id.imgBtnA);
        imgBtnC = (ImageButton) v.findViewById(R.id.imgBtnC);
        btnRecomecar  = (Button) v.findViewById(R.id.btnRecomecar);

        textView4 = (TextView) v.findViewById(R.id.textView4);
        textView5 = (TextView) v.findViewById(R.id.textView5);
        textView6 = (TextView) v.findViewById(R.id.textView6);
        textView7 = (TextView) v.findViewById(R.id.textView7);
        textView8 = (TextView) v.findViewById(R.id.textView8);
        textView9 = (TextView) v.findViewById(R.id.textView9);
        textView10 = (TextView) v.findViewById(R.id.textView10);
        textView15 = (TextView) v.findViewById(R.id.textView15);
        textView16 = (TextView) v.findViewById(R.id.textView16);
    }

}
