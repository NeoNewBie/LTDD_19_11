package com.example.ltdd_19_11.data.model

import com.example.ltdd_19_11.R

data class Student(
    val id: String,
    val mssv: String,
    val name: String,
    val major: String,
    val gpa: Double
) {
    val academicPerformance: String
        get() = when {
            gpa >= 3.6 -> "Xuất sắc"
            gpa >= 3.2 -> "Giỏi"
            gpa >= 2.5 -> "Khá"
            gpa >= 2.0 -> "Trung bình"
            else -> "Yếu"
        }

    val badgeBgColorResId: Int
        get() = when {
            gpa >= 3.6 -> R.color.grade_excellent_bg
            gpa >= 3.2 -> R.color.grade_good_bg
            gpa >= 2.5 -> R.color.grade_fair_bg
            gpa >= 2.0 -> R.color.grade_average_bg
            else -> R.color.grade_poor_bg
        }

    val badgeTextColorResId: Int
        get() = when {
            gpa >= 3.6 -> R.color.grade_excellent_text
            gpa >= 3.2 -> R.color.grade_good_text
            gpa >= 2.5 -> R.color.grade_fair_text
            gpa >= 2.0 -> R.color.grade_average_text
            else -> R.color.grade_poor_text
        }
}
