package com.example.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.billing.databinding.ItemWeightEntryBinding
import java.text.DecimalFormat

class WeightListAdapter(
    private val onDeleteClick: (WeightEntry) -> Unit
) : RecyclerView.Adapter<WeightListAdapter.WeightViewHolder>() {

    private var entries: List<WeightEntry> = emptyList()
    private val formatter = DecimalFormat("#0.00")

    fun submitList(list: List<WeightEntry>) {
        entries = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val binding = ItemWeightEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WeightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val entry = entries[position]
        holder.bind(entry)
        holder.itemView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(com.example.billing.R.id.btnDelete).setOnClickListener {
            onDeleteClick(entry)
        }
    }

    override fun getItemCount() = entries.size

    class WeightViewHolder(private val binding: ItemWeightEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: WeightEntry) {
            binding.tvWeight.text = "+ ${DecimalFormat("#0.00").format(entry.weight)} Kg"
        }
    }
}