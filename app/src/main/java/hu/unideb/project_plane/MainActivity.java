package hu.unideb.project_plane;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView inclineTextView;
    private TextView statusTextView;
    private ImageView planeImageView;
    private float zRotationAngle = 0f;
    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inclineTextView = findViewById(R.id.inclineTextView);
        statusTextView = findViewById(R.id.statusTextView);
        planeImageView = findViewById(R.id.planeImageView);

        // Получаем менеджер сенсоров
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            inclineTextView.setText("Гироскоп не поддерживается");
        }
        audioPlayer = new AudioPlayer();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       flightControl(event);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void flightControl(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float zRotationSpeed = event.values[2]; // Скорость вращения вокруг Z (рад/сек)
            zRotationAngle += Math.toDegrees(zRotationSpeed) * 0.02; // Конвертация в градусы
            int resultIncline = (int)zRotationAngle * -1;
            inclineTextView.setText("Z-Rot: " + resultIncline + "°");
            planeImageView.setRotation(resultIncline);
            setStatus((int)zRotationAngle);
        }
    }

    public void setStatus(int zRotationAngle) {
        String newStatus;
        String soundFile = "";

        if (zRotationAngle < -1) { // Добавляем порог, чтобы не вызывалось при малейшем движении
            newStatus = "Takeoff";
            soundFile = "takeoff_sound";
        } else if (zRotationAngle > 1) {
            newStatus = "Landing";
            soundFile = "landing_sound";
        } else {
            newStatus = "Flight";
            soundFile = "flight_sound";
        }

        if (!newStatus.equals(statusTextView.getText().toString())) { // Только если статус изменился
            statusTextView.setText("Status: " + newStatus);
            if (!soundFile.isEmpty()) {
                audioPlayer.playAudio(this, soundFile);
            }
        }
    }

}
