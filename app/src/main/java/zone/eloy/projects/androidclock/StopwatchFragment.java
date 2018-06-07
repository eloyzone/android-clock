package zone.eloy.projects.androidclock;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by eloy on 4/13/18.
 *
 * @author Eloy (Elyas Hadizadeh Tasbiti)
 */
public class StopwatchFragment extends Fragment implements View.OnClickListener
{
    private static final byte STATE_INITIAL = 0;
    private static final byte STATE_START = 1;
    private static final byte STATE_STOP = 2;
    private static final byte STATE_FINISHED = 3;

    private final static long TIMER_HAS_NOT_STARTED_YET = -1;
    private final static long LONG_DURATION_FOR_TIMER = 3_660_099; // milli seconds equal to 59:59:99
    private long tenMilliSecondsRemaining = TIMER_HAS_NOT_STARTED_YET;

    private byte stopWatchState = STATE_INITIAL;

    Button buttonStartStopWatch;
    Button buttonStopStopWatch;
    Button buttonResumeStopWatch;
    Button buttonResetStopWatch;

    TextView textViewStopWatchTenSeconds;
    TextView textViewStopWatchSeconds;
    TextView textViewStopWatchMinutes;

    CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stop_watch, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        configure();
    }

    private void configure()
    {
        findViewByIds();
        setOnClickListeners();
        goToSuitableState();
    }

    private void findViewByIds()
    {
        buttonStartStopWatch = getView().findViewById(R.id.button_start_stop_watch);
        buttonStopStopWatch = getView().findViewById(R.id.button_stop_stop_watch);
        buttonResumeStopWatch = getView().findViewById(R.id.button_resume_stop_watch);
        buttonResetStopWatch = getView().findViewById(R.id.button_reset_stop_watch);

        textViewStopWatchTenSeconds = getView().findViewById(R.id.textView_stopwatch_10ms);
        textViewStopWatchSeconds = getView().findViewById(R.id.textView_stopwatch_s);
        textViewStopWatchMinutes = getView().findViewById(R.id.textView_stopwatch_m);
    }

    private void setOnClickListeners()
    {
        buttonStartStopWatch.setOnClickListener(this);
        buttonStopStopWatch.setOnClickListener(this);
        buttonResumeStopWatch.setOnClickListener(this);
        buttonResetStopWatch.setOnClickListener(this);
    }

    private void goToSuitableState()
    {
        switch (stopWatchState)
        {
            case STATE_INITIAL:
                configInitialState();
                break;
            case STATE_START:
                configStartState();
                break;
            case STATE_STOP:
                configStopState();
                break;
            case STATE_FINISHED:
                break;
        }
    }

    private void countDownTimerCombine(long remainingSecondsStatus)
    {
        long duration = LONG_DURATION_FOR_TIMER;
        if (remainingSecondsStatus != TIMER_HAS_NOT_STARTED_YET)
            duration = remainingSecondsStatus;

        countDownTimer = new CountDownTimer(duration, 10)
        {
            @Override
            public void onTick(long remainingTime)
            {
                calculateRemainingTime(remainingTime);
            }

            @Override
            public void onFinish()
            {
            }
        };
        countDownTimer.start();
    }

    private void configInitialState()
    {
        buttonStartStopWatch.setVisibility(View.VISIBLE);
        buttonStopStopWatch.setVisibility(View.GONE);
        buttonResumeStopWatch.setVisibility(View.GONE);
        buttonResetStopWatch.setVisibility(View.GONE);
        tenMilliSecondsRemaining = TIMER_HAS_NOT_STARTED_YET;
        textViewStopWatchTenSeconds.setText("00");
        textViewStopWatchSeconds.setText("00:");
        textViewStopWatchMinutes.setText("00:");
        stopWatchState = STATE_INITIAL;
    }

    private void configStartState()
    {
        buttonResumeStopWatch.setVisibility(View.GONE);
        buttonStartStopWatch.setVisibility(View.GONE);
        buttonStopStopWatch.setVisibility(View.VISIBLE);
        buttonResetStopWatch.setVisibility(View.VISIBLE);
        stopWatchState = STATE_START;
    }

    private void configStopState()
    {
        buttonStartStopWatch.setVisibility(View.GONE);
        buttonStopStopWatch.setVisibility(View.GONE);

        buttonResumeStopWatch.setVisibility(View.VISIBLE);
        stopWatchState = STATE_STOP;
        calculateRemainingTime(tenMilliSecondsRemaining);
    }

    private void calculateRemainingTime(long remainingTime)
    {
        long mSeconds = (LONG_DURATION_FOR_TIMER - remainingTime) / 10;
        textViewStopWatchTenSeconds.setText(String.format("%02d", mSeconds % 100));

        long seconds = mSeconds / 100;
        textViewStopWatchSeconds.setText(String.format("%02d:", seconds % 60));

        long minutes = seconds / 60;
        textViewStopWatchMinutes.setText(String.format("%02d:", minutes));

        tenMilliSecondsRemaining = remainingTime;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button_start_stop_watch:
                countDownTimerCombine(tenMilliSecondsRemaining);
                configStartState();
                break;

            case R.id.button_stop_stop_watch:
                countDownTimer.cancel();
                configStopState();
                break;

            case R.id.button_reset_stop_watch:
                countDownTimer.cancel();
                configInitialState();
                break;

            case R.id.button_resume_stop_watch:
                countDownTimerCombine(tenMilliSecondsRemaining);
                configStartState();
                break;
        }
    }
}
