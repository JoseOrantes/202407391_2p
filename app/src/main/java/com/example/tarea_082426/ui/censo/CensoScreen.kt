package com.example.tarea_082426.ui.censo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.ui.res.stringResource
import com.example.tarea_082426.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensoScreen(
    userId: Int = 0,
    onBackToDashboard: () -> Unit = {},
    onMapClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    censoViewModel: CensoViewModel = viewModel()
) {
    val state by censoViewModel.state.collectAsState()

    var expandedPais by remember { mutableStateOf(false) }
    var expandedDept by remember { mutableStateOf(false) }
    var expandedMuni by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.census_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.width(360.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { censoViewModel.onNombreChange(it) },
                        label = { Text(stringResource(R.string.census_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.apellido,
                        onValueChange = { censoViewModel.onApellidoChange(it) },
                        label = { Text(stringResource(R.string.census_lastname)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.totalMiembros,
                        onValueChange = { censoViewModel.onTotalMiembrosChange(it) },
                        label = { Text(stringResource(R.string.census_total_members)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.cantidadTVs,
                        onValueChange = { censoViewModel.onCantidadTVsChange(it) },
                        label = { Text(stringResource(R.string.census_tv_count)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.latitud != null && state.longitud != null) {
                        Text(
                            text = "Ubicación seleccionada:\nLat: ${state.latitud}, Lng: ${state.longitud}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = onMapClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mapa")
                    }

                    /*
                    // 1. Dropdown País
                    ExposedDropdownMenuBox(
                        expanded = expandedPais,
                        onExpandedChange = { expandedPais = !expandedPais },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.paisSeleccionado?.nombre ?: stringResource(R.string.census_select_country),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.census_country)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPais) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPais,
                            onDismissRequest = { expandedPais = false }
                        ) {
                            state.paisesDisponibles.forEach { pais ->
                                DropdownMenuItem(
                                    text = { Text(pais.nombre) },
                                    onClick = {
                                        censoViewModel.onPaisSelected(pais)
                                        expandedPais = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Dropdown Departamento (Deshabilitado hasta que se elija País)
                    val paisElegido = state.paisSeleccionado != null
                    ExposedDropdownMenuBox(
                        expanded = expandedDept && paisElegido,
                        onExpandedChange = { if (paisElegido) expandedDept = !expandedDept },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.departamentoSeleccionado?.nombre ?: if (paisElegido) stringResource(R.string.census_select_department) else stringResource(R.string.census_select_country_first),
                            onValueChange = {},
                            readOnly = true,
                            enabled = paisElegido,
                            label = { Text(stringResource(R.string.census_department)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDept && paisElegido) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        if (paisElegido) {
                            ExposedDropdownMenu(
                                expanded = expandedDept,
                                onDismissRequest = { expandedDept = false }
                            ) {
                                state.departamentosDisponibles.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept.nombre) },
                                        onClick = {
                                            censoViewModel.onDepartamentoSelected(dept)
                                            expandedDept = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Dropdown Municipio (Deshabilitado hasta que se elija Departamento)
                    val deptElegido = state.departamentoSeleccionado != null
                    ExposedDropdownMenuBox(
                        expanded = expandedMuni && deptElegido,
                        onExpandedChange = { if (deptElegido) expandedMuni = !expandedMuni },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.municipioSeleccionado?.nombre ?: if (deptElegido) stringResource(R.string.census_select_municipality) else stringResource(R.string.census_select_department_first),
                            onValueChange = {},
                            readOnly = true,
                            enabled = deptElegido,
                            label = { Text(stringResource(R.string.census_municipality)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMuni && deptElegido) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        if (deptElegido) {
                            ExposedDropdownMenu(
                                expanded = expandedMuni,
                                onDismissRequest = { expandedMuni = false }
                            ) {
                                state.municipiosDisponibles.forEach { muni ->
                                    DropdownMenuItem(
                                        text = { Text(muni.nombre) },
                                        onClick = {
                                            censoViewModel.onMunicipioSelected(muni)
                                            expandedMuni = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                    */
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { censoViewModel.guardarCenso(userId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.census_save_button))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBackToDashboard,
                modifier = Modifier.width(300.dp)
            ) {
                Text(stringResource(R.string.census_back_dashboard))
            }

            if (state.mensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.mensaje,
                    color = if (state.censoGuardadoExitoso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
