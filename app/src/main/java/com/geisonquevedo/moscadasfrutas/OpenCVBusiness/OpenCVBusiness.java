package com.geisonquevedo.moscadasfrutas.OpenCVBusiness;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.geisonquevedo.moscadasfrutas.Database.DatabaseHandler;
import com.geisonquevedo.moscadasfrutas.utils.SaveBitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Geison Quevedo on 12/05/2016.
 */
public class OpenCVBusiness {

    public Context context;
    public SaveBitmap saveBitmap = new SaveBitmap();
    private String lastInsertId = "0";

    private boolean calcHistogramSingleton = true;
    private Mat imgHistoBq;

    public OpenCVBusiness(Context context) {
        this.context = context;

        DatabaseHandler db = new DatabaseHandler(context);
        this.lastInsertId = db.getLastId();
    }

    public double calcHistogram(Bitmap imageToMatch) throws IOException {

        double res = 0;
        Bitmap bmp;

        AssetManager assetManager = this.context.getAssets();

        try {
            if (this.calcHistogramSingleton) {
                InputStream is = assetManager.open("histogram/histo_bq.jpg");

                Bitmap histoBq = BitmapFactory.decodeStream(is);

                this.imgHistoBq = new Mat(histoBq.getHeight(), histoBq.getWidth(), 0, new Scalar(4));
                bmp = histoBq.copy(Bitmap.Config.RGB_565, true);
                Utils.bitmapToMat(bmp, this.imgHistoBq);

                this.calcHistogramSingleton = false;
            }

            Mat imgToCalc = new Mat(imageToMatch.getHeight(), imageToMatch.getWidth(), 0, new Scalar(4));
            bmp = imageToMatch.copy(Bitmap.Config.RGB_565, true);
            Utils.bitmapToMat(bmp, imgToCalc);

            Mat hist0 = new Mat();
            Mat hist1 = new Mat();

            int hist_bins = 30;           //number of histogram bins
            int hist_range[] = {0, 180};  //histogram range
            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(25);

            Imgproc.calcHist(Arrays.asList(this.imgHistoBq), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
            Imgproc.calcHist(Arrays.asList(imgToCalc), new MatOfInt(0), new Mat(), hist1, histSize, ranges);

            res = Imgproc.compareHist(hist0, hist1, Imgproc.CV_COMP_CORREL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }


    public boolean checkImage(Bitmap bmpToCheck) {
        Mat handledImage;
        Mat matImageWithoudBorders;

        Bitmap bmp;

        Mat matImage = new Mat(bmpToCheck.getHeight(), bmpToCheck.getWidth(), 0, new Scalar(4));
        bmpToCheck = bmpToCheck.copy(Bitmap.Config.RGB_565, true);
        Utils.bitmapToMat(bmpToCheck, matImage);

        matImageWithoudBorders = matImage;

        handledImage = getHandledImage(this.context, matImage);

        //Does the trick
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(handledImage, contours, hierarchy, 0, 1);
        List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());
        contours_poly.addAll(contours);

        MatOfPoint2f mMOP2f1, mMOP2f2;
        mMOP2f1 = new MatOfPoint2f();
        mMOP2f2 = new MatOfPoint2f();

        for (int i = 0; i < contours.size(); i++) {
            if (contours.get(i).toList().size() > 200) {

                contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
                Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 2, true);
                mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
                Rect appRect = Imgproc.boundingRect(contours_poly.get(i));

                Rect image_roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);

                //Do: Create all small images
                Mat imagem = new Mat(matImageWithoudBorders, image_roi);

                Mat mRgba = new Mat();
                Imgproc.cvtColor(imagem, mRgba, Imgproc.COLOR_BGR2RGBA, 4);
                bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imagem, bmp);

                try {
                    double ret = calcHistogram(bmp);
                    Log.i("BUSINISS", "IMAGE: " + i + " calcHistogram----------------:" + ret);
                    // if (ret >= 0.222 && ret <= 0.50) {
                    Log.i("BUSINISS", "Existe algo----------------" + i);
                    return true; //Existe algo amarelo realmente

                    // }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("BUSINISS", "naooo Existe algo----------------");
            }
        }

        return true;
    }

    public Bitmap processImage(Mat matImage) {

        Mat handledImage;
        Mat matImageWithoudBorders = new Mat();
        Mat imageDenoising = new Mat();
        ;
        Bitmap bmp;

        //Do:
        matImageWithoudBorders = matImage;

        //Do:
        //matImage.convertTo(matImage, -1, 1, 54);

        imageDenoising = matImage;
        //Do:
        //Photo.fastNlMeansDenoisingColored(matImage, imageDenoising, new Float(3), new Float(3), 7, 21);

        //Do:
        handledImage = getHandledImage(this.context, imageDenoising);

        //Do: Does the trick
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(handledImage, contours, hierarchy, 0, 1);
        List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());
        contours_poly.addAll(contours);

