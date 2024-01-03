package com.example.demoiot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi,txtLi;
    LabeledSwitch btn1;
    SeekBar btn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        txtLi   = findViewById(R.id.txtLight);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        btn1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("thinhcehcmut/feeds/nutnhan1","1");
                }else{
                    sendDataMQTT("thinhcehcmut/feeds/nutnhan1","0");
                }
            }
        });
        btn2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int u = 0;
                if(i<0.5){
                    u=0;
                }
                else if(i<=1.5){
                    u = 1;
                }else if(i<=2.5){
                    u=2;
                }
                else {
                    u=3;
                }
                switch (u){
                    case 0:
                        sendDataMQTT("thinhcehcmut/feeds/nutnhan2","0");
                        break;
                    case 1:
                        sendDataMQTT("thinhcehcmut/feeds/nutnhan2","1");
                        break;
                    case 2:
                        sendDataMQTT("thinhcehcmut/feeds/nutnhan2","2");
                        break;
                    case 3:
                        sendDataMQTT("thinhcehcmut/feeds/nutnhan2","3");
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }
    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic +"***" + message.toString());
                if(topic.contains("cambien1")){
                    txtTemp.setText("Nhiệt độ: "+message.toString()+" °C");
                }else if(topic.contains("cambien3")){
                    txtHumi.setText("Độ ẩm: "+ message.toString()+" %");
                }else if(topic.contains("cambien2")){
                    txtLi.setText("Ánh sáng: "+ message.toString()+" lux");
                }
                else if(topic.contains("button1")){
                    if(message.toString().equals("1")){
                        btn1.setOn(true);
                    }
                    else{
                        btn1.setOn(false);
                    }
                }
                else if(topic.contains("button2")){
                    if(message.toString().equals("0")){
                        btn2.setProgress(0);
                    }
                    else if(message.toString().equals("1")){
                        btn2.setProgress(1);
                    }
                    else if(message.toString().equals("2")){
                        btn2.setProgress(2);
                    }
                    else {
                        btn2.setProgress(3);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
