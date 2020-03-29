package by.extbe.wordsmasta.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
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
    private val viewModel: WordsLearningViewModel by viewModels()
    private val choiceButtons = mutableListOf<Button>()

    private lateinit var defaultChoiceBtnBg: Drawable
    private lateinit var successChoiceBtnBg: Drawable
    private lateinit var errorChoiceBtnBg: Drawable
    private lateinit var wordForTranslation: TextView
    private lateinit var activityLayout: ConstraintLayout

    private var wordChosen = false
    private var defaultChoiceBtnTextColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_learning)

        defaultChoiceBtnBg = getDrawable(R.drawable.wm_button)!!
        successChoiceBtnBg = getDrawable(R.drawable.wm_button_success)!!
        errorChoiceBtnBg = getDrawable(R.drawable.wm_button_error)!!
        defaultChoiceBtnTextColor = getColor(R.color.defaultTextColor)

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
                choiceButton.background = defaultChoiceBtnBg
                choiceButton.setTextColor(defaultChoiceBtnTextColor)
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
                pressedBtn.background = errorChoiceBtnBg
                pressedBtn.setTextColor(Color.WHITE)
                highlightCorrectTranslation(translation)
            }
        }

        private fun highlightCorrectTranslation(translation: String) {
            choiceButtons.find { it.text == translation }
                ?.let {
                    it.background = successChoiceBtnBg
                    it.setTextColor(Color.WHITE)
                }
        }
    }
}
