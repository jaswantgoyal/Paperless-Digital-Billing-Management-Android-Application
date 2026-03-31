package com.example.billing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.billing.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeightViewModel
    private lateinit var adapter: WeightListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeightViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = WeightListAdapter { entry ->
            viewModel.removeWeight(entry)
        }
        binding.recyclerViewWeights.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.entries.observe(this) { entries ->
            adapter.submitList(entries)
        }

        viewModel.total.observe(this) { total ->
            binding.tvTotal.text = String.format(Locale.US, "Total = %.2f Kg", total)
        }
    }

    private fun setupClickListeners() {
        binding.btnAdd.setOnClickListener {
            val input = binding.etWeight.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val weight = input.toDoubleOrNull()
            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Please enter a valid positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addWeight(weight)
            binding.etWeight.text.clear()
        }

        binding.btnPrint.setOnClickListener {
            printReceipt()
        }

        binding.btnSave.setOnClickListener {
            saveReceipt()
        }

        // Optional: Clear all from options menu (improves UX)
//        binding.btnClearAll.setOnClickListener {
//            viewModel.clearAll()
//            Toast.makeText(this, "All entries cleared", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun getReceiptText(): String {
        val entries = viewModel.entries.value ?: emptyList()
        val total = viewModel.total.value ?: 0.0
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val builder = StringBuilder()
        builder.append("================================\n")
        builder.append("        ${getString(R.string.firm_name)}\n")
        builder.append("        ${getString(R.string.firm_address)}\n")
        builder.append("        ${getString(R.string.firm_phone)}\n")
        builder.append("================================\n")
        builder.append("Date: $date\n")
        builder.append("--------------------------------\n")
        builder.append("Items:\n")
        entries.forEachIndexed { index, entry ->
            builder.append(String.format(Locale.US, "%2d. + %.2f Kg\n", index + 1, entry.weight))
        }
        builder.append("--------------------------------\n")
        builder.append(String.format(Locale.US, "TOTAL: %.2f Kg\n", total))
        builder.append("================================\n")
        builder.append("Thank you for your business!\n")
        return builder.toString()
    }

    private fun printReceipt() {
        val receiptText = getReceiptText()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, receiptText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Print / Share Receipt"))
    }

    private fun saveReceipt() {
        val receiptText = getReceiptText()
        val fileName = "receipt_${System.currentTimeMillis()}.txt"
        val file = File(filesDir, fileName)

        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(receiptText.toByteArray())
            }
            Toast.makeText(this, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}