/*
 * OdometryDisplay.java
 * 
 * Controls what is displayed on the NXT screen
 */
import lejos.nxt.LCD;

public class Display {
	/****
	 * Print the current values of x, y, and theta to the screen.
	 * 
	 * Modified to be called from the Odometry class, to allow for more control
	 * 
	 * @param x The current x value of the odometer in cm
	 * @param y The current y value of the domoeter in cm
	 * @param theta The current value of theta (amount the robot has rotated) in radians
	 */
	public static void printOdometryInfo(final double x, final double y, final double theta){
		new Thread(new Runnable(){ public void run(){
			LCD.clear();
			// clear the lines for displaying odometry information
			LCD.drawString("X:                  ", 0, 0);
			LCD.drawString("Y:                  ", 0, 1);
			LCD.drawString("T:                  ", 0, 2);
	
			LCD.drawString(formattedDoubleToString(x, 2), 3, 0);
			LCD.drawString(formattedDoubleToString(y, 2), 3, 1);
			LCD.drawString(formattedDoubleToString(theta, 2), 3, 2);
	
			// Get the last color value the OdometryCorrection has seen 
			// (prevent multiple instances of the color sensor)
			LCD.drawString("LightVal = " +LightLocalizer.lightValue, 0, 3);
			LCD.drawString("counter: " + Integer.toString(LightLocalizer.counter),0, 4);}}).start();}

	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;
		
		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";
		
		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long)x;
			if (t < 0)
				t = -t;
			
			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}
			
			result += stack;
		}
		
		// put the decimal, if needed
		if (places > 0) {
			result += ".";
		
			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}
		
		return result;
	}

	/******
	 * Print the main menu
	 */
	public static void printMainMenu() {
		// clear the display
		LCD.clear();

		// ask the user whether the motors should Avoid Block or Go to locations
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString(" FALL  | RISE   ", 0, 2);
		LCD.drawString("----------------", 0, 3);
		LCD.drawString("    DEMO=ENTER   ", 0, 4);
		LCD.drawString("     vvvvvv     ", 0, 5);
	}

}
