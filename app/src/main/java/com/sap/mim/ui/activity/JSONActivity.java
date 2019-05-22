package com.sap.mim.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sap.mim.R;
import com.sap.mim.entity.UserBean;
import com.sap.mim.util.SocketChannelClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONActivity extends AppCompatActivity {

    @Bind(R.id.bt1)
    Button button1;
    @Bind(R.id.bt2)
    Button button2;
    @Bind(R.id.bt3)
    Button button3;
    @Bind(R.id.bt4)
    Button button4;  //4个按钮
    @Bind(R.id.text)
    TextView textView;  //显示处理结果的textView

    private List<UserBean> userBeans; //保存数据的集合
    private JSONObject object;  //JSONObject对象，处理一个一个的对象
    private JSONObject object2;
    private JSONArray jsonArray;//JSONObject对象，处理一个一个集合或者数组
    private String jsonString;  //保存带集合的json字符串
    private String jsonString2;//不带集合的json字符串


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        ButterKnife.bind(this);// 完成view绑定
        initDate(); //初始化数据

        new Thread(){
            @Override
            public void run() {

                new SocketChannelClient().init();
            }
        }.start();
    }



    /**
     * 初始化2个用户对象
     */
    private void initDate() {

        userBeans = new ArrayList<>();
        UserBean userBean = new UserBean();
        userBean.setUserId(1);
        userBean.setUserName("jack");
        userBeans.add(userBean);

        UserBean userBean2 = new UserBean();
        userBean2.setUserId(2);
        userBean2.setUserName("科比");
        userBeans.add(userBean2);

    }

    private void changeArrayDataToJson() {  //把一个集合转换成json格式的字符串
        jsonArray = null;
        object = null;
        jsonArray = new JSONArray();
        object = new JSONObject();
        for (int i = 0; i < userBeans.size(); i++) {  //遍历上面初始化的集合数据，把数据加入JSONObject里面
            object2 = new JSONObject();//一个user对象，使用一个JSONObject对象来装
            try {
                object2.put("userId", userBeans.get(i).getUserId());  //从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
                object2.put("userName", userBeans.get(i).getUserName());
                jsonArray.put(object2); //把JSONObject对象装入jsonArray数组里面
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            object.put("userDate", jsonArray); //再把JSONArray数据加入JSONObject对象里面(数组也是对象)
            //object.put("time", "2013-11-14"); //这里还可以加入数据，这样json型字符串，就既有集合，又有普通数据
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonString = null;
        jsonString = object.toString(); //把JSONObject转换成json格式的字符串
        textView.setText(jsonString);
    }

    private void changeNotArrayDateToJson() {
        object = null;
        object = new JSONObject();
        try {
            object.put("userId", "1"); //把数据加入JSONObject对象即可，"userid"相当于map里面的key,1即为value的值。
            object.put("userName", "jack");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonString2 = null;
        jsonString2 = object.toString();//把JSONObject转换成json格式的字符串
        textView.setText(jsonString2);
    }

    //绑定事件监听
    @OnClick({R.id.bt1, R.id.bt2, R.id.bt3, R.id.bt4})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                changeNotArrayDateToJson(); //点击第一个按钮，把集合转换成json数据格式的string
                break;
            case R.id.bt2:
                changeArrayDataToJson(); //点击第2个按钮，把普通数据换成json数据格式的string
                break;
            case R.id.bt3:  //解析不带集合的json字符串
                if (jsonString2==null || "".equals(jsonString2)) {
                    Toast.makeText(this, "请先点击上面第1个按钮转把数据换成json字符串", Toast.LENGTH_LONG).show();
                    return;
                }
                changeJsonToData2();
                break;
            case R.id.bt4://解析带集合的json字符串
                if (jsonString==null || "".equals(jsonString)) {
                    Toast.makeText(this, "请先点击第2按钮把数据换成json字符串", Toast.LENGTH_LONG).show();
                    return;
                }
                changeJsonToData1();
                break;
            default:
                break;
        }

    }

    private void changeJsonToData1(){
        StringBuilder stringBuilder = new StringBuilder(); //用来保存解析出来的额数据，显示在textview
        UserBean userBean;
        List<UserBean> bList=new ArrayList<>();
        try {
            object = new JSONObject(jsonString); //用json格式的字符串获取一个JSONObject对象
            jsonArray = object.getJSONArray("userDate");  //通过key，获取JSONObject里面的一个JSONArray数组
            for (int i = 0; i < jsonArray.length(); i++) {  //遍历数据
                object = jsonArray.getJSONObject(i); //从JSONArray里面获取一个JSONObject对象
                userBean=new UserBean();
                userBean.setUserId(object.getInt("userId")); //通过key，获取里面的数据
                userBean.setUserName(object.getString("userName"));
                bList.add(userBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bList.size(); i++) {
            stringBuilder.append("用户id:"+bList.get(i).getUserId()).append("   ").append("用户名字:"+bList.get(i).getUserName());
        }
        textView.setText(stringBuilder.toString().replace("null", ""));
    }

    private void changeJsonToData2(){
        try {
            object = new JSONObject(jsonString2);
            String userName = object.getString("userName");
            String userIdString = object.getString("userId");
            textView.setText("用户id"+userIdString+"用户名字:"+userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
