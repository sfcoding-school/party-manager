package com.partymanager.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.partymanager.R;
import com.partymanager.app.dummy.FbFriendsAdapter;
import com.partymanager.app.helper.helperFacebook;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreaEventoActivity extends Activity {

    //Layout Elements
    ImageButton finito;
    EditText nome_evento;
    TextView container_friends;
    ListView listView;
    EditText inputSearch;
    ProgressBar pb;

    ArrayList<Friends> friendList;
    FbFriendsAdapter dataAdapter = null;
    ArrayList<Friends> friendsList;
    List<GraphUser> friends;
    List<Friends> finali = null;
    NetworkInfo networkInfo;
    ArrayList<String> id_toSend;
    public final String REG_ID = "reg_id";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);

        //SET LAYOUT
        finito = (ImageButton) findViewById(R.id.imageButton);
        nome_evento = (EditText) findViewById(R.id.etxt_nome_evento);
        container_friends = (TextView) findViewById(R.id.txt_container_friends);
        container_friends.setText("");
        listView = (ListView) findViewById(R.id.listView1);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        pb = (ProgressBar) findViewById(R.id.progressBar_creaEvento);
        pb.setVisibility(ProgressBar.VISIBLE);

        //setto networkInfo per controllo accesso a internet
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        //Controllo sessione FB
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            //se c'è la sessione e internet accessibile richiedo subito la lista amici
            if (networkInfo != null && networkInfo.isConnected()) {
                requestMyAppFacebookFriends(session);
            } else {
                Toast.makeText(getApplicationContext(), "Connessione assente", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Problema sessione FB", Toast.LENGTH_LONG).show();
        }

        updateView();
    }

    //Richiesta amici FB
    private void requestMyAppFacebookFriends(Session session) {
        Request friendsRequest = createRequest(session);
        friendsRequest.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                friends = getResults(response);
                friendsList = new ArrayList<Friends>();

                for (GraphUser user : friends) {
                    //controllo chi ha l'app installata
                    Boolean install = false;
                    if (user.getProperty("installed") != null && user.getProperty("installed").toString().equals("true")) {
                        install = true;
                    }

                    Friends friend = new Friends(user.getId(), user.getName(), false, install);
                    friendsList.add(friend);
                }

                pb.setVisibility(ProgressBar.INVISIBLE);

                dataAdapter = new FbFriendsAdapter(CreaEventoActivity.this, container_friends, inputSearch, R.layout.fb_friends, friendsList);
                listView.setAdapter(dataAdapter);
                friendList = dataAdapter.friendList;

            }
        });
        friendsRequest.executeAsync();
    }

    private Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "me/friends", null);

        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[]{"id", "name", "installed"};
        fields.addAll(Arrays.asList(requiredFields));

        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);

        return request;
    }

    private List<GraphUser> getResults(Response response) {
        GraphMultiResult multiResult = response
                .getGraphObjectAs(GraphMultiResult.class);
        GraphObjectList<GraphObject> data = multiResult.getData();
        return data.castToListOf(GraphUser.class);
    }
    //FINE Richiesta amici FB

    private void updateView() {

        //Listener EditText per ricerca amici
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                int textlength = cs.length();
                ArrayList<Friends> tempArrayList = new ArrayList<Friends>();
                if (friendList != null && friendList.size() > 0) {
                    for (Friends friends1 : friendList) {
                        if (textlength <= friends1.getName().length()) {
                            if (friends1.getName().toLowerCase().contains(cs.toString().toLowerCase())) {
                                tempArrayList.add(friends1);
                            }
                        }
                    }
                    dataAdapter = new FbFriendsAdapter(CreaEventoActivity.this, container_friends, inputSearch, R.layout.fb_friends, tempArrayList);
                    listView.setAdapter(dataAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        finito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finali = dataAdapter.getFinali();
                if ("".equals(nome_evento.getText().toString())/* || finali.isEmpty() */) { //controllo se inserito almeno un amico.. da rimettere poi
                    StringBuilder output = new StringBuilder();
                    if ("".equals(nome_evento.getText().toString())) {
                        output.append("Devi inserire un nome per l'evento.");
                    }
                    if (finali.isEmpty()) {
                        if (output.length() != 0)
                            output.append("\n");
                        output.append("Devi inserire almeno un amico");
                    }
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
                } else {
                    id_toSend = new ArrayList<String>();
                    StringBuilder id_to_invite = new StringBuilder();
                    for (Friends aFinali : finali){
                        if (aFinali.getAppInstalled()){
                            id_toSend.add(aFinali.getCode());
                        } else {
                            String temp = aFinali.getCode();
                            if (id_to_invite.length() != 0)
                                temp = ", " + temp;

                            id_to_invite.append(temp);
                        }
                    }
                    //for (Friends aFinali : finali) id_toSend.add(aFinali.getCode());
                    JSONArray jsArray = new JSONArray(id_toSend);

                    final SharedPreferences prefs = getPreferences();
                    String registrationId = prefs.getString(REG_ID, "");
                    if (registrationId.isEmpty()) {
                        Log.e("DEBUG ID: ", "problema REG_ID vuoto");
                    } else {
                        Log.e("TESTJSON: ", jsArray.toString());
                        Log.e("TESTJSON: ", id_to_invite.toString());

                        sendNewEvent(nome_evento.getText().toString(), registrationId, jsArray.toString());
                        sendInviti(id_to_invite.toString());
                    }
                }
            }
        });
    }

    private void sendInviti(String temp){
        WebDialog f  = helperFacebook.inviteFriends(this, temp);
        f.show();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(ProfileActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void sendNewEvent(final String name, final String ID_FB, final String List) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                String ris = null;
                Log.e("CreaEvento-sendToServer: ", "entrato");
                // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://androidpartymanager.herokuapp.com/addEvent");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("name", name));
                    nameValuePairs.add(new BasicNameValuePair("userList", List));
                    nameValuePairs.add(new BasicNameValuePair("admin", ID_FB));
                    Log.e("CreaEvento-sendToServer: ", name + " " + List + " " + ID_FB);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    ris = EntityUtils.toString(response.getEntity());

                    Log.e("CreaEvento-sendToServer-Risposta: ", ris);
                } catch (ClientProtocolException e) {
                    Log.e("CreaEvento-sendToServer: ", "catch 1");
                } catch (IOException e) {
                    Log.e("CreaEvento-sendToServer: ", "catch 2 ");
                }
                return ris;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (!isInteger(result)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreaEventoActivity.this);
                        alertDialogBuilder.setMessage("Problema nella creazione dell'evento.");

                        // set positive button: Yes message
                        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        // set negative button: No message
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        closeActivity(List, name, result);
                    }
                }
            }

            private boolean isInteger(String s) {
                try {
                    Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPreExecute() {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                if (getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                progressDialog = new ProgressDialog(CreaEventoActivity.this);
                progressDialog.setMessage("Creazione Evento");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

        }.execute();
    }

    private void closeActivity(String List, String nome_evento, String id_evento) {
        Intent intent = new Intent();
        intent.putExtra("listfriend", List);
        intent.putExtra("nome_evento", nome_evento);
        intent.putExtra("id_evento", id_evento);
        setResult(0, intent);

        finish();
    }

    //Click pulsante indietro
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreaEventoActivity.this);
        alertDialogBuilder.setMessage("Eliminare nuovo evento?");

        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CreaEventoActivity.this.finish();
            }
        });

        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}