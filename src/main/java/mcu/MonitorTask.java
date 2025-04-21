package mcu;

import java.util.TimerTask;
import org.firmata4j.ssd1306.SSD1306;

import utils.GraphDrawer;

/**
 * This class is used to monitor the watering system
 */
public class MonitorTask extends TimerTask {
    private int sampleCounter;
    private static final double DRYTRESHOLD = 3.521437, WETTRESHOLD = 2.90, NOTSOWETTRESHOLD = 3.360095;

    public boolean IsDry(double soilMoisture) { 
    	return soilMoisture >= DRYTRESHOLD; 
    }
    
    public boolean IsWet(double soilMoisture) { 
    	return soilMoisture <= WETTRESHOLD; 
    }
    
    public boolean IsNotWetEnough(double soilMoisture) { 
    	return soilMoisture <= NOTSOWETTRESHOLD; 
    }

    // Instances of the class
    private final GraphDrawer graph;
    private final PumpDriver pump;
    private final SSD1306 oled;
    private final MoistureSensorDriver sensor;
    private final StringBuilder message; // Message to be displayed on the OLED screen

    /**
     * @param pump Water Pump
     * @param oled OLED screen monitor
     * @param sensor Moisture Sensor
     * @param graph GraphDrawer object
     */
    public MonitorTask(PumpDriver pump, SSD1306 oled, MoistureSensorDriver sensor, GraphDrawer graph){
        this.pump = pump;
        this.oled = oled;
        this.sensor = sensor;
        this.graph = graph;
        this.message = new StringBuilder();
    }

    private void turnPumpOff(){
        try {
            this.pump.turnOff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to record a measured value, and graph it
     * @param graph Graph tp be updating
     * @param measure Value measured to be recorded
     */
    private void updateGraph(GraphDrawer graph, double measure){
        graph.addPoint(sampleCounter, measure);
        sampleCounter++;
    }

    /**
     * This method is used to display a message on the OLED screen
     * @param oled SSD1306 reference
     * @param message Message to be displayed
     */
    private void updateOLED(SSD1306 oled, String message){
        oled.getCanvas().clear();
        oled.getCanvas().drawString(0,0, message);
        oled.display();
    }

    @Override
    public void run() {
        double soilMoisture = sensor.readMoistureLevel();
        updateGraph(graph, soilMoisture);

        // Updating message with the latest moisture reading
        message.append(String.format("Moisture: %5.3fV", soilMoisture));

        if(IsWet(soilMoisture)){
            message.append("\nSoil is Wet");
            turnPumpOff(); // Turning the pump off, just in case
        }else if(IsDry(soilMoisture) || IsNotWetEnough(soilMoisture)){
            message.append("\nSoil is Dry");
            pump.pumpWater(150);
        }

        turnPumpOff(); // Turning the pump off, just in case
        updateOLED(oled, message.toString());
        message.setLength(0);
    }
}
