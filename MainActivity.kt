package com.example.financetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financetracker.data.Transaction
import com.example.financetracker.databinding.ActivityMainBinding
import com.example.financetracker.viewmodel.TransactionViewModel
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter
    private val database = FirebaseDatabase.getInstance().getReference("transactions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ RecyclerView setup
        adapter = TransactionAdapter { transaction ->
            viewModel.delete(transaction)
            database.child(transaction.id ?: "").removeValue()
            Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // ✅ Observe LiveData
        viewModel.allTransactions.observe(this, Observer {
            adapter.submitList(it)
            updateSummary(it)
        })

        viewModel.totalIncome.observe(this, Observer { income ->
            binding.incomeText.text = "Income: ₦${income ?: 0.0}"
        })

        viewModel.totalExpense.observe(this, Observer { expense ->
            binding.expenseText.text = "Expense: ₦${expense ?: 0.0}"
        })

        // ✅ Add new transaction
        binding.addButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val amountText = binding.amountInput.text.toString()
            val isExpense = binding.expenseSwitch.isChecked

            if (title.isNotEmpty() && amountText.isNotEmpty()) {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val id = database.push().key!!
                val type = if (isExpense) "Expense" else "Income"

                val transaction = Transaction(
                    id = id,
                    title = title,
                    amount = amount,
                    type = type,
                    date = System.currentTimeMillis().toString()
                )

                // ✅ Save locally (Room)
                viewModel.insert(transaction)

                // ✅ Save online (Firebase)
                database.child(id).setValue(transaction)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
                    }

                // ✅ Clear input fields
                binding.titleInput.text?.clear()
                binding.amountInput.text?.clear()
                binding.expenseSwitch.isChecked = false
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        binding.incomeText.text = "Income: ₦$totalIncome"
        binding.expenseText.text = "Expense: ₦$totalExpense"
        binding.balanceText.text = "Balance: ₦$balance"
    }
}
