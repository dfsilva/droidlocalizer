package br.com.diegosilva.droidlocalizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AtualizarPosicao extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Intent it = new Intent("SERVICO_2");
		context.startService(it);
	}

}
