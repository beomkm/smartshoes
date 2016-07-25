package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class GraphActivity extends Activity {

    String[] result = new String[] {"정상", "비정상"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        drawPieChart();
    }

    private void drawPieChart(){
        // Pie Chart Section Value
        double[] percent = { 80, 20 };

        // Color of each Pie Chart Sections
        int[] colors = { Color.BLUE, Color.MAGENTA };

        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries(
                "Sports CLub");
        for (int i = 0; i < percent.length; i++) {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(result[i], percent[i]);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < percent.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayChartValues(true);
            //Adding colors to the chart
            defaultRenderer.setBackgroundColor(Color.BLACK);
            defaultRenderer.setLabelsTextSize(20f);
            defaultRenderer.setLegendTextSize(20f);
            defaultRenderer.setApplyBackgroundColor(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setChartTitle("Result of Foot Monitoring");
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(false);

        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);
        // remove any views before u paint the chart
        chartContainer.removeAllViews();
        // drawing pie chart
        View mChart = ChartFactory.getPieChartView(getBaseContext(),
                distributionSeries, defaultRenderer);
        // adding the view to the linearlayout
        chartContainer.addView(mChart);
    }


}
