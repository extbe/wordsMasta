package by.extbe.wordsmasta.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import by.extbe.wordsmasta.R
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.importDataBtn)?.setOnClickListener {
            val intent = Intent(this, ImportDataActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.learnWordsBtn)?.setOnClickListener {
            val intent = Intent(this, WordsLearningPreparationActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.exitButton).setOnClickListener { finish() }

        invokeRoomOnCreatedLifecycleListener()
    }

    private fun invokeRoomOnCreatedLifecycleListener() {
        GlobalScope.launch(Dispatchers.IO) {
            WordsMastaDatabase.getDatabase(applicationContext).runInTransaction {}
        }
    }
}
