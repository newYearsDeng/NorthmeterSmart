package com.northmeter.northmetersmart.order;


public class OrderList {

	// 用户
	public static String PARTNER = "6ccpug";
	// 用户密码
	public static String USER_PASSWORD = "057100000153tt6k7y13tp36h7929nz0dubk94eq432pl23x6c68o45mkbyk4m61";
	// URL
	//public static String URL_PATH = "http://218.17.157.121:9123/Action";//"http://119.29.80.149:8816/Action";//"http://218.17.157.121:7001/Action";//"http://218.17.157.121:8816/Action";
									//"http://northmeter1.oicp.net:7001/Action";"http://218.17.157.121:7001/Action"
	/**
	 * 命令编号
	 */
	public static int ORDER_NULL = -1;
	public static int ORDER_SOCKET_READ = 0;//电能信息
	public static int ORDER_SOCKET_ON = 1;//插座表合闸
	public static int ORDER_SOCKET_OFF = 2;//插座表跳闸
	public static int ORDER_SOCKET_READ_DN = 3;//插座表读取电量信息
	public static int ORDER_SOCKET_CLEAR = 4; //电量清零
	
	public static int ORDER_SOCKET_SEND_DN_glxx=5; //功率下限
	public static int ORDER_SOCKET_SEND_DN_glsx=6; //功率上限
	public static int ORDER_SOCKET_SEND_DN_dlsx=7; //电量上限
	public static int ORDER_SOCKET_SEND_DN_tzyssj=8; //跳闸延时时间
	public static int ORDER_SOCKET_SEND_DN_hzyssj=9; //和闸延时时间
	
	public static int ORDER_SOCKET_RDJGS=10;//日冻结个数 
	public static int ORDER_SOCKET_RDJSJ=11;//日冻结数据
	
	public static int READ_SOCKET_RECORD=12;//读取以太网网关内档案
	public static int ADD_ZIGBEE_RECORD=13;//添加zigbee档案
	public static int DELETE_ZIGBEE_RECORD=14;//删除zigbee档案
	public static int READ_ZIGBEE_SHUJU=15;//读取zigbee插座信息
	
	public static int ADD_ZIGBEE_AIR_RECORD=16;//添加zigbee空调控制器档案
	public static int DELETE_ZIGBEE_AIR_RECORD=17;//删除zigbee空调控制器档案
	public static int READ_ZIGBEE_AIR_SHUJU=18;//读取zigbee空调控制器信息
	public static int SEND_ZIGBEE_AIR_HWM=19;//空调控制器发送红外
	public static int KEEP_ZIGBEE_AIR_HWM=20;//保存红外码
	public static int READ_ZIGBEE_AIR_HWM=21;//读取以存储的红外码
	public static int READ_ZIGBEE_TIME=22;//读时间
	public static int WRITE_ZIGBEE_TIME=23;//写时间
	public static int KEEP_ZIGBEE_WEEK_TASK=24;//保存定时任务
	public static int READ_ZIGBEE_WEEK_TASK=25;//读取定时任务
	public static int CLEAR_ZIGBEE_HWM=26;//空调控制器红外码清零
	public static int READ_ZIGBEE_AIR_RQXQ=27;//读取日期星期
	public static int WRITE_ZIGBEE_AIR_RQXQ=28;//校准日期星期
	public static int ZIGBEE_AIR_ON=29;//空调控制器合闸
	public static int ZIGBEE_AIR_OFF=30;//空调控制器跳闸
	public static int ZIGBEE_AIR_HJWD=31;//分体空调控制器环境温度
	
	public static int ADD_Translation_GATEWAY=32;//添加档案到无线智能网关
	public static int DELETE_Translation_GATEWAY=33;//删除无线智能网关 内档案
	public static int READ_Translation_GATEWAY=34;//读取无线智能网关内档案
	
	public static int SEND_CENTER_ON=35;//中央空调开
	public static int SEND_CENTER_OFF=36;//中央空调关
	public static int SEND_CENTER_SPEED=37;//中央空调风速调节
	public static int READ_CENTER_FSDW=38;//读取风速档位
	public static int READ_CENTER_KTKGXXZ=39;//读取空调开关信息字
	public static int READ_CENTER_WDJMS=40;//读取温度及模式
	public static int SEND_CENTER_WDJMS=41;//设定温度及模式
	public static int SEND_CENTER_HJWD=42;//读取环境温度
	
