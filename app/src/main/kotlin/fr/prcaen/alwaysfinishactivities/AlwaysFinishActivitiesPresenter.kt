package fr.prcaen.alwaysfinishactivities

import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting

internal class AlwaysFinishActivitiesPresenter(
  private val platformHelper: PlatformHelper,
  private val screen: AlwaysFinishActivitiesScreen
) {

  @UiThread
  fun onAdded() {
    screen.registerSettingsContentObserver()

    updateUI()
  }

  @UiThread
  fun onRemoved() {
    screen.unregisterSettingsContentObserver()
  }

  @UiThread
  fun onClicked() {
    val newValue = !platformHelper.isAlwaysFinishActivities()

    if(!platformHelper.setAlwaysFinishActivities(newValue)) {
      screen.displayRequirePermissionAlert()

      return
    }

    updateUI()
  }

  @UiThread
  fun onSettingsChanged() {
    updateUI()
  }

  @UiThread
  @VisibleForTesting
  fun updateUI() {
    screen.updateTile(
        active = platformHelper.isAlwaysFinishActivities()
    )
  }
}