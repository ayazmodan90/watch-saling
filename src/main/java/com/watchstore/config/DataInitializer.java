package com.watchstore.config;

import com.watchstore.model.Product;
import com.watchstore.model.User;
import com.watchstore.repository.ProductRepository;
import com.watchstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByEmail("admin@watchstore.com")) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@watchstore.com");
            admin.setPassword("admin123");
            admin.setPhone("9999999999");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.Status.ACTIVE);
            userRepository.save(admin);
            System.out.println("✅ Default Admin created: admin@watchstore.com / admin123");
        }

        // Add 15 sample products if DB is empty
        if (productRepository.count() == 0) {
            addProduct("Rolex Submariner", "Rolex", "Luxury",
                "Iconic Swiss dive watch with ceramic bezel, Oystersteel case, and 300m water resistance. Date display and Oyster bracelet.",
                new BigDecimal("850000"), 5,
                "https://images.unsplash.com/photo-1547996160-81dfa63595aa?w=400");

            addProduct("Omega Seamaster 300M", "Omega",  "Luxury",
                "Professional diver's watch with co-axial movement, 300m water resistance, and helium escape valve. Bond's choice.",
                new BigDecimal("520000"), 8,
                "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=400");

            addProduct("Casio G-Shock GA-2100", "Casio", "Sport",
                "Carbon core guard structure, shock & water resistant 200m. Multi-function digital-analog display with LED backlight.",
                new BigDecimal("8500"), 50,
                "https://images.unsplash.com/photo-1508057198894-247b23fe5ade?w=400");

            addProduct("Apple Watch Series 9", "Apple", "Smart",
                "Advanced health sensors, ECG, blood oxygen monitoring, crash detection, and Always-On Retina display. WatchOS 10.",
                new BigDecimal("41900"), 30,
                "https://images.unsplash.com/photo-1551816230-ef5deaed4a26?w=400");

            addProduct("Seiko Presage Cocktail", "Seiko", "Luxury",
                "Japanese automatic movement, stunning enamel dial inspired by classic cocktails, sapphire crystal glass, 50m water resistance.",
                new BigDecimal("32000"), 12,
                "https://images.unsplash.com/photo-1594534475808-b18fc33b045e?w=400");

            addProduct("Fossil Gen 6 Smartwatch", "Fossil", "Smart",
                "Wear OS by Google, Snapdragon 4100+ chip, SpO2 tracking, heart rate monitor, GPS, and 3-day battery life.",
                new BigDecimal("22995"), 20,
                "https://images.unsplash.com/photo-1617625802912-cde586faf331?w=400");

            addProduct("Titan Edge Slim", "Titan", "Casual",
                "World's slimmest watch collection with 3D dial technology, mineral crystal glass, and genuine leather strap.",
                new BigDecimal("4995"), 40,
                "https://images.unsplash.com/photo-1434056886845-dac89ffe9b56?w=400");

            addProduct("Tag Heuer Carrera", "Tag Heuer", "Luxury",
                "Iconic chronograph with COSC-certified automatic movement, 100m water resistance, and luxurious alligator leather strap.",
                new BigDecimal("195000"), 6,
                "https://images.unsplash.com/photo-1529870420596-88e9dcef62af?w=400");

            addProduct("Casio Vintage A168W", "Casio", "Casual",
                "Classic retro digital watch with stainless steel band, EL backlight, 1/100 second stopwatch, and daily alarm.",
                new BigDecimal("1895"), 100,
                "https://images.unsplash.com/photo-1612817288484-6f916006741a?w=400");

            addProduct("Garmin Fenix 7 Pro", "Garmin", "Sport",
                "Multi-sport GPS smartwatch with solar charging, topographic maps, advanced training metrics, and 18-day battery life.",
                new BigDecimal("89900"), 15,
                "https://images.unsplash.com/photo-1544117519-31a4b719223d?w=400");

            addProduct("Orient Bambino Classic", "Orient", "Vintage",
                "Elegant hand-winding mechanical watch with domed crystal, classic Roman numeral dial, and stainless steel case.",
                new BigDecimal("12500"), 18,
                "https://images.unsplash.com/photo-1516574187841-cb9cc2ca948b?w=400");

            addProduct("Samsung Galaxy Watch 6", "Samsung", "Smart",
                "Advanced BioActive Sensor, body composition analysis, sleep coaching, ECG, and seamless Galaxy ecosystem integration.",
                new BigDecimal("27999"), 25,
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=400");

            addProduct("Fastrack Reflex 3.0", "Fastrack", "Sport",
                "Activity tracker with SpO2 monitoring, continuous heart rate, sleep tracking, multiple sports modes, and 10-day battery.",
                new BigDecimal("3995"), 60,
                "https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=400");

            addProduct("Longines Master Collection", "Longines", "Luxury",
                "Swiss-made automatic chronograph with moon phase display, date, and elegant sunray dial. 30m water resistance.",
                new BigDecimal("145000"), 4,
                "https://images.unsplash.com/photo-1587836374828-4dbafa94cf0e?w=400");

            addProduct("HMT Janata Classic", "HMT", "Vintage",
                "Iconic Indian mechanical watch with hand-winding movement, classic gold-tone case, and genuine leather strap. A piece of history.",
                new BigDecimal("2200"), 35,
                "https://images.unsplash.com/photo-1465153690352-10c1b29577f8?w=400");

            System.out.println("✅ 15 Sample products added!");
        }
    }

    private void addProduct(String name, String brand, String category,
                             String description, BigDecimal price, int stock, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setCategory(category);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        p.setStatus(Product.ProductStatus.ACTIVE);
        productRepository.save(p);
    }
}
