package ricardopazdemiquel.com.imotosconductorFinal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ricardopazdemiquel.com.imotosconductorFinal.R;


public class fr_perfil extends Fragment {

      public fr_perfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fr_perfil, container, false);
    }

}
