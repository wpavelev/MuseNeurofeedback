package com.wpavelev.museneurofeedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.wpavelev.museneurofeedback.Constants.FRAMERATE;


public class GraphFragment extends Fragment {


    private Mean graphValuesYCurrent = new Mean();
    private Mean graphValuesYMean = new Mean();

    //GraphFragment
    private GraphView graph;
    private LineGraphSeries<DataPoint> lineGraphCurrentMean;
    private LineGraphSeries<DataPoint> lineGraphGeneralMean;
    private double lastValueCurrentMeanGraph = 0;
    private double lastValueGeneralMeanGraph = 0;

    private double chartXValue = 0;
    private double chartXValueDelay = 0;


    private int graphLimit = 6000;
    private boolean dataSetChanged = false;
    private int chartFrameRate = 60;

    View view;

    Constants settings = Constants.getInstance();


    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_graph, container, false);

        initGraph();
        resetGraph();

        return view;

    }


    private void initGraph() {
        final double rate = FRAMERATE / chartFrameRate;

        graphLimit = settings.getPretestTime() * (int) rate;

        graph = view.findViewById(R.id.chart);

        graph.removeAllSeries();

        // custom label formatter to show currency "EUR"
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value / rate, isValueX) + " sec";
                } else {
                    // show currency for y values
                    return super.formatLabel(value*100, isValueX) + "%";
                }
            }
        });


        lineGraphCurrentMean = new LineGraphSeries<>();
        lineGraphCurrentMean.setColor(getResources().getColor(R.color.green));


        lineGraphGeneralMean = new LineGraphSeries<>();
        lineGraphGeneralMean.setColor(getResources().getColor(R.color.red));

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        graph.addSeries(lineGraphCurrentMean);
        graph.addSeries(lineGraphGeneralMean);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(graphLimit);


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setMinY(0);




    }

    private double getCurrentMean() {

        double value = 0;
        // TODO: 30.05.2018

        return value;
    }
    private double getGeneralMean() {

        double value = 0;
        // TODO: 30.05.2018

        return value;
    }



    private void addGraphData() {

            if (dataSetChanged) {
                chartXValue++;
                dataSetChanged = false;
            }






            chartXValueDelay++;
            if (chartXValueDelay >= chartFrameRate) {
                chartXValueDelay = 0;
                chartXValue++ ;
                lastValueCurrentMeanGraph = getCurrentMean();
                lineGraphCurrentMean.appendData(new DataPoint(chartXValue, lastValueCurrentMeanGraph), false, graphLimit);
                lastValueGeneralMeanGraph = getGeneralMean();
                lineGraphGeneralMean.appendData(new DataPoint(chartXValue, lastValueGeneralMeanGraph), false, graphLimit);
            }





        if (chartXValue >= graphLimit) {
            chartXValue = 0;
            resetGraph();

        }





    }


    private void resetGraph() {

        DataPoint[] tempData = new DataPoint[]{new DataPoint(0, lastValueCurrentMeanGraph)};
        lineGraphCurrentMean.resetData(tempData);
        tempData = new DataPoint[]{new DataPoint(0, lastValueGeneralMeanGraph)};
        lineGraphGeneralMean.resetData(tempData);

    }
}
