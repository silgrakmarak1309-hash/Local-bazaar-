package com.example.ui.screens.safety

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyCenterScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportGeneralDialog by remember { mutableStateOf(false) }
    var reportCategory by remember { mutableStateOf("Online Scam / QR Code Fraud") }
    var reportDetails by remember { mutableStateOf("") }
    var showReportSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LocalBazaar Safety Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("safety_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Alert Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Golden Rule: No Advance Payments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Never send advance token money, scanning \"receive money\" QR codes, or sharing UPI PINs. Real buyers never ask you to scan a QR code or enter a PIN to receive money.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F1D1D),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Safety Guides
            item {
                Text(
                    text = "Key Safety Guidelines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SafetyRuleCard(
                    icon = Icons.Default.Place,
                    title = "Meet in Busy Public Places",
                    description = "Always schedule meetups in crowded daylight spots such as metro stations, bustling market squares, coffee shops, or shopping malls."
                )
            }

            item {
                SafetyRuleCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "Inspect Before Paying",
                    description = "Test electronics thoroughly (IMEI, battery health, accessories) and verify physical condition of items in person before completing any cash/UPI transfer."
                )
            }

            item {
                SafetyRuleCard(
                    icon = Icons.Default.Lock,
                    title = "Private Address & Identity Protection",
                    description = "LocalBazaar strictly hides your exact home address and coordinates. Never share Aadhaar photos, PAN cards, or passwords over chat."
                )
            }

            item {
                SafetyRuleCard(
                    icon = Icons.Default.DoNotDisturb,
                    title = "Prohibited Marketplace Items",
                    description = "Weapons, narcotics, counterfeit currency, prescription drugs, stolen goods, and adult products are strictly prohibited and immediately banned."
                )
            }

            // Quick Report Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showReportGeneralDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("report_incident_btn")
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report a Suspicious Incident / Scammer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showReportGeneralDialog) {
        AlertDialog(
            onDismissRequest = { showReportGeneralDialog = false },
            title = { Text("Report Incident", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select incident type:")
                    val cats = listOf(
                        "Online Scam / QR Code Fraud",
                        "Suspicious Seller Profile",
                        "Counterfeit / Fake Goods",
                        "Abusive Chat / Harassment"
                    )
                    cats.forEach { c ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportCategory = c }
                        ) {
                            RadioButton(selected = reportCategory == c, onClick = { reportCategory = c })
                            Text(c, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        label = { Text("Explain what happened") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitSafetyReport("General", "safety_center", reportCategory, reportDetails)
                        showReportGeneralDialog = false
                        showReportSuccess = true
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportGeneralDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportSuccess) {
        AlertDialog(
            onDismissRequest = { showReportSuccess = false },
            title = { Text("Incident Logged", fontWeight = FontWeight.Bold) },
            text = { Text("Thank you for helping keep LocalBazaar safe. Our compliance team has received your report and is investigating.") },
            confirmButton = {
                Button(onClick = { showReportSuccess = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SafetyRuleCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
