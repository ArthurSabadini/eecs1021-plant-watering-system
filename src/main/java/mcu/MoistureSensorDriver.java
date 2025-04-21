package mcu;

import org.firmata4j.Pin;
import java.io.IOException;

/**
 * This class is used to read values of the moisture sensor
 */
public class MoistureSensorDriver {
    private final double VOLT_MOISTURE_CONVERT = 0.00488758553275;
    private final Pin sensorPin;

    public MoistureSensorDriver(Pin pumpPin) throws IOException {
        this.sensorPin = pumpPin;
        this.sensorPin.setMode(Pin.Mode.ANALOG);
    }

    public Pin getPin(){ 
    	return this.sensorPin; 
    }

    /**
     * This function return the analog voltage value
     * @return Analog 0 - 1023
     */
    public final double readVoltage(){ 
    	return this.sensorPin.getValue(); 
    }

    /**
     * This function return the moisture level value
     * @return Voltage 0 - 5V
     */
    public final double readMoistureLevel(){ 
    	return this.VOLT_MOISTURE_CONVERT * readVoltage(); 
    }
}
