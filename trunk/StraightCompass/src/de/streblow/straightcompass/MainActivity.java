package de.streblow.straightcompass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	// define the display assembly compass picture
	private ImageView image;

	// record the compass picture angle turned
	private float currentDegree = 0f;

	// device sensor manager
	private SensorManager mSensorManager;

	TextView tvHeading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 
		image = (ImageView) findViewById(R.id.ivCompass);

		// TextView that will tell the user what degree is he heading
		tvHeading = (TextView) findViewById(R.id.tvHeading);

		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_settings:
//			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//			startActivity(intent);
			return true;
		case R.id.action_help:
			HelpDialog help = new HelpDialog(this);
			help.setTitle(R.string.help_title);
			help.show();
			return true;
		case R.id.action_about:
			AboutDialog about = new AboutDialog(this);
			about.setTitle(R.string.about_title);
			about.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
			SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// get the angle around the z-axis rotated (relative to 0 degrees)
		float degree = Math.round(event.values[0]) % 360;

		// get the sectors around the 8 main directions
		int currentSector = (int) (-currentDegree / (360f / 16f));
		int sector = (int) (degree / (360f / 16f));
		String direction = "";
		if (sector == 15 || sector == 0) direction = "N";
		else if (sector == 1 || sector == 2) direction = "NE";
		else if (sector == 3 || sector == 4) direction = "E";
		else if (sector == 5 || sector == 6) direction = "SE";
		else if (sector == 7 || sector == 8) direction = "S";
		else if (sector == 9 || sector == 10) direction = "SW";
		else if (sector == 11 || sector == 12) direction = "W";
		else if (sector == 13 || sector == 14) direction = "NW";

		tvHeading.setText(((int)degree) + String.valueOf(Character.toChars(176)) + " " + direction);

		// create a rotation animation (reverse turn degree degrees)

		if (sector >= 14 || sector <= 1)
			// check possible zero-crossing
			if (currentSector >= 14 && sector <= 1)
				// crossing with the needle from W to E rotate image counter clockwise
				degree = 360f + degree;
			else if (currentSector <= 1 && sector >= 14)
				// crossing with the needle from E to W rotate image clockwise
				currentDegree = -360f - currentDegree;
		RotateAnimation ra = new RotateAnimation(
				currentDegree, 
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF,
				0.5f);

		// how long the animation will take place
		ra.setDuration(100);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// set the animation's interpolator to linear
		ra.setInterpolator(new LinearInterpolator());

		// Start the animation
		image.startAnimation(ra);
		currentDegree = - (degree % 360f);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}
}

