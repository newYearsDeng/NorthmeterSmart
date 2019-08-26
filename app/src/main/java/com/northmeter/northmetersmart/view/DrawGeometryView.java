package com.northmeter.northmetersmart.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * 自定义的View,绘制几何图形,这里主要绘制中央空调的方波图
 * 
 * @author yinjie 2015年6月22日 下午6:31:55
 *
 */
@SuppressLint("DrawAllocation")
public class DrawGeometryView extends View {

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public DrawGeometryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 */
	public DrawGeometryView(Context context) {
		super(context);
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//
//		// 我们先定义三种不同风格的画笔
//		Paint redPaint = new Paint(); // 红色画笔
//		redPaint.setAntiAlias(true); // 抗锯齿效果,显得绘图平滑
//		redPaint.setColor(Color.RED); // 设置画笔颜色
//		redPaint.setStrokeWidth(15.0f);// 设置笔触宽度
//		redPaint.setStyle(Style.FILL);// 设置画笔的填充类型(完全填充)
//
//		// ================================================================
//		Paint greenPaint = new Paint(); // 绿色画笔
//		greenPaint.setAntiAlias(true); // 抗锯齿效果,显得绘图平滑
//		greenPaint.setColor(Color.GREEN); // 设置画笔颜色
//		greenPaint.setStrokeWidth(10.0f);// 设置笔触宽度
//		greenPaint.setStyle(Style.STROKE);// 设置画笔的填充类型(仅是描边)
//
//		// =================================================================
//		Paint bluePaint = new Paint(); // 蓝色画笔
//		bluePaint.setAntiAlias(true); // 抗锯齿效果,显得绘图平滑
//		bluePaint.setColor(Color.BLUE); // 设置画笔颜色
//		bluePaint.setStrokeWidth(5.0f);// 设置笔触宽度
//		bluePaint.setStyle(Style.FILL_AND_STROKE);// 设置画笔的填充类型(完全填充和描边)
//
//		// 1.绘制整个View背景颜色
//		canvas.drawColor(Color.WHITE);
//
//		// 2.绘制点(点比较小，可能看不清楚)
//		// 这里演示的是绘制单个点，绘制多个点使用canvas.drawPoints方法
//		canvas.drawPoint(10f, 10f, redPaint);
//		canvas.drawPoint(210f, 10f, greenPaint);
//		canvas.drawPoint(410f, 10f, bluePaint);
//
//		// 3.绘制线
//		canvas.drawLine(10f, 40f, 100f, 40f, redPaint);
//		canvas.drawLine(210f, 40f, 300f, 40f, greenPaint);
//		canvas.drawLine(410f, 40f, 500f, 40f, bluePaint);
//
//		// 4.绘制几何图形-矩形，RectF代表一个矩形的区域，构造的参数代表左上角和右下角的浮点型值坐标
//		canvas.drawRect(new RectF(10f, 60f, 100f, 150f), redPaint);
//		canvas.drawRect(new Rect(210, 60, 300, 150), greenPaint);
//		canvas.drawRect(410, 60, 500, 150, bluePaint);
//
//		// 5.绘制几何图形-圆角矩形
//		// 第二和第三个参数是圆角的半径，值越大圆角越明显
//		canvas.drawRoundRect(new RectF(10f, 180, 100f, 250), 2f, 2f, redPaint);
//		canvas.drawRoundRect(new RectF(210f, 180f, 300f, 250f), 7f, 7f,
//				greenPaint);
//		canvas.drawRoundRect(new RectF(410f, 180f, 500f, 250f), 15f, 15f,
//				bluePaint);
//
//		// 6.绘制几何图形-圆形,前面三个参数依次代表圆心的x,y坐标和圆的半径
//		canvas.drawCircle(50f, 310f, 45f, redPaint);
//		canvas.drawCircle(250f, 310f, 45f, greenPaint);
//		canvas.drawCircle(450f, 310f, 45f, bluePaint);
//
//		// 7.绘制几何图形-椭圆,第一个参数表示这个椭圆的外切矩形
//		canvas.drawOval(new RectF(10f, 410f, 140f, 500f), redPaint);
//		canvas.drawOval(new RectF(210f, 410f, 370f, 500f), greenPaint);
//		canvas.drawOval(new RectF(410f, 410f, 610f, 500f), bluePaint);
//
//		// 8.绘制几何图形-弧形
//		// 第一个参数表示包围这个弧形所需的最小的矩形,即弧的轮廓
//		// 第二个参数绊脚石其实的角度
//		// 第三个参数表示圆弧扫过的度数(弧的角度大小是其绝对值),则结束的角度等于开始的角度加上扫过的角度
//		// 第四个参数表示是否使用圆心连接弧的两端
//
//		canvas.drawArc(new RectF(10f, 510f, 150f, 650f), 0f, 90f, true,
//				redPaint);
//		canvas.drawArc(new RectF(210f, 610f, 350f, 750f), -10f, -100f, false,
//				greenPaint);
//		canvas.drawArc(new RectF(400f, 590f, 530f, 700f), 10f, -80f, true,
//				bluePaint);
//
//		// 9.绘制几何图形-不规则几何图形
//		// 不规则图形没有直接提供方法，我们可以间接定义一些路径(Path),让后把这些路径连起来就可以绘制不规则图形了
//		// Path还提供了一系列的添加一些几何图形作为其路径的add...()方法
//		Path path = new Path();
//		path.moveTo(10, 670); // path起点
//		path.lineTo(100, 700);
//		path.lineTo(150, 800);
//		path.close();
//		canvas.drawPath(path, redPaint);
//	}
	
	
	@Override  
	protected void onDraw(Canvas canvas) {  
	    super.onDraw(canvas);  
	    //设置绘制模式为-虚线作为背景线。  
	    PathEffect effect = new DashPathEffect(new float[] { 6, 6, 6, 6, 6}, 2);  
	    //背景虚线路径.  
	    Path path = new Path(); 
	    //只是绘制的XY轴
	    Paint linePaint = new Paint();
	    linePaint.setStyle(Style.STROKE);  
	    linePaint.setStrokeWidth((float)0.7);  
	    linePaint.setColor(Color.BLACK);  
	    linePaint.setAntiAlias(true);// 锯齿不显示  
	    //XY刻度上的字
	    Paint textPaint = new Paint();
	    textPaint.setStyle(Style.FILL);// 设置非填充  
	    textPaint.setStrokeWidth(1);// 笔宽5像素  
	    textPaint.setColor(Color.BLACK);// 设置为蓝笔  
	    textPaint.setAntiAlias(true);// 锯齿不显示  
	    textPaint.setTextAlign(Align.CENTER);  
	    textPaint.setTextSize(15); 
	    //绘制XY轴上的字：Y开关状态、X时间
	    Paint xyChartPaint = new Paint();
	    xyChartPaint.setStyle(Style.FILL);  
	    xyChartPaint.setStrokeWidth(1);  
	    xyChartPaint.setColor(Color.BLUE);  
	    xyChartPaint.setAntiAlias(true);  
	    xyChartPaint.setTextAlign(Align.CENTER);  
	    xyChartPaint.setTextSize(18);   
	    //绘制的折线
	    Paint chartLinePaint = new Paint();
	    chartLinePaint.setStyle(Style.FILL);  
	    chartLinePaint.setStrokeWidth(3);  
	    chartLinePaint.setColor(Color.RED);//(1)黄色  
	    
	    float[] dateY = new float[]{0,1,1,0,1,1};
	    String[] dateX = new String[]{"10:06","10:07","10:08","10:09","10:10","10:11"};
	    chartLinePaint.setAntiAlias(true);  

	    //基准点。  
	    float gridX = 30+10;  
	    float gridY = getHeight() - 50;  
	    //XY间隔。  
	    float xSpace = (float) ((getWidth()-60)/6-1.5);  
	    //画Y轴(带箭头)。  
	    canvas.drawLine(gridX, gridY-20-10, gridX, 30+10, linePaint);  
	    canvas.drawLine(gridX, 30+10, gridX-6, 30+14+10, linePaint);//Y轴箭头。  
	    canvas.drawLine(gridX, 30+10, gridX+6, 30+14+10, linePaint);  
	    //画Y轴名字。
	    //由于是竖直显示的，先以原点顺时针旋转90度后为新的坐标系
	    canvas.rotate(-90);
	    //当xyChartPaint的setTextAlign（）设置为center时第二、三个参数代表这四个字中点所在的xy坐标
	    canvas.drawText("开关状态", -((float)(getHeight()-60)-15-5 - 1/((float)1.6*1) * (getHeight()-60)/2), gridX-15, xyChartPaint);  
	    canvas.rotate(90); //改变了坐标系还要再改过来 
	    float y = 0;
	    //画X轴。  
	    y = gridY-20;  
	    canvas.drawLine(gridX, y-10, getWidth(), y-10, linePaint);//X轴.  
	    canvas.drawLine(getWidth(), y-10, getWidth()-14, y-6-10, linePaint);//X轴箭头。  
	    canvas.drawLine(getWidth(), y-10, getWidth()-14, y+6-10, linePaint);  
	    //画背景虚线，一条(因为除去了X轴)，画Y轴刻度
	    y = (float)(getHeight()-60)-15-5 - 1/((float)1.6*1) * (getHeight()-60);//虚线的Y，开关是开的时候的Y。  
	    linePaint.setPathEffect(effect);//设法虚线间隔样式。  
	    //画除X轴之外的------背景虚线一条-------      
	    path.moveTo(gridX, y);//背景【虚线起点】。  
	    path.lineTo(getWidth(), y);//背景【虚线终点】。  
	    canvas.drawPath(path, linePaint);     
	    //画Y轴刻度。  
	    canvas.drawText("关", gridX-6-7, gridY-20, textPaint);
	    canvas.drawText("开", gridX-6-7, y+10, textPaint);  
	      
	    //绘制X刻度坐标。  
	    float x = 0;  
	    if(dateX[0] != null) { //用X来判断，就是用来如果刚开始的点数少于7个则从左到右递增，从而没有了刚开始的几个虚点；（因为X和Y的数组初始化时都没赋值，所以刚开始的时候用这个就可以判断数组中到底几个点） 
	        for(int n = 0; n < dateX.length; n++) {  
	            //取X刻度坐标.  
	            x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。  
	            //画X轴具体刻度值。  
	            if(dateX[n] != null) {        
	canvas.drawLine(x, gridY-30, x, gridY-18, linePaint);//短X刻度。  
	                canvas.drawText(dateX[n], x, gridY+5, textPaint);//X具体刻度值。
	            }  
	        }  
	    }  
	      
	    //起始点。  

	    float lastPointX = 0; //前一个点 
	    float lastPointY = 0;  
	    float currentPointX = 0;//当前点
	    float currentPointY = 0;  
	    if(dateY != null) {     
	        //1.绘制折线。  
	        for(int n = 0; n < dateY.length; n++) {  
	            //get current point  
	            currentPointX =  n * xSpace + gridX;  
	            currentPointY =  (float)(getHeight()-60)-15-5 - (float)dateY[n]/((float)1.6*1) * (getHeight()-60);  
	            if(dateX[n] != null){//用X来判断，就是用来如果刚开始的点数少于7个则从左到右递增，从而没有了刚开始的几个虚点；（因为X和Y的数组初始化时都没赋值，所以刚开始的时候用这个就可以判断数组中到底几个点）
	           if(n>0){//从第二个点开始判断
	           if(dateY[n-1]==dateY[n]){//如果相邻两个点相等一直画直线
	           //draw line                                                                             
	                   canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);//第一条线[蓝色]      
	           }
	           else{//如果相邻间的点不相等证明是从开到关或者关到开的状态，要画竖线
	           //draw line  横线                                                                           
	                   canvas.drawLine(lastPointX, lastPointY, currentPointX, lastPointY, chartLinePaint);//第一条线[蓝色]   
	                 //draw line  竖线                                                                           
	                   canvas.drawLine(currentPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);//第一条线[蓝色]     
	           }
	           }
	            }
	         
	            lastPointX = currentPointX;  
	            lastPointY = currentPointY;  
	        }  
	    } 
	  //画X轴名字。  
	    canvas.drawText("时间", getWidth()/2-10, getHeight()-18, xyChartPaint);
	} 

}

