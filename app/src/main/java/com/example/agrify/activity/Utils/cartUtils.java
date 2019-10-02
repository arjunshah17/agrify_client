package com.example.agrify.activity.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class cartUtils {
    public static void clearAllCart(Context context) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        WriteBatch clearBatch = firebaseFirestore.batch();


        FirebaseFunctions mFunctions;
        mFunctions = FirebaseFunctions.getInstance();

        HashMap<String, Object> storeHash = new HashMap<>();
        firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String productId = doc.getId();
                        String sellerId = doc.getString("sellerId");
                        DocumentReference Prodref = firebaseFirestore.collection("store").document(productId).collection("product_user_cart").document(auth.getUid());
                        DocumentReference sellerProductRef = firebaseFirestore.collection("Sellers").document(sellerId).collection("productList").document(productId).collection("cartItemUser").document(auth.getUid());
                        clearBatch.delete(Prodref);
                        clearBatch.delete(sellerProductRef);
                        DocumentReference userRef=firebaseFirestore.collection("Users").document(auth.getUid());
                        clearBatch.update(userRef,"cartCounter",0);
                    }

                    CollectionReference ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
                    storeHash.put("path", ref.getPath());
                    mFunctions.getHttpsCallable("recursiveDelete").call(storeHash).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                        @Override
                        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                            if (task.isSuccessful()) {
                                DocumentReference reference = firebaseFirestore.collection("Users").document(auth.getUid());
                                clearBatch.update(reference, "cartCounter", 0);
                                clearBatch.update(reference, "cartSellerId", "");
                                clearBatch.commit();
                                Toasty.info(context, "cart is cleared", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public static void deletecartItem(String productId, String sellerId, Context context) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        WriteBatch clearBatch = firebaseFirestore.batch();


        HashMap<String, Object> storeHash = new HashMap<>();
        DocumentReference userRef=firebaseFirestore.collection("Users").document(auth.getUid());
     clearBatch.update(userRef,"cartCounter", FieldValue.increment(- 1));
        DocumentReference Prodref = firebaseFirestore.collection("store").document(productId).collection("product_user_cart").document(auth.getUid());
        DocumentReference sellerProductRef = firebaseFirestore.collection("Sellers").document(sellerId).collection("productList").document(productId).collection("cartItemUser").document(auth.getUid());
        clearBatch.delete(Prodref);
        clearBatch.delete(sellerProductRef);
        DocumentReference ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document(productId);
        clearBatch.delete(ref);
        clearBatch.commit();

    }

    public static void deleteorderItem(String productId, String sellerId, Context applicationContext) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        WriteBatch clearBatch = firebaseFirestore.batch();


        HashMap<String, Object> storeHash = new HashMap<>();
        // DocumentReference Prodref = firebaseFirestore.collection("store").document(productId).collection("product_user_cart").document(auth.getUid());
        DocumentReference sellerProductRef = firebaseFirestore.collection("Sellers").document(sellerId).collection("productList").document(productId).collection("tempOrderCart").document(auth.getUid());
        //  clearBatch.delete(Prodref);
        clearBatch.delete(sellerProductRef);
        DocumentReference ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").document(productId);
        clearBatch.delete(ref);
        clearBatch.commit();
    }
}
