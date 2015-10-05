package it.jaschke.alexandria;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    public static final int RESULT_OK = -1;
    public static final int RESULT_CANCELED = 0;
    private ResponseReceiver receiver;


    public AddBook(){
    }







    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        //IntentFilter filter = new IntentFilter();
        //filter.addAction(ResponseReceiver.TRANSACTION_DONE);

        //receiver = new ResponseReceiver();
        //this.registerReceiver(receiver, filter);

        IntentFilter filter = new IntentFilter(ResponseReceiver.TRANSACTION_DONE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getActivity().registerReceiver(receiver, filter);



        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need



            }


            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }



                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                // getActivity().stopService(bookIntent);
                AddBook.this.restartLoader();
            }
        });






        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.

                //  http://stackoverflow.com/questions/20013213/zxing-onactivityresult-not-called-in-fragment-only-in-activity
                // (new IntentIntegrator(this)).initiateScan(); did not work in Fragment. It only works in Activity. Just
                // change the "this" to getActivity()
                //(new IntentIntegrator(getActivity())).initiateScan();

                // use getActivity() for Fragment
                Context context = getActivity();

                //IntentIntegrator integrator = new IntentIntegrator(getActivity());
                //integrator.initiateScan();
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);                        // after startActivityForResult(), it will call

                } catch (ActivityNotFoundException anfe) {
                    Log.e("onClick", "Scanner Not Found", anfe);
                }

                // CharSequence text = "This button should let you scan a book for its barcode!";
                // int duration = Toast.LENGTH_SHORT;

                // Toast toast = Toast.makeText(context, text, duration);
                // toast.show();


            }


        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
                ean.setContentDescription(getString(R.string.input_hint));  // no need for this because I introduced hint alreaady...
                ean.setHint(getString(R.string.input_hint));
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
            ean.setContentDescription(getString(R.string.input_hint));  // no need for this because I introduced hint alreaady...
            ean.setHint(getString(R.string.input_hint));




        }

        return rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                ean.setText(contents);  // then this goes to afterTextChanged() method
                // Handle successful scan
               // Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
               // toast.setGravity(Gravity.TOP, 25, 400);
               // toast.show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
               // Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
               // toast.setGravity(Gravity.TOP, 25, 400);
               // toast.show();

            }
        }
    }








    // https://github.com/ataulm/android-basic/blob/zxing/app/src/main/java/com/ataulm/basic/StartScanningFromFragmentActivity.java
    // @Override
  // public static void onActivityResult(int requestCode, int resultCode, Intent data, Context context) {
         //super.onActivityResult(requestCode, resultCode, data);
     //   IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
   //     String scanData = (scanningResult != null) ? scanningResult.getContents() : "";

   //     if (scanData == null || scanData.isEmpty()) {
            //scanDataTextView.setText("Scan complete, no data");
   //     } else {
           // scanDataTextView.setText(scanData);
   //     }
   // }





    // http://stackoverflow.com/questions/12074316/how-to-create-for-intentintegrator-in-android-with-zxing
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    switch(requestCode) {
    //        case IntentIntegrator.REQUEST_CODE:
    //        {
    //            if (resultCode == 1){
   //             }
    //            else
     //           {
    //                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
  //                  String UPCScanned = scanResult.getContents();
   //             }
   //             break;
    //        }
   //    }
   // }








    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));

        if (TextUtils.isEmpty(authors)) {
            ((TextView) rootView.findViewById(R.id.authors)).setLines(0);
            ((TextView) rootView.findViewById(R.id.authors)).setText(" ");
        } else {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        }



        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }



    // startActivityForResult()
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String TRANSACTION_DONE = "it.jaschke.alexandria.TRANSACTION_DONE";

        @Override
        public void onReceive(Context context, Intent intent) {

              // Update UI, new "message" processed by SimpleIntentService
            int mResult =  intent.getIntExtra(BookService.SERVICE_RESULT, 0);

            switch (mResult) {
                case BookService.STATUS_FINISHED:
                    // do nothing
                    break;
                case BookService.STATUS_ERROR:
                    ean.setText("");   // clear the screen
                    ean.setHint(getString(R.string.input_hint));
                    break;
                case BookService.STATUS_BOOKNOTFOUND :
                    //ean.setText("");   // I am not going to clear the screen if book is not found just to differentiate against status_error
                    ean.setHint(getString(R.string.input_hint));
                    break;
            }



        }
    }




    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }


        @Override
        public void onResume() {
            //Log.v(MainActivity.TAG, "in ArtistsFragment onResume");
            super.onResume();
        }

        @Override
        public void onPause() {
            //Log.v(MainActivity.TAG, "in ArtistsFragment onResume");
            super.onPause();
        }

    }
