package com.example.currency_converter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View.OnFocusChangeListener

enum class Master {
    NO_ONE,
    CONVERT_FROM,
    CONVERT_TO
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eT_convert_from.addTextChangedListener(change_editText_convert_from)
        eT_convert_to.addTextChangedListener(change_editText_convert_to)

        eT_convert_from.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> master = Master.CONVERT_FROM
                false -> master = Master.NO_ONE
            }
        }

        eT_convert_to.setOnFocusChangeListener{ _, hasFocus ->
            when (hasFocus) {
                true -> master = Master.CONVERT_TO
                false -> master = Master.NO_ONE
            }
        }

    }

    // Переменная отвечает за то - какой edit_text сейчас ведущий
    protected var master: Master = Master.NO_ONE;

    // Листенер на изменения в Конвертировать из
    protected var change_editText_convert_from: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (master == Master.CONVERT_FROM) {
                eT_convert_to.setText(s)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    // Листенер на изменения в Конвертировать в
    protected var change_editText_convert_to: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (master == Master.CONVERT_TO) {
                eT_convert_from.setText(s)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }
}

