package br.com.diegosilva.droidlocalizer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import br.com.diegosilva.droidlocalizer.utils.Constantes;
import br.com.diegosilva.droidlocalizer.utils.HttpClientUtils;

public class AtualizarPosicao extends BroadcastReceiver {

	private LocationManager lm;
	private Context ctx;

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
			getLocationManager().removeUpdates(ll);
			try {
				JSONObject ultimaLocalizacao = getLastPosition();
				if (ultimaLocalizacao != null) {
					BigDecimal latAnt = BigDecimal.valueOf(ultimaLocalizacao.getJSONArray("localizacao").getJSONObject(0).getDouble("latitude"));
					BigDecimal lonAnt = BigDecimal.valueOf(ultimaLocalizacao.getJSONArray("localizacao").getJSONObject(0).getDouble("longitude"));

					BigDecimal lat = BigDecimal.valueOf(location.getLatitude());
					BigDecimal lon = BigDecimal
							.valueOf(location.getLongitude());
					float distancia = distanticaEntrePontos(
							latAnt.floatValue(), lonAnt.floatValue(),
							lat.floatValue(), lon.floatValue());

					if (Constantes.DISTANCIA_MINIMA.compareTo(distancia) < 0) {
						registrarPosicao(location, location.getLongitude(),
								location.getLatitude());
					} else {
						atualizaLocalizacao(location.getLongitude(),
								location.getLatitude(),
								ultimaLocalizacao.getJSONArray("localizacao").getJSONObject(0).getLong("id_localizacao"));
					}
				}
			} catch (Exception e) {
				Log.e("ANDROID_LOCALIZER", e.getMessage());
			}
		}
	};

	private float distanticaEntrePontos(float lat1, float lng1, float lat2,
			float lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;
		int meterConversion = 1609;
		return new Float(dist * meterConversion).floatValue();
	}

	private void registrarPosicao(Location location, double lon, double lat) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put(
				"id_usuario",
				ctx.getSharedPreferences(Constantes.APP_NAME_KEY, 0).getString(
						Constantes.ID_USUARIO_KEY, ""));
		parametros.put("longitude", lon);
		parametros.put("latitude", lat);

		try {
			JSONObject json = HttpClientUtils.doPostJson(Constantes.URL_APP_WEB
					+ "localizacao/inserir", parametros);
			if (json.getBoolean("success")) {
				Log.i("Sucesso", "sucesso");
			}
		} catch (Exception e) {
			Log.e("ANDROID_LOCALIZER", e.getMessage());
		}
	}

	private void atualizaLocalizacao(double lon, double lat, long idLocalizacao) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put(
				"id_usuario",
				ctx.getSharedPreferences(Constantes.APP_NAME_KEY, 0).getString(
						Constantes.ID_USUARIO_KEY, ""));
		parametros.put("longitude", lon);
		parametros.put("latitude", lat);
		parametros.put("id_localizacao", idLocalizacao);

		try {
			JSONObject json = HttpClientUtils.doPostJson(Constantes.URL_APP_WEB
					+ "localizacao/atualizar", parametros);
			if (json.getBoolean("success")) {
				Log.i("Sucesso", "sucesso");
			}
		} catch (Exception e) {
			Log.e("ANDROID_LOCALIZER", e.getMessage());
		}
	}

	private JSONObject getLastPosition() throws Exception {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put(
				"id_usuario",
				ctx.getSharedPreferences(Constantes.APP_NAME_KEY, 0).getString(
						Constantes.ID_USUARIO_KEY, ""));
		try {
			JSONObject json = HttpClientUtils.doPostJson(Constantes.URL_APP_WEB
					+ "localizacao/lastPosition", parametros);
			if (json.getBoolean("success")) {
				Log.i("ANDROID_LOCALIZER",
						"Sucesso na busca da ultima localizacao");
				return json;
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("ANDROID_LOCALIZER", new Date()
				+ "Efetuando as verificações para iniciar o serviço");
		ctx = context;
		getLocationManager().requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, ll);

		Log.i("ANDROID_LOCALIZER", new Date() + "Fim das verificações");
	}

	public LocationManager getLocationManager() {
		if (lm == null)
			lm = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
		return lm;
	}

}
