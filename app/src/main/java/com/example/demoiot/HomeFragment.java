package com.example.demoiot;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi,txtLi;
    LabeledSwitch btn1;
    SeekBar btn2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        txtTemp = view.findViewById(R.id.txtTemperature);
        txtHumi = view.findViewById(R.id.txtHumidity);
        txtLi   = view.findViewById(R.id.txtLight);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);


        btn1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("thanhliemtala/feeds/button1","1");
                }else{
                    sendDataMQTT("thanhliemtala/feeds/button1","0");
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
                        sendDataMQTT("thanhliemtala/feeds/button2","0");
                        break;
                    case 1:
                        sendDataMQTT("thanhliemtala/feeds/button2","1");
                        break;
                    case 2:
                        sendDataMQTT("thanhliemtala/feeds/button2","2");
                        break;
                    case 3:
                        sendDataMQTT("thanhliemtala/feeds/button2","3");
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
        return view;

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
        mqttHelper = new MQTTHelper(this.getContext());
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
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // Lưu trạng thái vào Bundle
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isButtonPressed", btn1.isOn());
//
//        // Lưu Bundle vào đối tượng Arguments của Fragment
//        setArguments(bundle);
//    }
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Lấy trạng thái từ đối tượng savedInstanceState
//        Bundle savedInstanceState = this.getArguments();
//        if (savedInstanceState != null) {
//          //  btn1 = savedInstanceState.getBoolean("isButtonPressed");
//
//        }
//    }
}
