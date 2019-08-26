//package com.northmeter.northmetersmart.activity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//
//import android.animation.ValueAnimator;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Build.VERSION;
//import android.os.Build.VERSION_CODES;
//import android.text.Layout;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationSet;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.LinearLayout.LayoutParams;
//
//import com.mob.mobapi.API;
//import com.mob.mobapi.APICallback;
//import com.mob.mobapi.MobAPI;
//import com.mob.mobapi.apis.Weather;
//import com.northmeter.northmetersmart.R;
//import com.northmeter.northmetersmart.base.BaseActivity;
//import com.northmeter.northmetersmart.droid.ChangeCityActivity;
//import com.northmeter.northmetersmart.droid.WeatherHelp;
//
///**天气预报*/
//public class WeatherActivity extends BaseActivity implements OnClickListener,APICallback {
//	private Weather api;
//	private TextView city_name,city_time,today_weather,today_temputer,today_air,today_wind,today_damp;
//	private ImageView image_today_weather;
//	private LinearLayout linear_textout;
//	private LinearLayout layout_background;
//	private SharedPreferences sp;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.weather_activity);
//		init_view();
//		reg_mobapi();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
//			String citys = data.getStringExtra("city_result");
//			city_name.setText(citys);
//			api.queryByCityName(citys, this);
//			Editor editor = sp.edit();
//			editor.putString("city_name", citys);
//			editor.commit();
//			}
//	}
//
//	private void reg_mobapi(){
//		api = (Weather) MobAPI.getAPI(Weather.NAME);
//		api.queryByCityName(city_name.getText().toString(), this);
//	}
//
//	/**初始化视图数据*/
//	private void init_view(){
//		findViewById(R.id.but_back_1).setOnClickListener(this);
//		layout_background = (LinearLayout) findViewById(R.id.layout_background);
//
//		image_today_weather = (ImageView) findViewById(R.id.image_today_weather);
//
//		findViewById(R.id.change_city).setOnClickListener(this);
//
//        AnimationSet animationSet = new AnimationSet(true);
////        TranslateAnimation translateAnimation = new TranslateAnimation(
////                //X轴初始位置
////                Animation.RELATIVE_TO_SELF, 0.0f,
////                //X轴移动的结束位置
////                Animation.RELATIVE_TO_SELF,1.5f,
////                //y轴开始位置
////                Animation.RELATIVE_TO_SELF,0.0f,
////                //y轴移动后的结束位置
////                Animation.RELATIVE_TO_SELF,0.0f);
////
////        //3秒完成动画
////        translateAnimation.setDuration(15000);
////        //如果fillAfter的值为真的话，动画结束后，控件停留在执行后的状态
////        animationSet.setFillAfter(true);
////        //将AlphaAnimation这个已经设置好的动画添加到 AnimationSet中
////
////        animationSet. setRepeatCount(ValueAnimator.INFINITE);
////        animationSet. setRepeatMode(ValueAnimator.RESTART);
////        animationSet.addAnimation(translateAnimation);
////
////
////        //启动动画
////        anim_image.startAnimation(animationSet);
//
//
//
//		city_name = (TextView) findViewById(R.id.city_name);
//		city_time = (TextView) findViewById(R.id.city_time);
//		today_temputer = (TextView) findViewById(R.id.today_temputer);
//		today_weather = (TextView) findViewById(R.id.today_weather);
//		today_air = (TextView) findViewById(R.id.today_air);
//		today_wind = (TextView) findViewById(R.id.today_wind);
//		today_damp = (TextView) findViewById(R.id.today_damp);
//
//
//		linear_textout = (LinearLayout) findViewById(R.id.today_layout_2);
//		linear_textout.setOrientation(LinearLayout.HORIZONTAL);
//
//		sp = getSharedPreferences("weather_message", MODE_PRIVATE);
//		String citys_name = sp.getString("city_name", "深圳");
//		city_name.setText(citys_name);
//		today_weather.setText(sp.getString("weather", ""));
//		image_today_weather.setImageResource(WeatherHelp.get_weather_resource(sp.getString("weather", "")));
//		today_temputer.setText(sp.getString("temperature", ""));
//		today_air.setText(sp.getString("airCondition", ""));
//		today_wind.setText(sp.getString("wind", ""));
//		today_damp.setText(sp.getString("humidity", ""));
//		city_time.setText(sp.getString("city_time", ""));
//	}
//
//
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		try{
//			switch(v.getId()){
//			case R.id.but_back_1:
//				finish();
//				break;
//			case R.id.change_city:
//				startActivityForResult(new Intent(this,ChangeCityActivity.class),1);
//				break;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//
//	@Override
//	public void onError(API arg0, int arg1, Throwable arg2) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onSuccess(API api, int action, Map<String, Object> result) {
//		// TODO Auto-generated method stub
//		switch (action) {
//		case Weather.ACTION_CITYS:
//			break;
//		case Weather.ACTION_QUERY:
//			System.out.println(result);
//			linear_textout.removeAllViews();
//			onWeatherDetailsGot(result);
//			break;
//		}
//	}
//
//	// 显示天气数据
//    private void onWeatherDetailsGot(Map<String, Object> result) {
//			Editor editor = sp.edit();
//
//			@SuppressWarnings("unchecked")
//			ArrayList<HashMap<String, Object>> results = (ArrayList<HashMap<String, Object>>) result.get("result");
//			HashMap<String, Object> weather = results.get(0);
//			System.out.println(weather.get("weather"));//晴
//			today_weather.setText(weather.get("weather").toString());
//			image_today_weather.setImageResource(WeatherHelp.get_weather_resource(weather.get("weather").toString()));
//			editor.putString("weather", weather.get("weather").toString());
//
//			System.out.println(weather.get("temperature"));//23℃
//			today_temputer.setText(weather.get("temperature").toString());
//			editor.putString("temperature", today_temputer.getText().toString());
//
//			System.out.println(weather.get("humidity"));//湿度：94%
//			today_damp.setText(weather.get("humidity").toString());
//			editor.putString("humidity", today_damp.getText().toString());
//
//			System.out.println(weather.get("wind"));//东南风2级
//			today_wind.setText(weather.get("wind").toString());
//			editor.putString("wind", today_wind.getText().toString());
//
//			System.out.println(weather.get("sunrise"));//06:00
//			System.out.println(weather.get("sunset"));//18:45
//			System.out.println(weather.get("airCondition"));//优
//			today_air.setText("空气质量:  "+weather.get("airCondition"));
//			editor.putString("airCondition", today_air.getText().toString());
//
//			System.out.println(weather.get("pollutionIndex"));//19
//			System.out.println(weather.get("coldIndex"));//低发期
//			System.out.println(weather.get("dressingIndex"));//薄短袖类
//			System.out.println(weather.get("exerciseIndex"));//不适宜
//			System.out.println(weather.get("washIndex"));//不适宜
//			String time = weather.get("updateTime").toString();
//
//			layout_background.setBackgroundResource(WeatherHelp.get_weatherBackground(time.substring(8, 10),weather.get("weather").toString()));
//
//			String date = time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
//			time = time.substring(8, 10) + ":" + time.substring(10, 12) + ":" + time.substring(12);
//			System.out.println(date + " " + time);//2017-04-20 05:24:41
//			city_time.setText(date + " " + time);
//			editor.putString("city_time", city_time.getText().toString());
//			editor.commit();
//			@SuppressWarnings("unchecked")
//			ArrayList<HashMap<String, Object>> weeks = (ArrayList<HashMap<String,Object>>) weather.get("future");
//			if (weeks != null) {
//				for (HashMap<String, Object> week : weeks) {
//					System.out.println(week.get("week"));//今天    星期五
//					System.out.println(week.get("temperature"));//29°C / 21°C
//					System.out.println(week.get("dayTime"));//雷阵雨
//					System.out.println(week.get("night"));//雷阵雨
//					System.out.println(week.get("wind"));//微风 小于3级
//
//					LinearLayout linear_detailed_child = new LinearLayout(WeatherActivity.this);
//					linear_detailed_child.setOrientation(LinearLayout.VERTICAL);
//					linear_detailed_child.setGravity(Gravity.CENTER);
//					LayoutParams llp = new LayoutParams(LayoutParams.WRAP_CONTENT,
//							LayoutParams.MATCH_PARENT, 1.0f);
//					llp.setMargins(0, 1, 0, 1);
//					linear_detailed_child.setLayoutParams(llp);
//
//					//设置权重
//				    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT , 1.0f);
//				    String daytime = week.get("dayTime").toString();
//				    if(daytime==null||daytime.equals("")){
//				    	daytime=week.get("night").toString();
//				    }
//
//				    ImageView image_weather = new ImageView(WeatherActivity.this);
//				    image_weather.setLayoutParams(lp);
//				    image_weather.setImageResource(WeatherHelp.get_weather_resource(daytime));
//				    image_weather.setPadding(30, 30, 10, 30);
//				    linear_detailed_child.addView(image_weather);
//
//					TextView text_1 = new TextView(WeatherActivity.this);
//					text_1.setLayoutParams(lp);
//					text_1.setText(week.get("week").toString());
//					text_1.setTextSize(15);
//					text_1.setTextColor(Color.WHITE);
//					text_1.setPadding(30, 30, 10, 30);
//					linear_detailed_child.addView(text_1);
//
//					TextView text_2 = new TextView(WeatherActivity.this);
//					text_2.setLayoutParams(lp);
//					text_2.setText(daytime);
//					text_2.setTextSize(15);
//					text_2.setTextColor(Color.WHITE);
//					text_2.setPadding(30, 30, 10, 30);
//					linear_detailed_child.addView(text_2);
//
//					TextView text_3 = new TextView(WeatherActivity.this);
//					text_3.setLayoutParams(lp);
//					text_3.setText(week.get("temperature").toString());
//					text_3.setTextSize(15);
//					text_3.setTextColor(Color.WHITE);
//					text_3.setPadding(30, 30, 10, 30);
//					linear_detailed_child.addView(text_3);
//
//					TextView text_4 = new TextView(WeatherActivity.this);
//					text_4.setLayoutParams(lp);
//					text_4.setText(week.get("wind").toString());
//					text_4.setTextSize(15);
//					text_4.setTextColor(Color.WHITE);
//					text_4.setPadding(30, 30, 10, 30);
//					linear_detailed_child.addView(text_4);
//
//					linear_textout.addView(linear_detailed_child);
//
//				}
//
//			}
//		}
//
//
//}
//
//
////{msg=success,
//// result=[{date=2018-12-07, airQuality={no2=30,
////		fetureData=[{date=2018-12-08, aqi=56, quality=良}, {date=2018-12-09, aqi=75, quality=良},
////						{date=2018-12-10, aqi=79, quality=良}, {date=2018-12-11, aqi=68, quality=良},
////						{date=2018-12-12, aqi=65, quality=良}, {date=2018-12-13, aqi=66, quality=良}],
////			pm25=51, province=广东, city=深圳,
////		hourData=[{dateTime=2018-12-07 08:00:00, aqi=70},
////		{dateTime=2018-12-07 07:00:00, aqi=70},{dateTime=2018-12-07 06:00:00, aqi=74},
////		{dateTime=2018-12-07 05:00:00, aqi=70},{dateTime=2018-12-07 04:00:00, aqi=79},
////		{dateTime=2018-12-07 03:00:00, aqi=69},{dateTime=2018-12-07 02:00:00, aqi=55},
////		{dateTime=2018-12-07 01:00:00, aqi=54},{dateTime=2018-12-07 00:00:00, aqi=56},
////		{dateTime=2018-12-06 23:00:00, aqi=56},{dateTime=2018-12-06 22:00:00, aqi=54},
////		{dateTime=2018-12-06 21:00:00, aqi=53},{dateTime=2018-12-06 20:00:00, aqi=51},
////		{dateTime=2018-12-06 19:00:00, aqi=51},{dateTime=2018-12-06 18:00:00, aqi=48},
////		{dateTime=2018-12-06 17:00:00, aqi=46},{dateTime=2018-12-06 16:00:00, aqi=47},
////		{dateTime=2018-12-06 15:00:00, aqi=43}, {dateTime=2018-12-06 14:00:00, aqi=41},
////		{dateTime=2018-12-06 13:00:00, aqi=41}, {dateTime=2018-12-06 12:00:00, aqi=42},
////		{dateTime=2018-12-06 11:00:00, aqi=41}, {dateTime=2018-12-06 10:00:00, aqi=39},
////		{dateTime=2018-12-06 09:00:00, aqi=37}],
////		district=深圳, so2=5, aqi=70, pm10=55, updateTime=2018-12-07 09:00:00, quality=良},
////
////		sunrise=06:13, dressingIndex=单衣类, week=周五, city=深圳, updateTime=20181207093656,
////		exerciseIndex=比较适宜, washIndex=不适宜, province=广东,
////		future=[{date=2018-12-07, week=今天, night=阴, temperature=21°C / 14°C, dayTime=阴, wind=无持续风向 小于3级},
////		{date=2018-12-08, week=星期六, night=小雨, temperature=19°C / 12°C, dayTime=阴, wind=无持续风向 小于3级},
////		{date=2018-12-09, week=星期日, night=小雨, temperature=16°C / 11°C, dayTime=小雨, wind=无持续风向 小于3级},
////		{date=2018-12-10, week=星期一, night=阴, temperature=15°C / 12°C, dayTime=小雨, wind=微风 小于3级},
////		{date=2018-12-11, week=星期二, night=晴, temperature=17°C / 10°C, dayTime=阴, wind=北风 3～4级},
////		{date=2018-12-12, week=星期三, night=多云, temperature=15°C / 12°C, dayTime=晴, wind=微风 小于3级},
////		{date=2018-12-13, week=星期四, night=局部多云, temperature=21°C / 16°C, dayTime=局部多云, wind=东北偏北风 3级},
////		{date=2018-12-14, week=星期五, night=少云, temperature=23°C / 17°C, dayTime=少云, wind=东北风 3级},
////		{date=2018-12-15, week=星期六, night=局部多云, temperature=24°C / 19°C, dayTime=少云, wind=东北偏东风 3级},
////		{date=2018-12-16, week=星期日, night=晴, temperature=24°C / 17°C, dayTime=局部多云, wind=东北偏北风 3级}],
////		sunset=18:15, temperature=19℃, weather=晴, coldIndex=低发期, humidity=湿度：59%, distrct=深圳,
////		time=09:22, pollutionIndex=70, airCondition=良, wind=北风3级}], retCode=200}
//
