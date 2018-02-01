package com.example.android.inventoryapp.data;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventoryapp.R;

/**
 * Created by Chris on 1/30/2018.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks {

    private static final int INVENTORY_LOADER = 0;

    private Uri mCurrentProductUri;

    private TextView mQuantityText;

    private TextView mEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvitiy_detail);
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        mQuantityText = (TextView) findViewById(R.id.detail_quantity);
        mEmailText = (TextView) findViewById(R.id.detail_provider_email);

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
            quantity = Integer.parseInt(quantityString) + 1;
        }
    }

    private void decrementQuantity() {
        String quantityString = mQuantityText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString) - 1;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY};


        return new CursorLoader(this,
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int quantityColumnIndex =
                    cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);

            int quantity = cursor.getInt(quantityColumnIndex);

            mQuantityText.setText(Integer.toString(quantity));
        }
    }



    @Override
    public void onLoaderReset(Loader loader) {
        mQuantityText.setText("");

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

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
