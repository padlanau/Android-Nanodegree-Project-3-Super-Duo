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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import it.jaschke.alexandria.data.AlexandriaContract;

public class AppWidget extends AppWidgetProvider {

  
  @Override
  public void onUpdate(Context ctxt, AppWidgetManager mgr, int[] appWidgetIds) {
    ComponentName me=new ComponentName(ctxt, AppWidget.class);
    mgr.updateAppWidget(me, buildUpdate(ctxt, appWidgetIds));

  }
  
  private RemoteViews buildUpdate(Context ctxt, int[] appWidgetIds) {
    RemoteViews updateViews = new RemoteViews(ctxt.getPackageName(), R.layout.widget);
  
    Intent i=new Intent(ctxt, AppWidget.class);
    
    i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
    
    PendingIntent pi= PendingIntent.getBroadcast(ctxt, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

    Cursor cursor = ctxt.getContentResolver().query(
            AlexandriaContract.BookEntry.CONTENT_URI,
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null  // sort order
    );

    int mCount = cursor.getCount();
    cursor.getColumnName(1);


    updateViews.setTextViewText(R.id.textview_description, ctxt.getString(R.string.scanned_books) + String.valueOf(mCount));
    updateViews.setContentDescription(R.id.textview_description, ctxt.getString(R.string.scanned_books) + String.valueOf(mCount));

    return(updateViews);
  }
}