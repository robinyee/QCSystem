package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TabData extends Fragment {

    private LineChartView mChartView;
    private List<PointValue> values;
    private List<Line> lines;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private List<PointValue> points;
    private int position = 0;
    private Timer timer;
    private boolean isFinish = true;
    private Axis axisY, axisX;
    private Random random = new Random();
    private boolean hasLabelForSelected = true;

    private ListView mainListView;
    private ArrayList<String> listData;

    ListView listview;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_data, container, false);

        //显示趋势图
        mChartView = (LineChartView) view.findViewById(R.id.chart);
        initView();
        timer = new Timer();

        //显示数据列表
        listview = (ListView) view.findViewById(R.id.datalist);
        listData = new ArrayList<String>();
        for (int i = 1; i <= 50; i++) {
            listData.add(i + "        2019年1月20日 2:30         COD " + i + ".00 mg/L");
        }
        String[] stringData = listData.toArray(new String[0]);
        listview.setAdapter(new DataAdapter(view.getContext(), stringData));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //实时添加新的点
                PointValue value1 = new PointValue(position * 1, random.nextInt(10) + 1);
                value1.setLabel("00:00");
                pointValueList.add(value1);

                float x = value1.getX();
                //根据新的点的集合画出新的线
                Line line = new Line(pointValueList);
                line.setColor(Color.GREEN);
                line.setShape(ValueShape.CIRCLE);
                line.setCubic(true);//曲线是否平滑，即是曲线还是折线
                line.setHasLabelsOnlyForSelected(hasLabelForSelected); //设置数据点可以选择

                linesList.clear();
                linesList.add(line);
                lineChartData = initDatas(linesList);
                lineChartView.setLineChartData(lineChartData);

                //根据点的横坐实时变幻坐标的视图范围
                Viewport port;
                if (x > 30) {
                    port = initViewPort(x - 30, x);
                } else {
                    port = initViewPort(0, 30);
                }
                lineChartView.setCurrentViewport(port);//当前窗口

                Viewport maPort = initMaxViewPort(x);
                lineChartView.setMaximumViewport(maPort);//最大窗口
                position++;

                lineChartView.setOnValueTouchListener(new ValueTouchListener());
            }
        }, 300, 10000);
    }

    private void initView() {
        lineChartView = (LineChartView) view.findViewById(R.id.chart);
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();

        //初始化坐标轴
        axisY = new Axis();
        //添加坐标轴的名称
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        lineChartData = initDatas(null);
        lineChartView.setLineChartData(lineChartData);

        Viewport port = initViewPort(0, 30);
        lineChartView.setCurrentViewportWithAnimation(port);
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();

        lineChartView.setOnValueTouchListener(new ValueTouchListener());//为图表设置值得触摸事件
        lineChartView.setZoomEnabled(true);//设置是否支持缩放
        //lineChartView.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        lineChartView.setInteractive(true);//设置图表是否可以与用户互动
        lineChartView.setValueSelectionEnabled(true);//设置图表数据是否选中进行显示
        //lineChartView.setLineChartData(LineChartData data);//为图表设置数据，数据类型为LineChartData

        points = new ArrayList<>();
    }


    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    /**
     * 当前显示区域
     *
     * @param left
     * @param right
     * @return
     */

    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 10;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    /**
     * 最大显示区域
     *
     * @param right
     * @return
     */

    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 10;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 30;
        return port;
    }

    /**
     * 触摸监听类
     */

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getView().getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "默认Toast样式", Toast.LENGTH_SHORT).show();
            /*
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            */
        }
        @Override
        public void onValueDeselected() {

        }
    }
}