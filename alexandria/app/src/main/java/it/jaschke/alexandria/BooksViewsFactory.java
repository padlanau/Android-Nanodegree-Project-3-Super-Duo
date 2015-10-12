/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package it.jaschke.alexandria;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import it.jaschke.alexandria.data.AlexandriaContract;

public class BooksViewsFactory implements RemoteViewsService.RemoteViewsFactory {


  private Context ctxt=null;
  private int appWidgetId;

  private Cursor cursor;
  private ContentResolver cr;
  private String title;
  private String subTitle;
  private boolean hasNoBookScanned;

 // List<String> urlList = new ArrayList<String>();
  List<String> titleList = new ArrayList<String>();
 // List<String> subTitleList = new ArrayList<String>();

    public BooksViewsFactory(Context ctxt, Intent intent) {
    this.ctxt=ctxt;
    appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

      cr = ctxt.getContentResolver();
  }

  @Override
  public void onCreate() {
      cursor = cr.query(
              AlexandriaContract.BookEntry.CONTENT_URI,
              null, // leaving "columns" null just returns all the columns.
              null, // cols for "where" clause
              null, // values for "where" clause
              null  // sort order
      );


      if (cursor.moveToFirst()) {
          do {
          //    urlList.add(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL)));
              titleList.add(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE)));
          //     subTitleList.add(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)));

          }  // enddo
          while (cursor.moveToNext());
      }  // endif

  }

  @Override
  public void onDestroy() {
    // no-op
  }

  @Override
  public int getCount() {
      hasNoBookScanned = false;

      if (cursor.getCount() == 0) {
          hasNoBookScanned = true;
          return 1; // I'm setting this up to 1 so it would go to getViewAt() to display
      } else {

          // the count here will determine how many times you will return to getViewAt()
          return cursor.getCount();

      }

  }

  @Override
  public RemoteViews getViewAt(int position) {
    RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.row);

     row.setImageViewResource(R.id.imageview_fullbookcover, R.drawable.ic_launcher);

    if (hasNoBookScanned) {
        row.setTextViewText(R.id.textview_booktitle, "No books have been scanned yet. Please go to the app");
        row.setContentDescription(R.id.textview_booktitle, "No books have been scanned yet. Please go to the app");

      } else {

        row.setTextViewText(R.id.textview_booktitle, titleList.get(position));
        row.setContentDescription(R.id.textview_booktitle, titleList.get(position));

    }


     //row.setTextViewText(R.id.textview_booksubtitle, subTitleList.get(position));


    Intent i=new Intent();
    Bundle extras=new Bundle();

    extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    i.putExtras(extras);
    row.setOnClickFillInIntent(R.id.textview_booktitle, i);

    return(row);
  }

  @Override
  public RemoteViews getLoadingView() {
    return(null);
  }

  @Override
  public int getViewTypeCount() {
    return(1);
  }

  @Override
  public long getItemId(int position) {
    return(position);
  }

  @Override
  public boolean hasStableIds() {
    return(true);
  }

  @Override
  public void onDataSetChanged() {
    // no-op
  }
}