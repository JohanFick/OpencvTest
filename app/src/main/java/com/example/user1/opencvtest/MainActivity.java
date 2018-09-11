package com.example.user1.opencvtest;



import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.OpenCVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


//Opencv includes
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.ml.*;
import org.opencv.utils.*;




public class MainActivity extends AppCompatActivity {

    String[] ImageFolders = {"Bleshoender","BlouValk","BontKiewiet","BontVisVanger","EuropeseTroupant","GewoneTarentaal","GlansSpreeu","Hadeda","HoepHoep","IndianMyna","Koning_BlouSysie","KroonKiewiet","KweVoel","MallardEend","Mossie","ParadysVlieeVanger","RooiBorsLaksMan","RooiOogKransDuif","RooiVink","TortelDuif","Volstruis","WitborsKraai"};
    String[] BirdSizes = {"KH","SH","GH","KK","SK","GK","KT","ST","GT","VO","VV"};
    // # 0 ,  1 ,  2 ,  3  , 4  , 5  , 6  , 7 , 8   , 9  , 10
    float[] DataSetBirdSizes = {6,4,3,2,3,7,2,8,2,3,0,3,4,7,1,0,2,4,1,3,10,5};
    float[] DataSetBirdSocial = {8,5,9,6,1,8,8,10,5,4,10,6,10,6,6,5,5,8,4,6,8,8};
    float[] DataHuMoments = new float[7];
    float[] DataColorHist = new float[64];
    Mat Data_hist;
    int guess_1 = 0,guess_2 = 0;




    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);



        if (!OpenCVLoader.initDebug()) {
            tv.setText(tv.getText() + "\n OpenCVLoader.initDebug(), Not loaded");
        } else {
            tv.setText(tv.getText() + "\n OpenCVLoader.initDebug(), Loaded");
          //  tv.setText(tv.getText() + "\n"+ svm());

        }

         final ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        Button btnImageChanger = (Button) findViewById(R.id.btnimagechanger);

        btnImageChanger.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                imageView.setImageResource(R.drawable.ring);
            }
        });

//##################################################################################################################################
//Global Variables


