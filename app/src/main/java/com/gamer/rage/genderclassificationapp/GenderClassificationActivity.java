package com.gamer.rage.genderclassificationapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgcodecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class GenderClassificationActivity extends AppCompatActivity {

    ImageView imageView;
    FaceRecognizer faceRecognizer;
    opencv_core.MatVector images;
    opencv_core.Mat labels;
    opencv_core.Mat testSample;
    TextView text;

    public static void read_csv(String filename, opencv_core.Mat[] images, Integer[] labels) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));
            String line;
            int i=0;
            while ((line = fileReader.readLine())!=null) {
                String[] linestream = line.split(";");
                if(linestream[0]!=null && linestream[1]!=null) {
                    images[i] = (opencv_imgcodecs.imread(linestream[0]));
                    opencv_core.CV_32SC1
                    labels[i] = Integer.parseInt(linestream[1]);
                }
                i++;
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void train() {
        faceRecognizer = opencv_face.createFisherFaceRecognizer();
       //Give name of the stored csv file
        // These vectors hold the images and corresponding labels.
        images = new opencv_core.MatVector();
        Integer[] labelArray = new Integer[161];
        opencv_core.Mat[] imgArray = new opencv_core.Mat[161];
        read_csv(CameraActivity.mFileDir.getAbsolutePath() + "/DCIM/testdata.csv", imgArray, labelArray);
        opencv_core.Mat labelsMat = new opencv_core.Mat(0,40,opencv_core.CV_32SC1);
        labels.create(1, 40, opencv_core.CV_32SC1);

        for(int i=0;i<40;i++) {
           labelsMat.push_back();
        }
        labels.put(labelsMat);
        images.put(imgArray);

        faceRecognizer.train(images, labels);
    }
    public int classifyGender() {

        // Read in the data. This can fail if no valid
        // input filename is    given.
        testSample = opencv_imgcodecs.imread(CameraActivity.mFileDir.getAbsolutePath()+ "/DCIM/selfie.jpg");
        int predictedLabel = faceRecognizer.predict(testSample);
        return predictedLabel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_classification);
        imageView = (ImageView) findViewById(R.id.imageView);
        File imgFile = new  File(CameraActivity.mFileDir.getAbsolutePath() + "/DCIM/selfie.jpg");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        text = (TextView) findViewById(R.id.textView);
        text.setText("Calling train");
        try {
            new trainer().execute().get(120, TimeUnit.SECONDS);
        }
        catch(Exception e) {
e.printStackTrace();
        }
        text.setText("Calling classify");
        int result = classifyGender();
        if(result == 0) {
            Toast.makeText(this,"Need a masquara",Toast.LENGTH_LONG).show();
        }
        else if(result == 1) {
            Toast.makeText(this,"Need a razor",Toast.LENGTH_LONG).show();
        }
    }

    class trainer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            train();
            return null;
        }
    }
}
