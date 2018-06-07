package zone.eloy.projects.androidclock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by eloy on 4/13/18.
 *
 * @author Eloy (Elyas Hadizadeh Tasbiti)
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button buttonOpenStopWatchFragment;
    Button buttonOpenTimerFragment;

    private static final byte STATUS_STOP_WATCH = 1;
    private static final byte STATUS_TIMER = 2;
    private byte fragmentStatus;

    private StopwatchFragment stopwatchFragment;
    private TimerFragment timerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configure();
    }

    @Override
    public void onBackPressed()
    {
        this.moveTaskToBack(true);
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button_open_stop_watch:
                if (fragmentStatus == STATUS_TIMER)
                {
                    getFragmentManager().beginTransaction().remove(timerFragment).commit();
                    getFragmentManager().beginTransaction().add(R.id.fragment_container, stopwatchFragment).commit();
                    fragmentStatus = STATUS_STOP_WATCH;
                }

                buttonOpenStopWatchFragment.setTextColor(getResources().getColor(R.color.DarkRed));
                buttonOpenTimerFragment.setTextColor(getResources().getColor(R.color.DarkGray));
                break;

            case R.id.button_open_timer:
                if (fragmentStatus == STATUS_STOP_WATCH)
                {
                    getFragmentManager().beginTransaction().remove(stopwatchFragment).commit();
                    getFragmentManager().beginTransaction().add(R.id.fragment_container, timerFragment).commit();
                    fragmentStatus = STATUS_TIMER;
                }
                buttonOpenTimerFragment.setTextColor(getResources().getColor(R.color.DarkRed));
                buttonOpenStopWatchFragment.setTextColor(getResources().getColor(R.color.DarkGray));
                break;
        }
    }

    private void configure()
    {
        findViewsById();
        setOnClickListeners();
        initializing();
    }

    private void findViewsById()
    {
        buttonOpenStopWatchFragment = findViewById(R.id.button_open_stop_watch);
        buttonOpenTimerFragment = findViewById(R.id.button_open_timer);
    }

    private void setOnClickListeners()
    {
        buttonOpenStopWatchFragment.setOnClickListener(this);
        buttonOpenTimerFragment.setOnClickListener(this);
    }

    private void initializing()
    {
        stopwatchFragment = new StopwatchFragment();
        timerFragment = new TimerFragment();

        getFragmentManager().beginTransaction().add(R.id.fragment_container, timerFragment).commit();
        buttonOpenTimerFragment.setTextColor(getResources().getColor(R.color.DarkRed));
        fragmentStatus = STATUS_TIMER;
    }

}
