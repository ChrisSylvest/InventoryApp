package com.example.android.inventoryapp.data;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

import static android.R.attr.onClick;

/**
 * Created by Chris on 1/30/2018.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks {

    private static final int EXISTING_INVENTORY_LOADER = 0;

    private Uri mCurrentProductUri;

    private TextView mNameText;

    private TextView mQuantityText;

    private TextView mPriceText;

    private TextView mEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvitiy_detail);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mNameText = (TextView) findViewById(R.id.product_name);
        mQuantityText = (TextView) findViewById(R.id.product_quantity);
        mPriceText = (TextView) findViewById(R.id.product_price);
        mEmailText = (TextView) findViewById(R.id.provider_email);

        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        Button incrementButton = (Button) findViewById(R.id.increase_quantity_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                incrementQuantity();
                saveQuantity();
            }
        });

        Button decrementButton = (Button) findViewById(R.id.decrease_quantity_button);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                decrementQuantity();
                saveQuantity();
            }
        });
    }

    private void saveQuantity() {
        String quantityString = mQuantityText.getText().toString().trim();

        ContentValues values = new ContentValues();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.detail_update_quantity_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementQuantity() {
        String quantityString = mQuantityText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
            quantity = quantity + 1;
        }
    }

    private void decrementQuantity() {
        String quantityString = mQuantityText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
            quantity = quantity - 1;
        }
    }

    private void deleteCurrentProduct(View view) {
        showDeleteConfirmationDialog();
        return;
    }

    private void increment(View view) {
        incrementQuantity();
        saveQuantity();
        return;
    }

    private void decrement(View view) {
        decrementQuantity();
        saveQuantity();
        return;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }



    @Override
    public void onLoaderReset(Loader loader) {
        mNameText.setText("");
        mQuantityText.setText("");
        mPriceText.setText("");
        mEmailText.setText("");

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.detail_delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.detail_delete_product_success),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
