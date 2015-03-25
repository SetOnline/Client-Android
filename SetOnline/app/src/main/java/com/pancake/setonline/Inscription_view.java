package com.pancake.setonline;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;


public class Inscription_view extends ActionBarActivity {
    private Button bt_loadAvatar;
    private ImageView ivAvatar;

    private Button bt_loadAvatarCamera;

    private Button bt_validateInscription;

    private EditText etPseudo;
    private EditText etPassword;
    private EditText etMail;

    private static final int ACTION_SELECT_PICTURE = 1;
    private static final int ACTION_TAKE_PHOTO = 2;

    // NodeJS
    private Socket mSocket;

    private Emitter.Listener onInscriptionResult = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String s = (String)args[0];
                    try {
                        JSONArray data = new JSONArray(s);

                        boolean adresse_mail = data.getJSONObject(0).getString("value").equals("true");
                        boolean pseudo = data.getJSONObject(1).getString("value").equals("true");
                        boolean mdp = data.getJSONObject(2).getString("value").equals("true");
                        boolean avatar = true; //data.getJSONObject(3).getString("value").equals("true";
                        if(adresse_mail && pseudo && mdp && avatar){
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getBaseContext())
                                            .setSmallIcon(R.drawable.ic_launcher2)
                                            .setContentTitle(getString(R.string.notif_title_inscription))
                                            .setContentText(getString(R.string.notif_inscription));
                            // Sets an ID for the notification
                            int mNotificationId = 001;
                            // Gets an instance of the NotificationManager service
                            NotificationManager mNotifyMgr =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            // Builds the notification and issues it.
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());

                            Intent intent = new Intent(getApplicationContext(), menuJeu_view.class);
                            startActivity(intent);
                        } else {
                            if(!adresse_mail){
                                Toast.makeText(Inscription_view.this, getString(R.string.error_invalid_mail), Toast.LENGTH_LONG).show();
                                etPseudo.requestFocus();
                            }

                            if(!pseudo){
                                Toast.makeText(Inscription_view.this, getString(R.string.error_nickname_too_short), Toast.LENGTH_LONG).show();
                                etPseudo.requestFocus();
                            }

                            if(!mdp){
                                Toast.makeText(Inscription_view.this, getString(R.string.error_password_too_short), Toast.LENGTH_LONG).show();
                                etPassword.requestFocus();
                            }

                            if(!avatar){
                                // can't happen here
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_view);

        // nodeJS, gestion de la communication client/serveur
        try {
            mSocket = IO.socket(new URI("http://37.59.123.190:1337"));
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("error initializing mSocket");
            Toast.makeText(Inscription_view.this, "Serveur hors ligne :(", Toast.LENGTH_LONG).show();
        }

        System.out.println("COUCOU");System.out.println("COUCOU");
        System.out.println("COUCOU");System.out.println("COUCOU");
        System.out.println("COUCOU");System.out.println("COUCOU");
        System.out.println("COUCOU");System.out.println("COUCOU");
        System.out.println("COUCOU");System.out.println("COUCOU");

        mSocket.connect();

        mSocket.on("Resultat inscription", onInscriptionResult);
        // interface

        etPseudo = (EditText)findViewById(R.id.etPseudo);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etMail = (EditText)findViewById(R.id.etMail);

        ivAvatar = (ImageView)findViewById(R.id.ivAvatar);

        bt_loadAvatar = (Button)findViewById(R.id.btUploadAvatar);
        bt_loadAvatar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), ACTION_SELECT_PICTURE);
            }
        });

        bt_loadAvatarCamera = (Button)findViewById(R.id.btAvatarCamera);
        bt_loadAvatarCamera.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Profil_model.createAppFolderIfNeeded();
                File f = new File(Profil_model.getAvatarFilename());
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
                }

            }
        });

        bt_validateInscription = (Button)findViewById(R.id.btValidate);
        bt_validateInscription.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // valid data ?
                if(etPseudo.length() < 3){
                    Toast.makeText(getBaseContext(), R.string.error_nickname_too_short, Toast.LENGTH_LONG).show();
                    etPseudo.requestFocus();
                    return;
                }

                if(!Profil_model.isValidEmailAddress(etMail.getText().toString())){
                    Toast.makeText(getBaseContext(), R.string.error_invalid_mail, Toast.LENGTH_LONG).show();
                    etMail.requestFocus();
                    return;
                }

                if(etPassword.getText().length() < 10){
                    Toast.makeText(getBaseContext(), R.string.error_password_too_short, Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                    return;
                }

                JSONArray inscription_packet = new JSONArray();
                JSONObject json_pseudo = new JSONObject();
                JSONObject json_mail = new JSONObject();
                JSONObject json_psswd = new JSONObject();
                try {
                    json_pseudo.put("name", "pseudo");
                    json_pseudo.put("value", etPseudo.getText().toString());

                    json_mail.put("name", "adresse_mail");
                    json_mail.put("value", etMail.getText().toString());

                    // TODO : CRYPTAGE ?
                    json_psswd.put("name", "mdp");
                    json_psswd.put("value", etPassword.getText().toString());

                    // TODO : SEND AVATAR
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                inscription_packet.put(json_mail);
                inscription_packet.put(json_pseudo);
                inscription_packet.put(json_psswd);

                mSocket.emit("Creation compte", inscription_packet.toString());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                bfOptions.inDither=false;          //Disable Dithering mode
                bfOptions.inPurgeable=true;        //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                bfOptions.inInputShareable=true;   //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                bfOptions.inSampleSize=5;
                bfOptions.inTempStorage=new byte[32 * 1024];
                InputStream stream = null;
                try {
                    stream = getContentResolver().openInputStream(selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                final Bitmap myImage = BitmapFactory.decodeStream(stream, null , bfOptions);
                //ByteArrayOutputStream bos = new ByteArrayOutputStream();
                //myImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                //bitmapdata = bos.toByteArray();
                ivAvatar.setImageBitmap(myImage);

                Bitmap saved = Profil_model.getResizedBitmap(myImage, Profil_model.AVATAR_PICTURE_SIZE, Profil_model.AVATAR_PICTURE_SIZE);
                Profil_model.createAppFolderIfNeeded();
                File f = new File(Profil_model.getAvatarFilename());

                try {
                    f.createNewFile();
                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    saved.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == ACTION_TAKE_PHOTO){
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivAvatar.setImageBitmap(imageBitmap);*/

                Profil_model.createAppFolderIfNeeded();
                File f = new File(Profil_model.getAvatarFilename());

                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                bfOptions.inDither=false;          //Disable Dithering mode
                bfOptions.inPurgeable=true;        //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                bfOptions.inInputShareable=true;   //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                bfOptions.inSampleSize=5;
                bfOptions.inTempStorage=new byte[32 * 1024];

                InputStream stream = null;
                try {
                    stream = getContentResolver().openInputStream(Uri.fromFile(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                final Bitmap myImage = BitmapFactory.decodeStream(stream, null , bfOptions);
                Bitmap saved = Profil_model.getResizedBitmap(myImage, Profil_model.AVATAR_PICTURE_SIZE, Profil_model.AVATAR_PICTURE_SIZE);
                f.delete();

                try {
                    f.createNewFile();
                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    saved.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivAvatar.setImageBitmap(saved);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription_view, menu);
        return true;
    }

    @Override
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

    public void onDestroy() {
        super.onDestroy();

        // déconnexion du socket
        mSocket.disconnect();

        mSocket.off("Compte cree");
        mSocket.off("Compte pas cree");
    }
}
