package com.northmeter.northmetersmart.wxpay;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.northmeter.northmetersmart.R;
import com.northmeter.northmetersmart.base.BaseActivity;
import com.northmeter.northmetersmart.control.URL_help;
import com.northmeter.northmetersmart.helper.CustomProgressDialog;
import com.northmeter.northmetersmart.order.MyOrder;
import com.northmeter.northmetersmart.order.OrderList;
import com.northmeter.northmetersmart.order.OrderManager;
import com.northmeter.northmetersmart.utils.Utils;
import com.northmeter.northmetersmart.wxPresenter.I_WXRequestShow;
import com.northmeter.northmetersmart.wxPresenter.WXRequestHttp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 微信支付
 */
public class WXPayActivity extends BaseActivity implements OnClickListener,I_WXRequestShow {
	private Button submitButton;
	private Button confirmButton;
	private TextView textView;
	private EditText charge_number;//充值金额
	private StringBuffer sb;
	private Map<String,String> resultunifiedorder;
	private PayReq req;
	private IWXAPI msgApi;
	private String str_type,str_mac,str_name;
	private boolean charge_flag = false;//是否可进行充值
	private String chargeNum = null;
	private WXRequestHttp requestInterface;
	private CustomProgressDialog progressDialog;
	private SharedPreferences sp1;
	private String userID = "01",userName = "admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_activity_main);
        Intent intent = getIntent();

		msgApi = WXAPIFactory.createWXAPI(this, null);

        str_type = intent.getStringExtra("type");
        str_name = intent.getStringExtra("name");
        str_mac = intent.getStringExtra("mac");
        
        submitButton = (Button) findViewById(R.id.bt_submit_order);
        confirmButton = (Button) findViewById(R.id.bt_corfirm);
        textView = (TextView) findViewById(R.id.tv_prepay_id);
        charge_number = (EditText) findViewById(R.id.charge_number);
        charge_number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        submitButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        findViewById(R.id.img_wx_back).setOnClickListener(this);
        
        findViewById(R.id.but_charge_1).setOnClickListener(this);
        findViewById(R.id.but_charge_2).setOnClickListener(this);
        findViewById(R.id.but_charge_3).setOnClickListener(this);
        findViewById(R.id.but_charge_4).setOnClickListener(this);
        findViewById(R.id.but_charge_5).setOnClickListener(this);
        findViewById(R.id.but_charge_6).setOnClickListener(this);
        findViewById(R.id.textview_syje).setOnClickListener(this);
        
        sb = new StringBuffer();
        req = new PayReq();
        
        requestInterface = new WXRequestHttp(this);
        progressDialog = CustomProgressDialog.createDialog(this);
        getUserData();
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_submit_order://提交订单	
			 String urlString="https://api.mch.weixin.qq.com/pay/unifiedorder";
			 PrePayIdAsyncTask prePayIdAsyncTask=new PrePayIdAsyncTask();
			 prePayIdAsyncTask.execute(urlString);
			 break;
		case R.id.bt_corfirm://确认支付
			
			genPayReq();//生成签名参数
			sendPayReq();
			break;
		case R.id.img_wx_back:
			finish();
			break;
			
