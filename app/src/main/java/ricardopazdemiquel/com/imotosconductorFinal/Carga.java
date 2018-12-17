package ricardopazdemiquel.com.imotosconductorFinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;


import ricardopazdemiquel.com.imotosconductorFinal.R;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosconductorFinal.utiles.Contexto;
import ricardopazdemiquel.com.imotosconductorFinal.utiles.Token;


public class Carga extends AppCompatActivity {

    private JSONObject usr_log ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);
        Token.currentToken= FirebaseInstanceId.getInstance().getToken();
       // Log.d("TOKEN",Token.currentToken);


        usr_log = getUsr_log();
        try {
            if(usr_log!=null){
            String calidate=new ValidarSession(usr_log.getInt("id"),Token.currentToken).execute().get();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (usr_log != null) {
            try {
                JSONObject historial =getHistorial();
                if(historial==null){
                    try {
                        String resp=new get_historial_carrera(usr_log.getInt("id"),"1990-01-01").execute().get();
                        cargarHistorial(resp);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        String resp=new get_historial_carrera(usr_log.getInt("id"),historial.getJSONArray("arr").getJSONObject(0).getString("fecha_pedido")).execute().get();
                        cargarHistorial(resp);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Intent intent = new Intent(Carga.this, LoginConductor.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                continuar();

            }
        }, 1000);

    }

    public void cargarHistorial(String resp){
        if (resp == null) {
            Toast.makeText(Carga.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
            Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
        }else if (resp.equals("falso")) {
                Toast.makeText(Carga.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
        } else {
                try {
                    JSONObject obj = new JSONObject(resp);
                    switch (obj.getInt("tipo")){
                        case 1:

                            break;
                        case 2:
                            JSONObject historial =getHistorial();
                            if(historial!=null){
                                JSONArray arr = historial.getJSONArray("arr");
                                JSONArray nuevas= obj.getJSONArray("arr");
                                for (int i = nuevas.length()-1 ; i >= 0; i--) {
                                    arr.put(0,nuevas.getJSONObject(i));

                                }
                                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("historial_carreras", arr.toString());
                                editor.commit();
                                continuar();
                            }else{
                                JSONArray nuevas= obj.getJSONArray("arr");
                                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.putString("historial_carreras", nuevas.toString());
                                editor.commit();
                                continuar();
                            }

                            break;
                        case 3:
                            //preguntar

                            alert();
                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    public void continuar(){
        Intent intent = new Intent(Carga.this, MainActivityConductor.class);
        startActivity(intent);
        finish();
    }
    private void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Carga.this);
        builder.setMessage("Sincronizar historial de viajes?")
                .setTitle("Descarga")
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        //  new MapCarrera.terminar_Carrera(id_carrera).execute();

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                        continuar();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONObject usr_log = new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public JSONObject getHistorial() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String resp = preferencias.getString("historial_carreras", "");
        if (resp.length() <= 0) {
            return null;
        } else {
            try {

                JSONObject historial = new JSONObject(resp);
                return historial;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class get_historial_carrera extends AsyncTask<Void, String, String> {
        private int id;
        private String fecha;
        public get_historial_carrera( int id, String fecha){
            this.id=id;
            this.fecha=fecha;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_mis_viajes_actu");
            parametros.put("id",id+"");
            parametros.put("fecha",fecha);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
    public class ValidarSession extends AsyncTask<Void, String, String> {
        private int id;
        private String token;

        public ValidarSession(int id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "validar_token");
            parametros.put("id_usr",id+"");
            parametros.put("token",token+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp==null){
                Toast.makeText(Carga.this,"Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
            }else{
                if (resp.contains("falso")) {
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                } else {
                    try {
                        JSONArray obj = new JSONArray(resp);
                        if(obj.length()<=0) {
                            Toast.makeText(Carga.this,"No se encontro la sesión, porfavor vuelva a iniciar.", Toast.LENGTH_LONG).show();
                            SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("usr_log", "");
                            usr_log=null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
}
