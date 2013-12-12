package com.example.tiltapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity
{
  private String lastSound;
  private String storedValues;
  SensorEventListener listener;
  MediaPlayer audio;

  private String getStringFromOrientation(double zAzi, double xPitch, double yRoll)
  {
    if((xPitch > -25 && xPitch < 25))
    {
      if(yRoll > -45 && yRoll < 45)
      {
        return "Up";
      }
      if(yRoll > 45)
      {
        return "Left";
      }
      if(yRoll < -45)
      {
        return "Right";
      }
    }
    else if((xPitch < -25 && xPitch > -100) && (yRoll > -45 && yRoll < 45))
    {
      return "Forward";
    }
    else if((xPitch > 25 && xPitch < 130) && (yRoll > -45 && yRoll < 45))
    {
      return "Backwards";
    }
    else if((xPitch < -100 && xPitch > -160) || (xPitch > 130 && xPitch < 170))
    {
      return "Down";
    }
    return "None";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if(lastSound == null)
    {
      lastSound = "";
    }
    storedValues = "";
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    listener = new SensorEventListener()
    {
      @Override
      public void onSensorChanged(final SensorEvent sensorEvent)
      {
        final String position = getStringFromOrientation(sensorEvent.values[0],
                sensorEvent.values[1], sensorEvent.values[2]);
        if(!position.equals("None") && !position.equals(lastSound))
        {
          runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              lastSound = position;
              try
              {
                if(audio != null)
                {
                  audio.stop();
                }
                audio = new MediaPlayer();
                audio.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/tilt/" +
                        lastSound + ".mp3");
                audio.prepare();
                audio.start();
              }
              catch(IOException e)
              {
                //Java is a beautiful language with many well thought out ideas!
                e.printStackTrace();
              }
              TextView text = (TextView) findViewById(R.id.editText);
              text.setText(String.format("%s\n%.2f,\t%.2f,\t%.2f\n%s", lastSound,
                      sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2],
                      storedValues));
            }
          });
        }
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
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    if(listener != null)
    {
      SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      sensorManager.unregisterListener(listener);
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    if(listener != null)
    {
      SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
              sensorManager.SENSOR_DELAY_FASTEST);
    }
  }
}