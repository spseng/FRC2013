/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.*;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.*;


/**
 *
 * @author David
 */
public class FrisbeeShooter extends Subsystem {
    /* The ultrasonic scaling constant provides a direct means to convert readings
     * from the ultrasonic sensor (in volts) and convert them to measurements of 
     * distance (in ft.). The constant is calculated based on values of the ultrasonic
     * sensor at 10 ft.
     */
    private static final double ULTRASONIC_K = 10.0 / 1.179;
    //the motor that spins the shooter
    private Victor shooterMotor;
    //is the shooter on or off
    private boolean isOn;    
    //the ultrasonic distance sensor
    AnalogChannel ultrasonic_sensor;
    //the quad encoder on the drive shaft of the shooter motor
    //it is a counter because only one channel works
    Counter counter;
    
    //TESTING
    Timer timer;
    Relay feeder1;
    boolean relayworking, relayclockwise;
    
    public FrisbeeShooter() {
        shooterMotor = new Victor(RobotMap.shooterVictor);

        ultrasonic_sensor = new AnalogChannel(RobotMap.ultrasonic_sensor);
        counter = new Counter(1, 2);

        isOn = false;
        
        //TESTING
        timer = new Timer();
        feeder1 = new Relay(RobotMap.feederRelay);
        relayworking = false;
        relayclockwise = true;
    }
    
    protected void initDefaultCommand() {
        //this subsystem will look for input from the xbox by default
        //setDefaultCommand(new RunShooter());
    }
    
    public void shoot(double angle, double speed) {
        //possibly implemented later
    }
    
    public void activateFrisbeeFeeder() {
        timer.reset();
        timer.start(); //start feeder movement
        relayworking=true;
        relayclockwise=true;
   }
    
    public void resetFrisbeeFeeder() {
        if (relayworking && relayclockwise)
        {
           feeder1.set(Relay.Value.kForward);
        }
        else if (relayworking && !relayclockwise)
        {
           feeder1.set(Relay.Value.kReverse);
        }
        else
        {
            feeder1.set(Relay.Value.kOff);
        }
        
        if ((relayworking && timer.get()>0.5 && relayclockwise)
                ||
             (relayworking && timer.get()>0.52 && !relayclockwise)) //returns microseconds
        {
            if (relayclockwise) //if we went clockwise for 500msec, set up for counterclock
            {
                relayclockwise=false;
                timer.reset();
                timer.start();
            }
            else
            {                       //if done counterclock, then end and prepare for next shot
                relayclockwise=true;
                relayworking=false;
                timer.reset();
                timer.stop();
            }

        }
            
        if (!relayworking)
        {
            feeder1.set(Relay.Value.kOff);
        }
    }
    
    //return the current speed of the shooter motor
    public double getSpeed() {
        return shooterMotor.get();
    }
    
    public void startCounter() {
        counter.start();
    }
    
    public void stopCounter() {
        counter.stop();
    }
    
    public void resetCounter() {
        counter.reset();
    }
    
    public double getCounterRev() {
        return counter.get() / 250.0;
    }
    
    public double getCounterRPM() {
        double freq = 1.0 / counter.getPeriod();
        return freq / 60.0;
    }
    
    public double getUltrasonicDist() {
        return ultrasonic_sensor.getAverageVoltage() * ULTRASONIC_K;
    }
    
    //set the speed of the shooter motor
    //if the shooter is supposed to be off, set the speed to 0
    public void setSpeed(double speed) {
        shooterMotor.set(-speed);
    }
    
    //returns true if the shooter is on
    //this DOES NOT MEAN the shooter must be spinning
    //it is possible for the shooter to be "on" with a value of 0.0
    public boolean isOn() {
        return isOn;
    }
    
    //turn the shooter on
    public void turnOn() {
        isOn = true;
    }
    
    //turn the shooter off
    public void turnOff() {
        isOn = false;
        setSpeed(0.0);
        counter.reset();
    }
}
