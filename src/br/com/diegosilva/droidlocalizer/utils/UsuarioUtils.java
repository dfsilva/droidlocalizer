package br.com.diegosilva.droidlocalizer.utils;

import android.app.Activity;
import android.content.SharedPreferences;

public class UsuarioUtils {

	public static String getIdUsuarioLogado(Activity ac) {
		SharedPreferences pref = ac.getSharedPreferences(
				Constantes.APP_NAME_KEY, 0);
		return pref.getString(Constantes.ID_USUARIO_KEY, "");
	}

}
