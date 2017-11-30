package com.chen.qrcode;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.zxing.CaptureActivity;
import com.chen.library.zxing.ZXingConstants;
import com.chen.library.zxing.utils.ZXingUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**th
 2017/8/27
 This project is based on the ZXingCodeDemo from the maning0303
 (Github: https://github.com/maning0303/MNZXingCode)
 Thank you for maning0303
 */


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView latitude;
    private TextView longitude;
    private TextView loctype;
    private TextView mRed;
    private TextView mGreen;
    private TextView mBlue;
    private ImageView imageView;
    private EditText editText;
    private CheckBox checkbox;
    private Button getRGB;
    private LocationManager locationManager;
    private String provider;
    private static int TAKE_PHOTO_REQUEST = 3;
    public int GPS_OK = 0;
    public int RGB_OK = 0;
    Bitmap RGBp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view,"Tap ---------------------->>", Snackbar.LENGTH_LONG)
                        .setAction("Send to Cloud", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //这里的单击事件代表点击消除Action后的响应事件
                                if ( GPS_OK == 1 && RGB_OK == 1){
                                    Toast.makeText(MainActivity.this, "Send successful", Toast.LENGTH_LONG).show();

                                    int f1=1;
                                    String key = "MI0NTSQFYYOFMYX0";
                                    String f1s = String.valueOf(f1);
                                    String lati = (String) latitude.getText();
                                    String longti = (String) longitude.getText();
                                    String Redstr = (String) mRed.getText();
                                    String Greenstr = (String) mGreen.getText();
                                    String Bluestr = (String) mBlue.getText();

                                    Thingspeak thingspeak = new Thingspeak();
                                    thingspeak.setKey(key);
                                    thingspeak.setfield("1");
                                    thingspeak.setData(f1s);
                                    thingspeak.setR(Redstr);
                                    thingspeak.setG(Greenstr);
                                    thingspeak.setB(Bluestr);
                                    thingspeak.setLatitude(lati);
                                    thingspeak.setLongitude(longti);
                                    new Thread(thingspeak, "Thread1").start();

                                }else{
                                    Toast.makeText(MainActivity.this, "GPS or RGB is not prepared well.", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .show();
            }
        });


        textView = (TextView) findViewById(R.id.tv_show);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        checkbox = (CheckBox) findViewById(R.id.checkbox);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        loctype = (TextView) findViewById(R.id.loctype);
        getRGB = (Button) findViewById(R.id.getRGB);
        mRed = (TextView) findViewById(R.id.R);
        mGreen = (TextView) findViewById(R.id.G);
        mBlue = (TextView) findViewById(R.id.B);

        getRGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,TAKE_PHOTO_REQUEST);
            }
        });
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.android_example));


        imageView.setOnTouchListener(new PicOnTouchListener());
        //GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
            loctype.setText("GPS location");
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
            loctype.setText("Network location");
        } else {
            Toast.makeText(this, "Please check GPS is opened?", Toast.LENGTH_LONG).show();
            return;
        }


        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            locationManager.requestLocationUpdates(provider, 2000, 2, locationListener);
        }

    }

    //OnTouch监听器
    private class PicOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent){
            int[] viewCoords = new int[2];
            imageView.getLocationOnScreen(viewCoords);
            int touchX = (int) motionEvent.getX();
            int touchY = (int) motionEvent.getY();
            int adjustedX = touchX/4;
            int adjustedY = touchY/3;


            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                if (RGBp != null){
                    if(adjustedX > RGBp.getWidth()){
                        adjustedX = RGBp.getWidth();
                    }
                    if(adjustedY > RGBp.getHeight()){
                        adjustedY = RGBp.getHeight();
                    }
                    //String X = String.valueOf(adjustedX);
                    //String Y = String.valueOf(adjustedY);
                    //Toast.makeText(MainActivity.this, "RGB photo existed", Toast.LENGTH_LONG).show();
                    //int bitmipwidth = RGBp.getWidth();
                    //int bitmipheith = RGBp.getHeight();
                    //String wi = String.valueOf(bitmipwidth);
                    //String hei = String.valueOf(bitmipheith);
                    int colour = RGBp.getPixel(adjustedX,adjustedY);
                    int red = Color.red(colour);
                    int blue = Color.blue(colour);
                    int green = Color.green(colour);
                    //int alpha = Color.alpha(colour);
                    String R = String.valueOf(red);
                    String G = String.valueOf(green);
                    String B = String.valueOf(blue);
                    mRed.setText(R);
                    mGreen.setText(G);
                    mBlue.setText(B);

                    RGB_OK = 1;

                }
                //String B = String.valueOf(blue);
            }
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result: grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "Need to get the permissions", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "Something Error", Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:

        }
    }



    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub
            // 更新当前经纬度
            //String la = "La is " + arg0.getLatitude();
            //String lo = "Lo is " + arg0.getLongitude();
            DecimalFormat df = new DecimalFormat("######0.00");
            double rawla = arg0.getLatitude();
            double rawlo = arg0.getLongitude();
            String adjustedla = df.format(rawla);
            String adjustedlo = df.format(rawlo);
            //String la =  arg0.getLatitude()+"";
            //String lo =  arg0.getLongitude()+"";
            latitude.setText(adjustedla);
            longitude.setText(adjustedlo);
            loctype.setText("Location success");
            GPS_OK = 1;
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }


    public void scanCode(View view) {

        Intent intent = new Intent(MainActivity.this,
                CaptureActivity.class);
        intent.putExtra(ZXingConstants.ScanIsShowHistory, true);
        startActivityForResult(intent, 0x001);
    }


    public void createQRImage(View view) {
        String str = editText.getText().toString();

        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, "String is not allowed to be NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap qrImage;
        if (checkbox.isChecked()) {
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            qrImage = ZXingUtils.createQRCodeWithLogo(str, logo);
        } else {
            qrImage = ZXingUtils.createQRImage(str);

            /*
            // auto save Photo
            //String bitName="QRcode"+ System.currentTimeMillis();
            String bitName="QRcode";
            File f = new File("/sdcard/" + bitName + ".png");
            //File f = new File( Environment.getExternalStorageDirectory(), "QRCodeImage");
            try {
                //创建
                f.createNewFile();
            } catch (IOException e) {

            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //原封不动的保存在内存卡上
            qrImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Auto save on /sdcard/QRCodeXXXX-XX-XX.jpg ", Toast.LENGTH_SHORT).show();
            //
            */

        }

        if (qrImage != null) {
            imageView.setImageBitmap(qrImage);
        } else {
            Toast.makeText(this, "Generate error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == ZXingConstants.ScanRequltCode) {
            /**
             * get the return string
             */
            String result = data.getStringExtra(ZXingConstants.ScanResult);
            //String photopath = data.getStringExtra(ZXingConstants.Scanphotopath);

            textView.setText(result);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }

            //Thingspeak thingspeak = new Thingspeak();
            //int f1=1;
            //String f1s = String.valueOf(f1);
            //thingspeak.getfield(f1s);
            //new Thread(thingspeak, "Thread1").start();

            //


        } else if (resultCode == ZXingConstants.ScanHistoryResultCode) {
            /**
             * history result
             */

            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (requestCode == TAKE_PHOTO_REQUEST){
            Bundle bundle = data.getExtras();
            RGBp = (Bitmap) bundle.get("data");
            //imageView.setImageBitmap(RGBp);
            //RGBp = bundle.getParcelable("data");
            //comp(RGBp);
            imageView.setImageBitmap(RGBp);
        }
    }

}
