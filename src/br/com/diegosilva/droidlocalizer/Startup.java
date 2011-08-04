package br.com.diegosilva.droidlocalizer;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import br.com.diegosilva.droidlocalizer.utils.Constantes;

public class Startup extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("ANDROID_LOCALIZER", new Date()
				+ "Efetuando as verificações para iniciar o serviço");
		SharedPreferences pref = context.getSharedPreferences(
				Constantes.APP_NAME_KEY, 0);
		if (pref.getBoolean(Constantes.STATUS_SERVICO, false)) {
			agendar(context, pref);
		}
		Log.i("ANDROID_LOCALIZER", new Date() + "Fim das verificações");
	}

	private void agendar(Context context, SharedPreferences pref) {
		Intent serviceIntent = new Intent("ATUALIZAR_POSICAO");
		PendingIntent p = PendingIntent.getBroadcast(context, 0, serviceIntent,
				0);
		AlarmManager alarme = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		long time = Calendar.getInstance().getTimeInMillis();
		int tempoAtualizacao = (pref.getInt(Constantes.TEMPO_ATUALIZACAO, 0) <= Constantes.INTERVALO_ATUALIZACAO ? Constantes.INTERVALO_ATUALIZACAO
				: pref.getInt(Constantes.TEMPO_ATUALIZACAO, 0)) * 60000;
		alarme.setRepeating(AlarmManager.RTC_WAKEUP, time, tempoAtualizacao, p);
	}

}
