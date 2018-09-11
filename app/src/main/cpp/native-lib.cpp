#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgcodecs.hpp>
#include <fstream>


//Namespaces declaration
using namespace cv;
using namespace cv::ml;
using namespace std;

//Global Variables
float DataHuMoments[7] = {0,0,0,0,0,0,0};
String sillyString = "I am a silly string";


//Function declarations
void opening_images();

//Native interactions
void opening_images(){

    Mat image ;
    fstream file ;
    sillyString = "I have changed";

    String imagePath = "/storage/emulated/0/Pictures/Data/Birds/Bleshoender/1.png";

    imread(imagePath,IMREAD_COLOR);

    if(!image.data){
        sillyString = "C++ Image not loaded";
        //imshow("Image window",image);
    }
    else {
        sillyString = "C++ Image loaded";

    }

}

extern "C" {

JNIEXPORT jint JNICALL Java_com_example_user1_opencvtest_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba);

JNIEXPORT jint JNICALL Java_com_example_user1_opencvtest_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba) {

    Mat& image= *(Mat*)addrRgba;


    int conv;
    jint retVal;

    if(!image.data){
        retVal = 0;
        //imshow("Image window",image);
    }
    else {
        retVal = 10;

    }

    return retVal;

}

}


extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_user1_opencvtest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */, long addrInputImage) {




    /*if(cv::abs(-1)){
        std::string hello;
        opening_images();
        hello = sillyString;;
        return env->NewStringUTF(hello.c_str());
    }*/

}

extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_user1_opencvtest_MainActivity_svm(JNIEnv *env, jobject) {

    return nullptr;

}

extern "C" {
JNIEXPORT jint JNICALL
Java_com_example_color_MainActivity_openCVJNI(JNIEnv *env, jobject thiz, long addrInputImage)
{
    cv::Mat* pInputImage = (cv::Mat*)addrInputImage;
    return pInputImage->rows;
}
}

