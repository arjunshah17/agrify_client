package com.example.agrify.activity.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.databinding.FragmentPaymentMethodBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentMethodSelectionFragment extends BottomSheetDialogFragment {

    RadioClickedListener listener;
    FragmentPaymentMethodBinding binding;

    PaymentMethodSelectionFragment(RadioClickedListener listener) {
        this.listener = listener;
    }

    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_method, container,
                false);
        binding.cashRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listener.isGooglePay(false);
                    binding.googlePayRadiobutton.setChecked(false);
                }

            }
        });
        binding.googlePayRadiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    listener.isGooglePay(true);
                    binding.cashRadioButton.setChecked(false);

                }

            }
        });

        return binding.getRoot();
    }

    interface RadioClickedListener {
        void isGooglePay(boolean mode);

    }


}
