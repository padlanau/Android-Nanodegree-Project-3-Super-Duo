

package it.jaschke.alexandria;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return(new BooksViewsFactory(this.getApplicationContext(), intent));
  }
}