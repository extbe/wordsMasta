package by.extbe.wordsmasta.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import by.extbe.wordsmasta.R
import by.extbe.wordsmasta.constant.ImportStatus
import by.extbe.wordsmasta.viewmodel.ImportDataViewModel
import kotlinx.android.synthetic.main.activity_import_data.*

class ImportDataActivity : AppCompatActivity() {
    private companion object {
        const val READ_FILE_REQUEST_CODE = 42
        const val MIME_TYPE_TEXT_PLAIN = "*/*"
    }

    private val importDataViewModel: ImportDataViewModel by viewModels()

    private var defaultTextColor = 0
    private var successColor = 0
    private var errorColor = 0
    private var inProgressColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_data)

        defaultTextColor = getColor(R.color.defaultTextColor)
        successColor = getColor(R.color.colorSuccess)
        errorColor = getColor(R.color.colorError)
        inProgressColor = getColor(R.color.colorInProgress)

        findViewById<Button>(R.id.choseFileForImportBtn).setOnClickListener {
            val openFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            openFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
            openFileIntent.type = MIME_TYPE_TEXT_PLAIN
            startActivityForResult(openFileIntent, READ_FILE_REQUEST_CODE)
        }

        findViewById<ImageButton>(R.id.goBackButton).setOnClickListener { finish() }

        val importStatus = findViewById<TextView>(R.id.importStatus)
        val importProgressBar = findViewById<ProgressBar>(R.id.importProgressBar)
        importDataViewModel.importStatus.observe(this, Observer {
            importStatus.text = it.description
            val importStatusColor = determineImportStatusColor(it)
            importStatus.setTextColor(importStatusColor)
            importProgressBar.progressDrawable.colorFilter =
                PorterDuffColorFilter(importStatusColor, PorterDuff.Mode.SRC_IN)
        })

        importDataViewModel.totalCounter.observe(this, Observer {
            importProgressBar.max = it
        })
        importDataViewModel.importCounter.observe(this, Observer {
            importProgressBar.progress = it
        })
    }

    private fun determineImportStatusColor(importStatus: ImportStatus): Int =
        when (importStatus) {
            ImportStatus.NO_DATA -> defaultTextColor
            ImportStatus.IN_PROGRESS -> inProgressColor
            ImportStatus.COMPLETED -> successColor
            ImportStatus.ERROR -> errorColor
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == READ_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                intent?.data?.let { fileUri ->
                    importDataViewModel.importWordsFromFile(fileUri)
                }
            } catch (e: Exception) {
                importStatus.text = ImportStatus.ERROR.description
                showError(e.message ?: "Unexpected error ${e.javaClass.canonicalName}")
            }

        }
    }

    private fun showError(msg: String) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(true)
            .setPositiveButton("Да") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }
}
