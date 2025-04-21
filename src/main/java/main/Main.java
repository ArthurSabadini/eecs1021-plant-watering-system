package main;

import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.SSD1306;

import mcu.ArduinoUno;
import mcu.MoistureSensorDriver;
import mcu.MonitorTask;
import mcu.PumpDriver;
import utils.GraphDrawer;

import java.io.IOException;
import java.util.Timer;

public class Main {
    /**
     * This method is used to turn off all devices
     * @param oled SSD1606 device
     * @param pump Water pump
     * @throws IOException Turning pump off
     */
    public static void turnOffDevices(SSD1306 oled, PumpDriver pump) throws IOException {
        pump.turnOff();
        oled.clear();
        oled.turnOff();
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        ArduinoUno board = new ArduinoUno(ArduinoUno.IOSDEFAULTPORT) {
            @Override
            public void execute() throws IOException, InterruptedException {
                // Setting up the graph
                GraphDrawer graph = new GraphDrawer(50,5);
                graph.setXLabel("Sample [n]");
                graph.setYLabel("Voltage [V]");
                graph.setTitle("Sample vs Voltage");

                // Setting up pump
                PumpDriver waterPump = new PumpDriver(this.getPin(Ports.D2));

                // Setting up OLED
                I2CDevice device = this.getI2CDevice(Ports.SSD1306);
                SSD1306 oled = new SSD1306(device, SSD1306.Size.SSD1306_128_64);

                // Setting up moisture sensor
                MoistureSensorDriver sensor = new MoistureSensorDriver(this.getPin(Ports.A3));

                // Initiating OLED
                oled.init();
                oled.getCanvas().setTextsize(1);

                // Setting up timer, and timer task
                // Read sample moisture level every 150 ms
                // Since the pump needs 150 ms to finish its task, we'll give it enough time to turn off
                Timer timer = new Timer();
                MonitorTask task = new MonitorTask(waterPump, oled, sensor, graph);
                timer.scheduleAtFixedRate(task, 8, 150);

                // Setting up button
                Pin button = this.getPin(Ports.D6);
                button.setMode(Pin.Mode.INPUT);

                // When the button is pressed, the system will shut off
                button.addEventListener(new PinEventListener() {
                    private void shutOff(){
                        try {
                            turnOffDevices(oled, waterPump);
                            ThreadContinue();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    @Override
                    public void onModeChange(IOEvent ioEvent) { /* Useless method */ }
                    
                    @Override
                    public void onValueChange(IOEvent ioEvent) {
                        // When button is pressed, we stop the program and save the graph
                        task.cancel();
                        timer.cancel();
                        graph.save("latestRunGraph.png");
                        this.shutOff();
                    }
                });

                // We'll wait until the button is pressed, to end the program
                ThreadWait();
            }
        };

        board.ExitAfterExecution(true);
        board.run();
    }
}
