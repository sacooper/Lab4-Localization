import java.util.ArrayList;
import lejos.nxt.ColorSensor;

/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of this class is to serve as a localizer using the light sensor.
 * This should only be used in the corner tile, near the intersection
 * of the grid lines representing (0,0)
 */
public class LightLocalizer {
	private Odometer odo;	// Odometer to correct and use
	private Driver driver;	// Driver to control movement
	private ColorSensor cs;	// Color Sensor to use for line detection
	private static final double 
		D_LIGHT_TO_SENSOR = 15;	// Distance from center of rotation to sensor
	public static int counter = 0;	// Number of lines seen
	private ArrayList<Double> angles = new ArrayList<Double>();	// Angles lines were seen at
	public static double lightValue, theta;	// Current light value and theta
	public static final int THRESHOLD = 50; // Threshold to determine if we've seen a line (relative)
	
	/*****
	 * Instantiate a new LightLocalizer
	 * 
	 * @param odo	The odometer to use for determining theta and to correct
	 * @param driver	Driver to control movement
	 * @param cs		ColorSensor to use for line detection
	 */
	public LightLocalizer(Odometer odo, Driver driver, ColorSensor cs) {
		this.odo = odo;
		this.driver = driver;
		this.cs = cs;
	}
	
	/****
	 * Perform localization and correct the current X and Y values
	 */
	public void doLocalization() {
		cs.setFloodlight(lejos.robotics.Color.GREEN);
		odo.setModTheta(false);
		double last_val = cs.getNormalizedLightValue(),	// last light value we saw
				   start = odo.getTheta();
		
		driver.rotate(true);	// Rotate clockwise
		while (Math.toDegrees(odo.getTheta()-start) < 360){		// Rotate a full 360
			lightValue = cs.getNormalizedLightValue();	
			boolean saw_line = false;	// Whether or not we saw a line this iteration
			// Determine if we've seen a line
			if((last_val - lightValue) > THRESHOLD){
				counter++;	
				angles.add(odo.getTheta());
				saw_line = true;}
			last_val = lightValue;
			
			// If we saw a line, wait a bit longer
			try {
				if (saw_line)
					Thread.sleep(300);
				else
					Thread.sleep(25);} 
			catch (InterruptedException e) {}
		}
		odo.setModTheta(true);

	driver.stop(); // Stop rotating
	
	if (angles.size() != 4) {	// Restart if bad turn
		angles.clear();
		counter = 0;
		doLocalization();
		return;}
	
	// Correct the X and Y coordinate values
	odo.setY(-D_LIGHT_TO_SENSOR * Math.cos((angles.get(2)-angles.get(0))/2));
	odo.setX(-D_LIGHT_TO_SENSOR * Math.cos((angles.get(3)-angles.get(1))/2));
	
	} 
 }