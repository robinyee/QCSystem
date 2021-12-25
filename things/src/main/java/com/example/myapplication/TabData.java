package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_APPEND;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

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
    private Button btnTaskData, btnRecordData, btnAlterData;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy年MM月dd日");

    ListView listview;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_data, container, false);

        //初始化控件
        //mChartView = (LineChartView) view.findViewById(R.id.chart); //显示趋势图
        listview = (ListView) view.findViewById(R.id.datalist); //显示数据列表
        //btnRefresh = view.findViewById(R.id.btnRefresh);
        //btnQuery = view.findViewById(R.id.btnQuery);
        //btnExport = view.findViewById(R.id.btnExport);
        //btnDelete = view.findViewById(R.id.btnDelete);
        btnFirstPage = view.findViewById(R.id.btnFirstPage);
        btnPreviousPage = view.findViewById(R.id.btnPreviousPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);
        btnLastPage = view.findViewById(R.id.btnLastPage);
        btnTaskData = view.findViewById(R.id.btnTaskData);
        btnRecordData = view.findViewById(R.id.btnRecordData);
        btnAlterData = view.findViewById(R.id.btnAlertData);


        //查询数据
        SysData.currentPage = 1;
        //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据

        //显示数据列表
        addListTable();
        /*
        //点击刷新按钮
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
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

         */

        /*
        //点击查询按钮
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog("start");  //显示清空数据库警告对话框
            }
        });

         */

        /*
        //点击导出按钮
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("导出数据", "导出数据至CVS文件");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Result> rss;
                        rss = MainActivity.db.resultDao().getAll();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                        String fileName = "Data" + dateFormat.format(System.currentTimeMillis()) + ".csv";
                        try {
                            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            for(Result result:rss) {
                                fos.write((result.rid + "," + dateFormat2.format(result.dateTime)  + "," + result.dataType  + "," + result.dataValue + "\n").getBytes());
                            }
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

                //模拟添加数据

                Log.i("数据库", "添加数据");
                //将数据保存至数据库
                SysData.saveDataToDB();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = new Result();
                        //result.rid = 0;
                        result.dateTime = System.currentTimeMillis();
                        result.dataType = "COD";
                        DecimalFormat df = new DecimalFormat("#.00");
                        result.dataValue = Double.valueOf(df.format(random.nextDouble()*10));
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
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
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
        */

        /*
        //点击清空按钮
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();  //显示清空数据库警告对话框
            }
        });

         */

        //点击Task按钮
        btnTaskData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.WHITE);
                btnRecordData.setTextColor(Color.BLACK);
                btnAlterData.setTextColor(Color.BLACK);
                SysData.currentPage = 1;
                SysData.listDataType = "task";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击Record数据按钮
        btnRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.BLACK);
                btnRecordData.setTextColor(Color.WHITE);
                btnAlterData.setTextColor(Color.BLACK);
                SysData.currentPage = 1;
                SysData.listDataType = "record";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击AlterData按钮
        btnAlterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.BLACK);
                btnRecordData.setTextColor(Color.BLACK);
                btnAlterData.setTextColor(Color.WHITE);
                SysData.currentPage = 1;
                SysData.listDataType = "alert";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
            }
        });

        //点击第一页按钮
        btnFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                //initView();
                //绘制曲线
                //drawLine();

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
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                //initView();
                //绘制曲线
                //drawLine();

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
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                //initView();
                //绘制曲线
                //drawLine();

            }
        });

        //点击最后页按钮
        btnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = SysData.maxPage;
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //显示数据列表
                addListTable();
                //初始化折线图
                //initView();
                //绘制曲线
                //drawLine();

            }
        });

        //初始化折线图
        //initView();
        //绘制曲线
        //drawLine();

        //timer = new Timer(); 定时器

        return view;
    }

    //设置日期对话框
    private void showDateDialog(final String type){

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialogDate = new DatePickerDialog(getView().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date newDate;
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        newDate = calendar.getTime();

                        if(type.equals("start")) {
                            SysData.startDataTime = newDate.getTime();
                            showDateDialog("end");
                        }
                        if (type.equals("end")) {
                            SysData.endDataTime = newDate.getTime();
                            Log.i("数据库", "按时间段查询数据");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if(SysData.listDataType.equals("task")) {
                                        //SysData.tasks = MainActivity.db.taskDao().findByTime(SysData.startDataTime, SysData.endDataTime);
                                    }
                                    if(SysData.listDataType.equals("record")) {
                                        SysData.records = MainActivity.db.recordDao().findByTime(SysData.startDataTime, SysData.endDataTime);
                                    }
                                    if(SysData.listDataType.equals("alert")) {
                                        SysData.alertLogs = MainActivity.db.alertLogDao().findByTime(SysData.startDataTime, SysData.endDataTime);
                                    }
                                }
                            }).start();
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
                            //drawLine();
                            Toast.makeText(getView().getContext(), "起始时间：" + dateFormatShort.format(SysData.startDataTime) + "\n截至时间：" +  dateFormatShort.format(SysData.endDataTime), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.show();
    }

    //按清空按钮时显示对话框
    private void showDeleteDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_warning_black_24dp);
        altDialog.setTitle("警告");
        altDialog.setMessage("清空数据：" + SysData.listDataType + "\n数据删除后将不能恢复，是否清除所有数据？");
        altDialog.setPositiveButton("清空",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("数据库", "删除数据");
                                if(SysData.listDataType.equals("task")) {
                                    MainActivity.db.taskDao().deleteAll();
                                }
                                if(SysData.listDataType.equals("record")) {
                                    MainActivity.db.recordDao().deleteByTime(System.currentTimeMillis());
                                }
                                if(SysData.listDataType.equals("alert")) {
                                    MainActivity.db.alertLogDao().deleteByTime(System.currentTimeMillis());
                                }

                                SysData.currentPage = 1;
                                //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
                            }
                        }).start();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //重新查询数据
                        SysData.currentPage = 1;
                        //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //从数据库读取数据
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
                        //drawLine();
                        Log.i("数据库", "已清空数据");
                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        altDialog.show();
    }

    //显示数据列表
    public void addListTable() {
        //将数据加入到ListView展示数据
        listData = new ArrayList<String>();
        if (SysData.listDataType.equals("task")) {
            if (SysData.tasks != null && !SysData.tasks.isEmpty()) {
                listData.add("序号\t\t\t\t生效时间\t\t\t\t失效时间\t\t\t定时指令\t任务名称\t状态");
                for (Task task : SysData.tasks) {
                    listData.add(task.tid + "\t\t" + dateFormat.format(task.startTime) + "\t\t" + dateFormat.format(task.endTime) + "\t" + task.cron + "\t" + task.task + "\t" + task.enable);
                }
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            } else {
                listData.add("暂未查询到数据");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }
        if (SysData.listDataType.equals("record")) {
            if (SysData.records != null && !SysData.records.isEmpty()) {
                listData.add("序号\t\t时间\t\t\t\t\t标样类型\t\t配制浓度\t\t测量结果");
                for (Record record : SysData.records) {
                    listData.add(record.rid + "\t\t" + dateFormat.format(record.dateTime) + "\t\t" + record.dataType + "\t\t" + record.preValue + "\t\t" + record.meaValue);
                }
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            } else {
                listData.add("暂未查询到数据");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }

        if (SysData.listDataType.equals("alert")) {
            if (SysData.alertLogs != null && !SysData.alertLogs.isEmpty()) {
                listData.add("序号\t\t报警时间\t\t\t\t出错信息\t\t\t\t复位时间");
                for (AlertLog alertLog : SysData.alertLogs) {
                    if (alertLog.resetTime != null) {
                        listData.add(alertLog.alertid + "\t" + dateFormat.format(alertLog.alertTime) + "\t"
                                + "\t" + alertLog.errorMsg + "\t" + dateFormat.format(alertLog.resetTime));
                    } else {
                        listData.add(alertLog.alertid + "\t" + dateFormat.format(alertLog.alertTime) + "\t"
                                + "\t" + alertLog.errorMsg + "\t" + " ");
                    }
                }
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            } else {
                listData.add("暂未查询到数据");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }
    }

    /*
    //绘制曲线
    public void drawLine() {
        //SysData.readChartData(30, 0);       //从数据库中读取30条数据
        //结果数据绘制成折线图
        for (int i = SysData.resultChart.size() - 1; i >= 0; i--) {
            addPoint(SysData.resultChart.size() - i, SysData.resultChart.get(i).dataValue);
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

     */

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
        //lineChartView = (LineChartView) view.findViewById(R.id.chart);
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
        lineChartView.setCurrentViewportWithAnimation(port);
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(false, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();

        lineChartView.setOnValueTouchListener(new ValueTouchListener());//为图表设置值得触摸事件
        lineChartView.setZoomEnabled(false);//设置是否支持缩放
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
        port.top = 20;
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
        port.top = 20;
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