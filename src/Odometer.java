import lejos.nxt.LCD;

/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of this class is to serve as an odometer, keeping track of the current 
 * X, Y, and Theta coodinates.
 */

public class Odometer extends Thread {
	// robot position
	/*constants*/
	
	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 15;
	//LCD update period
	
	private static final double WHEEL_BASE = 15.5,
								WHEEL_RADIUS = 2.16;	
	/*variables*/ 
	private static int previousTachoL,          /* Tacho L at last sample */
					   previousTachoR,         /* Tacho R at last sample */
					   currentTachoL,           /* Current tacho L */
					   currentTachoR;           /* Current tacho R */
	private double x, y, theta;
	private boolean modTheta; // Whether or not to keep theta within [0, 360)
	
	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		this.modTheta = true;
		Lab4.LEFT_MOTOR.resetTachoCount();
		Lab4.RIGHT_MOTOR.resetTachoCount();
		
		previousTachoL = 0;
		previousTachoR = 0;
		currentTachoL = 0;
		currentTachoR = 0;
		
	    LCD.clear();
	    LCD.drawString("Odometer Demo",0,0,false);
	    LCD.drawString("Current X  ",0,4,false);
	    LCD.drawString("Current Y  ",0,5,false);
	    LCD.drawString("Current T  ",0,6,false);

	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		for(int i = 0; true; i++){
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			double leftDistance, rightDistance, deltaDistance, deltaTheta, dX, dY;
			currentTachoL = Lab4.LEFT_MOTOR.getTachoCount();
			currentTachoR = Lab4.RIGHT_MOTOR.getTachoCount();
			
			leftDistance = 3.14159 * WHEEL_RADIUS * (currentTachoL - previousTachoL) / 180;
			rightDistance = 3.14159 * WHEEL_RADIUS * (currentTachoR - previousTachoR) / 180;
			
			previousTachoL = currentTachoL;
			previousTachoR = currentTachoR;
			
			deltaDistance = .5 * (leftDistance + rightDistance);
			deltaTheta = (leftDistance - rightDistance) / WHEEL_BASE;

			synchronized (this) {
				// don't use the variables x, y, or theta anywhere but here!
				if (modTheta)
					theta = (theta + deltaTheta) % (2 * Math.PI);
				else theta += deltaTheta;
				
				dX = deltaDistance * Math.sin(theta);
				dY = deltaDistance * Math.cos(theta);
				
				x += dX;
				y += dY;
			}

			if (i%5==0) Display.printOdometryInfo(x, y, theta);
			
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	
	// Getters and Setters for Odometer parameters
	public synchronized double getX() {return x;}

	public synchronized double getY() {return y;}

	public synchronized double getTheta() {return theta;}

	public synchronized boolean modTheta() {return modTheta;}

	public synchronized void setX(double x) { this.x = x; }

	public synchronized void setY(double y) { this.y = y; }

	public synchronized void setThetaRad(double theta) { this.theta = theta % (2 * Math.PI); }
	
	public synchronized void setThetaDeg(double theta) { this.theta = Math.toRadians(theta % (360)); }

	public synchronized void setModTheta(boolean modTheta) {
		this.modTheta = modTheta;
		theta %= (Math.PI * 2);}
}