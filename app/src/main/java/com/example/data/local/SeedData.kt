package com.example.data.local

object SeedData {
  val initialProducts = listOf(
    // 1. Mobile / Laptop
    ProductEntity(
      name = "OnePlus 11R 5G (16GB / 256GB)",
      category = "Mobile / Laptop",
      price = 24500.0,
      originalPrice = 39999.0,
      unit = "1 Unit with Box & Charger",
      vendorName = "Rohit Sharma",
      vendorLocality = "Hawakhana, West Garo Hills",
      vendorPhone = "+91 98765 11002",
      distanceKm = 1.1,
      rating = 4.8,
      reviewCount = 28,
      description = "Galactic Silver color, Snapdragon 8+ Gen 1, 100W SuperVOOC fast charger, original bill and box included. Battery health 96%.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Verified Seller",
      iconEmoji = "📱"
    ),
    ProductEntity(
      name = "Apple MacBook Air M2 (16GB RAM, 512GB SSD)",
      category = "Mobile / Laptop",
      price = 78000.0,
      originalPrice = 119900.0,
      unit = "Midnight Blue (Under AppleCare)",
      vendorName = "Tengrang Sangma",
      vendorLocality = "Araimile, West Garo Hills",
      vendorPhone = "+91 98765 11010",
      distanceKm = 0.8,
      rating = 4.9,
      reviewCount = 19,
      description = "Pristine condition, used for graphic design. 100% battery health, original MagSafe 3 charger and bill available.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Verified Gadget",
      iconEmoji = "💻"
    ),

    // 2. Grocery
    ProductEntity(
      name = "Fresh Organic Farm Vegetable & Fruit Basket",
      category = "Grocery",
      price = 349.0,
      originalPrice = 450.0,
      unit = "5 kg Combo Basket",
      vendorName = "Kisan Fresh Organics",
      vendorLocality = "Tura Bazaar, West Garo Hills",
      vendorPhone = "+91 98765 11001",
      distanceKm = 0.6,
      rating = 4.9,
      reviewCount = 142,
      description = "Naturally grown chemical-free seasonal vegetables & fresh local fruits. Directly harvested from local organic farms in Garo Hills.",
      inStock = true,
      isFarmerDirect = true,
      badge = "ITEM",
      harvestOrPackDate = "Harvested Today, 5:30 AM",
      iconEmoji = "🥦"
    ),
    ProductEntity(
      name = "Pure Meghalaya Forest Wild Honey (1 Litre)",
      category = "Grocery",
      price = 850.0,
      originalPrice = 1100.0,
      unit = "1 Litre Glass Bottle",
      vendorName = "Garo Hills Natural Store",
      vendorLocality = "Dobasipara, West Garo Hills",
      vendorPhone = "+91 98765 11005",
      distanceKm = 1.4,
      rating = 4.9,
      reviewCount = 64,
      description = "100% pure unfiltered raw forest honey sustainably gathered from indigenous wild hives in the Nokrek biosphere.",
      inStock = true,
      isFarmerDirect = true,
      badge = "ITEM",
      harvestOrPackDate = "Raw & Unprocessed",
      iconEmoji = "🍯"
    ),

    // 3. Services
    ProductEntity(
      name = "Home Electrician & AC Repairing Specialist",
      category = "Services",
      price = 299.0,
      originalPrice = 499.0,
      unit = "Inspection & Service Visit",
      vendorName = "Sharma Electricals & Cooling",
      vendorLocality = "Araimile, West Garo Hills",
      vendorPhone = "+91 98765 11003",
      distanceKm = 0.9,
      rating = 5.0,
      reviewCount = 89,
      description = "Certified electrician and HVAC technician for prompt doorstep repair, house wiring, inverter setup, and AC servicing.",
      inStock = true,
      isFarmerDirect = false,
      badge = "SERVICE",
      harvestOrPackDate = "Available Today",
      iconEmoji = "🔧"
    ),
    ProductEntity(
      name = "Plumbing & Water Tank Deep Cleaning Service",
      category = "Services",
      price = 399.0,
      originalPrice = 600.0,
      unit = "Doorstep Service Call",
      vendorName = "QuickPlumb Pro Tura",
      vendorLocality = "Ringrey, West Garo Hills",
      vendorPhone = "+91 98765 11011",
      distanceKm = 1.0,
      rating = 4.9,
      reviewCount = 45,
      description = "Leakage repair, pipe fitting, sanitary fixture installations, and pressurized water tank cleaning with anti-bacterial treatment.",
      inStock = true,
      isFarmerDirect = false,
      badge = "SERVICE",
      harvestOrPackDate = "Same Day Booking",
      iconEmoji = "🚿"
    ),

    // 4. Electronics
    ProductEntity(
      name = "Samsung 43-inch Crystal 4K Ultra HD Smart TV",
      category = "Electronics",
      price = 26900.0,
      originalPrice = 37900.0,
      unit = "1 Unit (Under Warranty)",
      vendorName = "Tura Electronics & Appliance",
      vendorLocality = "Bazar Ward No. 3, West Garo Hills",
      vendorPhone = "+91 98765 11007",
      distanceKm = 0.5,
      rating = 4.9,
      reviewCount = 52,
      description = "HDR10+, PurColor, Object Tracking Sound Lite with wall mount bracket and Magic Remote.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Brand New in Box",
      iconEmoji = "📺"
    ),
    ProductEntity(
      name = "Sony WH-1000XM4 Wireless Noise Cancelling Headphones",
      category = "Electronics",
      price = 16500.0,
      originalPrice = 24990.0,
      unit = "Silver Edition with Carry Case",
      vendorName = "Digital Zone Gadgets",
      vendorLocality = "Hawakhana, West Garo Hills",
      vendorPhone = "+91 98765 11012",
      distanceKm = 1.3,
      rating = 4.8,
      reviewCount = 37,
      description = "Industry leading active noise cancellation, 30-hour battery life, touch controls, with all original accessories and audio jack cable.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Verified Tested",
      iconEmoji = "🎧"
    ),

    // 5. Bike
    ProductEntity(
      name = "Royal Enfield Classic 350 (Dual Channel ABS)",
      category = "Bike",
      price = 155000.0,
      originalPrice = 185000.0,
      unit = "Single Owner (14,200 km)",
      vendorName = "Vikram Rajput",
      vendorLocality = "Tura Bazaar, West Garo Hills",
      vendorPhone = "+91 98765 11004",
      distanceKm = 0.4,
      rating = 4.7,
      reviewCount = 15,
      description = "Halcyon Grey edition, fully serviced at authorized showroom. Comprehensive insurance valid till Dec 2026, zero accidental history.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Verified Listing",
      iconEmoji = "🏍️"
    ),
    ProductEntity(
      name = "Honda Activa 6G (2023 Premium Metallic Grey)",
      category = "Bike",
      price = 62000.0,
      originalPrice = 78000.0,
      unit = "Driven 6,800 km only",
      vendorName = "Pawan Auto Deals",
      vendorLocality = "Araimile, West Garo Hills",
      vendorPhone = "+91 98765 11013",
      distanceKm = 0.7,
      rating = 4.9,
      reviewCount = 22,
      description = "Telescopic suspension, tubeless tyres, high mileage ~55 km/l. All papers complete with Meghalaya ML08 registration.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Inspected Condition",
      iconEmoji = "🛵"
    ),

    // 6. Car
    ProductEntity(
      name = "Maruti Suzuki Swift ZXi (2022 Petrol)",
      category = "Car",
      price = 540000.0,
      originalPrice = 690000.0,
      unit = "Magma Grey (28,500 km)",
      vendorName = "Garo Hills Motors",
      vendorLocality = "Hawakhana, West Garo Hills",
      vendorPhone = "+91 98765 11014",
      distanceKm = 1.2,
      rating = 4.8,
      reviewCount = 18,
      description = "Touchscreen infotainment, push button start, reverse camera, non-accidental, dealer serviced with valid comprehensive insurance.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Certified Pre-Owned",
      iconEmoji = "🚗"
    ),
    ProductEntity(
      name = "Hyundai Creta SX 1.5 (2021 Panoramic Sunroof)",
      category = "Car",
      price = 980000.0,
      originalPrice = 1350000.0,
      unit = "Polar White (34,000 km)",
      vendorName = "Northeast Premium Wheels",
      vendorLocality = "Ringrey, West Garo Hills",
      vendorPhone = "+91 98765 11015",
      distanceKm = 1.5,
      rating = 4.9,
      reviewCount = 14,
      description = "Top variant with panoramic sunroof, 10.25-inch infotainment with Bose sound, wireless charger, brand new Michelin tyres.",
      inStock = true,
      isFarmerDirect = false,
      badge = "ITEM",
      harvestOrPackDate = "Verified Documents",
      iconEmoji = "🚙"
    ),

    // 7. Personal Care
    ProductEntity(
      name = "Organic Ayurvedic Hair Oil & Natural Shampoo Kit",
      category = "Personal Care",
      price = 499.0,
      originalPrice = 750.0,
      unit = "200ml Oil + 250ml Shampoo",
      vendorName = "Nokrek Herbal Care",
      vendorLocality = "Dobasipara, West Garo Hills",
      vendorPhone = "+91 98765 11016",
      distanceKm = 1.4,
      rating = 4.9,
      reviewCount = 88,
      description = "Infused with Bhringraj, Amla, Shikakai, and cold-pressed coconut oil. Strengthens hair roots and controls dandruff naturally.",
      inStock = true,
      isFarmerDirect = true,
      badge = "ITEM",
      harvestOrPackDate = "100% Herbal & Chemical-Free",
      iconEmoji = "🧴"
    ),
    ProductEntity(
      name = "Pure Aloe Vera & Neem Herbal Skin Care Combo",
      category = "Personal Care",
      price = 380.0,
      originalPrice = 550.0,
      unit = "Gel (300g) + Facewash (150ml)",
      vendorName = "Garo Hills Natural Store",
      vendorLocality = "Tura Bazaar, West Garo Hills",
      vendorPhone = "+91 98765 11005",
      distanceKm = 0.6,
      rating = 5.0,
      reviewCount = 63,
      description = "Pure soothing organic aloe vera gel harvested from local hills with antibacterial neem face cleanser for radiant glowing skin.",
      inStock = true,
      isFarmerDirect = true,
      badge = "ITEM",
      harvestOrPackDate = "Organic Certified",
      iconEmoji = "🌿"
    ),

    // 8. Fashion & Traditional Wear
    ProductEntity(
      name = "Traditional Garo Handloom Dakmanda Shawl",
      category = "Fashion & Wear",
      price = 1200.0,
      originalPrice = 1600.0,
      unit = "Handwoven Piece",
      vendorName = "Garo Weavers Cooperative",
      vendorLocality = "Rongkhon, West Garo Hills",
      vendorPhone = "+91 98765 11008",
      distanceKm = 2.1,
      rating = 5.0,
      reviewCount = 41,
      description = "Authentic tribal handwoven cotton cloth with intricate indigenous patterns made by local master artisans.",
      inStock = true,
      isFarmerDirect = true,
      badge = "ITEM",
      harvestOrPackDate = "Artisan Made",
      iconEmoji = "👗"
    )
  )

