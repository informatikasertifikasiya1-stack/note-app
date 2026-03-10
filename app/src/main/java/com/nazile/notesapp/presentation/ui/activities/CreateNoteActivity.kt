package com.nazile.notesapp.presentation.ui.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nazile.notesapp.R
import com.nazile.notesapp.data.models.Note
import com.nazile.notesapp.data.prefs.Setting.Dark_Mode
import com.nazile.notesapp.databinding.ActivityCreateNoteBinding
import com.nazile.notesapp.presentation.NemosoftsText.NemosoftsEditText
import com.nazile.notesapp.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

@AndroidEntryPoint
class CreateNoteActivity : AppCompatActivity() {
    private var setectedImagePath: String? = null
    private var setectedNoteColor: String? = null
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


        mBinding.createInputNote.setSelection(mBinding.createInputNote.getEditableText().length)

        mBinding.createTextDeteTime.setText(
            SimpleDateFormat("EEEE , dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())
        )

        setectedNoteColor = "#333333"
        setectedImagePath = ""

        if (getIntent().getBooleanExtra("isViemOrUpdate", false)) {
            alreadyAvailableNote = getIntent().getSerializableExtra("note") as Note?
            setViewOrUpdateNote()
        }

        findViewById<View?>(R.id.create_imageRemoveWebURL)!!.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View?) {
                mBinding.createTextWebURL!!.setText(null)
                mBinding.createLayoutWebURL!!.setVisibility(View.GONE)
            }
        })

        findViewById<View?>(R.id.create_imageRemoveImage)!!.setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View?) {
                mBinding.createImageNote.setImageBitmap(null)
                mBinding.createImageNote.setVisibility(View.GONE)
                mBinding.createImageRemoveImage.setVisibility(View.GONE)
                setectedImagePath = ""
            }
        })

