package com.example.ieee_task.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.ieee_task.AdminActivity
import com.example.ieee_task.R
import com.example.ieee_task.TruckDriverActivity
import com.example.ieee_task.UserActivity
import com.example.ieee_task.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class fragmentRegister : Fragment(R.layout.fragment_register) {

    private var bind: FragmentRegisterBinding? = null
    private val binding get() = bind!!
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        bind = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth=FirebaseAuth.getInstance()

        binding.loginTextView.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(R.id.action_fragmentRegister_to_fragmentLogin)
        }

        binding.buttonRegister.setOnClickListener{
            val name:String = binding.editTextRegisterName.text.toString().trim{it <=' '}
            val emailID:String=binding.editTextRegisterEmail.text.toString().trim{it <=' '}
            val pass:String=binding.editTextPassword.text.toString().trim{it <=' '}
            val confirmPass:String=binding.editTextConfirmPassword.text.toString().trim{it <=' '}
            val address: String =binding.editTextRegisterAddress.text.toString().trim{it <=' '}

            when{
                TextUtils.isEmpty(name)->{
                    Toast.makeText(activity,"Enter Name! ", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(emailID)->{
                    Toast.makeText(activity,"Enter Email ID! ", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(pass)->{
                    Toast.makeText(activity,"Enter Password! ", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(confirmPass)->{
                    Toast.makeText(activity,"Confirm Password! ", Toast.LENGTH_SHORT).show()
                }

                pass!=confirmPass->{
                    Toast.makeText(activity,"Passwords Do Not Match! ", Toast.LENGTH_SHORT).show()

                }
                TextUtils.isEmpty(address)->{
                    Toast.makeText(activity,"Enter Address", Toast.LENGTH_SHORT).show()
                }


                else->{
                    //Get type of user
                    var radioID: Int = binding.radioGroup.checkedRadioButtonId
                    var radioSelection: RadioButton = binding.radioGroup.findViewById(radioID)
                    var type: String = radioSelection.text.toString()



                    //Create an instance and register a user with email and password:
                    auth.createUserWithEmailAndPassword(emailID,pass).addOnCompleteListener { task ->
                        //if registration is successful:
                        if (task.isSuccessful) {

                            Toast.makeText(
                                activity, "Registered Successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                            //User info
                            val user= auth.currentUser
                            val userID= user!!.uid

                            //Pushing data in database:
                            val database = Firebase.database
                            val myRef = database.getReference("Person").child(userID)

                            val hashMap: HashMap<String,String> = HashMap()
                            hashMap["userID"]=userID
                            hashMap["name"]= name
                            hashMap["email"]=emailID
                            hashMap["address"]=address
                            hashMap["type"]=type

                            myRef.setValue(hashMap).addOnCompleteListener(requireActivity()){
                                if(it.isSuccessful){

                                    Log.d("heree!!", "uploaded data")

                                    when{
                                        type=="Admin"->{
                                            val intent = Intent(activity,AdminActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)

                                        }
                                        type=="Truck Driver"->{
                                            val intent = Intent(activity,TruckDriverActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                        else->{
                                            val intent = Intent(activity,UserActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                    }

                                }
                            }
                        } else {
                            //If registration not successful:
                            Toast.makeText(activity,task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
            }
        }
    }



    //To avoid memory leak:
    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }


}