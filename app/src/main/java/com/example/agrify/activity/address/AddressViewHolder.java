package com.example.agrify.activity.address;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.address.model.Address;
import com.example.agrify.databinding.AddressItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vincent.blurdialog.BlurDialog;
import com.vincent.blurdialog.listener.OnPositiveClick;

import es.dmoral.toasty.Toasty;

public class AddressViewHolder extends RecyclerView.ViewHolder {
    AddressItemBinding binding;
    Activity activity;
    BlurDialog blurDialog;
    public AddressViewHolder( AddressItemBinding binding) {
        super(binding.getRoot());
        this.binding=binding;

    }

    public void bind(DocumentSnapshot snapshot, Activity activity, String TAG, AddressAdapter.OnAddressSelectedListener listener) {
        if(TAG=="AddressListFragment")
        {
            binding.buttonLayout.setVisibility(View.GONE);
            itemView.setOnClickListener(v->{
                listener.OnAddressSelected(snapshot);
            });
        }
        Address address=snapshot.toObject(Address.class);
        binding.setAddress(address);
        binding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, addressActivity.class);
            intent.putExtra("addressRef", snapshot.getReference().getPath());
            activity.startActivity(intent);
        });
        blurDialog=new BlurDialog();
        BlurDialog.Builder builder = new BlurDialog.Builder()
                .isCancelable(true).radius(10)
                .isOutsideCancelable(true).message("are you sure you want to delete "+snapshot.getString("name")+" address ?")
                .positiveMsg("Yes")
                .negativeMsg("No").positiveClick(new OnPositiveClick() {
                    @Override
                    public void onClick() {
                        delete(snapshot);
                        blurDialog.dismiss();
                    }
                }) .negativeClick(() -> {
                    blurDialog.dismiss();

                }) .type(BlurDialog.TYPE_DELETE)
                .createBuilder(activity);
        blurDialog.setBuilder(builder);
        String comAdress=address.getHouseNum()+address.getLocation();
        binding.addressLocation.setText(comAdress);
       this.activity=activity;
        binding.deleteButton.setOnClickListener(v -> {

            blurDialog.show();



        });
    }

    private void delete(DocumentSnapshot snapshot) {
        snapshot.getReference().delete().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toasty.info(activity,"success",Toasty.LENGTH_SHORT).show();
            }
            else
            {
                Toasty.error(activity,task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
            }
        });
    }

}
