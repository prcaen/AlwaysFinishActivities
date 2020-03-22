package fr.prcaen.alwaysfinishactivities

import android.database.ContentObserver
import android.os.Handler
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast

class AlwaysFinishActivitiesTileService : TileService(), AlwaysFinishActivitiesScreen {

  private val platformHelper: PlatformHelper by lazy {
    PlatformHelper(this.applicationContext)
  }

  private val presenter: AlwaysFinishActivitiesPresenter by lazy {
    AlwaysFinishActivitiesPresenter(platformHelper, this)
  }

  private val settingContentObserver: ContentObserver by lazy {
    object : ContentObserver(Handler()) {
      override fun onChange(selfChange: Boolean) {
        Log.d(LOG_TAG, "Settings changed: $selfChange")

        presenter.onSettingsChanged()
      }

      override fun deliverSelfNotifications(): Boolean = true
    }
  }

  override fun onTileAdded() {
    Log.d(LOG_TAG, "Tile added")

    presenter.onAdded()
  }

  override fun onClick() {
    Log.d(LOG_TAG, "Clicked")

    presenter.onClicked()
  }

  override fun onTileRemoved() {
    Log.d(LOG_TAG, "Tile removed")

    presenter.onRemoved()
  }

  override fun updateTile(active: Boolean) {
    qsTile?.state = if (active) STATE_ACTIVE else STATE_INACTIVE

    qsTile?.updateTile()
  }

  override fun registerSettingsContentObserver() {
    platformHelper.registerSettingsContentObserver(settingContentObserver)
  }

  override fun unregisterSettingsContentObserver() {
    platformHelper.unregisterSettingsContentObserver(settingContentObserver)
  }

  override fun displayRequirePermissionAlert() {
    Toast.makeText(this, R.string.permissions_required, Toast.LENGTH_LONG)
        .show()
  }

}