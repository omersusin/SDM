package grab.bit.android.pages.directorypicker

import android.content.Context
import android.os.Environment
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import grab.bit.android.pages.onboarding.permissions.ABDMPermissions
import grab.bit.android.pages.onboarding.permissions.rememberAppPermissionState
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitleWithDescription
import grab.bit.android.ui.SheetUI
import grab.bit.android.ui.configurable.RenderSpinnerInSheet
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.shared.ui.widget.IconActionButton
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.shared.ui.widget.PrimaryMainActionButton
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.ui.widget.TransparentIconActionButton
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.shared.util.ui.VerticalScrollableContent
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.util.PathValidator
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.createDirectories
import grab.bit.util.exists
import grab.bit.util.isDirectory
import grab.bit.util.listFiles
import grab.bit.util.listFilesOrNull
import grab.bit.util.startsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import kotlin.time.Duration.Companion.milliseconds

val alwaysAllowedPaths = listOf(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toOkioPath(),
)

@Composable
fun DirectoryPicker(
    title: StringSource,
    isVisible: Boolean,
    initialDirectory: Path,
    onDirectorySelected: (Path?) -> Unit
) {
    var changingRoot by remember { mutableStateOf(false) }
    val state = rememberResponsiveDialogState(false)
    LaunchedEffect(isVisible) {
        if (isVisible) {
            state.show()
        } else {
            state.hide()
        }
    }
    state.OnFullyDismissed {
        onDirectorySelected(null)
    }
    val onDismiss = state::hide
    ResponsiveDialog(
        state,
        onDismiss
    ) {
        var currentDirectory by remember(initialDirectory) {
            mutableStateOf(initialDirectory)
        }
        var creatingNewFolder by remember { mutableStateOf(false) }
        // update this counter in order to refresh directory list!
        var updateDirectories by remember { mutableIntStateOf(0) }
        fun refreshDirectories() {
            updateDirectories++
        }

        val storagePermissionState = rememberAppPermissionState(ABDMPermissions.StoragePermission)
        val context = LocalContext.current
        // Feature #15: allowlist removable SD-card volumes (from
        // getExternalFilesDirs) in addition to Downloads. The primary shared
        // root stays gated behind full storage access, as before.
        val primaryRoot = remember {
            runCatching { Environment.getExternalStorageDirectory().canonicalPath }.getOrNull()
        }
        val removableRoots = remember {
            runCatching { getRootPaths(context) }
                .getOrDefault(emptyList())
                .filter { it.toString() != primaryRoot }
        }
        val selectableRoots = remember {
            (alwaysAllowedPaths + removableRoots).distinct()
        }
        val hasRemovableVolume = remember(removableRoots) { removableRoots.isNotEmpty() }
        val safPickerLauncher = rememberSafDirectoryPickerLauncher { treeUri ->
            if (treeUri == null) return@rememberSafDirectoryPickerLauncher
            runCatching {
                SafFolderStorage.persistTreePermission(context, treeUri)
                SafFolderStorage.treeUriToFilePath(context, treeUri)
                    ?.toPath()
                    ?.takeIf { java.io.File(it.toString()).exists() }
                    ?.let { currentDirectory = it }
            }
        }
        val directoryList = remember(
            currentDirectory,
            updateDirectories,
            storagePermissionState.isGranted,
        ) {
            val weHaveFullAccess = storagePermissionState.isGranted
            DirectoryList(
                currentDirectory = currentDirectory,
                directories = runCatching {
                    currentDirectory
                        .listFiles()
                        .filter { it.isDirectory() }
                }
                    .getOrNull()
                    .orEmpty()
                    .map {
                        DirectoryItem(it.name, it)
                    },
                backDirectory = currentDirectory
                    .parent
                    // don't go somewhere that we can't return
                    ?.takeIf {
                        it.listFilesOrNull()?.isNotEmpty() ?: false
                    },
                currentDirectoryCanWrite = if (weHaveFullAccess) {
                    true
                } else {
                    selectableRoots.any { allowedPath ->
                        currentDirectory.startsWith(allowedPath)
                    }
                }
            )
        }
        val coroutineScope = rememberCoroutineScope()
        fun createNewFolderAndRefresh(newFolderName: String) {
            creatingNewFolder = false
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    currentDirectory.resolve(newFolderName).createDirectories()
                }
                delay(50.milliseconds)
                refreshDirectories()
                // schedule refresh
            }
        }
        SheetUI(
            header = {
                SheetHeader(
                    headerTitle = {
                        SheetTitleWithDescription(
                            title = title.rememberString(),
                            description = currentDirectory.toString()
                        )
                    },
                    headerActions = {
                        TransparentIconActionButton(
                            MyIcons.close,
                            contentDescription = Res.string.close.asStringSource(),
                            onClick = onDismiss
                        )
                    }
                )
            }
        ) {
            val horizontalPadding = mySpacings.largeSpace
            val itemPadding = PaddingValues(
                horizontal = mySpacings.largeSpace,
                vertical = mySpacings.mediumSpace
            )
            Column {
                val lazyListState = rememberLazyListState()
                AnimatedContent(
                    directoryList,
                    modifier = Modifier
                        .weight(1f, false)
                ) { directoryList ->
                    VerticalScrollableContent(
                        lazyListState = lazyListState,
                    ) {
                        Box(
                            Modifier.heightIn(250.dp)
                        ) {
                            LazyColumn {
                                if (directoryList.backDirectory != null) {
                                    item {
                                        val backDirectoryItem = remember(directoryList.currentDirectory) {
                                            DirectoryItem(
                                                name = "..",
                                                path = directoryList.backDirectory
                                            )
                                        }
                                        RenderDirectoryItem(
                                            modifier = Modifier
                                                .animateItem()
                                                .fillMaxWidth(),
                                            item = backDirectoryItem,
                                            onDirectorySelected = {
                                                currentDirectory = backDirectoryItem.path
                                            },
                                            itemPadding = itemPadding,
                                        )
                                    }
                                }
                                items(directoryList.directories) { directoryItem ->
                                    RenderDirectoryItem(
                                        modifier = Modifier
                                            .animateItem()
                                            .fillMaxWidth(),
                                        item = directoryItem,
                                        onDirectorySelected = {
                                            currentDirectory = directoryItem.path
                                        },
                                        itemPadding = itemPadding,
                                    )
                                }
                            }
                            if (directoryList.directories.isEmpty()) {
                                Text(
                                    myStringResource(Res.string.list_is_empty),
                                    Modifier
                                        .matchParentSize()
                                        .wrapContentSize()
                                )
                            }
                        }
                    }

                }
                Spacer(Modifier.height(mySpacings.mediumSpace))
                Column(
                    Modifier.padding(horizontal = horizontalPadding)
                ) {
                    AnimatedVisibility(!directoryList.currentDirectoryCanWrite && !storagePermissionState.isGranted) {
                        ActionButton(
                            text = myStringResource(Res.string.give_storage_permission),
                            onClick = {
                                storagePermissionState.launchRequest()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = mySpacings.mediumSpace),
                            borderColor = myColors.warningGradient,
                            contentColor = myColors.warning,
                            start = {
                                MyIcon(
                                    icon = storagePermissionState.appPermission.icon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = mySpacings.mediumSpace)
                                )
                            }
                        )
                    }
                    // Feature #15: removable-volume shortcut. Opens the system
                    // folder picker (SAF), persists the tree-URI grant, and
                    // jumps the in-app browser to the resolved path when possible.
                    AnimatedVisibility(hasRemovableVolume && !storagePermissionState.isGranted) {
                        ActionButton(
                            text = myStringResource(Res.string.storage_saf_picker),
                            onClick = {
                                safPickerLauncher.launch()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = mySpacings.mediumSpace),
                            borderColor = myColors.warningGradient,
                            contentColor = myColors.warning,
                            start = {
                                MyIcon(
                                    icon = MyIcons.folder,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = mySpacings.mediumSpace)
                                )
                            }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconActionButton(
                            MyIcons.folder,
                            contentDescription = Res.string.storage_roots.asStringSource(),
                            onClick = {
                                changingRoot = true
                            }
                        )
                        Spacer(Modifier.width(mySpacings.mediumSpace))
                        PrimaryMainActionButton(
                            text = myStringResource(Res.string.ok),
                            onClick = {
                                onDirectorySelected(currentDirectory)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = directoryList.currentDirectoryCanWrite,
                        )
                        Spacer(Modifier.width(mySpacings.mediumSpace))
                        IconActionButton(
                            MyIcons.add,
                            contentDescription = Res.string.new_folder.asStringSource(),
                            enabled = directoryList.currentDirectoryCanWrite,
                        ) {
                            creatingNewFolder = true
                        }
                    }
                }
            }
            SheetInput(
                isOpened = creatingNewFolder,
                title = Res.string.new_folder.asStringSource(),
                initialValue = { "" },
                validate = {
                    val newFolder = runCatching { currentDirectory.resolve(it) }.getOrNull() ?: return@SheetInput false
                    PathValidator.isValidPath(newFolder.toString()) && !newFolder.exists()
                },
                onConfirm = { newFolderName ->
                    createNewFolderAndRefresh(newFolderName)
                },
                onDismiss = {
                    creatingNewFolder = false
                }
            ) { params ->
                MyTextField(
                    text = params.editingValue,
                    onTextChange = params.setEditingValue,
                    modifier = params.modifier,
                    placeholder = "New Folder",
                    keyboardActions = params.keyboardActions,
                )
            }
        }
        StorageRoots(
            onRequestChangeStorageRoot = {
                currentDirectory = it
                changingRoot = false
            },
            currentDirectory = currentDirectory,
            isOpened = changingRoot,
            onDismiss = {
                changingRoot = false
            }
        )
    }
}

