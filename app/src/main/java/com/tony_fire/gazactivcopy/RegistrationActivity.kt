package com.tony_fire.gazactivcopy

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.InfoClass
import com.retrofit.RetrofitInstance
import com.retrofit.UserInfo


import com.tony_fire.gazactivcopy.databinding.ActivityRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userField()

    }
    private fun userField() {
        binding.regButton.setOnClickListener {
            val ed1stName = binding.ed1stName.text.toString().trim()
            val ed2stName = binding.edSecondName.text.toString().trim()
            val ed_email = binding.edEmail.text.toString().trim()
            val ed_tel = binding.edPhone.text.toString().trim()
            val countrypicker = binding.codePicker.selectedCountryNameCode.toString()



            if(ed1stName.isEmpty()){
                binding.ed1stName.error = "Введите имя"
                binding.ed1stName.requestFocus()
                return@setOnClickListener

            }

            if(ed2stName.isEmpty()){
                binding.edSecondName.error = "Введите фамилию"
                binding.edSecondName.requestFocus()
                return@setOnClickListener

            }

            if(ed_email.isEmpty()){
                binding.edEmail.error = "Введите Email"
                binding.edEmail.requestFocus()
                return@setOnClickListener

            }else if(!Patterns.EMAIL_ADDRESS.matcher(ed_email).matches()){
                binding.edEmail.error = "Введите корректный Email"
                binding.edEmail.requestFocus()
                return@setOnClickListener


            }
            if(ed_tel.isEmpty()){
                binding.edPhone.error = "Введите телефон"
                binding.edPhone.requestFocus()
                return@setOnClickListener

            }
            binding.regButton.setBackgroundResource(R.drawable.background_selector_button_color)
            binding.regButton.isEnabled = false

            val userinfo = UserInfo(countrypicker,ed_email,ed1stName,"5eb177835a0071baef6ea3b8",ed2stName,ed_tel,"azsxertye")
            val call : Call<InfoClass> = RetrofitInstance.api.signUpUser(userinfo)

            call.enqueue(object : Callback<InfoClass> {
                override fun onResponse(call: Call<InfoClass>, response: Response<InfoClass>) {
                    if(response.isSuccessful){
                        Log.d("Info", "Status: ${response.body()?.status}" )
                        Log.d("Info", "Pr: ${response.body()?.data?.pr}" )
                        Log.d("Info", "Redirect: ${response.body()?.data?.redirect}" )

                        when {
                            response.body()?.data?.pr.equals("success") -> {
                                val i = Intent(
                                    this@RegistrationActivity,
                                    ThanksActivity::class.java
                                ).apply {
                                    putExtra("Name", userinfo.first_name)
                                    putExtra("Second", userinfo.last_name)

                                }
                                startActivity(i)
                                binding.regButton.setBackgroundResource(R.drawable.button_selector)
                                binding.regButton.isEnabled = true
                            }
                            response.body()?.data?.pr.equals("exist") -> {
                                Toast.makeText(this@RegistrationActivity,"Вы уже зарегистрированы!",
                                    Toast.LENGTH_LONG).show()
                                binding.regButton.setBackgroundResource(R.drawable.button_selector)
                                binding.regButton.isEnabled = true
                            }
                            response.body()?.data?.pr.equals("redirect") -> {
                                val url = response.body()?.data?.redirect.toString()
                                val bi = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                val text = "Выберите лучший браузер для открытия ссылки"
                                val chooser = Intent.createChooser(bi,text)
                                startActivity(chooser)
                                binding.regButton.setBackgroundResource(R.drawable.button_selector)
                                binding.regButton.isEnabled = true
                            }
                        }

                    }
                    else {
                        Toast.makeText(applicationContext,"Произошла ошибка,попробуйте снова", Toast.LENGTH_SHORT).show()
                        binding.regButton.setBackgroundResource(R.drawable.button_selector)
                        binding.regButton.isEnabled = true
                        return
                    }

                }

                override fun onFailure(call: Call<InfoClass>, t: Throwable) {
                    Toast.makeText(applicationContext,"Проверьте интернет соединение", Toast.LENGTH_SHORT).show()
                    binding.regButton.setBackgroundResource(R.drawable.button_selector)
                    binding.regButton.isEnabled = true
                }


            })



        }

    }
}