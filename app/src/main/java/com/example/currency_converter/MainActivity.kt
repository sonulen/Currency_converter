package com.example.currency_converter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*

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

        // Создам слушателя который возьмет имя выбранного item и передаст в converter
        currency_from.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                converter.update_currency_from(parent?.getItemAtPosition(position).toString())
            }
        }
        currency_to.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                converter.update_currency_to(parent?.getItemAtPosition(position).toString())
            }
        }

    }

    // Переменная отвечает за то - какой edit_text сейчас ведущий
    protected var master: Master = Master.NO_ONE

    // Переменная отвечает за данные для всего виджета пересчета
    protected var converter = Currency_converter_widget(this)

    // Листенер на изменения в "Конвертировать из"
    protected var change_editText_convert_from: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (master == Master.CONVERT_FROM) {
                if (!s.isEmpty()) {
                    converter.update_convert_from_count(s.toString().toDouble())
                } else {
                    converter.update_convert_from_count(0.0)
                }
            }
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    // Листенер на изменения в "Конвертировать в"
    protected var change_editText_convert_to: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (master == Master.CONVERT_TO) {
                if (!s.isEmpty()) {
                    converter.update_convert_to_count(s.toString().toDouble())
                } else {
                    converter.update_convert_to_count(0.0)
                }
            }
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }
}