        MatOfPoint2f mMOP2f1, mMOP2f2;
        mMOP2f1 = new MatOfPoint2f();
        mMOP2f2 = new MatOfPoint2f();

        ArrayList<Integer> imagesToMatch = new ArrayList<Integer>();

        for (int i = 0; i < contours.size(); i++) {
            if (contours.get(i).toList().size() > 80) {
                Log.i("CAMERA", "Size: " + contours.get(i).toList().size());

                contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
                Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 2, true);
                mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
                Rect appRect = Imgproc.boundingRect(contours_poly.get(i));

                Rect image_roi = new Rect(appRect.x, appRect.y, appRect.width, appRect.height);

                //Do: Create all small images
                Mat imagem = new Mat(matImageWithoudBorders, image_roi);

                Mat mRgba = new Mat();
                Imgproc.cvtColor(imagem, mRgba, Imgproc.COLOR_BGR2RGBA);
                bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imagem, bmp);

                //Do: Call
                try {
                    double ret = calcHistogram(bmp);
                    Log.i("CAMERA", "Img: " + i + " Histogram: " + ret + " Area: " + Imgproc.contourArea(contours.get(i)));
                    //if (ret >= 0.122 && ret <= 7.50) {
                        Log.i("CAMERA", "Existe algo No range Histogram:" + i);
                        imagesToMatch.add(i);
                        saveBitmap.save(bmp, String.valueOf(i), true, lastInsertId);
                   // } else {
                  ///      Log.i("CAMERA", "NÃOOOOO Existe algo No range Histogram:" + i);
                   // }
                        /* Verifica novamente se existe algo amarelo no bloco de imagem. PQ??? SE RETIRAR
                         não funciona tão bem. */
                    /// if (oCVBusiness.checkImage(bmp, context)) {


                    // } else {
                    //     Log.i("CAMERA", "Nãoooo Existe algo----------------:"+i);
                    // }

                    //  }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Does the trick
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        Imgproc.findContours(handledImage, contours, hierarchy, 0, 1);
        contours_poly = new ArrayList<MatOfPoint>(contours.size());
        contours_poly.addAll(contours);

        mMOP2f1 = new MatOfPoint2f();
        mMOP2f2 = new MatOfPoint2f();

        for (int i = 0; i < contours.size(); i++) {
            if (contours.get(i).toList().size() > 80 && imagesToMatch.contains((i))) {
                contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
                Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 3, true);
                mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
                Rect appRect = Imgproc.boundingRect(contours_poly.get(i));

                if (Imgproc.contourArea(contours.get(i)) > 4500.0) {
                    Imgproc.rectangle(imageDenoising, new Point(appRect.x - 30, appRect.y - 40), new Point(appRect.x + appRect.width, appRect.y + appRect.height), new Scalar(0, 255, 0), 3, 8, 0);

                } else {
                    Imgproc.rectangle(imageDenoising, new Point(appRect.x - 30, appRect.y - 40), new Point(appRect.x + appRect.width, appRect.y + appRect.height), new Scalar(255, 0, 0), 3, 8, 0);
                }
            }
        }

        bmp = Bitmap.createBitmap(handledImage.cols(), handledImage.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(imageDenoising, bmp);

        saveBitmap.save(bmp, lastInsertId, false, "");

        return bmp;
    }

    private Mat getHandledImage(Context context, Mat matImage) {
        Mat imgHSV = new Mat();
        Mat imgThresh = new Mat();
        Mat erosionDst = new Mat();
        Mat dilatationDst = new Mat();

        Imgproc.cvtColor(matImage, imgHSV, Imgproc.COLOR_RGB2HSV_FULL, 1);

       // SharedPreferences pref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        Core.inRange(imgHSV,
                new Scalar(10, 100, 120), //50 //70
                new Scalar(48, 1.00 * 256, 1.00 * 256),
                imgThresh);

        int erosionType = Imgproc.MORPH_RECT;
        int erosionSize = 5;

        Mat element = Imgproc.getStructuringElement(erosionType,
                new Size(2 * erosionSize + 1, 2 * erosionSize + 1),
                new Point(erosionSize, erosionSize));

        Imgproc.erode(imgThresh, erosionDst, element);

        int dilatationType = Imgproc.MORPH_RECT;
        int dilatationSize = 2;

        element = Imgproc.getStructuringElement(dilatationType,
                new Size(2 * dilatationSize + 1, 2 * dilatationSize + 1),
                new Point(dilatationSize, dilatationSize));

        Imgproc.dilate(erosionDst, dilatationDst, element);

        return imgThresh;
    }

}





