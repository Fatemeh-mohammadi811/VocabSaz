package ir.shariaty.vocabsaz

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FiveLetterActivity : AppCompatActivity() {

    private val stageWords = mapOf(
        1 to "apple",
        2 to "table",
        3 to "chair",
        4 to "light",
        5 to "water",
        6 to "music",
        7 to "happy",
        8 to "green",
        9 to "earth",
        10 to "cloud"
    )

    private var currentWord = ""
    private var shuffledWord = ""
    private var currentIndex = 0
    private var lives = 5
    private var totalScore = 0
    private lateinit var timerTextView: TextView
    private lateinit var livesTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var answerTextView: TextView
    private lateinit var scoreButton: Button
    private lateinit var backButton: Button
    private lateinit var answerBoxes: Array<TextView>
    private lateinit var letterButtons: Array<Button>
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    private val completedLevels = mutableSetOf<Int>()
    private var timeLeftInMillis: Long = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_five_letter)

        initializeViews()
        setupListeners()

        currentIndex = intent.getIntExtra("SELECTED_STAGE", 1) - 1

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        totalScore = sharedPreferences.getInt("totalScore", 0)
        completedLevels.addAll(sharedPreferences.getStringSet("completedLevels", emptySet())?.map { it.toInt() } ?: emptyList())

        updateScore()
        loadNewWord()
    }

    private fun initializeViews() {
        timerTextView = findViewById(R.id.timer_text)
        livesTextView = findViewById(R.id.lives_text)
        levelTextView = findViewById(R.id.level_text)
        answerTextView = findViewById(R.id.answer)
        scoreButton = findViewById(R.id.score_button)
        backButton = findViewById(R.id.back_button)
        answerBoxes = arrayOf(
            findViewById(R.id.answer_box1),
            findViewById(R.id.answer_box2),
            findViewById(R.id.answer_box3),
            findViewById(R.id.answer_box4),
            findViewById(R.id.answer_box5)
        )
        letterButtons = arrayOf(
            findViewById(R.id.letter1),
            findViewById(R.id.letter2),
            findViewById(R.id.letter3),
            findViewById(R.id.letter4),
            findViewById(R.id.letter5)
        )
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            showExitConfirmationDialog()
        }

        scoreButton.setOnClickListener {
            updateScore()
        }

        for (button in letterButtons) {
            button.setOnClickListener {
                val letter = (it as Button).text.toString()
                addLetterToAnswerBox(letter)
                it.isEnabled = false

                if (isAnswerComplete()) {
                    checkAnswer(getAnswerFromBoxes())
                }
            }
        }
    }

    private fun loadNewWord() {
        currentWord = stageWords[currentIndex + 1] ?: ""
        shuffledWord = currentWord.toCharArray().apply { shuffle() }.concatToString()

        for (i in shuffledWord.indices) {
            letterButtons[i].text = shuffledWord[i].toString()
            letterButtons[i].isEnabled = true
        }

        for (box in answerBoxes) {
            box.text = ""
        }

        answerTextView.text = ""
        levelTextView.text = "Stage ${currentIndex +1}"
        updateLivesText()

        updateScore()


        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        startTimer(60000)
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timerTextView.text = "Time: ${millisUntilFinished / 1000} Sec"
            }

            override fun onFinish() {
                lives--
                updateLivesText()
                if (lives == 0) {
                    gameOver()
                } else {
                    Toast.makeText(this@FiveLetterActivity, "Your time is done! Your lives: $lives", Toast.LENGTH_SHORT).show()
                    loadNewWord()
                }
            }
        }.start()
    }

    private fun gameOver() {
        val editor = sharedPreferences.edit()
        editor.putInt("lastStageIndex", currentIndex)
        editor.putInt("totalScore", totalScore)
        editor.putInt("highestUnlockedStage_five_letter", currentIndex + 1)
        editor.putStringSet("completedLevels", completedLevels.map { it.toString() }.toSet())
        editor.apply()

        showDefeatDialog()
    }

    private fun updateLivesText() {
        val livesText = "❤️".repeat(lives)
        livesTextView.text = livesText
    }

    private fun addLetterToAnswerBox(letter: String) {
        for (box in answerBoxes) {
            if (box.text.isEmpty()) {
                box.text = letter
                break
            }
        }
    }

    private fun isAnswerComplete(): Boolean {
        for (box in answerBoxes) {
            if (box.text.isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun getAnswerFromBoxes(): String {
        var answer = ""
        for (box in answerBoxes) {
            answer += box.text
        }
        return answer
    }

    private fun resetLetters() {
        for (button in letterButtons) {
            button.isEnabled = true
        }

        for (box in answerBoxes) {
            box.text = ""
        }
    }

    private fun checkAnswer(answer: String) {
        if (answer == currentWord) {
            if (!completedLevels.contains(currentIndex + 1)) {
                totalScore += 150
                completedLevels.add(currentIndex + 1)
            }

            updateScore()


            val editor = sharedPreferences.edit()
            editor.putInt("totalScore", totalScore)
            editor.putStringSet("completedLevels", completedLevels.map { it.toString() }.toSet())
            editor.apply()

            showSuccessDialog()
        } else {
            lives--
            totalScore = maxOf(0, totalScore - 20)


            updateScore()

            updateLivesText()
            if (lives == 0) {
                gameOver()
            } else {
                showTryAgainDialog()
                resetLetters()
            }
        }
    }

    private fun showSuccessDialog() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()

        val gifImageView = dialogView.findViewById<ImageView>(R.id.success)
        Glide.with(this).load(R.drawable.success).into(gifImageView)

        val btnMenu = dialogView.findViewById<Button>(R.id.btn_menu)
        val btnNext = dialogView.findViewById<Button>(R.id.btn_next)


        if (currentIndex + 1 == stageWords.size) {
            btnNext.isEnabled = false
            btnNext.text = "Finished stages"


            val editor = sharedPreferences.edit()
            editor.putInt("highestUnlockedStage_five_letter", stageWords.size)
            editor.apply()


            Toast.makeText(this, "Excellent! All stages are opened.", Toast.LENGTH_LONG).show()
        }

        btnMenu.setOnClickListener {
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            val intent = Intent(this, VeryHardStageSelectionActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        btnNext.setOnClickListener {
            val nextStage = currentIndex + 2
            val editor = sharedPreferences.edit()
            editor.putInt("highestUnlockedStage_five_letter", nextStage)
            editor.apply()

            if (nextStage > stageWords.size) {
                Toast.makeText(this, "You opened all stages!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, VeryHardStageSelectionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                currentIndex++
                loadNewWord()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showTryAgainDialog() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_try_again, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()


        val gifImageView = dialogView.findViewById<ImageView>(R.id.try_again)
        Glide.with(this).load(R.drawable.try_again).into(gifImageView)


        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            startTimer(timeLeftInMillis)
        }, 3000)

        dialog.show()
    }

    private fun showDefeatDialog() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_defeat, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()

        val gifImageView = dialogView.findViewById<ImageView>(R.id.defeat_gif)
        Glide.with(this).load(R.drawable.defeat).into(gifImageView)

        val btnMenu = dialogView.findViewById<Button>(R.id.btn_menu)
        val btnRetry = dialogView.findViewById<Button>(R.id.btn_retry)

        btnMenu.setOnClickListener {
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            val intent = Intent(this, VeryHardStageSelectionActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        btnRetry.setOnClickListener {
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            loadNewWord()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateScore() {
        scoreButton.text = "Score: $totalScore"
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure to back to the select stage?")
            .setPositiveButton("Yes") { _, _ ->
                if (::countDownTimer.isInitialized) {
                    countDownTimer.cancel()
                }
                val intent = Intent(this, VeryHardStageSelectionActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}