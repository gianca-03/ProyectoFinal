package com.example.proyectofinal.listener

import com.example.proyectofinal.model.SneakerModel

interface ISneakerLoadListener {
    fun onSneakerLoadSucces(sneakerModeList: List<SneakerModel>?)
    fun onSneakerLoadFailed(message:String?)


}