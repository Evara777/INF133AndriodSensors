package com.example.tiltapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{

  private String storedValues;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    storedValues = "";
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    SensorEventListener listener = new SensorEventListener()
    {
      @Override
      public void onSensorChanged(final SensorEvent sensorEvent)
      {
        runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            TextView text = (TextView) findViewById(R.id.editText);
            text.setText(String.format("%s, %s, %s\n%s", Float.toString(sensorEvent.values[0]),
                    Float.toString(sensorEvent.values[1]), Float.toString(sensorEvent.values[2]),
                    storedValues));
          }
        });
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int i)
      {
      }
    };

    Button b = (Button) findViewById(R.id.button);
    b.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        TextView text = (TextView) findViewById(R.id.editText);
        storedValues = text.getText().toString();
      }
    });
    sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            sensorManager.SENSOR_DELAY_NORMAL);
  }
}