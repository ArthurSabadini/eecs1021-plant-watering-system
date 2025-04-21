package mcu;

import org.firmata4j.Pin;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * This class is used to control the pump
 */
public class PumpDriver {
    private final Object lock;
    private final Pin pumpPin;

    public PumpDriver(Pin pumpPin) throws IOException {
        this.lock = new Object();
        this.pumpPin = pumpPin;
        this.pumpPin.setMode(Pin.Mode.OUTPUT);
    }

    public final void turnOn() throws IOException { 
    	this.pumpPin.setValue(1); 
    }
    
    public final void turnOff() throws IOException { 
    	this.pumpPin.setValue(0); 
    }

    /**
     * This function makes the pump to pump water
     * @param delay Delay between turning pump on and off
     */
    public final void pumpWater(long delay) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                turnOn();
                WaitFor(delay);
                turnOff();
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        });
    }

    /**
     * This function is used to wait a specific amount within the class thread.
     * @param time Time in milliseconds to wait for
     * @throws InterruptedException Due to synchronization
     */
    public final void WaitFor(long time) throws InterruptedException{
        synchronized (this.lock){
            this.lock.wait(time);
        }
    }
}
