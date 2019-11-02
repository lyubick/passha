package org.kgbt.passha.android

import android.util.Log
import org.kgbt.passha.core.logger.Logger
import org.kgbt.passha.core.logger.Output

class PasshaLogsOutput : Output {
    override fun init() {
        // Nothing is required here
    }

    override fun log(lvl: Logger.LOGLEVELS?, log: String?) {
        when (lvl) {
            Logger.LOGLEVELS.TRACE -> Log.v("", log)
            Logger.LOGLEVELS.DEBUG -> Log.d("", log)
            Logger.LOGLEVELS.ERROR -> Log.e("", log)
            Logger.LOGLEVELS.FATAL -> Log.wtf("", log)
            Logger.LOGLEVELS.SILENT -> {}       // Do nothing
        }
    }

    override fun terminate() {
        // Nothing is required here
    }
}