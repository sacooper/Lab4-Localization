import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of this class is to serve as a localizer using the ultrasonic
 * sensor. The localization type is determined at instantiation.
 * 
 * Based on the typee of localization, the robot use the ultrasonic
 * sensor to detmine the correct heading.
 */
public class USLocalizer {
	
	/***
	 * Enum representing the type of localization to use
	 */
	public static enum LocalizationType {
		FALLING_EDGE, RISING_EDGE};

	public static final double 
		WALL_DISTANCE = 30,		// Ideal wall distance
		NOISE = 5;				// Noise value to prevent errors

	// Odometer to use to determine current relative angle
	private Odometer odo;	
	
	// Driver controlling robot movement
	private Driver driver;
	
	// Ultrasonic sensor to use for localization
	private UltrasonicSensor us;
	
	// Localization type to use
	private LocalizationType locType;

	/********
	 * Instantiate a new USLocalizer with the following parameters
	 * 
	 * @param odo - Odometer to use for determining relative angle
	 * 				and setting correct angle
	 * @param driver	Driver to use to control navigation
	 * @param us		Ultrasonic Sensor to use for localization
	 * @param locType	Localization Type to use during localization
	 */
	public USLocalizer(Odometer odo, Driver driver, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.driver = driver;
		this.us = us;
		this.locType = locType;

		// switch off the ultrasonic sensor
		us.off();
	}

	/*********
	 * Perform localization based on the Localization Type passed in
	 * during instantiation. Once is determined, go to a real heading
	 * of 45 deg and go forward 5cm
	 */
	public void doLocalization() {
		double angleA, angleB, errorAngle;
		switch (locType) {
		case FALLING_EDGE:
			// rotate the robot until it sees no wall
			rotateRisingEdge(true);
			// to avoid seeing one wall twice
			Sound.beep();
			driver.turnTo(25);
			Sound.beep();
			// keep rotating until the robot sees a wall, then latch the angle
			rotateFallingEdge(true);
			angleA = odo.getTheta();
			Sound.beep();
			driver.turnTo(-25);
			Sound.beep();
			// switch direction and wait until it sees no wall
			rotateRisingEdge(false);
			// keep rotating until the robot sees a wall, then latch the angle
			rotateFallingEdge(false);
			angleB = odo.getTheta();
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			errorAngle = getAngle(angleA, angleB);
			// update the odometer position (example to follow:)
			driver.turnTo(errorAngle);
			odo.setThetaDeg(0);
			odo.setX(0);
			odo.setY(0);
			break;

		case RISING_EDGE:
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall. This
			 * is very similar to the FALLING_EDGE routine, but the robot will
			 * face toward the wall for most of it.
			 */
			// finds wall
			rotateFallingEdge(true);
			// goes to end of wall
			rotateRisingEdge(true);
			angleA = odo.getTheta();

			Sound.beep();
			driver.turnTo(15);
			Sound.beep();
			// goes in the opposite direction towards a wall
			rotateFallingEdge(false);

			angleB = odo.getTheta();

			errorAngle = getAngle(angleA, angleB);
			driver.turnTo(errorAngle);
			odo.setThetaDeg(0);
			odo.setX(0);
			odo.setY(0);

			break;
		}
	}

	/*******
	 * Rotate, looking for the rising edge
	 * 
	 * @param clockwise Whether to rotate clockwise
	 */
	private void rotateRisingEdge(boolean clockwise) {
		driver.rotate(clockwise);
		double distance = 0;
		while (distance < (WALL_DISTANCE + NOISE)) {
			distance = getFilteredData();} 
		driver.stop();
	}

	/*******
	 * Rotoate, looking for the falling edge
	 * 
	 * @param direction
	 *            true is clockwise, false is counterclockwise rotation
	 */
	private void rotateFallingEdge(boolean direction) {
		driver.rotate(direction);
		double distance = getFilteredData();
		while (distance > (WALL_DISTANCE - NOISE)) {
			distance = getFilteredData();}
		driver.stop();
	}

	/******
	 * Get the correct 
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private double getAngle(double alpha, double beta) {
		/*
		 * if (alpha > beta) 
		 * 		return (225 - (alpha + beta)/2) 
		 * else 
		 * 		return (45 - (alpha + beta)/2);
		 */

		return (alpha > beta) 
			? (45 - (alpha + beta) / 2)
			: (225 - (alpha + beta) / 2);
	}

	/*******
	 * Get a value from the ultrasonic sensor for the current distance from the wall
	 * 
	 * @return A filtered value of the distance from the wall
	 */
	private int getFilteredData() {
		int dist;

		// do a ping
		us.ping();
		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		// there will be a delay here
		dist = us.getDistance();
		if (dist > 50)
			dist = 50;
		return dist;
	}

}
