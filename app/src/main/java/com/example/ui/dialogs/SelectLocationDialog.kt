package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SelectLocationDialog(
  currentState: String,
  currentDistrict: String,
  onDismiss: () -> Unit,
  onSaveLocation: (state: String, district: String) -> Unit
) {
  var selectedState by remember { mutableStateOf(currentState) }
  var selectedDistrict by remember { mutableStateOf(currentDistrict) }
  var stateMenuExpanded by remember { mutableStateOf(false) }
  var districtMenuExpanded by remember { mutableStateOf(false) }

  val statesAndDistricts = mapOf(
    "Meghalaya" to listOf(
      "West Garo Hills (Tura)",
      "East Garo Hills (Williamnagar)",
      "South Garo Hills (Baghmara)",
      "North Garo Hills (Resubelpara)",
      "South West Garo Hills (Ampati)",
      "East Khasi Hills (Shillong)",
      "West Khasi Hills (Nongstoin)",
      "Eastern West Khasi Hills (Mairang)",
      "South West Khasi Hills (Mawkyrwat)",
      "Ri-Bhoi (Nongpoh)",
      "West Jaintia Hills (Jowai)",
      "East Jaintia Hills (Khliehriat)"
    ),
    "Assam" to listOf(
      "Kamrup Metropolitan (Guwahati)",
      "Cachar (Silchar)",
      "Dibrugarh",
      "Jorhat",
      "Nagaon",
      "Goalpara",
      "Dhubri",
      "Kokrajhar",
      "Tinsukia",
      "Sonitpur (Tezpur)"
    ),
    "Delhi" to listOf(
      "New Delhi",
      "Central Delhi",
      "South Delhi",
      "North Delhi",
      "East Delhi",
      "West Delhi"
    ),
    "West Bengal" to listOf(
      "Kolkata",
      "North 24 Parganas",
      "South 24 Parganas",
      "Howrah",
      "Darjeeling",
      "Siliguri",
      "Hooghly"
    ),
    "Maharashtra" to listOf(
      "Mumbai City",
      "Mumbai Suburban",
      "Pune",
      "Nagpur",
      "Thane",
      "Nashik"
    ),
    "Karnataka" to listOf(
      "Bengaluru Urban",
      "Mysuru",
      "Hubballi-Dharwad",
      "Mangaluru",
      "Belagavi"
    ),
    "Tamil Nadu" to listOf(
      "Chennai",
      "Coimbatore",
      "Madurai",
      "Tiruchirappalli",
      "Salem"
    ),
    "Uttar Pradesh" to listOf(
      "Lucknow",
      "Noida (Gautam Buddha Nagar)",
      "Kanpur",
      "Varanasi",
      "Agra",
      "Prayagraj"
    ),
    "Bihar" to listOf(
      "Patna",
      "Gaya",
      "Muzaffarpur",
      "Bhagalpur"
    ),
    "Rajasthan" to listOf(
      "Jaipur",
      "Jodhpur",
      "Udaipur",
      "Kota"
    ),
    "Gujarat" to listOf(
      "Ahmedabad",
      "Surat",
      "Vadodara",
      "Rajkot"
    ),
    "Punjab" to listOf(
      "Ludhiana",
      "Amritsar",
      "Jalandhar",
      "Patiala"
    ),
    "Haryana" to listOf(
      "Gurugram",
      "Faridabad",
      "Panchkula",
      "Ambala"
    ),
    "Kerala" to listOf(
      "Thiruvananthapuram",
      "Ernakulam (Kochi)",
      "Kozhikode",
      "Thrissur"
    ),
    "Telangana" to listOf(
      "Hyderabad",
      "Warangal",
      "Rangareddy",
      "Medchal"
    ),
    "Odisha" to listOf(
      "Khordha (Bhubaneswar)",
      "Cuttack",
      "Puri",
      "Rourkela"
    ),
    "Madhya Pradesh" to listOf(
      "Indore",
      "Bhopal",
      "Jabalpur",
      "Gwalior"
    ),
    "Jharkhand" to listOf(
      "Ranchi",
      "Jamshedpur",
      "Dhanbad",
      "Bokaro"
    ),
    "Tripura" to listOf(
      "West Tripura (Agartala)",
      "Gomati",
      "South Tripura"
    ),
    "Manipur" to listOf(
      "Imphal West",
      "Imphal East",
      "Churachandpur"
    ),
    "Nagaland" to listOf(
      "Kohima",
      "Dimapur",
      "Mokokchung"
    ),
    "Mizoram" to listOf(
      "Aizawl",
      "Lunglei",
      "Champhai"
    ),
    "Arunachal Pradesh" to listOf(
      "Papum Pare (Itanagar)",
      "Changlang",
      "Tawang"
    ),
    "Sikkim" to listOf(
      "Gangtok",
      "Namchi",
      "Gyalshing"
    ),
    "Goa" to listOf(
      "North Goa (Panaji)",
      "South Goa (Margao)"
    ),
    "Jammu & Kashmir" to listOf(
      "Srinagar",
      "Jammu",
      "Anantnag"
    ),
    "Ladakh" to listOf(
      "Leh",
      "Kargil"
    ),
    "Chandigarh" to listOf(
      "Chandigarh"
    ),
    "Puducherry" to listOf(
      "Puducherry",
      "Karaikal"
    ),
    "Andaman & Nicobar" to listOf(
      "South Andaman (Port Blair)"
    )
  )

  val availableDistricts = statesAndDistricts[selectedState] ?: listOf("Default District")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("location_dialog_surface")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header with Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Select State & District",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
            Text(
              text = "Full India database: All 28 States & 8 Union Territories.",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )
          }

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable { onDismiss() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State Dropdown
        Text(
          text = "Select State / Union Territory",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { stateMenuExpanded = true }
              .testTag("select_state_dropdown"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedState,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF64748B)
              )
            }
          }

          DropdownMenu(
            expanded = stateMenuExpanded,
            onDismissRequest = { stateMenuExpanded = false }
          ) {
            statesAndDistricts.keys.forEach { state ->
              DropdownMenuItem(
                text = { Text(text = state, fontSize = 14.sp) },
                onClick = {
                  selectedState = state
                  selectedDistrict = statesAndDistricts[state]?.firstOrNull() ?: ""
                  stateMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // District Dropdown
        Text(
          text = "Select District",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { districtMenuExpanded = true }
              .testTag("select_district_dropdown"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedDistrict,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF64748B)
              )
            }
          }

          DropdownMenu(
            expanded = districtMenuExpanded,
            onDismissRequest = { districtMenuExpanded = false }
          ) {
            availableDistricts.forEach { district ->
              DropdownMenuItem(
                text = { Text(text = district, fontSize = 14.sp) },
                onClick = {
                  selectedDistrict = district
                  districtMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Location Button
        Button(
          onClick = {
            onSaveLocation(selectedState, selectedDistrict)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_location_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF15803D)
          )
        ) {
          Text(
            text = "Save Location",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
