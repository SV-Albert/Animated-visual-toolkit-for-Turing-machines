package execution;

import ui.SimulatorController;

public class ExecutionTimer implements Runnable{
    private final SimulatorController controller;
    private double speed;
    private boolean isRunning;

    public ExecutionTimer(SimulatorController controller, double speed){
        this.controller = controller;
        this.speed = speed;
    }

    public void changeSpeed(double speed){
        this.speed = speed;
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning){
            try {
                int maxDelayInMs = 2000;
                Thread.sleep((long) (maxDelayInMs/speed));
                controller.nextAutoStep();
            } catch (InterruptedException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }

    public void stop(){
        isRunning = false;
    }
}
