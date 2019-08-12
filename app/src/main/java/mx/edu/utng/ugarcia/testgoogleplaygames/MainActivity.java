package mx.edu.utng.ugarcia.testgoogleplaygames;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Button mainButton;
    private TextView scoreView;
    private TextView timeView;

    private int score = 0;
    private boolean playing = false;

    private GoogleApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainButton = findViewById(R.id.main_button);
        scoreView = findViewById(R.id.score_view);
        timeView = findViewById(R.id.time_view);


        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Could not connect to Play games services");
                        finish();
                    }
                }).build();




        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // More code goes here
                if (!playing) {
                    // The first click
                    playing = true;
                    mainButton.setText("Keep Clicking");

                    // Initialize CountDownTimer to 40 seconds
                    new CountDownTimer(40000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timeView.setText("Time remaining: " + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish() {
                            playing = false;
                            timeView.setText("Game over");
                            mainButton.setVisibility(View.GONE);

                            Games.Leaderboards.submitScore(apiClient,
                                    getString(R.string.leaderboard_maxima_puntuacion),
                                    score);
                        }
                    }.start();  // Start the timer
                } else {
                    // Subsequent clicks
                    score++;
                    scoreView.setText("Score: " + score + " points");
                    if (score > 100) {
                        Games.Achievements
                                .unlock(apiClient,
                                        getString(R.string.achievement_lightning_fast));
                    }
                }
            }
        });
    }

    public void showAchievements(View v) {
        startActivityForResult(
                Games.Achievements
                        .getAchievementsIntent(apiClient),
                1
        );
    }

    public void showLeaderboard(View v) {
        startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_maxima_puntuacion)), 0);
    }
}
