package zone.eloy.projects.androidclock;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by eloy on 4/13/18.
 *
 * @author Eloy (Elyas Hadizadeh Tasbiti)
 */
public class TimerFragment extends Fragment implements View.OnClickListener
{
    private static final byte STATE_INITIAL = 0;
    private static final byte STATE_START = 1;
    private static final byte STATE_STOP = 2;
    private static final byte STATE_FINISHED = 3;

    private long timerDuration = 0;
    private byte timerState = STATE_INITIAL;

    private static final byte FRAGMENT_STATE_ON_RESUME = 1;
    private static final byte FRAGMENT_STATE_ON_PAUSE = 2;
    private byte fragmentState;

    Button buttonStartTimer;
    Button buttonStopTimer;
    Button buttonResumeTimer;
    Button buttonResetTimer;

    TextView textViewTimerSeconds;
    TextView textViewTimerMinutes;
    TextView textViewTimerHours;

    CountDownTimer countDownTimer;

    NumberPicker numberPickerSeconds;
    NumberPicker numberPickerMinutes;
    NumberPicker numberPickerHours;

    LinearLayout linearLayoutTimerContainer;
    LinearLayout linearLayoutTimerInputNumberContainer;
    LinearLayout linearLayoutTimerMessageContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        configure();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button_start_timer:
                if (numberPickerSeconds.getValue() == 0 && numberPickerMinutes.getValue() == 0 && numberPickerHours.getValue() == 0)
                {
                    Toast.makeText(getContext(), getString(R.string.wrongInputForTimer), Toast.LENGTH_SHORT).show();
                } else
                {
                    configStartState();
                    calculateInputTime();
                    countDownTimerCombine();
                }
                break;

            case R.id.button_stop_timer:
                countDownTimer.cancel();
                configStopState();
                break;

            case R.id.button_reset_timer:
                countDownTimer.cancel();
                configInitialState();
                break;

