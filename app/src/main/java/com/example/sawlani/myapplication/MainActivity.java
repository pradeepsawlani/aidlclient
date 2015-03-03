package com.example.sawlani.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sawlani.myservice.*;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MyApplication";
    private IAdditionService additionService;
    private AdditionServiceConnection addServiceConn;

    class AdditionServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            additionService = IAdditionService.Stub.asInterface((IBinder)service);
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            additionService = null;
            Log.d(TAG, "onServiceDisconnected()");

        }
    };

    private void initService() {
        addServiceConn = new AdditionServiceConnection();
        Intent i = new Intent();
        i.setAction("com.example.sawlani.myservice.add");
        i.setPackage("com.example.sawlani.myservice");
        Log.d(TAG, "Intent=" + i);
        boolean ret =  bindService(i, addServiceConn, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    private void releaseService() {
        unbindService(addServiceConn);
        addServiceConn = null;
        Log.d(TAG, "releaseService() unbound");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void buttonOnClick(View v) {
        final EditText numText1 = (EditText) findViewById(R.id.numText1);
        final EditText numText2 = (EditText) findViewById(R.id.numText2);
        if (numText1.getText().toString().equals("") || numText2.getText().toString().equals("")) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Enter valid number", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        final int num1 = Integer.parseInt(numText1.getText().toString());
        final int num2 = Integer.parseInt(numText2.getText().toString());
        int res = 0;

        try {
            res = additionService.add(num1, num2);
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to add numbers " + e);
            e.printStackTrace();
        }

        final TextView result =  (TextView) findViewById(R.id.ResultView);
        result.setText(Integer.toString(res));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        releaseService();
    }
}
