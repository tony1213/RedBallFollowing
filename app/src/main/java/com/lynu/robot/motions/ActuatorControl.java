package com.lynu.robot.motions;

//import SerialPortInterface.SPInterface;

public class ActuatorControl {

    private static final String TAG = "ActuatorControl";

    //
    private int SLEEPTIME = 10;

    /**/
    private int mINeckHorizon = 0;
    private int mINeckVertical = 1;
    private int mINeckHAngle = 130;
    private int mINeckVAngle = 0;

//    private SPInterface				mSPInterface;

    public ActuatorControl() {

//		mSPInterface = new SPInterface();
    }

    public void openActuator() {

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

                int rgb[] = {160, 20, 20};

				/**/
//		        mSPInterface.setNeckData(mINeckHorizon, mINeckHAngle);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				
				/**/
//				mSPInterface.setNeckData(mINeckVertical, mINeckVAngle);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				
				/**/
//				mSPInterface.setEyeColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
                int[] lightsData = new int[16];
                for (int i = 0; i < 16; ++i) {
                    lightsData[i] = 1;
                }
//		        mSPInterface.setLightsData(lightsData);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setNeckColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setBottomColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setMotorData(0, 0);
            }
        }.start();
    }

    public void closeActuactor() {

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

                int rgb[] = {0, 0, 0};
				
				/**/
//				mSPInterface.setEyeColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
                int[] lightsData = new int[16];
                for (int i = 0; i < 16; ++i) {
                    lightsData[i] = 0;
                }
//		        mSPInterface.setLightsData(lightsData);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				
				/**/
//		        mSPInterface.setNeckColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setBottomColor(rgb);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setMotorData(0, 0);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		        /**/
//		        mSPInterface.setNeckData(mINeckHorizon, mINeckHAngle);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				
				/**/
//				mSPInterface.setNeckData(mINeckVertical, 30);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //
    public void resetNeck() {

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

//				mSPInterface.setNeckData(mINeckHorizon, mINeckHAngle);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

//				mSPInterface.setNeckData(mINeckVertical, mINeckVAngle);
                try {
                    sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }.start();

    }

    public void setNeckData(int index, int angle) {

//		mSPInterface.setNeckData(index, angle);
    }

    public void setMoto(double speedL, double speedR) {

//		mSPInterface.setMotorData(speedL, speedR);
    }

    public void onDestroy() {

//		mSPInterface.onDestroy();
    }

}
