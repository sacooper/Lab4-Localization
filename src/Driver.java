/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of this class is to serve as a driver, controlling the movement 
 * of the robot. This is done by using the odometer, which is passed in as a
 * paramater in the constructor, to determine the angle to turn to and the
 * distance to travel as a function of the current position, current angle, 
 * and destination
 */
public class Driver {
	
	private static final int 
		FORWARD_SPEED = 250,
		LOCALIZE_SPEED = 100;
	
	private static final double 
		WHEEL_BASE = 15.5,
		WHEEL_RADIUS = 2.16;
	
	private double thetar, xr, yr;
	private boolean navigating;
	private Odometer odo;
	
	public Driver(Odometer odometer){
		this.odo =  odometer;
		navigating = false;
	}
	
	/**
	 * Has the robot move to a position, relative to starting coordinates
	 * 
	 * Calculates angle and distance to move to using basic trig and then calls
	 * the turnTo and goForward method to move to that point
	 * 
	 * @param x Coordinate of destination
	 * @param y Coordinate of destination
	 */
	public void travel (double x, double y){
		//gets position. Synchronized to avoid collision
			synchronized (odo) {
				thetar = odo.getTheta() * 180 / Math.PI;
				xr = odo.getX();
				yr = odo.getY();
			}
			//calculates degrees to turn from 0 degrees
			double thetad =  Math.atan2(x - xr, y - yr) * 180 / Math.PI;
			//calculates actual angle to turn
			double theta =  thetad - thetar;
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
			//finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
			
			this.navigating = true;
			if(theta < -180){
				turnTo(theta + 360);
			}
			else if(theta > 180){
				turnTo(theta - 360);
			}
			else turnTo(theta);
			//updates values to display
			
			goForward(distance);
			
			this.navigating = false;}
	
	
	/*****
	 * Go forward a specified distance
	 * 
	 * @param distance Distance forward to travel in cm
	 */
	public void goForward(double distance){
		
		// drive forward 
		Lab4.LEFT_MOTOR.setSpeed(FORWARD_SPEED);
		Lab4.RIGHT_MOTOR.setSpeed(FORWARD_SPEED);
		
		Lab4.LEFT_MOTOR.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		Lab4.RIGHT_MOTOR.rotate(convertDistance(WHEEL_RADIUS, distance), false);}
	
	
	/*********
	 * Turn a specified amount
	 * 
	 * @param theta The amount to turn in degrees
	 */
	public void turnTo (double theta){
	
		// turn degrees clockwise
		Lab4.LEFT_MOTOR.setSpeed(LOCALIZE_SPEED);
		Lab4.RIGHT_MOTOR.setSpeed(LOCALIZE_SPEED);
		
		//calculates angel to turn to and rotates
		Lab4.LEFT_MOTOR.rotate(convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), true);
		Lab4.RIGHT_MOTOR.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), false);}
	
	/********
	 * Rotate in place
	 * 
	 * @param rotateClockwise Whether or not to rotate clockwise
	 */
	public void rotate (boolean rotateClockwise){
		Lab4.LEFT_MOTOR.setSpeed(LOCALIZE_SPEED);
		Lab4.RIGHT_MOTOR.setSpeed(LOCALIZE_SPEED);
		if (rotateClockwise){
			Lab4.LEFT_MOTOR.forward();
			Lab4.RIGHT_MOTOR.backward();
		} else {
			Lab4.LEFT_MOTOR.backward();
			Lab4.RIGHT_MOTOR.forward();
		}
	}
	
	/****
	 * Stop the robot in place
	 */
	public void stop(){
		Lab4.LEFT_MOTOR.setSpeed(0);
		Lab4.RIGHT_MOTOR.setSpeed(0);
	}
	
	/**
	 * Returns true if the robot is navigating
	 * 
	 * @return Whether or not the robot is currently navigating to a point
	 */
	public boolean isNavigating(){
		return this.navigating;
	}
	
	/**
	 * Returns degrees to turn servos in order to rotate robot by that amount
	 * 
	 * Uses basic math to convert and absolute angle to degrees to turn.
	 * 
	 * @param Radius of lego wheel (cm)
	 * @param Width of wheel base (cm)
	 * @param Absolute angle to turn  (degrees)
	 * 
	 * @return Degrees the servo should turn
	 */
	public static int convertAngle(double radius, double width, double angle) {
		//(width * angle / radius ) / (2)
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * Moves robot linerly a certain distance
	 * 
	 * @param Radius of lego wheel
	 * @param Distance to travel
	 * 
	 * @return degrees to turn servos in order to move forward by that amount
	 */
	public static int convertDistance(double radius, double distance) {
		// ( D / R) * (360 / 2PI)
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
