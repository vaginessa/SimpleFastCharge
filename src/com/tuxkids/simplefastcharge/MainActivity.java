package com.tuxkids.simplefastcharge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends Activity implements OnCheckedChangeListener {
	
	AlertDialog ad;
	TextView tv;
	ToggleButton tb;
	private final static String fastcharge = "/sys/kernel/fast_charge/force_fast_charge";
	private final static int BUFFER_SIZE = 2048;
	
	
	/** Called when the activity is first created. */
@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//search togglebutton and textView by ID
		tv=(TextView)findViewById(R.id.status);
		tb=(ToggleButton)findViewById(R.id.check);
		//load validasi function
		Validasi();
		
		//search id toggle
			
		tb.setOnCheckedChangeListener(this);
	}

	public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		tv=(TextView)findViewById(R.id.status);
		String enable  = "echo 1 > /sys/kernel/fast_charge/force_fast_charge";
		String disable = "echo 0 > /sys/kernel/fast_charge/force_fast_charge";
		String close = "exit";	
		
		if (isChecked) {
		execCommand(enable);
		execCommand(close);
		tv.setText("Touch To Disable");
			}
		else {
		execCommand(disable);
		execCommand(close);
		tv.setText("Touch To Enable");
			}
		}
		
		
		public void Validasi(){
			String file = "/sys/kernel/fast_charge/force_fast_charge";
			File myFile = new File(file);
			String getStatus = getCurrentStatus();
			String aktif = "1";
			
			if (myFile.exists()) {
				if (getStatus.equals(aktif)) {
					tb.setChecked(true);
					tv.setText("Touch To Disable");
				}
				else {
					tb.setChecked(false);
					tv.setText("Touch To Enable");
				}
			} 
			else { 
				//show message error when kernel not support
				Context context = getApplicationContext();
				CharSequence text1 = "You're kernel not support Fast Charge !";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text1, duration);
				toast.show();
				//disable togglebutton
				tb.setEnabled(false);
				tv.setText("Sorry, you're kernel not \nsupport for run this app !");
				
			}
	}
	

		public static String getCurrentStatus() {
			return readString(fastcharge);
		}
		 
	
		private static String readString(String filename) {
			try {
				File f = new File(filename);
				if (f.exists()) {
					InputStream is = null;
					if (f.canRead()) {
						is = new FileInputStream(f);
					} else {
						Log.w(Constants._COUNT, "read-only file, trying w/ root: " + filename);
						/*
						 * Try reading as root.
						 */
						String[] commands = {
								"cat " + filename + "\n", "exit\n"
						};
						Process p = Runtime.getRuntime().exec(getSUbinaryPath());
						DataOutputStream dos = new DataOutputStream(p.getOutputStream());
						for (String command : commands) {
							dos.writeBytes(command);
							dos.flush();
						}
						if (p.waitFor() == 0) {
							is = p.getInputStream();
						} else {
							// is = p.getErrorStream();
							return null;
						}
					} // end-if: f.canRead()
					BufferedReader br = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE);
					String line = br.readLine();
					br.close();
					return line;
				} else {
					/*
					 * File does not exist.
					 */
					Log.e(Constants._COUNT, "file does not exist: " + filename);
					return null;
				}
			} catch (InterruptedException iex) {
				Log.e(Constants._COUNT, iex.getMessage(), iex);
				return null;
			} catch (IOException ioex) {
				Log.e(Constants._COUNT, ioex.getMessage(), ioex);
				return null;
			}
		}
		
		public static String getSUbinaryPath() {
			String s = "/system/bin/su";
			File f = new File(s);
			if (f.exists()) {
				return s;
			}
			s = "/system/xbin/su";
			f = new File(s);
			if (f.exists()) {
				return s;
			}
			return null;
		}

	public Boolean execCommand(String command) 
    {
        try {
            Runtime rt = Runtime.getRuntime();
            //request permission
            Process process = rt.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream()); 
            os.writeBytes(command + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true; 
    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId())
		{
		case R.id.about :
		alert();
		return true;	
		
		default:
		return super.onOptionsItemSelected(item);
		}	
	}
	
	public void alert (){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false)
		       .setTitle("About")
		       .setMessage(
		    		"Simple Fast Charge version 1.0\n" +
		       		"Created by : tuxkids\n\n\n\n\n\n\n" +
		    		"+--------------------------------+\n" +
		       		"   Contact Developer:\n" +
		       		"   ukie.tux@gmail.com\n" +
		       		"+--------------------------------+")
		       .setIcon(R.drawable.ic_about)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}