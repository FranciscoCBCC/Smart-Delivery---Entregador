package com.example.dell.fichacadastral;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Dell on 20/07/2017.
 */

public class Form_Layout extends Fragment implements TextWatcher {
    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;
    private EditText edtRpSenha;

    private EditText edtCep;
    private EditText edtRua;
    private EditText edtCidade;
    private EditText edtComplemento;
    private EditText edtBairro;
    private ProgressBar progressBar;
    private Spinner spinner;
    private AddressTask addressTask;
    private RelativeLayout layout_pai;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.form_layout, container, false);

        spinner = (Spinner) view.findViewById(R.id.snp_Estados);
        configureSpinner();
        progressBar = (ProgressBar) view.findViewById(R.id.pgb_progress);

        //Associa os objetos aos respectivos ids do xml.
        edtNome = (EditText) view.findViewById(R.id.edt_nome);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtSenha = (EditText) view.findViewById(R.id.edt_senha);
        edtRpSenha = (EditText) view.findViewById(R.id.edt_rp_senha);

        insereDados(); //Método para inserção dos dados

        edtCep = (EditText) view.findViewById(R.id.edt_Cep);
        edtRua = (EditText) view.findViewById(R.id.edt_rua);
        edtCidade = (EditText) view.findViewById(R.id.edt_cidade);
        edtComplemento = (EditText) view.findViewById(R.id.edt_complemento);
        edtBairro = (EditText) view.findViewById(R.id.edt_bairro);
        layout_pai = (RelativeLayout) view.findViewById(R.id.layout_pai);

        edtCep.addTextChangedListener(this);

        if (isAdded()) {
            //verify if the fragment is attached at the activity
        }

        if (!JsonRequest.hasConnection(getActivity())) {
            edtCep.setText("No connection");
        }
        return view;

    }

    //Recebe os dados inseridos na caixa de texto e salva nas respectivas variáveis
    private void insereDados(){
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();
        String rpSenha = edtRpSenha.getText().toString();
    }

    private void configureSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.string_array_estados));


        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

    }

    /**
     * This method verify whether the field text has changed, if yes; it executes a search for
     * the Cep.
     *
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String zipCode = editable.toString();
        if (zipCode.length() == 8) {
            if (addressTask == null || addressTask.getStatus() != AsyncTask.Status.RUNNING) {
                addressTask = new AddressTask(zipCode);
                addressTask.execute();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    /**
     * this class will make a requisition.
     */
    public class AddressTask extends AsyncTask<Void, Void, LoadedAddress> {

        private final String url;

        public AddressTask(String cep) {
            this.url = "https://viacep.com.br/ws/" + cep + "/json/";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            edtCep.setEnabled(false);
            layout_pai.setBackgroundColor(getResources().getColor(R.color.background_color_loading));
            progressBar.setVisibility(true? View.VISIBLE:View.GONE);
        }

        @Override
        protected LoadedAddress doInBackground(Void... voids) {
            try {
                LoadedAddress loadedAddress = JsonRequest.loadJsonAddress(url);

                return loadedAddress;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(LoadedAddress address) {
            super.onPostExecute(address);
            progressBar.setVisibility(false? View.VISIBLE:View.GONE);
            layout_pai.setBackgroundColor(getResources().getColor(R.color.color_whight));
            if (getActivity() != null) {
                edtCep.setEnabled(true);

                if (address != null) {
                    edtCep.setText(address.getCep());
                    edtRua.setText(address.getLogradouro());
                    edtBairro.setText(address.getBairro());
                    edtComplemento.setText(address.getComplemento());
                    edtCidade.setText(address.getLocalidade());

                }
            }
        }

    }
}
