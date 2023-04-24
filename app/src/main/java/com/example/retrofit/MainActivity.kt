package com.example.retrofit

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var spinner : Spinner
    private lateinit var tvUrl : TextView
    private val URL_KEY = "url"

    private var url : String = ""
    private var urlPreferences : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("PREFERENCIAS", savedInstanceState?.getString(URL_KEY).toString())

        tvUrl = findViewById(R.id.tvUrl)
        spinner = findViewById(R.id.spinner)

        val ivPicasso = findViewById<ImageView>(R.id.ivPicasso)


        val arrayList = ArrayList<String>()
        arrayList.add("Samoyed")
        arrayList.add("Affenpinscher")
        arrayList.add("Briard")

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)

        spinner.setAdapter(arrayAdapter);

        spinner.onItemSelectedListener = this

        val apiCall = API().crearServicioAPI()


        apiCall.imagenAleatoria().enqueue(object : Callback<ImageRandom> {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(
                call: Call<ImageRandom>, response:
                Response<ImageRandom>) {
                url = response.body()?.message!!

                Picasso.get().load(url).into(ivPicasso)
                tvUrl.text = url

                val sharedPreferences  = getSharedPreferences("PERSISTENCIA", MODE_PRIVATE)
                val preferencesEditor = sharedPreferences.edit()
                preferencesEditor.putString(URL_KEY,url)
                preferencesEditor.apply()

                Log.d("API_PRUEBA", "status es " + response.body()?.status)
                Log.d("API_PRUEBA ", "message es " + url)

            }
            override fun onFailure(call: Call<ImageRandom>, t: Throwable) {
                val sharedPreferences  = getSharedPreferences("PERSISTENCIA", MODE_PRIVATE)
                urlPreferences = sharedPreferences.getString(URL_KEY,"").toString()
                Log.d("urlpref", urlPreferences)

                Picasso.get().load(urlPreferences).into(ivPicasso)
                Toast.makeText(
                    this@MainActivity,
                    "La ultima imagen vista fue $urlPreferences",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        spinner = findViewById<Spinner>(R.id.spinner)

        if (item.itemId == R.id.option_menu_list_images) {
            Toast.makeText(
                this, "OPTION menu 1",
                Toast.LENGTH_SHORT).show()

            val apiCall = API().crearServicioAPI()
            apiCall.listaImagenesDePerrosPorRaza("hound").enqueue(object : Callback<ImagesBreed>{
                override fun onResponse(call: Call<ImagesBreed>, response: Response<ImagesBreed>) {
                    val dogs = response.body()?.message
                    Log.d("PRUEBAS", "Status de la respuesta ${response.body()?.status}")
                    if(dogs!=null) {
                        for(dog in dogs) {
                            Log.d("PRUEBAS", "Perro es $dog")
                        }
                    }
                }
                override fun onFailure(call: Call<ImagesBreed>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "No fue posible conectar a API",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_images, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val raza: String = parent?.getItemAtPosition(position).toString()
        Log.d("raza", raza)
        val apiCall = API().crearServicioAPI()

        apiCall.listaImagenesDePerrosPorRaza(raza.lowercase()).enqueue(object : Callback<ImagesBreed>{
            override fun onResponse(call: Call<ImagesBreed>, response: Response<ImagesBreed>) {
                val dogs = response.body()?.message
                Log.d("PRUEBAS", "Status de la respuesta ${response.body()?.status}")

                if(dogs!=null) {
                    val linearLayoutManager = LinearLayoutManager(
                        this@MainActivity,
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    val recycler = findViewById<RecyclerView>(R.id.recyclerDogs)

                    recycler.layoutManager = linearLayoutManager

                    recycler.adapter = DogsAdapter(dogs,this@MainActivity)
                }
            }
            override fun onFailure(call: Call<ImagesBreed>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "No fue posible conectar a API",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("PREFERENCIAS ON SAVE", "onSaveInstanceState")
        outState.putString(URL_KEY, urlPreferences)
        outState?.run {
            putString(URL_KEY, urlPreferences)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        if(TextUtils.isEmpty(urlPreferences)){
            val sharedPreferences  = getSharedPreferences("PERSISTENCIA", MODE_PRIVATE)
            urlPreferences = sharedPreferences.getString(URL_KEY,"").toString()
        }
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
