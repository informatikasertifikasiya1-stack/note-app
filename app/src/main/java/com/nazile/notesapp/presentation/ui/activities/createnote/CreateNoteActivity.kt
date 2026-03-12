package com.nazile.notesapp.presentation.ui.activities.createnote

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Html
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.databinding.ActivityCreateNoteBinding
import com.nazile.notesapp.presentation.NemosoftsText.NemosoftsEditText
import com.nazile.notesapp.presentation.ui.activities.CreateNoteViewModel
import com.nazile.notesapp.utils.showSnackBar
import com.nazile.notesapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setTitle(R.string.app_name)


        mBinding.createInputNote.setSelection(mBinding.createInputNote.editableText.length)

        mBinding.createTextDeteTime.setText(
            SimpleDateFormat("EEEE , dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())
        )

        setectedImagePath = ""

        if (intent.getBooleanExtra("isViemOrUpdate", false)) {
            alreadyAvailableNote = intent.getSerializableExtra("note") as Note?
            setViewOrUpdateNote()
        }


//        initMiscellaneous()
        setClickListeners()

    }

    private fun setClickListeners() {

        findViewById<View?>(R.id.create_imageRemoveWebURL)!!.setOnClickListener {
            mBinding.createTextWebURL!!.setText(null)
            mBinding.createLayoutWebURL!!.setVisibility(View.GONE)
        }

        findViewById<View?>(R.id.create_imageRemoveImage)!!.setOnClickListener {
            mBinding.createImageNote.setImageBitmap(null)
            mBinding.createImageNote.setVisibility(View.GONE)
            mBinding.createImageRemoveImage.setVisibility(View.GONE)
            setectedImagePath = ""
        }
        formattingClickListeners()

        mBinding.textMiscellaneous.setOnClickListener {
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
            val webLink = mBinding.createTextWebURL.toString()
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
        mBinding.createInputNoteTitle.setText(alreadyAvailableNote!!.title)
        mBinding.createInputNoteTitle.setText(alreadyAvailableNote!!.subtitle)
        mBinding.createInputNote.fromHtml(alreadyAvailableNote!!.noteText)
        mBinding.createTextDeteTime.setText(alreadyAvailableNote!!.dateTime)

        if (alreadyAvailableNote!!.imagePath != null && !alreadyAvailableNote!!.imagePath!!.trim { it <= ' ' }
                .isEmpty()) {
            mBinding.createImageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote!!.imagePath))
            mBinding.createImageNote.setVisibility(View.VISIBLE)
            findViewById<View?>(R.id.create_imageRemoveImage)!!.setVisibility(View.VISIBLE)
            setectedImagePath = alreadyAvailableNote!!.imagePath
        }

        if (alreadyAvailableNote!!.webLink != null && !alreadyAvailableNote!!.webLink!!.trim { it <= ' ' }
                .isEmpty()) {
            mBinding.createTextWebURL.setText(alreadyAvailableNote!!.webLink)
            mBinding.createLayoutWebURL.setVisibility(View.VISIBLE)
        }
    }

    private fun saveNote() {
        if (mBinding.createInputNoteTitle.text.toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
            return
        } else if (mBinding.createInputNoteTitle.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note Subtitle can't be empty!", "error")
            return
        } else if (mBinding.createInputNote.text.toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note can't be empty!", "error")
            return
        }

        val note = Note()
        note.title = mBinding.createInputNoteTitle.text.toString()
        note.subtitle = mBinding.createInputNoteTitle.text.toString()
        note.noteText = mBinding.createInputNote.toHtml()
        note.dateTime = mBinding.createTextDeteTime.text.toString()
        note.color = selectedNoteColor
        note.imagePath = setectedImagePath

        if (mBinding.createLayoutWebURL.getVisibility() == View.VISIBLE) {
            note.webLink = mBinding.createTextWebURL.getText().toString()
        }

        if (alreadyAvailableNote != null) {
            note.id = alreadyAvailableNote!!.id
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

    /*  private fun initMiscellaneous() {
          linearMiscellaneous!!.findViewById<View?>(R.id.textMiscellaneous)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      if (bottomSheetBehavior!!.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                          bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                      } else {
                          bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                      }
                  }
              })

          val imageView1 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor1)
          val imageView2 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor2)
          val imageView3 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor3)
          val imageView4 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor4)
          val imageView5 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor5)
          val imageView6 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor6)
          val imageView7 = linearMiscellaneous!!.findViewById<ImageView>(R.id.imageColor7)

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor1)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#333333"
                      imageView1.setImageResource(R.drawable.ic_done)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor2)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#fdbe3b"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(R.drawable.ic_done)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor3)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#E040FB"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(R.drawable.ic_done)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor4)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#3a52fc"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(R.drawable.ic_done)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor5)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#F50057"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(R.drawable.ic_done)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor6)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#00E676"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(R.drawable.ic_done)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.imageColor7)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      setectedNoteColor = "#FF3D00"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(R.drawable.ic_done)
                      setSubtitleIndicatorColor()
                  }
              })

          if (alreadyAvailableNote != null && alreadyAvailableNote!!.color != null && !alreadyAvailableNote!!.color!!.trim { it <= ' ' }
                  .isEmpty()) {
              when (alreadyAvailableNote!!.color) {
                  "#333333" -> {
                      setectedNoteColor = "#333333"
                      imageView1.setImageResource(R.drawable.ic_done)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#fdbe3b" -> {
                      setectedNoteColor = "#fdbe3b"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(R.drawable.ic_done)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#E040FB" -> {
                      setectedNoteColor = "#E040FB"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(R.drawable.ic_done)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#3a52fc" -> {
                      setectedNoteColor = "#3a52fc"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(R.drawable.ic_done)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#F50057" -> {
                      setectedNoteColor = "#F50057"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(R.drawable.ic_done)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#00E676" -> {
                      setectedNoteColor = "#00E676"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(R.drawable.ic_done)
                      imageView7.setImageResource(0)
                      setSubtitleIndicatorColor()
                  }

                  "#FF3D00" -> {
                      setectedNoteColor = "#FF3D00"
                      imageView1.setImageResource(0)
                      imageView2.setImageResource(0)
                      imageView3.setImageResource(0)
                      imageView4.setImageResource(0)
                      imageView5.setImageResource(0)
                      imageView6.setImageResource(0)
                      imageView7.setImageResource(R.drawable.ic_done)
                      setSubtitleIndicatorColor()
                  }
              }
          }

          linearMiscellaneous!!.findViewById<View?>(R.id.layoutAddImage)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                      if (ContextCompat.checkSelfPermission(
                              getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                          ) != PackageManager.PERMISSION_GRANTED
                      ) {
                          ActivityCompat.requestPermissions(
                              this@CreateNoteActivity,
                              arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                              REQUST_CODE_STORAGE_PERMISSION
                          )
                      } else {
                          selectImage()
                      }
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.layoutAddUrl)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                      showAddURLDialog()
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.layoutExport)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                      if (ContextCompat.checkSelfPermission(
                              getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                          ) != PackageManager.PERMISSION_GRANTED
                      ) {
                          ActivityCompat.requestPermissions(
                              this@CreateNoteActivity,
                              arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                              REQUST_CODE_STORAGE_PERMISSION
                          )
                      } else {
                          showExportDialog()
                      }
                  }
              })

          linearMiscellaneous!!.findViewById<View?>(R.id.layoutShareNote)!!
              .setOnClickListener(object : View.OnClickListener {
                  override fun onClick(view: View?) {
                      bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                      if (inpuNoteTitle!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
                          this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
                      } else if (inpuNoteSubtitle!!.getText().toString().trim { it <= ' ' }
                              .isEmpty()) {
                          this@CreateNoteActivity.showSnackBar("Note can't be empty!", "error")
                      } else {
                          val sendIntent = Intent()
                          sendIntent.setAction(Intent.ACTION_SEND)
                          sendIntent.putExtra(
                              Intent.EXTRA_TEXT,
                              inpuNoteTitle!!.getText().toString() + "\n\n" +
                                      textDeteTime!!.getText().toString() + "\n\n" +
                                      inpuNoteSubtitle!!.getText().toString() + "\n\n" +
                                      Html.fromHtml(mBinding.createInputNote!!.toHtml()) + "\n" +
                                      "https://play.google.com/store/apps/details?id=" + getPackageName()
                          )
                          sendIntent.setType("text/plain")
                          startActivity(sendIntent)
                      }
                  }
              })

          if (alreadyAvailableNote != null) {
              linearMiscellaneous!!.findViewById<View?>(R.id.layoutDeleteNote)!!
                  .setVisibility(View.VISIBLE)
              linearMiscellaneous!!.findViewById<View?>(R.id.layoutDeleteNote)!!
                  .setOnClickListener(object : View.OnClickListener {
                      override fun onClick(view: View?) {
                          bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                          showDeletNoteDialog()
                      }
                  })
          }
      }*/


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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUST_CODE_STORAGE_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun getPathFromUri(uri: Uri?): String? {
        try {
            var filePath: String? = ""
            val wholeID = DocumentsContract.getDocumentId(uri)

            val id: String? =
                wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val column = arrayOf<String?>(MediaStore.Images.Media.DATA)
            val sel = MediaStore.Images.Media._ID + "=?"

            val cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf<String?>(id), null
            )

            val columnIndex = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            if (uri == null) {
                return null
            }
            val projection = arrayOf<String?>(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(uri, projection, null, null, null)
            if (cursor != null) {
                val column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val returnn = cursor.getString(column_index)
                cursor.close()
                return returnn
            }
            return uri.getPath()
        }
    }


    private fun deleteSaveNote() {
        if (mBinding.createInputNoteTitle!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
            return
        } else if (mBinding.createInputNoteSubtitle!!.getText().toString().trim { it <= ' ' }
                .isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note Subtitle can't be empty!", "error")
            return
        } else if (mBinding.createInputNote!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
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

                    mBinding.createImageNote.setImageURI(uri)
                    mBinding.createImageNote.visibility = View.VISIBLE
                    findViewById<View?>(R.id.create_imageRemoveImage)?.visibility = View.VISIBLE
                    setectedImagePath = uri.path
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
                    saveImage()
                }

            view.findViewById<View?>(R.id.ll_export_txt_file)!!
                .setOnClickListener {
                    dialogExport?.dismiss()
                    saveResults()
                }

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener { dialogExport!!.dismiss() }
        }

        dialogExport?.show()
    }

    private fun saveImage() {
        val bitmap: Bitmap = viewToBitmap(mBinding.llScroll)
        saveImageToDownloads(bitmap)
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
            Toast.makeText(this, "Image saved to Downloads", Toast.LENGTH_SHORT).show()

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

    fun saveResults() {
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

    companion object {
        private const val REQUST_CODE_STORAGE_PERMISSION = 1
        private const val REQUST_CODE_SELECT_IMAGE = 2
    }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = permissions.entries.all { it.value }

            if (granted) {
//                openImagePicker()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    fun requestStoragePermission() {

        val permissions = when {

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

        storagePermissionLauncher.launch(permissions)
    }

    fun hasStoragePermission(): Boolean {

        return when {

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }

            else -> {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}