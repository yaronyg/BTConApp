// Copyright (c) Microsoft. All Rights Reserved. Licensed under the MIT License. See license.txt in the project root for further information.
package test.microsoft.com.btconnectorlib;

/**
 * Created by juksilve on 12.3.2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;


public class BTListenerThread extends Thread {

    private BluetoothBase.BluetoothStatusChanged callback;
    private final BluetoothServerSocket mSocket;
    boolean mStopped = false;

    public BTListenerThread(BluetoothBase.BluetoothStatusChanged Callback,BluetoothAdapter bta) {
        callback = Callback;
        BluetoothServerSocket tmp = null;

        try {
            tmp = bta.listenUsingInsecureRfcommWithServiceRecord(BluetoothBase.MY_NAME, BluetoothBase.MY_UUID);
        } catch (IOException e) {
            printe_line("listen() failed: " + e.toString());
        }
        mSocket = tmp;
    }

    public void run() {
    //    while (!this.interrupted()) {
            printe_line("starting to listen");
            BluetoothSocket socket = null;
            try {
                socket = mSocket.accept();
                if(socket != null){
                    printe_line("we got incoming connection");
                    callback.GotConnection(socket);
                }else if(!mStopped){
                    callback.ListeningFailed("Socket is null");
                }

            } catch (Exception e) {
                if(!mStopped) {
                    //return failure
                    printe_line("accept socket failed: " + e.toString());
                    callback.ListeningFailed(e.toString());
                }
            }
       // }
    }

    private void printe_line(String message){
        Log.d("BTListerThread",  "BTListerThread: " + message);
    }

    public void Stop() {
        printe_line("cancelled");
        mStopped = true;
        try {
            mSocket.close();
        } catch (IOException e) {
            printe_line("closing socket failed: " + e.toString());
        }
    }
}