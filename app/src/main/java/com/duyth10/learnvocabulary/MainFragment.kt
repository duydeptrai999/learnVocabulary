package com.duyth10.learnvocabulary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class MainFragment : Fragment() {

    private val vocabularyViewModel: VocabularyViewModel by viewModels()
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var quizFrg: ImageView
    private lateinit var btnSelectAll: ImageView
    private lateinit var btnDone: ImageView
    private lateinit var clearAll: ImageView

    private var isSelectionMode = false
    private var currentList = mutableListOf<Vocabulary>()
    private var selectedVocabularies = mutableListOf<Vocabulary>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewVocabulary)
        fabAdd = view.findViewById(R.id.fabAdd)
        quizFrg = view.findViewById(R.id.quizFrg)
        btnSelectAll = view.findViewById(R.id.chooseAll)
        btnDone = view.findViewById(R.id.done)
        clearAll = view.findViewById(R.id.closeAll)

        clearAll.setOnClickListener {
            vocabularyAdapter.clearSelection()
            selectedVocabularies.clear()
        }
        btnSelectAll.setOnClickListener {
            vocabularyAdapter.selectAllVocabularies()
            selectedVocabularies = vocabularyAdapter.getSelectedVocabularies().toMutableList()

        }
        quizFrg.setOnClickListener {
            if (isSelectionMode) {
                isSelectionMode = false
                selectedVocabularies.clear()
                vocabularyAdapter.isSelectionMode = false
                btnDone.visibility = View.GONE
                clearAll.visibility = View.GONE
                btnSelectAll.visibility = View.GONE
                Toast.makeText(context, "Hủy chế độ quiz", Toast.LENGTH_SHORT).show()
            } else {
                isSelectionMode = true
                selectedVocabularies.clear()
                vocabularyAdapter.isSelectionMode = true
                btnDone.visibility = View.VISIBLE
                clearAll.visibility = View.VISIBLE
                btnSelectAll.visibility = View.VISIBLE
                Toast.makeText(context, "Chọn các từ bạn muốn làm quiz", Toast.LENGTH_SHORT).show()
            }

            vocabularyAdapter.notifyDataSetChanged()
        }

        btnDone.setOnClickListener {
            if (selectedVocabularies.isNotEmpty()) {
                proceedToQuizFragment()
            } else {
                Toast.makeText(
                    context,
                    "Vui lòng chọn ít nhất một từ để làm quiz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        vocabularyAdapter = VocabularyAdapter(
            mutableListOf(),
            onEditClick = { vocabulary ->
                showEditDialog(vocabulary)
            },
            onDeleteClick = { position ->
                vocabularyViewModel.deleteVocabulary(position)
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            },
            onVocabularySelect = { vocabulary -> toggleVocabularySelection(vocabulary) },
            isSelectionMode = isSelectionMode

        )


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = vocabularyAdapter

        vocabularyViewModel.vocabularyList.observe(viewLifecycleOwner, Observer { updatedList ->
            currentList = updatedList.toMutableList()
            vocabularyAdapter.updateList(updatedList)
        })

        fabAdd.setOnClickListener {
            showAddDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            val wordSend = args.getString("wordSend")
            val phoneticsTextSend = args.getString("phoneticsTextSend")
            val phoneticsAudioSend = args.getString("phoneticsAudioSend")
            val meaningsBasic = args.getString("meaningsBasic")

            val correctAnswersCount = args.getInt("correctAnswersCount")
            val incorrectAnswersCount = args.getInt("incorrectAnswersCount")
            val isFromExternalApp = args.getBoolean("isFromExternalApp")

            Log.d("ReceiverIfm", "correctAnswersCount: $correctAnswersCount")
            Log.d("ReceiverIfm", "incorrectAnswersCount: $incorrectAnswersCount")

            if ((incorrectAnswersCount != null || correctAnswersCount != null) && isFromExternalApp != true) {

                showQuizResultDialog(correctAnswersCount, incorrectAnswersCount)

            }
            Log.d("MainFragment", "wordSend: $wordSend")
            Log.d("MainFragment", "phoneticsTextSend: $phoneticsTextSend")
            Log.d("MainFragment", "phoneticsAudioSend: $phoneticsAudioSend")
            Log.d("MainFragment", "meaningsBasic: $meaningsBasic")


            if (wordSend != null || phoneticsTextSend != null || phoneticsAudioSend != null || meaningsBasic != null) {
                showAddDialog(wordSend, phoneticsTextSend, meaningsBasic)
            }
        }
    }

    private fun showAddDialog(
        word: String? = null,
        phoneticsText: String? = null,
        meaning: String? = null
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit, null)
        val editEnglish = dialogView.findViewById<EditText>(R.id.editEnglish)
        val editVietnamese = dialogView.findViewById<EditText>(R.id.editVietnamese)
        val editDescription = dialogView.findViewById<EditText>(R.id.editDescription)

        // Prefill the data if available
        editEnglish.setText(word ?: "")
        editVietnamese.setText(meaning ?: "")
        editDescription.setText(phoneticsText ?: "")

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Vocabulary")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val english = editEnglish.text.toString()
                val vietnamese = editVietnamese.text.toString()
                val description = editDescription.text.toString()
                if (english.isNotEmpty() && vietnamese.isNotEmpty()) {
                    vocabularyViewModel.addVocabulary(
                        Vocabulary(
                            0,
                            english,
                            vietnamese,
                            description
                        )
                    )
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }


    private fun showEditDialog(vocabulary: Vocabulary) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit, null)
        val editEnglish = dialogView.findViewById<EditText>(R.id.editEnglish)
        val editVietnamese = dialogView.findViewById<EditText>(R.id.editVietnamese)
        val editDescription = dialogView.findViewById<EditText>(R.id.editDescription)

        editEnglish.setText(vocabulary.eng)
        editVietnamese.setText(vocabulary.vie)
        editDescription.setText(vocabulary.description)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Vocabulary")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val english = editEnglish.text.toString()
                val vietnamese = editVietnamese.text.toString()
                val description = editDescription.text.toString()
                if (english.isNotEmpty() && vietnamese.isNotEmpty()) {
                    val position =
                        vocabularyViewModel.vocabularyList.value?.indexOf(vocabulary) ?: -1
                    if (position != -1) {
                        vocabularyViewModel.updateVocabulary(
                            Vocabulary(
                                vocabulary.id,
                                english,
                                vietnamese,
                                description
                            )
                        )
                    }
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(" ", null)
            .create()
        dialog.show()
    }

    private fun toggleVocabularySelection(vocabulary: Vocabulary) {
        if (selectedVocabularies.contains(vocabulary)) {
            selectedVocabularies.remove(vocabulary)
        } else {
            selectedVocabularies.add(vocabulary)
        }
        vocabularyAdapter.notifyDataSetChanged()
    }

    private fun selectAllVocabularies() {
        selectedVocabularies.clear()
        selectedVocabularies.addAll(currentList)
        vocabularyAdapter.notifyDataSetChanged()
    }

    private fun proceedToQuizFragment() {
        val selectedVocabularyList = selectedVocabularies.map { it.id }.toMutableList()
        Log.d("selectedVocabularyList", selectedVocabularyList.toString())

        val bundle = Bundle().apply {
            putIntegerArrayList("selectedVocabularyIds", ArrayList(selectedVocabularyList))
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val quizFragment = QuizFragment()
        quizFragment.arguments = bundle
        transaction.replace(R.id.fragment_container, quizFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showQuizResultDialog(correctAnswers: Int, incorrectAnswers: Int) {
        val message = "Correct answers: $correctAnswers\nIncorrect answers: $incorrectAnswers"

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Quiz Result")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }


}
