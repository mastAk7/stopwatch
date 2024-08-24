package com.saksham4106.stopwatch;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.AudioClip;

import java.util.concurrent.*;

public class MainController {

    @FXML
    public TextField hourLimit;
    @FXML
    public TextField minuteLimit;
    @FXML
    public TextField secondLimit;

    private ScheduledFuture<?> scheduledStopwatchFuture;
    private ScheduledFuture<?> scheduledTimerFuture;
    private int counter = 0;
    private int timerCounter = 0;
    private boolean isStopwatchRunning = false;
    private boolean isTimerRunning = false;

    @FXML
    private Label hourTimer;
    @FXML
    private Label minuteTimer;
    @FXML
    private Label secondTimer;
    @FXML
    private Label millisecondTimer;
    @FXML
    private Button startButton;
    @FXML
    private Button startTimerButton;

    AudioClip note = new AudioClip(getClass().getResource("Alarm-ringtone.wav").toString());

    @FXML
    protected void onStartButtonClick() {
        if(!isStopwatchRunning){
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            this.scheduledStopwatchFuture = executorService.scheduleWithFixedDelay(() -> Platform.runLater(() -> {
                counter += 1;
                String[] time = parseTime(counter, true).split(":");
                hourTimer.setText(time[0]);
                minuteTimer.setText(time[1]);
                secondTimer.setText(time[2]);
                millisecondTimer.setText(time[3]);

                if(time[0].equals("00")){
                    hourTimer.setOpacity(0.7);
                }
                if(time[1].equals("00")){
                    minuteTimer.setOpacity(0.7);
                }

            }), 0, 1, TimeUnit.MILLISECONDS);

            startButton.setText("Stop");
            isStopwatchRunning = true;

        }else{
            stopStopwatch();
            isStopwatchRunning = false;
        }
    }

    @FXML
    public void initialize(){

        note.setVolume(0.5);

        EventHandler<KeyEvent> inputFilter = event -> {
            TextField tx = (TextField) event.getSource();
            if((tx.getText().length() >= 2 && tx.getText().matches("[0-9]")) || isTimerRunning){
                event.consume();
            }
        };
        EventHandler<ScrollEvent> scrollFilter = event -> {
          TextField tx = (TextField) event.getSource();
          int factor = 1;
          if(tx.getId().equals("hourLimit")) factor = 3600;
          else if(tx.getId().equals("minuteLimit")) factor = 60;
          if(!isTimerRunning) {
              timerCounter += (event.getDeltaY() / 40) * factor;
              if(timerCounter < 0) timerCounter = 0;
          }
          setTimer();

        };

        hourLimit.addEventFilter(KeyEvent.KEY_TYPED, inputFilter);
        hourLimit.addEventFilter(ScrollEvent.SCROLL, scrollFilter);
        minuteLimit.addEventFilter(KeyEvent.KEY_TYPED, inputFilter);
        minuteLimit.addEventFilter(ScrollEvent.SCROLL, scrollFilter);
        secondLimit.addEventFilter(KeyEvent.KEY_TYPED, inputFilter);
        secondLimit.addEventFilter(ScrollEvent.SCROLL, scrollFilter);
    }

    @FXML
    protected void startTimer(){
        timerCounter = Integer.parseInt(hourLimit.getText()) * 3600 + Integer.parseInt(minuteLimit.getText()) * 60 + Integer.parseInt(secondLimit.getText());
            if(!isTimerRunning){
                ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                this.scheduledTimerFuture = executorService.scheduleWithFixedDelay(() -> Platform.runLater(() -> {
                    timerCounter -= 1;
                    setTimer();
                    if(timerCounter == 0){
                        timerOver();
                    }

                }), 0, 1, TimeUnit.SECONDS);
                isTimerRunning = true;
                startTimerButton.setText("Stop");
            }else {
                stopTimer();

            }
    }
    @FXML
    protected void resetTimer(){
        stopTimer();
        this.timerCounter = 0;
        hourLimit.setText("00");
        minuteLimit.setText("00");
        secondLimit.setText("00");
    }
    @FXML
    protected void resetStopwatch(){
        stopStopwatch();
        this.counter = 0;
        hourTimer.setText("00");
        minuteTimer.setText("00");
        secondTimer.setText("00");
        millisecondTimer.setText("00");

    }
    private void timerOver(){
        note.play();
    }

    private void stopTimer(){
        this.scheduledTimerFuture.cancel(true);
        startTimerButton.setText("start");
        isTimerRunning = false;
        note.stop();

    }
    private void setTimer(){
        if(timerCounter >= 0){
            String[] time = parseTime(timerCounter, false).split(":");
            hourLimit.setText(time[0]);
            minuteLimit.setText(time[1]);
            secondLimit.setText(time[2]);

            if(time[0].equals("00")){
                hourLimit.setOpacity(0.7);
            }
            if(time[1].equals("00")){
                minuteLimit.setOpacity(0.7);
            }
            if(time[2].equals("00")){
                secondLimit.setOpacity(0.7);
            }
        }

    }

    private void stopStopwatch(){
        this.scheduledStopwatchFuture.cancel(true);
        startButton.setText("Start");
    }
    private String parseTime(int totalSeconds, boolean inMilliseconds){
        int millisecondFactor = 1;
        if(inMilliseconds){
            millisecondFactor = 1000;
        }
        int hours = totalSeconds / (3600 * millisecondFactor);
        int minutes  = (totalSeconds % (3600 * millisecondFactor)) / (60 * millisecondFactor);
        int seconds = ((totalSeconds % (3600 * millisecondFactor)) % (60 * millisecondFactor)) / millisecondFactor;

        String hoursStr = hours <= 9 ? "0" + hours : Integer.toString(hours);
        String minStr = minutes <= 9 ? "0" + minutes : Integer.toString(minutes);
        String secStr = seconds <= 9 ? "0" + seconds : Integer.toString(seconds);


        if(inMilliseconds){
            int milliseconds = (((totalSeconds % 3600000) % 60000) % 1000) / 10;
            String millStr = milliseconds <= 9 ? "0" + milliseconds : Integer.toString(milliseconds);
            return hoursStr + ":" + minStr + ":" + secStr + ":" + millStr;
        }else {
            return hoursStr + ":" + minStr + ":" + secStr;
        }

    }


}