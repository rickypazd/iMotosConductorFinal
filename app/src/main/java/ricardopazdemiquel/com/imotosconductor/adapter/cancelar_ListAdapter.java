package ricardopazdemiquel.com.imotosconductor.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;


import ricardopazdemiquel.com.imotosconductor.ConfirmarCancelacion;
import ricardopazdemiquel.com.imotosconductor.R;
import ricardopazdemiquel.com.imotosconductor.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosconductor.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosconductor.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosconductor.utiles.Contexto;

import static android.content.Context.MODE_PRIVATE;

public class cancelar_ListAdapter extends BaseAdapter {

    private JSONArray listaCanchas;
    private Context contexto;


    private  int id_carrera;


    public cancelar_ListAdapter(Context contexto, JSONArray lista , int id_carrera) {
        this.contexto = contexto;
        this.listaCanchas = lista;
        this.id_carrera = id_carrera;
    }

    @Override
    public int getCount() {
        return listaCanchas.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return listaCanchas.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto)
                    .inflate(R.layout.layou_list_marca, viewGroup, false);
        }
        TextView nombre = view.findViewById(R.id.tv_NombreList);

        try {
            final JSONObject cancha = listaCanchas.getJSONObject(i);

            nombre.setText(cancha.getString("nombre"));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final JSONObject usr_log = getUsr_log();
                    try {
                        new cancelar_carrera(usr_log.getInt("id"),id_carrera,cancha.getInt("id")).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }


    public JSONObject getUsr_log() {
        SharedPreferences preferencias = contexto.getSharedPreferences("myPref", MODE_PRIVATE);
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

    private class cancelar_carrera extends AsyncTask<Void, String, String> {

        private int usuario;
        private int id_carrera;
        private int tipo;

        public cancelar_carrera( int usuario , int id_carrera, int tipo ){
            this.usuario = usuario;
            this.id_carrera = id_carrera;
            this.tipo = tipo;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "cancelar_carrera");
            parametros.put("id_usr",usuario+"");
            parametros.put("id_carrera",id_carrera+"");
            parametros.put("id_tipo",tipo+"");
            parametros.put("tipo_cancelacion", 1+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(contexto.getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(resp!=null) {
                if (resp.equals("falso")) {
                    Log.e(Contexto.APP_TAG, "Hubo un error al obtener la lista de servidor.");
                    return;
                } else {
                    try {
                        JSONObject obj = new JSONObject(resp);
                        Intent inte = new Intent(contexto, ConfirmarCancelacion.class);
                        inte.putExtra("obj_cancelacion", obj.toString());
                        //inte.putExtra("obj_carrera",obj.toString());
                        contexto.startActivity(inte);
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