            case R.id.button_resume_timer:
                countDownTimerCombine();
                configStartState();
                break;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        fragmentState = FRAGMENT_STATE_ON_PAUSE;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentState = FRAGMENT_STATE_ON_RESUME;
    }


    private void calculateInputTime()
    {
        long seconds = numberPickerSeconds.getValue();
        long minutes = numberPickerMinutes.getValue();
        long hours = numberPickerHours.getValue();
        timerDuration = (seconds + (60 * minutes) + (60 * 60 * hours)) * 1000;
    }

    private void configure()
    {
        findViewByIds();
        setOnClickListeners();
        setInputNumbersValues();
        goToSuitableState();
    }

    private void findViewByIds()
    {
        linearLayoutTimerContainer = getView().findViewById(R.id.linearLayout_timer_container);
        linearLayoutTimerInputNumberContainer = getView().findViewById(R.id.linearLayout_timer_input_number_container);
        linearLayoutTimerMessageContainer = getView().findViewById(R.id.linearLayout_timer_message_container);

        numberPickerSeconds = getView().findViewById(R.id.number_picker_seconds);
        numberPickerMinutes = getView().findViewById(R.id.number_picker_minutes);
        numberPickerHours = getView().findViewById(R.id.number_picker_hours);

        textViewTimerSeconds = getView().findViewById(R.id.textView_timer_s);
        textViewTimerMinutes = getView().findViewById(R.id.textView_timer_m);
        textViewTimerHours = getView().findViewById(R.id.textView_timer_h);

        buttonStartTimer = getView().findViewById(R.id.button_start_timer);
        buttonStopTimer = getView().findViewById(R.id.button_stop_timer);
        buttonResumeTimer = getView().findViewById(R.id.button_resume_timer);
        buttonResetTimer = getView().findViewById(R.id.button_reset_timer);
    }

    private void setOnClickListeners()
    {
        buttonStartTimer.setOnClickListener(this);
        buttonStopTimer.setOnClickListener(this);
        buttonResumeTimer.setOnClickListener(this);
        buttonResetTimer.setOnClickListener(this);
    }

    private void setInputNumbersValues()
    {
        final String[] secondsNumbers = new String[61];
        for (int i = 0; i < secondsNumbers.length; i++)
            secondsNumbers[i] = String.format("%02d", i);

        String[] hoursNumbers = new String[100];
        for (int i = 0; i < hoursNumbers.length; i++)
            hoursNumbers[i] = String.format("%02d", i);

        numberPickerSeconds.setMaxValue(60);
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setDisplayedValues(secondsNumbers);

        numberPickerMinutes.setMaxValue(60);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setDisplayedValues(secondsNumbers);

        numberPickerHours.setMaxValue(99);
        numberPickerHours.setMinValue(0);
        numberPickerHours.setDisplayedValues(hoursNumbers);
    }

    private void goToSuitableState()
    {
        switch (timerState)
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
                configFinishState();
                break;
        }
    }

    private void countDownTimerCombine()
    {
        countDownTimer = new CountDownTimer(timerDuration, 1000)
        {
            @Override
            public void onTick(long remainingTime)
            {
                timerDuration = remainingTime;

                if (fragmentState == FRAGMENT_STATE_ON_RESUME)
                    calculateRemainingTime(remainingTime);
            }

            @Override
            public void onFinish()
            {
                timerState = STATE_FINISHED;
                if (fragmentState == FRAGMENT_STATE_ON_RESUME)
                    configFinishState();
            }
        };
        countDownTimer.start();
    }

    private void calculateRemainingTime(long remainingTime)
    {
        long seconds = remainingTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        textViewTimerSeconds.setText(String.format("%02d", seconds));
        textViewTimerMinutes.setText(String.format("%02d:", minutes));
        textViewTimerHours.setText(String.format("%02d:", hours));

        if (hours == 0 && minutes == 0 && seconds < 11)
            makeTimerRed();
         else
            makeTimerGray();
    }

    private void configInitialState()
    {
        linearLayoutTimerMessageContainer.setVisibility(View.GONE);
        buttonStartTimer.setVisibility(View.VISIBLE);
        buttonStopTimer.setVisibility(View.GONE);
        buttonResumeTimer.setVisibility(View.GONE);
        buttonResetTimer.setVisibility(View.GONE);
        linearLayoutTimerInputNumberContainer.setVisibility(View.VISIBLE);
        linearLayoutTimerContainer.setVisibility(View.GONE);
        timerState = STATE_INITIAL;
        timerDuration = 0;
        numberPickerSeconds.setValue(0);
        numberPickerMinutes.setValue(0);
        numberPickerHours.setValue(0);
    }


    private void configStartState()
    {
        if (linearLayoutTimerInputNumberContainer.getVisibility() == View.VISIBLE)
        {
            linearLayoutTimerInputNumberContainer.setVisibility(View.GONE);
            linearLayoutTimerMessageContainer.setVisibility(View.GONE);
            linearLayoutTimerContainer.setVisibility(View.VISIBLE);
        }
        buttonResumeTimer.setVisibility(View.GONE);
        buttonStartTimer.setVisibility(View.GONE);

        buttonStopTimer.setVisibility(View.VISIBLE);
        buttonResetTimer.setVisibility(View.VISIBLE);
        timerState = STATE_START;
        calculateRemainingTime(timerDuration);
    }

    private void configStopState()
    {
        buttonStopTimer.setVisibility(View.GONE);
        buttonStartTimer.setVisibility(View.GONE);
        linearLayoutTimerInputNumberContainer.setVisibility(View.GONE);
        linearLayoutTimerMessageContainer.setVisibility(View.GONE);
        buttonResumeTimer.setVisibility(View.VISIBLE);
        timerState = STATE_STOP;
        calculateRemainingTime(timerDuration);
    }

    private void configFinishState()
    {
        linearLayoutTimerInputNumberContainer.setVisibility(View.GONE);
        linearLayoutTimerContainer.setVisibility(View.VISIBLE);
        buttonResumeTimer.setVisibility(View.GONE);
        buttonStartTimer.setVisibility(View.GONE);
        buttonStopTimer.setVisibility(View.GONE);
        buttonResetTimer.setVisibility(View.VISIBLE);
        timerState = STATE_FINISHED;
        timerDuration = 0;
        textViewTimerSeconds.setText(String.format("%02d", 0));
        linearLayoutTimerMessageContainer.setVisibility(View.VISIBLE);
        makeTimerRed();
    }

    private void makeTimerRed()
    {
        textViewTimerSeconds.setTextColor(getResources().getColor(R.color.DarkRed));
        textViewTimerMinutes.setTextColor(getResources().getColor(R.color.DarkRed));
        textViewTimerHours.setTextColor(getResources().getColor(R.color.DarkRed));
    }

    private void makeTimerGray()
    {
        textViewTimerSeconds.setTextColor(getResources().getColor(R.color.DarkGray));
        textViewTimerMinutes.setTextColor(getResources().getColor(R.color.DarkGray));
        textViewTimerHours.setTextColor(getResources().getColor(R.color.DarkGray));
    }




}