  val initialStores = listOf(
    StoreEntity(
      name = "Kisan Fresh Organics & Mandi",
      category = "Farm Produce & Fruits",
      locality = "Tura Bazaar, West Garo Hills",
      address = "Shop 14, Main Agricultural Mandi, Tura",
      phone = "+91 98765 11001",
      rating = 4.9,
      distanceKm = 0.6,
      deliveryTime = "15-25 mins",
      openingHours = "6:00 AM - 8:30 PM",
      isOpen = true,
      isVerified = true,
      storeBadge = "Verified Farmer Direct",
      storeEmoji = "🥦"
    ),
    StoreEntity(
      name = "Sharma Electricals & Cooling",
      category = "Local Home Services",
      locality = "Araimile, West Garo Hills",
      address = "Near SBI ATM, Araimile Junction",
      phone = "+91 98765 11003",
      rating = 5.0,
      distanceKm = 0.9,
      deliveryTime = "Doorstep in 30 mins",
      openingHours = "8:00 AM - 9:00 PM",
      isOpen = true,
      isVerified = true,
      storeBadge = "Top Rated Pro",
      storeEmoji = "⚡"
    ),
    StoreEntity(
      name = "Tura Tech & Mobile Hub",
      category = "Mobiles & Gadgets",
      locality = "Hawakhana, West Garo Hills",
      address = "Commercial Complex, 1st Floor, Hawakhana",
      phone = "+91 98765 11002",
      rating = 4.8,
      distanceKm = 1.1,
      deliveryTime = "Pickup & Same-Day",
      openingHours = "9:30 AM - 8:00 PM",
      isOpen = true,
      isVerified = true,
      storeBadge = "Verified Gadget Hub",
      storeEmoji = "📱"
    ),
    StoreEntity(
      name = "Garo Hills Natural Store",
      category = "Organic Forest Products",
      locality = "Dobasipara, West Garo Hills",
      address = "Nokrek Road, Dobasipara",
      phone = "+91 98765 11005",
      rating = 4.9,
      distanceKm = 1.4,
      deliveryTime = "20-40 mins",
      openingHours = "7:00 AM - 7:30 PM",
      isOpen = true,
      isVerified = true,
      storeBadge = "100% Indigenous",
      storeEmoji = "🍯"
    )
  )

