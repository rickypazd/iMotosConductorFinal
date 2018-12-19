package ricardopazdemiquel.com.imotosconductorFinal.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ricardopazdemiquel.com.imotosconductorFinal.R;


/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Ver_Producto_Dialog extends DialogFragment implements View.OnClickListener {

    private TextView text_pedido;
    private ImageView btn_cancelar;

    public static String APP_TAG = "registro";

    private static final String TAG = Ver_Producto_Dialog.class.getSimpleName();
    private String mensaje;

    private static final int EDITAR = 1;
    private static final int AGREGAR = 2;

    @SuppressLint("ValidFragment")
    public Ver_Producto_Dialog(String mensaje) {
        this.mensaje = mensaje;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_ver_producto, null);
        builder.setView(v);

        text_pedido = v.findViewById(R.id.text_pedido);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        text_pedido.setOnClickListener(this);
        btn_cancelar.setOnClickListener(this);

        text_pedido.setText(mensaje);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancelar:
                dismiss();
                break;
        }
    }

 }


