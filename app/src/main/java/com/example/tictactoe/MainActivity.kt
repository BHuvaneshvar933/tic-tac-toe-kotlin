package com.example.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.os.Build
import android.os.Vibrator
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.Position
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    var activePlayer = 0
    var gameActive = 1
    val gameState = arrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)

    val winPositions = arrayOf(
        arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
        arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
        arrayOf(0, 4, 8), arrayOf(2, 4, 6)
    )

    fun tap(view: View) {
        val img = view as ImageView
        val tappedImage = img.tag.toString().toInt()

        if (gameActive == 0) {
            return
        }

        if (gameState[tappedImage] == 2) {
            gameState[tappedImage] = activePlayer
            img.translationY = -1000f

            if (activePlayer == 0) {
                img.setImageResource(R.drawable.x1)
                activePlayer = 1
                findViewById<TextView>(R.id.status).text = "O's Turn - Tap to Play"
            } else {
                img.setImageResource(R.drawable.o1)
                activePlayer = 0
                findViewById<TextView>(R.id.status).text = "X's Turn - Tap to Play"
            }

            img.animate().translationYBy(1000f).duration = 300
        }

        // Check for win
        for (winPosition in winPositions) {
            val a = winPosition[0]
            val b = winPosition[1]
            val c = winPosition[2]

            if (gameState[a] == gameState[b] &&
                gameState[b] == gameState[c] &&
                gameState[a] != 2
            ) {
                val winnerStr = if (gameState[a] == 0) "X has won!" else "O has won!"
                findViewById<TextView>(R.id.status).text = winnerStr
                val winner = if (gameState[a] == 0) "X" else "O"
                Toast.makeText(this, "$winner has won!", Toast.LENGTH_SHORT).show()
                // Vibrate on win
                val vibrator = getSystemService(Vibrator::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    vibrator?.vibrate(300) // Deprecated for older versions
                }

                // ✅ Confetti Blast (fixed)
                val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
                konfettiView.visibility = View.VISIBLE

                val party = Party(
                    emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(200),
                    spread = 360,
                    speed = 10f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    colors = listOf(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.CYAN),
                    position = Position.Relative(0.5, 0.3),
                    shapes = listOf(Shape.Circle, Shape.Square),
                    timeToLive = 2000L
                )

                konfettiView.start(party)

                Handler(Looper.getMainLooper()).postDelayed({
                    konfettiView.visibility = View.GONE
                }, 3000)

                gameActive = 0
                return
            }
        }
    }

    fun rstbtn(view: View) {
        gameReset(view)
    }

    fun gameReset(view: View) {
        gameActive = 1
        activePlayer = 0
        for (i in gameState.indices) {
            gameState[i] = 2
        }

        for (i in 0..8) {
            val imageId = resources.getIdentifier("imageView$i", "id", packageName)
            findViewById<ImageView>(imageId).setImageDrawable(null)
        }

        findViewById<TextView>(R.id.status).text = "X's Turn - Tap to Play"
    }
}


private fun ImageView.setImageResource(unit: Any) {}
