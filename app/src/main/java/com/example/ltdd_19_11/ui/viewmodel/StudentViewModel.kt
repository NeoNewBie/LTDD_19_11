package com.example.ltdd_19_11.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ltdd_19_11.data.model.SortOption
import com.example.ltdd_19_11.data.model.Student
import com.example.ltdd_19_11.data.model.StudentStats
import com.example.ltdd_19_11.data.repository.StudentRepository
import java.util.UUID

class StudentViewModel(
    private val repository: StudentRepository = StudentRepository()
) : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _stats = MutableLiveData<StudentStats>()
    val stats: LiveData<StudentStats> = _stats

    private val _majors = MutableLiveData<List<String>>()
    val majors: LiveData<List<String>> = _majors

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    var currentQuery: String = ""
        private set
    var currentMajor: String = "Tất cả"
        private set
    var currentSort: SortOption = SortOption.NAME_ASC
        private set

    init {
        refreshData()
    }

    fun refreshData() {
        val filteredList = repository.getFilteredStudents(currentQuery, currentMajor, currentSort)
        _students.value = filteredList
        _stats.value = repository.getStats(repository.getAllStudents())
        _majors.value = repository.getAvailableMajors()
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        val filteredList = repository.getFilteredStudents(currentQuery, currentMajor, currentSort)
        _students.value = filteredList
    }

    fun onMajorFilterChanged(major: String) {
        currentMajor = major
        val filteredList = repository.getFilteredStudents(currentQuery, currentMajor, currentSort)
        _students.value = filteredList
    }

    fun onSortOptionChanged(sortOption: SortOption) {
        currentSort = sortOption
        val filteredList = repository.getFilteredStudents(currentQuery, currentMajor, currentSort)
        _students.value = filteredList
    }

    fun addStudent(mssv: String, name: String, major: String, gpa: Double): Boolean {
        if (repository.isMssvExists(mssv)) {
            _toastMessage.value = "Mã sinh viên $mssv đã tồn tại!"
            return false
        }
        val newStudent = Student(
            id = UUID.randomUUID().toString(),
            mssv = mssv.trim(),
            name = name.trim(),
            major = major.trim(),
            gpa = gpa
        )
        val success = repository.addStudent(newStudent)
        if (success) {
            _toastMessage.value = "Thêm sinh viên thành công"
            refreshData()
        } else {
            _toastMessage.value = "Không thể thêm sinh viên"
        }
        return success
    }

    fun updateStudent(id: String, mssv: String, name: String, major: String, gpa: Double): Boolean {
        if (repository.isMssvExists(mssv, excludeId = id)) {
            _toastMessage.value = "Mã sinh viên $mssv đã trùng với sinh viên khác!"
            return false
        }
        val updatedStudent = Student(
            id = id,
            mssv = mssv.trim(),
            name = name.trim(),
            major = major.trim(),
            gpa = gpa
        )
        val success = repository.updateStudent(updatedStudent)
        if (success) {
            _toastMessage.value = "Cập nhật sinh viên thành công"
            refreshData()
        } else {
            _toastMessage.value = "Không thể cập nhật sinh viên"
        }
        return success
    }

    fun deleteStudent(student: Student) {
        val success = repository.deleteStudent(student.id)
        if (success) {
            _toastMessage.value = "Đã xóa sinh viên ${student.name}"
            refreshData()
        } else {
            _toastMessage.value = "Xóa sinh viên thất bại"
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
