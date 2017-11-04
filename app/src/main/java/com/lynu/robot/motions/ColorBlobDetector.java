package com.lynu.robot.motions;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColorBlobDetector {
	private static final String  TAG              = "ColorBlobDetector";	
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(30,55,55,0);//Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private Rect mRect = new Rect();
    
    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mGreyMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }
    
    public Scalar converRgb2Hsv(Scalar rgb) {
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgb);
        Mat pointMatHsv = new Mat();
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 3);

        return new Scalar(pointMatHsv.get(0, 0)[0], pointMatHsv.get(0, 0)[1], pointMatHsv.get(0, 0)[2]);
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		long start = System.currentTimeMillis();        
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
        long end = System.currentTimeMillis();
		long elapse = end - start;       
		
        Log.e(TAG, "Contours===>" + contours.size() + " , " + elapse);
        
        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            
            if (area > maxArea)
            {
                maxArea = area;
            }
        }
        
        if(maxArea < 10)
        {
        	mRect = new Rect(0,0,0,0);
        }
        else 
        {
	        each = contours.iterator();
	        while (each.hasNext()) {
	            MatOfPoint wrapper = each.next();
	            double area = Imgproc.contourArea(wrapper);
	            Rect rect = Imgproc.boundingRect(wrapper);
	            
	            if (area == maxArea)
	            {
	                mRect = rect;
	            }
	        }        
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }
    
    public void process2(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        
    	/// Convert it to gray
        Imgproc.cvtColor(mPyrDownMat, mGreyMat, Imgproc.COLOR_RGB2GRAY);
        
        /// Reduce the noise so we avoid false circle detection
        Imgproc.GaussianBlur(mGreyMat, mGreyMat, new Size(5, 5), 1.5, 1.5);
        
        Mat circles = new Mat();
		long start = System.currentTimeMillis();
		
        //Imgproc.HoughCircles( mGreyMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, mGreyMat.rows()/8, 200, 100, 0, 0 );
        Imgproc.HoughCircles( mGreyMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, mGreyMat.rows()/8, 80, 40, 0, 0 );
        //Imgproc.HoughCircles( mGreyMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, mGreyMat.rows()/8 );

		long end = System.currentTimeMillis();
		long elapse = end - start;        
        
        //Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // Find max contour area
        double maxArea = 0;
        
        //Log.e(TAG, "Circles: " + circles.cols() + " , " + mGreyMat.rows()/8);
        Log.e(TAG, "Hough  " + circles.cols() + " , " + elapse);
        
        for (int x = 0; x < circles.cols(); x++) 
        {
                double vCircle[]=circles.get(0,x);

                Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int)Math.round(vCircle[2]);
                
                
            	if(maxArea < radius * radius)
            	{
            		maxArea = radius * radius;
            		mRect = new Rect((int)(center.x - radius), (int)(center.y - radius),
            				radius * 2, radius * 2);
            	}               
                
                // draw the circle center
                //Core.circle(frame2, center, 3,new Scalar(0,255,0), -1, 8, 0 );
                // draw the circle outline
                //Core.circle(frame2, center, radius, new Scalar(0,0,255), 3, 8, 0 );
        }
        
        if(maxArea < 100)
        {
        	mRect = new Rect(0,0,0,0);
        }
    }    

    public List<MatOfPoint> getContours() {
        return mContours;
    }
    
    public Rect getRect() {
        return mRect;
    }    
}
