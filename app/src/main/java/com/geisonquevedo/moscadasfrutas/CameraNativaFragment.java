package com.geisonquevedo.moscadasfrutas;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.geisonquevedo.moscadasfrutas.OpenCVBusiness.ProcessImageAsyncTask;
import com.geisonquevedo.moscadasfrutas.utils.SaveBitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * ff
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraNativaFragment extends Fragment {

    ImageButton imageButton;
    ImageButton imgBtnProc;
    ImageButton imgBtnCancel;
    ImageView imgProfilePic;
    PhotoViewAttacher mAttacher;

    private static int REQUEST_GALLERY = 1;
    private static int REQUEST_CAMERA = 2;
    private static int REQUEST_EXEMPLO_1 = 3;
    private static int REQUEST_EXEMPLO_2 = 4;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraNativaFragment newInstance(String param1, String param2) {
        CameraNativaFragment fragment = new CameraNativaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CameraNativaFragment() {
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

        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        imgProfilePic = (ImageView) v.findViewById(R.id.imgProfilePic);
        imageButton = (ImageButton) v.findViewById(R.id.imageButton);
        imgBtnProc = (ImageButton) v.findViewById(R.id.imgBtnProc);
        imgBtnCancel = (ImageButton) v.findViewById(R.id.imgBtnCancel);


        showProcessImages();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        imgBtnProc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierFragment mainFragment = new ClassifierFragment();
                getFragmentManager().beginTransaction().replace(R.id.relativeLayoutMainContent, mainFragment).commit();
                hideAllButtons();
            }
        });

        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                CameraNativaFragment mainFragment = new CameraNativaFragment();
                getFragmentManager().beginTransaction().replace(R.id.relativeLayoutMainContent, mainFragment).commit();
                hideAllButtons();
            }
        });

        this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Show Message
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Click na câmera para iniciar!";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Mat src;

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQUEST_CAMERA) {

                final File file = getTempFile(this.getActivity());

                Bitmap captureBmp;
                try {
                    captureBmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                    src = new Mat(captureBmp.getHeight(), captureBmp.getWidth(), 0, new Scalar(0));
                    Bitmap bmp32 = captureBmp.copy(Bitmap.Config.RGB_565, false);
                    Utils.bitmapToMat(bmp32, src);

                    new ProcessImageAsyncTask(getActivity(), src) {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            imgProfilePic.setImageBitmap(this.imgToShow);
                            showProcessImages();
                        }
                    }.execute();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == REQUEST_GALLERY && null != data) {
                try {
                    InputStream inputStream = this.getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeStream(inputStream);

                    src = new Mat(bmp.getHeight(), bmp.getWidth(), 0, new Scalar(4));
                    Bitmap bmp32 = bmp.copy(Bitmap.Config.RGB_565, true);
                    Utils.bitmapToMat(bmp32, src);

                    new ProcessImageAsyncTask(getActivity(), src) {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            imgProfilePic.setImageBitmap(this.imgToShow);
                            showProcessImages();
                        }
                    }.execute();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private void showProcessImages() {
        //Show Message
        Context context = getActivity().getApplicationContext();
        CharSequence text = "Click no icone verde para iniciar!";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

        imageButton.setVisibility(View.VISIBLE);
        imgBtnProc.setVisibility(View.INVISIBLE);
        imgBtnCancel.setVisibility(View.INVISIBLE);
    }

    private void hideAllButtons() {
        imageButton.setVisibility(View.INVISIBLE);
        imgBtnProc.setVisibility(View.INVISIBLE);
        imgBtnCancel.setVisibility(View.INVISIBLE);
        imgProfilePic.setVisibility(View.INVISIBLE);
    }

    private void processImage() {
        Log.i("LOADIMG:", "OPSSSS");

        String path;

        SaveBitmap saveBmp = new SaveBitmap();
        Context context = getActivity().getApplicationContext();
        AssetManager assetManager = context.getAssets();

        path = saveBmp.getDir() + "/mosca_automatically/temp.jpg";
        Log.i("LOADIMG:", "path:" + saveBmp.getDir() + "/mosca_automatically/temp.jpg");
        Bitmap bmp = BitmapFactory.decodeFile(path);

        Mat src = new Mat(bmp.getHeight(), bmp.getWidth(), 0, new Scalar(4));
        Bitmap bmp32 = bmp.copy(Bitmap.Config.RGB_565, true);
        Utils.bitmapToMat(bmp32, src);

        new ProcessImageAsyncTask(getActivity(), src) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                imgProfilePic.setImageBitmap(this.imgToShow);

                showProcessImages();
                //mAttacher.update();
            }
        }.execute();


    }


    private void selectImage() {
        final CharSequence[] items = {"Exemplo 1", "Exemplo 2", "Camera", "Galeria", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Adicionar Foto:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(getActivity().getBaseContext())));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (items[item].equals("Imagens")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_GALLERY);
                } else if (items[item].equals("Exemplo 1") || items[item].equals("Exemplo 2")) {
                    try {
                        InputStream is;

                        Context context = getActivity().getApplicationContext();
                        AssetManager assetManager = context.getAssets();

                        if (items[item].equals("Exemplo 1")) {
                            is = assetManager.open("exemplo/1.jpg");
                        } else {
                            is = assetManager.open("exemplo/2.jpg");
                        }

                        Bitmap bmp = BitmapFactory.decodeStream(is);

                        Mat src = new Mat(bmp.getHeight(), bmp.getWidth(), 0, new Scalar(4));
                        Bitmap bmp32 = bmp.copy(Bitmap.Config.RGB_565, true);
                        Utils.bitmapToMat(bmp32, src);

                        new ProcessImageAsyncTask(getActivity(), src) {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                imgProfilePic.setImageBitmap(this.imgToShow);
                                //mAttacher.update();
                                showProcessImages();
                            }
                        }.execute();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private File getTempFile(Context context) {
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }

        return new File(path, "image.bmp");
    }

    static {
        OpenCVLoader.initDebug();
    }

}
