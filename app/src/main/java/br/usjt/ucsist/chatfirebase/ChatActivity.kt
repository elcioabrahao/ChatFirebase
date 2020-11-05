package br.usjt.ucsist.chatfirebase

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {


    var adapter: ChatAdapter? = null
    var mensagens: MutableList<Mensagem>? = null
    var fireUser: FirebaseUser? = null
    var mMsgsReference: CollectionReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mensagens = ArrayList()
        adapter = ChatAdapter(mensagens!!, this)
        mensagensRecyclerView.setAdapter(adapter)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        mensagensRecyclerView.setLayoutManager(linearLayoutManager)

    }

    override fun onStart() {
        super.onStart()
        setupFirebase()
    }

    private fun setupFirebase() {
        fireUser = FirebaseAuth.getInstance().currentUser
        mMsgsReference = FirebaseFirestore.getInstance().collection("mensagens")
        getRemoteMsgs()
    }

    private fun getRemoteMsgs() {
        mMsgsReference!!.addSnapshotListener(EventListener<QuerySnapshot?>() { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            mensagens!!.clear()
            for (doc in querySnapshot!!.documents) {
                val incomingMsg = doc.toObject(Mensagem::class.java)
                mensagens!!.add(incomingMsg!!)
            }
            Collections.sort(mensagens)
            adapter!!.notifyDataSetChanged()

        })
    }

    fun enviarMensagem(view: View?) {
        val mensagem = mensagemEditText!!.text.toString()
        val m = Mensagem(fireUser!!.email!!, Date(),
                mensagem)
        esconderTeclado(view!!)
        mMsgsReference!!.add(m)
    }

    private fun esconderTeclado(v: View) {
        val ims: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        ims.hideSoftInputFromWindow(v.windowToken, 0)
    }



}

