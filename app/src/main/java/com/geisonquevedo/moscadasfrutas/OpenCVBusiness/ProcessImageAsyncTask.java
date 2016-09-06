package com.geisonquevedo.moscadasfrutas.OpenCVBusiness;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Mat;

/**
 * Created by Geison Quevedo on 20/05/2016.
 */
public class ProcessImageAsyncTask extends AsyncTask<Void, Integer, Void> {

    boolean running;

    public Context context;
    private Mat src;
    public Bitmap imgToShow;

    ProgressDialog progressDialog;

    public ProcessImageAsyncTask(Context contextAct, Mat imagem) {
        this.context = contextAct;
        this.src = imagem;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Thread.sleep(0);
            Log.i("ProcessImagesAsyncTask","------ProcessImagesAsyncTask-------");
            OpenCVBusiness openCVBusiness = new OpenCVBusiness(context);
            this.imgToShow = openCVBusiness.processImage(this.src);
            Log.i("Passo","Passo1111");

            progressDialog.dismiss();
        } catch (InterruptedException e) {
            Log.i("Passo","ERROOO");
            e.printStackTrace();
        }

        publishProgress(1);

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setMessage("Processando");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(context,
                "Mosca das Frutas",
                "Processando");

        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                running = false;
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }

}

