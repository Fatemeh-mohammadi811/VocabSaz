package ir.shariaty.vocabsaz

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EasyStageSelectionActivity : AppCompatActivity() {
    private lateinit var stageButtons: Array<Button>
    private var highestUnlockedStage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_stage_selection)


        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        highestUnlockedStage = sharedPreferences.getInt("highestUnlockedStage_three_letter", 1)


        stageButtons = arrayOf(
            findViewById(R.id.stage1),
            findViewById(R.id.stage2),
            findViewById(R.id.stage3),
            findViewById(R.id.stage4),
            findViewById(R.id.stage5),
            findViewById(R.id.stage6),
            findViewById(R.id.stage7),
            findViewById(R.id.stage8),
            findViewById(R.id.stage9),
            findViewById(R.id.stage10)
        )


        for (i in stageButtons.indices) {
            if (i + 1 > highestUnlockedStage) {
                stageButtons[i].isEnabled = false
                stageButtons[i].text = "Lock"
            } else {
                stageButtons[i].isEnabled = true
                stageButtons[i].text = "Stage ${i + 1}"
                stageButtons[i].setOnClickListener {
                    val intent = Intent(this, ThreeLetterActivity::class.java)
                    intent.putExtra("SELECTED_STAGE", i + 1)
                    startActivity(intent)
                }
            }
        }

        stageButtons[0].isEnabled = true
        stageButtons[0].text = "Stage 1"


        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, LevelsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}