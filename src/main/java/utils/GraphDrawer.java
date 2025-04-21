package utils;

import java.util.HashMap;
import edu.princeton.cs.introcs.StdDraw;

/**
 * This class is used to graph the Voltage vs Sample of the system
 */
public class GraphDrawer {
    private static final String imagePath = "../../";
    private double width, height;
    private final HashMap<Integer, Double> points;

    public GraphDrawer(double width, double height){
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLUE);

        setWidth(width);
        setHeight(height);
        this.points = new HashMap<>();

        StdDraw.setPenColor(StdDraw.BLACK);
    }

    public void addPoint(int x, double y){
        this.points.put(x,y);
        StdDraw.text(x % this.width, y % this.height, "*");
    }

    public void save(String fileName) { 
    	StdDraw.save(imagePath + fileName); 
    }
    
    public void setXLabel(String xLabel) { 
    	StdDraw.text(this.width / 2, -0.2, xLabel); 
    }
    
    public void setYLabel(String yLabel) { 
    	StdDraw.text(5, this.height + 0.1, yLabel); 
    }
    
    public void setTitle(String title){ 
    	StdDraw.text(this.width / 2, this.height + 0.1, title); 
    }

    public void setWidth(double width){
        this.width = width;
        StdDraw.setXscale(0, this.width);
        StdDraw.line(0,0,this.width,0);
    }

    public void setHeight(double height){
        this.height = height;
        StdDraw.setYscale(0,this.height);
        StdDraw.line(-0.5,0,0,this.height);
    }
}
