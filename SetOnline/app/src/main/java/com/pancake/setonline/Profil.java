package com.pancake.setonline;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;


public class Profil extends ActionBarActivity {
    private TabHost myTabHost;
    private float lastSwipePosX = 0.f;

    private ListView lvFriends;
    private FriendListAdapter lv_adapter_friends;

    private ListView lvMedals;
    private TrophyListAdapter lv_adapter_medals;

    private ListView lvTrophies;
    private TrophyListAdapter lv_adapter_trophies;

    private ImageView ivPlayerAvatar;

    private ImageView ivAddNewFriend;
    private EditText etNewFriendName;

    private Emitter.Listener onGetListeAmis = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String newList[];
                    try {
                        JSONArray ja = new JSONArray((String)args[0]);
                        newList = new String[ja.length()];

                        for(int i = 0; i != ja.length(); ++i){
                            newList[i] = ja.getJSONObject(i).getString("name") + "\n" + (ja.getJSONObject(i).getBoolean("status") ? "1" : "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    lv_adapter_friends = new FriendListAdapter(getBaseContext(), newList);
                    lvFriends.setAdapter(lv_adapter_friends);
                }
            });
        }
    };

    private Emitter.Listener onGetListeTrophees = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONArray ja = new JSONArray((String)args[0]);
                        String newList[] = new String[ja.length()];

                        for(int i = 0; i != ja.length(); ++i){
                            newList[i] = ja.getJSONObject(i).getString("pic") + "\n" + ja.getJSONObject(i).getString("desc");
                        }

                        lv_adapter_trophies = new TrophyListAdapter(getBaseContext(), newList);
                        lvTrophies.setAdapter(lv_adapter_trophies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onGetListeMedailles = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONArray ja = new JSONArray((String)args[0]);
                        String newList[] = new String[ja.length()];

                        for(int i = 0; i != ja.length(); ++i){
                            newList[i] = ja.getJSONObject(i).getString("pic") + "\n" + ja.getJSONObject(i).getString("desc");
                        }

                        lv_adapter_medals = new TrophyListAdapter(getBaseContext(), newList);
                        lvMedals.setAdapter(lv_adapter_medals);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onReponseDemandeAmi = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    System.out.println("Reponse demande ami");
                    int i = (Integer)args[0];
                    if(i == 1){
                        AlertDialog.Builder dlgAddFriend = new AlertDialog.Builder(Profil.this);
                        dlgAddFriend.setMessage("Demande envoyée");
                        dlgAddFriend.setTitle("Set!");
                        dlgAddFriend.setPositiveButton("OK", null);
                        dlgAddFriend.setCancelable(false);
                        dlgAddFriend.create().show();
                    } else {
                        AlertDialog.Builder dlgAddFriend = new AlertDialog.Builder(Profil.this);
                        dlgAddFriend.setMessage("Échec de la demande");
                        dlgAddFriend.setTitle("Set!");
                        dlgAddFriend.setPositiveButton("OK", null);
                        dlgAddFriend.setCancelable(false);
                        dlgAddFriend.create().show();
                    }
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        myTabHost =(TabHost) findViewById(R.id.tabHost);
        myTabHost.setup();

        TabHost.TabSpec battery_tab_spec = myTabHost.newTabSpec("trophies");

        battery_tab_spec.setContent(R.id.tab1);
        battery_tab_spec.setIndicator("Trophées");
        myTabHost.addTab(battery_tab_spec);

        // Set Tab Specification for Network Tab
        TabHost.TabSpec network_tab_spec = myTabHost.newTabSpec("medals");

        network_tab_spec.setContent(R.id.tab2);
        network_tab_spec.setIndicator("Médailles");
        myTabHost.addTab(network_tab_spec);

        // Set Tab Specification for Device Tab
        TabHost.TabSpec device_tab_spec = myTabHost.newTabSpec("friends");

        device_tab_spec.setContent(R.id.tab3);
        device_tab_spec.setIndicator("Amis");
        myTabHost.addTab(device_tab_spec);

        myTabHost.setOnTabChangedListener(new AnimatedTabHostListener(getBaseContext(), myTabHost));

        ivPlayerAvatar = (ImageView)findViewById(R.id.ivProfilPlayerAvatar);

        File imgFile = new  File(Profil_model.getAvatarFilename());

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivPlayerAvatar.setImageBitmap(myBitmap);
        }

        ivAddNewFriend = (ImageView)findViewById(R.id.ivAddNewFriend);
        etNewFriendName = (EditText)findViewById(R.id.etAddFriendName);

        lvFriends = (ListView)findViewById(R.id.lvFriends);
        lvMedals = (ListView)findViewById(R.id.lvMedals);
        lvTrophies = (ListView)findViewById(R.id.lvTrophies);

        // NodeJS

        ivAddNewFriend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                SocketManager.mSocketIO.emit("Demander ami", etNewFriendName.getText().toString());
            }
        });

        SocketManager.mSocketIO.on("Reponse demande ami", onReponseDemandeAmi);
        SocketManager.mSocketIO.on("Reponse liste amis", onGetListeAmis);
        SocketManager.mSocketIO.on("Reponse liste trophees", onGetListeTrophees);
        SocketManager.mSocketIO.on("Reponse liste medailles", onGetListeMedailles);

        SocketManager.mSocketIO.emit("Demande liste amis");
        SocketManager.mSocketIO.emit("Demande liste trophees");
        SocketManager.mSocketIO.emit("Demande liste medailles");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // SWIPE TABS
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                lastSwipePosX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = event.getX();

                if (lastSwipePosX < currentX - 250) {
                    myTabHost.setCurrentTab(myTabHost.getCurrentTab() - 1);
                }

                if (lastSwipePosX > currentX + 250) {
                    myTabHost.setCurrentTab(myTabHost.getCurrentTab() + 1);
                }

                break;
            }
        }
        return false;
    }

    public void onDestroy(){
        SocketManager.mSocketIO.off("Reponse liste amis");
        SocketManager.mSocketIO.off("Reponse liste trophees");
        SocketManager.mSocketIO.off("Reponse liste medailles");
        SocketManager.mSocketIO.off("Reponse demande ami");
        super.onDestroy();
    }

    /////////////////////////////////
    // TAB ANIMATION MANAGEMENT
    /////////////////////////////////
    public class AnimatedTabHostListener implements TabHost.OnTabChangeListener {

        private static final int ANIMATION_TIME = 240;
        private TabHost tabHost;
        private View previousView;
        private View currentView;
        private GestureDetector gestureDetector;
        private int currentTab;

        public AnimatedTabHostListener(Context context, TabHost tabHost){
            this.tabHost = tabHost;
            this.previousView = tabHost.getCurrentView();
            gestureDetector = new GestureDetector(context, new SwipeGestureDetector());
            tabHost.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event){
                    if (gestureDetector.onTouchEvent(event)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
        }

        public void onTabChanged(String tabId){
            currentView = tabHost.getCurrentView();
            if (tabHost.getCurrentTab() > currentTab){
                previousView.setAnimation(outToLeftSlide());
                currentView.setAnimation(inFromRightSlide());
            } else {
                previousView.setAnimation(outToRightSlide());
                currentView.setAnimation(inFromLeftSlide());
            }
            previousView = currentView;
            currentTab = tabHost.getCurrentTab();

        }

        private Animation inFromRightSlide(){
            Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f);
            return setProperties(inFromRight);
        }

        private Animation outToRightSlide(){
            Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            return setProperties(outToRight);
        }

        private Animation inFromLeftSlide(){
            Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f);
            return setProperties(inFromLeft);
        }

        private Animation outToLeftSlide(){
            Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            return setProperties(outtoLeft);
        }

        private Animation setProperties(Animation animation){
            animation.setDuration(ANIMATION_TIME);
            animation.setInterpolator(new AccelerateInterpolator());
            return animation;
        }

        class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
            private static final int SWIPE_MIN_DIST = 120;
            private static final int MAX_SWIPE = 250;
            private static final int SWIPE_SPEED = 200;
            private int nbTabs;

            public SwipeGestureDetector(){
                nbTabs = tabHost.getTabContentView().getChildCount();
            }

            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
                int newTab = 0;
                if (Math.abs(event1.getY() - event2.getY()) <= MAX_SWIPE){
                    return false;
                }
                if (event1.getX() - event2.getX() >= SWIPE_MIN_DIST && Math.abs(velocityX) <= SWIPE_SPEED){
                    newTab = currentTab + 1;
                } else if (event2.getX() - event1.getX() >= SWIPE_MIN_DIST
                            && Math.abs(velocityX) <= SWIPE_SPEED){
                    newTab = currentTab - 1;
                }
                if (newTab == 0 || newTab < nbTabs){
                    return false;
                }
                tabHost.setCurrentTab(newTab);
                return super.onFling(event1, event2, velocityX, velocityY);
            }
        }
    }

    private class FriendListAdapter extends ArrayAdapter<String> {

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.ami_row_layout, parent, false);

            TextView tvPseudo = (TextView) rowView.findViewById(R.id.tvPseudoAmi);
            ImageView ivConnected = (ImageView) rowView.findViewById(R.id.ivConnected);

            String data[] = getItem(position).split("\n");
            String pseudo = data[0];
            boolean connected = (data[1].charAt(0) == '1');

            if(convertView == null ) {
                tvPseudo.setText(pseudo);
                if(connected) ivConnected.setBackgroundResource(R.drawable.ic_connected);
            } else
                rowView = (View)convertView;

            return rowView;
        }

        public FriendListAdapter(Context context, String[] values) {
            super(context, R.layout.ami_row_layout, values);
        }
    }

    private class TrophyListAdapter extends ArrayAdapter<String> {

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.trophee_row_layout, parent, false);

            ImageView ivTropPic = (ImageView)rowView.findViewById(R.id.ivTropheePic);
            TextView tvTropDesc = (TextView)rowView.findViewById(R.id.tvTropheeDesc);

            String data[] = getItem(position).split("\n");
            String picName = data[0];
            String Desc = data[1];

            if(convertView == null ) {
                tvTropDesc.setText(Desc);
                ivTropPic.setBackgroundResource(getResources().getIdentifier(picName, "drawable", getPackageName()));
            } else
                rowView = (View)convertView;

            return rowView;
        }

        public TrophyListAdapter(Context context, String[] values) {
            super(context, R.layout.trophee_row_layout, values);
        }
    }
}

