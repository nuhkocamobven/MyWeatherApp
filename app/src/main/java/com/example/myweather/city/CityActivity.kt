package com.example.myweather.city

import android.app.SearchManager
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.example.myweather.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_city.city_recycler
import kotlin.Int.Companion.MAX_VALUE



class CityActivity : AppCompatActivity(),CityAdapter.OnItemClickListener {

    private val gson = Gson()
    private var cityAdapter = CityAdapter(this)
    private lateinit var searchView: SearchView
    private var cityId: String?=null
    private  var cityDbHelper: CityHelper?=null
    val factory: SQLiteDatabase.CursorFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        cityDbHelper = CityHelper(this, "city.db", factory, 2)

        this.setTitle("Select City")

        city_recycler.setHasFixedSize(true)
        city_recycler.layoutManager = LinearLayoutManager(this)
        getAllCities()


        /*checkboxCity.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                if(checkboxCity.isChecked){
                    val editor=getSharedPreferences("com.example.myweather.city.CityActivity",Context.MODE_PRIVATE).edit()
                    editor.putBoolean("City Added",true)
                    editor.apply()
                    saveCity()
                    Toast.makeText(this@CityActivity,"Added to Home",Toast.LENGTH_SHORT).show()
                }else{
                    val ctyId=intent.extras.getInt("id")
                    cityDbHelper!!.deleteCity(ctyId)
                    val editor=getSharedPreferences("com.example.myweather.city.CityActivity",Context.MODE_PRIVATE).edit()
                    editor.putBoolean("City Removed",true)
                    editor.apply()
                    Toast.makeText(this@CityActivity,"Removed from Home",Toast.LENGTH_SHORT).show()
                }
            }

        })*/

    }
    private fun getAllCities() {
        val json = readJSONFromAsset()
        val cities = gson.fromJson<City>(json, City::class.java)
        cityAdapter.cities = cities.cityDetail.toMutableList()
        city_recycler.adapter = cityAdapter

    }

    private fun readJSONFromAsset(): String? {
        val json: String?
        try {
            val inputStream = resources.assets.open("document.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = MAX_VALUE


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                cityAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                cityAdapter.filter.filter(newText)
                return true

            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_search) {
            true
        } else
            return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
        }else
            super.onBackPressed()
    }


    fun saveCity(){
        val cities=CityDetail()
        val name=cities.getName()
        val lat=cities.getLat()
        val lon=cities.getLon()

        cities.setId(cityId)
        cities.setName(name.toString())
        if (lat != null) {
            cities.setLat(lat.toFloat().toString())
        }
        if (lon != null) {
            cities.setLon(lon.toFloat().toString())
        }
        cityDbHelper?.addCity(cities)
    }

    override fun onItemClicked(city: CityDetail) {
        /*val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id", city.getId())
        this.startActivity(intent)*/

        }

    }

