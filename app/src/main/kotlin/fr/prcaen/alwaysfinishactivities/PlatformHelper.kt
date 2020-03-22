package fr.prcaen.alwaysfinishactivities

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Global.ALWAYS_FINISH_ACTIVITIES
import android.provider.Settings.SettingNotFoundException
import android.util.Log

internal class PlatformHelper(context: Context) {

  private val contentResolver: ContentResolver = context.contentResolver

  fun isAlwaysFinishActivities(): Boolean = try {
    Settings.Global.getInt(contentResolver, ALWAYS_FINISH_ACTIVITIES) == DONT_KEEP_ACTIVITIES_VALUE
  } catch (e: SettingNotFoundException) {
    Log.e(LOG_TAG, "Could not read global setting: $ALWAYS_FINISH_ACTIVITIES", e)
    false
  }

  fun setAlwaysFinishActivities(enable: Boolean): Boolean = try {
    setAlwaysFinishActivitiesInActivityManager(enable)
    setAlwaysFinishActivitiesInGlobalSettings(enable)

    true
  } catch (e: Exception) {
    Log.w(LOG_TAG, "Could not set setting: $ALWAYS_FINISH_ACTIVITIES", e)

    false
  }

  fun registerSettingsContentObserver(observer: ContentObserver) {
    contentResolver.registerContentObserver(
        Settings.System.getUriFor(ALWAYS_FINISH_ACTIVITIES),
        false,
        observer
    )
  }

  fun unregisterSettingsContentObserver(observer: ContentObserver) {
    contentResolver.unregisterContentObserver(observer)
  }

  /**
   * Set Don't keep activities in Activity Manager.
   *
   * Using reflection here, Android are restricting methods.
   *
   * @param enable to Enable Don't keep activities.
   */
  private fun setAlwaysFinishActivitiesInActivityManager(enable: Boolean) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      val serviceInstance = Class.forName(ACTIVITY_MANAGER_CLASS_NAME)
          .getMethod(ACTIVITY_MANAGER_METHOD_SINGLETON_NAME)
          .invoke(null)

      serviceInstance.javaClass
          .getMethod(SET_ALWAYS_FINISH_METHOD_NAME, Boolean::class.javaPrimitiveType)
          .invoke(serviceInstance, enable)
    } else {
      val serviceInstance = Class.forName(ACTIVITY_MANAGER_NATIVE_CLASS_NAME)
          .getMethod(ACTIVITY_MANAGER_NATIVE_METHOD_SINGLETON_NAME)
          .invoke(null)

      serviceInstance.javaClass
          .getMethod(SET_ALWAYS_FINISH_METHOD_NAME, Boolean::class.javaPrimitiveType)
          .invoke(serviceInstance, enable)
    }

  }

  /**
   * Set Don't keep activities in Global Settings.
   *
   * @param enable to Enable Don't keep activities.
   */
  private fun setAlwaysFinishActivitiesInGlobalSettings(enable: Boolean) {
    val value = if (enable) DONT_KEEP_ACTIVITIES_VALUE else KEEP_ACTIVITIES_VALUE
    Settings.Global.putInt(contentResolver, ALWAYS_FINISH_ACTIVITIES, value)
  }

  companion object {
    private const val ACTIVITY_MANAGER_CLASS_NAME = "android.app.ActivityManager"
    private const val ACTIVITY_MANAGER_NATIVE_CLASS_NAME = "android.app.ActivityManagerNative"
    private const val ACTIVITY_MANAGER_METHOD_SINGLETON_NAME = "getService"
    private const val ACTIVITY_MANAGER_NATIVE_METHOD_SINGLETON_NAME = "getDefault"
    private const val SET_ALWAYS_FINISH_METHOD_NAME = "setAlwaysFinish"

    private const val KEEP_ACTIVITIES_VALUE = 0
    private const val DONT_KEEP_ACTIVITIES_VALUE = 1
  }

}