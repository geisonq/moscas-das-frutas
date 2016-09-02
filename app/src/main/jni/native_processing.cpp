#include "native_processing.h"

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_geisonquevedo_moscadasfrutas_CameraActivity_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {
    Mat& mGr  = *(Mat*)addrRgba;
    Mat& mRgb = *(Mat*)addrRgba;
   /* vector<KeyPoint> v;

    Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(mGr, v);
    for (unsigned int i = 0; i < v.size(); i++) {
        const KeyPoint& kp = v[i];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
    }*/
}

JNIEXPORT void JNICALL Java_com_geisonquevedo_moscadasfrutas_CameraActivity_SetupApp(JNIEnv*, jobject, jlong addrRgba, jlong addrGray) {
    Mat& rgba = *(Mat*)addrRgba;
    Mat& gray = *(Mat*)addrRgba;
    Mat dst;

    Mat3b img = imread("path_to_image");

    // Convert to HSV color space
    Mat3b hsv;
    cvtColor(rgba, hsv, COLOR_BGR2HSV);

    // Get yellow pixels
    Mat1b polyMask;
    inRange(hsv, Scalar(29, 220, 220), Scalar(31, 255, 255), polyMask);

    // Fill outside of polygon
    floodFill(polyMask, Point(0, 0), Scalar(255));

    // Invert (inside of polygon filled)
    polyMask = ~polyMask;

    // Create a black image
    Mat3b res(rgba.size(), Vec3b(0,0,0));

    // Copy only masked part
    rgba.copyTo(res, polyMask);

rgba = res;

}

}
