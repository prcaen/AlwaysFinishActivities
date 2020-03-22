package fr.prcaen.alwaysfinishactivities

import androidx.annotation.UiThread

internal interface AlwaysFinishActivitiesScreen {

  @UiThread
  fun updateTile(active: Boolean)

  @UiThread
  fun registerSettingsContentObserver()

  @UiThread
  fun unregisterSettingsContentObserver()

  @UiThread
  fun displayRequirePermissionAlert()

}