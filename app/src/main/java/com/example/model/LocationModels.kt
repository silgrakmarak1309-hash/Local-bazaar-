package com.example.model

data class LocationState(
    val id: String,
    val name: String,
    val isUnionTerritory: Boolean = false,
    val districts: List<LocationDistrict>
)

data class LocationDistrict(
    val id: String,
    val name: String,
    val stateName: String,
    val areas: List<String> = emptyList()
)

data class SelectedLocation(
    val country: String = "India",
    val state: String = "Maharashtra",
    val district: String = "Mumbai Suburban",
    val area: String = "Bandra West"
) {
    val displayShort: String get() = if (area.isNotBlank()) "$area, $district" else district
    val displayFull: String get() = if (area.isNotBlank()) "$area, $district, $state" else "$district, $state"
}

object IndiaLocationData {
    val states: List<LocationState> = listOf(
        // 1. Andhra Pradesh
        LocationState("AP", "Andhra Pradesh", false, listOf(
            LocationDistrict("ap_vsp", "Visakhapatnam", "Andhra Pradesh", listOf("Gajuwaka", "MVP Colony", "Madhurawada", "Dwaraka Nagar", "Siripuram", "Pendurthi")),
            LocationDistrict("ap_vja", "NTR (Vijayawada)", "Andhra Pradesh", listOf("Benz Circle", "MG Road", "Governorpet", "Gollapudi", "Patamata")),
            LocationDistrict("ap_gnt", "Guntur", "Andhra Pradesh", listOf("Arundelpet", "Brodipet", "Kothapet", "Pattabhipuram")),
            LocationDistrict("ap_tir", "Tirupati", "Andhra Pradesh", listOf("Alipiri", "Bhavani Nagar", "Chandragiri", "Renigunta")),
            LocationDistrict("ap_kdd", "Kakinada", "Andhra Pradesh", listOf("Bhanugudi Junction", "Main Road", "Suryaraopeta")),
            LocationDistrict("ap_knl", "Kurnool", "Andhra Pradesh", listOf("Nandyal Road", "Park Road", "Birla Gate")),
            LocationDistrict("ap_nlr", "Sri Potti Sriramulu Nellore", "Andhra Pradesh", listOf("Pogathota", "Magunta Layout", "Vedayapalem")),
            LocationDistrict("ap_ana", "Ananthapuramu", "Andhra Pradesh", listOf("Court Road", "Kamalanagar", "Clock Tower")),
            LocationDistrict("ap_kad", "YSR Kadapa", "Andhra Pradesh", listOf("Raja Reddy Street", "Seven Roads Junction")),
            LocationDistrict("ap_eg", "East Godavari (Rajahmundry)", "Andhra Pradesh", listOf("Danavaipeta", "Kotipalli Bus Stand", "Innespeta")),
            LocationDistrict("ap_wg", "West Godavari (Bhimavaram)", "Andhra Pradesh", listOf("Somavaram", "Balusumoodi")),
            LocationDistrict("ap_elu", "Eluru", "Andhra Pradesh", listOf("Powerpet", "RR Pet")),
            LocationDistrict("ap_kri", "Krishna (Machilipatnam)", "Andhra Pradesh", listOf("Koneru Center", "Frenchpet")),
            LocationDistrict("ap_pra", "Prakasam (Ongole)", "Andhra Pradesh", listOf("Trunk Road", "Lawyerpet")),
            LocationDistrict("ap_sri", "Srikakulam", "Andhra Pradesh", listOf("Palakonda Road", "Seven Road Junction")),
            LocationDistrict("ap_vzn", "Vizianagaram", "Andhra Pradesh", listOf("Cantonment", "Mayuri Junction")),
            LocationDistrict("ap_vzn_m", "Parvathipuram Manyam", "Andhra Pradesh", listOf("Main Market", "Railway Station Road")),
            LocationDistrict("ap_asr", "Alluri Sitharama Raju", "Andhra Pradesh", listOf("Paderu", "Araku Valley")),
            LocationDistrict("ap_nak", "Anakapalli", "Andhra Pradesh", listOf("Main Bazaar", "Ring Road")),
            LocationDistrict("ap_kns", "Dr. B.R. Ambedkar Konaseema", "Andhra Pradesh", listOf("Amalapuram", "Ravulapalem")),
            LocationDistrict("ap_bpt", "Bapatla", "Andhra Pradesh", listOf("Old Bus Stand", "Surya Lanka Road")),
            LocationDistrict("ap_pal", "Palnadu (Narasaraopet)", "Andhra Pradesh", listOf("Prakash Nagar", "Station Road")),
            LocationDistrict("ap_ndl", "Nandyal", "Andhra Pradesh", listOf("Sanjeeva Nagar", "Gandhi Chowk")),
            LocationDistrict("ap_sss", "Sri Sathya Sai (Puttaparthi)", "Andhra Pradesh", listOf("Chitravathi Road", "Gopuram")),
            LocationDistrict("ap_anm", "Annamayya (Rayachoti)", "Andhra Pradesh", listOf("Madanapalle", "Rayachoti Main Road")),
            LocationDistrict("ap_ctr", "Chittoor", "Andhra Pradesh", listOf("High Road", "Gandhi Statue"))
        )),

        // 2. Arunachal Pradesh
        LocationState("AR", "Arunachal Pradesh", false, listOf(
            LocationDistrict("ar_ita", "Itanagar Capital Complex", "Arunachal Pradesh", listOf("Ganga Market", "E-Sector", "Naharlagun", "Nirjuli")),
            LocationDistrict("ar_taw", "Tawang", "Arunachal Pradesh", listOf("Old Market", "New Market")),
            LocationDistrict("ar_wka", "West Kameng (Bomdila)", "Arunachal Pradesh", listOf("Main Market", "Bazar Line")),
            LocationDistrict("ar_eka", "East Kameng (Seppa)", "Arunachal Pradesh", listOf("Seppa Town", "Bazar")),
            LocationDistrict("ar_pap", "Papum Pare (Yupia)", "Arunachal Pradesh", listOf("Yupia Town", "Doimukh")),
            LocationDistrict("ar_krd", "Kra Daadi", "Arunachal Pradesh", listOf("Palin", "Jamin")),
            LocationDistrict("ar_kur", "Kurung Kumey", "Arunachal Pradesh", listOf("Koloriang Town")),
            LocationDistrict("ar_lsu", "Lower Subansiri (Ziro)", "Arunachal Pradesh", listOf("Hapoli", "Old Ziro")),
            LocationDistrict("ar_usu", "Upper Subansiri (Daporijo)", "Arunachal Pradesh", listOf("Daporijo Town")),
            LocationDistrict("ar_wsi", "West Siang (Aalo)", "Arunachal Pradesh", listOf("Aalo Main Market")),
            LocationDistrict("ar_esi", "East Siang (Pasighat)", "Arunachal Pradesh", listOf("Pasighat Bazar", "Medical Colony")),
            LocationDistrict("ar_sia", "Siang (Pangin)", "Arunachal Pradesh", listOf("Pangin Town", "Boleng")),
            LocationDistrict("ar_usi", "Upper Siang (Yingkiong)", "Arunachal Pradesh", listOf("Yingkiong Town")),
            LocationDistrict("ar_lsi", "Lower Siang (Likabali)", "Arunachal Pradesh", listOf("Likabali Town")),
            LocationDistrict("ar_ldv", "Lower Dibang Valley (Roing)", "Arunachal Pradesh", listOf("Roing Market")),
            LocationDistrict("ar_div", "Dibang Valley (Anini)", "Arunachal Pradesh", listOf("Anini Town")),
            LocationDistrict("ar_anj", "Anjaw (Hawai)", "Arunachal Pradesh", listOf("Hawai Town", "Hayuliang")),
            LocationDistrict("ar_loh", "Lohit (Tezu)", "Arunachal Pradesh", listOf("Tezu Main Bazar")),
            LocationDistrict("ar_nam", "Namsai", "Arunachal Pradesh", listOf("Namsai Bazar", "Chowkham")),
            LocationDistrict("ar_cha", "Changlang", "Arunachal Pradesh", listOf("Miao", "Jairampur", "Changlang Town")),
            LocationDistrict("ar_tir", "Tirap (Khonsa)", "Arunachal Pradesh", listOf("Khonsa Town")),
            LocationDistrict("ar_lon", "Longding", "Arunachal Pradesh", listOf("Longding Bazar")),
            LocationDistrict("ar_kam", "Kamle (Raga)", "Arunachal Pradesh", listOf("Raga Town")),
            LocationDistrict("ar_pke", "Pakke Kessang", "Arunachal Pradesh", listOf("Lemmi", "Seijosa")),
            LocationDistrict("ar_lpr", "Lepa Rada (Basar)", "Arunachal Pradesh", listOf("Basar Town")),
            LocationDistrict("ar_shy", "Shi Yomi (Tato)", "Arunachal Pradesh", listOf("Mechuka", "Tato"))
        )),

        // 3. Assam (All 31+ Districts including Kamrup Metropolitan, Kamrup, Cachar, Dibrugarh, etc.)
        LocationState("AS", "Assam", false, listOf(
            LocationDistrict("as_kmm", "Kamrup Metropolitan", "Assam", listOf("Guwahati - GS Road", "Guwahati - Paltan Bazaar", "Guwahati - Panbazar", "Guwahati - Zoo Road", "Guwahati - Dispur", "Guwahati - Jalukbari", "Guwahati - Beltola", "Guwahati - Chandmari", "Guwahati - Six Mile", "Guwahati - Bharalumukh")),
            LocationDistrict("as_kam", "Kamrup", "Assam", listOf("Amingaon", "Rangia", "Baihata Chariali", "Mirza", "Palasbari", "Chaygaon", "Boko")),
            LocationDistrict("as_dib", "Dibrugarh", "Assam", listOf("Thana Chariali", "HS Road", "Chowkidinghee", "Graham Bazar", "Amolapatty", "Moranhat", "Naharkatia")),
            LocationDistrict("as_jor", "Jorhat", "Assam", listOf("Gar-Ali", "AT Road", "Jail Road", "Tarajan", "Nimati Ghat", "Titabar", "Mariani")),
            LocationDistrict("as_cac", "Cachar (Silchar)", "Assam", listOf("Tarapur", "Janiganj", "Rangirkhari", "Link Road", "Sadarghat", "Sonai", "Lakhipur")),
            LocationDistrict("as_tin", "Tinsukia", "Assam", listOf("GNB Road", "Daily Bazar", "Makum", "Digboi", "Doomdooma", "Margherita")),
            LocationDistrict("as_siv", "Sivasagar", "Assam", listOf("Boarding Road", "Temple Road", "Nazira", "Amguri", "Demow")),
            LocationDistrict("as_son", "Sonitpur (Tezpur)", "Assam", listOf("Mission Chariali", "Tribeni Complex", "Main Bazar", "Dhekiajuli", "Jamugurihat")),
            LocationDistrict("as_nag", "Nagaon", "Assam", listOf("Haibargaon", "AT Road", "Fouzdaripatty", "Koliabor", "Raha")),
            LocationDistrict("as_bar", "Barpeta", "Assam", listOf("Barpeta Town", "Howly", "Sarthebari", "Pathsala", "Kalgachia")),
            LocationDistrict("as_bon", "Bongaigaon", "Assam", listOf("Chapaguri", "Station Road", "New Bongaigaon", "Abhayapuri", "Bijni")),
            LocationDistrict("as_gol", "Golaghat", "Assam", listOf("Bokakhat (Kaziranga)", "Main Market", "Dergaon", "Sarupathar")),
            LocationDistrict("as_dhu", "Dhubri", "Assam", listOf("Bilasipara", "Gauripur", "Sapatgram", "Dhubri Town")),
            LocationDistrict("as_goa", "Goalpara", "Assam", listOf("Goalpara Town", "Dudhnoi", "Lakhipur", "Matia")),
            LocationDistrict("as_kar", "Karimganj", "Assam", listOf("Station Road", "Main Market", "Badarpur", "Ramkrishna Nagar")),
            LocationDistrict("as_hai", "Hailakandi", "Assam", listOf("Hailakandi Town", "Lala", "Algapur")),
            LocationDistrict("as_kok", "Kokrajhar", "Assam", listOf("JD Road", "Bhowraguri", "Gossaigaon")),
            LocationDistrict("as_bak", "Baksa", "Assam", listOf("Mushalpur", "Tamulpur", "Barama", "Simla")),
            LocationDistrict("as_chi", "Chirang", "Assam", listOf("Kajalgaon", "Basugaon", "Runikhata")),
            LocationDistrict("as_uda", "Udalguri", "Assam", listOf("Udalguri Town", "Tangla", "Rowta", "Mazbat")),
            LocationDistrict("as_lak", "Lakhimpur", "Assam", listOf("North Lakhimpur", "Bihpuria", "Narayanpur", "Dhakuakhana")),
            LocationDistrict("as_dhe", "Dhemaji", "Assam", listOf("Dhemaji Town", "Silapathar", "Jonai")),
            LocationDistrict("as_dar", "Darrang (Mangaldai)", "Assam", listOf("Mangaldai Town", "Kharupetia", "Sipajhar")),
            LocationDistrict("as_mor", "Morigaon", "Assam", listOf("Morigaon Town", "Jagiroad", "Mayong")),
            LocationDistrict("as_nal", "Nalbari", "Assam", listOf("Nalbari Town", "Tihu", "Belsor")),
            LocationDistrict("as_bis", "Biswanath", "Assam", listOf("Biswanath Chariali", "Gohpur", "Helem")),
            LocationDistrict("as_cha", "Charaideo", "Assam", listOf("Sonari", "Moran", "Sapekhati")),
            LocationDistrict("as_hoj", "Hojai", "Assam", listOf("Hojai Town", "Lanka", "Doboka")),
            LocationDistrict("as_maj", "Majuli", "Assam", listOf("Garamur", "Kamalabari", "Jengraimukh")),
            LocationDistrict("as_kan", "Karbi Anglong (Diphu)", "Assam", listOf("Diphu Town", "Bokajan", "Howraghat")),
            LocationDistrict("as_wka", "West Karbi Anglong (Hamren)", "Assam", listOf("Hamren Town", "Baithalangso")),
            LocationDistrict("as_dha", "Dima Hasao (Haflong)", "Assam", listOf("Haflong Town", "Umrangso", "Mahur")),
            LocationDistrict("as_ssm", "South Salmara-Mankachar", "Assam", listOf("Hatsingimari", "Mankachar"))
        )),

        // 4. Bihar
        LocationState("BR", "Bihar", false, listOf(
            LocationDistrict("br_pat", "Patna", "Bihar", listOf("Boring Road", "Kankarbagh", "Fraser Road", "Bailey Road", "Ashok Rajpath", "Danapur", "Rajendra Nagar", "Patliputra")),
            LocationDistrict("br_gay", "Gaya", "Bihar", listOf("Bodh Gaya", "Civil Lines", "GB Road", "AP Colony")),
            LocationDistrict("br_muz", "Muzaffarpur", "Bihar", listOf("Motijheel", "Aghoria Bazar", "Mithanpura", "Brahmpura")),
            LocationDistrict("br_bha", "Bhagalpur", "Bihar", listOf("Station Road", "Tilkamanjhi", "Khalifabag", "Aliganj")),
            LocationDistrict("br_dar", "Darbhanga", "Bihar", listOf("Laheriasarai", "Tower Chowk", "Benta")),
            LocationDistrict("br_pur", "Purnia", "Bihar", listOf("Bhatta Bazar", "Line Bazar", "Gulabbagh")),
            LocationDistrict("br_beg", "Begusarai", "Bihar", listOf("Main Market", "Har-har Mahadev Chowk", "Barauni")),
            LocationDistrict("br_ara", "Bhojpur (Arrah)", "Bihar", listOf("Gopali Chowk", "Nawada", "Station Road")),
            LocationDistrict("br_vai", "Vaishali (Hajipur)", "Bihar", listOf("Anjaanpir Chowk", "Dakbangla Road", "Cinemal Road")),
            LocationDistrict("br_nal", "Nalanda (Bihar Sharif)", "Bihar", listOf("Ranchi Road", "Hospital Mor", "Rajgir")),
            LocationDistrict("br_cha", "East Champaran (Motihari)", "Bihar", listOf("Chhatauni", "Main Bazar", "Raxaul")),
            LocationDistrict("br_wch", "West Champaran (Bettiah)", "Bihar", listOf("Lal Bazar", "Station Road", "Narkatiaganj")),
            LocationDistrict("br_sam", "Samastipur", "Bihar", listOf("Station Road", "Mohanpur", "Kashipur")),
            LocationDistrict("br_sar", "Saran (Chhapra)", "Bihar", listOf("Municipality Chowk", "Hathua Market", "Garkha")),
            LocationDistrict("br_siw", "Siwan", "Bihar", listOf("Babunia Mor", "Hospital Road", "Mairwa")),
            LocationDistrict("br_gop", "Gopalganj", "Bihar", listOf("Ghosh Mor", "Post Office Road")),
            LocationDistrict("br_kat", "Katihar", "Bihar", listOf("Mirchaibari", "MG Road")),
            LocationDistrict("br_sah", "Saharsa", "Bihar", listOf("DB Road", "Super Market")),
            LocationDistrict("br_roh", "Rohtas (Sasaram)", "Bihar", listOf("GT Road", "Dharamsala Road", "Dehri-on-Sone")),
            LocationDistrict("br_aur", "Aurangabad", "Bihar", listOf("MG Road", "Ramesh Chowk"))
        )),

        // 5. Chhattisgarh
        LocationState("CG", "Chhattisgarh", false, listOf(
            LocationDistrict("cg_rpr", "Raipur", "Chhattisgarh", listOf("Pandri", "Jaistambh Chowk", "Telibandha", "Shankar Nagar", "Samta Colony", "Naya Raipur")),
            LocationDistrict("cg_dur", "Durg - Bhilai", "Chhattisgarh", listOf("Civic Centre", "Sector 6", "Supela", "Power House", "Nehru Nagar", "Durg City")),
            LocationDistrict("cg_bsp", "Bilaspur", "Chhattisgarh", listOf("Vyapar Vihar", "Link Road", "Telipara", "Mangla")),
            LocationDistrict("cg_kor", "Korba", "Chhattisgarh", listOf("Transport Nagar", "TP Nagar", "Niharika", "Balco Nagar")),
            LocationDistrict("cg_raj", "Rajnandgaon", "Chhattisgarh", listOf("Ganj Line", "Cinema Line", "Kaurin Bhata")),
            LocationDistrict("cg_rgh", "Raigarh", "Chhattisgarh", listOf("Station Road", "Chowk Bazar", "Kirodimal Nagar")),
            LocationDistrict("cg_jgd", "Bastar (Jagdalpur)", "Chhattisgarh", listOf("Sanjay Market", "Dharampura", "Geedam Road")),
            LocationDistrict("cg_amb", "Surguja (Ambikapur)", "Chhattisgarh", listOf("Ghadi Chowk", "Gudri Bazar", "Banaras Road")),
            LocationDistrict("cg_dhm", "Dhamtari", "Chhattisgarh", listOf("Sihawa Road", "Ghari Chowk")),
            LocationDistrict("cg_mah", "Mahasamund", "Chhattisgarh", listOf("Station Road", "Main Market")),
            LocationDistrict("cg_jnj", "Janjgir-Champa", "Chhattisgarh", listOf("Kachehari Chowk", "Station Road")),
            LocationDistrict("cg_kwd", "Kabirdham (Kawardha)", "Chhattisgarh", listOf("Main Market", "Rajmahal Road"))
        )),

        // 6. Goa
        LocationState("GA", "Goa", false, listOf(
            LocationDistrict("ga_north", "North Goa", "Goa", listOf("Panaji", "Mapusa", "Candolim", "Calangute", "Porvorim", "Anjuna", "Vagator", "Bicholim", "Ponda")),
            LocationDistrict("ga_south", "South Goa", "Goa", listOf("Margao", "Vasco da Gama", "Colva", "Benaulim", "Curchorem", "Cavelossim", "Canacona"))
        )),

        // 7. Gujarat
        LocationState("GJ", "Gujarat", false, listOf(
            LocationDistrict("gj_ahd", "Ahmedabad", "Gujarat", listOf("Navrangpura", "Satellite", "Bodakdev", "Vastrapur", "Prahlad Nagar", "Maninagar", "CG Road", "SG Highway", "Bopal", "Ghatlodia")),
            LocationDistrict("gj_sur", "Surat", "Gujarat", listOf("Adajan", "Vesu", "Ghod Dod Road", "Athwalines", "Varachha", "Piplod", "Katargam", "Rander")),
            LocationDistrict("gj_vad", "Vadodara", "Gujarat", listOf("Alkapuri", "Fatehgunj", "Akota", "Manjalpur", "Gotri", "Karelibaug", "Sayajigunj")),
            LocationDistrict("gj_raj", "Rajkot", "Gujarat", listOf("Yagnik Road", "Kalawad Road", "150 Feet Ring Road", "Amin Marg", "Dhebar Road")),
            LocationDistrict("gj_gan", "Gandhinagar", "Gujarat", listOf("Sector 11", "Sector 21", "Infocity", "Kudasan", "Randesan", "Gift City")),
            LocationDistrict("gj_bha", "Bhavnagar", "Gujarat", listOf("Waghawadi Road", "Kalanala", "Ghogha Circle", "Subhashnagar")),
            LocationDistrict("gj_jam", "Jamnagar", "Gujarat", listOf("Limda Lane", "Patel Colony", "Digvijay Plot", "Bedeshwar")),
            LocationDistrict("gj_jun", "Junagadh", "Gujarat", listOf("Zanzarda Road", "Moti Baug", "Joshipura", "Station Road")),
            LocationDistrict("gj_kuc", "Kutch (Bhuj / Gandhidham)", "Gujarat", listOf("Bhuj Main", "Gandhidham Sector 1", "Anjar", "Mandvi")),
            LocationDistrict("gj_ana", "Anand", "Gujarat", listOf("Amul Dairy Road", "Vidyanagar", "AV Road")),
            LocationDistrict("gj_bhr", "Bharuch", "Gujarat", listOf("Station Road", "Zadeshwar Road", "Link Road", "Ankleshwar")),
            LocationDistrict("gj_nav", "Navsari", "Gujarat", listOf("Lunsikui", "Fuwara", "Station Road")),
            LocationDistrict("gj_val", "Valsad", "Gujarat", listOf("Tithal Road", "Dharampur Road", "Vapi GIDC"))
        )),

        // 8. Haryana
        LocationState("HR", "Haryana", false, listOf(
            LocationDistrict("hr_gur", "Gurugram (NCR)", "Haryana", listOf("DLF Phase 1-5", "Cyber City", "Golf Course Road", "Sohna Road", "Sector 14", "Sector 56", "Sector 29", "Palam Vihar", "MG Road")),
            LocationDistrict("hr_far", "Faridabad", "Haryana", listOf("NIT 1-5", "Sector 15", "Sector 16", "Green Field", "Neharpar / Greater Faridabad", "Ballabgarh")),
            LocationDistrict("hr_pan", "Panipat", "Haryana", listOf("Model Town", "Sanjay Chowk", "GT Road", "Assandh Road")),
            LocationDistrict("hr_amb", "Ambala", "Haryana", listOf("Ambala Cantt", "Ambala City", "Model Town", "Cloth Market")),
            LocationDistrict("hr_kar", "Karnal", "Haryana", listOf("Model Town", "Sector 13", "Kunjpura Road", "Mughal Canal")),
            LocationDistrict("hr_his", "Hisar", "Haryana", listOf("Urban Estate", "PLA", "Red Square Market", "Camp Chowk")),
            LocationDistrict("hr_roh", "Rohtak", "Haryana", listOf("Model Town", "Delhi Road", "D-Park", "Sector 14")),
            LocationDistrict("hr_son", "Sonipat", "Haryana", listOf("Model Town", "Murthal Road", "Sector 14", "Kundli")),
            LocationDistrict("hr_pan_k", "Panchkula", "Haryana", listOf("Sector 5", "Sector 7", "Sector 11", "Sector 20", "MDC")),
            LocationDistrict("hr_yam", "Yamunanagar", "Haryana", listOf("Model Town", "Jagadhri", "Station Road"))
        )),

        // 9. Himachal Pradesh
        LocationState("HP", "Himachal Pradesh", false, listOf(
            LocationDistrict("hp_sml", "Shimla", "Himachal Pradesh", listOf("Mall Road", "Lower Bazar", "Sanjauli", "Chotta Shimla", "Kasumpti", "Lakkar Bazar")),
            LocationDistrict("hp_kng", "Kangra (Dharamshala / McLeodGanj)", "Himachal Pradesh", listOf("Kotwali Bazar", "McLeodGanj", "Palampur", "Kangra Town", "Nurpur")),
            LocationDistrict("hp_kul", "Kullu - Manali", "Himachal Pradesh", listOf("Mall Road Manali", "Dhalpur Kullu", "Bhuntar", "Old Manali")),
            LocationDistrict("hp_sol", "Solan", "Himachal Pradesh", listOf("Mall Road", "Kalka Road", "Baddi", "Barotiwala")),
            LocationDistrict("hp_mnd", "Mandi", "Himachal Pradesh", listOf("Indira Market", "Seri Bazar", "Sunder Nagar")),
            LocationDistrict("hp_una", "Una", "Himachal Pradesh", listOf("Main Bazar", "Rotary Chowk", "Mehatpur")),
            LocationDistrict("hp_hmp", "Hamirpur", "Himachal Pradesh", listOf("Gandhi Chowk", "Anu", "Bhota")),
            LocationDistrict("hp_bls", "Bilaspur", "Himachal Pradesh", listOf("Main Market", "Ghumarwin")),
            LocationDistrict("hp_chm", "Chamba", "Himachal Pradesh", listOf("Chaugan Bazar", "Dalhousie", "Khajjiar")),
            LocationDistrict("hp_srm", "Sirmaur (Nahan)", "Himachal Pradesh", listOf("Gunnu Ghat", "Paonta Sahib")),
            LocationDistrict("hp_kin", "Kinnaur", "Himachal Pradesh", listOf("Reckong Peo", "Kalpa")),
            LocationDistrict("hp_lhs", "Lahaul and Spiti", "Himachal Pradesh", listOf("Keylong", "Kaza"))
        )),

        // 10. Jharkhand
        LocationState("JH", "Jharkhand", false, listOf(
            LocationDistrict("jh_ran", "Ranchi", "Jharkhand", listOf("Main Road", "Lalpur", "Doranda", "Hinoo", "Kanke Road", "Harmu", "Bariatu", "Ratu Road")),
            LocationDistrict("jh_jsr", "East Singhbhum (Jamshedpur)", "Jharkhand", listOf("Bistupur", "Sakchi", "Kadma", "Sonari", "Telco Colony", "Golmuri", "Mango")),
            LocationDistrict("jh_dhn", "Dhanbad", "Jharkhand", listOf("Bank More", "Hirapur", "Saraidhela", "Katras", "Jharia", "Govindpur")),
            LocationDistrict("jh_bok", "Bokaro", "Jharkhand", listOf("City Centre Sector 4", "Sector 1", "Chas", "Sector 9", "Co-operative Colony")),
            LocationDistrict("jh_hzb", "Hazaribagh", "Jharkhand", listOf("Korrah", "Malviya Marg", "Bada Bazar")),
            LocationDistrict("jh_dgr", "Deoghar", "Jharkhand", listOf("Tower Chowk", "Baidyanath Dham", "Castairs Town")),
            LocationDistrict("jh_grd", "Giridih", "Jharkhand", listOf("Bada Chowk", "Makatpur", "Mahuatand")),
            LocationDistrict("jh_rmg", "Ramgarh", "Jharkhand", listOf("Main Road", "Subhash Chowk", "Gola")),
            LocationDistrict("jh_dum", "Dumka", "Jharkhand", listOf("Tin Bazar", "Dudhani", "Bada Bandh")),
            LocationDistrict("jh_pal", "Palamu (Daltonganj)", "Jharkhand", listOf("Sixer Chowk", "Katchery Road", "Sahitya Samaj Chowk"))
        )),

        // 11. Karnataka
        LocationState("KA", "Karnataka", false, listOf(
            LocationDistrict("ka_blr_u", "Bengaluru Urban", "Karnataka", listOf("Indiranagar", "Koramangala", "HSR Layout", "Whitefield", "Jayanagar", "JP Nagar", "Marathahalli", "Electronic City", "BTM Layout", "Malleshwaram", "Hebbal", "Yelahanka", "Bellandur", "Banashankari")),
            LocationDistrict("ka_blr_r", "Bengaluru Rural", "Karnataka", listOf("Doddaballapura", "Devanahalli", "Hosakote", "Nelamangala")),
            LocationDistrict("ka_mys", "Mysuru", "Karnataka", listOf("Gokulam", "Jayalakshmipuram", "Kuvempunagar", "Saraswathipuram", "Vijayanagar", "Hebbal Industrial Area", "Devaraja Market")),
            LocationDistrict("ka_mng", "Dakshina Kannada (Mangaluru)", "Karnataka", listOf("Kadri", "Kodialbail", "Bejai", "Hampankatta", "Surathkal", "Lalbagh", "Balmatta")),
            LocationDistrict("ka_hub", "Dharwad (Hubballi-Dharwad)", "Karnataka", listOf("Vidyanagar", "Keshwapur", "Gokul Road", "Navanagar", "Line Bazaar", "Koppikar Road")),
            LocationDistrict("ka_bel", "Belagavi", "Karnataka", listOf("Camp", "Tilakwadi", "Khanapur Road", "Shahapur", "Hindwadi")),
            LocationDistrict("ka_klb", "Kalaburagi (Gulbarga)", "Karnataka", listOf("Super Market", "Aiwan-e-Shahi", "Sedam Road")),
            LocationDistrict("ka_shv", "Shivamogga", "Karnataka", listOf("Nehru Road", "Gandhi Bazar", "Gopala", "Vinoba Nagar")),
            LocationDistrict("ka_tum", "Tumakuru", "Karnataka", listOf("MG Road", "SS Puram", "Ashoka Road", "Batawadi")),
            LocationDistrict("ka_dav", "Davanagere", "Karnataka", listOf("PJ Extension", "MCC A/B Block", "Mandipet")),
            LocationDistrict("ka_udp", "Udupi", "Karnataka", listOf("Manipal", "Car Street", "Kalsanka", "Malpe")),
            LocationDistrict("ka_bal", "Ballari", "Karnataka", listOf("Car Street", "Gandhinagar", "Cantonment")),
            LocationDistrict("ka_has", "Hassan", "Karnataka", listOf("MG Road", "Vidyanagar", "Salagame Road")),
            LocationDistrict("ka_ckm", "Chikkamagaluru", "Karnataka", listOf("MG Road", "Indira Gandhi Road", "Kalyan Nagar")),
            LocationDistrict("ka_kdg", "Kodagu (Coorg - Madikeri)", "Karnataka", listOf("College Road", "General Thimmaiah Circle", "Kushalnagar", "Virajpet"))
        )),

        // 12. Kerala
        LocationState("KL", "Kerala", false, listOf(
            LocationDistrict("kl_ekm", "Ernakulam (Kochi)", "Kerala", listOf("Kaloor", "Marine Drive", "Panampilly Nagar", "Edappally", "Kakkanad InfoPark", "Fort Kochi", "Palarivattom", "Vyttila", "Aluva", "MG Road")),
            LocationDistrict("kl_tvm", "Thiruvananthapuram", "Kerala", listOf("Kowdiar", "Vellayambalam", "Pattom", "Kazhakkoottam (Technopark)", "Palayam", "Sasthamangalam", "East Fort", "Thampanoor")),
            LocationDistrict("kl_clt", "Kozhikode (Calicut)", "Kerala", listOf("Mavoor Road", "SM Street", "Palayam", "Thondayad", "Kottuli", "Beach Road", "Nadakkavu")),
            LocationDistrict("kl_tcr", "Thrissur", "Kerala", listOf("Round North/South/East/West", "MG Road", "Ayyanthole", "Swaraj Round")),
            LocationDistrict("kl_knr", "Kannur", "Kerala", listOf("Fort Road", "Thana", "Payyambalam", "Talap", "Payyanur")),
            LocationDistrict("kl_ktm", "Kottayam", "Kerala", listOf("Collectorate", "Nagampadam", "Baker Junction", "Kanjikuzhy", "Pala")),
            LocationDistrict("kl_alp", "Alappuzha", "Kerala", listOf("Mullakkal", "Boat Jetty", "Civil Station", "Cherthala")),
            LocationDistrict("kl_plk", "Palakkad", "Kerala", listOf("TB Road", "Stadium Bypass", "Fort Maidan", "Ottapalam")),
            LocationDistrict("kl_mlp", "Malappuram", "Kerala", listOf("Up Hill", "Down Hill", "Manjeri", "Perinthalmanna", "Tirur")),
            LocationDistrict("kl_klm", "Kollam", "Kerala", listOf("Chinnakada", "Asramam", "Kadappakada", "Beach Road")),
            LocationDistrict("kl_ptm", "Pathanamthitta", "Kerala", listOf("Ring Road", "Thiruvalla", "Adoor", "Ranni")),
            LocationDistrict("kl_idk", "Idukki", "Kerala", listOf("Thodupuzha", "Munnar", "Kattappana", "Nedumkandam")),
            LocationDistrict("kl_wyn", "Wayanad", "Kerala", listOf("Kalpetta", "Sulthan Bathery", "Mananthavady")),
            LocationDistrict("kl_ksr", "Kasaragod", "Kerala", listOf("MG Road", "Kanhangad", "Uppala"))
        )),

        // 13. Madhya Pradesh
        LocationState("MP", "Madhya Pradesh", false, listOf(
            LocationDistrict("mp_ind", "Indore", "Madhya Pradesh", listOf("Vijay Nagar", "Palasia", "MG Road", "Rajwada", "Bhawarkua", "Chappan Dukan", "Sarafa", "Super Corridor", "Annapurna")),
            LocationDistrict("mp_bho", "Bhopal", "Madhya Pradesh", listOf("MP Nagar", "Arera Colony", "New Market", "Kolar Road", "Hoshangabad Road", "TT Nagar", "Shahpura", "Bairagarh")),
            LocationDistrict("mp_jbp", "Jabalpur", "Madhya Pradesh", listOf("Civil Lines", "Wright Town", "Gorakhpur", "Napier Town", "Vijay Nagar", "Sadar")),
            LocationDistrict("mp_gwl", "Gwalior", "Madhya Pradesh", listOf("City Centre", "Lashkar", "Morar", "Phoolbagh", "Maharaj Bada")),
            LocationDistrict("mp_ujj", "Ujjain", "Madhya Pradesh", listOf("Freeganj", "Mahakal Marg", "Tower Chowk", "Nanaji Deshmukh Marg")),
            LocationDistrict("mp_sgr", "Sagar", "Madhya Pradesh", listOf("Katra Bazar", "Civil Lines", "Makronia")),
            LocationDistrict("mp_rtm", "Ratlam", "Madhya Pradesh", listOf("Do Batti", "Station Road", "Chandni Chowk")),
            LocationDistrict("mp_sat", "Satna", "Madhya Pradesh", listOf("Rewa Road", "Panna Naka", "Station Road")),
            LocationDistrict("mp_rew", "Rewa", "Madhya Pradesh", listOf("Venkat Road", "Civil Lines", "Kothi Compound")),
            LocationDistrict("mp_kat", "Katni", "Madhya Pradesh", listOf("Mission Chowk", "Gole Bazar", "Subhash Chowk"))
        )),

        // 14. Maharashtra (All 36 Districts)
        LocationState("MH", "Maharashtra", false, listOf(
            LocationDistrict("mh_mum_sub", "Mumbai Suburban", "Maharashtra", listOf("Bandra West", "Bandra East", "Andheri East", "Andheri West", "Juhu", "Borivali", "Kandivali", "Malad", "Goregaon", "Santacruz", "Powai", "Kurla", "Ghatkopar", "Mulund", "Vikhroli")),
            LocationDistrict("mh_mum_city", "Mumbai City", "Maharashtra", listOf("Colaba", "Marine Lines", "Dadar", "Worli", "Parel", "Byculla", "Lower Parel", "Fort", "Nariman Point", "Mahim")),
            LocationDistrict("mh_pune", "Pune", "Maharashtra", listOf("Kothrud", "Koregaon Park", "Hinjewadi", "Viman Nagar", "Baner", "Wakad", "Aundh", "Shivajinagar", "Hadapsar", "Kalyani Nagar", "Pimpri-Chinchwad")),
            LocationDistrict("mh_thane", "Thane", "Maharashtra", listOf("Thane West", "Kalyan", "Dombivli", "Navi Mumbai - Vashi", "Navi Mumbai - Nerul", "Navi Mumbai - Kharghar", "Mira Road", "Bhayandar", "Ulhasnagar")),
            LocationDistrict("mh_nagpur", "Nagpur", "Maharashtra", listOf("Dharampeth", "Sadar", "Sitabuldi", "Manish Nagar", "Pratap Nagar", "Civil Lines", "Ramdaspeth")),
            LocationDistrict("mh_nashik", "Nashik", "Maharashtra", listOf("College Road", "Gangapur Road", "Indira Nagar", "Panchavati", "Nashik Road", "Cidco")),
            LocationDistrict("mh_csn", "Chhatrapati Sambhajinagar (Aurangabad)", "Maharashtra", listOf("CIDCO", "Cannaught Place", "Samarth Nagar", "Garkheda", "Jalna Road", "Kranti Chowk")),
            LocationDistrict("mh_kol", "Kolhapur", "Maharashtra", listOf("Rajarampuri", "Tarabai Park", "Shahupuri", "Laxmipuri", "Mahadwar Road")),
            LocationDistrict("mh_sol", "Solapur", "Maharashtra", listOf("Saat Rasta", "Navi Peth", "Lashkar", "Jule Solapur")),
            LocationDistrict("mh_amr", "Amravati", "Maharashtra", listOf("Rajapeth", "Badnera Road", "Camp", "Gadge Nagar")),
            LocationDistrict("mh_jal", "Jalgaon", "Maharashtra", listOf("Court Chowk", "Navi Peth", "MIDC", "Ring Road")),
            LocationDistrict("mh_nan", "Nanded", "Maharashtra", listOf("Station Road", "Vazirabad", "Sneha Nagar")),
            LocationDistrict("mh_san", "Sangli", "Maharashtra", listOf("Vishrambag", "Haripur Road", "Miraj", "High Street")),
            LocationDistrict("mh_sat", "Satara", "Maharashtra", listOf("Rajwada", "Powai Naka", "Sadar Bazar", "Karad")),
            LocationDistrict("mh_ahm", "Ahilyanagar (Ahmednagar)", "Maharashtra", listOf("Savedi", "Market Yard", "Station Road")),
            LocationDistrict("mh_chd", "Chandrapur", "Maharashtra", listOf("Ganj Ward", "Ramnagar", "Ballarpur Road")),
            LocationDistrict("mh_par", "Parbhani", "Maharashtra", listOf("Station Road", "Gandhi Park")),
            LocationDistrict("mh_lat", "Latur", "Maharashtra", listOf("Ausa Road", "Gandhi Chowk", "MIDC")),
            LocationDistrict("mh_dha", "Dharashiv (Osmanabad)", "Maharashtra", listOf("Nehru Chowk", "Samarth Nagar")),
            LocationDistrict("mh_bee", "Beed", "Maharashtra", listOf("Jalna Road", "Subhash Road")),
            LocationDistrict("mh_jln", "Jalna", "Maharashtra", listOf("Old Jalna", "Devalgaon Raja Road")),
            LocationDistrict("mh_bul", "Buldhana", "Maharashtra", listOf("Khamgaon", "Malkapur", "Shegaon")),
            LocationDistrict("mh_ako", "Akola", "Maharashtra", listOf("Gorakshan Road", "Civil Lines", "Old City")),
            LocationDistrict("mh_was", "Washim", "Maharashtra", listOf("Civil Lines", "Risod Road")),
            LocationDistrict("mh_hin", "Hingoli", "Maharashtra", listOf("Akola Road", "Nanded Naka")),
            LocationDistrict("mh_yav", "Yavatmal", "Maharashtra", listOf("Darda Nagar", "Station Road", "Arni Road")),
            LocationDistrict("mh_war", "Wardha", "Maharashtra", listOf("Sevagram Road", "Bachelor Road")),
            LocationDistrict("mh_bhn", "Bhandara", "Maharashtra", listOf("Main Market", "Tumsar Road")),
            LocationDistrict("mh_gon", "Gondia", "Maharashtra", listOf("Rail Toly", "Civil Lines", "Kudwa")),
            LocationDistrict("mh_gad", "Gadchiroli", "Maharashtra", listOf("Complex Area", "Chamorshi Road")),
            LocationDistrict("mh_dhu", "Dhule", "Maharashtra", listOf("Agra Road", "Deopur", "Lane 4")),
            LocationDistrict("mh_ndb", "Nandurbar", "Maharashtra", listOf("Station Road", "Market Yard")),
            LocationDistrict("mh_rai", "Raigad", "Maharashtra", listOf("Alibaug", "Panvel", "Khopoli", "Mahad")),
            LocationDistrict("mh_rtg", "Ratnagiri", "Maharashtra", listOf("Maruti Mandir", "Kuwarbav", "Chiplun")),
            LocationDistrict("mh_sdg", "Sindhudurg", "Maharashtra", listOf("Kudal", "Kankavli", "Sawantwadi", "Malvan")),
            LocationDistrict("mh_pal", "Palghar", "Maharashtra", listOf("Vasai", "Virar", "Nalasopara", "Dahanu", "Boisar"))
        )),

        // 15. Manipur
        LocationState("MN", "Manipur", false, listOf(
            LocationDistrict("mn_ime", "Imphal East", "Manipur", listOf("Porompat", "Lamlai", "Sawombung")),
            LocationDistrict("mn_imw", "Imphal West", "Manipur", listOf("Thangal Bazar", "Paona Bazar", "Kwakeithel", "Lamphelpat", "Uripok")),
            LocationDistrict("mn_ccp", "Churachandpur", "Manipur", listOf("Tuibong", "Tedim Road", "Rengkai")),
            LocationDistrict("mn_thb", "Thoubal", "Manipur", listOf("Thoubal Bazar", "Kakching Road")),
            LocationDistrict("mn_bsh", "Bishnupur", "Manipur", listOf("Moirang", "Nambol", "Bishnupur Bazar")),
            LocationDistrict("mn_sen", "Senapati", "Manipur", listOf("Senapati Bazar", "Maram")),
            LocationDistrict("mn_ukh", "Ukhrul", "Manipur", listOf("Viewland", "Phungreitang", "Wino Bazar")),
            LocationDistrict("mn_kpk", "Kangpokpi", "Manipur", listOf("Kangpokpi Bazar", "Motbung")),
            LocationDistrict("mn_chl", "Chandel", "Manipur", listOf("Chandel Town", "Moreh Border Town")),
            LocationDistrict("mn_kch", "Kakching", "Manipur", listOf("Kakching Bazar", "Pallel")),
            LocationDistrict("mn_jir", "Jiribam", "Manipur", listOf("Jiribam Bazar", "Babupara")),
            LocationDistrict("mn_tam", "Tamenglong", "Manipur", listOf("Tamenglong Town"))
        )),

        // 16. Meghalaya (All 12 Districts including East Khasi Hills, Shillong, etc.)
        LocationState("ML", "Meghalaya", false, listOf(
            LocationDistrict("ml_ekh", "East Khasi Hills (Shillong)", "Meghalaya", listOf("Police Bazar (PB)", "Laitumkhrah", "Laban", "Mawlai", "Risa Colony", "Nongthymmai", "Polo Grounds", "Mawprem", "Dhankheti", "Upper Shillong")),
            LocationDistrict("ml_wkh", "West Khasi Hills (Nongstoin)", "Meghalaya", listOf("Nongstoin Town", "Mairang Road", "New Market")),
            LocationDistrict("ml_ewk", "Eastern West Khasi Hills (Mairang)", "Meghalaya", listOf("Mairang Bazar", "Pyndengumiong")),
            LocationDistrict("ml_swk", "South West Khasi Hills (Mawkyrwat)", "Meghalaya", listOf("Mawkyrwat Town", "Ranikor")),
            LocationDistrict("ml_rbh", "Ri Bhoi (Nongpoh)", "Meghalaya", listOf("Nongpoh Bazar", "Byrnihat", "Umiam (Barapani)", "Khanapara Border")),
            LocationDistrict("ml_wjh", "West Jaintia Hills (Jowai)", "Meghalaya", listOf("Iawmusiang Market", "Ladmukhla", "Chutwakhu", "Mynthong")),
            LocationDistrict("ml_ejh", "East Jaintia Hills (Khliehriat)", "Meghalaya", listOf("Khliehriat Bazar", "Ladrymbai")),
            LocationDistrict("ml_wgh", "West Garo Hills (Tura)", "Meghalaya", listOf("Tura Super Market", "Hawakhana", "Arapetta", "Dobasipara", "Rongram")),
            LocationDistrict("ml_egh", "East Garo Hills (Williamnagar)", "Meghalaya", listOf("Williamnagar Main Market", "Kusimkol")),
            LocationDistrict("ml_sgh", "South Garo Hills (Baghmara)", "Meghalaya", listOf("Baghmara Bazar", "Rongara")),
            LocationDistrict("ml_ngh", "North Garo Hills (Resubelpara)", "Meghalaya", listOf("Resubelpara Town", "Mendipathar")),
            LocationDistrict("ml_swg", "South West Garo Hills (Ampati)", "Meghalaya", listOf("Ampati Bazar", "Mahendraganj"))
        )),

        // 17. Mizoram
        LocationState("MZ", "Mizoram", false, listOf(
            LocationDistrict("mz_azl", "Aizawl", "Mizoram", listOf("Bara Bazar", "Zarkawt", "Khatla", "Chanmari", "Dawrpui", "Mission Veng", "Bawngkawn")),
            LocationDistrict("mz_lul", "Lunglei", "Mizoram", listOf("Venglai", "Chanmari Lunglei", "Bazar Veng")),
            LocationDistrict("mz_cmp", "Champhai", "Mizoram", listOf("Vengthlang", "Kahrawt", "Zokhawthar Border")),
            LocationDistrict("mz_kol", "Kolasib", "Mizoram", listOf("Vengthar", "Diakkawn", "Bairabi")),
            LocationDistrict("mz_ser", "Serchhip", "Mizoram", listOf("Bazar Veng", "New Serchhip")),
            LocationDistrict("mz_law", "Lawngtlai", "Mizoram", listOf("Areopagitica", "Bazar Veng")),
            LocationDistrict("mz_sia", "Siaha", "Mizoram", listOf("Meisatla", "New Siaha")),
            LocationDistrict("mz_mam", "Mamit", "Mizoram", listOf("Mamit Bazar", "Kanhmun"))
        )),

        // 18. Nagaland
        LocationState("NL", "Nagaland", false, listOf(
            LocationDistrict("nl_koh", "Kohima", "Nagaland", listOf("Main Town", "PR Hill", "Razhoo Point", "High School Junction", "Kezieke", "Midland")),
            LocationDistrict("nl_dmp", "Dimapur", "Nagaland", listOf("Hong Kong Market", "Nyamo Lotha Road", "Circular Road", "Purana Bazar", "Walford", "Duncan Bosti")),
            LocationDistrict("nl_chk", "Chumoukedima", "Nagaland", listOf("7th Mile", "Chumoukedima Town", "Medziphema")),
            LocationDistrict("nl_mok", "Mokokchung", "Nagaland", listOf("Main Police Point", "Arkong Ward", "Tongdentsuyong")),
            LocationDistrict("nl_wok", "Wokha", "Nagaland", listOf("Tsumang Colony", "Main Market")),
            LocationDistrict("nl_tue", "Tuensang", "Nagaland", listOf("Tower Clock", "High School Sector")),
            LocationDistrict("nl_mon", "Mon", "Nagaland", listOf("Main Town", "Helipad Area")),
            LocationDistrict("nl_znb", "Zunheboto", "Nagaland", listOf("Project Colony", "Old Town")),
            LocationDistrict("nl_phk", "Phek", "Nagaland", listOf("Phek Town", "Pfütsero")),
            LocationDistrict("nl_prn", "Peren", "Nagaland", listOf("Jalukie", "Peren Town"))
        )),

        // 19. Odisha
        LocationState("OD", "Odisha", false, listOf(
            LocationDistrict("od_bhu", "Khordha (Bhubaneswar)", "Odisha", listOf("Saheed Nagar", "Nayapalli", "Jayadev Vihar", "Patia", "Khandagiri", "Chandrasekharpur", "Master Canteen", "Old Town")),
            LocationDistrict("od_ctc", "Cuttack", "Odisha", listOf("Badambadi", "College Square", "Choudhury Bazar", "CDA Sector", "Buxi Bazar", "Mangalabag")),
            LocationDistrict("od_rkl", "Sundargarh (Rourkela)", "Odisha", listOf("Civil Township", "Sector 19", "Uditnagar", "Panposh", "Chhend Colony")),
            LocationDistrict("od_ber", "Ganjam (Berhampur)", "Odisha", listOf("Giri Market", "Bada Bazar", "Kamapalli", "Courtpeta")),
            LocationDistrict("od_smp", "Sambalpur", "Odisha", listOf("Golbazar", "Budharaja", "Ainthapali", "Dhanupali", "Burla")),
            LocationDistrict("od_pur", "Puri", "Odisha", listOf("Grand Road (Bada Danda)", "VIP Road", "Sea Beach", "Balagandi")),
            LocationDistrict("od_bls", "Balasore", "Odisha", listOf("OT Road", "Fakir Mohan Golei", "Motiganj")),
            LocationDistrict("od_bhd", "Bhadrak", "Odisha", listOf("Kacheri Bazar", "Charampa", "Bypass")),
            LocationDistrict("od_ang", "Angul", "Odisha", listOf("Amalapada", "Hakimpada", "Nalco Nagar")),
            LocationDistrict("od_jaj", "Jajpur", "Odisha", listOf("Jajpur Road (Vyasanagar)", "Jajpur Town", "Kalinganagar"))
        )),

        // 20. Punjab
        LocationState("PB", "Punjab", false, listOf(
            LocationDistrict("pb_ldh", "Ludhiana", "Punjab", listOf("Model Town", "Sarabha Nagar", "BRS Nagar", "Civil Lines", "Ferozepur Road", "Ghumar Mandi", "Dugri", "Gill Road")),
            LocationDistrict("pb_asr", "Amritsar", "Punjab", listOf("Ranjit Avenue", "Mall Road", "Lawrence Road", "Majitha Road", "Hall Gate", "Katra Jaimal Singh", "GT Road")),
            LocationDistrict("pb_mhl", "SAS Nagar (Mohali - NCR)", "Punjab", listOf("Phase 3B2", "Phase 7", "Sector 70", "Sector 82", "Kharar", "Zirakpur", "Aerocity")),
            LocationDistrict("pb_jln", "Jalandhar", "Punjab", listOf("Model Town", "Civil Lines", "Lajpat Nagar", "Cantt Road", "Rama Mandi", "BMC Chowk")),
            LocationDistrict("pb_ptl", "Patiala", "Punjab", listOf("Leela Bhawan", "Urban Estate Phase 1/2", "Chotti Baradari", "Tripuri", "Mall Road")),
            LocationDistrict("pb_bth", "Bathinda", "Punjab", listOf("Model Town", "Mall Road", "Civil Lines", "Goniana Road")),
            LocationDistrict("pb_hsp", "Hoshiarpur", "Punjab", listOf("Mall Road", "Model Town", "Prabhat Chowk")),
            LocationDistrict("pb_pth", "Pathankot", "Punjab", listOf("Mission Road", "Dhangu Road", "Gandhi Chowk")),
            LocationDistrict("pb_mga", "Moga", "Punjab", listOf("GT Road", "Main Bazar", "Baghapurana Road"))
        )),

        // 21. Rajasthan
        LocationState("RJ", "Rajasthan", false, listOf(
            LocationDistrict("rj_jpr", "Jaipur", "Rajasthan", listOf("Malviya Nagar", "Vaishali Nagar", "C-Scheme", "Mansarovar", "Raja Park", "Tonk Road", "MI Road", "Jagatpura", "Vidhyadhar Nagar", "Johari Bazar")),
            LocationDistrict("rj_jdh", "Jodhpur", "Rajasthan", listOf("Shastri Nagar", "Ratanada", "Sardarpura", "Pal Road", "Paota", "Chopasni Housing Board")),
            LocationDistrict("rj_udp", "Udaipur", "Rajasthan", listOf("Panchwati", "Fatehpura", "Hiran Magri", "Saheli Nagar", "Bapu Bazar", "Shobhagpura")),
            LocationDistrict("rj_kta", "Kota", "Rajasthan", listOf("Talwandi", "Vigyan Nagar", "Gumanpura", "Indra Vihar", "Rajeev Gandhi Nagar", "Kunhari")),
            LocationDistrict("rj_ajm", "Ajmer", "Rajasthan", listOf("Vaishali Nagar", "Civil Lines", "Kutchery Road", "Panchsheel Nagar", "Pushkar")),
            LocationDistrict("rj_bkn", "Bikaner", "Rajasthan", listOf("Rani Bazar", "Kote Gate", "JNV Colony", "Pawan Puri")),
            LocationDistrict("rj_alw", "Alwar", "Rajasthan", listOf("Scheme 1/2", "Moti Doongri", "Bhagat Singh Circle", "Bhiwadi")),
            LocationDistrict("rj_bhl", "Bhilwara", "Rajasthan", listOf("Bhopal Ganj", "Subhash Nagar", "Pur Road")),
            LocationDistrict("rj_bht", "Bharatpur", "Rajasthan", listOf("Kumher Gate", "Anah Gate", "Circular Road")),
            LocationDistrict("rj_skr", "Sikar", "Rajasthan", listOf("Bajaj Road", "Fatehpuri Gate", "Palsana Road"))
        )),

        // 22. Sikkim
        LocationState("SK", "Sikkim", false, listOf(
            LocationDistrict("sk_gtk", "Gangtok", "Sikkim", listOf("MG Marg", "Deorali", "Tadong", "Burtuk", "Development Area", "Ranipool")),
            LocationDistrict("sk_nam", "Namchi", "Sikkim", listOf("Central Park", "Assangthang", "Bhanjyang")),
            LocationDistrict("sk_gyl", "Gyalshing (West Sikkim)", "Sikkim", listOf("Pelling", "Gyalshing Bazar", "Dentam")),
            LocationDistrict("sk_mng", "Mangan (North Sikkim)", "Sikkim", listOf("Mangan Town", "Chungthang", "Lachung")),
            LocationDistrict("sk_pky", "Pakyong", "Sikkim", listOf("Airport Road", "Pakyong Bazar", "Rhenock")),
            LocationDistrict("sk_srg", "Soreng", "Sikkim", listOf("Soreng Bazar", "Nayabazar"))
        )),

        // 23. Tamil Nadu
        LocationState("TN", "Tamil Nadu", false, listOf(
            LocationDistrict("tn_chn", "Chennai", "Tamil Nadu", listOf("T. Nagar", "Anna Nagar", "Adyar", "Besant Nagar", "Velachery", "Mylapore", "Nungambakkam", "Thiruvanmiyur", "OMR IT Corridor", "Kilpauk", "Tambaram", "Porur", "Guindy", "Alwarpet")),
            LocationDistrict("tn_cbe", "Coimbatore", "Tamil Nadu", listOf("RS Puram", "Gandhipuram", "Saibaba Colony", "Peelamedu", "Race Course", "Saravanampatti", "Ramanathapuram", "Vadavalli")),
            LocationDistrict("tn_mdu", "Madurai", "Tamil Nadu", listOf("KK Nagar", "Anna Nagar", "Simmakkal", "Tallakulam", "SS Colony", "Mattuthavani", "Goripalayam")),
            LocationDistrict("tn_trc", "Tiruchirappalli (Trichy)", "Tamil Nadu", listOf("Thillai Nagar", "Cantonment", "K.K. Nagar", "Srirangam", "Main Guard Gate")),
            LocationDistrict("tn_slm", "Salem", "Tamil Nadu", listOf("Fairlands", "Hasthampatti", "Alagapuram", "Suramangalam")),
            LocationDistrict("tn_tpr", "Tiruppur", "Tamil Nadu", listOf("Kumaran Road", "Avinashi Road", "Dharapuram Road", "College Road")),
            LocationDistrict("tn_erd", "Erode", "Tamil Nadu", listOf("Perundurai Road", "Brough Road", "Sampath Nagar")),
            LocationDistrict("tn_vlr", "Vellore", "Tamil Nadu", listOf("Gandhi Nagar", "Katpadi", "Bagayam", "Sathuvachari")),
            LocationDistrict("tn_tin", "Tirunelveli", "Tamil Nadu", listOf("Palayamkottai", "Vannarpettai", "Junction")),
            LocationDistrict("tn_tht", "Thoothukudi (Tuticorin)", "Tamil Nadu", listOf("Palayamkottai Road", "WGC Road", "Millerpuram")),
            LocationDistrict("tn_knk", "Kanyakumari (Nagercoil)", "Tamil Nadu", listOf("Court Road", "Tower Junction", "Vadasery")),
            LocationDistrict("tn_thj", "Thanjavur", "Tamil Nadu", listOf("Medical College Road", "Old Bus Stand", "South Rampart")),
            LocationDistrict("tn_dnd", "Dindigul", "Tamil Nadu", listOf("Salai Road", "Round Road", "Palani Road")),
            LocationDistrict("tn_knp", "Kanchipuram", "Tamil Nadu", listOf("Gandhi Road", "Ennaikaran", "Orikkai")),
            LocationDistrict("tn_chg", "Chengalpattu", "Tamil Nadu", listOf("Mahabalipuram", "GST Road", "Maraimalai Nagar"))
        )),

        // 24. Telangana
        LocationState("TG", "Telangana", false, listOf(
            LocationDistrict("ts_hyd", "Hyderabad", "Telangana", listOf("Banjara Hills", "Jubilee Hills", "Gachibowli", "Hitec City", "Madhapur", "Kondapur", "Kukatpally", "Begumpet", "Ameerpet", "Charminar Area", "Secunderabad", "Dilsukhnagar", "Manikonda", "Uppal", "Miyapur")),
            LocationDistrict("ts_rgr", "Rangareddy", "Telangana", listOf("LB Nagar", "Attapur", "Rajendranagar", "Shamshabad (Airport)", "Gandi Maisamma")),
            LocationDistrict("ts_mdc", "Medchal-Malkajgiri", "Telangana", listOf("Malkajgiri", "Alwal", "Kompally", "Medchal", "Ecil Crossroads")),
            LocationDistrict("ts_hnk", "Hanumakonda & Warangal", "Telangana", listOf("Hanamkonda Main", "Kazipet", "Subedari", "Nayeem Nagar", "Chowrasta")),
            LocationDistrict("ts_krm", "Karimnagar", "Telangana", listOf("Mukarrampura", "Collectorate Road", "Kashmirgadda", "Jyothi Nagar")),
            LocationDistrict("ts_kmm", "Khammam", "Telangana", listOf("Wyra Road", "Mamillagudem", "Bypass Road")),
            LocationDistrict("ts_nzb", "Nizamabad", "Telangana", listOf("Khaleelwadi", "Bodhan Road", "Armoor")),
            LocationDistrict("ts_mhb", "Mahabubnagar", "Telangana", listOf("Raichur Road", "New Town", "Mettugadda")),
            LocationDistrict("ts_nlg", "Nalgonda", "Telangana", listOf("Clock Tower Center", "VT Colony", "Miryalaguda")),
            LocationDistrict("ts_sdp", "Siddipet", "Telangana", listOf("Mustabad Road", "Collectorate Road", "Gajwel"))
        )),

        // 25. Tripura
        LocationState("TR", "Tripura", false, listOf(
            LocationDistrict("tr_wtr", "West Tripura (Agartala)", "Tripura", listOf("Battala", "Banamalipur", "Radhanagar", "Akhaura Road", "Kunjaban", "GB Hospital Area", "Melarmath")),
            LocationDistrict("tr_gmt", "Gomati (Udaipur)", "Tripura", listOf("Matabari", "Udaipur Town", "R.K. Pur")),
            LocationDistrict("tr_dhl", "Dhalai (Ambassa)", "Tripura", listOf("Ambassa Bazar", "Kamalpur", "Gandacherra")),
            LocationDistrict("tr_nth", "North Tripura (Dharmanagar)", "Tripura", listOf("Dharmanagar Town", "Kanchanpur")),
            LocationDistrict("tr_sth", "South Tripura (Belonia)", "Tripura", listOf("Belonia Bazar", "Santirbazar", "Sabroom")),
            LocationDistrict("tr_unk", "Unakoti (Kailashahar)", "Tripura", listOf("Kailashahar Town", "Kumarghat")),
            LocationDistrict("tr_sph", "Sepahijala (Bishramganj)", "Tripura", listOf("Bishalgarh", "Sonamura", "Jatrapur")),
            LocationDistrict("tr_khw", "Khowai", "Tripura", listOf("Khowai Town", "Teliamura"))
        )),

        // 26. Uttar Pradesh
        LocationState("UP", "Uttar Pradesh", false, listOf(
            LocationDistrict("up_lko", "Lucknow", "Uttar Pradesh", listOf("Hazratganj", "Gomti Nagar", "Aliganj", "Indira Nagar", "Mahanagar", "Alambagh", "Aminabad", "Chowk", "Vikas Nagar", "Jankipuram")),
            LocationDistrict("up_knp", "Kanpur", "Uttar Pradesh", listOf("Swaroop Nagar", "Civil Lines", "Kakadeo", "Govind Nagar", "Kidwai Nagar", "Mall Road", "Gumti No. 5")),
            LocationDistrict("up_vns", "Varanasi", "Uttar Pradesh", listOf("Lanka (BHU)", "Sigra", "Godowlia", "Bhelupur", "Mahmoorganj", "Cantonment", "Assi Ghat", "Shivpur")),
            LocationDistrict("up_agr", "Agra", "Uttar Pradesh", listOf("Sanjay Place", "Tajganj", "Kamla Nagar", "Dayal Bagh", "Khandari", "Fatehabad Road")),
            LocationDistrict("up_pry", "Prayagraj (Allahabad)", "Uttar Pradesh", listOf("Civil Lines", "Katra", "George Town", "Tagore Town", "Allenganj", "Naini")),
            LocationDistrict("up_mrt", "Meerut", "Uttar Pradesh", listOf("Shastri Nagar", "Civil Lines", "Abu Lane", "Ganga Nagar", "Begum Bridge")),
            LocationDistrict("up_gbn", "Gautam Buddha Nagar (Noida / Gr Noida)", "Uttar Pradesh", listOf("Sector 18", "Sector 62", "Sector 137", "Sector 50", "Sector 76", "Pari Chowk", "Knowledge Park", "Greater Noida West")),
            LocationDistrict("up_ghz", "Ghaziabad (NCR)", "Uttar Pradesh", listOf("Indirapuram", "Vaishali", "Raj Nagar Extension", "Vasundhara", "Crossings Republik", "Kaushambi", "RDC Raj Nagar")),
            LocationDistrict("up_brl", "Bareilly", "Uttar Pradesh", listOf("Civil Lines", "DD Puram", "Rajendra Nagar", "Rampur Garden")),
            LocationDistrict("up_ali", "Aligarh", "Uttar Pradesh", listOf("Civil Lines", "Centre Point", "Marris Road", "Dodhpur")),
            LocationDistrict("up_mor", "Moradabad", "Uttar Pradesh", listOf("Civil Lines", "MDA Colony", "Kanth Road")),
            LocationDistrict("up_gor", "Gorakhpur", "Uttar Pradesh", listOf("Golghar", "Civil Lines", "Betiahata", "Medical College Road")),
            LocationDistrict("up_jhn", "Jhansi", "Uttar Pradesh", listOf("Sadar Bazar", "Elite Crossing", "Civil Lines", "Nandanpura")),
            LocationDistrict("up_mth", "Mathura - Vrindavan", "Uttar Pradesh", listOf("Dampier Nagar", "Vrindavan Raman Reti", "Krishna Nagar", "Bhuteshwar")),
            LocationDistrict("up_ayd", "Ayodhya (Faizabad)", "Uttar Pradesh", listOf("Ram Path", "Civil Lines", "Rikabganj", "Naka Bypass"))
        )),

        // 27. Uttarakhand
        LocationState("UK", "Uttarakhand", false, listOf(
            LocationDistrict("uk_ddn", "Dehradun", "Uttarakhand", listOf("Rajpur Road", "Paltan Bazar", "Chakrata Road", "Dalanwala", "Vasant Vihar", "Clement Town", "Mussoorie", "Rishikesh")),
            LocationDistrict("uk_hwd", "Haridwar", "Uttarakhand", listOf("Ranipur More", "Jwalapur", "Kankhal", "Shivalik Nagar", "BHEL")),
            LocationDistrict("uk_ntl", "Nainital", "Uttarakhand", listOf("Mall Road", "Haldwani (Kaladhungi Road)", "Kathgodam", "Ramnagar", "Mukteshwar")),
            LocationDistrict("uk_usn", "Udham Singh Nagar (Rudrapur)", "Uttarakhand", listOf("Civil Lines", "Kashipur", "Pantnagar", "Kichha")),
            LocationDistrict("uk_alm", "Almora", "Uttarakhand", listOf("Mall Road", "Lala Bazar", "Ranikhet")),
            LocationDistrict("uk_pgr", "Pithoragarh", "Uttarakhand", listOf("Siltham", "Cinema Line", "Didihat")),
            LocationDistrict("uk_pau", "Pauri Garhwal", "Uttarakhand", listOf("Kotdwar", "Pauri Town", "Srinagar Garhwal")),
            LocationDistrict("uk_teh", "Tehri Garhwal", "Uttarakhand", listOf("New Tehri", "Chamba")),
            LocationDistrict("uk_cha", "Chamoli (Gopeshwar)", "Uttarakhand", listOf("Gopeshwar", "Joshimath", "Karnaprayag")),
            LocationDistrict("uk_utk", "Uttarkashi", "Uttarakhand", listOf("Main Bazar", "Bhatwari Road", "Barkot"))
        )),

        // 28. West Bengal
        LocationState("WB", "West Bengal", false, listOf(
            LocationDistrict("wb_kol", "Kolkata", "West Bengal", listOf("Park Street", "Salt Lake Sector 1", "Salt Lake Sector 5", "New Town", "Ballygunge", "Alipore", "Gariahat", "Shyambazar", "Behala", "Jadavpur", "Dum Dum", "Howrah Bridge Approach")),
            LocationDistrict("wb_hwh", "Howrah", "West Bengal", listOf("Shibpur", "Kadamtala", "Santragachi", "Salkia", "Bally", "Liluah")),
            LocationDistrict("wb_dar", "Darjeeling & Siliguri", "West Bengal", listOf("Mall Road Darjeeling", "Sevoke Road Siliguri", "Hill Cart Road", "Pradhan Nagar", "Matigara", "Kurseong")),
            LocationDistrict("wb_n24", "North 24 Parganas", "West Bengal", listOf("Barasat", "Barrackpore", "Bidhannagar", "Naihati", "Madhyamgram", "Habra")),
            LocationDistrict("wb_s24", "South 24 Parganas", "West Bengal", listOf("Sonarpur", "Baruipur", "Garia", "Budge Budge", "Diamond Harbour")),
            LocationDistrict("wb_pbd", "Paschim Bardhaman (Asansol / Durgapur)", "West Bengal", listOf("City Centre Durgapur", "Benachity", "Asansol Burnpur Road", "Ushagram")),
            LocationDistrict("wb_ebp", "Purba Bardhaman", "West Bengal", listOf("Curzon Gate", "Burdwan Town", "Katwa")),
            LocationDistrict("wb_hoo", "Hooghly", "West Bengal", listOf("Chinsurah", "Serampore", "Uttarpara", "Chandannagar", "Bandel")),
            LocationDistrict("wb_mal", "Malda", "West Bengal", listOf("English Bazar", "Rabindra Avenue", "Mahanandapally")),
            LocationDistrict("wb_med", "Paschim Medinipur", "West Bengal", listOf("Kharagpur", "Midnapore Town", "Ghatal"))
        )),

        // UNION TERRITORIES (8)
        // 1. Delhi
        LocationState("DL", "Delhi", true, listOf(
            LocationDistrict("dl_south", "South Delhi", "Delhi", listOf("Hauz Khas", "Saket", "Greater Kailash 1/2", "Lajpat Nagar", "Green Park", "South Extension", "Malviya Nagar", "Vasant Kunj", "Nehru Place")),
            LocationDistrict("dl_central", "Central & New Delhi", "Delhi", listOf("Connaught Place", "Karol Bagh", "Pahar Ganj", "Chanakyapuri", "Patel Nagar", "Rajendra Nagar", "Khan Market")),
            LocationDistrict("dl_west", "West Delhi", "Delhi", listOf("Janakpuri", "Rajouri Garden", "Punjabi Bagh", "Tilak Nagar", "Dwarka Sector 6", "Dwarka Sector 12", "Paschim Vihar", "Uttam Nagar")),
            LocationDistrict("dl_north", "North Delhi", "Delhi", listOf("Civil Lines", "Model Town", "Kamla Nagar", "Roop Nagar", "Ashok Vihar", "Shalimar Bagh")),
            LocationDistrict("dl_nw", "North West Delhi (Rohini)", "Delhi", listOf("Rohini Sector 7", "Rohini Sector 9", "Rohini Sector 15", "Pitampura", "Prashant Vihar")),
            LocationDistrict("dl_east", "East Delhi", "Delhi", listOf("Preet Vihar", "Laxmi Nagar", "Mayur Vihar Phase 1/2", "Karkardooma", "Patparganj", "Geeta Colony")),
            LocationDistrict("dl_se", "South East Delhi", "Delhi", listOf("Kalkaji", "New Friends Colony", "Okhla", "Sarita Vihar", "Jasola", "Alaknanda")),
            LocationDistrict("dl_sw", "South West Delhi", "Delhi", listOf("Dwarka Sector 21", "Mahipalpur", "Najafgarh", "Palam")),
            LocationDistrict("dl_shd", "Shahdara", "Delhi", listOf("Vivek Vihar", "Jhilmil", "Dilshad Garden", "Shahdara Main"))
        )),

        // 2. Chandigarh
        LocationState("CH", "Chandigarh", true, listOf(
            LocationDistrict("ch_main", "Chandigarh", "Chandigarh", listOf("Sector 17 Plaza", "Sector 35 Market", "Sector 22", "Sector 8 Inner Market", "Sector 9", "Sector 26", "Sector 43", "Industrial Area Phase 1", "Elante Mall Area"))
        )),

        // 3. Jammu and Kashmir
        LocationState("JK", "Jammu and Kashmir", true, listOf(
            LocationDistrict("jk_srn", "Srinagar", "Jammu and Kashmir", listOf("Lal Chowk", "Karan Nagar", "Rajbagh", "Residency Road", "Hazratbal", "Sanat Nagar", "Hyderpora", "Soura")),
            LocationDistrict("jk_jam", "Jammu", "Jammu and Kashmir", listOf("Gandhi Nagar", "Trikuta Nagar", "Raghunath Bazar", "Channi Himmat", "Bahu Plaza", "Janipur", "Talab Tillo")),
            LocationDistrict("jk_ant", "Anantnag", "Jammu and Kashmir", listOf("KP Road", "Khanabal", "Achabal")),
            LocationDistrict("jk_bar", "Baramulla", "Jammu and Kashmir", listOf("Main Market", "Carriapa Park", "Gulmarg", "Sopore")),
            LocationDistrict("jk_bdg", "Budgam", "Jammu and Kashmir", listOf("Main Town", "Beerwah", "Chadoora")),
            LocationDistrict("jk_plw", "Pulwama", "Jammu and Kashmir", listOf("Murran Chowk", "Pampore (Saffron)", "Awantipora")),
            LocationDistrict("jk_kth", "Kathua", "Jammu and Kashmir", listOf("College Road", "Govindsar", "Main Market")),
            LocationDistrict("jk_udh", "Udhampur", "Jammu and Kashmir", listOf("Mukherjee Bazar", "Gole Market", "Dhar Road"))
        )),

        // 4. Ladakh
        LocationState("LA", "Ladakh", true, listOf(
            LocationDistrict("la_leh", "Leh", "Ladakh", listOf("Main Bazaar", "Fort Road", "Changspa", "Choglamsar", "Skalzangling")),
            LocationDistrict("la_krg", "Kargil", "Ladakh", listOf("Main Market", "Baroo", "Batalik Road", "Sankoo", "Drass"))
        )),

        // 5. Puducherry
        LocationState("PY", "Puducherry", true, listOf(
            LocationDistrict("py_pud", "Puducherry", "Puducherry", listOf("White Town / French Quarter", "Mission Street", "MG Road", "Jawaharlal Nehru Street", "Heritage Town", "Auroville Area", "Lawspet")),
            LocationDistrict("py_krk", "Karaikal", "Puducherry", listOf("Church Street", "Bharathiar Road")),
            LocationDistrict("py_mah", "Mahe", "Puducherry", listOf("Main Road", "Railway Station Road")),
            LocationDistrict("py_yan", "Yanam", "Puducherry", listOf("Pillaraya Temple Street", "Ferry Road"))
        )),

        // 6. Andaman and Nicobar Islands
        LocationState("AN", "Andaman and Nicobar Islands", true, listOf(
            LocationDistrict("an_s_and", "South Andaman (Port Blair)", "Andaman and Nicobar Islands", listOf("Aberdeen Bazaar", "Junglighat", "Haddo", "Garacharma", "Bathu Basti", "Havelock (Swaraj Dweep)", "Neil (Shaheed Dweep)")),
            LocationDistrict("an_n_and", "North and Middle Andaman", "Andaman and Nicobar Islands", listOf("Mayabunder", "Diglipur", "Rangat")),
            LocationDistrict("an_nic", "Nicobar", "Andaman and Nicobar Islands", listOf("Car Nicobar", "Great Nicobar (Campbell Bay)"))
        )),

        // 7. Dadra and Nagar Haveli and Daman and Diu
        LocationState("DN", "Dadra and Nagar Haveli and Daman and Diu", true, listOf(
            LocationDistrict("dn_slv", "Dadra and Nagar Haveli (Silvassa)", "Dadra and Nagar Haveli and Daman and Diu", listOf("Naroli Road", "Tokarkhada", "Kilvani Road", "Samarvarni")),
            LocationDistrict("dn_dmn", "Daman", "Dadra and Nagar Haveli and Daman and Diu", listOf("Nani Daman", "Moti Daman", "Devka Beach Road")),
            LocationDistrict("dn_diu", "Diu", "Dadra and Nagar Haveli and Daman and Diu", listOf("Main Bazaar", "Ghoghla", "Nagoa"))
        )),

        // 8. Lakshadweep
        LocationState("LD", "Lakshadweep", true, listOf(
            LocationDistrict("ld_main", "Lakshadweep", "Lakshadweep", listOf("Kavaratti", "Agatti", "Andrott", "Amini", "Minicoy", "Kalpeni"))
        ))
    )

    fun findState(name: String): LocationState? {
        val clean = name.trim()
        return states.find { it.name.equals(clean, ignoreCase = true) }
            ?: states.find { it.name.contains(clean, ignoreCase = true) || clean.contains(it.name, ignoreCase = true) }
    }
    
    fun getDistrictsForState(stateName: String): List<LocationDistrict> =
        findState(stateName)?.districts ?: emptyList()

    fun getAreasForDistrict(stateName: String, districtName: String): List<String> {
        val districts = getDistrictsForState(stateName)
        val cleanDist = districtName.trim().lowercase()
        val exact = districts.find { it.name.equals(districtName.trim(), ignoreCase = true) }
        if (exact != null && exact.areas.isNotEmpty()) return exact.areas
        
        val partial = districts.find {
            val n = it.name.lowercase()
            n.contains(cleanDist) || cleanDist.contains(n.substringBefore(" (").trim())
        }
        if (partial != null && partial.areas.isNotEmpty()) return partial.areas

        return listOf("Main Bazaar", "Market Yard", "Station Road", "Town Center", "Bypass Road")
    }
}
