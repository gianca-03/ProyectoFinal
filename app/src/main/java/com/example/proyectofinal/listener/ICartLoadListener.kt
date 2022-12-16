package com.example.proyectofinal.listener

import com.example.proyectofinal.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModeList:List<CartModel>)
    fun onLoadCartFailed(message:String?)
}