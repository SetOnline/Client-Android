package com.pancake.setonline;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    // Gestion de l'�v�nement de r�ception du r�sultat de l'inscription. Appel� � partir d'un thread
    private Emitter.Listener onInscriptionResult = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String s = (String)args[0];
                    System.out.println("OnInscriptionResult !");
                    try {
                        JSONArray data = new JSONArray(s);

                        boolean adresse_mail = data.getJSONObject(0).getString("value").equals("true");
                        boolean pseudo = data.getJSONObject(1).getString("value").equals("true");
                        boolean mdp = data.getJSONObject(2).getString("value").equals("true");
                        boolean avatar = true; //data.getJSONObject(3).getString("value").equals("true";
                        if(adresse_mail && pseudo && mdp && avatar){
                            /*NotificationCompat.Builder mBuilder =
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
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());*/

                            //SocketManager.isNetGame = true;
                            //Intent intent = new Intent(getApplicationContext(), menuJeu_view.class);
                            //startActivity(intent);
                            Intent newIntent = new Intent(Inscription_view.this, Connexion.class);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(newIntent);
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

    /**
     *  Initialisation de la vue
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_view);

        // nodeJS, gestion de la communication client/serveur
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();
        SocketManager.mSocketIO.on("Resultat inscription", onInscriptionResult);
        // interface

        etPseudo = (EditText)findViewById(R.id.etPseudo);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etMail = (EditText)findViewById(R.id.etMail);

        ivAvatar = (ImageView)findViewById(R.id.ivAvatar);

        // cr�ation d'avatar (� partir d'une image ou d'une photo)
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

        // bouton d'inscription
        bt_validateInscription = (Button)findViewById(R.id.btValidate);
        bt_validateInscription.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // donn�es valides ?
                if(etPseudo.length() < 3 || etPseudo.length() > 8){
                    Toast.makeText(getBaseContext(), R.string.error_nickname_too_short, Toast.LENGTH_LONG).show();
                    etPseudo.requestFocus();
                    return;
                }

                if(!Profil_model.isValidEmailAddress(etMail.getText().toString())){
                    Toast.makeText(getBaseContext(), R.string.error_invalid_mail, Toast.LENGTH_LONG).show();
                    etMail.requestFocus();
                    return;
                }

                if(etPassword.getText().length() < 4 || etPassword.getText().length() > 20){
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

                    // TODO : ENVOYER L'AVATAR
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                inscription_packet.put(json_mail);
                inscription_packet.put(json_pseudo);
                inscription_packet.put(json_psswd);

                SocketManager.mSocketIO.emit("Creation compte", inscription_packet.toString());
            }
        });
    }

    /**
     * R�cup�ration de l'image ou de la photo (pour l'avatar)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_SELECT_PICTURE) { // Avatar depuis IMAGE
                Uri selectedImageUri = data.getData();
                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                bfOptions.inDither=false;          // D�sactiver le Dithering
                bfOptions.inPurgeable=true;        // Supprimable si il y a besoin de RAM
                bfOptions.inInputShareable=true;
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

                // redimension
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
            } else if(requestCode == ACTION_TAKE_PHOTO){ // avatar depuis PHOTO
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivAvatar.setImageBitmap(imageBitmap);*/

                Profil_model.createAppFolderIfNeeded();
                File f = new File(Profil_model.getAvatarFilename());

                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                bfOptions.inDither=false;          // D�sactiver le Dithering
                bfOptions.inPurgeable=true;        // Supprimable si il y a besoin de RAM
                bfOptions.inInputShareable=true;
                bfOptions.inSampleSize=5;
                bfOptions.inTempStorage=new byte[32 * 1024];

                InputStream stream = null;
                try {
                    stream = getContentResolver().openInputStream(Uri.fromFile(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                // redimension
                final Bitmap myImage = BitmapFactory.decodeStream(stream, null , bfOptions);
                Bitmap saved = Profil_model.getResizedBitmap(myImage, Profil_model.AVATAR_PICTURE_SIZE, Profil_model.AVATAR_PICTURE_SIZE);
                f.delete();

                try {
                    f.createNewFile();
                    // Conversion de la bitmap en tableau de BYTE
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    saved.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    // Sauvegarde dans un fichier
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
        SocketManager.mSocketIO.off("Resultat inscription");

        super.onDestroy();
    }
}
