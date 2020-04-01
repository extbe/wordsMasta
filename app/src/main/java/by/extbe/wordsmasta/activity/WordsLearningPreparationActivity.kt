package by.extbe.wordsmasta.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import by.extbe.wordsmasta.R
import by.extbe.wordsmasta.constant.DEFAULT_SOURCE_LANGUAGE
import by.extbe.wordsmasta.viewmodel.WordsLearningPreparationViewModel

class WordsLearningPreparationActivity : AppCompatActivity() {
    private lateinit var sourceLangsSpinner: Spinner
    private lateinit var targetLangsSpinner: Spinner
    private lateinit var wordsGroupSpinner: Spinner

    private val viewModel: WordsLearningPreparationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_learning_preparation)

        sourceLangsSpinner = findViewById(R.id.sourceLanguageCode)
        targetLangsSpinner = findViewById(R.id.targetLanguageCode)
        wordsGroupSpinner = findViewById(R.id.wordsGroup)

        viewModel.languageNames.observe(this, Observer {
            fillSpinnerWithValues(it.toTypedArray(), sourceLangsSpinner)
            sourceLangsSpinner.onItemSelectedListener = OnSourceLanguageSelectedListener()
        })

        viewModel.wordGroups.observe(this, Observer {
            fillSpinnerWithValues(it.toTypedArray(), wordsGroupSpinner)
        })

        findViewById<Button>(R.id.startWordsLearningBtn)?.setOnClickListener {
            val intent = Intent(this, WordsLearningActivity::class.java)
            intent.putExtra(SOURCE_LANGUAGE, sourceLangsSpinner.selectedItem.toString())
            intent.putExtra(TARGET_LANGUAGE, targetLangsSpinner.selectedItem.toString())
            intent.putExtra(WORD_GROUP, wordsGroupSpinner.selectedItem.toString())
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.goBackButton).setOnClickListener { finish() }
    }

    private fun fillSpinnerWithValues(values: Array<String>, spinner: Spinner) {
        ArrayAdapter(baseContext, R.layout.spinner_active_item, values).also {
            it.setDropDownViewResource(R.layout.spinner_popup_item)
            spinner.adapter = it
        }
    }

    inner class OnSourceLanguageSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            parent?.let { spinner ->
                val position = determineItemPosition(DEFAULT_SOURCE_LANGUAGE.title, spinner.adapter)
                parent.setSelection(position)
            }
        }

        private fun determineItemPosition(seekingItem: Any, adapter: Adapter): Int {
            for (i in 0 until adapter.count) {
                val item = adapter.getItem(i)
                if (item == seekingItem) {
                    return i
                }
            }
            return -1
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val sourceLangName = parent?.getItemAtPosition(position) as? String
                ?: DEFAULT_SOURCE_LANGUAGE.title
            val filteredLanguageNames = viewModel.getLanguageNamesExceptGiven(sourceLangName)
                .toTypedArray()
            fillSpinnerWithValues(filteredLanguageNames, targetLangsSpinner)
        }
    }

}