@Composable
private fun RenderDirectoryItem(
    modifier: Modifier,
    item: DirectoryItem,
    onDirectorySelected: () -> Unit,
    itemPadding: PaddingValues,
) {
    Row(
        modifier
            .clickable(
                onClick = onDirectorySelected
            )
            .heightIn(mySpacings.thumbSize)
            .padding(itemPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyIcon(MyIcons.folder, null)
        Spacer(Modifier.width(mySpacings.mediumSpace))
        Text(item.name)
    }
}

@Composable
private fun StorageRoots(
    onRequestChangeStorageRoot: (Path) -> Unit,
    currentDirectory: Path,
    isOpened: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val roots = remember {
        runCatching { getRootPaths(context) }
            .onFailure { it.printStackTrace() }
            .getOrElse { emptyList() }
    }
    val currentRoot = remember(currentDirectory) {
        roots.firstOrNull {
            currentDirectory.startsWith(it)
        }
    }
    RenderSpinnerInSheet(
        title = Res.string.storage_roots.asStringSource(),
        isOpened = isOpened,
        onDismiss = onDismiss,
        possibleValues = roots,
        value = currentRoot,
        onSelect = {
            if (it == null) {
                onDismiss()
            } else {
                onRequestChangeStorageRoot(it)
            }
        },
    ) {
        Row {
            Text(it.toString())
        }
    }
}

private fun getRootPaths(context: Context): List<Path> {
    val externalFilesDirs = context.getExternalFilesDirs(null)
    if (externalFilesDirs.isNullOrEmpty()) {
        return emptyList()
    }
    return externalFilesDirs
        .map {
            it
                .absolutePath
                .substringBefore("/Android/")
                .toPath()
        }
}

@Immutable
private data class DirectoryList(
    val currentDirectory: Path,
    val backDirectory: Path?,
    val directories: List<DirectoryItem>,
    val currentDirectoryCanWrite: Boolean,
)

@Immutable
private data class DirectoryItem(
    val name: String,
    val path: Path,
)
