package br.com.diegosilva.droidlocalizer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import br.com.diegosilva.droidlocalizer.R;

public final class DialogUtils {

	public static AlertDialog showConfirmDialog(final Activity ac,
			final Execucao exec, final String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ac);

		builder.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								exec.executarSim();
								dialog.dismiss();
							}
						})
				.setNegativeButton("Não",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								exec.executarNao();
								dialog.dismiss();
							}
						});
		return builder.show();
	}

	/**
	 * Exibe mensagem de Erro genérica.
	 * 
	 * @param ac
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlertError(final Activity ac,
			final String msg, final Execucao exec) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ac);
		builder.setMessage(msg).setTitle("Erro").setIcon(R.drawable.error)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						if (exec != null) {
							exec.executarSim();
						}
					}
				});
		return builder.show();
	}

	/**
	 * Exibe mensagem de Erro genérica.
	 * 
	 * @param ac
	 * @param msg
	 * @return
	 */
	public static AlertDialog showAlertInfo(final Activity ac,
			final String msg, final Execucao exec) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ac);
		builder.setMessage(msg).setTitle("Informação").setIcon(R.drawable.info)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						if (exec != null) {
							exec.executarSim();
						}
					}
				});
		return builder.show();
	}

}
