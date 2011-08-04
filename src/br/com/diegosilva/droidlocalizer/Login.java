package br.com.diegosilva.droidlocalizer;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.com.diegosilva.droidlocalizer.utils.Constantes;
import br.com.diegosilva.droidlocalizer.utils.DialogUtils;
import br.com.diegosilva.droidlocalizer.utils.HttpClientUtils;

public class Login extends Activity implements Runnable {

	private ProgressDialog dialog;
	private boolean customTitleSupported;
	private TextView titleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customTitleSupported = getWindow().requestFeature(
				Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		customTitleBar();
		Button btnEntrar = (Button) findViewById(R.id.btnEntrar);

		btnEntrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(Login.this, "",
						"Autenticando usuário", true);
				new Thread(Login.this).start();
			}
		});
	}

	public void customTitleBar() {
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.custom_title_bar);
			titleText = (TextView) findViewById(R.id.titleMsg);
			titleText.setText("Entrar no Sistema");
		}
	}

	private void login() throws Exception {
		try {
			String login = ((EditText) findViewById(R.id.txLogin)).getText()
					.toString();
			String password = ((EditText) findViewById(R.id.txSenha)).getText()
					.toString();

			Map<String, Object> par = new HashMap<String, Object>();
			par.put("login", login);
			par.put("senha", password);

			JSONObject objJson = HttpClientUtils.doPostJson(
					Constantes.URL_APP_WEB + "usuario/loginJson", par);

			boolean success = objJson.getBoolean("success");

			if (success) {
				SharedPreferences pref = getSharedPreferences(
						Constantes.APP_NAME_KEY, 0);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(Constantes.ID_USUARIO_KEY,
						objJson.getString("idUsuario"));
				/*
				 * String number =
				 * getIntent().getExtras().getString("PHONE_NUMBER");
				 * editor.putString(Constantes.NUMERO_TELEFONE, number);
				 */
				editor.commit();
				startActivity(new Intent(this, Configuracoes.class));
				finish();

			} else {
				throw new Exception(getText(R.string.tx_user_pass_invalid)
						.toString());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dialog.dismiss();
		}
	}

	@Override
	public void run() {
		try {
			Looper.prepare();
			login();
		} catch (final Exception e) {
			Log.e("Login", e.getMessage());
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogUtils.showAlertError(Login.this, e.getMessage(), null);
				}
			});
		}

	}
}
