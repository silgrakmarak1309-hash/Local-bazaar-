package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val isService: Boolean = false,
    val description: String = "",
    val subcategories: List<String> = emptyList()
)

object LocalBazaarCategories {
    val productCategories = listOf(
        CategoryItem(
            id = "cat_mobiles",
            name = "Mobiles & Tablets",
            icon = Icons.Default.PhoneAndroid,
            description = "Smartphones, Tablets, Smartwatches & Mobile Accessories",
            subcategories = listOf("Smartphones", "iPhones", "Tablets & iPads", "Smartwatches", "Accessories")
        ),
        CategoryItem(
            id = "cat_electronics",
            name = "Electronics",
            icon = Icons.Default.Tv,
            description = "TVs, Audio, Cameras & Home Entertainment",
            subcategories = listOf("TVs & Monitors", "Audio & Speakers", "Cameras & Lenses", "Gaming Consoles")
        ),
        CategoryItem(
            id = "cat_computers",
            name = "Laptops & Computers",
            icon = Icons.Default.Laptop,
            description = "Laptops, Desktops, PC Parts & Monitors",
            subcategories = listOf("Laptops", "MacBooks", "Desktops", "Printers", "PC Components")
        ),
        CategoryItem(
            id = "cat_bikes",
            name = "Bikes & Scooters",
            icon = Icons.Default.TwoWheeler,
            description = "Motorcycles, Scooters, Electric Bikes & Bicycles",
            subcategories = listOf("Motorcycles", "Scooters", "Electric Scooters", "Bicycles", "Riding Gear")
        ),
        CategoryItem(
            id = "cat_cars",
            name = "Cars",
            icon = Icons.Default.DirectionsCar,
            description = "Used Cars, Commercial Vehicles & Auto Accessories",
            subcategories = listOf("Hatchbacks", "Sedans", "SUVs", "Commercial Vehicles", "Car Accessories")
        ),
        CategoryItem(
            id = "cat_furniture",
            name = "Furniture",
            icon = Icons.Default.Weekend,
            description = "Sofas, Beds, Dining, Office Chairs & Wardrobes",
            subcategories = listOf("Sofas & Couches", "Beds & Mattresses", "Office Chairs & Desks", "Dining Tables")
        ),
        CategoryItem(
            id = "cat_appliances",
            name = "Home Appliances",
            icon = Icons.Default.Kitchen,
            description = "Fridges, Washing Machines, ACs, Microwaves",
            subcategories = listOf("Refrigerators", "Washing Machines", "Air Conditioners", "Microwaves & Ovens")
        ),
        CategoryItem(
            id = "cat_books",
            name = "Books",
            icon = Icons.Default.MenuBook,
            description = "Competitive Exam Books, Novels, Textbooks & Comics",
            subcategories = listOf("Exam & Entrance", "College & School", "Fiction & Novels", "Non-Fiction")
        ),
        CategoryItem(
            id = "cat_fashion",
            name = "Clothing & Fashion",
            icon = Icons.Default.Checkroom,
            description = "Traditional Wear, Men's, Women's, Shoes & Bags",
            subcategories = listOf("Men's Wear", "Women's Ethnic", "Footwear", "Watches & Bags")
        ),
        CategoryItem(
            id = "cat_sports",
            name = "Sports & Fitness",
            icon = Icons.Default.FitnessCenter,
            description = "Gym Equipment, Cricket, Badminton & Cycles",
            subcategories = listOf("Gym & Fitness", "Cricket & Outdoor", "Badminton & Tennis", "Cycles")
        ),
        CategoryItem(
            id = "cat_kids",
            name = "Kids & Baby",
            icon = Icons.Default.ChildCare,
            description = "Strollers, Toys, Baby Clothes & Baby Gear",
            subcategories = listOf("Baby Gear & Strollers", "Toys & Games", "Kids Furniture")
        ),
        CategoryItem(
            id = "cat_music",
            name = "Musical Instruments",
            icon = Icons.Default.MusicNote,
            description = "Guitars, Keyboards, Harmonicas & Indian Instruments",
            subcategories = listOf("Acoustic & Electric Guitars", "Keyboards & Pianos", "Tabla & Harmonium")
        ),
        CategoryItem(
            id = "cat_other_prod",
            name = "Other Products",
            icon = Icons.Default.Category,
            description = "Collectibles, Hobbies, Tools and misc items",
            subcategories = listOf("Collectibles", "Tools", "General")
        )
    )

    val serviceCategories = listOf(
        CategoryItem(
            id = "srv_tutors",
            name = "Tutors & Coaching",
            icon = Icons.Default.School,
            isService = true,
            description = "Home Tutors, Language Teachers & Competitive Coaching",
            subcategories = listOf("School Tutors", "Competitive Exam", "Music & Dance", "Languages")
        ),
        CategoryItem(
            id = "srv_electricians",
            name = "Electricians",
            icon = Icons.Default.ElectricalServices,
            isService = true,
            description = "Home Wiring, Inverter Repair & Appliance Install",
            subcategories = listOf("Wiring & Repair", "Inverter & Battery", "Appliance Installation")
        ),
        CategoryItem(
            id = "srv_plumbers",
            name = "Plumbers",
            icon = Icons.Default.Plumbing,
            isService = true,
            description = "Pipe fitting, Leakage, Motor Pump Repair & Bathroom fittings",
            subcategories = listOf("Leakage & Repair", "Bathroom Fittings", "Motor Pumps")
        ),
        CategoryItem(
            id = "srv_mechanics",
            name = "Mechanics & Auto Care",
            icon = Icons.Default.Build,
            isService = true,
            description = "Bike/Car Servicing, Puncture & Breakdown Support",
            subcategories = listOf("Doorstep Bike Service", "Car Maintenance", "Puncture & Battery Jump")
        ),
        CategoryItem(
            id = "srv_mobile_repair",
            name = "Mobile & Laptop Repair",
            icon = Icons.Default.PhonelinkSetup,
            isService = true,
            description = "Screen Replacement, Battery Fix & OS re-installation",
            subcategories = listOf("Screen Replacement", "Battery & Charging", "Chip Level Repair")
        ),
        CategoryItem(
            id = "srv_tailors",
            name = "Tailors & Boutiques",
            icon = Icons.Default.ContentCut,
            isService = true,
            description = "Custom Stitching, Alterations & Bridal Wear",
            subcategories = listOf("Ladies Tailoring & Blouse", "Men's Suit Stitching", "Alterations")
        ),
        CategoryItem(
            id = "srv_cleaning",
            name = "Cleaning & Pest Control",
            icon = Icons.Default.CleaningServices,
            isService = true,
            description = "Deep House Cleaning, Sofa Wash & Pest Eradication",
            subcategories = listOf("Deep Home Cleaning", "Sofa & Carpet", "Pest Control")
        ),
        CategoryItem(
            id = "srv_photo",
            name = "Photography & Events",
            icon = Icons.Default.PhotoCamera,
            isService = true,
            description = "Weddings, Birthdays, Product Shoot & Portfolios",
            subcategories = listOf("Wedding & Pre-wedding", "Events & Birthday", "Product Shoot")
        ),
        CategoryItem(
            id = "srv_other",
            name = "Other Services",
            icon = Icons.Default.Handyman,
            isService = true,
            description = "Painters, Movers & Packers, Carpenters",
            subcategories = listOf("Carpenters", "Painters", "Packers & Movers")
        )
    )

    val allCategories: List<CategoryItem> = productCategories + serviceCategories

    fun findCategory(name: String): CategoryItem? =
        allCategories.find { it.name.equals(name, ignoreCase = true) }
}
