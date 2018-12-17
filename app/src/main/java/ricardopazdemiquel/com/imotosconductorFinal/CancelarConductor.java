package ricardopazdemiquel.com.imotosconductorFinal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;

import java.util.Hashtable;

import ricardopazdemiquel.com.imotosconductorFinal.R;
import ricardopazdemiquel.com.imotosconductorFinal.adapter.cancelar_ListAdapter;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosconductorFinal.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosconductorFinal.utiles.Contexto;


public class CancelarConductor extends AppCompatActivity {

    private ListView listView;
    private int id_carrera;

    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_cancelar_conductor);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        id_carrera = Integer.parseInt(getIntent().getStringExtra("id_carrera"));

        listView = (android.widget.ListView) findViewById(R.id.list_vista_marca);

        new get_obtener_cancelaciones().execute();
    }

    // Opcion para ir atras sin reiniciar el la actividad anterior de nuevo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class get_obtener_cancelaciones extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_motivos_cancelacion");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null) {
                Toast.makeText(CancelarConductor.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            } else if (resp.equals("falso")) {
                Toast.makeText(CancelarConductor.this, "Hubo un error al obtener la lista de servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener la lista de servidor.");
            } else {
                try {
                    JSONArray arr = new JSONArray(resp);
                    ListAdapter adaptador = new cancelar_ListAdapter(CancelarConductor.this, arr, id_carrera);
                    listView.setAdapter(adaptador);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
