package com.example.currency_converter

import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException



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
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                success_load(response)
            }
        })
    }

    fun update_convert_from_count(new_count:Double) {
        count_from = new_count
        count_to = new_count * coeff
    }

    fun update_convert_to_count(new_count:Double) {
        count_to = new_count
        count_from = new_count / coeff
    }

    fun success_load(response: Response) {
        instance.runOnUiThread(Runnable {
            instance.layout_with_progress_bar.visibility = View.INVISIBLE
            instance.layout_main.visibility = View.VISIBLE
        })
        println(response.body?.string())
    }


}