package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.AuthActivity;
import com.example.agrify.activity.StoreDetailActivity;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.activity.listener.NavigationIconClickListener;
import com.example.agrify.databinding.FragmentStoreBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements StoreAdapter.OnStoreSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;
    private static String[] CATEGORES_NAMES;
    FragmentStoreBinding bind;
    public String selectedCategory = "all";
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    NavigationIconClickListener navigationIconClickListener;
    private StoreAdapter mAdapter;
    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CATEGORES_NAMES = getResources().getStringArray(R.array.categories_names);//add category from array


        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_store, container, false);//inflaste layout


        firebaseAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            bind.productGrid.setBackground(getContext().getDrawable(R.drawable.store_item_background));//set curve background
        }


        setUpToolbar();
        initFirestore();
        initRecyclerView();
        initListView();

        bind.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                navigationIconClickListener.closeMenu();
            }
        });


        return bind.getRoot();
    }

    private void initListView() {

        bind.categoryListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.category_list_item, CATEGORES_NAMES));
        bind.categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView) view).getText().toString();
                Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                navigationIconClickListener.closeMenu();
                loadStoreProducts(text);

            }
        });
    }

    void loadStoreProducts(String text) {

        productLoading(true);
        if (text.equals("all")) {
            mQuery = mFirestore.collection("store").orderBy("name");
            selectedCategory = text;
        } else {
            selectedCategory = text;
            mQuery = mFirestore.collection("store").orderBy("name").whereEqualTo("category", text);
        }


        mAdapter.setQuery(mQuery);
        mAdapter.notifyDataSetChanged();

    }





    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("store").orderBy("name");

    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new StoreAdapter(mQuery, this, getActivity()) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.

            }


        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        bind.storeRecycleView.setHasFixedSize(true);
        bind.storeRecycleView.setLayoutManager(gridLayoutManager);


        bind.storeRecycleView.setAdapter(mAdapter);


    }

    @Override
    public void onStoreSelected(DocumentSnapshot store) {
        Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
        intent.putExtra(StoreDetailActivity.KEY_STORE_ID, store.getId());

        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }


    private void setUpToolbar() {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(bind.appBar);
        }
        navigationIconClickListener = new NavigationIconClickListener(getContext(), bind.productGrid, new AccelerateDecelerateInterpolator(), getContext().getDrawable(R.drawable.shr_menu), getContext().getDrawable(R.drawable.shr_close_menu));

        bind.appBar.setNavigationOnClickListener(navigationIconClickListener);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    void signOut() {
        if (firebaseAuth.getCurrentUser() != null) {
            AuthUI.getInstance().signOut(getActivity()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(getActivity(), AuthActivity.class));
                }
            });

        }

    }

    void productLoading(Boolean state)
    {
        if(state)
        {
            //TODO start shrimmer effect
            Toasty.info(getActivity(),"loading",Toasty.LENGTH_SHORT).show();
        }
        else
        {
            // TODO stop shrimmer effect
            Toasty.info(getActivity(),"loaded",Toasty.LENGTH_SHORT).show();
        }
    }

}