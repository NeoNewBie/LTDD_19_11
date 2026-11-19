package com.example.ltdd_19_11.data.repository

import com.example.ltdd_19_11.data.model.SortOption
import com.example.ltdd_19_11.data.model.Student
import com.example.ltdd_19_11.data.model.StudentStats
import java.util.UUID

class StudentRepository {

    private val studentList = mutableListOf(
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV001",
            name = "Nguyễn Văn An",
            major = "Công nghệ Thông tin",
            gpa = 3.8
        ),
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV002",
            name = "Trần Thị Bình",
            major = "Khoa học Máy tính",
            gpa = 3.4
        ),
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV003",
            name = "Lê Hoàng Cường",
            major = "Kỹ thuật Phần mềm",
            gpa = 2.9
        ),
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV004",
            name = "Phạm Minh Đức",
            major = "Công nghệ Thông tin",
            gpa = 3.9
        ),
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV005",
            name = "Vũ Thị Giang",
            major = "Hệ thống Thông tin",
            gpa = 2.2
        ),
        Student(
            id = UUID.randomUUID().toString(),
            mssv = "SV006",
            name = "Đặng Quang Hải",
            major = "Kỹ thuật Phần mềm",
            gpa = 3.6
        )
    )

    fun getAllStudents(): List<Student> {
        return studentList.toList()
    }

    fun getAvailableMajors(): List<String> {
        val majors = studentList.map { it.major }.distinct().sorted().toMutableList()
        majors.add(0, "Tất cả")
        return majors
    }

    fun isMssvExists(mssv: String, excludeId: String? = null): Boolean {
        return studentList.any { it.mssv.equals(mssv, ignoreCase = true) && it.id != excludeId }
    }

    fun addStudent(student: Student): Boolean {
        if (isMssvExists(student.mssv)) {
            return false
        }
        studentList.add(0, student)
        return true
    }

    fun updateStudent(student: Student): Boolean {
        val index = studentList.indexOfFirst { it.id == student.id }
        if (index != -1) {
            studentList[index] = student
            return true
        }
        return false
    }

    fun deleteStudent(id: String): Boolean {
        return studentList.removeIf { it.id == id }
    }

    fun getFilteredStudents(
        query: String = "",
        majorFilter: String = "Tất cả",
        sortOption: SortOption = SortOption.NAME_ASC
    ): List<Student> {
        var filtered = studentList.filter { student ->
            val matchesQuery = query.isBlank() ||
                    student.name.contains(query, ignoreCase = true) ||
                    student.mssv.contains(query, ignoreCase = true)

            val matchesMajor = majorFilter == "Tất cả" || student.major == majorFilter

            matchesQuery && matchesMajor
        }

        filtered = when (sortOption) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.name }
            SortOption.GPA_DESC -> filtered.sortedByDescending { it.gpa }
            SortOption.GPA_ASC -> filtered.sortedBy { it.gpa }
        }

        return filtered
    }

    fun getStats(students: List<Student> = studentList): StudentStats {
        if (students.isEmpty()) {
            return StudentStats(0, 0.0, 0)
        }
        val total = students.size
        val avgGpa = students.map { it.gpa }.average()
        val excellentCount = students.count { it.gpa >= 3.2 }

        val roundedAvg = kotlin.math.round(avgGpa * 100) / 100.0
        return StudentStats(
            totalCount = total,
            averageGpa = roundedAvg,
            excellentCount = excellentCount
        )
    }
}
