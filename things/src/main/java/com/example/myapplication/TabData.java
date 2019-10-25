package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import static com.example.myapplication.MainActivity.readData;

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
    private Button btnRefresh, btnQuery, btnExport, btnDelete;
    private Button btnFirstPage, btnPreviousPage, btnNextPage, btnLastPage;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    ListView listview;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_data, container, false);

        //初始化控件
        mChartView = (LineChartView) view.findViewById(R.id.chart); //显示趋势图
        listview = (ListView) view.findViewById(R.id.datalist); //显示数据列表
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnQuery = view.findViewById(R.id.btnQuery);
        btnExport = view.findViewById(R.id.btnExport);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnFirstPage = view.findViewById(R.id.btnFirstPage);
        btnPreviousPage = view.findViewById(R.id.btnPreviousPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);
        btnLastPage = view.findViewById(R.id.btnLastPage);

        //查询数据
        SysData.currentPage = 1;
        MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据

        //显示数据列表
        addListTable();

        //点击刷新按钮
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                initView();
                //绘制曲线
                drawLine();
            }
        });

        //点击导出按钮
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("数据库", "添加数据");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = new Result();
                        //result.rid = 0;
                        result.dateTime = System.currentTimeMillis();
                        result.dataType = "COD";
                        result.dataValue = random.nextInt(10) + 1;
                        MainActivity.db.resultDao().insert(result);

                        //db.resultDao().delete(result);
                        //db.resultDao().deleteByTime(System.currentTimeMillis());
                    }
                }).start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //重新查询数据
                SysData.currentPage = 1;
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                initView();
                //绘制曲线
                drawLine();
            }
        });

        //点击清空按钮
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("数据库", "删除数据");
                        MainActivity.db.resultDao().deleteByTime(System.currentTimeMillis());
                        SysData.currentPage = 1;
                        MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                    }
                }).start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //重新查询数据
                SysData.currentPage = 1;
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                initView();
                //绘制曲线
                drawLine();
            }
        });

        //点击第一页按钮
        btnFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击上一页按钮
        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage--;
                if(SysData.currentPage < 1) {
                    SysData.currentPage = 1;
                }
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击下一页按钮
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage++;
                if(SysData.currentPage > (SysData.maxPage)) {
                    SysData.currentPage = SysData.maxPage;
                }
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击最后页按钮
        btnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = SysData.maxPage;
                MainActivity.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //初始化折线图
        initView();
        //timer = new Timer();
        //绘制曲线
        drawLine();



        return view;
    }

    //生成模拟数据列表
    public void addListTable() {
        //将数据加入到ListView展示数据
        listData = new ArrayList<String>();
        if(SysData.results != null && !SysData.results.isEmpty()) {
            for (Result result : SysData.results) {
                listData.add(result.rid + "     " + dateFormat.format(result.dateTime) + "     " + result.dataType + "     " + result.dataValue + " mg/L");
            }
            String[] stringData = listData.toArray(new String[0]);
            listview.setAdapter(new DataAdapter(view.getContext(), stringData));
        } else {
            listData.add("暂未查询到数据");
            String[] stringData = listData.toArray(new String[0]);
            listview.setAdapter(new DataAdapter(view.getContext(), stringData));
        }
    }

    //绘制曲线
    public void drawLine() {
        //结果数据绘制成折线图
        for (int i = SysData.results.size() - 1; i >= 0; i--) {
            addPoint(SysData.results.size() - 1 - i, SysData.results.get(i).dataValue);
        }
    }

    //增加新数据
    public void addPoint(int num, double dataValue) {
        //实时添加新的点
        PointValue value1 = new PointValue(num, (float) dataValue);
        value1.setLabel(String.valueOf(dataValue));
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
        //position++;

        lineChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    /*
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
    */

    private void initView() {
        lineChartView = (LineChartView) view.findViewById(R.id.chart);
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();

        //初始化坐标轴
        axisY = new Axis();
        axisY.setName("COD值");
        //添加坐标轴的名称
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        //axisX.setName("日期");
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        lineChartData = initDatas(null);
        lineChartView.setLineChartData(lineChartData);

        Viewport port = initViewPort(0, 30);
        //Viewport port = initViewPort(System.currentTimeMillis()/1000 - 30 * 24 * 3600, System.currentTimeMillis()/1000);
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
            Toast.makeText(getView().getContext(), "COD值：" + value.getY(), Toast.LENGTH_SHORT).show();
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