package com.geisonquevedo.moscadasfrutas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.geisonquevedo.moscadasfrutas.utils.SaveBitmap;
import com.victor.loading.newton.NewtonCradleLoading;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener {

    private CameraBridgeViewBase mOpenCvCameraView;
    private NewtonCradleLoading newtonCradleLoading;

    Mat mRgba;
    Mat mGray;
    Mat cacheMRgba = null;

    int counter = 0;
    int cPrevSeq = 0;
    int cCurrSeq = 1;

    ImageButton btnLeft;
    ImageButton btnRight;

    Point resolution1920X1080 = new Point(960, 540);
    Size size480X270 = new Size(480, 270);
    Scalar colorYellow = new Scalar(255, 255, 0, 255);

    public SaveBitmap saveBitmap = new SaveBitmap();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraActivity() {
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full Screen wide
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnRight = (ImageButton) findViewById(R.id.btnRight);
        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(1920, 1080);
        mOpenCvCameraView.setFocusable(true);
        mOpenCvCameraView.enableView();
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat();
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (cacheMRgba == null) {
            cacheMRgba = mRgba;
        }

        if (counter == 1) {
            cacheMRgba = mRgba;

            Imgproc.resize(mGray, mGray, this.size480X270);

            MatOfRect circles = new MatOfRect();
            //Detecta circulos
            Imgproc.HoughCircles(mGray, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 400, 30, 60, 115, 250);

            float circle[] = new float[3];

            if (circles.cols() > 0) {
                cPrevSeq++;

                //for (int i = 0; i < circles.cols(); i++) {
                circles.get(0, 0, circle);
                org.opencv.core.Point center = new org.opencv.core.Point();
                center.x = circle[0] * 4;
                center.y = circle[1] * 4;

                //Chama o método para alinhar a armadilha no centro da tela;
                allignTrap(center);

                // Quando encontra a sequencia de frames com a armadilha
                //  na posição correta, uma imagem é registrada
                if (cCurrSeq == 20) {
                    mOpenCvCameraView.getHandler().post(new Runnable() {
                        public void run() {
                            newtonCradleLoading.start();
                        }
                    });

                    Mat mask = createMask(mRgba);

                    Mat imagemMascarada = new Mat(mask.rows(), mask.cols(), CvType.CV_8UC3);
                    imagemMascarada.setTo(new Scalar(0, 0, 0, 0));
                    Imgproc.cvtColor(cacheMRgba, cacheMRgba, Imgproc.COLOR_BGR2BGRA);

                    cacheMRgba.copyTo(imagemMascarada, mask);
                    Imgproc.cvtColor(imagemMascarada, imagemMascarada, Imgproc.COLOR_RGBA2BGR);

                    Bitmap bmp;
                    bmp = Bitmap.createBitmap(inputFrame.rgba().cols(), inputFrame.rgba().rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(imagemMascarada, bmp);
                    saveBitmap.save(bmp, "temp", true, "automatically");

                    mOpenCvCameraView.getHandler().post(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(CameraActivity.this, MainActivity.class);
                            myIntent.putExtra("startIntend", "CameraFragnent");
                            CameraActivity.this.startActivity(myIntent);
                            mOpenCvCameraView.disableView();
                        }
                    });

                    mOpenCvCameraView.getHandler().post(new Runnable() {
                        public void run() {
                            newtonCradleLoading.stop();
                        }
                    });
                }

                //Desenha o circulo amarelo, para informar onde esta a arnadilha
                Imgproc.circle(mRgba, center, (int) circle[2] * 4, this.colorYellow, 4);

            }
        } else {
            mRgba = cacheMRgba;
        }

        //Desenha o circulo verde no meio da tela
        mRgba = drawCircleOnCernter(mRgba);

        //Incrementa os contadores
        counterAdder();

        return mRgba;
    }

    private void loadCameraFragment() {
        mOpenCvCameraView.getHandler().post(new Runnable() {
            public void run() {
                Intent myIntent = new Intent(CameraActivity.this, MainActivity.class);
                myIntent.putExtra("startIntend", "CameraFragnent");
                CameraActivity.this.startActivity(myIntent);
                mOpenCvCameraView.disableView();
            }
        });
    }

    public Mat createMask(Mat image) {
        Mat imgThreshBlue = new Mat();
        Mat imgThreshWhite = new Mat();
        Mat imgThresh = new Mat();
        Mat imgHsv = new Mat();

        Bitmap bmpMask;

        Imgproc.cvtColor(image, imgHsv, Imgproc.COLOR_RGB2HSV_FULL, 1);
        double hueScale = 2.0 / 1.41176470588;

        /*
        Encontra circulo azul
         */
        Core.inRange(imgHsv,
                new Scalar(hueScale * 105, 70, 70), //Blue
                new Scalar(hueScale * 130, 255, 255), //Dark Blue
                imgThreshBlue);

        /*
        Workaround: Converte imagem binaria para GRAY:
        1ª CONVERTO DE BINARIO PARA BMP
        2ª CONVERTO DE BMP PARA MAT
        3ª CONVERTO DE MAT PARA MAT GRAY
        * */
        bmpMask = Bitmap.createBitmap(imgThreshBlue.cols(), imgThreshBlue.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgThreshBlue, bmpMask);
        Utils.bitmapToMat(bmpMask, imgThresh);
        Imgproc.cvtColor(imgThresh, imgThresh, Imgproc.COLOR_RGB2GRAY);

        MatOfRect circles = new MatOfRect();
        Imgproc.HoughCircles(imgThresh, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 200, 10, 10, 100, 10000);

        float circle[] = new float[3];
        if (circles.cols() > 0) {

            circles.get(0, 0, circle);
            org.opencv.core.Point center = new org.opencv.core.Point();
            center.x = circle[0] * 2;
            center.y = circle[1] * 2;

            //Desenha o circulo
            Imgproc.circle(imgThresh, center, (int) (circle[2] + 20) * 2, new Scalar(255, 255, 255), -1, 8, 1);
        }

        return imgThresh;
    }

    public void allignTrap(org.opencv.core.Point center) {
        Log.i("allignTrap y:", "Y:" + center.x);

        //Reiniciai a contagem caso circulo não estaja dentro do retangulo
        Rect rectInside = new Rect(new Point(1080, 1050), new Point(760, 45));
        if (!center.inside(rectInside)) {
            Log.i("allignTrap y:", "RESET RESET");
            resetCouterToTakePhotoAutomatilly();
        }

        //RESET ALL
        btnRight.getHandler().post(new Runnable() {
            public void run() {
                btnRight.setVisibility(View.INVISIBLE);
                btnLeft.setVisibility(View.INVISIBLE);
            }
        });

        if (center.x >= 990 && center.x <= 1200) {
            btnRight.getHandler().post(new Runnable() {
                public void run() {
                    Log.i("allignTrap y:", "<-------------");
                    /*Se precisa ir para direita ou esquerda deve-se resetar o contador, pois que
                    quero 5 frames perfeitos. */
                    //resetCouterToAutomatillyPhoto();
                    btnRight.setVisibility(View.VISIBLE);
                    btnLeft.setVisibility(View.INVISIBLE);
                }
            });
        }

        if (center.x >= 840 && center.x <= 930) {
            btnLeft.getHandler().post(new Runnable() {
                public void run() {
                    Log.i("allignTrap y:", "--------------->");
                    // resetCouterToAutomatillyPhoto();
                    btnRight.setVisibility(View.INVISIBLE);
                    btnLeft.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    /*
    * counter: contador geral
    * @couterToAutomatillyPhoto: incrementa o contador que informa
    *                            o numero de frames ideais para tirar a foto
    * */
    public void counterAdder() {
        if (counter > 3) {
            counter = 0;
        }

        couterToTakePhotoAutomatilly();

        counter++;
    }

    public void couterToTakePhotoAutomatilly() {

        if (cPrevSeq == cCurrSeq) {
            cCurrSeq++;
        }

        //A cada XX frames a contagem é reiniciada
        if (cCurrSeq > 20) {
            resetCouterToTakePhotoAutomatilly();
        }
    }

    public void resetCouterToTakePhotoAutomatilly() {
        cPrevSeq = 0;
        cCurrSeq = 1;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public Mat drawCircleOnCernter(Mat image) {
        Imgproc.circle(image, this.resolution1920X1080, 500, new Scalar(0, 255, 0, 150), 4);

        //DESABILITAR: USADO PARA DEBUG
        //Imgproc.rectangle(image, new Point(1080,1050), new Point(760,45), new Scalar(255, 0, 0, 150), 4);
        return image;
    }


}