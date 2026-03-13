package com.nazile.notesapp.presentation.ui.activities.createnote

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.databinding.ActivityCreateNoteBinding
import com.nazile.notesapp.presentation.NemosoftsText.NemosoftsEditText
import com.nazile.notesapp.presentation.ui.activities.CreateNoteViewModel
import com.nazile.notesapp.presentation.ui.activities.MainViewModel
import com.nazile.notesapp.utils.showSnackBar
import com.nazile.notesapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CreateNoteActivity : AppCompatActivity(), TextOptionsBottomSheet.TextOptionListener {
    private var setectedImagePath: String? = null
    var selectedNoteColor: String? = "#424141"
    private var dialogAddURL: AlertDialog? = null
    private var dialogDeletNote: AlertDialog? = null
    private var dialogExport: AlertDialog? = null

    private var alreadyAvailableNote: Note? = null
    private var generator = 0

    private val mBinding by lazy {
        ActivityCreateNoteBinding.inflate(layoutInflater)
    }
    private val noteViewModel: CreateNoteViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)


        mBinding.createTextDeteTime.setText(
            SimpleDateFormat("EEEE , dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())
        )


        if (intent.getBooleanExtra("isViemOrUpdate", false)) {
            alreadyAvailableNote = intent.getSerializableExtra("note") as Note?
            setViewOrUpdateNote()
        }


//        initMiscellaneous()
        setClickListeners()

    }

    private fun setClickListeners() {
        mBinding.backImg.setOnClickListener {
            finish()
        }
        findViewById<View?>(R.id.create_imageRemoveWebURL)?.setOnClickListener {
            mBinding.createTextWebURL.text = null
            mBinding.createLayoutWebURL.visibility = View.GONE
        }

        findViewById<View?>(R.id.create_imageRemoveImage)!!.setOnClickListener {
            mBinding.createImageNote.setImageBitmap(null)
            mBinding.createImageNote.visibility = View.GONE
            mBinding.createImageRemoveImage.visibility = View.GONE
            setectedImagePath = ""
        }
        formattingClickListeners()

        mBinding.bottomSheetView.setOnClickListener {
            val sheet = TextOptionsBottomSheet()
            sheet.listener = this
            sheet.show(supportFragmentManager, "TextOptionsSheet")

        }
        mBinding.saveBtn.setOnClickListener {
            if (mBinding.createInputNote.text.toString().trim { it <= ' ' }.isEmpty()) {
                showSnackBar("Note title can't be empty!", "error")
                return@setOnClickListener
            } else if (mBinding.createInputNoteSubtitle.text.toString().trim { it <= ' ' }
                    .isEmpty()) {
                showSnackBar("Note Subtitle can't be empty!", "error")
                return@setOnClickListener
            } else if (mBinding.createInputNote.text.toString().trim().isEmpty()) {
                showSnackBar("Note can't be empty!", "error")
                return@setOnClickListener
            }
            val title = mBinding.createInputNoteTitle.text.toString()
            val subTitle = mBinding.createInputNoteSubtitle.text.toString()
            val noteText = mBinding.createInputNote.toHtml()
            val dateTime = mBinding.createTextDeteTime.text.toString()
            val noteColor = selectedNoteColor
            val imgPath = setectedImagePath
            val webLink = mBinding.createTextWebURL.text.toString()
            val id = alreadyAvailableNote?.id


            val note = Note(
                id = id ?: 0,
                title = title,
                subtitle = subTitle,
                noteText = noteText,
                dateTime = dateTime,
                color = noteColor,
                imagePath = imgPath,
                webLink = webLink

            )
            lifecycleScope.launch(Dispatchers.IO) {
                noteViewModel.saveNote(note)
            }
            finish()


        }
    }

    private fun formattingClickListeners() {
        mBinding.bold.setOnClickListener {
            mBinding.createInputNote.bold(
                mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_BOLD
                )
            )
        }
        mBinding.bold.setOnLongClickListener {
            Toast.makeText(this@CreateNoteActivity, R.string.toast_bold, Toast.LENGTH_SHORT)
                .show()
            true
        }

        mBinding.italic.setOnClickListener {
            mBinding.createInputNote.italic(
                !mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_ITALIC
                )
            )
        }

        mBinding.italic.setOnLongClickListener {
            Toast.makeText(this@CreateNoteActivity, R.string.toast_italic, Toast.LENGTH_SHORT)
                .show()
            true
        }
        mBinding.underline.setOnClickListener {
            mBinding.createInputNote.underline(
                !mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_UNDERLINED
                )
            )
        }
        mBinding.underline.setOnLongClickListener {
            Toast.makeText(
                this@CreateNoteActivity,
                R.string.toast_underline,
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        mBinding.strikethrough.setOnClickListener {
            mBinding.createInputNote.strikethrough(
                !mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_STRIKETHROUGH
                )
            )
        }
        mBinding.strikethrough.setOnLongClickListener {
            Toast.makeText(
                this@CreateNoteActivity,
                R.string.toast_strikethrough,
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        mBinding.bullet.setOnClickListener {
            mBinding.createInputNote.bullet(
                !mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_BULLET
                )
            )
        }
        mBinding.bullet.setOnLongClickListener {
            Toast.makeText(this@CreateNoteActivity, R.string.toast_bullet, Toast.LENGTH_SHORT)
                .show()
            true
        }


        mBinding.quote.setOnClickListener {
            mBinding.createInputNote.quote(
                !mBinding.createInputNote.contains(
                    NemosoftsEditText.FORMAT_QUOTE
                )
            )
        }

        mBinding.quote.setOnLongClickListener {
            Toast.makeText(this@CreateNoteActivity, R.string.toast_quote, Toast.LENGTH_SHORT)
                .show()
            true
        }

        mBinding.clear.setOnClickListener { mBinding.createInputNote.clearFormats() }

        mBinding.clear.setOnLongClickListener {
            Toast.makeText(
                this@CreateNoteActivity,
                R.string.toast_format_clear,
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    }

    private fun setViewOrUpdateNote() {
        setectedImagePath = alreadyAvailableNote?.imagePath

        mBinding.createInputNoteTitle.setText(alreadyAvailableNote?.title)
        mBinding.createInputNoteSubtitle.setText(alreadyAvailableNote?.subtitle)
        mBinding.createInputNote.fromHtml(alreadyAvailableNote?.noteText)
        mBinding.createTextDeteTime.text = alreadyAvailableNote?.dateTime
        mBinding.createImageNote.setImageURI(selectedNoteColor?.toUri())

        if (alreadyAvailableNote?.imagePath != null && setectedImagePath?.isNotEmpty() == true) {
            Log.d("NoteApp", "setViewOrUpdateNote: $setectedImagePath")
            mBinding.createImageNote.load(setectedImagePath) {
                crossfade(true)
            }
            mBinding.createImageNote.visibility = View.VISIBLE
            mBinding.createImageRemoveImage.visibility = View.VISIBLE
            setectedImagePath = alreadyAvailableNote?.imagePath
        }

        if (alreadyAvailableNote?.webLink != null && !alreadyAvailableNote?.webLink!!.trim { it <= ' ' }
                .isEmpty()) {
            mBinding.createTextWebURL.text = alreadyAvailableNote?.webLink
            mBinding.createLayoutWebURL.visibility = View.VISIBLE
        }
    }

    fun getBitmapFromUri(uri: Uri?): Bitmap? {
        return try {
            val inputStream = uri?.let { contentResolver.openInputStream(it) }
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showDeletNoteDialog() {
        if (dialogDeletNote == null) {
            val builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                R.layout.layout_delete_note_move,
                findViewById<View?>(R.id.layoutDeleteNoteContainer) as ViewGroup?
            )
            builder.setView(view)
            dialogDeletNote = builder.create()
            if (dialogDeletNote!!.getWindow() != null) {
                dialogDeletNote!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
            }
            view.findViewById<View?>(R.id.textDeleteNote)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        @SuppressLint("StaticFieldLeak")
                        class DeleteNoteTask : AsyncTask<Void?, Void?, Void?>() {
                            override fun onPreExecute() {
                                super.onPreExecute()
                                deleteSaveNote()
                            }

                            override fun doInBackground(vararg voids: Void?): Void? {
//                            NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao()
//                                    .deletNote(alreadyAvailableNote);
                                return null
                            }

                            override fun onPostExecute(aVoid: Void?) {
                                super.onPostExecute(aVoid)
                                val intent = Intent()
                                intent.putExtra("isNoteDeleted", true)
                                setResult(RESULT_OK, intent)
                                dialogDeletNote!!.dismiss()
                                finish()
                            }
                        }

                        DeleteNoteTask().execute()
                    }
                })

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialogDeletNote!!.dismiss()
                    }
                })
        }

        dialogDeletNote!!.show()
    }

    private fun setSubtitleIndicatorColor(noteColor: String?) {
        selectedNoteColor = noteColor
        mBinding.createViewSubtitleIndicator.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(noteColor))

    }

    private fun deleteSaveNote() {
        if (mBinding.createInputNoteTitle.text.toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
            return
        } else if (mBinding.createInputNoteSubtitle.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note Subtitle can't be empty!", "error")
            return
        } else if (mBinding.createInputNote.text.toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note can't be empty!", "error")
            return
        }

        val note = Note()
        note.title = mBinding.createInputNoteTitle.getText().toString()
        note.subtitle = mBinding.createInputNoteSubtitle.getText().toString()
        note.noteText = mBinding.createInputNote!!.toHtml()
        note.dateTime = mBinding.createTextDeteTime.getText().toString()
        note.color = selectedNoteColor
        note.imagePath = setectedImagePath

        if (mBinding.createLayoutWebURL.getVisibility() == View.VISIBLE) {
            note.webLink = mBinding.createTextWebURL!!.getText().toString()
        }

        if (alreadyAvailableNote != null) {
            note.id = alreadyAvailableNote!!.id
        }

        @SuppressLint("StaticFieldLeak")
        class DeleteSaveNoteTask : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg voids: Void?): Void? {
//                DeleteDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
            }
        }
        DeleteSaveNoteTask().execute()
    }

    /* @Override
    public void onBackPressed() {
        if (bottomSheetBehavior!=null && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (alreadyAvailableNote == null){
            if (inpuNoteTitle.getText().toString().trim().isEmpty()){
                super.onBackPressed();
            }else  if (inpuNoteSubtitle.getText().toString().trim().isEmpty()){
                super.onBackPressed();
            }else if (mBinding.createInputNote.getText().toString().trim().isEmpty()){
                super.onBackPressed();
            }else {
                saveNoteOnBackPressed();
            }
        }else {
            super.onBackPressed();
        }
    }*/
    private fun saveNoteOnBackPressed() {
        val note = Note()
        note.title = mBinding.createInputNoteTitle.getText().toString()
        note.subtitle = mBinding.createInputNoteSubtitle!!.getText().toString()
        note.noteText = mBinding.createInputNote!!.toHtml()
        note.dateTime = mBinding.createTextDeteTime!!.getText().toString()
        note.color = selectedNoteColor
        note.imagePath = setectedImagePath

        if (mBinding.createTextWebURL!!.getVisibility() == View.VISIBLE) {
            note.webLink = mBinding.createTextWebURL!!.getText().toString()
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg voids: Void?): Void? {
//                NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        SaveNoteTask().execute()
    }


    override fun onSelectedColor(color: String?) {
        selectedNoteColor = color
        setSubtitleIndicatorColor(selectedNoteColor)
    }

    val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                try {
                    lifecycleScope.launch {
                        val uriDeferred = async {
                            saveImageToDownloads(uri)
                        }
                        val savedUri = uriDeferred.await()
                        mBinding.createImageNote.visibility = View.VISIBLE
                        findViewById<View?>(R.id.create_imageRemoveImage)?.visibility = View.VISIBLE
                        setectedImagePath = savedUri
                        mBinding.createImageNote.setImageURI(savedUri?.toUri())
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
//                    imageView.setImageURI(uri)
            }
        }

    override fun onAddImage() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onAddUrl() {
        showAddURLDialog()
    }

    override fun onExportNote() {
        showExportDialog()
    }

    override fun onShareNote() {
        shareNote()
    }

    override fun onDeleteNote() {
        lifecycleScope.launch(Dispatchers.IO) {
            alreadyAvailableNote?.let {
                mainViewModel.deleteNote(it)
                finish()
            }
        }
    }

    /**   export file methods **/
    private fun showExportDialog() {
        if (dialogExport == null) {
            val builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                R.layout.layout_export_file, findViewById<View?>(R.id.ll_layoutExport) as ViewGroup?
            )
            builder.setView(view)
            dialogExport = builder.create()
            if (dialogExport?.window != null) {
                dialogExport?.window!!.setBackgroundDrawable(0.toDrawable())
            }
            view.findViewById<View?>(R.id.ll_export_image_file)!!
                .setOnClickListener {
                    dialogExport!!.dismiss()
                    exportNoteAsImage()
                }

            view.findViewById<View?>(R.id.ll_export_txt_file)!!
                .setOnClickListener {
                    dialogExport?.dismiss()
                    exportNoteAsText()
                }

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener { dialogExport!!.dismiss() }
        }

        dialogExport?.show()
    }

    private fun exportNoteAsImage() {
        val bitmap: Bitmap = viewToBitmap(mBinding.llScroll)
        saveImageToDownloads(bitmap)
    }

    private fun saveImageToDownloads(uri: Uri): String? {

        val resolver = contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
            )
        }

        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        return imageUri?.let { targetUri ->
            resolver.openOutputStream(targetUri)?.use { outputStream ->
                resolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            // Return the actual file path or URI string to save in your Room Database
            targetUri.toString()
        }
    }
    private fun saveImageToDownloads(bitmap: Bitmap) {

        val resolver = contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
            )
        }

        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let { uri ->

            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            openGeneratedJPG(uri)
            Toast.makeText(this, "Image saved to Pictures", Toast.LENGTH_SHORT).show()

        } ?: run {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun openGeneratedJPG(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "No application available to view image",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun exportNoteAsText() {
        val textTitle = mBinding.createInputNoteTitle.text.toString()
        val textSubtitle = mBinding.createInputNoteSubtitle.text.toString()
        val textNote = mBinding.createInputNote.text.toString()

        val text = """
        $textTitle
        
        $textSubtitle
        
        $textNote
    """.trimIndent()

        val fileName = "notes_${System.currentTimeMillis()}.txt"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // Android 10+
            val resolver = contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/" + getString(R.string.app_name)
                )
            }

            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(text.toByteArray())
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "text/plain")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(intent)
                Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_SHORT).show()
            }

        } else {

            // Android 9 and below
            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString() + "/" + getString(R.string.app_name)

            val dir = File(path)
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, fileName)

            try {
                val out = FileOutputStream(file)
                out.write(text.toByteArray())
                out.flush()
                out.close()

                Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**   export file methods ends **/
    private fun shareNote() {
        if (mBinding.createInputNoteTitle.text.toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
        } else if (mBinding.createInputNote.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note can't be empty!", "error")
        } else {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                mBinding.createInputNoteTitle.text.toString() + "\n\n" +
                        mBinding.createTextDeteTime.text.toString() + "\n\n" +
                        mBinding.createInputNoteSubtitle.text.toString() + "\n\n" +
                        Html.fromHtml(mBinding.createInputNote.toHtml()) + "\n" +
                        "https://play.google.com/store/apps/details?id=" + packageName
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    /**   Add URL methods **/
    private fun showAddURLDialog() {
        if (dialogAddURL == null) {
            val builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                R.layout.layout_add_url,
                findViewById<View?>(R.id.layoutAddUrlContiner) as ViewGroup?
            )
            builder.setView(view)

            dialogAddURL = builder.create()
            if (dialogAddURL?.window != null) {
                dialogAddURL?.window?.setBackgroundDrawable(0.toDrawable())
            }

            val inputURL = view.findViewById<EditText>(R.id.inputUrl)
            inputURL.requestFocus()

            view.findViewById<View?>(R.id.textAdd)
                ?.setOnClickListener {
                    if (inputURL.text.toString().trim { it <= ' ' }.isEmpty()) {
                        this@CreateNoteActivity.toast("Enter URL")
                    } else if (!Patterns.WEB_URL.matcher(inputURL.text.toString())
                            .matches()
                    ) {
                        this@CreateNoteActivity.toast("Enter Valid URL")
                    } else {
                        mBinding.createTextWebURL.text = inputURL.text.toString()
                        mBinding.createLayoutWebURL.visibility = View.VISIBLE
                        dialogAddURL!!.dismiss()
                    }
                }

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener { dialogAddURL?.dismiss() }
        }
        dialogAddURL?.show()
    }
}