package br.com.diegosilva.droidlocalizer;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import br.com.diegosilva.droidlocalizer.utils.Constantes;
import br.com.diegosilva.droidlocalizer.utils.DialogUtils;
import br.com.diegosilva.droidlocalizer.utils.Execucao;

public class Configuracoes extends Activity {

	private boolean customTitleSupported;
	private TextView titleText;
	private ToggleButton tbStatus;
	private EditText etTempo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customTitleSupported = getWindow().requestFeature(
				Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.configuracoes);
		customTitleBar();

		tbStatus = (ToggleButton) findViewById(R.id.tbStatusServico);
		etTempo = (EditText) findViewById(R.id.txQtd);

		SharedPreferences pref = getSharedPreferences(Constantes.APP_NAME_KEY,
				0);
		tbStatus.setChecked(pref.getBoolean(Constantes.STATUS_SERVICO, false));
		etTempo.setText(String.valueOf(pref.getInt(
				Constantes.TEMPO_ATUALIZACAO, 30)));

		if (!tbStatus.isChecked()) {
			etTempo.setEnabled(false);
		}

		tbStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					etTempo.setEnabled(true);
				} else {
					etTempo.setEnabled(false);
				}
			}
		});

		Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
		btnSalvar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences(
						Constantes.APP_NAME_KEY, 0);
				SharedPreferences.Editor editor = pref.edit();

				editor.putBoolean(Constantes.STATUS_SERVICO,
						tbStatus.isChecked());
				editor.putInt(Constantes.TEMPO_ATUALIZACAO,
						Integer.parseInt(etTempo.getText().toString()));

				editor.commit();
				verificaIniciaServico();
				DialogUtils.showAlertInfo(Configuracoes.this,
						"Alterações efetuadas com sucesso!", new Execucao() {
							@Override
							public void executarSim() {
								finish();
							}

							@Override
							public void executarNao() {
							}
						});
			}
		});
	}

	public void customTitleBar() {
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.custom_title_bar);
			titleText = (TextView) findViewById(R.id.titleMsg);
			titleText.setText("Configurações");
		}
	}

	private void verificaIniciaServico() {
		if (tbStatus.isChecked()) {
			agendar();
		} else if (!tbStatus.isChecked()) {
			Intent serviceIntent = new Intent("ATUALIZAR_POSICAO");
			PendingIntent p = PendingIntent.getBroadcast(Configuracoes.this, 0,
					serviceIntent, 0);
			AlarmManager alarme = (AlarmManager) Configuracoes.this
					.getSystemService(Activity.ALARM_SERVICE);
			alarme.cancel(p);
		}

	}

	private void agendar() {
		Intent serviceIntent = new Intent("ATUALIZAR_POSICAO");
		PendingIntent p = PendingIntent.getBroadcast(Configuracoes.this, 0,
				serviceIntent, 0);
		AlarmManager alarme = (AlarmManager) Configuracoes.this
				.getSystemService(Activity.ALARM_SERVICE);

		SharedPreferences pref = getSharedPreferences(Constantes.APP_NAME_KEY,0);
		int tempoAtualizacao = (pref.getInt(Constantes.TEMPO_ATUALIZACAO, 0) <= Constantes.INTERVALO_ATUALIZACAO ? Constantes.INTERVALO_ATUALIZACAO
				: pref.getInt(Constantes.TEMPO_ATUALIZACAO, 0)) * 60000;

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 2);
		alarme.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), tempoAtualizacao, p);
	}
}
