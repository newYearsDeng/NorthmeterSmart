package com.northmeter.northmetersmart.view;
/**
 * 
 */

import com.github.mikephil.charting.utils.ColorTemplate;

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
 * 自定义的View,cancas绘制中央空调方波图
 * 
 * @author 
 *
 */
@SuppressLint("DrawAllocation")
public class DrawGeometryView_1 extends View {
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	private float[] DateY;
	private String[] DateX;
	private int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
	
	public DrawGeometryView_1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 */
	public DrawGeometryView_1(Context context,float[] dateY,String[] dateX) {
		super(context);
		this.DateY = dateY;
		this.DateX = dateX;
	}


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
	    linePaint.setColor(mColors[3]);  
	    linePaint.setAntiAlias(true);// 锯齿不显示  
	    //XY刻度上的字
	    Paint textPaint = new Paint();
	    textPaint.setStyle(Style.FILL);// 设置非填充  
	    textPaint.setStrokeWidth(1);// 笔宽5像素  
	    textPaint.setColor(mColors[3]);// 设置为蓝笔  
	    textPaint.setAntiAlias(true);// 锯齿不显示  
	    textPaint.setTextAlign(Align.CENTER);  
	    textPaint.setTextSize(15f); 
	    //绘制XY轴上的字：Y开关状态、X时间
	    Paint xyChartPaint = new Paint();
	    xyChartPaint.setStyle(Style.FILL);  
	    xyChartPaint.setStrokeWidth(1);  
	    xyChartPaint.setColor(mColors[3]);  
	    xyChartPaint.setAntiAlias(true);  
	    xyChartPaint.setTextAlign(Align.CENTER);  
	    xyChartPaint.setTextSize(27f);   
	    //绘制的折线
	    Paint chartLinePaint = new Paint();
	    chartLinePaint.setStyle(Style.FILL);  
	    chartLinePaint.setStrokeWidth(3);  
	    chartLinePaint.setColor(mColors[1]);//(1)黄色  
	    
	    float[] dateY = DateY;//new float[]{0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1};
	    String[] dateX = DateX;
//	    		new String[]{"10:06","10:07","10:08","10:09","10:10","10:11",
//	    		"10:12","10:13","10:14","10:15","10:16","10:17",
//	    		"10:18","10:19","10:20","10:21","10:22","10:23",
//	    		"10:24","10:25","10:26","10:27","10:28","10:29"};
	    chartLinePaint.setAntiAlias(true);  

	    //基准点。  
	    float gridX = 30+10;  
	    float gridY = getHeight() - 50;  
	    //XY间隔。  
	    float xSpace;
	    xSpace = (float) ((getWidth()-60)/DateX.length-1.5); 
	    
	     
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
	            	canvas.drawLine(x, gridY-20, x, 30+10, linePaint);//短X刻度。  
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
	    canvas.drawText("时间", gridX+18, getHeight()-18, xyChartPaint);
	} 

}

