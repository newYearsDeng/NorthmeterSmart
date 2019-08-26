package com.northmeter.northmetersmart.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.adp.Machine_ListviewAdp;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.camera.activity.CaptureActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.db.DBDevice;
import com.northmeter.northmetersmart.db.DBDeviceData;
import com.northmeter.northmetersmart.db.DBDiagramData;
import com.northmeter.northmetersmart.device.MyDevice;
import com.northmeter.northmetersmart.device.MyDeviceData;
import com.northmeter.northmetersmart.helper.CustomQRCodeDialog;
import com.northmeter.northmetersmart.model.Machine_LvModel;
import com.northmeter.northmetersmart.mqtt.PushService;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.order.Type_Entity;

public class MachineAty extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	List<Machine_LvModel> models;
	Machine_ListviewAdp listViewAdp;
	private ListView listview_val;

	private List<MyDevice> devices;
	private List<MyDeviceData> devicesData;

	// 一次最大导出设备数量
	private int MAX_OUTPUT = 5;

	// scan
	private final static int SCANNIN_GREQUEST_CODE = 1;
	public static String str_scan_result;

	// 导入时，扫描后得到的设备信息
	private List<MyDevice> devicesToImport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.machine_aty);
		findViewById(R.id.button1).setOnClickListener(this);
		//findViewById(R.id.btn_1).setOnClickListener(this);
		findViewById(R.id.btn_2).setOnClickListener(this);
		// findViewById(R.id.btn_3).setOnClickListener(this);
		// findViewById(R.id.btn_4).setOnClickListener(this);

		/*
		 * huzenan
		 */
		findViewById(R.id.btn_3).setOnClickListener(this); // 导入
		findViewById(R.id.btn_4).setOnClickListener(this); // 导出
		str_scan_result = null;
		devicesToImport = new ArrayList<MyDevice>();
		/*
		 * end huzenan
		 */

		findViewById(R.id.btn_5).setOnClickListener(this);
		listview_val = (ListView) findViewById(R.id.listview_val);

		// 通过数据库初始化所有的设备
		DBDevice db_d = new DBDevice(this);
		DBDeviceData db_dd = new DBDeviceData(this);
		devices = db_d.GetMyDevices();
		devicesData = db_dd.GetMyDeviceData();

		models = new ArrayList<Machine_LvModel>();
		for (int i = 0; i < devices.size(); i++) {

			Machine_LvModel model = new Machine_LvModel();

			MyDevice md = devices.get(i);

			// MyDeviceData mdd = db_dd.Query(md.getMac());
			// ==================================================
			MyDeviceData mdd = null;
			if (!devicesData.isEmpty()) {
				for (int j = 0; j < devicesData.size(); j++) {
					
					if (devicesData.get(j).getMac().equals(md.getMac())) {
						mdd = devicesData.get(j);
						break;
					}
				}
			}
			// ==================================================

			model.setId(md.getMac());
			model.setIscheck("false");
			model.setTitle(md.getName());
			model.setContact(md.getContact());
			
			switch (md.getType()) {
			case MyDevice.DEVICE_WIFI_SMART_SOCKET:
				model.setType("插座");
				break;
			case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET:
				model.setType("Zigbee插座");
				break;
			case MyDevice.DEVICE_WIFI_SMART_GATEWAY:
				model.setType("网关");
				break;
			case MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY:
				model.setType("网关");
				break;
			case MyDevice.DEVICE_ZIGBEE_SMART_AIR:
				model.setType("分体空调控制器");
				break;
			case MyDevice.DEVICE_WIFI_SMART_AIR:
				model.setType("空调控制器");
				break;
			case MyDevice.DEVICE_Translation_GATEWAY:
				model.setType("无线智能网关");
				break;
			case MyDevice.DEVICE_CENTER_SMART_AIR:
				model.setType("中央空调控制器");
				break;
			default:
				break;
			}

			if (mdd != null) {
				if (mdd.getStateLastTime().equals("on"))
					model.setStatus(R.drawable.green);
				else
					model.setStatus(R.drawable.red);
			} else
					model.setStatus(R.drawable.red);

			models.add(model);
		}

		listViewAdp = new Machine_ListviewAdp(getApplicationContext(), models);
		listview_val.setAdapter(listViewAdp);
		listview_val.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Machine_LvModel model = models.get(arg2);
		if (model.getIscheck().equals("false"))
			model.setIscheck("true");
		else
			model.setIscheck("false");

		// 更新 listview 的视图
		listViewAdp.notifyDataSetChanged();

		System.out.println("click!!!!!!!!!!! name = " + model.getTitle()
				+ "  index = " + arg2 + "  isCheck = " + model.getIscheck());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1: // 返回
			finish();
			break;

