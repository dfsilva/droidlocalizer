package br.com.diegosilva.droidlocalizer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import br.com.diegosilva.droidlocalizer.utils.Constantes;
import br.com.diegosilva.droidlocalizer.utils.HttpClientUtils;

public class Servico extends Service {

	private Handler mHandler = new Handler();
	private LocationManager lm;
	private Location ultimaLocation;

	private LocationListener ll = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("ANDROID_LOCALIZER", new Date() + "Status Changed");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("ANDROID_LOCALIZER", new Date() + "Provider Enabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("ANDROID_LOCALIZER", new Date() + "Provider disabled");
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.i("ANDROID_LOCALIZER", new Date() + "Localizacao Alterada");

			if (ultimaLocation == null) {
				registrarPosicao(location, location.getLongitude(),
						location.getLatitude());
			} else {
				BigDecimal lat = BigDecimal.valueOf(location.getLatitude());
				BigDecimal lon = BigDecimal.valueOf(location.getLongitude());

				BigDecimal latAnt = BigDecimal.valueOf(ultimaLocation
						.getLatitude());
				BigDecimal lonAnt = BigDecimal.valueOf(ultimaLocation
						.getLongitude());
				if (!latAnt.equals(lat) || !lonAnt.equals(lon)) {
					registrarPosicao(location, location.getLongitude(),
							location.getLatitude());
				}
			}
			getLocationManager().removeUpdates(ll);
		}
	};

	private void registrarPosicao(Location location, double lon, double lat) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put(
				"id_usuario",
				getSharedPreferences(Constantes.APP_NAME_KEY, 0).getString(
						Constantes.ID_USUARIO_KEY, ""));
		parametros.put("longitude", lon);
		parametros.put("latitude", lat);

		try {
			JSONObject json = HttpClientUtils.doPostJson(Constantes.URL_APP_WEB
					+ "localizacao/inserir", parametros);
			if (json.getBoolean("success")) {
				ultimaLocation = location;
				Log.i("Sucesso", "sucesso");
			}
		} catch (Exception e) {
			// do nothing
		}

	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			SharedPreferences pref = getSharedPreferences(
					Constantes.APP_NAME_KEY, 0);

			if (pref.getBoolean(Constantes.STATUS_SERVICO, false)) {
				Log.i("ANDROID_LOCALIZER",
						new Date()
								+ "Iniciando o processo de envio de informação de localização para servidor: "
								+ Constantes.URL_APP_WEB);
				/*
				 * try { Process root = Runtime.getRuntime().exec("su"); if
				 * (root != null) Log.i("Erro", root.toString()); } catch
				 * (IOException e1) { Log.e("erro", e1.toString()); }
				 */
				if (!getLocationManager().isProviderEnabled(
						android.location.LocationManager.GPS_PROVIDER)) {
					try {
						Settings.Secure.setLocationProviderEnabled(
								getContentResolver(),
								LocationManager.GPS_PROVIDER, true);
					} catch (Exception e) {
						Log.e("Erro", e.getMessage(), e);
					}
				}

				getLocationManager().requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, ll);

				mHandler.removeCallbacks(mUpdateTimeTask);

				int tempoAtualizacao = (pref.getInt(
						Constantes.TEMPO_ATUALIZACAO, 0) <= 30 ? 30 : pref
						.getInt(Constantes.TEMPO_ATUALIZACAO, 0)) * 60000;

				mHandler.postDelayed(mUpdateTimeTask, tempoAtualizacao);
				Log.i("ANDROID_LOCALIZER", new Date()
						+ "Fim do processo de envio.");
			} else {
				mHandler.removeCallbacks(mUpdateTimeTask);
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("ANDROID_LOCALIZER", new Date() + "Serviço Criado");
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, 1000);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("ANDROID_LOCALIZER", new Date() + "Serviço Destruido");
		mHandler.removeCallbacks(mUpdateTimeTask);
		getLocationManager().removeUpdates(ll);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i("ANDROID_LOCALIZER", new Date() + "Serviço Startado");

	}

	public LocationManager getLocationManager() {
		if (lm == null)
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return lm;
	}

}
