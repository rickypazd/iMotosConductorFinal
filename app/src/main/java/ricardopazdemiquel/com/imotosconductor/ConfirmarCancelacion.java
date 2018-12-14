package ricardopazdemiquel.com.imotosconductor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import ricardopazdemiquel.com.imotosconductor.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosconductor.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosconductor.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosconductor.utiles.Contexto;


public class ConfirmarCancelacion extends AppCompatActivity {

    private JSONObject obj_cancelacion;
    private TextView tv_cancelacion_mensaje;
    private Button btn_confirmar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_cancelacion);
        tv_cancelacion_mensaje=findViewById(R.id.tv_cancelacion_mensaje);
        btn_confirmar=findViewById(R.id.btn_confirmar);
        String resp = getIntent().getStringExtra("obj_cancelacion");
        try {
            obj_cancelacion=new JSONObject(resp);
            // CONTENDIO DEL JSON  exito boolean, fecha_cancelacion, id_carrera, id_tipo, tipo_cancelacion,id_usr,total,cobro
            if(obj_cancelacion.getBoolean("cobro")){
                tv_cancelacion_mensaje.setText("Cancelar el viaje en este punto tiene un costo de Bs. "+obj_cancelacion.getDouble("total"));
            }else{
                tv_cancelacion_mensaje.setText("Todavía puedes cancelar este viaje sin costo.");
            }

            btn_confirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new cancelar_carrera(obj_cancelacion.toString()).execute();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private class cancelar_carrera extends AsyncTask<Void, String, String> {

        private String json;

        public cancelar_carrera( String json){
            this.json = json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "ok_cancelar_carrera");
            parametros.put("json",json+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null) {
                Toast.makeText(ConfirmarCancelacion.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            } else if (resp.equals("falso")) {
                Toast.makeText(ConfirmarCancelacion.this, "Hubo un error al obtener la lista de servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener la lista de servidor.");
            } else if (resp.equals("exito")) {
                Intent intent = new Intent(ConfirmarCancelacion.this, MainActivityConductor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

}
