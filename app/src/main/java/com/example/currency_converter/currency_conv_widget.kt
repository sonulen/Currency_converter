package com.example.currency_converter

import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import android.widget.ArrayAdapter


enum class Master {
    NO_ONE,
    CONVERT_FROM,
    CONVERT_TO,
    CURRENCY_FROM,
    CURRENCY_TO,
}


class Currency_converter_widget(instance:MainActivity) {
    var instance:MainActivity
    var count_from:Double = 0.0
    var count_to: Double = 0.0
    var base:String ?= null
    lateinit var rates: Map<String, Double>
    var currency_from: String = "USD"
    var currency_to: String = "RUB"
    var coeff: Double = 0.0

    private val client = OkHttpClient()

    init{
        this.instance = instance
        run("http://data.fixer.io/api/latest?access_key=4353a124be0c1f20ccc43039c18c3d38")
    }

    fun run(url: String) {
        var request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Попробуем еще раз через 1 сек.
                Thread.sleep(1000)
                run("http://data.fixer.io/api/latest?access_key=4353a124be0c1f20ccc43039c18c3d38")
            }
            override fun onResponse(call: Call, response: Response) {
                success_load(response)
            }
        })
    }

    fun update_convert_from_count(new_count:Double) {
        count_from = new_count
        count_to = new_count / coeff
        instance.runOnUiThread(Runnable {
            instance.eT_convert_to.setText(count_to.toString())
        })
    }

    fun update_convert_to_count(new_count:Double) {
        count_to = new_count
        count_from = new_count * coeff
        instance.runOnUiThread(Runnable {
            instance.eT_convert_from.setText(count_from.toString())
        })
    }

    fun update_currency_from(currency:String) {
        currency_from = currency
        update_coeff()
        update_convert_from_count(count_from)
    }

    fun update_currency_to(currency:String) {
        currency_to = currency
        update_coeff()
        update_convert_to_count(count_to)
    }

    fun update_coeff() {
        coeff = rates.getValue(currency_from) / rates.getValue(currency_to)
    }


    fun success_load(response: Response) {
        val gson = Gson()
        // response.body может быть использован только один раз
        var decoded = gson.fromJson(response.body?.string(), ResponseFixerIo::class.java)
        base = decoded.base
        rates = decoded.rates

        instance.runOnUiThread(Runnable {
            instance.layout_with_progress_bar.visibility = View.INVISIBLE
            instance.layout_main.visibility = View.VISIBLE


            // Create an ArrayAdapter using the string array and a default spinner layout
            val adapter = ArrayAdapter<String>(
                instance.baseContext, android.R.layout.simple_spinner_item, ArrayList(rates.keys)
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            instance.currency_from.adapter = adapter
            instance.currency_to.adapter = adapter

            // Установим по дефолту из USD в RUB
            instance.currency_from.setSelection(adapter.getPosition("USD"))
            instance.currency_to.setSelection(adapter.getPosition("RUB"))
            update_coeff()
            update_convert_from_count(count_from)
        })

    }


}