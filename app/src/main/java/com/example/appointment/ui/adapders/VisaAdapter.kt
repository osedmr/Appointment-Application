package com.example.appointment.ui.adapders

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.data.model.Visa
import com.example.appointment.databinding.VisaCardViewBinding

class VisaAdapter :RecyclerView.Adapter<VisaAdapter.VisaViewHolder>() {

    inner class VisaViewHolder(val binding: VisaCardViewBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBAck = object : DiffUtil.ItemCallback<Visa>(){
        override fun areItemsTheSame(oldItem: Visa, newItem: Visa): Boolean {
            return oldItem.visaTypeId == newItem.visaTypeId
        }

        override fun areContentsTheSame(oldItem: Visa, newItem: Visa): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer<Visa>(this,differCallBAck)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisaViewHolder {
        val binding=VisaCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VisaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisaViewHolder, position: Int) {
        val visa = differ.currentList[position]
        Log.d("VisaAdapter", "Binding visa at position $position: $visa")
        holder.binding.apply {
            tvVisaCategory.text = visa.visaCategory
            tvCountries.text = "${visa.sourceCountry} ➝ ${visa.missionCountry}"
            tvAppointmentDate.text = visa.appointmentDate ?: "No appointment"
            tvCenterName.text = visa.centerName

            btnBookNow.setOnClickListener {
                val context = holder.itemView.context
                val uri = Uri.parse(visa.bookNowLink)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }
        }
    }

}



















