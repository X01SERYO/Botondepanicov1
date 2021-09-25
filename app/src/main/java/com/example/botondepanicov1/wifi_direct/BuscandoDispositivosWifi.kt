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
import com.example.botondepanicov1.R
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BuscandoDispositivosWifi : AppCompatActivity(){

    private var wifiP2pManager: WifiP2pManager? = null
    private var wifiP2pChannel: WifiP2pManager.Channel? = null
    var longitude = 0.0
    var latitude = 0.0
    var indice = 0.0
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

    private var key: String = "MY_KEY"

    private var mProgressDialog: ProgressDialog? = null
    var wifiManager: WifiManager? = null

    //MODULO SERVICE
    private val TAG = "MainActivity"
    //private val intent = Intent
    private val token = "MainActivity"
    private val button: Button? = null
    private var datei: Date? = null

    //Variables Calculo Reference ideal Acelerometer and Proximity
    private val RANGE_AP_A = 0
    private val RANGE_AP_B = 720
    private val REFERENCE_IDEAL_AP_C = 0
    private val REFERENCE_IDEAL_AP_D = 10

    //Variables calculo Reference ideal Battery
    private val RANGE_BATTERY_A = 0
    private val RANGE_BATTERY_B = 100
    private val REFERENCE_IDEAL_BATTERY_C = 100
    private val REFERENCE_IDEAL_BATTERY_D = 100

    //Variables criteria values
    private val CRITERIA_ACELEROMETER_VALUE = 0.5
    private val CRITERIA_PROXIMITY_VALUE = 0.17
    private val CRITERIA_BATTERY_VALUE = 0.33
    private var weightNormalized: List<Double> = ArrayList()

    //Estado de la bateria
    private var bm: BatteryManager? = null
    private var percentageBattery = 0

    //Obtain data Sharedpreference
    val PREFERENCES_DATE = "saveDate"
    val ACELEROMETER_ITERATION_DATE = "acelerometerIterationKey"
    val PROXIMITY_ITERATION_DATE = "proximityIterationKey"
    private var sharedpreferences: SharedPreferences? = null
    private val sharedPreferencesRelativeIndex: SharedPreferences? = null
    private val editorRelativeIndex: SharedPreferences.Editor? = null

    //Nombre extern file
    private val filenameIndexRelative = "IndexRelativeFile"
    private var global_level = 0


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
        global_level = (level * 100 / scale.toFloat()).toInt()

        inicio()
        iniciarCuenta()
        Log.d("sergio", "fin")


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
                adapter?.getCount()?.minus(1)?.let { lv!!.setSelection(it) }
            }
        })
        setMap()
        val handler = Handler()
        val codificator = Codificator()


        handler.postDelayed(Runnable {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            (record as HashMap<Any?, Any?>)["longitude"] = longitude.toString()
            (record as HashMap<Any?, Any?>)["latitude"] = latitude.toString()
            (record as HashMap<Any?, Any?>)["date"] = codificator.dateToString(Date())//
            (record as HashMap<Any?, Any?>)["indice"] = pref.getString(key,"No hay datos").toString()
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
                    val arrayList: ArrayList<Ingredient>?
                    arrayList = ListSort(ingredients)
                    adapter!!.clear()
                    for (i in arrayList!!.indices) {
                        adapter!!.add(arrayList[i])
                    }
                    lv!!.setSelection(adapter!!.getCount() - 1)
                    Log.d("Add device", java.lang.String.valueOf(adapter!!.getCount()))
                    Log.d("Add device", ingredient.name.toString() + " device")
                    when (ingredients!!.size) {
                        1 -> {
                            otherDevice!!.visibility = View.VISIBLE
                            otherDevice!!.y =
                                calculateY(java.lang.Double.valueOf(map["latitude"]))
                            otherDevice!!.x =
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

    fun calcularIndicedeActividad(): Double {
        datei = Date()
        Log.v("TIME", percentageBattery.toString())
        val dateFinallyIteration = datei!!.time
        val timei = dateToCalendar(datei!!)
        val dateIterationAcelerometer = sharedpreferences!!.getLong(ACELEROMETER_ITERATION_DATE, 0)
        val timeAce = longToCalendar(dateIterationAcelerometer)
        val dateIterationProximity = sharedpreferences!!.getLong(PROXIMITY_ITERATION_DATE, 0)
        val timePro = longToCalendar(dateIterationProximity)
        val timec1 = CalculateTiempoTranscurrido(dateFinallyIteration, dateIterationAcelerometer)
        val timec2 = CalculateTiempoTranscurrido(dateFinallyIteration, dateIterationProximity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            percentageBattery = bm!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }
        val idealReferenceAcelerometro = CalculateReferenceIdeal(
            timec1,
            RANGE_AP_A,
            RANGE_AP_B,
            REFERENCE_IDEAL_AP_C,
            REFERENCE_IDEAL_AP_D
        )
        val idealReferenceProximidad = CalculateReferenceIdeal(
            timec2,
            RANGE_AP_A,
            RANGE_AP_B,
            REFERENCE_IDEAL_AP_C,
            REFERENCE_IDEAL_AP_D
        )
        val idealReferenceBattery = CalculateReferenceIdeal(
            percentageBattery,
            RANGE_BATTERY_A,
            RANGE_BATTERY_B,
            REFERENCE_IDEAL_BATTERY_C,
            REFERENCE_IDEAL_BATTERY_D
        )
        weightNormalized = CalculateWeightNormalized(
            idealReferenceAcelerometro,
            idealReferenceProximidad,
            idealReferenceBattery
        )
        val resultIndex = CalculateIndixe(weightNormalized)
        Toast.makeText(applicationContext, "El indice es: $resultIndex", Toast.LENGTH_SHORT).show()
        writeFileRelativeIndex(
            timei,
            timeAce,
            timePro,
            timec1.toString(),
            timec2.toString(),
            percentageBattery.toString(),
            idealReferenceAcelerometro.toString(),
            idealReferenceProximidad.toString(),
            idealReferenceBattery.toString(),
            weightNormalized.toString(),
            resultIndex.toString()
        )
        return resultIndex
    }

    private fun dateToCalendar(date: Date): String? {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        val dateTest = cal.time
        val format1 = SimpleDateFormat("HH:mm:ss")
        var inActiveDate: String? = null
        inActiveDate = format1.format(dateTest)
        Log.v(TAG, inActiveDate)
        return inActiveDate
    }

    private fun longToCalendar(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val mHour = calendar[Calendar.HOUR]
        val mMinutes = calendar[Calendar.MINUTE]
        val mSecond = calendar[Calendar.SECOND]
        val time = "$mHour:$mMinutes:$mSecond"
        Log.v(TAG, time)
        return time
    }

    fun CalculateTiempoTranscurrido(dateiteration: Long, dateActual: Long): Int {
        var functionResult = 0
        val differenceDate = Math.abs(dateActual - dateiteration)
        val minutesResult = TimeUnit.MILLISECONDS.toMinutes(differenceDate).toInt()
        if (dateiteration == 0L || minutesResult > 720) {
            functionResult = 720
        } else if (dateiteration != 0L) {
            functionResult = minutesResult
        }
        return functionResult
    }

    fun CalculateReferenceIdeal(t: Int, a: Int, b: Int, c: Int, d: Int): Double {
        var resultIdealReference = 0.0
        if (t >= c && t <= d) {
            resultIdealReference = 1.0
        } else if (t >= a && t <= c && a != c) {
            resultIdealReference = 1.toDouble() - Math.min(Math.abs(t - c), Math.abs(t - d))
                .toDouble() / Math.abs(a - c)
            //resultIdealReference= 1-(Math.min(Math.abs(t - c), Math.abs(t - d))/Math.abs(a - c));
        } else if (t >= d && t <= b && d != b) {
            resultIdealReference = 1.toDouble() - Math.min(Math.abs(t - c), Math.abs(t - d))
                .toDouble() / Math.abs(d - b)
            //resultIdealReference= 1-(Math.min(Math.abs(t - c), Math.abs(t - d))/Math.abs(d - b));
        }
        return resultIdealReference
    }

    fun CalculateWeightNormalized(
        idealReferenceAcelerometer: Double,
        idealReferenceProximity: Double,
        idealReferenceBattery: Double
    ): List<Double> {
        val listNormalized: MutableList<Double> = ArrayList()
        val weigthAcelerometer = idealReferenceAcelerometer * CRITERIA_ACELEROMETER_VALUE
        val weigthProximity = idealReferenceProximity * CRITERIA_PROXIMITY_VALUE
        val weigthBattery = idealReferenceBattery * CRITERIA_BATTERY_VALUE
        listNormalized.add(weigthAcelerometer)
        listNormalized.add(weigthProximity)
        listNormalized.add(weigthBattery)
        return listNormalized
    }

    fun CalculateIndixe(listNormalised: List<Double>): Double {
        var firstIndex = 0.0
        var secondIndex = 0.0
        var relativeIndex = 0.0
        val acelerometerValue = listNormalised[0]
        val proximityValue = listNormalised[1]
        val batteryValue = listNormalised[2]
        firstIndex = Math.sqrt(
            Math.pow(
                acelerometerValue - CRITERIA_ACELEROMETER_VALUE,
                2.0
            ) + Math.pow(
                proximityValue - CRITERIA_PROXIMITY_VALUE,
                2.0
            ) + Math.pow(batteryValue - CRITERIA_BATTERY_VALUE, 2.0)
        )
        secondIndex = Math.sqrt(
            Math.pow(acelerometerValue, 2.0) + Math.pow(
                proximityValue,
                2.0
            ) + Math.pow(
                batteryValue,
                2.0
            )
        )
        relativeIndex = secondIndex / (firstIndex + secondIndex)
        Toast.makeText(applicationContext, "indice relativo: $relativeIndex", Toast.LENGTH_SHORT)
            .show()
        return relativeIndex
    }

    fun writeFileRelativeIndex(
        timei: String?,
        timeAce: String,
        timePro: String,
        timec1: String,
        timec2: String,
        percentageBattery: String,
        ira: String,
        irp: String,
        irb: String,
        weightNormalized: String,
        resultIndex: String
    ) {
        var bw: BufferedWriter? = null
        var fw: FileWriter? = null
        try {
            val data =
                "$timei,  $timeAce, $timePro, $timec1, $timec2, $percentageBattery, $ira, $irp, $irb, $weightNormalized, $resultIndex"
            val file = File(getExternalFilesDir(null), filenameIndexRelative)
            // Si el archivo no existe, se crea!
            if (!file.exists()) {
                file.createNewFile()
            }
            // flag true, indica adjuntar informaciÃ³n al archivo.
            fw = FileWriter(file.absoluteFile, true)
            bw = BufferedWriter(fw)
            bw.newLine()
            bw.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                //Cierra instancias de FileWriter y BufferedWriter
                bw?.close()
                fw?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    private fun ListSort(lista: ArrayList<Ingredient>?): ArrayList<Ingredient>? {
        val copy: ArrayList<Ingredient>?
        copy = lista
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
        val finalX: Double
        finalX = (Math.abs(longitude) - Math.abs(otherLongitude)) * (10000 * distance * 5)
        return finalX.toFloat()
    }

    private fun calculateY(otherLatitude: Double): Float {
        val finalY: Double
        finalY = (Math.abs(latitude) - Math.abs(otherLatitude)) * (10000 * distance * 5)
        return finalY.toFloat()
    }

    private fun boton(){

        val intent = Intent(this, BuscandoDispositivosWifi::class.java)
        Log.d("tiempo", "este es el tiempo")
        Handler().postDelayed({ wifiManager!!.isWifiEnabled = false }, 30000)
        Log.d("tiempo", "este es el  otro tiempo")
        startActivity(intent)
        finish()

    }

    fun iniciarCuenta() {
        val tiempo: Long = bateria()
        Log.d("Sergio", "$tiempo inicio")
        //10000 equivale a diez segunodos de intervlo de descuento
        val cuenta = object : CountDownTimer(tiempo, 10000) {
            override fun onTick(millisUntilFinished: Long) {
                val tiempo_f = millisUntilFinished / 1000
                val minutos_f = (tiempo_f / 60).toInt()
                val segundos_f = tiempo_f % 60
                Log.d("Sergio", "$minutos_f:$segundos_f")
            }

            override fun onFinish() {
                boton()
                //Log.d("Sergio", "BATERIA_count "+global_level);

                //
            }
        }.start()
    }

    fun bateria(): Long {
        Log.d("Sergio", " batery$global_level")
        var tiempo: Long = 20000
        if (100 >= global_level && global_level > 80) {
            Log.d("Sergio", " 100>=global_level | global_level>80")
            //40 segundos
            tiempo = (20 * 1000).toLong()
        } else if (80 >= global_level && global_level > 60) {
            Log.d("Sergio", " 80>=global_level && global_level>60")
            //20 segundos
            tiempo = (40 * 1000).toLong()
        } else if (60 >= global_level && global_level > 40) {
            Log.d("Sergio", " 60>=global_level && global_level>40")
            //1 minuto
            tiempo = (60 * 1000).toLong()
        } else if (40 >= global_level && global_level > 20) {
            Log.d("Sergio", " 40>=global_level && global_level>20")
            //1 minuto y 30 segundo
            tiempo = (60 * 1000 + 30 * 1000).toLong()
        } else if (20 >= global_level && global_level > 10) {
            Log.d("Sergio", " 20>=global_level && global_level>10")
            //2 minuto
            tiempo = (2 * 60 * 1000).toLong()
        } else if (10 >= global_level && global_level > 0) {
            Log.d("Sergio", " 10>=global_level && global_level>0")
            //2 minuto y 30 segundo
            tiempo = (2 * 60 * 1000 + 30 * 1000).toLong()
        }
        return tiempo
    }
}
