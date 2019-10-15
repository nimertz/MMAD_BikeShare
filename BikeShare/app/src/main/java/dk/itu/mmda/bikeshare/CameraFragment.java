package dk.itu.mmda.bikeshare;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraFragment extends Fragment implements OnTouchListener, CvCameraViewListener2 {
    private byte cont;
    private int index;

    private Button mChangeCameraButton;
    private Button mTakePictureButton;

    private JavaCameraView mOpenCvCameraView;
    private Mat mRgbaImage;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(CameraFragment.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private void saveImage(Bitmap btm) {
        File cacheDir = getContext().getCacheDir();
        File f = new File(cacheDir, "pic");

        try {
            FileOutputStream out = new FileOutputStream(
                    f);
            btm.compress(
                    Bitmap.CompressFormat.PNG,
                    100, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);


        cont = 0;
        mOpenCvCameraView = view.findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(index);
        mOpenCvCameraView.setCvCameraViewListener(this);

        index = 0;
        mChangeCameraButton = view.findViewById(R.id.camera_button);
        mChangeCameraButton.setOnClickListener(view1 -> {
            index = index ^ 1;
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.setCameraIndex(index);
            mOpenCvCameraView.enableView();
        });
        mTakePictureButton = view.findViewById(R.id.capture_button);
        mTakePictureButton.setOnClickListener(v -> {
            Bitmap btm = convertMatToBitMap(mRgbaImage);
            saveImage(btm);

            getActivity().finish();
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug())
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                    getContext(), mLoaderCallback);
        else
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgbaImage = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgbaImage.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat image = inputFrame.rgba();


        if (index == 1)
            Core.flip(image, image, 1);

        if (cont == 1)
            return convertToGrayscale(image);
        else if (cont == 2)
            return convertToBGRA(image);
        else if (cont == 3)
            return convertToCanny(image);

        mRgbaImage = findCircles(image);

        return image;
    }



    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;

        try {
            bmp = Bitmap.createBitmap(input.cols(), input.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(input, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        cont++;
        cont %= 4;
        return false;
    }

    private Mat convertToGrayscale(Mat image) {
        Mat grayscale = new Mat();
        Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_RGBA2GRAY);
        return grayscale;
    }

    private Mat convertToBGRA(Mat image) {
        Mat bgra = new Mat();
        Imgproc.cvtColor(image, bgra, Imgproc.COLOR_RGBA2BGRA);
        return bgra;
    }

    private Mat convertToCanny(Mat image) {
        Mat grayscale = convertToGrayscale(image);

        Mat thresh = new Mat();
        double otsuThresh = Imgproc.threshold(grayscale, thresh,
                0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        Mat canny = new Mat();
        Imgproc.Canny(grayscale, canny, otsuThresh * 0.5, otsuThresh);

        grayscale.release();
        thresh.release();

        return canny;
    }

    private Mat findCircles(Mat src) {
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double) gray.rows() / 16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
        }
        return src;
    }

}
