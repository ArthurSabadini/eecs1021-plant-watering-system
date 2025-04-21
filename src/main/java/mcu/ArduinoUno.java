package mcu;

import java.io.IOException;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.fsm.FiniteStateMachine;
import org.firmata4j.transport.TransportInterface;

public abstract class ArduinoUno extends FirmataDevice {
    public static final String IOSDEFAULTPORT = "/dev/cu.usbserial-0001", WINDOWSDEFAULTPORT = "COM4";
    private boolean exitAfterExecution = false;
    private final Object threadLock = new Object();

    /**
     * The enum {@code Ports} contains all ports of the Arduino Uno board.
     * To get the numerical pin of a specific port call {@code Ports.port.getPortPin()}.
     */
    public enum Ports {

        // Digital and D pins
        D0(0), RX(0),
        D1(1), TX(1),
        D2(2), INT0(2),
        D3(3), INT1(3),
        D4(4),
        D5(5),
        D6(6),
        D7(7),

        // Digital and B pins
        B0(8),
        B1(9),
        B2(10), SS(10),
        B3(11), MOSI(11),
        B4(12), MISO(12),
        B5(13), SCK(13),

        // Analog and A pins
        A0(14),
        A1(15),
        A2(16),
        A3(17),
        A4(18), SDA(18),
        A5(19), SCL(19),

        // I2C pins
        SSD1306(0x3C), // OLED display
        LIS3DHTR(0x19),// 3-axis accelerometer
        AM2320(0x38), // Temperature & Humidity sensor (0x5C, 0x3F, 0x27)
        BMP280(0x77); // Air pressure sensor

        private final int portPin;
        Ports(int portPin){
            this.portPin = portPin;
        }
        
        public int getPortPin(){
            return this.portPin;
        }
    }

    public org.firmata4j.Pin getPin(Ports port){
        return this.getPin(port.getPortPin());
    }

    public org.firmata4j.I2CDevice getI2CDevice(Ports port) throws IOException{
        return this.getI2CDevice((byte)port.getPortPin());
    }

    /**
     * This function is used to pause the current thread, to wait for some other task.
     * @throws InterruptedException Due to synchronization
     */
    public final void ThreadWait() throws InterruptedException{
        synchronized (this.threadLock){
            this.threadLock.wait();
        }
    }

    /**
     * This function is used to come back to the paused thread.
     * @throws IllegalMonitorStateException Due to synchronization
     */
    public final void ThreadContinue() throws IllegalMonitorStateException{
        synchronized (this.threadLock){
            this.threadLock.notify();
        }
    }

    /**
     * Used to determined whether the program should end after execution.
     * @param exit should exit or not
     */
    public void ExitAfterExecution(boolean exit) { this.exitAfterExecution = exit; }

    public ArduinoUno(String port) { 
    	super(port); 
    }
    
    public ArduinoUno(TransportInterface transport) { 
    	super(transport); 
    }
    
    public ArduinoUno(TransportInterface transport, FiniteStateMachine protocol) { 
    	super(transport, protocol); 
    }

    /**
     * This function executes after we've initialized the board, the board is stopped after its execution.
     * Note this method must be overridden
     * @throws IOException Due to input/output
     */
    public abstract void execute() throws IOException, InterruptedException;

    /**
     * This function is responsible for running the board, the method {@code execute()} is used here.
     * Note it is immutable.
     * @throws IOException Could possibly throw an exception during communication
     * @throws InterruptedException Due to synchronization
     *
     */
    public final void run() throws IOException, InterruptedException {
        this.start(); // start comms with board;
        System.out.println("Board started.");
        
        this.ensureInitializationIsDone();
        this.execute(); // Execution

        this.stop(); // finish with the board.
        System.out.println("Board stopped.");

        if(this.exitAfterExecution){
            System.exit(0);
        }
    }
}