//        initMiscellaneous()
        setSubtitleIndicatorColor()
        setClickListeners()

        mBinding.bold.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.bold(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_BOLD
                    )
                )
            }
        })
        mBinding.bold!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(this@CreateNoteActivity, R.string.toast_bold, Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        })

        mBinding.italic!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.italic(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_ITALIC
                    )
                )
            }
        })

        mBinding.italic!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(this@CreateNoteActivity, R.string.toast_italic, Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        })
        mBinding.underline!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.underline(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_UNDERLINED
                    )
                )
            }
        })
        mBinding.underline!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(
                    this@CreateNoteActivity,
                    R.string.toast_underline,
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        })
        mBinding.strikethrough!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.strikethrough(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_STRIKETHROUGH
                    )
                )
            }
        })
        mBinding.strikethrough!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(
                    this@CreateNoteActivity,
                    R.string.toast_strikethrough,
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        })

        mBinding.bullet!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.bullet(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_BULLET
                    )
                )
            }
        })
        mBinding.bullet!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(this@CreateNoteActivity, R.string.toast_bullet, Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        })


        mBinding.quote!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.quote(
                    !mBinding.createInputNote!!.contains(
                        NemosoftsEditText.FORMAT_QUOTE
                    )
                )
            }
        })

        mBinding.quote!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(this@CreateNoteActivity, R.string.toast_quote, Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        })

        mBinding.clear!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mBinding.createInputNote!!.clearFormats()
            }
        })

        mBinding.clear!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(
                    this@CreateNoteActivity,
                    R.string.toast_format_clear,
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        })
    }

    private fun setClickListeners() {
        mBinding.saveBtn.setOnClickListener {

            if (mBinding.createInputNote.getText().toString().trim { it <= ' ' }.isEmpty()) {
                showSnackBar("Note title can't be empty!", "error")
                return@setOnClickListener
            } else if (mBinding.createInputNoteSubtitle.getText().toString().trim { it <= ' ' }
                    .isEmpty()) {
                showSnackBar("Note Subtitle can't be empty!", "error")
                return@setOnClickListener
            } else if (mBinding.createInputNote.getText().toString().trim().isEmpty()) {
                showSnackBar("Note can't be empty!", "error")
                return@setOnClickListener
            }
            val title = mBinding.createInputNoteTitle.text.toString()
            val subTitle = mBinding.createInputNoteSubtitle.text.toString()
            val noteText = mBinding.createInputNote.toHtml()
            val dateTime = mBinding.createTextDeteTime.text.toString()
            val noteColor = setectedNoteColor
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


        }
    }

    private fun setViewOrUpdateNote() {
        mBinding.createInputNoteTitle!!.setText(alreadyAvailableNote!!.title)
        mBinding.createInputNoteTitle!!.setText(alreadyAvailableNote!!.subtitle)
        mBinding.createInputNote!!.fromHtml(alreadyAvailableNote!!.noteText)
        mBinding.createTextDeteTime!!.setText(alreadyAvailableNote!!.dateTime)

        if (alreadyAvailableNote!!.imagePath != null && !alreadyAvailableNote!!.imagePath!!.trim { it <= ' ' }
                .isEmpty()) {
            mBinding.createImageNote!!.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote!!.imagePath))
            mBinding.createImageNote!!.setVisibility(View.VISIBLE)
            findViewById<View?>(R.id.create_imageRemoveImage)!!.setVisibility(View.VISIBLE)
            setectedImagePath = alreadyAvailableNote!!.imagePath
        }

        if (alreadyAvailableNote!!.webLink != null && !alreadyAvailableNote!!.webLink!!.trim { it <= ' ' }
                .isEmpty()) {
            mBinding.createTextWebURL!!.setText(alreadyAvailableNote!!.webLink)
            mBinding.createLayoutWebURL!!.setVisibility(View.VISIBLE)
        }
    }

    private fun saveNote() {
        if (mBinding.createInputNoteTitle!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note title can't be empty!", "error")
            return
        } else if (mBinding.createInputNoteTitle.getText().toString().trim { it <= ' ' }
                .isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note Subtitle can't be empty!", "error")
            return
        } else if (mBinding.createInputNote!!.getText().toString().trim { it <= ' ' }.isEmpty()) {
            this@CreateNoteActivity.showSnackBar("Note can't be empty!", "error")
            return
        }

        val note = Note()
        note.title = mBinding.createInputNoteTitle!!.getText().toString()
        note.subtitle = mBinding.createInputNoteTitle!!.getText().toString()
        note.noteText = mBinding.createInputNote!!.toHtml()
        note.dateTime = mBinding.createTextDeteTime!!.getText().toString()
        note.color = setectedNoteColor
        note.imagePath = setectedImagePath

        if (mBinding.createLayoutWebURL!!.getVisibility() == View.VISIBLE) {
            note.webLink = mBinding.createTextWebURL!!.getText().toString()
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

    private fun showExportDialog() {
        if (dialogExport == null) {
            val builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                R.layout.layout_export_file, findViewById<View?>(R.id.ll_layoutExport) as ViewGroup?
            )
            builder.setView(view)
            dialogExport = builder.create()
            if (dialogExport!!.getWindow() != null) {
                dialogExport!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
            }
            view.findViewById<View?>(R.id.ll_export_image_file)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialogExport!!.dismiss()
                        saveImage()
                    }
                })

            view.findViewById<View?>(R.id.ll_export_txt_file)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialogExport!!.dismiss()
                        saveResults()
                    }
                })

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialogExport!!.dismiss()
                    }
                })
        }

        dialogExport!!.show()
    }

    private fun saveImage() {
        val bitmap: Bitmap
        bitmap = viewToBitmap(mBinding.llScroll)
        saveImageToExternalStorage(bitmap)
    }

    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val iconsStoragePath = Environment.getExternalStorageDirectory()
            .toString() + "/" + getString(R.string.app_name)
        val sdIconStorageDir = File(iconsStoragePath)
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdir()
        }
        generator = generatorNumber()
        val fname = "Image_" + generator + ".jpg"
        val file = File(sdIconStorageDir, fname)

        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Image is created!!!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(
            this, arrayOf<String>(file.toString()), null,
            object : OnScanCompletedListener {
                override fun onScanCompleted(path: String?, uri: Uri?) {
                    Log.i("ExternalStorage", "Scanned " + path + ":")
                    Log.i("ExternalStorage", "-> uri=" + uri)
                }
            })

        openGeneratedJPG()
    }

    private fun openGeneratedJPG() {
        val iconsStoragePath = Environment.getExternalStorageDirectory()
            .toString() + "/" + getString(R.string.app_name)
        val sdIconStorageDir = File(iconsStoragePath)
        if (sdIconStorageDir.exists()) {
            val fname = "Image_" + generator + ".jpg"
            val file = File(sdIconStorageDir, fname)
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "image/*")
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this@CreateNoteActivity,
                    "No Application available to view JPG",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generatorNumber(): Int {
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        return n
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

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable =
            mBinding.createViewSubtitleIndicator.getBackground() as GradientDrawable
        when (setectedNoteColor) {
            "#333333" -> if (Dark_Mode) {
                gradientDrawable.setColor(Color.parseColor("#ECECEC"))
            } else {
                gradientDrawable.setColor(Color.parseColor("#121212"))
            }

            else -> gradientDrawable.setColor(Color.parseColor(setectedNoteColor))
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(
                intent,
                getResources().getString(R.string.select_image)
            ), REQUST_CODE_SELECT_IMAGE
        )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.getData()
                if (selectedImageUri != null) {
                    try {
                        val inputStream = getContentResolver().openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        mBinding.createImageNote!!.setImageBitmap(bitmap)
                        mBinding.createImageNote!!.setVisibility(View.VISIBLE)
                        findViewById<View?>(R.id.create_imageRemoveImage)!!.setVisibility(View.VISIBLE)
                        setectedImagePath = getPathFromUri(selectedImageUri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun getPathFromUri(uri: Uri?): String? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
            } else {
                if (uri == null) {
                    return null
                }
                val projection = arrayOf<String?>(MediaStore.Images.Media.DATA)
                val cursor = getContentResolver().query(uri, projection, null, null, null)
                if (cursor != null) {
                    val column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    val retunn = cursor.getString(column_index)
                    cursor.close()
                    return retunn
                }
                return uri.getPath()
            }
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


    private fun showAddURLDialog() {
        if (dialogAddURL == null) {
            val builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                R.layout.layout_add_url,
                findViewById<View?>(R.id.layoutAddUrlContiner) as ViewGroup?
            )
            builder.setView(view)

            dialogAddURL = builder.create()
            if (dialogAddURL!!.getWindow() != null) {
                dialogAddURL!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
            }

            val inputURL = view.findViewById<EditText>(R.id.inputUrl)
            inputURL.requestFocus()

            view.findViewById<View?>(R.id.textAdd)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        if (inputURL.getText().toString().trim { it <= ' ' }.isEmpty()) {
                            Toast.makeText(this@CreateNoteActivity, "Enter URL", Toast.LENGTH_SHORT)
                                .show()
                        } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString())
                                .matches()
                        ) {
                            Toast.makeText(
                                this@CreateNoteActivity,
                                "Enter Valid URL",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mBinding.createTextWebURL!!.setText(inputURL.getText().toString())
                            mBinding.createLayoutWebURL!!.setVisibility(View.VISIBLE)
                            dialogAddURL!!.dismiss()
                        }
                    }
                })

            view.findViewById<View?>(R.id.textCancel)!!
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialogAddURL!!.dismiss()
                    }
                })
        }
        dialogAddURL!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_seve, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

        //        switch (id){
