package com.tuxkids.simplefastcharge;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends Activity implements OnCheckedChangeListener {
	ToggleButton tb;
/** Called when the activity is first created. */
@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//load validasi function
		Validasi();
		
		//search id toggle
		tb=(ToggleButton)findViewById(R.id.check);	
		tb.setOnCheckedChangeListener(this);
	}

	public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		String enable  = "echo 1 > /sys/kernel/fast_charge/force_fast_charge";
		String disable = "echo 0 > /sys/kernel/fast_charge/force_fast_charge";
			
		if (isChecked) {
		execCommand(enable);
			}
		else {
		execCommand(disable);
			}
		}
		
		
		public void Validasi(){
			String file = "/sys/kernel/fast_charge/force_fast_charge";
			File myFile = new File(file);
			
			
			if (myFile.exists()) {
				/*if (cek.equals("1")){
					tb.setChecked(true);				
				}
				else {
					tb.setChecked(false);
				}*/
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
				
			}
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
}