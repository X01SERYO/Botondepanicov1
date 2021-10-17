package com.example.botondepanicov1.wifi_direct

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.DataSetObserver
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.botondepanicov1.AlarmaSonora
import com.example.botondepanicov1.R
import kotlinx.android.synthetic.main.activity_buscando_dispositivos_wifi.*
import java.util.*
import kotlin.math.abs

class BuscandoDispositivosWifi : AppCompatActivity(){

    private var wifiP2pManager: WifiP2pManager? = null
    private var wifiP2pChannel: WifiP2pManager.Channel? = null
    var longitude = 0.0
    var latitude = 0.0
    private var ingredients: ArrayList<Ingredient>? = null
    private var record: MutableMap<*, *>? = null
    private var lv: ListView? = null
    private var distance = 0.0
    private var serviceInfo: WifiP2pDnsSdServiceInfo? = null
    var adapter: MapDevicesAdapter? = null
    lateinit var ownDevice: LinearLayout
    lateinit var otherDevice7: LinearLayout
    lateinit var otherDevice8: LinearLayout
    lateinit var otherDevice: LinearLayout
    lateinit var otherDevice1: LinearLayout
    lateinit var otherDevice2: LinearLayout
    lateinit var otherDevice3: LinearLayout
    lateinit var otherDevice4: LinearLayout
    lateinit var otherDevice5: LinearLayout
    lateinit var otherDevice6: LinearLayout
    lateinit var otherDevice9: LinearLayout

    // Llave de las preferencias
    private var key: String = "MY_KEY"
    private var keyAlarma: String = "ALARMA"

    private var mProgressDialog: ProgressDialog? = null
    var wifiManager: WifiManager? = null

    //Estado de la bateria
    private var bm: BatteryManager? = null

    //Obtain data Sharedpreference
    val PREFERENCES_DATE = "saveDate"
    private var sharedpreferences: SharedPreferences? = null

    //Inicialización nivel bateria
    private var globalLevel = 0

    //Terminar activiadd
    private var terminarActividad = 0

    //Alarma
    private lateinit var playPausar : Button
    private lateinit var mp : MediaPlayer
    private val alarma = AlarmaSonora()