//            case R.id.imageSave :
//                saveNote();
//                break;
//        }
        return super.onOptionsItemSelected(item)
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
        note.color = setectedNoteColor
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
        note.color = setectedNoteColor
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

    fun saveResults() {
        var textNote = " "
        var textTitle = " "
        var textSubtitle = " "

        if (!mBinding.createInputNote!!.getText().toString().isEmpty()) {
            textNote = mBinding.createInputNote!!.getText().toString()
        }
        if (!mBinding.createInputNoteTitle!!.getText().toString().isEmpty()) {
            textTitle = mBinding.createInputNoteTitle!!.getText().toString()
        }
        if (!mBinding.createInputNoteSubtitle!!.getText().toString().isEmpty()) {
            textSubtitle = mBinding.createInputNoteSubtitle!!.getText().toString()
        }

        val text = (textTitle + "\n"
                + "\n"
                + textSubtitle + "\n"
                + "\n"
                + textNote)

        val iconsStoragePath = Environment.getExternalStorageDirectory()
            .toString() + "/" + getString(R.string.app_name)
        val sdIconStorageDir = File(iconsStoragePath)
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdir()
        }
        generator = generatorNumber()
        val fname = "notes_" + generator + ".txt"
        val file = File(sdIconStorageDir, fname)

        try {
            val out = FileOutputStream(file)
            out.write(text.toByteArray())
            mBinding.createInputNote!!.getText().clear()
            out.flush()
            out.close()
            Toast.makeText(this, "txt is created!!!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val REQUST_CODE_STORAGE_PERMISSION = 1
        private const val REQUST_CODE_SELECT_IMAGE = 2
    }
}