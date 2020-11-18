package br.usjt.ucsist.chatfirebase.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import br.usjt.ucsist.chatfirebase.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonBiometria.isEnabled = false
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                habilitarBotaoBiometria()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(this,"Erro ao tentar autenticar com biometria. Use login e senha!",Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this,"Não há biometrica disponível. Use login e senha!",Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(this,"Para usar biometrica faça pelo menos um cadastro ou use login e senha!",Toast.LENGTH_SHORT).show()
        }
        mAuth = FirebaseAuth.getInstance()

        buttonBiometria.setOnClickListener(View.OnClickListener {
          _ ->
            executor =
                    ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int,
                                                           errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)

                            Toast.makeText(applicationContext,
                                    "AErro ao tentar autenticar com biometria. Use login e senha!", Toast.LENGTH_SHORT)
                                    .show()
                        }

                        override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            val intent = Intent(this@MainActivity,ChatActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, "A autenticação biometrica falhou. Use login e senha!",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticação Biometrica do Chat")
                    .setSubtitle("Logue com sua autenticação biométrica")
                    .setNegativeButtonText("Cancelar")
                    .build()
            biometricPrompt.authenticate(promptInfo)
        })
    }

    fun habilitarBotaoBiometria(){
        buttonBiometria.isEnabled = true
        buttonBiometria.background = getDrawable(R.drawable.ic_fingerprint)
    }

    fun fazerLogin(view: View?) {
        val login: String = loginEditText.text.toString()
        val senha: String = senhaEditText.text.toString()
        mAuth!!.signInWithEmailAndPassword(login,
                senha).addOnSuccessListener { result -> startActivity(Intent(this, ChatActivity::class.java)) }.addOnFailureListener { exception ->
            exception.printStackTrace()
            Toast.makeText(this, exception.message,
                    Toast.LENGTH_SHORT).show()
        }
    }


    fun irParaCadastro(view: View?) {
        startActivity(Intent(this, NovoUsuarioActivity::class.java))
    }

}