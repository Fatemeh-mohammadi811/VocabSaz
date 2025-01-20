package ir.shariaty.vocabsaz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LevelsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        val easyButton: Button = findViewById(R.id.level_easy)
        val hardButton: Button = findViewById(R.id.level_hard)
        val veryHardButton: Button = findViewById(R.id.level_very_hard)
        val backToStartButton: Button = findViewById(R.id.back_to_start_button)

        easyButton.setOnClickListener {
            val intent = Intent(this, EasyStageSelectionActivity::class.java)
            startActivity(intent)
        }

        hardButton.setOnClickListener {
            val intent = Intent(this, HardStageSelectionActivity::class.java)
            startActivity(intent)
        }

        veryHardButton.setOnClickListener {
            val intent = Intent(this, VeryHardStageSelectionActivity::class.java)
            startActivity(intent)
        }

        backToStartButton.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }
}