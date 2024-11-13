package com.duyth10.learnvocabulary

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VocabularyAdapter(
    private var vocabularyList: List<Vocabulary>,
    private val onEditClick: (Vocabulary) -> Unit,
    private val onDeleteClick: (Vocabulary) -> Unit,
    private val onVocabularySelect: (Vocabulary) -> Unit,
    var isSelectionMode: Boolean = false
) : RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {

    private val selectedVocabularies = mutableSetOf<Vocabulary>()

    inner class VocabularyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglishWord: TextView = itemView.findViewById(R.id.tvEnglishWord)
        val tvVietnameseMeaning: TextView = itemView.findViewById(R.id.tvVietnameseMeaning)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val iconMood: ImageView = itemView.findViewById(R.id.choose)

        init {
            itemView.setOnClickListener {
                onEditClick(vocabularyList[adapterPosition])
            }
            iconMood.setOnClickListener {
                toggleVocabularySelection(adapterPosition)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(vocabularyList[adapterPosition])
            }

            iconMood.setOnClickListener {
                toggleVocabularySelection(adapterPosition)
                Log.d("Vocabuadapter", selectedVocabularies.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocabulary, parent, false)
        return VocabularyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val vocabulary = vocabularyList[position]
        holder.tvEnglishWord.text = vocabulary.eng
        holder.tvVietnameseMeaning.text = vocabulary.vie
        holder.tvDescription.text =
            vocabulary.description.takeIf { it?.isNotEmpty() == true } ?: "No description"

        holder.iconMood.visibility = if (isSelectionMode) View.VISIBLE else View.GONE

        holder.btnDelete.visibility = if (!isSelectionMode) View.VISIBLE else View.GONE

        holder.iconMood.setImageResource(
            if (selectedVocabularies.contains(vocabulary)) R.drawable.baseline_check else R.drawable.baseline_close
        )
    }

    override fun getItemCount() = vocabularyList.size


    fun updateList(newList: List<Vocabulary>) {
        vocabularyList = newList
        selectedVocabularies.clear()
        notifyDataSetChanged()
    }

    fun toggleVocabularySelection(position: Int) {
        val vocabulary = vocabularyList[position]

        if (selectedVocabularies.contains(vocabulary)) {
            selectedVocabularies.remove(vocabulary)
        } else {
            selectedVocabularies.add(vocabulary)
        }

        onVocabularySelect(vocabulary)
        notifyDataSetChanged()
    }

    fun selectAllVocabularies() {
        selectedVocabularies.clear()
        selectedVocabularies.addAll(vocabularyList)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedVocabularies.clear()
        notifyDataSetChanged()
    }


    fun getSelectedVocabularies(): List<Vocabulary> {
        return selectedVocabularies.toList()
    }


}
