package com.example.ltdd_19_11.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ltdd_19_11.data.model.Student
import com.example.ltdd_19_11.databinding.ItemStudentBinding
import java.util.Locale

class StudentAdapter(
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            val context = binding.root.context

            binding.tvName.text = student.name
            binding.tvMssv.text = "MSSV: ${student.mssv}"
            binding.tvMajor.text = "Ngành: ${student.major}"

            val gpaFormatted = String.format(Locale.US, "%.2f", student.gpa)
            binding.tvGpaBadge.text = "GPA: $gpaFormatted (${student.academicPerformance})"

            val bgColor = ContextCompat.getColor(context, student.badgeBgColorResId)
            val textColor = ContextCompat.getColor(context, student.badgeTextColorResId)

            binding.tvGpaBadge.backgroundTintList = ColorStateList.valueOf(bgColor)
            binding.tvGpaBadge.setTextColor(textColor)

            binding.btnEdit.setOnClickListener {
                onEditClick(student)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(student)
            }
        }
    }

    private class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}
