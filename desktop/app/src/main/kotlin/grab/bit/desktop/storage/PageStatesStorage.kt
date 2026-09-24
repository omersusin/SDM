package grab.bit.desktop.storage

import grab.bit.desktop.pages.home.HomePageStateToPersist
import androidx.datastore.core.DataStore
import arrow.optics.optics
import grab.bit.desktop.pages.settings.SettingPageStateToPersist
import grab.bit.desktop.pages.singleDownloadPage.SingleDownloadPageStateStorage
import grab.bit.desktop.pages.singleDownloadPage.SingleDownloadPageStateToPersist
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.util.ConfigBaseSettingsByJson
import kotlinx.serialization.Serializable

@optics
@Serializable
data class CommonData(
    val lastSavedLocations: List<String> = emptyList(),
) {
    companion object
}

@optics
@Serializable
data class PageStatesModel(
    val home: HomePageStateToPersist = HomePageStateToPersist(),
    val settings: SettingPageStateToPersist = SettingPageStateToPersist(),
    val downloadPage: SingleDownloadPageStateToPersist = SingleDownloadPageStateToPersist(),
    val global: CommonData = CommonData(),
) {
    companion object {
        val default get() = PageStatesModel()
    }
}

class PageStatesStorage(
    settings: DataStore<PageStatesModel>,
) : ConfigBaseSettingsByJson<PageStatesModel>(settings),
    ILastSavedLocationsStorage,
    SingleDownloadPageStateStorage {
    override val lastUsedSaveLocations = from(PageStatesModel.global.lastSavedLocations)
    override val singleDownloadPageState = from(PageStatesModel.downloadPage)
    val homePageStorage = from(PageStatesModel.home)
    val settingsPageStorage = from(PageStatesModel.settings)
}
