package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.data.PinFunction
import com.bynd.esp32dashboard.data.PinSpec
import com.bynd.esp32dashboard.data.Pins
import com.bynd.esp32dashboard.ui.theme.AppColors

/**
 * Dropdown de pines.
 * Muestra solo los GPIOs compatibles con la función requerida.
 * Pines en uso por otros componentes aparecen deshabilitados. -bynd
 */
@Composable
fun PinSelector(
    label: String,
    selected: Int?,
    requiredFunction: PinFunction,
    usedPins: Set<Int>,
    onSelected: (Int?) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val candidates: List<PinSpec> = remember(requiredFunction) {
        Pins.candidates(requiredFunction)
    }

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
        Spacer(Modifier.height(4.dp))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.IconBg, RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isError) AppColors.Error else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.let { "GPIO $it" } ?: "— Sin asignar —",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected != null) AppColors.TextPrimary else AppColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                    tint = AppColors.TextSecondary)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("— Sin asignar —", color = AppColors.TextSecondary) },
                    onClick = { onSelected(null); expanded = false }
                )
                candidates.forEach { spec ->
                    val inUse = spec.gpio in usedPins && spec.gpio != selected
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    "GPIO ${spec.gpio}",
                                    color = if (inUse) AppColors.TextSecondary else AppColors.TextPrimary
                                )
                                Text(
                                    spec.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextSecondary
                                )
                            }
                        },
                        leadingIcon = {
                            if (selected == spec.gpio) {
                                Icon(Icons.Default.Check, contentDescription = null,
                                    tint = AppColors.Primary)
                            }
                        },
                        enabled = !inUse,
                        onClick = { onSelected(spec.gpio); expanded = false }
                    )
                }
            }
        }
    }
}
