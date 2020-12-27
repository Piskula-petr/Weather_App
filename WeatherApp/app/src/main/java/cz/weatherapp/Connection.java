package cz.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class Connection {
		
		private Context context;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 */
		public Connection(Context context) {
				
				this.context = context;
		}
		
		
		/**
		 * Kontrola přípojení k internetu
		 *
		 * @return - vrací true / false
		 */
		public boolean isOnline() {
				
				ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

				if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
						
						return true;
						
				} else return false;
		}
		
		
		/**
		 * Zobrazení chybového dialogu
		 */
		public void showNoConnectionDialog(Activity activity) {
				
				// Vytvoření dialogu
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setCancelable(false);
				builder.setMessage(R.string.no_connection_dialog_text);
				builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
								
								// Zavření aktivity
								((Activity) context).finish();
						}
				});
				
				// Zobrazení dialogu
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
		}
		
}
