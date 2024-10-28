package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sourceAmountEditText: EditText
    private lateinit var targetAmountEditText: EditText
    private lateinit var sourceCurrencySpinner: Spinner
    private lateinit var targetCurrencySpinner: Spinner
    private lateinit var exchangeRateInfoTextView: TextView
    private lateinit var updateRatesButton: Button

    private val conversionRates = mapOf(
        "USD-EUR" to 0.93, "EUR-USD" to 1.08,
        "USD-GBP" to 0.77, "GBP-USD" to 1.30,
        "USD-JPY" to 110.0, "JPY-USD" to 0.0091,
        "USD-AUD" to 1.4, "AUD-USD" to 0.71,
        "EUR-GBP" to 0.88, "GBP-EUR" to 1.14,
        "EUR-JPY" to 118.0, "JPY-EUR" to 0.0085,
        "EUR-AUD" to 1.51, "AUD-EUR" to 0.66,
        "GBP-JPY" to 135.0, "JPY-GBP" to 0.0074,
        "GBP-AUD" to 1.8, "AUD-GBP" to 0.56,
        "JPY-AUD" to 0.0127, "AUD-JPY" to 79.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceAmountEditText = findViewById(R.id.source_amount)
        targetAmountEditText = findViewById(R.id.target_amount)
        sourceCurrencySpinner = findViewById(R.id.source_currency_spinner)
        targetCurrencySpinner = findViewById(R.id.target_currency_spinner)
        exchangeRateInfoTextView = findViewById(R.id.exchange_rate_info)
        updateRatesButton = findViewById(R.id.update_rates)

        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "AUD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter

        sourceAmountEditText.addTextChangedListener(sourceAmountWatcher)
        targetAmountEditText.addTextChangedListener(targetAmountWatcher)
        sourceCurrencySpinner.onItemSelectedListener = currencyChangeListener
        targetCurrencySpinner.onItemSelectedListener = currencyChangeListener

        updateRatesButton.setOnClickListener {
            Toast.makeText(this, "Rates updated", Toast.LENGTH_SHORT).show()
        }

    }

    private val sourceAmountWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty()) {
                targetAmountEditText.removeTextChangedListener(targetAmountWatcher)
                targetAmountEditText.setText("")
                targetAmountEditText.addTextChangedListener(targetAmountWatcher)
            } else {
                performConversion(reverse = false)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val targetAmountWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty()) {
                sourceAmountEditText.removeTextChangedListener(sourceAmountWatcher)
                sourceAmountEditText.setText("")
                sourceAmountEditText.addTextChangedListener(sourceAmountWatcher)
            } else {
                performConversion(reverse = true)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val currencyChangeListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            performConversion()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun performConversion(reverse: Boolean = false) {
        val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = targetCurrencySpinner.selectedItem.toString()
        val conversionKey = "$sourceCurrency-$targetCurrency"
        val conversionRate = conversionRates[conversionKey] ?: 1.0

        if (reverse) {
            val targetAmountText = targetAmountEditText.text.toString()
            if (targetAmountText.isNotEmpty()) {
                val targetAmount = targetAmountText.toDoubleOrNull() ?: return
                val sourceAmount = targetAmount / conversionRate
                updateAmountFields(String.format("%.2f", sourceAmount), isSource = true)
            }
        } else {
            val sourceAmountText = sourceAmountEditText.text.toString()
            if (sourceAmountText.isNotEmpty()) {
                val sourceAmount = sourceAmountText.toDoubleOrNull() ?: return
                val targetAmount = sourceAmount * conversionRate
                updateAmountFields(String.format("%.2f", targetAmount), isSource = false)
            }
        }

        exchangeRateInfoTextView.text = "1 $sourceCurrency = $conversionRate $targetCurrency"
    }

    private fun updateAmountFields(newAmount: String, isSource: Boolean) {
        if (isSource) {
            sourceAmountEditText.removeTextChangedListener(sourceAmountWatcher)
            sourceAmountEditText.setText(newAmount)
            sourceAmountEditText.addTextChangedListener(sourceAmountWatcher)
        } else {
            targetAmountEditText.removeTextChangedListener(targetAmountWatcher)
            targetAmountEditText.setText(newAmount)
            targetAmountEditText.addTextChangedListener(targetAmountWatcher)
        }
    }
}