  val initialOrders = listOf(
    OrderEntity(
      orderId = "LB-90214",
      itemsSummary = "1x Fresh Organic Farm Vegetable & Fruit Basket, 1x Pure Meghalaya Forest Honey",
      totalAmount = 1199.0,
      itemCount = 2,
      orderStatus = "OUT_FOR_DELIVERY",
      deliveryAddress = "House 24, Hawakhana, Tura, West Garo Hills",
      deliveryInstructions = "Please call upon arrival at the gate",
      paymentMethod = "Cash on Delivery",
      placedAt = System.currentTimeMillis() - 25 * 60 * 1000,
      vendorName = "Kisan Fresh Organics",
      deliveryTimeEstimate = "Arriving in ~10 mins"
    ),
    OrderEntity(
      orderId = "LB-88341",
      itemsSummary = "1x Home Electrician & AC Repairing Visit",
      totalAmount = 299.0,
      itemCount = 1,
      orderStatus = "CONFIRMED",
      deliveryAddress = "Araimile Road, Tura, West Garo Hills",
      deliveryInstructions = "Doorbell ring twice",
      paymentMethod = "UPI QR on Arrival",
      placedAt = System.currentTimeMillis() - 2 * 3600 * 1000,
      vendorName = "Sharma Electricals & Cooling",
      deliveryTimeEstimate = "Technician Scheduled at 3:30 PM"
    )
  )

