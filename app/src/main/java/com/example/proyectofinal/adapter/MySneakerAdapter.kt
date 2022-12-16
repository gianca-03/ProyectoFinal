package com.example.proyectofinal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal.R
import com.example.proyectofinal.eventbus.UpdateCartEvent
import com.example.proyectofinal.listener.ICartLoadListener
import com.example.proyectofinal.listener.IRecyclerClickListener
import com.example.proyectofinal.model.CartModel
import com.example.proyectofinal.model.SneakerModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus

class MySneakerAdapter(
    private val context: Context,
    private val list:List<SneakerModel>,
            private val cartListener: ICartLoadListener
): RecyclerView.Adapter<MySneakerAdapter.MySneakerViewHolder>() {

    class MySneakerViewHolder(itemView:View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
         var imageView: ImageView?=null
         var txtName: TextView ?=null
         var  txtPrice: TextView ?=null

        private var clickListener: IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener){
            this.clickListener = clickListener;
        }


        init {
                imageView = itemView.findViewById(R.id.imageView) as ImageView
                txtName = itemView.findViewById(R.id.txtName) as TextView
                txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            itemView.setOnClickListener(this)


        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySneakerViewHolder {
        return MySneakerViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_sneaker_item,parent,false))
    }

    override fun onBindViewHolder(holder: MySneakerViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price)

        holder.setClickListener(object: IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
               addToCart(list[position])
            }

        })


    }

    private fun addToCart(sneakerModel: SneakerModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(sneakerModel.Key!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){   //si ya esta, solo actualiaza
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()
                        cartModel!!.quantity =  cartModel!!.quantity+1;
                        updateData["quantity"] = cartModel!!.quantity
                        updateData["totalPrice"] = cartModel!!.quantity*cartModel.price!!.toFloat()

                        userCart.child(sneakerModel.Key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Carrito actualizado")
                            }
                        .addOnFailureListener{e -> cartListener.onLoadCartFailed(e.message)}
                    } else {  // si no esta, agrega
                        val cartModel = CartModel()
                        cartModel.key = sneakerModel.Key
                        cartModel.name = sneakerModel.name
                        cartModel.image = sneakerModel.image
                        cartModel.quantity = 1
                        cartModel.totalPrice = sneakerModel.price!!.toFloat()

                        userCart.child(sneakerModel.Key!!)
                            .setValue(sneakerModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("New item agregado")
                            }
                            .addOnFailureListener{e -> cartListener.onLoadCartFailed(e.message)}

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                        cartListener.onLoadCartFailed(error.message)
                }

            })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}