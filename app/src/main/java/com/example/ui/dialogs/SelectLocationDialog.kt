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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
      "Kamrup Rural",
      "Cachar (Silchar)",
      "Dibrugarh",
      "Jorhat",
      "Nagaon",
      "Goalpara",
      "Dhubri",
      "Kokrajhar",
      "Tinsukia",
      "Sonitpur (Tezpur)",
      "Barpeta",
      "Bongaigaon",
      "Nalbari",
      "Sivasagar",
      "Karimganj",
      "Hailakandi",
      "Dima Hasao",
      "Karbi Anglong"
    ),
    "Delhi" to listOf(
      "New Delhi",
      "Central Delhi",
      "South Delhi",
      "South East Delhi",
      "South West Delhi",
      "North Delhi",
      "North East Delhi",
      "North West Delhi",
      "East Delhi",
      "West Delhi",
      "Shahdara"
    ),
    "Maharashtra" to listOf(
      "Mumbai City",
      "Mumbai Suburban",
      "Pune",
      "Nagpur",
      "Thane",
      "Nashik",
      "Aurangabad (Chhatrapati Sambhaji Nagar)",
      "Solapur",
      "Amravati",
      "Kolhapur",
      "Navi Mumbai"
    ),
    "Karnataka" to listOf(
      "Bengaluru Urban",
      "Bengaluru Rural",
      "Mysuru",
      "Hubballi-Dharwad",
      "Mangaluru (Dakshina Kannada)",
      "Belagavi",
      "Kalaburagi",
      "Ballari",
      "Udupi"
    ),
    "West Bengal" to listOf(
      "Kolkata",
      "North 24 Parganas",
      "South 24 Parganas",
      "Howrah",
      "Darjeeling",
      "Siliguri",
      "Hooghly",
      "Jalpaiguri",
      "Malda",
      "Murshidabad",
      "Purba Medinipur"
    ),
    "Uttar Pradesh" to listOf(
      "Lucknow",
      "Noida (Gautam Buddha Nagar)",
      "Ghaziabad",
      "Kanpur",
      "Varanasi",
      "Agra",
      "Prayagraj",
      "Meerut",
      "Bareilly",
      "Aligarh",
      "Gorakhpur"
    ),
    "Bihar" to listOf(
      "Patna",
      "Gaya",
      "Muzaffarpur",
      "Bhagalpur",
      "Darbhanga",
      "Purnia",
      "Begusarai",
      "Bhojpur (Arrah)"
    ),
    "Tamil Nadu" to listOf(
      "Chennai",
      "Coimbatore",
      "Madurai",
      "Tiruchirappalli",
      "Salem",
      "Tirunelveli",
      "Erode",
      "Vellore"
    ),
    "Telangana" to listOf(
      "Hyderabad",
      "Warangal",
      "Rangareddy",
      "Medchal-Malkajgiri",
      "Nizamabad",
      "Karimnagar",
      "Khammam"
    ),
    "Andhra Pradesh" to listOf(
      "Visakhapatnam",
      "Vijayawada",
      "Guntur",
      "Nellore",
      "Kurnool",
      "Tirupati",
      "Kakinada"
    ),
    "Gujarat" to listOf(
      "Ahmedabad",
      "Surat",
      "Vadodara",
      "Rajkot",
      "Bhavnagar",
      "Jamnagar",
      "Gandhinagar"
    ),
    "Rajasthan" to listOf(
      "Jaipur",
      "Jodhpur",
      "Udaipur",
      "Kota",
      "Bikaner",
      "Ajmer",
      "Alwar"
    ),
    "Kerala" to listOf(
      "Thiruvananthapuram",
      "Ernakulam (Kochi)",
      "Kozhikode",
      "Thrissur",
      "Kollam",
      "Kannur",
      "Palakkad",
      "Kottayam"
    ),
    "Punjab" to listOf(
      "Ludhiana",
      "Amritsar",
      "Jalandhar",
      "Patiala",
      "Bathinda",
      "Mohali (SAS Nagar)"
    ),
    "Haryana" to listOf(
      "Gurugram",
      "Faridabad",
      "Panipat",
      "Ambala",
      "Karnal",
      "Hisar",
      "Rohtak",
      "Panchkula"
    ),
    "Madhya Pradesh" to listOf(
      "Indore",
      "Bhopal",
      "Jabalpur",
      "Gwalior",
      "Ujjain",
      "Sagar"
    ),
    "Odisha" to listOf(
      "Khordha (Bhubaneswar)",
      "Cuttack",
      "Sundargarh (Rourkela)",
      "Ganjam (Berhampur)",
      "Sambalpur",
      "Puri"
    ),
    "Jharkhand" to listOf(
      "Ranchi",
      "East Singhbhum (Jamshedpur)",
      "Dhanbad",
      "Bokaro",
      "Deoghar",
      "Hazaribagh"
    ),
    "Chhattisgarh" to listOf(
      "Raipur",
      "Durg (Bhilai)",
      "Bilaspur",
      "Korba",
      "Rajnandgaon"
    ),
    "Uttarakhand" to listOf(
      "Dehradun",
      "Haridwar",
      "Nainital",
      "Rishikesh",
      "Haldwani",
      "Roorkee"
    ),
    "Himachal Pradesh" to listOf(
      "Shimla",
      "Kangra (Dharamshala)",
      "Mandi",
      "Solan",
      "Kullu & Manali"
    ),
    "Tripura" to listOf(
      "West Tripura (Agartala)",
      "Gomati",
      "South Tripura",
      "North Tripura",
      "Dhalai",
      "Khowai"
    ),
    "Manipur" to listOf(
      "Imphal West",
      "Imphal East",
      "Thoubal",
      "Bishnupur",
      "Churachandpur"
    ),
    "Nagaland" to listOf(
      "Kohima",
      "Dimapur",
      "Mokokchung",
      "Tuensang",
      "Wokha",
      "Mon"
    ),
    "Mizoram" to listOf(
      "Aizawl",
      "Lunglei",
      "Champhai",
      "Kolasib",
      "Serchhip"
    ),
    "Arunachal Pradesh" to listOf(
      "Papum Pare (Itanagar)",
      "Changlang",
      "West Kameng",
      "Tawang",
      "East Siang"
    ),
    "Sikkim" to listOf(
      "East Sikkim (Gangtok)",
      "West Sikkim (Gyalshing)",
      "South Sikkim (Namchi)",
      "North Sikkim (Mangan)"
    ),
    "Goa" to listOf(
      "North Goa (Panaji)",
      "South Goa (Margao)"
    ),
    "Jammu & Kashmir" to listOf(
      "Srinagar",
      "Jammu",
      "Anantnag",
      "Baramulla",
      "Kathua",
      "Udhampur"
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
      "Karaikal",
      "Mahe",
      "Yanam"
    ),
    "Andaman & Nicobar" to listOf(
      "South Andaman (Port Blair)",
      "North and Middle Andaman",
      "Nicobar"
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
              text = "Select Location",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
            Text(
              text = "All 28 States & 8 Union Territories across India",
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

        Spacer(modifier = Modifier.height(14.dp))

        // GPS Auto-detect button
        OutlinedButton(
          onClick = {
            selectedState = "Meghalaya"
            selectedDistrict = "West Garo Hills (Tura)"
            onSaveLocation("Meghalaya", "West Garo Hills (Tura)")
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF15803D)
          )
        ) {
          Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            tint = Color(0xFF15803D),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.size(6.dp))
          Text(
            text = "Auto-Detect Current GPS (Tura)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

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
