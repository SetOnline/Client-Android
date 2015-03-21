package com.pancake.setonline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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


public class ModificationProfil_view extends ActionBarActivity {
    // NodeJS
    private Socket mSocket;

    private Button bt_loadAvatar;
    private Button bt_loadAvatarCamera;
    private ImageView ivAvatar;

    private Button bt_validate;

    private EditText etOldPassword;
    private EditText etNewPassword;

    private static final int ACTION_SELECT_PICTURE = 1;
    private static final int ACTION_TAKE_PHOTO = 2;

    private String avatar_filename = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_profil_view);

        // nodeJS, gestion de la communication client/serveur
        try {
            mSocket = IO.socket(new URI("http://37.59.123.190:1337"));
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("error initializing mSocket");
            Toast.makeText(ModificationProfil_view.this, "Serveur hors ligne :(", Toast.LENGTH_LONG).show();
        }

        mSocket.connect();

        ivAvatar = (ImageView)findViewById(R.id.ivAvatar);

        bt_validate = (Button)findViewById(R.id.btValidateProfilModif);
        bt_validate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(etNewPassword.getText().length() < 10){
                    Toast.makeText(getBaseContext(), R.string.error_password_too_short, Toast.LENGTH_LONG).show();
                    etNewPassword.requestFocus();
                    return;
                }

                JSONArray profile_modification_packet = new JSONArray();
                JSONObject json_NewPsswd = new JSONObject();
                JSONObject json_OldPsswd = new JSONObject();
                try {
                    // TODO : CRYPTAGE ?
                    json_NewPsswd.put("name", "ancienmdp");
                    json_NewPsswd.put("value", etNewPassword.getText().toString());

                    json_OldPsswd.put("name", "nouveaumdp");
                    json_OldPsswd.put("value", etOldPassword.getText().toString());

                    // TODO : SEND AVATAR
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                profile_modification_packet.put(json_OldPsswd);
                profile_modification_packet.put(json_NewPsswd);

                mSocket.emit("Creation compte", profile_modification_packet.toString());

                // TODO : SEND NEW AVATAR
            }
        });

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
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File f = new File(storageDir.getPath(), "profil.jpg");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
                }
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

                Bitmap saved = Profil_model.getResizedBitmap(myImage, 150, 150);
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File f = new File(storageDir.getPath(), "profil.jpg");
                //Toast.makeText(getApplicationContext(), storageDir.getPath() + "/profil.jpg", Toast.LENGTH_SHORT).show();
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

                    avatar_filename = storageDir.getPath() + "/profil.jpg";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == ACTION_TAKE_PHOTO){
                /*Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivAvatar.setImageBitmap(imageBitmap);*/

                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

                //create a file to write bitmap data
                File f = new File(storageDir.getPath(), "profil.jpg");

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
                Bitmap saved = Profil_model.getResizedBitmap(myImage, 150, 150);
                f.delete();


                //Toast.makeText(getApplicationContext(), storageDir.getPath() + "/profil.jpg", Toast.LENGTH_SHORT).show();
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

                    avatar_filename = storageDir.getPath() + "/profil.jpg";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                avatar_filename = storageDir.getPath() + "/profil.jpg";
                ivAvatar.setImageBitmap(saved);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modification_mot_de_passe_view, menu);
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
    }
}
