package com.watchstore.controller;

import com.watchstore.model.Product;
import com.watchstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Please send a message."));
        }

        // Build product context for the AI
        List<Product> products = productService.getActiveProducts();
        String productList = products.stream()
            .map(p -> String.format("- %s (%s) | Category: %s | Price: ₹%s | Stock: %d",
                p.getName(), p.getBrand(), p.getCategory(), p.getPrice(), p.getStock()))
            .collect(Collectors.joining("\n"));

        String systemPrompt = """
            You are WatchBot, a friendly watch store assistant for WatchStore India.
            You help customers find the perfect watch based on their needs and budget.
            
            Available watches in our store:
            """ + productList + """
            
            Guidelines:
            - Answer ONLY about watches and our store products
            - Suggest watches based on budget, category (Luxury/Sport/Casual/Smart/Vintage), or brand
            - Keep replies short, friendly and helpful (2-4 sentences max)
            - Mention price in ₹ (Indian Rupees)
            - If asked about something unrelated to watches, politely redirect to watch topics
            - For buying, direct them to browse our shop at /shop
            """;

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", System.getenv("ANTHROPIC_API_KEY") != null 
                ? System.getenv("ANTHROPIC_API_KEY") : "");
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = Map.of(
                "model", "claude-haiku-4-5-20251001",
                "max_tokens", 300,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userMessage))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages", request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<?> content = (List<?>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    Map<?, ?> firstContent = (Map<?, ?>) content.get(0);
                    String reply = (String) firstContent.get("text");
                    return ResponseEntity.ok(Map.of("reply", reply));
                }
            }
        } catch (Exception e) {
            // Fallback: rule-based responses if API not configured
            String reply = getFallbackReply(userMessage, products);
            return ResponseEntity.ok(Map.of("reply", reply));
        }

        return ResponseEntity.ok(Map.of("reply", "Sorry, I'm having trouble right now. Please browse our shop for watches!"));
    }

    private String getFallbackReply(String message, List<Product> products) {
        String msg = message.toLowerCase();
        if (msg.contains("luxury") || msg.contains("rolex") || msg.contains("expensive")) {
            return "🌟 For luxury watches, we recommend Rolex Submariner (₹8,50,000), Omega Seamaster (₹5,20,000), or Tag Heuer Carrera (₹1,95,000). These are premium Swiss timepieces!";
        } else if (msg.contains("sport") || msg.contains("fitness") || msg.contains("running")) {
            return "🏃 For sports, check out Garmin Fenix 7 Pro (₹89,900) for GPS tracking, Casio G-Shock GA-2100 (₹8,500) for durability, or Fastrack Reflex 3.0 (₹3,995) for fitness tracking!";
        } else if (msg.contains("smart") || msg.contains("apple") || msg.contains("samsung")) {
            return "📱 Our smartwatches include Apple Watch Series 9 (₹41,900), Samsung Galaxy Watch 6 (₹27,999), and Fossil Gen 6 (₹22,995). All feature health monitoring!";
        } else if (msg.contains("budget") || msg.contains("cheap") || msg.contains("affordable")) {
            return "💰 Great budget options: Casio Vintage A168W (₹1,895), HMT Janata Classic (₹2,200), Fastrack Reflex (₹3,995), Titan Edge Slim (₹4,995). Quality watches at great prices!";
        } else if (msg.contains("vintage") || msg.contains("classic") || msg.contains("old")) {
            return "⌚ For vintage style, we have Orient Bambino Classic (₹12,500) and HMT Janata Classic (₹2,200). Timeless pieces with classic mechanical movements!";
        } else if (msg.contains("price") || msg.contains("cost") || msg.contains("range")) {
            return "💎 Our price range: Budget ₹1,895–₹5,000 | Mid-range ₹5,000–₹50,000 | Luxury ₹50,000+. Visit /shop to browse by category!";
        }
        return "👋 Hi! I'm WatchBot. I can help you find the perfect watch by budget, style (Luxury/Sport/Smart/Casual/Vintage), or brand. What are you looking for?";
    }
}
