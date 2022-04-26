package at.ac.htlinn.androidexamples.mqtt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.example.chatservice.R;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient mqttAndroidClient;
    String serverUri;
    String user;
    String password = "";
    String defTopic = "test/android";
    String clientId = "Android02"; //HAS TO BE UNIQUE!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        serverUri = getString(R.string.hivemq_uri);
        user = getString(R.string.hivemq_user);
        password = getString(R.string.hivemq_password);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MQTT", "Connection lost");
                Log.e("MQTT", cause.getMessage());
                Log.e("MQTT", cause.getStackTrace().toString());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MQTT", "messageArrived");
                EditText rec = findViewById(R.id.txt_received);
                rec.append(message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("MQTT", "deliveryComplete");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(user);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Connection established");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Connect failed");
                    Log.e("MQTT", exception.getMessage());
                    Log.e("MQTT", exception.getStackTrace().toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }

        ((TextView)findViewById(R.id.txt_topic)).setText(defTopic);
    }

    public void publish(View v)
    {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(((EditText)findViewById(R.id.txt_sent)).getText().toString().getBytes());
            mqttAndroidClient.publish(defTopic, message);
            Log.i("MQTT", "Message Published");
            if(!mqttAndroidClient.isConnected()){
                Log.e("MQTT", "Connection lost");
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Subscribed!");
            Log.e("MQTT", e.getMessage());
            Log.e("MQTT", e.getStackTrace().toString());
        }
    }

    public void subscribe(View v)
    {
        try {
            mqttAndroidClient.subscribe(defTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "onFailure!");
                }
            });

        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }
}