//		case R.id.btn_1: // 全选
//			for (int i = 0; i < models.size(); i++) {
//				Machine_LvModel model = models.get(i);
//				if (model.getIscheck().equals("false"))
//					model.setIscheck("true");
//			}
//			listViewAdp.notifyDataSetChanged();
//			break;

		case R.id.btn_2: // 反选
			for (int i = 0; i < models.size(); i++) {
				Machine_LvModel model = models.get(i);
				if (model.getIscheck().equals("true"))
					model.setIscheck("false");
				else
					model.setIscheck("true");
			}
			listViewAdp.notifyDataSetChanged();
			break;

		// 导入：读取二维码，生成设备信息，存入数据库 MyDevices表中
		case R.id.btn_3: 
			try{
				// 开始扫描二维码，得到的信息会存储在 str_scan_result
				Intent intent = new Intent();
				intent.setClass(this, CaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}catch(Exception e){
				e.printStackTrace();}
					break;

		// 导出：根据 MyDevices表所列出的属性，获取1~5个设备的信息，组成字符串，生成二维码
		case R.id.btn_4:
			// 根据所选择的设备(1~5个)，生成二维码所需的字符串
			String codeString = "";
			int totalSelected = 0;

			for (int i = 0; i < models.size(); i++) {
				Machine_LvModel model = models.get(i);
				DBDevice db_d = new DBDevice(this);
				List<MyDevice> devices = db_d.GetMyDevices();

				// 若该设备被选中
				if (model.getIscheck().equals("true")) {
					totalSelected++;

					// 在数据库中找到该设备
					String selectMac = model.getId();
					if (!devices.isEmpty()) {
						for (int j = 0; j < devices.size(); j++) {
							MyDevice md = devices.get(j);

							// 找到对应设备
							if (selectMac.equals(md.getMac()))
								codeString = codeString
										+ md.createDeviceInformation();
						}
					}
				} 
			}
			codeString = codeString ; // 最后在结尾标记多一个分号;

			// 若选择了1~5个设备
			if (totalSelected > 0) {
				if (totalSelected <= MAX_OUTPUT) {
					// 根据生成的字符串生成二维码
					Bitmap qrcode = generateQRCode(codeString);

					// 弹出二维码对话框
					CustomQRCodeDialog QRCodeDialog = CustomQRCodeDialog
							.createDialog(this, qrcode);
					QRCodeDialog.show();
				} else
					Toast.makeText(getApplicationContext(), "请选择5个以内的设备进行导出",
							Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(getApplicationContext(), "请至少选择一个设备",
						Toast.LENGTH_SHORT).show();

			break;
		/*
		 * end huzenan
		 */

		case R.id.btn_5: // 删除
			try{
			Toast.makeText(getApplicationContext(),
					((Button) v).getText() + "", Toast.LENGTH_SHORT).show();

			// 需要删除的设备的集合，不能在找到设备的时候就删除
			// 因为 List 在执行 delete 后会将所有元素往前移动一位
			final List<Machine_LvModel> modelsToDelete = new ArrayList<Machine_LvModel>();
			for (int i = 0; i < models.size(); i++){

				System.out.println("开始设备的删除 数量=" + models.size());

				Machine_LvModel model = models.get(i);
				final Machine_LvModel model_delete = models.get(i);
				System.out.println("准备删除设备 mac=" + model.getId()
						+ " isChecked=" + model.getIscheck());

				if (model.getIscheck().equals("true")) {

					System.out.println("开始删除设备 mac=" + model.getId()
							+ " isChecked=" + model.getIscheck());

					DBDevice db_d = new DBDevice(this);
					DBDeviceData db_dd = new DBDeviceData(this);
					DBDiagramData db_diagramd = new DBDiagramData(this);
					db_d.Delete(model.getId()); //id即mac地址
					db_dd.Delete(model.getId());
					db_diagramd.Delete(model.getId());
					
					// 添加一个需要删除的设备
					modelsToDelete.add(models.get(i));
					
				} else
					System.out.println("设备未被选中 mac=" + model.getId()
							+ " isChecked=" + model.getIscheck());
			}

			// 删除 models 中所有需要删除的设备
			for (int i = 0; i < modelsToDelete.size(); i++) {
				Machine_LvModel md1 = modelsToDelete.get(i);

				for (int j = 0; j < models.size(); j++) {
					Machine_LvModel md2 = models.get(j);

					// 每个设备的 id 即 mac 都是唯一的
					if (md1.getId().equals(md2.getId())) {
						models.remove(models.get(j));
						break;
					}
				}
			}
			// 更新 listview 的视图
			listViewAdp.notifyDataSetChanged();
//			/*删除网关内档案*/
//			new Thread(new Runnable() {
//				@Override
//				public void run(){
//					for (int i = 0; i < modelsToDelete.size(); i++) {
//						try{
//							Machine_LvModel md1 = modelsToDelete.get(i);
//							if(md1.getId().equals(md1.getContact())){
//								continue;
//							}
//							sendMsgByUserCommand(md1.getId(),OrderList.DELETE_ZIGBEE_RECORD,md1.getContact());
//						}catch(Exception e){
//							e.printStackTrace();
//							}
//						}
//					}
//			}).start();
			
			}catch(Exception e){
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

	// 矩阵转换为bitmap
	private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
		int w = matrix.getWidth();
		int h = matrix.getHeight();
		int[] rawData = new int[w * h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int color = Color.WHITE;
				if (matrix.get(i, j)) {
					color = Color.BLACK;
				}
				rawData[i + (j * w)] = color;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
		return bitmap;
	}

	// 生成二维码bitmap
	private Bitmap generateQRCode(String content) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
		    getWindowManager().getDefaultDisplay().getMetrics(dm);
		    int width = (int) (dm.widthPixels*0.6);    //得到宽度 
			
			Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            
			QRCodeWriter writer = new QRCodeWriter();
			// MultiFormatWriter writer = new MultiFormatWriter();
			BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
					width, width, hints);
			return bitMatrix2Bitmap(matrix);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 扫描二维码返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:

			// tv_result.setText("SCANNIN_GREQUEST_CODE");

			if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
				if (data != null) {
					if (data.hasExtra("result")) {

						// tv_result.setText("RESULT_FIRST_USER！");

						// Bundle bundle = data.getExtras();
						// 显示扫描到的内容

						// 得到扫描结果
						str_scan_result = data.getStringExtra("result");
						System.out.println("################### scan result = "
								+ str_scan_result);

						// 解析设备信息并存储到 devicesToImport 中（检测是否已存在设备）
						devicesToImport = MyDevice
								.getDevicesWithString(str_scan_result);
						if(devicesToImport==null||devicesToImport.equals(null)){
							Toast.makeText(this, "设备添加失败", Toast.LENGTH_SHORT).show();
							return;
						}

						// 将结果存储到数据库和 models中
						DBDevice db_d = new DBDevice(this);
						List<MyDevice> devices = db_d.GetMyDevices();
						boolean isExisted = true;
						int totalImput = 0;						
						for (int i = 0; i < devicesToImport.size(); i++) {
							MyDevice md = devicesToImport.get(i);

//							//检查是否存在网关
//							for(int k=0;k<devicesToImport.size();k++){
//								if(md.getContact()==md.getMac() | md.getContact().equals(md.getMac())){
//									isExisted = false;//二维码里面存在网关
//									
//								}
//							}
							//检查是否存在导入设备需要的网关
							for(int k=0;k<devicesToImport.size();k++){
								if(md.getContact()==devicesToImport.get(k).getMac() | md.getContact().equals(devicesToImport.get(k).getMac())){
									isExisted = false;//二维码里面存在网关
								}
							}
							/*当二维码里面不存在网关，检查数据库里面是否有网关*/
							if(isExisted==true){
								System.out.println("二维码里面不存在网关设备，检查数据库");
								for(int l=0;l<devices.size();l++){
									if(md.getContact()==devices.get(l).getMac() | md.getContact().equals(devices.get(l).getMac())){
										System.out.println("数据库里面存在网关");
										isExisted = false;
										}
									}		
								}
							/* ===============================================*/
							if(md.getContact()=="NONE"|md.getContact().equals("NONE")|md.getContact()=="null"|md.getContact().equals("null")){
								System.out.println("存在联系为空的设备"+md.getContact());
								isExisted = false;
							}
							/* ===============================================*/
							// 检测是否已存在该设备
							if (!devices.isEmpty()) {
								for (int j = 0; j < devices.size(); j++) {
									if (devices.get(j).getMac()
											.equals(md.getMac()))
										isExisted = true;
								}
							}
							/*===============================================*/
							
							// 若不存在该设备，则存储该设备信息，否则跳过该设备的处理阶段
							if (!isExisted) {

								db_d.Insert(md);/*保存数据库*/

								Machine_LvModel model = new Machine_LvModel();
								model.setId(md.getMac());
								model.setIscheck("false");
								model.setTitle(md.getName());
 
								switch (md.getType()) {
								case MyDevice.DEVICE_WIFI_SMART_SOCKET:
									model.setType("插座");
									break;
								case MyDevice.DEVICE_ZIGBEE_SMART_SOCKET:
									model.setType("Zigbee插座");
									break;
								case MyDevice.DEVICE_WIFI_SMART_GATEWAY:
									model.setType("网关");
									break;
								case MyDevice.DEVICE_ZIGBEE_SMART_GATEWAY:
									model.setType("网关");
									break;
								case MyDevice.DEVICE_ZIGBEE_SMART_AIR:
									model.setType("分体空调控制器");
									break;
								case MyDevice.DEVICE_WIFI_SMART_AIR:
									model.setType("空调控制器");
									break;
								case MyDevice.DEVICE_Translation_GATEWAY:
									model.setType("无线智能网关");
									break;
								case MyDevice.DEVICE_CENTER_SMART_AIR:
									model.setType("中央空调控制器");
									break;

								default:
									break;
								}

								model.setStatus(R.drawable.red);

								models.add(model);
								totalImput++;
							}

							isExisted = false;
						}

						// 更新 listview 的视图
						listViewAdp.notifyDataSetChanged();
						
						PushService.actionStop(getApplicationContext());
						/**启动mqtt后台监听*/
						PushService.actionStart(getApplicationContext());

						// 提示
						if (devicesToImport.size() - totalImput == 0) {
							Toast.makeText(getApplicationContext(),
									"本次扫描共导入 " + totalImput + " 个设备",
									Toast.LENGTH_SHORT).show();
						} else {
							int sameDevices = devicesToImport.size()
									- totalImput;
							Toast.makeText(
									getApplicationContext(),
									"本次扫描共导入 " + totalImput + " 个设备，有 "
											+ sameDevices + " 个设备未导入",
									Toast.LENGTH_SHORT).show();
						}

						// 清空
						devicesToImport.clear();
					}
				}
			}
		default:
			// str_scan_result = "default!";
		}
	}
	/*
	 * end huzenan
	 */
	
	/*网关内对应档案删除*/
	/*这里的mac是选中的设备的mac地址，同过这个mac地址查询需要删除设备的表号；如果选择需要删除的设备时网关，则跳过此方法*/
	public static String sendMsgByUserCommand(String mac,int order_type,String contact_mac){
		String rst_rce = null;
		String URL_PATH = URL_help.getInstance().getUrl_address();
		// =================================================
		// 发送命令并得到返回结果
		String rst_raw = OrderManager.getInstance().sendOrder(/*"01"+md.getTableNum(),删除表号只需要“01”+表号*/
				OrderList.getSendByDeviceType(
						Type_Entity.Gateway_type,
						contact_mac,order_type,"01"+mac),
						OrderList.USER_PASSWORD, URL_PATH, "utf-8");
		if(rst_raw==null){ 
			return null;
		}
		String rst_error = OrderManager.getInstance().getItemByOrder(rst_raw,
				"status", -1);
		if(!rst_error.equals("200")){
			return null;
		}

		if (rst_raw != null){
			// 解析返回的结果
			rst_rce = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
			if (rst_rce.equals("200")) {
				System.out.println("====================删除成功=== ");
			} else {
				System.out.println("====================删除失败=== ");
				}
				
		}
		return rst_rce;
	}
	
	/*表号反转*/
	private String reverseRst(String rst){
		//String newRst=rst.substring(2, rst.length()-2);
		String lastrst = "";
		for(int i=rst.length()/2;i>0;i--){
			lastrst=lastrst+rst.substring(i*2-2, i*2);
			
		}
		return lastrst;
	}
	
}
