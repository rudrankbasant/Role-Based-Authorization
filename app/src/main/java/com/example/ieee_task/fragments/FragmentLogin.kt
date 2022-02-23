package com.example.ieee_task.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.ieee_task.*
import com.example.ieee_task.R
import com.example.ieee_task.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class fragmentLogin : Fragment(R.layout.fragment_login) {

    private var bind: FragmentLoginBinding? = null
    private val binding get() = bind!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.registerTextView.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_fragmentLogin_to_fragmentRegister)
        }

        binding.buttonLogin.setOnClickListener {
            val emailID: String = binding.editTextLoginEmail.text.toString().trim { it <= ' ' }
            val pass: String = binding.editTextLoginPassword.text.toString().trim { it <= ' ' }

            when {
                TextUtils.isEmpty(emailID) -> {
                    Toast.makeText(activity, "Enter Email ID! ", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(pass) -> {
                    Toast.makeText(activity, "Enter Password! ", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailID, pass)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                //Store user id in a variable to pass on main activity:
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(activity,
                                    "Logged In Successfully",
                                    Toast.LENGTH_SHORT).show()

                                //Recognising User Type
                                takeToCorrectActivity()







                            } else {
                                Toast.makeText(activity, task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }

                }


            }

        }
    }

    private fun takeToCorrectActivity() {

        var firebase: FirebaseUser =FirebaseAuth.getInstance().currentUser!!
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Person")
        var type:String

        Log.d("here!!", "app reached here")



        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val person= dataSnapshot.getValue(Person::class.java)
                    if(person!!.userID==firebase.uid){
                        Log.d("thepersonishere",person.toString())
                        type=person.type


                        //going to respective activity
                        when{
                            type=="Admin"->{
                                val intent = Intent(activity,AdminActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            }
                            type=="Truck Driver"->{
                                val intent = Intent(activity, TruckDriverActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            else->{
                                val intent = Intent(activity, UserActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }

                        break

                    }




                }





            }

            override fun onCancelled(error: DatabaseError) {

            }


        })













    }



    //To avoid memory leak:
    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }
}