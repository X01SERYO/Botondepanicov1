package com.example.botondepanicov1.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.botondepanicov1.AlarmaSonora;
import com.example.botondepanicov1.R;
import com.example.botondepanicov1.wifi_direct.BuscandoDispositivosWifi;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BuscandoDispositivosBluetooth extends AppCompatActivity implements BeaconConsumer {


    // variables para la configuracion de los beacons
    private Beacon beacon;
    private BluetoothAdapter btAdapter;
    private BeaconManager beaconManager;
    private BeaconTransmitter beaconTransmitter;
    private static final String TAG = "Sergio";
    private Button buscar;

    private ListView listaBluetooth;
    private List<DispositivoBluetooth> mLista = new ArrayList<>();
    private AdapterBluetooth mAdapter;

    //Variables para permaneer en la actividad
    private Button onOffCambioAutomaticoBlue;
    private Boolean permanecerBlue = false;

    //Terminar activiadd
    private int terminarActividadBlue = 0;

    //Alarma
    private Button  playPausar;
    private MediaPlayer mp;
    private final AlarmaSonora alarma = new AlarmaSonora();

    //Direccion MAC
    String mac;
    String KEY_MAC = "MAC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscando_dispositivos_bluetooth);
        //LEER PREFERENCIAS DE LA MAC
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences((Context)this);
        String datos = String.valueOf(pref.getString(this.KEY_MAC, "No hay datos"));
        Log.v("Sergio","MACKEY"+datos);
        // valida que se pueda optener la MAC, si no es posible genera el numero aleatorio
        if (datos.equals("No hay datos")){
            mac = trasformarMac();
            guardarMacAleatoria(mac);
        }{
            mac = datos;
        }
        //llama a funcion de validar permisos de localozacion
        checkPermission();
        listaBluetooth = findViewById(R.id.listBluetooth);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        beaconManager = BeaconManager.getInstanceForApplication(this);

        playPausar = findViewById(R.id.alarmaBlue);
        mp = MediaPlayer.create(this,R.raw.alarma_sonora);

        encenderBluetooth();
        alarma.estadoPreferencia(playPausar,mp,this);
        onOffCambioAutomaticoBlue = findViewById(R.id.onOffAutoBlue);
        onOffCambioAutomaticoBlue.setText("No");
        // llama la funcion del temporizador
        iniciarCuenta();
        Log.v("Sergio", String.valueOf(calclarTiempo()));
        //llama la funcion para la configuracion del beacon
        setupBeacon();
        //llama la funcion para envio de los beacons
        envio();

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        inicioDescubrimiento();
    }
    //transforma la MAC para que quede Hexadecimal
    public static String trasformarMac(){
        String mac = "";
        Log.v("Sergio", "uuid|" + obtenerMac().equals("")+"|");
        if(obtenerMac().equals("")){
            return alternativaMac();
        }
        else {
            String[] macArray = obtenerMac().split(":");
            for (int i = 0; i < macArray.length; i++) {
                if (macArray[i].length() == 1) {
                    macArray[i] = macArray[i] + "0";
                }
            }

            for (String i : macArray) {
                mac = mac + i;
            }
            return mac;
        }
    }
    // optiene la MAC de los dispositivos si es posible
    public static String obtenerMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return "";
    }

    //si no es posible obtener la MAC guarda la aleatoria en preferencias
    public void guardarMacAleatoria(String mac){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((Context)this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(this.KEY_MAC, mac);
        editor.apply();
    }
    // concatena los numeros aleatorios
    public static String alternativaMac(){
        return numeroAleatorio()+numeroAleatorio();
    }

    // se genera numero aletario de 6 digitos
    public static String numeroAleatorio(){
        long min_val = 100000;
        int max_val = 999999;
        double randomNum = Math.random() * ( max_val - min_val );
        System.out.println("Random Number: "+randomNum);
        return String.valueOf( (int) randomNum);
    }

    //termina la actividad cuando se hace uso de boton atras
    @Override
    public void onBackPressed() {
        terminarActividadBlue = 1;
        alarma.apagarFinActividad(playPausar,mp,this);
        beaconManager.unbind(BuscandoDispositivosBluetooth.this);
        Log.d("Sergio", "terminarActividad = "+terminarActividadBlue);
        this.finish();
    }

    //verifica el estado del boton en NO para hacer cambio automatico
    public  void onClickOnOffCambioAutomatico(View view){
        permanecerBlue = !permanecerBlue;
        if (permanecerBlue){
            onOffCambioAutomaticoBlue.setText("Si");
            terminarActividadBlue = 1;
        }else{
            onOffCambioAutomaticoBlue.setText("No");

            cambioActividad(true);
        }
    }

    //encender o apagar bluetooth
    private void encenderBluetooth(){
        if(!btAdapter.isEnabled()){
            btAdapter.enable();
        }
    }

    // inica el descubrimiento de dispositivos
    private void inicioDescubrimiento(){
        beaconManager.bind(BuscandoDispositivosBluetooth.this);
    }

    //valida que el bluetooth habilitado y compatible con el dispositivo
    private boolean isBluetoothLEAvailable() {
        return btAdapter != null && this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
    //valida que el bluetooth este encendido
    private boolean getBlueToothOn() {
        return btAdapter != null && btAdapter.isEnabled();
    }

    ///llama la funcion que valida si el bluetooth este encendido
    private void envio() {
        if (getBlueToothOn()) {
            Log.i(TAG, "isBlueToothOn");
            transmitIBeacon();
        } else if (!isBluetoothLEAvailable()) {
            Toast toast = Toast.makeText(this, "Bluetooth no disponible en su dispositivo.",Toast.LENGTH_LONG);
            toast.show();
        } else {
            Log.i(TAG, "BlueTooth is off");
            Toast toast = Toast.makeText(this, "Habilite bluetooth antes de transmitir iBeacon.",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // envio de beacons
    private void transmitIBeacon() {
        boolean isSupported = false;
        isSupported = btAdapter.isMultipleAdvertisementSupported();
        if (isSupported) {

            Log.v(TAG, "is support advertistment");
            if (beaconTransmitter.isStarted()) {
                beaconTransmitter.stopAdvertising();

            } else {
                beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

                    @Override
                    public void onStartFailure(int errorCode) {
                        Log.e(TAG, "Advertisement start failed with code: " + errorCode);
                    }

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        Log.i(TAG, "Advertisement start succeeded." + settingsInEffect.toString());
                    }
                });

            }
        } else {
            Toast toast = Toast.makeText(this, "Su dispositivo no es compatible con LE Bluetooth.",Toast.LENGTH_LONG);
            toast.show();
        }
    }
    //configuracion del beacon concatenando la MAC
    private void setupBeacon() {
        String uuid = "954e6dac-5612-4642-b2d1-"+mac;
        Log.v("Sergio","uuid "+uuid);
        beacon = new Beacon.Builder()
                .setId1(uuid) // UUID for beacon
                .setId2("5") // Major for beacon
                .setId3("12") // Minor for beacon
                .setManufacturer(0x004C) // Radius Networks.0x0118  Change this for other beacon layouts//0x004C for iPhone
                .setTxPower(-56) // Power in dB
                .setDataFields(Arrays.asList(2L, 3L)) // Remove this for beacon layouts without d: fields
                .build();

        BluetoothManager btManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter ();

        beaconTransmitter = new BeaconTransmitter(this, new BeaconParser()
                .setBeaconLayout ("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }

    //recepcion de beacons
    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeaons", Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"), null, null);
        final Region region2 = new Region("myBeaons", null, null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                    beaconManager.startRangingBeaconsInRegion(region2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                    beaconManager.stopRangingBeaconsInRegion(region2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        //se optienen los datos recibidos de los beacons
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                for (Beacon oneBeacon : beacons) {

                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " adrres:" + oneBeacon.getBluetoothAddress()
                            + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());

                    mLista = eliminarDuplicados(mLista,oneBeacon);
                    mAdapter = new AdapterBluetooth(BuscandoDispositivosBluetooth.this, R.layout.adapter_dispositivos_encontrados_wifi,mLista);
                    listaBluetooth.setAdapter(mAdapter);
                }

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
            beaconManager.startMonitoringBeaconsInRegion(region2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //inicializa el temporizador
    public final void iniciarCuenta() {
        final long tiempo = this.calclarTiempo();
        Log.d("Sergio", tiempo + " inicio wifi");
        (new CountDownTimer(tiempo, 10000L) {
            public void onTick(long millisUntilFinished) {
                long tiempoF = millisUntilFinished / (long)1000;
                int minutosF = (int)(tiempoF / (long)60);
                long segundosF = tiempoF % (long)60;
                Log.d("Sergio", "" + minutosF + ':' + segundosF + " -- terminar " + terminarActividadBlue + " -- Permanecer " + permanecerBlue);
            }

            public void onFinish() {
                if (terminarActividadBlue == 0) {
                    cambioActividad(permanecerBlue);
                }

            }
        }).start();
    }
    // acci??n para cambio de tecnologia
    private void cambioActividad(Boolean permanecer){
        alarma.apagarTemporizador(playPausar,mp,BuscandoDispositivosBluetooth.this);
        Intent intent;
        if(permanecer){
            intent = new Intent(this, BuscandoDispositivosBluetooth.class);
        }else{
            intent = new Intent(this, BuscandoDispositivosWifi.class);
        }
        beaconManager.unbind(BuscandoDispositivosBluetooth.this);
        //btAdapter.disable();
        finish();
        startActivity(intent);
    }

    // calcula el tiempo del temporizador por medio de una variable con distribucion normal
    private Long calclarTiempo(){
        Random gauss = new Random();
        int numeroAleaorio = (int) gauss.nextGaussian();
        int desviacionStandar = 1;
        int media = 5;
        int numero = desviacionStandar * numeroAleaorio + media;
        numero *= 10000;
        return (long) numero;
    }

    // elimina el duplicados de dispositivos encontrados
    public List<DispositivoBluetooth> eliminarDuplicados(List<DispositivoBluetooth> lista, Beacon oneBeacon){
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        for(int i = 0; i<mLista.size();i++){
            if(mLista.get(i).nombre.equals(oneBeacon.getId1())){
                mLista.remove(i);
            }
        }
        mLista.add(new DispositivoBluetooth(oneBeacon.getId1(),oneBeacon.getDistance(),strDate));
        return lista;
    }
    // valida los permisos de localizacion
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        } else {
            checkPermission();
        }
    }

     // accion para el boton de encender alarma
    public void onClickAlarmaBlue(View view){
        alarma.reproducirParar(playPausar,mp,this);
    }

    //recargar la actividad
    public void onClickRefrescarBlue(View view){
        terminarActividadBlue = 1;
        cambioActividad(true);
    }

    // accion del boton para cambiar de tecnologia
    public void onClickCambiarWifiDirect(View view){
        terminarActividadBlue = 1;
        cambioActividad(false);
    }
}