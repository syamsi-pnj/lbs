package pnj.ac.id.locationbase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {
	private static final int request_code = 100;
	double lat = 0;
	double lng = 0;
	long minTime;
	float minDistance;
	String locProvider;
	LocationManager locMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


		if(periksaIzinPenyimpanan()) {
			locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			locProvider = LocationManager.NETWORK_PROVIDER;
			@SuppressLint("MissingPermission") Location lastKnownLocation = locMgr.getLastKnownLocation(locProvider);
			try {
				lat = lastKnownLocation.getLatitude();
				lng = lastKnownLocation.getLongitude();

			}catch (Exception ex){
				lat = 0.0;
				lng = 0.0;

			}


			Criteria cr = new Criteria();
			cr.setAccuracy(Criteria.ACCURACY_FINE);

			locProvider = locMgr.getBestProvider(cr, false);
			minTime = 5 * 1000;
			minDistance = 1;
		}

    }

	@SuppressLint("MissingPermission")
	@Override
	protected void onResume() {
		super.onResume();
		locMgr.requestLocationUpdates(locProvider, minTime, minDistance, this);
	}

	boolean periksaIzinPenyimpanan() {
		if(Build.VERSION.SDK_INT >= 24) {
			if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
				return true;
			}else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, request_code);
				return false;
			}
		}else {
			return  true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case request_code:
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
					Toast.makeText(getApplicationContext(), "Izin Berhasil", Toast.LENGTH_SHORT).show();
				}
				break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
	}

	@Override
	public void onLocationChanged(@NonNull Location loc) {
		lat = loc.getLatitude();
		lng = loc.getLongitude();

		Time now = new Time();
		now.setToNow();
		Toast.makeText(getApplicationContext(),"lat: "+lat+"-lon:"+lng+"Direfresh berdasarkan:  "+locProvider
			+" Waktu: "+now.hour+":"+now.minute+":"+now.second, Toast.LENGTH_SHORT).show();
	}
}
