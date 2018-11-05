package com.example.tworld.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.tworld.myapplication.R;

import java.util.ArrayList;



/**构建了黑色的背景，绿网格，画红色心电图的一个布局，
 * 心电图可以移动
 */

public class WH_ECGView extends View {

    private float gap_grid;//网格间距
    private int width,height;//手机页面宽，高
    private int xori;//原点x坐标
    private int grid_hori,grid_ver;//横、纵线条数
    private float gap_x;//两点间横坐标间距
    private int dataNum_per_grid = 2;//每小格内的数据个数
    private float y_center;//中心y值
    private ArrayList<String> data_source = new ArrayList<>(500);//数据点，500个

    private float x_change ;//滑动查看时，x坐标的变化
    private static float x_changed ;
    private static float startX;//手指touch屏幕时候的x坐标
    private int data_num;//总的数据个数
    private float offset_x_max;//x轴最大偏移量

    private int rect_high = 80;//下方用于显示心电图形的矩形区域的高
    private float rect_width;//下方矩形框的宽度
    private float rect_gap_x;//下方矩形区域心电图数据间的横坐标间隙
    private float multiple_for_rect_width;//矩形区域的宽与屏幕宽的比

    //构造函数
    public WH_ECGView(Context context, AttributeSet attrs){
        super(context,attrs);
        //背景色
        this.setBackgroundColor(getResources().getColor(R.color.black));//背景色是黑色的
    }

    //构造函数
    public WH_ECGView(Context context){
        super(context);
        //背景色
        this.setBackgroundColor(getResources().getColor(R.color.black));//背景色是黑色的
    }


    //可以看做初始化
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed){
            xori = 0;//原点
            gap_grid = 30.0f;//网格之间的间距
            width = getWidth();//宽度
            height = getHeight();//高度
            grid_hori = height/(int)gap_grid;//横线条数
            grid_ver = width/(int)gap_grid;//竖线条数
            y_center = (height - rect_high)/2;//y轴中线
            gap_x = gap_grid/dataNum_per_grid;//两个数据点之间的横坐标间距
            data_num = data_source.size();//数据数量,500
            x_change = 0.0f ;//滑动查看时，x坐标的变化
            x_changed = 0.0f ;
            offset_x_max = width - gap_x * data_num;//x轴最大偏移量
            rect_gap_x = (float) width/data_num;//心电图数据间的横坐标间隙
            rect_width = (float) width * width/(gap_x * data_num);//下方矩形框的宽度
            multiple_for_rect_width = (float) width/rect_width;//矩形区域的宽与屏幕宽的比
            Log.e("json","本页面宽： " + width +"  高:" + height);
//            Log.e("json","两点间横坐标间距:" + gap_x + "   矩形区域两点间横坐标间距：" + rect_gap_x);
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    //画背景网格和心电图波形
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawGrid(canvas);
       // DrawECGWave(canvas);  //画波形
    }

    /**
     * 画背景网格
     */
    private void DrawGrid(Canvas canvas){
        //横线
        for (int i = 1 ; i < grid_hori + 2 ; i ++){
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.limegreen)); //<color name="data_pr">#0a7b14</color>
            paint.setStrokeWidth(1.0f);//画笔宽度
            Path path = new Path();
            path.moveTo(xori, gap_grid * (i-1) + (height-grid_hori*gap_grid)/2);
            path.lineTo(width,gap_grid * (i-1) + (height-grid_hori*gap_grid)/2);
            if ( i % 5 != 0 ){//每第五条，为实线   其余为虚线 ，以下为画虚线方法
                PathEffect effect = new DashPathEffect(new float[]{1,5},1);
                paint.setPathEffect(effect);
            }
            canvas.drawPath(path,paint);
        }
        //竖线
        for (int i = 1 ; i < grid_ver + 2 ; i ++){
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.limegreen));
            paint.setStrokeWidth(1.0f);
            Path path = new Path();
            path.moveTo(gap_grid * (i-1) + (width-grid_ver*gap_grid)/2, 0);
            path.lineTo(gap_grid * (i-1) + (width-grid_ver*gap_grid)/2,height);
            if ( i % 5 != 0 ){
                PathEffect effect = new DashPathEffect(new float[]{1,5},1);
                paint.setPathEffect(effect);
            }
            canvas.drawPath(path,paint);
        }
    }

    /**
     * 画心电图
     */
    private void DrawECGWave(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.google_red));//心电图的波形是红色的
        paint.setStrokeWidth(3.0f);
        Path path = new Path();
        x_changed += x_change;

        if (x_changed > xori){//防止向右滑动太多 超左边界
            x_changed = xori;
        }else if (x_changed < offset_x_max ){//防止向左滑动太多 超右边界
            x_changed = offset_x_max;
        }

        //画点的路径
        //此处 xori设置为0 ，未用上
        int iXor = 1;
        for (int i = 1 ; i < this.data_source.size() ; i ++){
            float nnn = xori + gap_x * i +  x_changed;//x坐标
            if (nnn >= 0 ){
                iXor = i;//数据点的序号
                path.moveTo(nnn, getY_coordinate(data_source.get(i)));//心率的路径，x坐标y坐标
                break;
            }
        }

        for (int i = iXor; i < this.data_source.size(); i ++){
            float nnn = xori + gap_x * i +  x_changed;
            if (nnn < width + gap_x){
                path.lineTo(xori + gap_x * i +  x_changed , getY_coordinate(data_source.get(i)));//画路径
            }
        }

        canvas.drawPath(path,paint);

        /**
         * 绘制下方矩形区域
         * @param canvas
         */
        Paint Rect_paint = new Paint();
        Rect_paint.setStyle(Paint.Style.FILL);
        Rect_paint.setColor(getResources().getColor(R.color.greenlucency));
        Rect_paint.setStrokeWidth(1.0f);
        Path Rect_path = new Path();
        float rect_xori = (float) (0-x_changed)/multiple_for_rect_width;
        Rect_path.moveTo(rect_xori,height-rect_high - 20);
        Rect_path.lineTo(rect_xori+rect_width,height-rect_high - 20);
        Rect_path.lineTo(rect_xori+rect_width,height);
        Rect_path.lineTo(rect_xori,height);
        canvas.drawPath(Rect_path,Rect_paint);
    }

    /**
     * 滑动查看心电图
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX=event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                x_change = event.getX()-startX;
                invalidate();
                break;
        }
            return true;
    }

    /**
     * 将数值转换为y坐标，中间大的显示心电图的区域
     */
    private float getY_coordinate(String data){
        int y_int = Integer.parseInt(data);
        y_int = (y_int - 2048) *(-1);
        float y_coor = 0.0f;

        y_coor = y_int *3/4 + y_center;
        return y_coor;
    }

    /**
     * 暴露接口，设置数据源
     */
    public void setData(ArrayList<Integer> data){
        for (int item : data){
            this.data_source.add(String.valueOf(item));
        }
//        invalidate();
        postInvalidate();//刷新界面
    }

    //得到数据
    public ArrayList<String> getData_source(){

        return data_source;
    }


}
