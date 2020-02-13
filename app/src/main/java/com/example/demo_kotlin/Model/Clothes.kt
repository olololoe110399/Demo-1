package com.example.demo_kotlin.Model

class Clothes {
    var idclothes: String? = null
    var nameclothes: String? = null
    var price = 0.0
    var amount = 0
    override fun toString(): String {
        return "Clothes(idclothes=$idclothes, nameclothes=$nameclothes, price=$price, amount=$amount)"
    }

}