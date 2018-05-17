package owl.app.catalogo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import owl.app.catalogo.R;
import owl.app.catalogo.api.Api;
import owl.app.catalogo.api.RequestHandler;

public class RegistrarseActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText user;
    private EditText pass;
    private EditText mail;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        user = (EditText)findViewById(R.id.editTextName);
        pass = (EditText)findViewById(R.id.editTextPassword);
        mail = (EditText)findViewById(R.id.editTextMail);
        login = (Button)findViewById(R.id.btnLogin);

        login.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        createUsuario();
    }

    private void createUsuario() {
        String nombre = user.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String email = mail.getText().toString().trim();


        if (TextUtils.isEmpty(nombre)) {
            user.setError("Escribe tu nombre");
            user.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            pass.setError("Escribe una contraseña");
            pass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            pass.setError("Escribe tu email");
            pass.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("usuarios", nombre);
        params.put("password", password);
        params.put("role", "cliente");
        params.put("mail", email);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_USUARIO, params, Api.CODE_POST_REQUEST);
        request.execute();

        Toast.makeText(RegistrarseActivity.this,"usuario registrado", Toast.LENGTH_LONG).show();
    }

    // clase interna para realizar la solicitud de red extendiendo un AsyncTask
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        // la url donde necesitamos enviar la solicitud
        String url;

        //the parameters
        HashMap<String, String> params;

        // el código de solicitud para definir si se trata de un GET o POST
        int requestCode;

        // constructor para inicializar valores
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        // este método dará la respuesta de la petición
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    // refrescar la lista después de cada operación
                    // para que obtengamos una lista actualizada

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // la operación de red se realizará en segundo plano
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == Api.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrarseActivity.this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
