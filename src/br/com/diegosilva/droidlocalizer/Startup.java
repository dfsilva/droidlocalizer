package br.com.diegosilva.droidlocalizer;

import java.util.Date;

import br.com.diegosilva.droidlocalizer.utils.Constantes;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class Startup extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("ANDROID_LOCALIZER", new Date()
				+ "Efetuando as verificações para iniciar o serviço");
		SharedPreferences pref = context.getSharedPreferences(
				Constantes.APP_NAME_KEY, 0);
		if (pref.getBoolean(Constantes.STATUS_SERVICO, false)) {
			Intent serviceIntent = new Intent();
			serviceIntent.setAction("br.com.diegosilva.droidlocalizer.Servico");
			context.startService(serviceIntent);
		}
		Log.i("ANDROID_LOCALIZER", new Date() + "Fim das verificações");
	}

}
