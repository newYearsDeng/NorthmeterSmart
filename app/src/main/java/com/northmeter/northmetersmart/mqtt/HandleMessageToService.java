package com.northmeter.northmetersmart.mqtt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDeviceData;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.db.DBInfraredCode;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceData;
import com.northmeter.northmetersmart.device.MyDiagramData;
import com.northmeter.northmetersmart.order.MyResult;
import com.northmeter.northmetersmart.order.MyResultCentral;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

/**
 * PublishMqttMessage收到的消息后,处理订阅的topic发过来的message;
 * 2016-3-22
 * */
public class HandleMessageToService {
	static int flag;
	private static DBDeviceData dbdate;
	private static DBDiagramData dbDiagram;
	private static DBInfraredCode dbinfrared;
	private DBDevice dbdevice;
	private SharedPreferences spf;
	private static Editor edit;
	public HandleMessageToService(Context context){
		this.dbdate = new DBDeviceData(context);
		this.dbdevice = new DBDevice(context);
		this.dbDiagram = new DBDiagramData(context);
		this.dbinfrared = new DBInfraredCode(context);
		this.spf = context.getSharedPreferences("status", 0);
		edit=spf.edit();
	}
	
	public static synchronized String[] handleMqttM(final String receive){
		String[] getreceive = null;
		try{
			switch(getflag(receive)){
			case 1://登录包、设备上线信息;statusMsg/0a0003ahup#000663007098#1
				flushLonginMsg(receive.split("/")[1].split("#")[1]);
				getreceive = new String[]{"STATUSMSG",receive};	
				edit.putString(receive.split("/")[1].split("#")[1],"200");
				edit.commit();
				break;
			case 2://报警信息;
				flushThzMsg(receive.split("/")[2],"01");
				getreceive = new String[]{"WARNINGMSG",receive};
				break;
			case 9://warnpush/warn#分体空调#150721023750#电量已使用0.05kWh,接近定额值:0.2
				getreceive = new String[]{"warnpush",receive.split("/")[1]};
				break;
			case 10://alarm/AlarmType#on(off)#类型#表号#时间#内容
				getreceive = new String[]{"alarm",receive};
				break;
			case 3://设备数据块上报信息; //dataMsg/0a0001a4r5#150907000125#3832ddef35743433333333a335343353366a5b343354b53333b4b73633b8aa33383333334c47464349
				try{		       //dataMsg/0a0001aa7k#131223003322#3832ffef8c67363348557433337a3333a3353235835a3833
					String receive_last = receive.split("/")[1];
					edit.putString(receive_last.split("#")[1],"200");
					edit.commit();
					//dataMsg/0a0001a4r5#150907000125#3832ddef35743433333333a335343353366a5b343354b53333b4b73633b8aa33383333334c47464349
				 if(receive_last.split("#")[0].equals("0a0001a4r5")){
						List<MyDeviceData> devicesData = dbdate.GetMyDeviceData();
						MyDeviceData device = null;
						if(!devicesData.isEmpty()){
							for(int i=0;i<devicesData.size();i++){
								if(devicesData.get(i).getMac().equals(receive_last.split("#")[1])){
									device = devicesData.get(i);
								}
							}
						}	
						String receive_type = receive_last.split("#")[0];//上报数据属于那种设备类型；
						String receive_replace = receive_last.split("#")[2];//数据域；
						String msg = getLastCenterReceive(receive_type,receive_replace);//把数据645解析；

						System.out.println("msg:"+msg);
						
						MyResultCentral myResult = new MyResultCentral(msg);
						
						/**zdn->zdn, dy->sdwdjms, dl->fsdw, gl->gl, wg->hjwd, pl->Low_speed_time/Low_speed_time/Low_speed_time , glys->ysje/ztz , thzzt ->kgzt*/
						MyDeviceData myDeviceData = new MyDeviceData(receive_last.split("#")[1],getTime(),
								"on", myResult.getZdn(), myResult.getSdwdjms(),
								myResult.getFsdw(), myResult.getGl(), myResult.getHjwd(),
								myResult.getLow_speed_time()+"/"+myResult.getMiddle_speed_time()+"/"+myResult.getHigh_speed_time(),
								myResult.getSyje()+"/"+myResult.getZtz(), myResult.getKgzt());
						if(device!=null){
							// 更新数据库
							dbdate.Update(myDeviceData);
						}else{
							// 插入数据
							dbdate.Insert(myDeviceData);
						}
						System.out.println("插入数据表DiagramData: "+myResult.getZdn()+" / "+myResult.getGl());
						String kgzt = "00";//用作方波图显示时，由于中央空调跳合闸状态的01为关，所有需要调整为0
						if(myResult.getKgzt().equals("00")){
							kgzt = "01";
						}else{
							kgzt = "00";
						}
						MyDiagramData diagram_data = new MyDiagramData(receive_last.split("#")[1],getTime_(),myResult.getZdn(),kgzt,myResult.getHjwd());
						dbDiagram.Insert(diagram_data);
						
						// 表号/标示码（UPDATA）
						getreceive = new String[]{"UPDATA_NOTIFY",receive_last.split("#")[0]+"/"+receive_last.split("#")[1]+"/UPDATA"};
						
					}else{
						//分体空调，插座，四路灯控；
						List<MyDeviceData> devicesData = dbdate.GetMyDeviceData();
						MyDeviceData device = null;
						if(!devicesData.isEmpty()){
							for(int i=0;i<devicesData.size();i++){
								if(devicesData.get(i).getMac().equals(receive_last.split("#")[1])){
									device = devicesData.get(i);
								} 
							}
						}	
						String receive_type = receive_last.split("#")[0];//上报数据属于那种设备类型；
						String receive_replace = receive_last.split("#")[2];//数据域；
						System.out.println("receive_replace=========="+receive_replace);
						String msg = getLastReceice(receive_type,receive_replace);//把数据645解析；
						System.out.println("msg:"+msg);
						MyResult myResult = new MyResult(msg);
						MyDeviceData myDeviceData = new MyDeviceData(receive_last.split("#")[1],getTime(),
								"on", myResult.getZdn(), myResult.getDy(),
								myResult.getDl(), myResult.getGl(), myResult.getWg(),
								myResult.getPl(), myResult.getGlys(), myResult.getThzzt());
						if(device!=null){
							// 更新数据库
							dbdate.Update(myDeviceData);
						}else{
							// 插入数据
							dbdate.Insert(myDeviceData);
						}
						System.out.println("插入数据表DiagramData: "+myResult.getZdn()+" / "+myResult.getGl());
						MyDiagramData diagram_data = new MyDiagramData(receive_last.split("#")[1],getTime_(),myResult.getZdn(),myResult.getGl(),myResult.getWg());
						dbDiagram.Insert(diagram_data);
						
						// 表号/标示码（UPDATA）
						getreceive = new String[]{"UPDATA_NOTIFY",receive_last.split("#")[0]+"/"+receive_last.split("#")[1]+"/UPDATA"};
						
					}
					
					
	
				}catch(Exception e){
					e.printStackTrace();
				}

				break;
				
			case 4://抄表数据
				getreceive = handleReadControlData(receive);			
				break;
				
			case 5://连接超时
				getreceive = new String[]{"NOTIFY_ZIGBEE",receive.split("/")[1]+"/"+receive.split("/")[2]+"/TIMEOUT"};
				break;
				
			case 6://添加档案数据 addmanagerecord/0a0003ahup/000090600011/添加失败
				getreceive = new String[]{"AddManageRecord",receive.split("/")[2]+"/AddManageRecord/"+receive.split("/")[receive.split("/").length-1]};
				break;
//			case 7://获取报表数据
//				switch(receive.split("/")[3]){
//				case "read30point"://selected/0a0001aa7k/002014110119/read30point/data
//					getreceive = new String[]{"SELETC",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "read48h":
//					getreceive = new String[]{"SELETC",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_ranking"://selected/Type/Meter/get_ranking/002014110119 2 2 1.00 1 1.00
//					getreceive = new String[]{"SELETC",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "read01point"://日期 时间 电量 电压 电流 功率 温度 电网频率 功率因子 继电器状态
//					break;
//				case "get_meter_current_blob":// 中央空调实时数据块  selected/0a0001aa7k/002014110119/get_meter_current_blob/data
//					boolean flag = handle_meter_current_blob(receive);
//					if(flag){
//						getreceive = new String[]{"SELECT_CENTER",receive.split("/")[1]+"/"+receive.split("/")[2]+"/get_meter_current_blob_Success"};
//					}else{
//						getreceive = new String[]{"SELECT_CENTER",receive.split("/")[1]+"/"+receive.split("/")[2]+"/get_meter_current_blob_Fail"};
//					}
//					break;																
//				case "get_socket_readtime_workflow"://卡座设备首界面活动显示    selected/0a0001aa7k/002014110119/get_socket_readtime_workflow/data
//					getreceive = new String[]{"SELETC",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_socket_workflow_history"://活动流历史记录  selected/0a0001a820/160127001282/get_socket_workflow_history/2016-09-29/13:37:47 active
//					int state = receive.indexOf("get_socket_workflow_history")+"get_socket_workflow_history".length();	
//					getreceive = new String[]{"SELETC",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+receive.substring(state, receive.length())};
//					break;
//				case "get_cycle_tasks":
//					try{//selected/0a0001a820/160127001286/get_cycle_tasks/  4&002014110119&15:00&0003&ON&model_1&20&speed_1&updown_off&leftright_on&[wen]
//						//selected/0a0001a4r5/150907000125/get_cycle_tasks/  01&150907000125&09:31&0a0001a4r5
//						System.out.println("--------------收到定时任务:"+receive);
//						String task_back = receive.split("/")[4];//获取到数据域
//						dbinfrared.DeleteAll(receive.split("/")[2]);
//						
//						if(!task_back.equals("null")){
//							String[] task_back_all_list = task_back.split("\n");//如果获取到多条记录，按分好分离
//							for(int i=0;i<task_back_all_list.length;i++){
//								String[] task_back_list = task_back_all_list[i].split("&");
//								if(receive.split("/")[1].equals(Type_Entity.Split_air_conditioning)){//分体空调
//									MyInfraredCode myinfrared=new MyInfraredCode(Integer.parseInt(task_back_list[0]),task_back_list[1],task_back_list[2],task_back_list[3],task_back_list[4],task_back_list[5],task_back_list[6],task_back_list[7],task_back_list[8],task_back_list[9],task_back_list[10]);
//									dbinfrared.Insert(myinfrared);
//								}else if(receive.split("/")[1].equals(Type_Entity.Central_air_conditioning)){//中央空调
//									//Integer.parseInt(switch_onoff),str_mac,format(time_1)+":"+format(time_2), str_ver,switch_onoff,null,null,null,null,null,weekdays.toString());
//									MyInfraredCode myinfrared=new MyInfraredCode(Integer.parseInt(task_back_list[0]),task_back_list[1],task_back_list[2],task_back_list[3],task_back_list[0],null,null,null,null,null,task_back_list[4]);
//									dbinfrared.Insert(myinfrared);
//								}
//							}
//						}
//
//						getreceive = new String[]{"NOTIFY_AIR_TASK",receive.split("/")[1]+"/"+receive.split("/")[2]+"/TASK_UPDATA"};
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//					break;
//					
//				case "get_one_tasks"://selected/0a0001aa7k/150721023750/get_one_tasks/2016-09-28 13:50:23#2016-09-28 19:50:23#hz
//					getreceive = new String[]{"NOTIFY_ONE_TASK",receive.split("/")[1]+"/"+receive.split("/")[2]+"/get_one_tasks/"+receive.split("/")[4]};
//					break;
//				case "get_cq_of_gateway"://查询网关内档案通讯质量 selected/0a0003ahup/42435c5573fe/get_cq_of_gateway/data
//					getreceive = new String[]{"SELECT_cq",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_cq_of_meter"://查询单个设备通讯质量 selected/0a0001aa7k/002014110119/get_cq_of_meter/data
//					getreceive = new String[]{"SELECT_cq",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_report_year_month_list"://查询月报表月份列表   selected/表类型/表号/get_report_year_month_list/data
//					getreceive = new String[]{"SELECT_REPORT",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_report_of_month"://查询具体月份报表数据  selected/ 表类型/表号/get_report_of_month/data
//					int states = receive.indexOf("get_report_of_month")+"get_report_of_month".length();
//					getreceive = new String[]{"SELECT_REPORT",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+receive.substring(states, receive.length())};
//					break;
//				case "get_meter_used_ele_list_of_day"://查询具体月份日用电数据  selected/ 表类型/表号/get_meter_used_ele_list_of_day/data
//					getreceive = new String[]{"SELECT_REPORT",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_meter_used_time_list_of_day"://查询具体月份日用电数据  selected/ 表类型/表号/get_meter_used_ele_list_of_day/data
//					getreceive = new String[]{"SELECT_REPORT",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_work_activities"://活动数据查询 //查询具体月份日用电数据  selected/ 表类型/表号/get_work_activities/data
//					getreceive = new String[]{"SELECT_REPORT",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				case "get_quota"://selected/type/Meter/get_quota/data
//					getreceive = new String[]{"NOTIFY_QUOTA_TASK",receive.split("/")[1]+"/"+receive.split("/")[2]+"/"+receive.split("/")[3]+"/"+receive.split("/")[4]};
//					break;
//				}
//				
//				break;
//				
//			case 8://收到数据库错误：databaseerror/topic/database is locked
//				getreceive = new String[]{"NOTIFY_ZIGBEE",receive.split("/")[2]+"/"+receive.split("/")[2]+receive.split("/")[3]};
//				break;
//				
//				
//				default:
//					getreceive = null;
//					break;
					
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return getreceive;
	} 
	
	private static int getflag(String receive){
		try{
			if(receive.indexOf("result")<0){//目前通过检查是否存在result数据块来判断该数据是不是抄表返回数据；
				if(receive.split("/")[receive.split("/").length-1].equals("指令超时")){
					flag = 5;//连接超时：0a0003ahup/140523006582/yckz/tz/指令超时
				}else{
					switch(receive.split("/")[0]){
					case "statusMsg"://登录包、设备上线信息;
						flag = 1;
						break;
					case "warningMsg"://报警信息;
						flag = 2;
						break;
					case "dataMsg"://设备数据块上报信息;//dataMsg/0a0001a4r5/150907000125/3832ffef685b34339455333333333333333632ca7c3343343333334c47393c493354
						flag = 3;
						break;
					case "addmanagerecord":// addmanagerecord/0a0003ahup/000000000015/采集设备添加成功
						flag = 6;
						break;
					case "selected"://获取报表数据:selected/0a0001aa7k/002014110119/read30point/data   selected/0a0001aa7k/002014110119/read48h/data
						flag = 7;	//排名数据:   selected/type/meter/ranking/2016/5/002014110119 2 1 0.50 1 1.00 160127001285 0 0 0.00 0 0.00
						break;
					case "databaseerror"://数据库错误：databaseerror/0a0003ahup/000000000015/database is locked
						flag = 8;
						break;
					case "warnpush"://额度报警：warnpush/warn#分体空调#150721023750#电量已使用0.05kWh,接近定额值:0.2
						flag = 9;
						break;
					case "alarm"://alarm/AlarmType#on(off)#Type#meter#建筑#时间#内容
						flag = 10;
						break;
						
						default:
							flag = 11;
							break;
						}
				}
			}else{
				//收到抄表数据   :0a0001aa7k/002014110119/dsj/dwcssjk/{"order_no":"160630163409C63112FF4E96CA1FF7486AE0EB2B2B","result":"522.43,214.5,6.174,1.3027,NULL,49.97,0.983,00","status":"200","msg":"任务执行完成"}&sign=099AAB1CDEF7CC07521E3FDCB3C54AAA 
				flag=4;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("flag="+flag);
		return flag;
	}
	
	public static String getTime(){
		Time t = new Time();
		t.setToNow();
		String timeNow = String.valueOf(t.year) + "年"
				+ toDoubleDate(t.month + 1) + "月" + toDoubleDate(t.monthDay) + "日"
				+ toDoubleDate(t.hour) + "时" + toDoubleDate(t.minute) + "分" + toDoubleDate(t.second) + "秒"; // month是从0开始计算的
		return timeNow;
	}
	
	/**
	 * 将单个字符的日期（时间） 转换成两个字符 如：1 -> 01
	 */
	private static String toDoubleDate(int d) {
		if (d < 10)
			return String.valueOf("0" + d);
		else
			return String.valueOf(d);
	}
	
	public static String getTime_(){//时间格式为xxxx-xx-xx-xx
		Time t = new Time();
		t.setToNow();
		String timeNow = String.valueOf(t.year) + "-"
				+  toDoubleDate(t.month + 1) + "-"
				+  toDoubleDate(t.monthDay) + " "
				+  toDoubleDate(t.hour) + ":"
				+  toDoubleDate(t.minute) + ":"
				+  toDoubleDate(t.second); // month是从0开始计算的
		return timeNow;
	}
	
	/**获取数据库设备，用设备号作为topic进行订阅*/
	public List<String> getDeviceTopic(){
		List<String> topiclist = new ArrayList<String>();
		List<MyDevice> devices = dbdevice.GetMyDevices();
		topiclist.add("managerecord");
		topiclist.add("select_form_back");
		if(!devices.isEmpty() && devices.size()!=0){
			for(int i=0;i<devices.size();i++){
				System.out.println("---------------devices.get(i)"+devices.get(i).getMac());
				switch(devices.get(i).getType()){
				case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET://zigbee插座
					topiclist.add("0a0001a820/"+devices.get(i).getMac().toString());
					break;
				case MyDevice.DEVICE_ZIGBEE_SOCKET_FOUR://zigbee四路灯控
					topiclist.add("0a0001a830/"+devices.get(i).getMac().toString());
					break;
				case MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY://zigbee网关
					topiclist.add("0a0003ahup/"+devices.get(i).getMac().toString());
					break;
				case MyDevice.DEVICE_ZIGBEE_SMART_AIR://zigbee空调控制器
					topiclist.add("0a0001aa7k/"+devices.get(i).getMac().toString());
					break;
				case MyDevice.DEVICE_CENTER_SMART_AIR://中央空调控制器
					topiclist.add("0a0001a4r5/"+devices.get(i).getMac().toString());
					break;
					default:
						topiclist.add("/"+devices.get(i).getMac().toString());
						break;
				}
				System.out.println("topiclist===="+topiclist.get(i));
			}
		}else{
			topiclist = null;
		}
		return topiclist;
	}
	/**收到登录包数据时更新设备在线状态,on/off*/
	private static void flushLonginMsg(String mac_add){
		List<MyDeviceData> devices = dbdate.GetMyDeviceData();
		MyDeviceData device = null;
		if(!devices.isEmpty()){
			for(int i=0;i<devices.size();i++){
				if(devices.get(i).getMac().equals(mac_add)){
					device = devices.get(i);
				}
			}
		}
		if(device!=null){//已经设备数据表，更新登录状态；
			MyDeviceData new_device = new MyDeviceData(mac_add,getTime(),
					"on", device.getZdn(), device.getDy(),
					device.getDl(), device.getGl(), device.getWg(),
					device.getPl(), device.getGlys(), device.getThzzt());
			dbdate.Update(new_device);
		}else{//不存在设备数据表，插入登录状态；
			MyDeviceData new_device = new MyDeviceData(mac_add,getTime(),"on", "0", "0","0", "0", "0","0", "0", "跳闸");
			dbdate.Insert(new_device);
		}
		
	}
	/**收到功率超限数据时更新跳合闸状态,跳闸或者合闸*/
	private static void flushThzMsg(String mac_add,String getThzzt){
		List<MyDeviceData> devices = dbdate.GetMyDeviceData();
		MyDeviceData device = null;
		if(!devices.isEmpty()){
			for(int i=0;i<devices.size();i++){
				if(devices.get(i).getMac().equals(mac_add)){
					device = devices.get(i);
				}
			}
		}
		if(device!=null){//已经设备数据表，更新登录状态；
			MyDeviceData new_device = new MyDeviceData(mac_add,getTime(),
					"on", device.getZdn(), device.getDy(),
					device.getDl(), device.getGl(), device.getWg(),
					device.getPl(), device.getGlys(), getThzzt );
			dbdate.Update(new_device);
		}else{//不存在设备数据表，插入登录状态；
			MyDeviceData new_device = new MyDeviceData(mac_add,getTime(),"on", "0", "0","0", "0", "0","0", "0", "跳闸");
			dbdate.Insert(new_device);
		}
		
	}
	/**中央空调645上报数据处理
	 * 3832ddef35743433333333a335343353366a5b343354b53333b4b73633b8aa33383333334c47464349
	 * 05 FF AA BC 02 41 01 00 00 00 00 70 02 01 00 20 03 37 28 01 00 21 82 00 00 81 84 03 00 85 77 00 05 00 00 00 19 14 13 10 16*/
	public static String getLastCenterReceive(String type,String receive){
		StringBuffer sb1=new StringBuffer();
		try{
			 receive = receive.substring(8,receive.length());//中央空调上报数据，前面的4个在字节为标示符bcaaff05截取不计算；
			 for(int i=0;i<receive.length()/2;i++){
				 String result1 = Integer.toHexString(Integer.valueOf(receive.substring(i*2,i*2+2),16)-51);//16进制数据转10进制，减去51，再转为16进制
				 if(result1.length()<2){
					 result1 = "0"+result1;
				 }
				 sb1.append(result1);
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb1.toString();
	}
	
	
    /**645上报数据处理*/
	public static String getLastReceice(String type,String receive){//16进制转10进制
		 StringBuffer sb=new StringBuffer();
		 StringBuffer sb1=new StringBuffer();
		 StringBuffer sb2=new StringBuffer();
		 if(receive.length()==64){
			 try{
				 receive = receive.substring(8,receive.length());
				 String add0 = receive.substring(0,24);
				 String add1 = receive.substring(30,receive.length()-16);
				 for(int i=0;i<add0.length()/2;i++){
					 String result0 = Integer.toHexString(Integer.valueOf(add0.substring(i*2,i*2+2),16)-51);
					 if(result0.length()<2){
						 result0 = "0"+result0;
					 }
					 sb.append(result0);
					 
				 }
				 for(int i=0;i<add1.length()/2;i++){
					 String result1 = Integer.toHexString(Integer.valueOf(add1.substring(i*2,i*2+2),16)-51);//16进制数据转10进制，减去51，再转为16进制
					 if(result1.length()<2){
						 result1 = "0"+result1;
					 }
					 sb1.append(result1);
				 }
				 if(type.equals("0a0001aa7k")||type == "0a0001aa7k" || type.equals("0a0001a4r5")){//分体空调控制器类型会上报温度信息，需要单独处理ffffff字节；
					 String add2 = receive.substring(24,28);
					 if(add2.equals("3232")){
						 sb2.append("FFFF");
					 }else{
						 for(int i=0;i<add2.length()/2;i++){
							 String result2 = Integer.toHexString(Integer.valueOf(add2.substring(i*2,i*2+2),16)-51);//16进制数据转10进制，减去51，再转为16进制
							 if(result2.length()<2){
								 result2 = "0"+result2;
							 }
							 sb2.append(result2);
						 }
					 }
				
				 }else{
					 sb2.append("FFFF");
				 }
				 
				 
			}catch(Exception e){
				e.printStackTrace();
			}
		 }else if(receive.length()==48){
			 try{
				 receive = receive.substring(8,receive.length());
				 String add0 = receive.substring(0,24);
				 String add1 = receive.substring(30,receive.length());
				 for(int i=0;i<add0.length()/2;i++){
					 String result0 = Integer.toHexString(Integer.valueOf(add0.substring(i*2,i*2+2),16)-51);
					 if(result0.length()<2){
						 result0 = "0"+result0;
					 }
					 sb.append(result0);
					 
				 }
				 for(int i=0;i<add1.length()/2;i++){
					 String result1 = Integer.toHexString(Integer.valueOf(add1.substring(i*2,i*2+2),16)-51);//16进制数据转10进制，减去51，再转为16进制
					 if(result1.length()<2){
						 result1 = "0"+result1;
					 }
					 sb1.append(result1);
				 }
				 if(type.equals("0a0001aa7k")||type == "0a0001aa7k" || type.equals("0a0001a4r5")){//分体空调控制器类型会上报温度信息，需要单独处理ffffff字节；
					 String add2 = receive.substring(24,28);
					 if(add2.equals("3232")){
						 sb2.append("FFFF");
					 }else{
						 for(int i=0;i<add2.length()/2;i++){
							 String result2 = Integer.toHexString(Integer.valueOf(add2.substring(i*2,i*2+2),16)-51);//16进制数据转10进制，减去51，再转为16进制
							 if(result2.length()<2){
								 result2 = "0"+result2;
							 }
							 sb2.append(result2);
						 }
					 }
				
				 }else{
					 sb2.append("FFFF");
				 }
				 
				 
			}catch(Exception e){
				e.printStackTrace();
			}
		 }
		 return sb.toString()+sb2+"FF"+sb1.toString();
	 }
	
	/**
	 * 
	 * 中央空调 分体空调等实时数据块存储
	 * selected/0a0001aa7k/002014110119/get_meter_current_blob/data*/
	private static boolean handle_meter_current_blob(String receive_msg){
		boolean flag = false;
		try{
			String[] result_list = receive_msg.split("/");
			String[] data_list = result_list[result_list.length-1].split("#");
			String result_type= receive_msg.split("/")[1];//类型
			String result_num = receive_msg.split("/")[2];//表号
			String result_data = receive_msg.split("/")[4];//数据域
			if(result_data.equals("null")|result_data.equals("error")){
				flag = false;
			}else{
				List<MyDeviceData> devicesData = dbdate.GetMyDeviceData();
				MyDeviceData device = null;
				if(!devicesData.isEmpty()){
					for(int i=0;i<devicesData.size();i++){
						if(devicesData.get(i).getMac().equals(result_num)){
							device = devicesData.get(i);
						}
					}
				}	
				switch(result_type){
//				2016-10-17 11:19:56#系统时间    0
//				000141.21#电能  1
//				138.2#功率  2
//				027.5#环境温度  3
//				00#开关状态 4
//				2000#温度及模式  5
//				83#风机档位  6
//				00012837#低风速时长  7 
//				00008245#中风速时长 8
//				00038540#高风速时长 9
//				050077.85#剩余金额 10 
//				800C00#状态字 11
//				2016-10-17 11:19:00上报时间
				/** selected/0a0001a4r5/150907000125/get_meter_current_blob/
				 * 2016-10-17 11:19:56#000141.21#138.2#027.5#00#2000#83#00012837#00008245#00038540#050077.85#800C00#2016-10-17 11:19:00
				 * zdn->zdn, dy->sdwdjms, dl->fsdw, gl->gl, wg->hjwd, pl->Low_speed_time/Low_speed_time/Low_speed_time , glys->ysje/ztz , thzzt ->kgzt*/
				case Type_Entity.Central_air_conditioning:
					String[] result = result_data.split("#");
					if(device!=null){						
						MyDeviceData myDeviceData = new MyDeviceData(result_num,getTime(),
								"on", result[1], result[5],result[6], result[2], result[3],
								result[7]+"/"+result[8]+"/"+result[9],
								result[10]+"/"+result[11], result[4]);
						dbdate.Update(myDeviceData);
					}else{
						MyDeviceData myDeviceData = new MyDeviceData(result_num,getTime(),
								"on", result[1], result[5],result[6], result[2], result[3],
								result[7]+"/"+result[8]+"/"+result[9],
								result[10]+"/"+result[11], result[4]);
						dbdate.Insert(myDeviceData);
					}
					break;
				}
				flag = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**抄表，控制设备时返回数据处理
	 * 0a0001a820/160127001285/yckz/tz/{"order_no":"160413164731094DBBF8774398E53A168C121F50C6","result":-
	4,"status":"300","msg":"645 get data false"}&sign=9FED5EAA2CA33AFA6B8EE399AA5C92A1*/
	public static String[] handleReadControlData(String receive_msg){
		String[] getreceive = null;
		final String status = OrderManager.getInstance().getItemByOrder(receive_msg,
				"status", -1);
		switch(receive_msg.split("/")[2]){
		case "dsj":
			switch(receive_msg.split("/")[3]){
			case "dwcssjk":
				final String msg = OrderManager.getInstance().getItemByOrder(receive_msg,
						"result", -1);
				if(!msg.equals("") & status.equals("200")){
					edit.putString(receive_msg.split("/")[1],"200");
					edit.commit();
					System.out.println("运行到这里*********");
							try{
								List<MyDeviceData> devicesData = dbdate.GetMyDeviceData();
								MyDeviceData device = null;
								if(!devicesData.isEmpty()){
									for(int i=0;i<devicesData.size();i++){
										if(devicesData.get(i).getMac().equals(receive_msg.split("/")[1])){
											device = devicesData.get(i);
										}
									}
								}	
								
								float gl = Float.parseFloat(msg.split(",")[3])*1000;
								DecimalFormat df = new DecimalFormat("#.00");
								
								if(device!=null){
									String wg = null;
									if(receive_msg.split("/")[0].equals("0a0001aa7k")){
										 wg = device.getWg();			
									}else{
										 wg = msg.split(",")[4];
									}
									
									MyDeviceData myDeviceData = new MyDeviceData(receive_msg.split("/")[1],getTime(),
											"on", msg.split(",")[0], msg.split(",")[1],
											msg.split(",")[2], df.format(gl) , wg,
											msg.split(",")[5], msg.split(",")[6], msg.split(",")[7]);
									dbdate.Update(myDeviceData);
								}else{
									MyDeviceData myDeviceData = new MyDeviceData(receive_msg.split("/")[1],getTime(),
											"on", msg.split(",")[0], msg.split(",")[1],
											msg.split(",")[2], df.format(gl) , "0",
											msg.split(",")[5], msg.split(",")[6], msg.split(",")[7]);
									dbdate.Insert(myDeviceData);
								}
			
							}catch(Exception e){
								e.printStackTrace();
								edit.putString(receive_msg.split("/")[1],"300");
								edit.commit();
								getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/FAIL"};
							}
						getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/SUCCESS"};
					}else{
						edit.putString(receive_msg.split("/")[1],"100");
						edit.commit();
						getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/FAIL"};
					}				
				break;
			case "hjwd":
				
				break;
			case "rq":
				break;
			case "sj":
				break;
			case "rcb":
				break;
				
			}
			
			break;
		case "yckz":
			if(receive_msg.split("/")[3].equals("hz")){
				switch(status){
				case "200":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL_SUCCESS_HZ"};
					edit.putString(receive_msg.split("/")[1],"200");
					edit.commit();
					break;
				case "300":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/tz/执行失败!"};
					edit.putString(receive_msg.split("/")[1],"300");
					edit.commit();
					break;
				case "250":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/tz/执行中..."};
					edit.putString(receive_msg.split("/")[1],"250");
					edit.commit();
					break;
				case "400":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/tz/执行超时!"};
					edit.putString(receive_msg.split("/")[1],"400");
					edit.commit();
					break;
				case "350":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/tz/服务系统拒绝执行指令!"};
					edit.putString(receive_msg.split("/")[1],"350");
					edit.commit();
					break;
				case"100":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/tz/连接超时!"};
					edit.putString(receive_msg.split("/")[1],"100");
					edit.commit();
					break;
				}
			}else if(receive_msg.split("/")[3].equals("tz")){
				switch(status){
				case "200":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL_SUCCESS_TZ"};
					edit.putString(receive_msg.split("/")[1],"200");
					edit.commit();
					break;
				case "300":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/hz/执行失败!"};
					edit.putString(receive_msg.split("/")[1],"300");
					edit.commit();
					break;
				case "250":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/hz/执行中..."};
					edit.putString(receive_msg.split("/")[1],"250");
					edit.commit();
					break;
				case "400":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/hz/执行超时!"};
					edit.putString(receive_msg.split("/")[1],"400");
					edit.commit();
					break;
				case "350":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/hz/服务系统拒绝执行指令!"};
					edit.putString(receive_msg.split("/")[1],"350");
					edit.commit();
					break;
				case"100":
					getreceive = new String[]{"NOTIFY_ZIGBEE",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/CONTROL/hz/连接超时!"};
					edit.putString(receive_msg.split("/")[1],"100");
					edit.commit();
					break;
				}
			}else if(receive_msg.split("/")[3].equals("hwfs")){//红外发送
				switch(status){
				case "200":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS_SUCCESS"};
					edit.putString(receive_msg.split("/")[1],"200");
					edit.commit();
					break;
				case "300":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS/执行失败!"};
					edit.putString(receive_msg.split("/")[1],"300");
					edit.commit();
					break;
				case "250":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS/执行中..."};
					edit.putString(receive_msg.split("/")[1],"250");
					edit.commit();
					break;
				case "400":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS/执行超时!"};
					edit.putString(receive_msg.split("/")[1],"400");
					edit.commit();
					break;
				case "350":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS/服务系统拒绝执行指令!"};
					edit.putString(receive_msg.split("/")[1],"350");
					edit.commit();
					break;
				case"100":
					getreceive = new String[]{"NOTIFY_AIR",receive_msg.split("/")[0]+"/"+receive_msg.split("/")[1]+"/HWFS/连接超时!"};
					edit.putString(receive_msg.split("/")[1],"100");
					edit.commit();
					break;
				}
			}
			break;
			
		default:
			break;
		}
		return getreceive;
	}
	
}
