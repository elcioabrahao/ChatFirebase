package br.usjt.ucsist.chatfirebase.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.usjt.ucsist.chatfirebase.util.DateHelper
import br.usjt.ucsist.chatfirebase.model.Mensagem
import br.usjt.ucsist.chatfirebase.R


class ChatAdapter(
        private val values: List<Mensagem>,
        private val context: Context
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var onItemClick: ((Mensagem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ChatViewHolder(view)
    }


    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = values[position]


        holder.dataNomeTextView.text = context.getString(
                R.string.data_nome,
                DateHelper.format(item.data), item.usuario
        )
        holder.mensagemTextView.text = item.texto
        holder.localizacaoTextView.text = context.getString(
            R.string.data_localization,
            ""+item.latitude, ""+item.longitude
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(values[adapterPosition])
            }
        }

        val dataNomeTextView: TextView = view.findViewById(R.id.dataNomeTextView)
        val mensagemTextView: TextView = view.findViewById(R.id.mensagemTextView)
        val localizacaoTextView: TextView = view.findViewById(R.id.localizacaoTextView)
    }
}