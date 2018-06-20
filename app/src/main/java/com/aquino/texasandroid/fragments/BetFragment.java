package com.aquino.texasandroid.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.GameState;

public class BetFragment extends DialogFragment {

    private BetListener betListener;

    public interface BetListener {
        void onFinish(int value);
        void onCancel();
    }

    private int minimumBet, callAmount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        minimumBet = getArguments().getInt("min");
        callAmount = getArguments().getInt("call");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int minValue;
        if(callAmount != 0) {
            minValue = callAmount/minimumBet;
        } else minValue = 1;


        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(20);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int amount = value * minimumBet;
                return "" + amount;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bet Amount");
        builder.setMessage("Choose an amount :");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                betListener.onFinish(numberPicker.getValue());
                dismiss();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                betListener.onCancel();
                dismiss();
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public BetListener getBetListener() {
        return betListener;
    }

    public void setBetListener(BetListener betListener) {
        this.betListener = betListener;
    }
}