		case R.id.but_charge_1:
			charge_number.setText("10");
			break;
		case R.id.but_charge_2:
			charge_number.setText("20");
			break;
		case R.id.but_charge_3:
			charge_number.setText("50");
			break;
		case R.id.but_charge_4:
			charge_number.setText("100");
			break;
		case R.id.but_charge_5:
			charge_number.setText("200");
			break;
		case R.id.but_charge_6:
			charge_number.setText("500");
			break;
		case R.id.textview_syje:
			get_ChargeSYJE();
			break;
		default:
			break;
		}
	}
	/*
	 * 调起微信支付
	 */
	private void sendPayReq() {
		if (!msgApi.isWXAppInstalled()) {  
		    //提醒用户没有安装微信  
		    Toast.makeText(this, "请先安装微信", Toast.LENGTH_SHORT).show();  
		    return;  
		}
		if(charge_flag){
			msgApi.registerApp(Constants.APP_ID);
			msgApi.sendReq(req);
			Log.i(">>>>>", req.partnerId);
		}
		
	}
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		if (resultunifiedorder!=null) {
			req.prepayId = resultunifiedorder.get("prepay_id");
			req.packageValue = "prepay_id="+resultunifiedorder.get("prepay_id");//这里的package参数值必须是Sign=WXPay,否则IOS端调不起微信支付，
													//* (参数值是"prepay_id="+resultunifiedorder.get("prepay_id")的时候Android可以，IOS不可以)
			if(req.prepayId == null){
				charge_flag = false;
				Toast.makeText(WXPayActivity.this, "充值异常", Toast.LENGTH_SHORT).show();
				return;
			}
		}else{
			charge_flag = false;
			Toast.makeText(WXPayActivity.this, "充值异常", Toast.LENGTH_SHORT).show();
			return;
		}
		req.nonceStr = getNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));//微信开放平台审核通过的应用APPID
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));//随机字符串，不长于32位。推荐随机数生成算法
		signParams.add(new BasicNameValuePair("package", req.packageValue));//暂填写固定值Sign=WXPay
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));//微信支付分配的商户号
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));//微信返回的支付交易会话ID
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));//时间戳

		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");

		textView.setText(sb.toString());

		Log.e("Simon", "----"+signParams.toString());

	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		Log.e("Simon","----"+appSign);
		return appSign;
	}
	
	/**把支付订单post到后台生成一个预支付订单，返回prepay_id（预支付回话标识）*/
	private class PrePayIdAsyncTask extends AsyncTask<String,Void, Map<String, String>>
	{	//第一个String代表输入到任务的参数类型，也即是doInBackground()的参数类型
		//第二个String代表处理过程中的参数类型，也就是doInBackground()执行过程中的产出参数类型，通过publishProgress()发消息
		//传递给onProgressUpdate()一般用来更新界面
		//第三个String代表任务结束的产出类型，也就是doInBackground()的返回值类型，和onPostExecute()的参数类型
		
		//private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//dialog = ProgressDialog.show(WXPayActivity.this, "提示", "正在提交订单");
			progressDialog.show();
		}
		@Override
		protected Map<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String,String> xml = null;
			if(get_ChargeNunm()){
				String url = String.format(params[0]);
				String entity = getProductArgs();
				Log.e("Simon",">>>>"+entity);
				byte[] buf=Util.httpPost(url, entity);
				
				if(buf!=null){
					String content = new String(buf);
					Log.e("orion", "----"+content);
					xml = decodeXml(content);
				}
			}			
			return xml;
		}
		
		@Override
		protected void onPostExecute(Map<String, String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		    /**
			 *  {sign=6E3E0D350BC38191186D887B66E2194C, return_code=SUCCESS, trade_type=APP, result_code=SUCCESS,
			 *  appid=wx14af47ab474a0991, mch_id=1415675302, nonce_str=OANSjSJ5aMl0GRd1, 
			 *  prepay_id=wx2017041111220719f38620b20773315727, return_msg=OK}
			 *  */
			//sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
			//textView.setText(sb.toString());
			if(result!=null){
				resultunifiedorder=result;
				genPayReq();//生成签名参数
				sendPayReq();//发起微信支付
			}else{
				Toast.makeText(WXPayActivity.this, "存在未处理订单", Toast.LENGTH_SHORT).show();  
			}
			
		}
	}
	
	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if("xml".equals(nodeName)==false){
						//实例化student对象
						xml.put(nodeName,parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("Simon","----"+e.toString());
		}
		return null;

	}
	
	/**生成预支付订单*/
	private String getProductArgs() {
		// TODO Auto-generated method stub
		StringBuffer xml=new StringBuffer();
		try {
			charge_flag = true;
			float money = Float.parseFloat(charge_number.getText().toString());
			String charge = ""+Math.round(money*100);
			String nonceStr = getNonceStr();
			xml.append("<xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("attach", str_mac+"/"+str_type+"/"+userName+"/"+userID+"/"+chargeNum));//附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
			packageParams.add(new BasicNameValuePair("body", "设备充值"));//商品描述
			packageParams.add(new BasicNameValuePair("device_info", "WEB"));//终端设备号(门店号或收银设备ID)，默认请传"WEB"
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));//商户号
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。推荐随机数生成算法													
			packageParams.add(new BasicNameValuePair("notify_url", "http://218.17.157.121:7055/Service1.asmx/SaveWXPayInfo"));//的服务器回调地址
			packageParams.add(new BasicNameValuePair("out_trade_no",genOutTradNo()));//商户订单号
			packageParams.add(new BasicNameValuePair("total_fee", charge));//一分钱
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));//支付类型
			
			String sign=getPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlString = toXml(packageParams);
			return xmlString;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	//生成订单号,测试用，在客户端生成
	private String genOutTradNo() {
		Random random = new Random();
		//return "dasgfsdg1234"; //订单号写死的话只能支付一次，第二次不能生成订单
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	//生成随机号，防重发
	private String getNonceStr() {
		// TODO Auto-generated method stub
		Random random=new Random();
		
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	/**
	 生成签名
	 */

	private String getPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("Simon",">>>>"+packageSign);
		return packageSign;
	}
	/*
	 * 转换成xml
	 */
	private String toXml(List<NameValuePair> params) {
		String xmlstr = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>");
			for (int i = 0; i < params.size(); i++) {
				sb.append("<"+params.get(i).getName()+">");
				
				sb.append(params.get(i).getValue());
				
				sb.append("</"+params.get(i).getName()+">");
			}
			sb.append("</xml>");
	
			Log.e("Simon",">>>>"+sb.toString());
			
			//如果参数里面存在中文字符，需要进行重新编码
			xmlstr =  new String(sb.toString().getBytes(), "ISO8859-1");
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return xmlstr;  
	}
	
	protected boolean get_ChargeNunm(){
		boolean flag = false;
		try{
			MyOrder odToSend = new MyOrder(OrderList.PARTNER, str_type, str_mac, "0DD1F70B5F3A0B95C22126872845114A", "dsj", "gdcs", "");
			// 发送命令并得到返回的结果
			String rst_raw = OrderManager.getInstance().sendOrder(odToSend,
					OrderList.USER_PASSWORD, URL_help.getInstance().getUrl_address(), "UTF-8");
			
			String status = OrderManager.getInstance().getItemByOrder(rst_raw,
						"status", -1);
			 
			if(status.equals("200")){
				String result = OrderManager.getInstance().getItemByOrder(rst_raw,
						"result", -1);
				flag = true;
				chargeNum = result;
				System.out.println("购电次数=========="+result);
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return flag;
	}
	
	protected void get_ChargeSYJE(){
		progressDialog.show();
		new Thread(){
			public void run(){
				MyOrder odToSend = new MyOrder(OrderList.PARTNER, str_type, str_mac, 
						"0DD1F70B5F3A0B95C22126872845114A", "dsj", "jesjk", "");
				
				String send_msg = Utils.sendOrder(odToSend);
				
				requestInterface.getHttpRequestPost_withWhatMsg(URL_help.getInstance().getUrl_address(), send_msg,0);
			}
		}.start();

	}
	
	/**查询用户账户信息*/
	private void getUserData(){
		try{
			sp1 = getSharedPreferences("Customer_Infor", MODE_PRIVATE);//存储用户登录的信息，包括存储微信unionid和手机号码
			String userData = sp1.getString("userData", null);
			
			if(userData==null){
				String flag = sp1.getString("flag", null);
				String cust = sp1.getString(flag, null);
				String para = flag;
				if(flag.equals("unionid")){
					para = "weixin";
				}
				requestInterface.getHttpRequestGet_withWhatMsg(URL_help.getInstance().getUser_address()+"/users/getUser", 
						"flag="+para+"&"+flag+"="+cust , 1);//http://218.17.157.121:4003/users/getUser
			}else{
				JSONArray json_array = JSONObject.parseArray(userData);
		        JSONObject msg_bject = (JSONObject) json_array.get(0);
		        userID = msg_bject.getString("userid");
		        userName = msg_bject.getString("username");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {//"result":"2645.31,0.00,44.64,89.28,0.00,1853.03,1853.03"
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String resule = (String) msg.obj;
			switch(msg.what){
			case 0:
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				if(resule.equals("exception")){
					Toast.makeText(WXPayActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				}else{
					String status = OrderManager.getInstance().getItemByOrder(resule,
							"status", -1);	
					String result = OrderManager.getInstance().getItemByOrder(resule,
							"result", -1);
					if(status.equals("200")){
						init_dialog_view(result);
					}else{
						Toast.makeText(WXPayActivity.this, result, Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case 1:
				if(!resule.equals("exception")){
					JSONArray json_array = JSONObject.parseArray(resule);
			        JSONObject msg_bject = (JSONObject) json_array.get(0);
			        userID = msg_bject.getString("userid");
			        userName = msg_bject.getString("username");
			        String tel = msg_bject.getString("tel");
			        String dptid = msg_bject.getString("dptid");
			        String departmentname = msg_bject.getString("departmentname");
			        String createtime = msg_bject.getString("createtime");
			        
			        Editor editor = sp1.edit();
					editor.putString("userData", resule);
					editor.commit();
				}
				break;
			}
			
			
			
		}
		
	};
	
	/**打开弹出时初始化数据视图*/
	private void init_dialog_view(String data){//"result":"2645.31,0.00,44.64,89.28,0.00,1853.03,1853.03"
		//累积用电量，本月累积用补助金额，本月累计用预付费金额，本月累积用电总量，补助电余额，预付费余额，总余额
		try{
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
			alertDialog.show();  
			Window window = alertDialog.getWindow();  
			window.setContentView(R.layout.dialog_wx_syje);  

			ListView listview_dialog = (ListView) window.findViewById(R.id.listview_dialog);
			TextView text_bzje = (TextView) window.findViewById(R.id.text_bzje);
			TextView text_ffje = (TextView) window.findViewById(R.id.text_ffje);
			TextView text_zsyje = (TextView) window.findViewById(R.id.text_zsyje);
			
			String[] dataList = data.split(",");
			text_bzje.setText(dataList[1]+"元");
			text_ffje.setText(dataList[2]+"元");
			text_zsyje.setText(dataList[dataList.length-1]+"元");
			
			window.findViewById(R.id.text_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
				}
			});
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

		
	@Override
	public void showWXRequestData(int what, String msg) {
		// TODO Auto-generated method stub
		Message message = handler.obtainMessage(what);
		 message.obj = msg;
        this.handler.sendMessage(message);
	}

}
