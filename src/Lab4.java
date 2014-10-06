import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of this class is to serve as the entry point for Lab4 - Localization
 * 
 * First, the angle will be determined using Ultrasonic Localization. Once 
 * the angle of the robot is determined, the robot moves to do light sensor
 * localization to determine accurate X and Y coordinates.
 * 
 * Once these values are determiend, the Robot moves to the point (0,0)
 * and rotates to an angle of Theta=0.
 */
public class Lab4 {
	public static NXTRegulatedMotor LEFT_MOTOR = Motor.A,
				  					RIGHT_MOTOR = Motor.B;
	
	public static final int
		FALLING_EDGE = Button.ID_LEFT,
		RISING_EDGE = Button.ID_RIGHT,
		DEMO = Button.ID_ENTER;
	
	public static void main(String[] args) {
		// setup the odometer, ultrasonic sensor, and light sensor
		Odometer odo = new Odometer();
		Driver driver = new Driver(odo);
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor cs = new ColorSensor(SensorPort.S1);
		USLocalizer usl;
		Display.printMainMenu();
		int option = Button.waitForAnyPress();
		odo.start();
		switch (option){
		case FALLING_EDGE:
			usl = new USLocalizer(odo, driver, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();	
			driver.turnTo(-odo.getTheta());
			break;
		case RISING_EDGE:
			usl = new USLocalizer(odo, driver, us, USLocalizer.LocalizationType.RISING_EDGE);
			usl.doLocalization();		
			driver.turnTo(-odo.getTheta());
			break;
		case DEMO:
			// perform the ultrasonic localization
			usl = new USLocalizer(odo, driver, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			
			driver.turnTo(45);
			driver.goForward(12);
			
			// perform the light sensor localization
			LightLocalizer lsl = new LightLocalizer(odo, driver, cs);
			lsl.doLocalization();
	
			// Once localization is performed, travel to (0,0), and rotate to angle of 0
			driver.travel(0, 0);
			driver.turnTo(Math.toDegrees(-odo.getTheta()));
			break;
		default:
			System.exit(0);
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
