package ru.mephi.voip.ui.screens.settings.params

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mephi.voip.R
import ru.mephi.voip.ui.screens.settings.dialogs.ConfirmationDialog

@Composable
internal fun ClearCatalogCacheParam(
    clearCatalogCache: () -> Unit
) {
    var dialog by remember { mutableStateOf(false) }
    SettingsParam(
        title = stringResource(R.string.param_clear_catalog_cache),
        description = stringResource(R.string.param_clear_catalog_cache_description),
        trailingIcon = {
            Box(modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                )
            }
        },
        onClick = { dialog = true }
    )
    if (dialog) {
        ConfirmationDialog(
            onDismissRequest = { dialog = false },
            onConfirm = {
                dialog = false
                clearCatalogCache()
            },
            title = stringResource(R.string.param_clear_catalog_cache),
            text = stringResource(R.string.param_clear_catalog_cache_confirmation)
        )
    }
}