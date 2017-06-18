package br.ufes.inf.hfilho.previsodotempo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTMLController {

    public static String getString(String sUrl) throws IOException {
        String ret = null;
        InputStream content = null;
        HttpURLConnection urlConnection = null;
        int i = 0;
        for (i = 0; i < 3; i++) {
            URL url = new URL(sUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            String code = urlConnection.getContentEncoding();
            content = urlConnection.getInputStream();
            if (content != null) {
                break;
            }
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];

        try {
            while (true) {
                nRead = content.read(data);
                if (nRead == -1) {
                    break;
                }
                buffer.write(data, 0, nRead);
            }
            content.close();
            buffer.flush();
            if (urlConnection != null) urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ret = new String(buffer.toByteArray());
        return ret;
    }

}
