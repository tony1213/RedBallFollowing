package com.lynu.robot.motions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class MainActivity extends Activity implements CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private Mat mRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mColorBlobDetector;
    private Scalar CONTOUR_COLOR;

    ////////////////////////////////////////////////////
    private static int AREA_MIN = 80;    //120
    private static double THRESHOLD_X = 0.45;
    private static double THRESHOLD_Y = 0.2;
    private static double THRESHOLD_Y2 = 0.40;

    private static double THRESHOLD_CW_Y = 0.40;
    private static double RATE_SP = 0.13;//0.13;//0.08;
    private double RATE_FB_SP = 26;
    private double RATE_BACK_SP = 22;
    private double RATE_AREA = 0.10;
    private double RATE_HNECK = 0.20;

    private boolean mIsFrontBack = false;
    private boolean mIsLR = true;

    private int mImageWidth = 320;
    private int mImageHeight = 240;

    private int mXMin = 0;
    private int mXMax = mImageWidth;//320;
    private int mYMin = 0;
    private int mYMax = mImageHeight;//240;
    private int mY2Min = 0;
    private int mY2Max = 240;

    private int mCWYMin = 0;
    private int mCWYMax = mImageWidth;//320;

    private int mXMid = mImageWidth / 2;//160;
    private int mYMid = mImageHeight / 2;//120;

    private double mSelArea = 500;
    private double mLastLSp = 0;
    private double mLastRSp = 0;

    /*Search Block*/
    private boolean mIsFullCircle = false;
    private boolean mIsMissed = false;
    private boolean mIsMax = false;

    private int SEARCH_BACK_COUNT = 8;
    private int mISearchBackCount = SEARCH_BACK_COUNT;
    private int mINeckHorizon = 0;

    private int mIDirect = -1;
    private int mINeckHAngle = 130;
    private int mINeckHAngleOffset = 1;

    private int NECK_MIN = 10;//0;
    private int NECK_MAX = 250;//260;
    private int NECK_MID = mINeckHAngle;

    /**/
    private Scalar mDefaultHsv = new Scalar(240.0, 230.0, 200.0);
    private CameraBridgeViewBase mOpenCvCameraView;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.CameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    public void onDestroy() {
        super.onDestroy();
        setMoto(0, 0);
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height) {
        Log.e("WER", "onCameraViewStarted()");
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mColorBlobDetector = new ColorBlobDetector();

        mBlobColorHsv = new Scalar(255);
        CONTOUR_COLOR = new Scalar(255, 255, 0, 255);

        mColorBlobDetector.setHsvColor(mDefaultHsv);

        mImageWidth = width;
        mImageHeight = height;

        mXMin = (int) (mImageWidth * THRESHOLD_X);
        mXMax = mImageWidth - mXMin;

        mYMin = (int) (mImageHeight * THRESHOLD_Y);
        mYMax = mImageHeight - mYMin;

        mY2Min = (int) (mImageHeight * THRESHOLD_Y2);
        mY2Max = mImageHeight - mY2Min;

        mCWYMin = (int) (mImageHeight * THRESHOLD_CW_Y);
        mCWYMax = mImageHeight - mCWYMin;

        mXMid = mImageWidth / 2;
        mYMid = mImageHeight / 2;
    }

    public void onCameraViewStopped() {
        Log.e("WER", "onCameraViewStopped()");
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Log.e("WER", "onCameraFrame()");
        mRgba = inputFrame.rgba();
        move(mRgba);
        return mRgba;
    }

    /* clockwise 90*/
    private void move(Mat mRgba) {
        Log.e("WER", "move()");
        mColorBlobDetector.process(mRgba);
        List<MatOfPoint> contours = mColorBlobDetector.getContours();
        Rect rect = mColorBlobDetector.getRect();
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt = new Point();

        pt1.x = rect.x * 4;
        pt1.y = rect.y * 4;
        pt2.x = rect.x * 4 + rect.width * 4;
        pt2.y = rect.y * 4 + rect.height * 4;
        pt.x = (pt1.x + pt2.x) / 2;
        pt.y = (pt1.y + pt2.y) / 2;

        Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
        Imgproc.rectangle(mRgba, pt1, pt2, CONTOUR_COLOR);
//        Core.rectangle(mRgba, pt1, pt2, CONTOUR_COLOR);
        if (rect.width > 3 && rect.height > 3) {
            reSetColor(mRgba, new Rect(pt1, pt2));
        }
        if (mIsFullCircle) {
            if (mISearchBackCount > 0) {
                mISearchBackCount--;
                setMoto(-RATE_BACK_SP, -RATE_BACK_SP);
                return;
            }
            mIsFullCircle = false;
            mISearchBackCount = SEARCH_BACK_COUNT;
            setMoto(0, 0);
        }

        if ((pt2.x - pt1.x) * (pt2.y - pt1.y) < AREA_MIN) {  //AREA_MIN 80
            setMoto(0, 0);
            if (mIsFullCircle) {
                setMoto(-RATE_BACK_SP, -RATE_BACK_SP);
                mIsFullCircle = false;
                return;
            } else {
                mINeckHAngle += (mINeckHAngleOffset * mIDirect);
                if (mINeckHAngle < NECK_MIN) {
                    mINeckHAngle = NECK_MIN;
                    mIDirect = 1;
                } else if (mINeckHAngle > NECK_MAX) {
                    mINeckHAngle = NECK_MAX;
                    mIDirect = -1;
                    mIsMax = true;
                } else if (mIsMax && NECK_MID == mINeckHAngle) {
                    mIsFullCircle = true;
                    mIsMax = false;
                }
                mIsMissed = true;
                return;
            }
        }

        if (mIsMissed && mINeckHAngle != NECK_MID) {
            int iOffset = mINeckHAngle - NECK_MID;
            double dSp = (100 * RATE_HNECK * iOffset) / NECK_MID;
            if (dSp < 5) {
                dSp = 5;
            }
            if (iOffset < 0) {
                mINeckHAngle += mINeckHAngleOffset;
                setMoto(dSp * (-1), dSp);
            } else if (iOffset > 0) {
                mINeckHAngle -= mINeckHAngleOffset;
                setMoto(dSp, dSp * (-1));
            }
            return;
        }
        if (mIsMissed) {
            setMoto(0, 0);
            mIsMissed = false;
        }

        if (mIsLR) {
            int yOffset = (int) (pt.y - mYMid);
            yOffset = Math.abs(yOffset);
            if (pt.y < mCWYMin)	/* TURN Left*/ {
                double dSp = (100 * RATE_SP * yOffset) / mYMid;
                setMoto(dSp, dSp * (-1));
                mIsFrontBack = false;
            } else if (pt.y > mCWYMax) /* TURN Right*/ {
                double dSp = (100 * RATE_SP * yOffset) / mYMid;
                setMoto(dSp * (-1), dSp);
                mIsFrontBack = false;
            } else {
                mIsLR = false;
                if (false == mIsFrontBack) {
                    setMoto(0, 0);
                } else {
                    setMoto(mLastLSp, mLastRSp);
                }
            }
        } else {
            if (rect.area() < mSelArea * (1 - RATE_AREA)) /* GO AHEAD */ {
                setMoto(RATE_FB_SP, RATE_FB_SP);
                mIsFrontBack = true;
            } else if (rect.area() > mSelArea * (1 + RATE_AREA)) /* GO ABCK */ {
                setMoto(-RATE_FB_SP, -RATE_FB_SP);
                mIsFrontBack = true;
            } else {
                setMoto(0, 0);
            }
            mIsLR = true;
        }
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return new Scalar(pointMatRgba.get(0, 0));
    }

    private void reSetColor(Mat roiRgb, Rect dectectRect) {
        int iW = (int) (dectectRect.width * 0.3);
        int iH = (int) (dectectRect.height * 0.3);
        int iX = dectectRect.x + iW;
        int iY = dectectRect.y + iH;
        Rect centreRect = new Rect(iX, iY, iW, iH);
        Mat regionMat = roiRgb.submat(centreRect);
        Mat regionHsv = new Mat();
        Imgproc.cvtColor(regionMat, regionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        Scalar colorHsv = Core.sumElems(regionHsv);
        int pointCount = centreRect.width * centreRect.height;
        for (int i = 0; i < colorHsv.val.length; i++) {
            colorHsv.val[i] /= pointCount;
        }
        double deta = 0.0;
        deta += Math.abs(mDefaultHsv.val[0] - colorHsv.val[0]);
        deta += Math.abs(mDefaultHsv.val[1] - colorHsv.val[1]);
        deta += Math.abs(mDefaultHsv.val[2] - colorHsv.val[2]);
        if (deta < 30) {
            mColorBlobDetector.setHsvColor(colorHsv);
        }
    }

    private void setMoto(double speedL, double speedR) {
        double dLFlg = speedL * mLastLSp;
        double dRFlg = speedR * mLastRSp;
        mLastLSp = speedL * 0.8;
        mLastRSp = speedR * 0.8;
        if (dLFlg > 0 && dRFlg > 0) {
            speedL = mLastLSp;
            speedR = mLastRSp;
        }
    }
}
