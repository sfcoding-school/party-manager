package com.partymanager.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.partymanager.R;
import com.partymanager.app.dummy.AttributiAdapter;
import com.partymanager.app.dummy.DatiAttributi;
import com.partymanager.app.dummy.DummyContent;
import com.partymanager.app.utility.EventDialog;

import java.util.ArrayList;

public class Evento extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String mParam1; //Lista amici
    private String mParam2; //nome evento
    private String mParam3; //id evento
    private ImageButton bnt_friends;

    AttributiAdapter eAdapter;
    ListView listView;
    View riepilogo;
    Button btn_Domanda;
    Button btn_sino;
    EventDialog eventDialog;
    TextView luogo;
    TextView quando_data;
    TextView quando_ora;
    TextView dove;


    private OnFragmentInteractionListener mListener;

    public static Evento newInstance(String param1, String param2, String param3) {
        Evento fragment = new Evento();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);

        return fragment;
    }

    public Evento() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);

            //Log.e("Evento TEST: ", mParam1 + " " + mParam2 + " " + mParam3);
        }

        eventDialog = new EventDialog(getActivity());
        eAdapter = DatiAttributi.init(getActivity(), mParam3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento, container, false);

        listView = (ListView) view.findViewById(R.id.eventList);
        listView.setOnItemClickListener(this);
        btn_Domanda = (Button) view.findViewById(R.id.btn_domanda);
        btn_sino = (Button) view.findViewById(R.id.btn_sino);
        riepilogo = view.findViewById(R.id.stickyheader);
        bnt_friends = (ImageButton) view.findViewById(R.id.imgButton_amici);
        luogo = (TextView) view.findViewById(R.id.txt_luogo);
        quando_data= (TextView) view.findViewById(R.id.txt_data);
        quando_ora= (TextView) view.findViewById(R.id.txt_orario);
        dove= (TextView) view.findViewById(R.id.txt_dove_vediamo);

        ArrayList<DatiAttributi.Attributo> prova = DatiAttributi.ITEMS;

        for (DatiAttributi.Attributo temp: prova){
            if (temp.template.equals("data")){
                quando_data.setText(temp.risposta);
            }
            if (temp.template.equals("luogoE")){
                luogo.setText(temp.risposta);
            }
            if (temp.template.equals("luogoI")){
                dove.setText(temp.risposta);
            }
        }

        //TEST
        String[] my_array = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, my_array);
        listView.setAdapter(adapter);
        //TEST

        //listView.setAdapter(eAdapter);

        btn_Domanda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeAlpha(btn_Domanda);
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), btn_Domanda);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Data"))
                            eventDialog.date().show();
                        if (item.getTitle().equals("Luogo"))
                            eventDialog.luogo().show();
                        if (item.getTitle().equals("Personalizzata"))
                            eventDialog.personalizzata().show();

                        return true;
                    }
                });

                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        changeAlpha(btn_Domanda);
                    }
                });

                popup.show();
            }
        });

        bnt_friends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        btn_sino.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                changeAlpha(btn_sino);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
               Log.e("listview click", "hai cliccato l'elemento " + Integer.toString(arg2 +1) );

            }
        });

        return view;
    }

    private void changeAlpha(Button btn) {
        float alpha = btn.getAlpha();
        if (Float.compare(alpha, (float) 0.7) == 0) {
            btn.setAlpha((float) 0.9);
        } else {
            btn.setAlpha((float) 0.7);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }
}
                            /*if (prova.getVisibility() != View.GONE && currentFirstVisibleItem > 8) {
                                TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -prova.getHeight());
                                anim.setDuration(500);
                                anim.setFillAfter(false);
                                prova.startAnimation(anim);
                                prova.setVisibility(View.GONE);
                            }
                            if (currentLAstVisibileItem <= 11) {
                                prova.setVisibility(View.VISIBLE);
                            }*/

/*
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {


            int lastVisible = 0;
            int firstVisible = 0;
            boolean animation = true;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                TranslateAnimation anim = null;
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL){
                    firstVisible = view.getFirstVisiblePosition();
                    if (view.getFirstVisiblePosition() > lastVisible){
                        if (animation) {
                            animation = false;
                            anim = new TranslateAnimation(0, 0, 0, -360);
                            //riepilogo.setVisibility(View.GONE);
                            //FirstItem = view.getFirstVisiblePosition();
                            //itemFirst = view.getChildAt(FirstItem);
                            anim.setDuration(500);
                            anim.setFillAfter(true);
                            riepilogo.startAnimation(anim);
                        }
                    }else{
                        if (animation){
                            animation = false;
                            anim = new TranslateAnimation(0, 0, -360, 0);
                            anim.setDuration(500);
                            anim.setFillAfter(true);
                            riepilogo.startAnimation(anim);

                        }
                    }
                    lastVisible = firstVisible;
                }
                else if (scrollState == SCROLL_STATE_IDLE){
                   animation = true;
                }


        }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
*/