package br.com.diegosilva.droidlocalizer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class HttpClientUtils {

	public static final JSONObject doPostJsonLogedUser(String url, Activity ac,
			Map<String, Object> map) throws Exception {
		try {
			if (map == null) {
				map = new HashMap<String, Object>();
				map.put("idUsuario", UsuarioUtils.getIdUsuarioLogado(ac));
			} else {
				map.put("idUsuario", UsuarioUtils.getIdUsuarioLogado(ac));
			}
			String jsonString = doPost(url, map);
			return new JSONObject(jsonString);
		} catch (Exception e) {
			throw e;
		}
	}

	public static final JSONObject doPostJson(String url,
			Map<String, Object> map) throws Exception {
		try {
			String jsonString = doPost(url, map);
			return new JSONObject(jsonString);
		} catch (Exception e) {
			throw e;
		}
	}

	public static final String doPost(String url, Map<String, Object> map)
			throws Exception {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			Log.i("HttpClientUtils", "Invocando uri: " + httpPost.getURI());

			List<NameValuePair> params = getParams(map);
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			Log.i("HttpClientUtils", "HttpClient.params " + params);

			HttpResponse response = httpclient.execute(httpPost);

			Log.i("HttpClientUtils", String.valueOf(response.getStatusLine()));

			HttpEntity entity = response.getEntity();

			String retorno = "";

			if (entity != null) {
				InputStream in = entity.getContent();
				retorno = readString(in);
			}
			Log.i("HttpClientUtils", "Resposta: " + retorno);
			return retorno;
		} catch (Exception e) {
			Log.e("HttpClientUtils", e.getMessage(), e);
			throw e;
		}
	}

	private static byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}

			byte[] bytes = bos.toByteArray();
			return bytes;
		} finally {
			bos.close();
		}
	}

	private static String readString(InputStream in) throws IOException {
		byte[] bytes = readBytes(in);
		String texto = new String(bytes);
		return texto;
	}

	@SuppressWarnings("rawtypes")
	private static List<NameValuePair> getParams(Map map) throws IOException {
		if (map == null || map.size() == 0) {
			return null;
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		Iterator e = (Iterator) map.keySet().iterator();
		while (e.hasNext()) {
			String name = (String) e.next();
			Object value = map.get(name);
			params.add(new BasicNameValuePair(name, String.valueOf(value)));
		}
		return params;
	}
}
