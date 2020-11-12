package br.usjt.ucsist.chatfirebase.model

import java.io.Serializable
import java.util.*

class Mensagem : Comparable<Mensagem>, Serializable {

    var usuario: String? = null
    var data: Date? = null
    var texto: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    constructor(        usuario: String,
                        data: Date,
                        texto: String,
                        latitude: Double,
                        longitude: Double
    ){
        this.usuario = usuario
        this.data = data
        this.texto = texto
        this.latitude = latitude
        this.longitude = longitude
    }
    constructor()
    override operator fun compareTo(other: Mensagem): Int {
        return data!!.compareTo(other.data)
    }
}