	public static int ORDER_SOCKET_ON_1 = 43;//插座表合闸
	public static int ORDER_SOCKET_OFF_1 = 44;//插座表跳闸
	public static int ORDER_SOCKET_ON_2 = 45;//插座表合闸
	public static int ORDER_SOCKET_OFF_2 = 46;//插座表跳闸
	public static int ORDER_SOCKET_ON_3 = 47;//插座表合闸
	public static int ORDER_SOCKET_OFF_3 = 48;//插座表跳闸
	public static int ORDER_SOCKET_ON_4 = 49;//插座表合闸
	public static int ORDER_SOCKET_OFF_4 = 50;//插座表跳闸
	
	public static int CSH_ZIGBEE_AIR_RCB = 51;//日程表初始化
	
	public static int READ_CENTER_WEEK_TASK = 52;//读取中央空调日程表
	public static int WRITE_CENTER_WEEK_TASK = 53;//写中央空调日程表
	public static int READ_CENTER_DNSJK = 54;//读取中央空调最新数据块
	
	
	public static int Control_Light = 55;//照明灯控控制
	public static int Air_Quality_Qetector_Read = 56;//空气质量检测仪读取上报数据快

	/**
	 * 
	 * @param deviceType
	 * @param orderType
	 * @return
	 * 智能插座
	 */
	public static MyOrder getOrderByDeviceType(String deviceType, String tableNum,
			int orderType) {
		MyOrder od = null;
		switch (deviceType) {
		// 智能插座
		case Type_Entity.Wifi_socket: {
			if (ORDER_SOCKET_READ == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "", "dsj",
						"dbcssjk", "");
			} else if (ORDER_SOCKET_ON == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"hz", "0F");
			} else if (ORDER_SOCKET_OFF == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"tz", "0F"); 
			} else if(ORDER_SOCKET_READ_DN==orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "", "dsj",
						"dnglsjk", "");
			}
			  else if(ORDER_SOCKET_CLEAR==orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "", "ql",
						"", "");
			}
			 else if(ORDER_SOCKET_RDJGS==orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "", "dsj",
					   "djgs", "");
				}
			else if(ORDER_SOCKET_RDJSJ==orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "", "dsj",
					   "djsj", "");
				} 
		}
			break;

		default:
			break;
		}

		return od;
	}
	
	/**
	 * 生成需要发送的命令：所有设备
	 * */
	public static MyOrder getSendByDeviceType(String deviceType, String tableNum,
			int orderType,String cmd_data) {
		MyOrder od = null;

		switch (deviceType) {
		// wifi智能插座
		case Type_Entity.Wifi_socket: {
			if ( ORDER_SOCKET_SEND_DN_glxx == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"glxx", cmd_data);
			} else if (ORDER_SOCKET_SEND_DN_glsx == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"glsx", cmd_data);
			} else if (ORDER_SOCKET_SEND_DN_dlsx == orderType) {
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"dlsx", cmd_data);
			} else if(ORDER_SOCKET_SEND_DN_tzyssj == orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"tzyssj", cmd_data);
			}else if(ORDER_SOCKET_SEND_DN_hzyssj == orderType){
				od = new MyOrder(PARTNER, "0a0003a8nq", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"hzyssj", cmd_data);
			}
			
		}
			break;
		//以太网转zigbee网关
		case Type_Entity.Gateway_type: {
			if ( READ_SOCKET_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"readrecord", "");
				}
			else if(DELETE_ZIGBEE_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
			}
			break;
		//wifi转zigbee网关 ：DEVICE_WIFI_SMART_GATEWAY
		case Type_Entity.Wifi_gateway:{
			if ( READ_SOCKET_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"readrecord", "");
				}
			else if(DELETE_ZIGBEE_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
			}

			break;
				
		//zigbee插座
		case Type_Entity.Socket_type: {
			if ( ADD_ZIGBEE_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"addrecord",cmd_data);
			}
			else if(DELETE_ZIGBEE_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
			else if (READ_ZIGBEE_SHUJU == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "dsj",
								"dwcssjk", "");
						}
			else if (ORDER_SOCKET_READ == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "dsj",
						"dbcssjk", "");
			} else if (ORDER_SOCKET_ON == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"hz", "0F");
			} else if (ORDER_SOCKET_OFF == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"tz", "0F");
			} 
			
			else if (ORDER_SOCKET_ON_1 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"hz", "1000");
			} else if (ORDER_SOCKET_OFF_1 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"tz", "1000");
			}
			else if (ORDER_SOCKET_ON_2 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"hz", "100");
			} else if (ORDER_SOCKET_OFF_2 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"tz", "100");
			}
			else if (ORDER_SOCKET_ON_3 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"hz", "10");
			} else if (ORDER_SOCKET_OFF_3 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"tz", "10");
			}
			else if (ORDER_SOCKET_ON_4 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"hz", "1");
			} else if (ORDER_SOCKET_OFF_4 == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
						"tz", "1");
			}
			
			else if(ORDER_SOCKET_READ_DN==orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "dsj",
						"dnglsjk", "");
			}
			  else if(ORDER_SOCKET_CLEAR==orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "ql",
						"", "");
			}
			 else if(ORDER_SOCKET_RDJGS==orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "dsj",
					   "djgs", "");
				}
			else if(ORDER_SOCKET_RDJSJ==orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "", "dsj",
					   "djsj", "");
				} 
			else if ( ORDER_SOCKET_SEND_DN_glxx == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"glxx", cmd_data);
			} else if (ORDER_SOCKET_SEND_DN_glsx == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"glsx", cmd_data);
			} else if (ORDER_SOCKET_SEND_DN_dlsx == orderType) {
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"dlsx", cmd_data);
			} else if(ORDER_SOCKET_SEND_DN_tzyssj == orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"tzyssj", cmd_data);
			}else if(ORDER_SOCKET_SEND_DN_hzyssj == orderType){
				od = new MyOrder(PARTNER, "0a0001a820", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"hzyssj", cmd_data);
			}
			
			
			}
			break;
			
			/**zigbee四路灯控面板*/
		case Type_Entity.Four_street_control:{
			if ( ADD_ZIGBEE_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"addrecord",cmd_data);
			}
			else if(DELETE_ZIGBEE_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
			else if (READ_ZIGBEE_SHUJU == orderType) {
				od = new MyOrder(PARTNER, "0a0001a830", tableNum, "", "dsj",
								"dwcssjk", "");
						}
			 else if (ORDER_SOCKET_ON == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
							"hz", "0F");//1111
				} else if (ORDER_SOCKET_OFF == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
							"tz", "0F");//1111
				} 
				else if (ORDER_SOCKET_ON_1 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"hz", "08");//1000
				} else if (ORDER_SOCKET_OFF_1 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"tz", "08");//1000
				}
				else if (ORDER_SOCKET_ON_2 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"hz", "04");//100
				} else if (ORDER_SOCKET_OFF_2 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"tz", "04");//100
				}
				else if (ORDER_SOCKET_ON_3 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"hz", "02");//10
				} else if (ORDER_SOCKET_OFF_3 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"tz", "02");//10
				}
				else if (ORDER_SOCKET_ON_4 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"hz", "01");//1
				} else if (ORDER_SOCKET_OFF_4 == orderType) {
					od = new MyOrder(PARTNER, "0a0001a830", tableNum, "935006fb43df8b37a7397c0ff272c6fa", "yckz",
							"tz", "01");//1
				}
			
			}
			break;
			//zigbee空调控制器
		case Type_Entity.Split_air_conditioning:{
			if ( ADD_ZIGBEE_AIR_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"addrecord",cmd_data);
			}
			else if(DELETE_ZIGBEE_AIR_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
		}
			break;
			//zigbee中央空调控制器
		case Type_Entity.Central_air_conditioning:{
			if ( ADD_ZIGBEE_AIR_RECORD == orderType) {
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "", "managerecord",
						"addrecord",cmd_data);
			}
			else if(DELETE_ZIGBEE_AIR_RECORD == orderType ){
				od = new MyOrder(PARTNER, "0a0003ahup", tableNum, "" ,"managerecord",
						"deleterecord",cmd_data);
					}
			else if (READ_ZIGBEE_AIR_SHUJU == orderType) {//读取中央空调电表信息
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
								"dwcssjk", "");
					}
			else if(READ_CENTER_KTKGXXZ == orderType){ //读取中央空调开关信息字
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
								"ktkgxxz",cmd_data);
			}
			else if(READ_CENTER_FSDW == orderType){ //读取中央空调风速档位
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
								"fsdw",cmd_data);
			}
			else if(READ_CENTER_WDJMS == orderType){ //读取温度及模式
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
								"sdwdjms",cmd_data);
			}
			else if(SEND_CENTER_HJWD == orderType){ //读取环境温度
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
						"hjwd",cmd_data);
			}
			else if(SEND_CENTER_WDJMS == orderType){ //设定温度及模式
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
								"wdjms",cmd_data);
			}
			/*发送中央空调控制指令*/
			else if(SEND_CENTER_ON == orderType){//开机
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
								"yckg","aa");
				}
			else if(SEND_CENTER_OFF == orderType){//关机
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
								"yckg","55");
				}
			/**设定风速档位*/
			else if(SEND_CENTER_SPEED == orderType){
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
								"fsdw",cmd_data);
				}
			else if(WRITE_CENTER_WEEK_TASK == orderType){ //写入中央空调日程表
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"rcb",cmd_data);
			}else if(READ_CENTER_WEEK_TASK == orderType){ //读取中央空调日程表
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
						"rcb",cmd_data);
			}
			else if(READ_CENTER_DNSJK == orderType){ //读取新的数据块
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "", "dsj",
						"dnsjk",cmd_data);
			}else if(CSH_ZIGBEE_AIR_RCB == orderType){ //日程表初始化
				od = new MyOrder(PARTNER, "0a0001a4r5", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"sdcscsh","00");
			}
			
			}
			break;	
		case Type_Entity.Light_control://照明灯控
			if(Control_Light == orderType){ //控制
				od = new MyOrder(PARTNER, Type_Entity.Light_control, tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"tg",cmd_data);
			}
			break;
			
		case Type_Entity.Air_Quality_Qetector://空气质量检测仪
			if(Air_Quality_Qetector_Read == orderType){ //控制
				od = new MyOrder(PARTNER, Type_Entity.Air_Quality_Qetector, tableNum, "0DD1F70B5F3A0B95C22126872845114A", "dsj",
						"zdsb2",cmd_data);
			}
			break;
					

		default:
			break;
		}

		return od;
	}
	
	/**
	 * 生成需要发送的命令：空调控制器
	 * */
	public static MyOrder Send_Zigbee_ByDeviceType(String deviceType, String tableNum,
			int orderType,String eqpt_pwd,String cmd_id,String cmd_data) {
		MyOrder od = null;

		switch (deviceType) {
			//zigbee空调控制器
		case Type_Entity.Split_air_conditioning:{			
			if (READ_ZIGBEE_AIR_SHUJU == orderType) {
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
								"dwcssjk", "");
					}
			/*发送空调红外码命令*/
			else if(SEND_ZIGBEE_AIR_HWM == orderType){
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
								"hwfs",cmd_data);
			}
			/*发送空调红外码命令存储*/
			else if(KEEP_ZIGBEE_AIR_HWM == orderType){
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"hwbm",cmd_data);
			}
			else if(ZIGBEE_AIR_HJWD == orderType){//读分体空调环境温度
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
						"hjwd",cmd_data);
			}
			else if(READ_ZIGBEE_AIR_HWM == orderType){//读特定存储红外码条数
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
						"hwbmts",cmd_data);
			}else if(READ_ZIGBEE_TIME == orderType){//读取时间 
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
						"time",cmd_data);
			}else if(WRITE_ZIGBEE_TIME == orderType){//校准时间
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"time",cmd_data);
			}else if(READ_ZIGBEE_AIR_RQXQ == orderType){//读取日期星期
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
						"rq",cmd_data);
			}else if(WRITE_ZIGBEE_AIR_RQXQ == orderType){//校准日期星期
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"rqs",cmd_data);
			}else if(KEEP_ZIGBEE_WEEK_TASK == orderType){ //写入日程表
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "xsj",
						"rcb",cmd_data);
			}else if(READ_ZIGBEE_WEEK_TASK == orderType){ //读取日程表
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "", "dsj",
						"rcb",cmd_data);
			}else if(CSH_ZIGBEE_AIR_RCB == orderType){ //日程表初始化
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"sdcscsh",cmd_data);
			}	
			else if(CLEAR_ZIGBEE_HWM == orderType){  //红外编码条数初始化
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"hwbmcsh",cmd_data);
			}
			else if(ZIGBEE_AIR_ON == orderType){ //空调控制器合闸
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"hz","");
			}
			else if(ZIGBEE_AIR_OFF == orderType){ //空调控制器跳闸
				od = new MyOrder(PARTNER, "0a0001aa7k", tableNum, "0DD1F70B5F3A0B95C22126872845114A", "yckz",
						"tz","");
			}
			
			}
			break;
			

		default:
			break;
		}

		return od;
	}
	
	
	
	
}
