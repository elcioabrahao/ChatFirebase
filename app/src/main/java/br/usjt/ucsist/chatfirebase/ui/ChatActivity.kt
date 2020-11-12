package br.usjt.ucsist.chatfirebase.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.usjt.ucsist.chatfirebase.R
import br.usjt.ucsist.chatfirebase.model.Mensagem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {


    var adapter: ChatAdapter? = null
    var mensagens: MutableList<Mensagem>? = null
    var fireUser: FirebaseUser? = null
    var mMsgsReference: CollectionReference? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mensagens = ArrayList()
        adapter = ChatAdapter(mensagens!!, this)
        mensagensRecyclerView.setAdapter(adapter)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        mensagensRecyclerView.setLayoutManager(linearLayoutManager)

        adapter!!.onItemClick = { mensagem ->

            var intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("mensagem",mensagem)
            startActivity(intent);

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) { /* ... */
                    getLastKnownLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    Log.e("LOCALIZACAO","PERMISSAO NEGADA")
                }
            }).check()



    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }

            }

    }

    override fun onStart() {
        super.onStart()
        setupFirebase()
    }

    private fun setupFirebase() {
        fireUser = FirebaseAuth.getInstance().currentUser
        mMsgsReference = FirebaseFirestore.getInstance().collection("mensagenslatlong")
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

        Log.w("LOCALIZACAO", "Cliquei: " + latitude + " " + longitude)
        val mensagem = mensagemEditText!!.text.toString()
        val m = Mensagem(
            fireUser!!.email!!, Date(),
            mensagem,
            latitude,
            longitude
        )
        esconderTeclado(view!!)
        mMsgsReference!!.add(m)

    }

    private fun esconderTeclado(v: View) {
        val ims: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        ims.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.action_map) {
            val intent: Intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)

    }


}