//##################################################################################################################################
//
        tv.bringToFront();
       // readFile();
        writeFile();

        Mat image;

        String filePath = Environment.getExternalStorageDirectory()+"/Pictures/Data/Birds/Bleshoender/1.png";
        image = Imgcodecs.imread(filePath,Imgcodecs.IMREAD_COLOR);

       int i =  convertNativeGray(image.getNativeObjAddr());


       if(i==10)
       {

           Log.i("State", "C+++++++++++++++++++++++++++++++++++");
       }

    }

        void determine_colorHistOfImages(String filePath){


        Mat image;

        image = Imgcodecs.imread(filePath,Imgcodecs.IMREAD_COLOR);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        MatOfFloat ranges = new MatOfFloat(10f, 235f,10f, 235f);
        MatOfInt histSize = new MatOfInt(8,8);

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();


        Imgproc.calcHist(Arrays.asList(image), new MatOfInt(0,1), new Mat(), hist_1, histSize, ranges);
        Core.normalize(hist_1, hist_1, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        hist_2 = hist_1.reshape(0,1);

        //Log.i("ColorHist","Starting the hist calculations");
        String histString;

        histString = hist_2.dump();

        //Log.i("colorhist",histString);

        float[] data = new float[64];
        hist_2.get(0,0,data);

        for(int i=0;i<64;++i) {
            DataColorHist[i] = data[i];
           // Log.i("ColorHistHSV",String.valueOf(data[i]));
        }



        }


    void fd_hu_moments(String imageInSD )
    {
       // String imageInSD = Environment.getExternalStorageDirectory()+"/Pictures/1.png";
        Mat image;

        image = Imgcodecs.imread(imageInSD,Imgcodecs.IMREAD_COLOR);

        Mat canny_output = new Mat();
        Mat image_gray = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat hierarchy = new Mat();

        Imgproc.cvtColor(image,image_gray, Imgproc.COLOR_BGR2GRAY);
        org.opencv.core.Size s = new Size(3,3);

        Imgproc.blur(image_gray, image_gray, s);

        //Detect edges using canny
        Imgproc.Canny(image_gray,canny_output, 50, 150);
        //Find contours
        Imgproc.findContours(canny_output,contours,hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Moments momento = new Moments();
        Mat hu = new Mat();


        momento = Imgproc.moments(contours.get(0), false); // ERROR LINE

        Imgproc.HuMoments(momento, hu);

        String dataSringHu = hu.dump();

        double[] data = new double[7];

        hu.get(0,0,data);

        for(int i=0;i<7;++i)
        {
            float hudata;
            hudata = (float)data[i];
            DataHuMoments[i] = hudata ;
            //Log.i("HuMoments",String.valueOf(hudata));
        }


    }


    String determineBirdTypeKNN(String filePath,int BirdNo) {

        String birdType ;
        birdType = "No bird at the moment";

        Mat trainData = new Mat(), newcomer = new Mat();
        List<Integer> trainLabels = new ArrayList<Integer>(), newcomerLabels = new ArrayList<Integer>();

        float[] datafloatNewcomer = new float[73];

        datafloatNewcomer[0]= DataSetBirdSizes[1];
        datafloatNewcomer[1]= DataSetBirdSocial[1];

        String newcomerfilePath = filePath;//Environment.getExternalStorageDirectory()+"/Pictures/Data/Birds/BlouValk/1.png";
        fd_hu_moments(newcomerfilePath);
        determine_colorHistOfImages(newcomerfilePath);


        for(int k=0;k<7;++k)
        {
            datafloatNewcomer[k+2]= DataHuMoments[k];
        }

        for(int k=0;k<64;++k)
        {
            datafloatNewcomer[k+9]= DataColorHist[k];
        }


        Mat datanewcomer = new MatOfFloat(datafloatNewcomer);

        newcomer.push_back(datanewcomer.reshape(1,1));
        newcomerLabels.add(0);

        for(int id=0;id<3;++id)
        {
            id+=1;

        for(int i=0;i<22;++i) {

            String imagePath = Environment.getExternalStorageDirectory()+"/Pictures/Data/Birds/"+ (ImageFolders[i])+"/"+String.valueOf(id)+".png";

            fd_hu_moments(imagePath);
            determine_colorHistOfImages(imagePath);

            trainLabels.add(i);
            float[] datafloat = new float[73];
            datafloat[0] = DataSetBirdSizes[i];
            datafloat[1] = DataSetBirdSocial[i];

            for(int j=0;j<7;++j)
            {
                datafloat[2+j] = DataHuMoments[j];
            }

            for(int k=0;k<64;++k)
            {
                datafloatNewcomer[k+9]= DataColorHist[k];
            }


            Mat data = new MatOfFloat(datafloat);
            trainData.push_back(data.reshape(1,1));
        }
        }

        //Log.i("[INFO]","Done with training  data");


        trainData.convertTo(trainData, CvType.CV_32F);
        newcomer.convertTo(newcomer,CvType.CV_32F);

        KNearest knn = KNearest.create();
        knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabels));


        int K=3;

        Mat response = new Mat();
        Mat dist  = new Mat();
        Mat noArray  = new Mat();

        knn.findNearest(newcomer,K,noArray, response, dist);

        String stringResponse = response.dump();
        int birdNo;
        birdNo = Integer.parseInt(stringResponse.substring(1,2));




        return ImageFolders[birdNo];

    }

    void test_classifier(){

        float icountBirds = 0;
        for(int i=0;i<22;++i)
        {
            String filePath = Environment.getExternalStorageDirectory()+"/Pictures/Data/Birds/"+ (ImageFolders[i])+"/1.png";
            String bird;

            bird = determineBirdTypeKNN(filePath,i);

            if(bird ==(ImageFolders[i])){
                icountBirds+=1;

            }

            Log.i("[CLASSIFYING]",ImageFolders[i]+"--->"+bird);

        }
        float classificationRate = 100*(icountBirds/22);
        Log.i("CR = ",String.valueOf(classificationRate));
    }





    public void readFile(){
        if(isExternalStorageReadable()){

           //test_classifier();
          new myTask().execute();

        }
    }


    private boolean isExternalStorageReadable(){

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
        {
            Log.i("State","Yes, it's readible");
            return true;
        }
        else{

            return false;
        }
    }



    private boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes,it's writable");
            return true;
        }
        else{
                return false;
            }
        }


     public void writeFile(){

        String textString,fileName,filePath;
        textString = "Hello world";
        fileName = "/Pictures/myFile.txt";
        filePath = Environment.getExternalStorageDirectory()+fileName;

        if(isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File textFile = new File(Environment.getExternalStorageDirectory(),fileName);

            try {
                FileOutputStream fos = new FileOutputStream(textFile);
                fos.write(textString.toString().getBytes());
                fos.close();
                Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();

            }
            catch (IOException e) {

                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(this, "Cannot write to external", Toast.LENGTH_SHORT).show();
        }


     }

     public boolean checkPermission(String permission) {
       int check = ContextCompat.checkSelfPermission(this,permission);
       return (check == PackageManager.PERMISSION_GRANTED);

     }

     class myTask extends AsyncTask<Void,String,Void>{
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
         }

         @Override
         protected Void doInBackground(Void... voids) {

             for(int i=0;i<10;++i)
             {
                 publishProgress("Hello world");
             }

             test_classifier();

          return null;
         }

         @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);
         }

         @Override
         protected void onPostExecute(Void result) {
             Log.i("State","Done");

         }
     }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */



    public native int convertNativeGray(long matAddrRgba);

    //public native String svm();

    //public native String stringFromJNI();

}
