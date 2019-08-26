package com.northmeter.northmetersmart.droid;


import com.northmeter.northmetersmart.R;

public class WeatherHelp {
	
	public static int get_weatherBackground(String time,String weather){
		int resource = 0;
		try{
			String time_flag = null;
			int times = Integer.parseInt(time);
			switch(weather){
			case "多云":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_cloud_day;
				}
				break;
			case "局部多云":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_cloud_day;
				}
				break;
			case "少云":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_sunny_day;
				}
				break;
			case "局部少云":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_sunny_day;
				}
				break;
			case "晴":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_sunny_day;
				}
				break;
			case "晴天":
				if(times<7){
					resource = R.drawable.bg_sunny_night;
				}else{
					resource = R.drawable.bg_sunny_day;
				}
				break;
			case "阴":
				if(times<7){
					resource = R.drawable.bg_cloud_night;
				}else{
					resource = R.drawable.bg_cloud_night;
				}
				break;
			case "阴天":
				if(times<7){
					resource = R.drawable.bg_cloud_night;
				}else{
					resource = R.drawable.bg_cloud_night;
				}
				break;
			case "小雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "雷阵雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "中雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "大雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "暴雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "阵雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "雷雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "零散阵雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "零散雷雨":
				if(times<7){
					resource = R.drawable.bg_rain_night;
				}else{
					resource = R.drawable.bg_rain_day;
				}
				break;
			case "阵雪":
				if(times<7){
					resource = R.drawable.bg_snow_night;
				}else{
					resource = R.drawable.bg_snow_day;
				}
				break;
			case "小雪":
				if(times<7){
					resource = R.drawable.bg_snow_night;
				}else{
					resource = R.drawable.bg_snow_day;
				}
				break;
			case "雨夹雪":
				if(times<7){
					resource = R.drawable.bg_snow_night;
				}else{
					resource = R.drawable.bg_snow_day;
				}
				break;			
			case "中雪":
				if(times<7){
					resource = R.drawable.bg_snow_night;
				}else{
					resource = R.drawable.bg_snow_day;
				}
				break;
			case "大雪":
				if(times<7){
					resource = R.drawable.bg_snow_night;
				}else{
					resource = R.drawable.bg_snow_day;
				}
				break;
			case "霾":
				if(times<7){
					resource = R.drawable.bg_fog_night;
				}else{
					resource = R.drawable.bg_fog_day;
				}
				break;
			default:
				resource = R.drawable.bg_fog_night;
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return resource;
	}
	
	
	/**
	 * 多云,少云,晴,阴,小雨,雨,雷阵雨,中雨,阵雨,零散阵雨,零散雷雨,小雪,雨夹雪,阵雪,霾,暴雨,大雨,大雪,中雪
	 * */
	public static int get_weather_resource(String weather_name){
		int resource = 0 ;
		try{
			
			if(weather_name.indexOf("云")>0){
				resource=R.drawable.weather_4;
			}else if(weather_name.indexOf("雨")>0){
				resource=R.drawable.weather_11;
			}else if(weather_name.indexOf("雪")>0){
				resource=R.drawable.weather_23;
			}else if(weather_name.indexOf("晴")>0){
				resource=R.drawable.weather_0;
			}else if(weather_name.indexOf("阴")>0){
				resource=R.drawable.weather_9;
			}else if(weather_name.indexOf("雷")>0){
				resource=R.drawable.weather_17;
			}else if(weather_name.indexOf("霾")>0){
				resource=R.drawable.weather_31;
			}
			
			switch(weather_name){
			case "多云":
				resource=R.drawable.weather_4;
				break;
			case "局部多云":
				resource=R.drawable.weather_5;
				break;
			case "少云":
				resource=R.drawable.weather_7;
				break;
			case "局部少云":
				resource=R.drawable.weather_5;
				break;
			case "晴":
				resource=R.drawable.weather_0;
				break;
			case "晴天":
				resource=R.drawable.weather_0;
				break;
			case "阴":
				resource=R.drawable.weather_9;
				break;
			case "阴天":
				resource=R.drawable.weather_9;
				break;
			case "小雨":
				resource=R.drawable.weather_13;
				break;
			case "雨":
				resource=R.drawable.weather_11;
				break;
			case "雷阵雨":
				resource=R.drawable.weather_16;
				break;
			case "中雨":
				resource=R.drawable.weather_14;
				break;
			case "大雨":
				resource=R.drawable.weather_15;
				break;
			case "暴雨":
				resource=R.drawable.weather_18;
				break;
			case "阵雨":
				resource=R.drawable.weather_10;
				break;
			case "雷雨":
				resource=R.drawable.weather_17;
				break;
			case "零散阵雨":
				resource=R.drawable.weather_10;
				break;
			case "零散雷雨":
				resource=R.drawable.weather_16;
				break;
			case "阵雪":
				resource=R.drawable.weather_21;
				break;
			case "小雪":
				resource=R.drawable.weather_22;
				break;
			case "雨夹雪":
				resource=R.drawable.weather_12;
				break;			
			case "中雪":
				resource=R.drawable.weather_23;
				break;
			case "大雪":
				resource=R.drawable.weather_24;
				break;
			case "霾":
				resource=R.drawable.weather_31;
				break;
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return resource;
		
	}
}
