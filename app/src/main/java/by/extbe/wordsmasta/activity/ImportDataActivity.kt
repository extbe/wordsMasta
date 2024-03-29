package by.extbe.wordsmasta.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_data)

        findViewById<Button>(R.id.choseFileForImportBtn).setOnClickListener {
            val openFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            openFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
            openFileIntent.type = MIME_TYPE_TEXT_PLAIN
            startActivityForResult(openFileIntent, READ_FILE_REQUEST_CODE)
        }

        val importStatus = findViewById<TextView>(R.id.importStatus)
        importDataViewModel.importStatus.observe(this, Observer {
            importStatus.text = it.description
        })
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
