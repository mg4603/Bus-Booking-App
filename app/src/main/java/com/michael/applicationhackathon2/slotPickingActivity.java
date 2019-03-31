package com.michael.applicationhackathon2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class slotPickingActivity  extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private LinearLayout fridayeven;
    private LinearLayout saturdaymorn;
    private LinearLayout saturdayeven;
    private LinearLayout sundayeven;
    private Intent payIntent;
    int count;
    String day;
    private TextView textViewPrice ;

    private void switchPay(int index){
        Log.i("switch","error");
        generateCheckSum(4);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_picking);
        fridayeven = findViewById(R.id.fridayeven);
        sundayeven = findViewById(R.id.sundayeven);
        saturdaymorn = findViewById(R.id.saturdaymorn);
        saturdayeven = findViewById(R.id.saturdayeven);
        textViewPrice =  findViewById(R.id.textViewPrice);
        fridayeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("qwert","fridayeven");
                switchPay(1);
            }
        });

        sundayeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("qwert","sundayeven");
                switchPay(4);
            }
        });


        saturdayeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("qwert","saturdayeven");
                switchPay(3);
            }
        });

        saturdaymorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPay(2);
            }
        });

    }




    private void generateCheckSum(final int index) {

        //getting the tax amount first.
        String txnAmount = textViewPrice.getText().toString().trim();

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );



        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );
        Log.i("checksum call", "reched here");
        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                Log.d("On response","IN HERE");
                Log.i("On response","IN HERE");

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
                if(index==1){
                    initializeBooking("Friday Evening");
                }else if(index==2){
                    initializeBooking("Saturday Morning");
                }else if(index==3){
                    initializeBooking("Saturday Evening");
                }else if(index==4){
                    initializeBooking("Sunday Morning");
                }
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                Log.i("on failure for call",t.toString());
            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        Service.initialize(order,null);



        Service.startPaymentTransaction(this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Toast.makeText(slotPickingActivity.this, bundle.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(slotPickingActivity.this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(slotPickingActivity.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }

   void initializeBooking(String day){
        this.day = day;
       FirebaseDatabase.getInstance().getReference().child("Booking").child(day)
               .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       int count =0;
                       for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                           booking book = snapshot.getValue(booking.class);
                           count++;
                       }
                       if(count>=5){
                           Toast.makeText(slotPickingActivity.this,"Slot is full",Toast.LENGTH_SHORT).show();
                       }else{
                           confirmBooking();
                       }
                   }
                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                   }
               });

   }

   public void confirmBooking(){
        Log.d("con","In here");
        booking book = new booking(FirebaseAuth.getInstance().getCurrentUser().getUid(),"pid");
       FirebaseDatabase.getInstance().getReference().child("Booking").child(day).setValue(book);
       Toast.makeText(slotPickingActivity.this,"booking successful",Toast.LENGTH_SHORT).show();
   }







}
