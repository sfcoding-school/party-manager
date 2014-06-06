package com.partymanager.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.partymanager.R;

import java.util.ArrayList;

public class RisposteAdapter extends ArrayAdapter<DatiRisposte.Risposta> {

    public RisposteAdapter(Context context, ArrayList<DatiRisposte.Risposta> Risposta) {
        super(context, R.layout.risposta_row, Risposta);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.risposta_row, parent, false);
        }
        TextView risp = (TextView) convertView.findViewById(R.id.txt_risposta2);
        risp.setText(DatiRisposte.ITEMS.get(position).risposta);

        return convertView;
    }
}
