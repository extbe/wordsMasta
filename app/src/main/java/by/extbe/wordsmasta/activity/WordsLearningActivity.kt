package by.extbe.wordsmasta.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import by.extbe.wordsmasta.R
import by.extbe.wordsmasta.constant.DEFAULT_SOURCE_LANGUAGE
import by.extbe.wordsmasta.constant.DEFAULT_TARGET_LANGUAGE
import by.extbe.wordsmasta.constant.DEFAULT_WORD_GROUP
import by.extbe.wordsmasta.viewmodel.WordsLearningViewModel

class WordsLearningActivity : AppCompatActivity() {
    companion object {
        const val DEFAULT_CHOICE_BUTTON_COLOR = Color.GRAY
        const val CORRECT_CHOICE_BUTTON_COLOR = Color.GREEN
        const val INCORRECT_CHOICE_BUTTON_COLOR = Color.RED
    }

    private val viewModel: WordsLearningViewModel by viewModels()
    private val choiceButtons = mutableListOf<Button>()

    private lateinit var wordForTranslation: TextView
    private lateinit var activityLayout: ConstraintLayout

    private var wordChosen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_learning)

        val sourceLangName = intent.getStringExtra(SOURCE_LANGUAGE) ?: DEFAULT_SOURCE_LANGUAGE.title
        val targetLangName = intent.getStringExtra(TARGET_LANGUAGE) ?: DEFAULT_TARGET_LANGUAGE.title
        val wordGroupName = intent.getStringExtra(WORD_GROUP) ?: DEFAULT_WORD_GROUP.title
        viewModel.initContext(sourceLangName, targetLangName, wordGroupName)

        val onTranslationChosenListener = WordChosenListener()
        initializeChoiceButton(R.id.button1, onTranslationChosenListener)
        initializeChoiceButton(R.id.button2, onTranslationChosenListener)
        initializeChoiceButton(R.id.button3, onTranslationChosenListener)
        initializeChoiceButton(R.id.button4, onTranslationChosenListener)

        wordForTranslation = findViewById(R.id.sourceWord)
        activityLayout = findViewById(R.id.wordsLearningActivityRootLayout)
        activityLayout.setOnClickListener {
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
                activityLayout.performClick()
                return
            }
            val translation = viewModel.wordForTranslation.value!!.translation
            val pressedBtn = v as Button
            if (pressedBtn.text == translation) {
                viewModel.fetchNextWord()
            } else {
                wordChosen = true
                pressedBtn.setBackgroundColor(INCORRECT_CHOICE_BUTTON_COLOR)
                highlightCorrectTranslation(translation)
            }
        }

        private fun highlightCorrectTranslation(translation: String) {
            choiceButtons.find { it.text == translation }
                ?.setBackgroundColor(CORRECT_CHOICE_BUTTON_COLOR)
        }
    }
}
