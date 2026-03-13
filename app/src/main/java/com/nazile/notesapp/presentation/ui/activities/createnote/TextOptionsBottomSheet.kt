package com.nazile.notesapp.presentation.ui.activities.createnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nazile.notesapp.R
import com.nazile.notesapp.databinding.LayoutMisBinding

class TextOptionsBottomSheet() : BottomSheetDialogFragment() {
    private var binding: LayoutMisBinding? = null

    interface TextOptionListener {
        fun onSelectedColor(color: String?)
        fun onAddImage()
        fun onAddUrl()
        fun onExportNote()
        fun onShareNote()
        fun onDeleteNote()
    }

    var listener: TextOptionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LayoutMisBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorSelection()
        binding?.apply {
            layoutAddImage.setOnClickListener {
                listener?.onAddImage()
            }
            layoutAddUrl.setOnClickListener {
                listener?.onAddUrl()
            }
            layoutExport.setOnClickListener {
                listener?.onExportNote()
            }
            layoutShareNote.setOnClickListener {
                listener?.onShareNote()
            }
            layoutDeleteNote.setOnClickListener {
                listener?.onDeleteNote()
            }

        }
    }

    private fun colorSelection() {
        binding?.apply {
            val colorMap = mapOf(
                imageColor1 to "#333333",
                imageColor2 to "#FF9800",
                imageColor3 to "#E040FB",
                imageColor4 to "#03A9F4",
                imageColor5 to "#E91E63",
                imageColor6 to "#00E676",
                imageColor7 to "#795548"
            )

            val colorViews = colorMap.keys

            colorViews.forEach { view ->

                view.setOnClickListener {
                    val selectedColor = colorMap[view]
                    listener?.onSelectedColor(selectedColor)

                    colorViews.forEach {
                        it.setImageResource(0)
                    }

                    view.setImageResource(R.drawable.ic_done)
                }
            }

        }
    }

}