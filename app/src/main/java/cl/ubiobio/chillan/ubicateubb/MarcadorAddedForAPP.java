package cl.ubiobio.chillan.ubicateubb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.ubiobio.chillan.ubicateubb.entities.Coordinates;
import cl.ubiobio.chillan.ubicateubb.entities.VolleySingleton;

public class MarcadorAddedForAPP extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    Button Volver;
    TextView textViewInfoDb, textViewInfoDb2, textViewVisita;;
    //RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    String extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcador_added_for_app);
        //requestQueue= Volley.newRequestQueue(getApplicationContext());

        Volver=findViewById(R.id.ButtonReturn);
        Volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        textViewInfoDb=findViewById(R.id.TextViewInfoQueryDB);
        textViewInfoDb2=findViewById(R.id.TextViewInfoQueryDB2);
        textViewVisita=findViewById(R.id.TextViewvisita);
        extras= getIntent().getStringExtra("title");
        TextView title = findViewById(R.id.title);
        title.setText(extras);

        cargarWebService();




    }

    private void cargarWebService() {
        String webHost=getString(R.string.webhost);
        String url =webHost+"/WebServiceQueryTitleByTitle.php?title="+extras;

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //requestQueue.add(jsonObjectRequest);
        VolleySingleton.getInstanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"No se pudo conectar "+error.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Coordinates coordinates = new Coordinates();
        JSONArray jsonArray = response.optJSONArray("coordinates");
        JSONObject jsonObject=null;
        try {
            jsonObject=jsonArray.getJSONObject(0);
            coordinates.setInformation(jsonObject.optString("information"));
            textViewInfoDb.setText(coordinates.getInformation());
            coordinates.setInformationtwo(jsonObject.optString("informationtwo"));
            textViewInfoDb2.setText(coordinates.getInformationtwo());
            coordinates.setLink(jsonObject.optString("link"));

            if (!coordinates.getLink().equalsIgnoreCase("")){
                textViewVisita.setText(Html.fromHtml("Para más información visita " + "<a href=\"http://"+coordinates.getLink()+"\">http://"+coordinates.getLink()+"</a> "));
                textViewVisita.setMovementMethod(LinkMovementMethod.getInstance());
            }


        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
