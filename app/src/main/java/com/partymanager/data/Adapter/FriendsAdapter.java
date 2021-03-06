package com.partymanager.data.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.data.DatiFriends;
import com.partymanager.data.Friends;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;

public class FriendsAdapter extends ArrayAdapter<Friends> {

    Context context;
    String idAdminEvento;

    public FriendsAdapter(Context context, ArrayList<Friends> utenti, String idAdminEvento) {
        super(context, R.layout.fb_friends, utenti);
        this.context = context;
        this.idAdminEvento = idAdminEvento;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fb_friends, parent, false);
        }

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        cb.setVisibility(View.GONE);
        ImageView foto_profilo = (ImageView) convertView.findViewById(R.id.img_profilo);
        TextView installed = (TextView) convertView.findViewById(R.id.txt_installed);
        installed.setVisibility(View.GONE);

        TextView name = (TextView) convertView.findViewById(R.id.txt_friends_name);
        name.setVisibility(View.VISIBLE);

        String text = DatiFriends.ITEMS.get(position).getName();

        if (DatiFriends.ITEMS.get(position).getCode().equals(idAdminEvento)) {
            text += context.getString(R.string.FriendsAdapter);
        }

        name.setText(text);
        name.setTextColor(Color.BLACK);

        foto_profilo.setImageBitmap(null);
        foto_profilo.setBackground(context.getResources().getDrawable(R.drawable.com_facebook_profile_default_icon));
        if (DatiFriends.ITEMS.get(position).foto != null) {
            foto_profilo.setBackground(null);
            foto_profilo.setImageBitmap(DatiFriends.ITEMS.get(position).getFoto());
        } else {
            HelperFacebook.getFacebookProfilePicture(DatiFriends.ITEMS.get(position), this, 1);
        }

        return convertView;
    }
}