  val initialRequests = listOf(
    CommunityRequestEntity(
      title = "Looking for Fresh Organic King Chilli (Bhut Jolokia)",
      description = "Need 500g fresh picked green or red king chillies for traditional Garo pickle making.",
      category = "Grocery",
      requesterName = "Greja Marak",
      locality = "Hawakhana, West Garo Hills",
      status = "OPEN",
      offersCount = 3,
      createdAt = System.currentTimeMillis() - 4 * 3600 * 1000
    ),
    CommunityRequestEntity(
      title = "Second hand study table & ergonomic chair needed",
      description = "In good condition for college student studying in Tura campus. Budget around ₹2,000.",
      category = "Electronics",
      requesterName = "Tengrang Sangma",
      locality = "Araimile, West Garo Hills",
      status = "OPEN",
      offersCount = 1,
      createdAt = System.currentTimeMillis() - 10 * 3600 * 1000
    )
  )

  val initialMarketRates = listOf(
    MarketRateEntity(
      commodity = "Organic Tomatoes",
      hindiName = "Desi Tamatar",
      mandiPrice = "₹32 - ₹36 / kg",
      trend = "DOWN",
      trendPercentage = "-5%",
      updatedTime = "Today, 7:00 AM",
      emoji = "🍅"
    ),
    MarketRateEntity(
      commodity = "Wild Forest Honey",
      hindiName = "Shuddh Shahad",
      mandiPrice = "₹800 - ₹900 / L",
      trend = "STABLE",
      trendPercentage = "0%",
      updatedTime = "Today, 6:30 AM",
      emoji = "🍯"
    ),
    MarketRateEntity(
      commodity = "Mustard Oil (Kachi Ghani)",
      hindiName = "Sarson Tel",
      mandiPrice = "₹140 - ₹150 / L",
      trend = "UP",
      trendPercentage = "+3%",
      updatedTime = "Today, 7:15 AM",
      emoji = "🌻"
    ),
    MarketRateEntity(
      commodity = "Fresh Ginger",
      hindiName = "Adrak",
      mandiPrice = "₹90 - ₹110 / kg",
      trend = "UP",
      trendPercentage = "+7%",
      updatedTime = "Today, 7:00 AM",
      emoji = "🫚"
    )
  )
}
