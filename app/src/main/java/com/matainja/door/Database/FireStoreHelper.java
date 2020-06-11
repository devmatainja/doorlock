package com.matainja.door.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.matainja.door.model.Devices;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FireStoreHelper {

	FirebaseFirestore db;
	String DEVICE_COLLECTION="devices";
	String DEVICE_COLLECTION_FIELDS_NAME="name";
	String DEVICE_COLLECTION_FIELDS_SERIAL_NO="serial_no";
	String DEVICE_COLLECTION_FIELDS_IP="ip";
	String DEVICE_COLLECTION_FIELDS_MAC_ADDRESS="mac_address";
	String DEVICE_COLLECTION_FIELDS_WIFI_SSISD="ssid";
	String DEVICE_COLLECTION_FIELDS_WIFI_PASSWORD="wifi_password";
	String DEVICE_COLLECTION_FIELDS_ONLINE="online";
	String DEVICE_COLLECTION_FIELDS_JOIN_DATE="join_date";


	String TAG="FireStoreHelper";
	public void FireStoreHelper()
	{

	}


	public  void SaveDevice(Devices device)
	{
// Create a new user with a first and last name
		String joinDate = Calendar.getInstance().getTime().toString();

		String ip="0.0.0.0";
		String serial_no =device.getDevice_serial();

		Log.d(TAG, "Calling Save Device");
		Map<String, Object> user = new HashMap<>();
		user.put(DEVICE_COLLECTION_FIELDS_NAME, device.getName());
		user.put(DEVICE_COLLECTION_FIELDS_SERIAL_NO, device.getDevice_serial());
		user.put(DEVICE_COLLECTION_FIELDS_IP, ip);
		user.put(DEVICE_COLLECTION_FIELDS_MAC_ADDRESS, device.getDevice_mac_address());
		user.put(DEVICE_COLLECTION_FIELDS_WIFI_SSISD, device.getDevice_wifi_name());
		user.put(DEVICE_COLLECTION_FIELDS_WIFI_PASSWORD, device.getDevice_wifi_password());
		user.put(DEVICE_COLLECTION_FIELDS_ONLINE, "offline");

		user.put(DEVICE_COLLECTION_FIELDS_JOIN_DATE, joinDate);


		db = FirebaseFirestore.getInstance();
// Add a new document with a generated ID
		db.collection(DEVICE_COLLECTION)
				.add(user)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {

						Log.d(TAG, "success Save Device");

					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {

					}
				});

	}

	public void getDevice(String serial_no, final Devices device)
	{
		Log.d(TAG, "Calling getDevice");
		db = FirebaseFirestore.getInstance();

		db.collection(DEVICE_COLLECTION)
				.whereEqualTo(DEVICE_COLLECTION_FIELDS_SERIAL_NO, serial_no)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.getResult().isEmpty()) {

							Log.d(TAG, "empty getting documents: ");
							SaveDevice(device);

						} else {

							Log.d(TAG, "Error getting documents: ", task.getException());
						}
					}



				}


				);
	}
}