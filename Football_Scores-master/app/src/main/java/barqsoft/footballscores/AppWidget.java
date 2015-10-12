

package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.service.myFetchService;

public class AppWidget extends AppWidgetProvider {

  
  @Override
  public void onUpdate(Context ctxt, AppWidgetManager mgr, int[] appWidgetIds) {
    ComponentName me=new ComponentName(ctxt, AppWidget.class);
      
    mgr.updateAppWidget(me, buildUpdate(ctxt, appWidgetIds));
  }
  
  private RemoteViews buildUpdate(Context ctxt, int[] appWidgetIds) {
    RemoteViews updateViews=new RemoteViews(ctxt.getPackageName(), R.layout.widget);
  
    Intent i=new Intent(ctxt, AppWidget.class);
    
    i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
    
    PendingIntent pi= PendingIntent.getBroadcast(ctxt, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

      //
      int mPages = PagerFragment.NUM_PAGES;

      Intent service_start = new Intent(ctxt, myFetchService.class);
      ctxt.startService(service_start);

      SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-dd");

    // today
      Date today = Calendar.getInstance().getTime();
      String mToday = match_date.format(today);
      String[] argsToday = { mToday};

      // tomorrow
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_YEAR, 1);
      Date tomorrow = cal.getTime();
      String mTomorrow = match_date.format(tomorrow);
      String[] argsTomorrow = {mTomorrow};

      // Get Today's number of games
      Uri uri = Uri.withAppendedPath(DatabaseContract.BASE_CONTENT_URI, "date");    // content://barqsoft.footballscores/date
      Cursor cursor = ctxt.getContentResolver().query(uri, null, null, argsToday, null);
      int mCountResults = cursor.getCount();

      updateViews.setTextViewText(R.id.textview_today,  ctxt.getString(R.string.today_gameresults) + String.valueOf(mCountResults));
      updateViews.setContentDescription(R.id.textview_today, ctxt.getString(R.string.today_gameresults) + String.valueOf(mCountResults));

    // Get Tomorrow's number of games
      uri = Uri.withAppendedPath(DatabaseContract.BASE_CONTENT_URI, "date");    // content://barqsoft.footballscores/date
      cursor = ctxt.getContentResolver().query(uri, null, null, argsTomorrow, null);
      mCountResults = cursor.getCount();

      updateViews.setTextViewText(R.id.textview_tomorrow,  ctxt.getString(R.string.tomorrow_gameresults) + String.valueOf(mCountResults));
      updateViews.setContentDescription(R.id.textview_tomorrow, ctxt.getString(R.string.tomorrow_gameresults) + String.valueOf(mCountResults));
    return(updateViews);
  }
}