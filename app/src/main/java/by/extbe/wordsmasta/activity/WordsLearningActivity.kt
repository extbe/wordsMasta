package by.extbe.wordsmasta.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import by.extbe.wordsmasta.R
import by.extbe.wordsmasta.constant.DEFAULT_SOURCE_LANGUAGE
import by.extbe.wordsmasta.constant.DEFAULT_TARGET_LANGUAGE
import by.extbe.wordsmasta.viewmodel.WordsLearningViewModel
import kotlinx.android.synthetic.main.activity_words_learning.*

class WordsLearningActivity : AppCompatActivity() {
    companion object {
        const val DEFAULT_CHOICE_BUTTON_COLOR = Color.GRAY
        const val CORRECT_CHOICE_BUTTON_COLOR = Color.GREEN
        const val INCORRECT_CHOICE_BUTTON_COLOR = Color.RED
    }

    private val viewModel: WordsLearningViewModel by viewModels()
    private val choiceButtons = mutableListOf<Button>()

    private lateinit var wordForTranslation: TextView
    private lateinit var buttonsView: LinearLayout

    private var wordChosen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_learning)

        val sourceLangCode = intent.getStringExtra(SOURCE_LANGUAGE) ?: DEFAULT_SOURCE_LANGUAGE.title
        val targetLangCode = intent.getStringExtra(TARGET_LANGUAGE) ?: DEFAULT_TARGET_LANGUAGE.title
        viewModel.initContext(sourceLangCode, targetLangCode)

        val onTranslationChosenListener = WordChosenListener()
        initializeChoiceButton(R.id.button1, onTranslationChosenListener)
        initializeChoiceButton(R.id.button2, onTranslationChosenListener)
        initializeChoiceButton(R.id.button3, onTranslationChosenListener)
        initializeChoiceButton(R.id.button4, onTranslationChosenListener)

        wordForTranslation = findViewById(R.id.sourceWord)
        buttonsView = findViewById(R.id.translationButtons)
        buttonsView.setOnClickListener {
            if (wordChosen) viewModel.fetchNextWord()
        }

        viewModel.wordForTranslation.observe(this, Observer {
            wordForTranslation.text = it.word
            for ((buttonPosition, choiceButton) in choiceButtons.withIndex()) {
                choiceButton.text = it.translationChoices[buttonPosition]
                choiceButton.setBackgroundColor(DEFAULT_CHOICE_BUTTON_COLOR)
            }
            wordChosen = false
        })
    }

    private fun initializeChoiceButton(buttonId: Int, onClickListener: View.OnClickListener) {
        val button = findViewById<Button>(buttonId)
        button.setOnClickListener(onClickListener)
        choiceButtons.add(button)
    }

    inner class WordChosenListener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (wordChosen) {
                buttonsView.performClick()
                return
            }
            val translation = viewModel.wordForTranslation.value!!.translation
            val pressedBtn = v as Button
            if (pressedBtn.text == translation) {
                pressedBtn.setBackgroundColor(CORRECT_CHOICE_BUTTON_COLOR)
            } else {
                pressedBtn.setBackgroundColor(INCORRECT_CHOICE_BUTTON_COLOR)
                highlightCorrectTranslation(translation)
            }
            wordChosen = true
        }

        private fun highlightCorrectTranslation(translation: String) {
            choiceButtons.find { it.text == translation }
                ?.setBackgroundColor(CORRECT_CHOICE_BUTTON_COLOR)
        }
    }
}