    //Nombre Dispositivo
    private lateinit var nombreDispositivo: String

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscando_dispositivos_wifi)
        sharedpreferences = getSharedPreferences(PREFERENCES_DATE, AppCompatActivity.MODE_PRIVATE)
        bm = getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager

        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = this.registerReceiver(null, ifilter)
        val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        globalLevel = (level * 100 / scale.toFloat()).toInt()

        playPausar = findViewById(R.id.alarma)
        mp = MediaPlayer.create(this,R.raw.alarma_sonora)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Log.d("Sergio","INCIO ACTIVIDAD " + prefs.getString(keyAlarma,"No hay datos").toString())

        nombreDispositivo = "Dispositivo: " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL


        alarma.estadoPreferencia(playPausar,mp,this)
        inicio()
        iniciarCuenta()
        Log.d("Sergio", "fin")


    }

    override fun onBackPressed() {
        terminarActividad = 1
        alarma.apagarFinActividad(playPausar,mp,this)
        Log.d("Sergio", "terminarActividad = $terminarActividad")
        finish()
    }

    private fun inicio(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
        turnGPSOn()
        record = HashMap<Any?, Any?>()
        wifiManager = this.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        Handler().postDelayed({ wifiManager!!.isWifiEnabled = true }, 1500)
        ingredients = ArrayList<Ingredient>()
        lv = findViewById<View>(R.id.FndListIdMap) as ListView
        setButtons()
        val map = MapDevicesAdapter(this, R.layout.adapter_dispositivos_encontrados_wifi)
        adapter = map
        lv!!.adapter = adapter
        wifiP2pManager = getSystemService(AppCompatActivity.WIFI_P2P_SERVICE) as WifiP2pManager
        wifiP2pChannel = wifiP2pManager!!.initialize(applicationContext, mainLooper, null)
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setMessage("Buscando dispositivos por favor espere")
        mProgressDialog!!.setOnCancelListener { onBackPressed() }
        mProgressDialog!!.show()
        adapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                adapter?.count?.minus(1)?.let { lv!!.setSelection(it) }
            }
        })
        setMap()

        val codificator = Codificator()
        val handler = Handler()

        handler.postDelayed(Runnable {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            Log.v("Sergio","Pref " + pref.getString(key, nombreDispositivo).toString())
            (record as HashMap<Any?, Any?>)["longitude"] = longitude.toString()
            (record as HashMap<Any?, Any?>)["latitude"] = latitude.toString()
            (record as HashMap<Any?, Any?>)["date"] = codificator.dateToString(Date())//
            (record as HashMap<Any?, Any?>)["indice"] = pref.getString(key, nombreDispositivo).toString()
            Log.d("MapDevices", "Record coordinates in the hashmap")
            serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence.tcp",
                record as MutableMap<String, String>?
            )
            if (ActivityCompat.checkSelfPermission(
                    this@BuscandoDispositivosWifi,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@Runnable
            }
            wifiP2pManager!!.addLocalService(
                wifiP2pChannel,
                serviceInfo,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        // Success!
                    }

                    override fun onFailure(code: Int) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                })
            mProgressDialog!!.dismiss()
            wifiP2pManager!!.setDnsSdResponseListeners(wifiP2pChannel,
                { s, s1, wifiP2pDevice -> }
            ) { s, map, wifiP2pDevice ->
                val ingredient = Ingredient(
                    wifiP2pDevice.deviceName.split("°").toTypedArray()[0],
                    wifiP2pDevice.deviceAddress
                )
                ingredient.setLatitude(java.lang.Double.valueOf(map["latitude"]))
                ingredient.setLongitude(java.lang.Double.valueOf(map["longitude"]))
                ingredient.setIndice(map["indice"])
                ingredient.setDistance(
                    calclateDistance(
                        ingredient.getLongitude(),
                        ingredient.getLatitude()
                    ).toDouble()
                )
                val ingre = Ingredient(wifiP2pDevice.deviceName.split("°").toTypedArray()[0],
                    wifiP2pDevice.deviceAddress)
                ingre.Ingredient(
                    wifiP2pDevice.deviceName.split("°").toTypedArray()[0],
                    wifiP2pDevice.deviceAddress)
                distance = ingredient.getDistance()
                ingredient.setDate(map["date"])
                if (!isObjectInArray(wifiP2pDevice.deviceAddress)) {
                    ingredients!!.add(ingredient)
                    val arrayList: ArrayList<Ingredient>? = listSort(ingredients)
                    adapter!!.clear()
                    for (i in arrayList!!.indices) {
                        adapter!!.add(arrayList[i])
                    }
                    lv!!.setSelection(adapter!!.count - 1)
                    Log.d("Add device", java.lang.String.valueOf(adapter!!.count))
                    Log.d("Add device", ingredient.name.toString() + " device")
                    when (ingredients!!.size) {
                        1 -> {
                            otherDevice.visibility = View.VISIBLE
                            otherDevice.y =
                                calculateY(java.lang.Double.valueOf(map["latitude"]))
                            otherDevice.x =
                                calculateX(java.lang.Double.valueOf(map["longitude"]))
                        }
                        2 -> {
                            otherDevice1.setVisibility(View.VISIBLE)
                            otherDevice1.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice1.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        3 -> {
                            otherDevice2.setVisibility(View.VISIBLE)
                            otherDevice2.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice2.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        4 -> {
                            otherDevice3.setVisibility(View.VISIBLE)
                            otherDevice3.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice3.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        5 -> {
                            otherDevice4.setVisibility(View.VISIBLE)
                            otherDevice4.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice4.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        6 -> {
                            otherDevice5.setVisibility(View.VISIBLE)
                            otherDevice5.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice5.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        7 -> {
                            otherDevice6.setVisibility(View.VISIBLE)
                            otherDevice6.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice6.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        8 -> {
                            otherDevice7.setVisibility(View.VISIBLE)
                            otherDevice7.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice7.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        9 -> {
                            otherDevice8.setVisibility(View.VISIBLE)
                            otherDevice8.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice8.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                        10 -> {
                            otherDevice9.setVisibility(View.VISIBLE)
                            otherDevice9.setY(calculateY(java.lang.Double.valueOf(map["latitude"])))
                            otherDevice9.setX(calculateX(java.lang.Double.valueOf(map["longitude"])))
                        }
                    }
                }
            }
            val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
            wifiP2pManager!!.addServiceRequest(
                wifiP2pChannel,
                serviceRequest,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        // Success!
                    }

                    override fun onFailure(code: Int) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                })
            wifiP2pManager!!.discoverServices(
                wifiP2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        // Success!
                    }

                    override fun onFailure(code: Int) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                })
            Log.d("MapDevices", "Start services")
        }, 10000)

    }

    private fun isObjectInArray(deviceAddress: String): Boolean {
        val result = false
        for (ingredient in ingredients!!) {
            if (ingredient.mac.equals(deviceAddress)) {
                return true
            }
        }
        return result
    }

    private fun calclateDistance(otherLongitude: Double, otherLatitude: Double): Float {
        val loc1 = Location("")
        loc1.latitude = otherLatitude
        loc1.longitude = otherLongitude
        val loc2 = Location("")
        loc2.latitude = latitude
        loc2.longitude = longitude
        return loc1.distanceTo(loc2)
    }

    private fun turnGPSOn() {
        val provider =
            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        if (!provider.contains("gps")) {
            Toast.makeText(this, "Activando GPS", Toast.LENGTH_SHORT).show()
            val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent1)
        }
    }

    private fun setButtons() {
        otherDevice = findViewById<View>(R.id.locationBalckLy) as LinearLayout
        otherDevice1 = findViewById<View>(R.id.locationBlueLy) as LinearLayout
        otherDevice2 = findViewById<View>(R.id.locationGreenLy) as LinearLayout
        otherDevice3 = findViewById<View>(R.id.locationLBLy) as LinearLayout
        otherDevice4 = findViewById<View>(R.id.locationOrangeLy) as LinearLayout
        otherDevice5 = findViewById<View>(R.id.locationRedLy) as LinearLayout
        otherDevice6 = findViewById<View>(R.id.locationYWLy) as LinearLayout
        otherDevice7 = findViewById<View>(R.id.locationPinkLy) as LinearLayout
        otherDevice8 = findViewById<View>(R.id.locationWineLy) as LinearLayout
        otherDevice9 = findViewById<View>(R.id.locationLemonLy) as LinearLayout
    }

    private fun setMap() {
        //record!!["nameApp"] = "conecta2"
        val lm = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        try{
            lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 100, 4f, locationListenerGPS
            )
        }catch (e:Exception){
            Log.v("Sergio", e.toString())
        }

    }

    private val locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitude = location.longitude
            latitude = location.latitude
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private fun listSort(lista: ArrayList<Ingredient>?): ArrayList<Ingredient>? {
        val copy: ArrayList<Ingredient>? = lista
        var name = ""
        var mac = ""
        var distance = 0.0
        var indice = ""
        var date = ""
        for (i in copy!!.indices) for (j in i + 1 until copy.size) if (java.lang.Double.valueOf(copy[i].getIndice()) < java.lang.Double.valueOf(
                copy[j].getIndice()
            )
        ) {
            name = copy[j].getName().toString()
            mac = copy[j].getMac().toString()
            distance = copy[j].getDistance()
            indice = copy[j].getIndice().toString()
            date = copy[j].getDate().toString()

            //copy.get(j).equals(copy.get(i));
            copy[j].setName(copy[i].getName())
            copy[j].setMac(copy[i].getMac())
            copy[j].setDistance(copy[i].getDistance())
            copy[j].setIndice(copy[i].getIndice())
            copy[j].setDate(copy[i].getDate())

            //copy.get(i).equals(temp);
            copy[i].setName(name)
            copy[i].setMac(mac)
            copy[i].setDistance(distance)
            copy[i].setIndice(indice)
            copy[i].setDate(date)
        }
        return copy
    }

    private fun calculateX(otherLongitude: Double): Float {
        val finalX: Double = (abs(longitude) - abs(otherLongitude)) * (10000 * distance * 5)
        return finalX.toFloat()
    }

    private fun calculateY(otherLatitude: Double): Float {
        val finalY: Double = (abs(latitude) - abs(otherLatitude)) * (10000 * distance * 5)
        return finalY.toFloat()
    }

    private fun boton(){
        alarma.apagarTemporizador(playPausar,mp,this@BuscandoDispositivosWifi)

        val intent = Intent(this, BuscandoDispositivosWifi::class.java)
        wifiManager!!.isWifiEnabled = false
        finish()
        startActivity(intent)
    }

    fun iniciarCuenta() {
        val tiempo: Long = calclarTiempo()
        Log.d("Sergio", "$tiempo inicio")
        //10000 equivale a diez segunodos de intervlo de descuento
        object : CountDownTimer(tiempo, 10000) {
            override fun onTick(millisUntilFinished: Long) {
                val tiempoF = millisUntilFinished / 1000
                val minutosF = (tiempoF / 60).toInt()
                val segundosF = tiempoF % 60
                Log.d("Sergio", "$minutosF:$segundosF -- $terminarActividad")
            }

            override fun onFinish() {
                if(terminarActividad == 0){
                    boton()
                }
            }
        }.start()
    }

    private fun calclarTiempo():Long{
        val gauss = Random()
        val numeroAleaorio = gauss.nextGaussian().toInt()
        val desviacionStandar = 1
        val media = 5
        var numero = desviacionStandar * numeroAleaorio + media
        numero *= 10000
        return  numero.toLong()
    }

    private fun bateria(): Long {
        Log.d("Sergio", " batery$globalLevel")
        var tiempo: Long = 20000
        if (globalLevel in 81..100) {
            Log.d("Sergio", " 100>=global_level | global_level>80")
            //40 segundos
            tiempo = (20 * 1000).toLong()
        } else if (globalLevel in 61..80) {
            Log.d("Sergio", " 80>=global_level && global_level>60")
            //20 segundos
            tiempo = (40 * 1000).toLong()
        } else if (globalLevel in 41..60) {
            Log.d("Sergio", " 60>=global_level && global_level>40")
            //1 minuto
            tiempo = (60 * 1000).toLong()
        } else if (globalLevel in 21..40) {
            Log.d("Sergio", " 40>=global_level && global_level>20")
            //1 minuto y 30 segundo
            tiempo = (60 * 1000 + 30 * 1000).toLong()
        } else if (globalLevel in 11..20) {
            Log.d("Sergio", " 20>=global_level && global_level>10")
            //2 minuto
            tiempo = (2 * 60 * 1000).toLong()
        } else if (globalLevel in 1..10) {
            Log.d("Sergio", " 10>=global_level && global_level>0")
            //2 minuto y 30 segundo
            tiempo = (2 * 60 * 1000 + 30 * 1000).toLong()
        }
        return tiempo
    }

    fun onClickAlarma(view : View){
        alarma.reproducirParar(playPausar,mp,this)
    }

    fun onClickRefrescar(view: View){
        terminarActividad = 1
        boton()
    }
}
