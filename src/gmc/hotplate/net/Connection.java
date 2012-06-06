/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.net;

import gmc.hotplate.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Connection {
    private static final String LOG_TAG = Connection.class.getName();
    private ConnectionStatus status;
    private int timeout;
    private String errMessage;

    public enum ConnectionStatus {
        CONNECT_OK, CONNECT_UNDEFINED, CONNECT_IO_ERROR, CONNECT_TIMEOUT_ERROR,
    }

    public Connection(Context context) {
        status = ConnectionStatus.CONNECT_UNDEFINED;
        Resources r = context.getResources();
        timeout = Integer.parseInt(r.getString(R.string.default_http_timeout));
    }

    private String generateRequest(String urlString, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(urlString);
        if (!params.isEmpty()) {
            builder.append("?");
            int size = params.size();
            int cur = 0;
            for (Map.Entry<String, String> p : params.entrySet()) {
                builder.append(p.getKey() + "=" + p.getValue());
                if (cur + 1 != size) {
                    builder.append("&");
                    cur++;
                }
            }
        }
        return builder.toString();
    }

    public String requestServer(String urlString, Map<String, String> params) {
        StringBuilder response = new StringBuilder();
        status = ConnectionStatus.CONNECT_UNDEFINED;
        BufferedReader in = null;
        URLConnection conn = null;
        try {
            String request = generateRequest(urlString, params);
            Log.d(LOG_TAG, "Request: " + request);
            URL url = new URL(request);
            conn = url.openConnection();
            conn.setConnectTimeout(timeout);
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            status = ConnectionStatus.CONNECT_OK;
        } catch (SocketTimeoutException e) {
            errMessage = e.getMessage();
            status = ConnectionStatus.CONNECT_TIMEOUT_ERROR;
        } catch (IOException e) {
            status = ConnectionStatus.CONNECT_IO_ERROR;
            errMessage = e.getMessage();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    status = ConnectionStatus.CONNECT_IO_ERROR;
                    errMessage = e.getMessage();
                }
            }
        }
        return response.toString();
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getErrorMessage() {
        return errMessage;
    }

}
