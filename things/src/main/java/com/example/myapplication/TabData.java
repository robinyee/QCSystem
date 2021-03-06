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
    private SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy???MM???dd???");

    ListView listview;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_data, container, false);

        //???????????????
        //mChartView = (LineChartView) view.findViewById(R.id.chart); //???????????????
        listview = (ListView) view.findViewById(R.id.datalist); //??????????????????
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


        //????????????
        SysData.currentPage = 1;
        SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????

        //??????????????????
        addListTable();
        /*
        //??????????????????
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                initView();
                //????????????
                drawLine();
            }
        });

         */

        /*
        //??????????????????
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog("start");  //????????????????????????????????????
            }
        });

         */

        /*
        //??????????????????
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("????????????", "???????????????CVS??????");

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

                //??????????????????

                Log.i("?????????", "????????????");
                //???????????????????????????
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
                //??????????????????
                SysData.currentPage = 1;
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                initView();
                //????????????
                drawLine();


            }
        });
        */

        /*
        //??????????????????
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();  //????????????????????????????????????
            }
        });

         */

        //??????Task??????
        btnTaskData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.WHITE);
                btnRecordData.setTextColor(Color.BLACK);
                btnAlterData.setTextColor(Color.BLACK);
                SysData.currentPage = 1;
                SysData.listDataType = "task";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
            }
        });

        //??????Record????????????
        btnRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.BLACK);
                btnRecordData.setTextColor(Color.WHITE);
                btnAlterData.setTextColor(Color.BLACK);
                SysData.currentPage = 1;
                SysData.listDataType = "record";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
            }
        });

        //??????AlterData??????
        btnAlterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTaskData.setTextColor(Color.BLACK);
                btnRecordData.setTextColor(Color.BLACK);
                btnAlterData.setTextColor(Color.WHITE);
                SysData.currentPage = 1;
                SysData.listDataType = "alert";
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
            }
        });

        //?????????????????????
        btnFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = 1;
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                //initView();
                //????????????
                //drawLine();

            }
        });

        //?????????????????????
        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage--;
                if(SysData.currentPage < 1) {
                    SysData.currentPage = 1;
                }
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                //initView();
                //????????????
                //drawLine();

            }
        });

        //?????????????????????
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage++;
                if(SysData.currentPage > (SysData.maxPage)) {
                    SysData.currentPage = SysData.maxPage;
                }
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                //initView();
                //????????????
                //drawLine();

            }
        });

        //?????????????????????
        btnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.currentPage = SysData.maxPage;
                SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //??????????????????
                addListTable();
                //??????????????????
                //initView();
                //????????????
                //drawLine();

            }
        });

        //??????????????????
        //initView();
        //????????????
        //drawLine();

        //timer = new Timer(); ?????????

        return view;
    }

    //?????????????????????
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
                            Log.i("?????????", "????????????????????????");
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
                            //??????????????????
                            addListTable();
                            //??????????????????
                            initView();
                            //????????????
                            //drawLine();
                            Toast.makeText(getView().getContext(), "???????????????" + dateFormatShort.format(SysData.startDataTime) + "\n???????????????" +  dateFormatShort.format(SysData.endDataTime), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.show();
    }

    //?????????????????????????????????
    private void showDeleteDialog(){
        /* @setIcon ?????????????????????
         * @setTitle ?????????????????????
         * @setMessage ???????????????????????????
         * setXXX????????????Dialog???????????????????????????????????????
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_warning_black_24dp);
        altDialog.setTitle("??????");
        altDialog.setMessage("???????????????" + SysData.listDataType + "\n????????????????????????????????????????????????????????????");
        altDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("?????????", "????????????");
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
                                //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                            }
                        }).start();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //??????????????????
                        SysData.currentPage = 1;
                        //SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //??????????????????
                        addListTable();
                        //??????????????????
                        initView();
                        //????????????
                        //drawLine();
                        Log.i("?????????", "???????????????");
                    }
                });
        altDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // ??????
        altDialog.show();
    }

    //??????????????????
    public void addListTable() {
        //??????????????????ListView????????????
        listData = new ArrayList<String>();
        if (SysData.listDataType.equals("record")) {
            if (SysData.records != null && !SysData.records.isEmpty()) {
                listData.add("??????\t\t??????\t\t\t\t\t????????????\t\t????????????\t\t????????????");
                for (Record record : SysData.records) {
                    listData.add(record.rid + "\t\t" + dateFormat.format(record.dateTime) + "\t\t" + record.dataType + "\t\t" + record.preValue + "\t\t" + record.meaValue);
                }
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            } else {
                listData.add("?????????????????????");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }

        if (SysData.listDataType.equals("task")) {
            if (SysData.tasks != null && !SysData.tasks.isEmpty()) {
                listData.add("??????\t\t\t\t????????????\t\t\t\t????????????\t\t\t????????????\t????????????\t??????");
                for (Task task : SysData.tasks) {
                    listData.add(task.tid + "\t\t" + dateFormat.format(task.startTime) + "\t\t" + dateFormat.format(task.endTime) + "\t" + task.cron + "\t" + task.task + "\t" + task.enable);
                }
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            } else {
                listData.add("?????????????????????");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }

        if (SysData.listDataType.equals("alert")) {
            if (SysData.alertLogs != null && !SysData.alertLogs.isEmpty()) {
                listData.add("??????\t\t????????????\t\t\t\t????????????\t\t\t\t????????????");
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
                listData.add("?????????????????????");
                String[] stringData = listData.toArray(new String[0]);
                listview.setAdapter(new DataAdapter(view.getContext(), stringData));
            }
        }
    }

    /*
    //????????????
    public void drawLine() {
        //SysData.readChartData(30, 0);       //?????????????????????30?????????
        //??????????????????????????????
        for (int i = SysData.resultChart.size() - 1; i >= 0; i--) {
            addPoint(SysData.resultChart.size() - i, SysData.resultChart.get(i).dataValue);
        }
    }

    //???????????????
    public void addPoint(int num, double dataValue) {
        //?????????????????????
        PointValue value1 = new PointValue(num, (float) dataValue);
        value1.setLabel(String.valueOf(dataValue));
        pointValueList.add(value1);

        float x = value1.getX();
        //???????????????????????????????????????
        Line line = new Line(pointValueList);
        line.setColor(Color.GREEN);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);//?????????????????????????????????????????????
        line.setHasLabelsOnlyForSelected(hasLabelForSelected); //???????????????????????????

        linesList.clear();
        linesList.add(line);
        lineChartData = initDatas(linesList);
        lineChartView.setLineChartData(lineChartData);

        //???????????????????????????????????????????????????
        Viewport port;
        if (x > 30) {
            port = initViewPort(x - 30, x);
        } else {
            port = initViewPort(0, 30);
        }
        lineChartView.setCurrentViewport(port);//????????????

        Viewport maPort = initMaxViewPort(x);
        lineChartView.setMaximumViewport(maPort);//????????????
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
                //?????????????????????
                PointValue value1 = new PointValue(position * 1, random.nextInt(10) + 1);
                value1.setLabel("00:00");
                pointValueList.add(value1);

                float x = value1.getX();
                //???????????????????????????????????????
                Line line = new Line(pointValueList);
                line.setColor(Color.GREEN);
                line.setShape(ValueShape.CIRCLE);
                line.setCubic(true);//?????????????????????????????????????????????
                line.setHasLabelsOnlyForSelected(hasLabelForSelected); //???????????????????????????

                linesList.clear();
                linesList.add(line);
                lineChartData = initDatas(linesList);
                lineChartView.setLineChartData(lineChartData);

                //???????????????????????????????????????????????????
                Viewport port;
                if (x > 30) {
                    port = initViewPort(x - 30, x);
                } else {
                    port = initViewPort(0, 30);
                }
                lineChartView.setCurrentViewport(port);//????????????

                Viewport maPort = initMaxViewPort(x);
                lineChartView.setMaximumViewport(maPort);//????????????
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

        //??????????????????
        axisY = new Axis();
        axisY.setName("COD???");
        //????????????????????????
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        //axisX.setName("??????");
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

        lineChartView.setOnValueTouchListener(new ValueTouchListener());//?????????????????????????????????
        lineChartView.setZoomEnabled(false);//????????????????????????
        //lineChartView.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//?????????????????????????????????
        lineChartView.setInteractive(true);//???????????????????????????????????????
        lineChartView.setValueSelectionEnabled(true);//??????????????????????????????????????????
        //lineChartView.setLineChartData(LineChartData data);//???????????????????????????????????????LineChartData

        points = new ArrayList<>();
    }




    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    /**
     * ??????????????????
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
     * ??????????????????
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
     * ???????????????
     */

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getView().getContext(), "COD??????" + value.getY(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "??????Toast??????", Toast.LENGTH_SHORT).show();
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