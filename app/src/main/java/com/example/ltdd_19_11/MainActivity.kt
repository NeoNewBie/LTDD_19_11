package com.example.ltdd_19_11

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ltdd_19_11.data.model.SortOption
import com.example.ltdd_19_11.data.model.Student
import com.example.ltdd_19_11.databinding.ActivityMainBinding
import com.example.ltdd_19_11.databinding.DialogStudentFormBinding
import com.example.ltdd_19_11.ui.adapter.StudentAdapter
import com.example.ltdd_19_11.ui.viewmodel.StudentViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter

    private var majorsAdapter: ArrayAdapter<String>? = null
    private var isMajorSpinnerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearchAndFilter()
        setupSortButton()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onEditClick = { student -> showStudentFormDialog(student) },
            onDeleteClick = { student -> showDeleteConfirmDialog(student) }
        )

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = studentAdapter
        }
    }

    private fun setupSearchAndFilter() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.spMajorFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isMajorSpinnerInitialized) {
                    isMajorSpinnerInitialized = true
                    return
                }
                val selectedMajor = parent?.getItemAtPosition(position) as? String ?: "Tất cả"
                viewModel.onMajorFilterChanged(selectedMajor)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSortButton() {
        binding.btnSort.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, getString(R.string.sort_by_name))
            popup.menu.add(0, 2, 1, getString(R.string.sort_by_gpa_desc))
            popup.menu.add(0, 3, 2, getString(R.string.sort_by_gpa_asc))

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> viewModel.onSortOptionChanged(SortOption.NAME_ASC)
                    2 -> viewModel.onSortOptionChanged(SortOption.GPA_DESC)
                    3 -> viewModel.onSortOptionChanged(SortOption.GPA_ASC)
                }
                true
            }
            popup.show()
        }
    }

    private fun setupFab() {
        binding.fabAddStudent.setOnClickListener {
            showStudentFormDialog(null)
        }
    }

    private fun observeViewModel() {
        viewModel.students.observe(this) { students ->
            studentAdapter.submitList(students)
            if (students.isEmpty()) {
                binding.rvStudents.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                binding.rvStudents.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE
            }
        }

        viewModel.stats.observe(this) { stats ->
            binding.tvTotalCount.text = stats.totalCount.toString()
            binding.tvAvgGpa.text = String.format(Locale.US, "%.2f", stats.averageGpa)
            binding.tvExcellentCount.text = stats.excellentCount.toString()
        }

        viewModel.majors.observe(this) { majorsList ->
            majorsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, majorsList).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spMajorFilter.adapter = majorsAdapter
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage()
            }
        }
    }

    private fun showStudentFormDialog(student: Student?) {
        val isEdit = student != null
        val dialogBinding = DialogStudentFormBinding.inflate(LayoutInflater.from(this))

        dialogBinding.tvFormTitle.text = if (isEdit) {
            getString(R.string.title_edit_student)
        } else {
            getString(R.string.title_add_student)
        }

        val availableMajors = listOf(
            "Công nghệ Thông tin",
            "Khoa học Máy tính",
            "Kỹ thuật Phần mềm",
            "Hệ thống Thông tin",
            "An toàn Thông tin",
            "Trí tuệ Nhân tạo"
        )
        val majorAutoCompleteAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            availableMajors
        )
        dialogBinding.actvMajor.setAdapter(majorAutoCompleteAdapter)

        if (student != null) {
            dialogBinding.etMssv.setText(student.mssv)
            dialogBinding.etMssv.isEnabled = false
            dialogBinding.etName.setText(student.name)
            dialogBinding.actvMajor.setText(student.major, false)
            dialogBinding.etGpa.setText(student.gpa.toString())
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val mssv = dialogBinding.etMssv.text.toString().trim()
                val name = dialogBinding.etName.text.toString().trim()
                val major = dialogBinding.actvMajor.text.toString().trim()
                val gpaStr = dialogBinding.etGpa.text.toString().trim()

                dialogBinding.tilMssv.error = null
                dialogBinding.tilName.error = null
                dialogBinding.tilMajor.error = null
                dialogBinding.tilGpa.error = null

                var isValid = true

                if (mssv.isEmpty()) {
                    dialogBinding.tilMssv.error = getString(R.string.err_mssv_empty)
                    isValid = false
                }

                if (name.isEmpty()) {
                    dialogBinding.tilName.error = getString(R.string.err_name_empty)
                    isValid = false
                }

                if (major.isEmpty()) {
                    dialogBinding.tilMajor.error = getString(R.string.err_major_empty)
                    isValid = false
                }

                val gpa = gpaStr.toDoubleOrNull()
                if (gpa == null || gpa < 0.0 || gpa > 4.0) {
                    dialogBinding.tilGpa.error = getString(R.string.err_gpa_invalid)
                    isValid = false
                }

                if (!isValid) return@setOnClickListener

                val success = if (student != null) {
                    viewModel.updateStudent(
                        id = student.id,
                        mssv = mssv,
                        name = name,
                        major = major,
                        gpa = gpa!!
                    )
                } else {
                    viewModel.addStudent(
                        mssv = mssv,
                        name = name,
                        major = major,
                        gpa = gpa!!
                    )
                }

                if (success) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_confirm_delete))
            .setMessage(getString(R.string.msg_confirm_delete, student.name, student.mssv))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                viewModel.deleteStudent(student)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
