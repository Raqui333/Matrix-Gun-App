package io.github.bemvindoamatrix.matrixgun

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // songs initialize
        val wrong_click_song  = MediaPlayer.create(this, R.raw.errorclick)
        val  right_click_song = MediaPlayer.create(this, R.raw.rightclick)
        val  winning_song     = MediaPlayer.create(this, R.raw.winning)
        val  losing_song      = MediaPlayer.create(this, R.raw.losing)

        // putting wheel to run
        val wheel = findViewById<ImageView>(R.id.wheel)
        val wheel_animation = ObjectAnimator.ofFloat(wheel, "rotation", 360f).apply {
            duration     = 2000
            repeatCount  = Animation.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        // setting up HUD
        val lifebar = findViewById<TextView>(R.id.life)
        val scoreboard = findViewById<TextView>(R.id.score)

        lifebar.text    = "5"    // max chances/lives
        scoreboard.text = "0" // init score

        var life :Int
        var score :Int

        // getting when the user click anywhere in the main acticity
        val main_act = findViewById<ConstraintLayout>(R.id.mainAct)
        main_act.setOnClickListener() {
            var life  = Integer.parseInt(lifebar.text.toString())

            // change the HUD when winning or losing
            val winOrLose : (String) -> Unit = { result ->
                scoreboard.text     = result
                scoreboard.textSize = 50f

                wheel_animation.cancel()
                wheel.rotation = 0f

                if (result == getString(R.string.on_win))
                    winning_song.start()
                else
                    losing_song.start()
            }

            // taking action when the shot was right or wrong
            if (scoreboard.text == getString(R.string.on_lose) || scoreboard.text == getString(R.string.on_win)) {
                // resetting HUD when blocked
                wheel_animation.start()
                wheel_animation.duration = 2000

                lifebar.text        = "5"
                scoreboard.text     = "0"
                scoreboard.textSize = 70f
            } else { // when HUD isn't blocked
                score = Integer.parseInt(scoreboard.text.toString())

                if (wheel.rotation <= 10f || wheel.rotation >= 350f) {
                    // increasing score and speed when shot hits the gap
                    scoreboard.text = (++score).toString()
                    wheel_animation.duration -= 100

                    if (score >= 10) { // win
                        winOrLose(getString(R.string.on_win))
                    } else {
                        if (right_click_song.isPlaying) right_click_song.seekTo(0)
                        right_click_song.start()
                    }

                } else {
                    // decreasing life when shot miss the gap
                    lifebar.text = (--life).toString()

                    if (life <= 0) { // lose
                        winOrLose(getString(R.string.on_lose))
                    } else {
                        if (wrong_click_song.isPlaying) wrong_click_song.seekTo(0)
                        wrong_click_song.start()
                    }
                }
            }
        }
    }
}