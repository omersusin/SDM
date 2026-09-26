package grab.bit.android.pages.directorypicker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import grab.bit.util.compose.StringSource
import okio.Path.Companion.toPath

class DirectoryPickerLauncher(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}


@Composable
fun rememberAndroidDirectoryPickerLauncher(
    initialDirectory: String?,
    title: StringSource,
    onDirectorySelected: (String?) -> Unit,
): DirectoryPickerLauncher {
    val pickFolderLauncher = rememberLauncherForActivityResult(
        contract = DirectoryPickerActivity.Contract,
    ) { directory ->
        onDirectorySelected(directory?.toString())
    }
    val initialDirectory by rememberUpdatedState(initialDirectory)
    val title by rememberUpdatedState(title)
    return remember {
        DirectoryPickerLauncher {
            pickFolderLauncher.launch(
                DirectoryPickerActivity.Inputs(
                    title = title,
                    initialDirectory = initialDirectory?.toPath(),
                )
            )
        }
    }
}

class SafDirectoryPickerLauncher(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}

/**
 * Feature #15: system folder picker (Storage Access Framework,
 * [ActivityResultContracts.OpenDocumentTree]) for removable/SD-card volumes
 * the in-app browser cannot write to directly. The caller is responsible for
 * persisting the returned tree URI (see [SafFolderStorage]).
 */
@Composable
fun rememberSafDirectoryPickerLauncher(
    onTreePicked: (Uri?) -> Unit,
): SafDirectoryPickerLauncher {
    val currentHandler by rememberUpdatedState(onTreePicked)
    val safLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        currentHandler(uri)
    }
    return remember {
        SafDirectoryPickerLauncher {
            safLauncher.launch(null)
        }
    }
}
