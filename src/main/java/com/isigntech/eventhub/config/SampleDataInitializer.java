package com.isigntech.eventhub.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isigntech.eventhub.entity.Attendee;
import com.isigntech.eventhub.entity.Booking;
import com.isigntech.eventhub.entity.BookingItem;
import com.isigntech.eventhub.entity.Event;
import com.isigntech.eventhub.entity.EventCategory;
import com.isigntech.eventhub.entity.Role;
import com.isigntech.eventhub.entity.User;
import com.isigntech.eventhub.entity.Venue;
import com.isigntech.eventhub.repository.AttendeeRepository;
import com.isigntech.eventhub.repository.BookingItemRepository;
import com.isigntech.eventhub.repository.BookingRepository;
import com.isigntech.eventhub.repository.EventCategoryRepository;
import com.isigntech.eventhub.repository.EventRepository;
import com.isigntech.eventhub.repository.RoleRepository;
import com.isigntech.eventhub.repository.UserRepository;
import com.isigntech.eventhub.repository.VenueRepository;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    private static final String SAMPLE_EMAIL = "alex.morgan@example.com";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EventCategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final AttendeeRepository attendeeRepository;

    public SampleDataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            EventCategoryRepository categoryRepository,
            VenueRepository venueRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            BookingItemRepository bookingItemRepository,
            AttendeeRepository attendeeRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Roles
        roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role(null, "CUSTOMER")));
        roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

        // Cleanup old initial placeholder events if present
        for (String legacyTitle : List.of("Austin Summer Sounds", "Central Texas Tech Forum")) {
            for (Event legacyEvent : eventRepository.findByTitleContainingIgnoreCase(legacyTitle)) {
                if (legacyEvent.getTitle().equalsIgnoreCase(legacyTitle)) {
                    List<BookingItem> items = bookingItemRepository.findAll().stream()
                            .filter(i -> i.getEvent() != null && i.getEvent().getId().equals(legacyEvent.getId()))
                            .toList();
                    for (BookingItem item : items) {
                        Booking b = item.getBooking();
                        attendeeRepository.findAll().stream()
                                .filter(a -> a.getBooking() != null && a.getBooking().getId().equals(b.getId()))
                                .forEach(attendeeRepository::delete);
                        bookingItemRepository.delete(item);
                        bookingRepository.delete(b);
                    }
                    eventRepository.delete(legacyEvent);
                }
            }
        }

        // Categories
        EventCategory techCategory = findOrCreateCategory("Technology", "AI, Web Development, Cloud & Startups");
        EventCategory musicCategory = findOrCreateCategory("Music", "Live Concerts, DJ Sets & Festivals");
        EventCategory artCategory = findOrCreateCategory("Art & Design", "Exhibitions, Galleries & Workshops");
        EventCategory bizCategory = findOrCreateCategory("Business", "Conferences, Expos & Networking");
        EventCategory foodCategory = findOrCreateCategory("Food & Drink", "Tastings, Culinary Masterclasses & Festivals");
        EventCategory sportsCategory = findOrCreateCategory("Sports & Fitness", "Marathons, Tournaments & Fitness Expos");

        // Venues
        Venue javitsCenter = findOrCreateVenue("Jacob Javits Convention Center", "429 11th Ave", "New York", 3500);
        Venue amphitheater = findOrCreateVenue("Downtown Amphitheater", "1200 Grand Ave", "Los Angeles", 8000);
        Venue artsCenter = findOrCreateVenue("Contemporary Arts Center", "201 Michigan Blvd", "Chicago", 1500);
        Venue palaceHotel = findOrCreateVenue("Palace Hotel Conference Hall", "2 New Montgomery St", "San Francisco", 2000);
        Venue waterfrontPavilion = findOrCreateVenue("Waterfront Pavilion", "1800 Harbor Drive", "San Diego", 2200);
        Venue centralPark = findOrCreateVenue("City Central Park & Boulevard", "1000 Park Avenue", "Seattle", 5000);

        // Demo User
        User customer = userRepository.findByEmail(SAMPLE_EMAIL)
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Alex Morgan")
                        .email(SAMPLE_EMAIL)
                        .password("demo123")
                        .phone("+1 (555) 234-5678")
                        .role("CUSTOMER")
                        .build()));

        // Seed 6 Showcase Events
        Event techSummit = upsertEvent(
                "Tech Innovators Global Summit 2026",
                "Join over 2,000 tech visionaries, software engineers, and founders for three days of keynotes, architectural deep-dives, hands-on workshops, and startup pitches covering AI, cloud infrastructure, and modern web frameworks.",
                techCategory,
                javitsCenter,
                LocalDateTime.of(2026, 11, 15, 9, 30),
                89.0,
                500,
                86,
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1200&q=80",
                "NextGen Tech Foundation",
                true);

        upsertEvent(
                "Neon Horizons Electronic Music Festival",
                "An electrifying two-night audio-visual celebration featuring world-class DJs, immersive laser installations, food truck villages, and interactive art domes under the California night sky.",
                musicCategory,
                amphitheater,
                LocalDateTime.of(2026, 12, 4, 18, 0),
                110.0,
                1200,
                34,
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80",
                "Pulse Music Collective",
                true);

        upsertEvent(
                "Modern Art, XR & Generative Design Expo",
                "Explore the boundaries between digital medium, spatial computing, and physical sculpture. Curated displays from 40+ international artists alongside live creative coding demonstrations.",
                artCategory,
                artsCenter,
                LocalDateTime.of(2026, 11, 20, 10, 0),
                32.0,
                300,
                142,
                "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?auto=format&fit=crop&w=1200&q=80",
                "Chicago Creative Guild",
                false);

        upsertEvent(
                "Global Venture & Startup Pitch Summit",
                "Where founders meet Tier-1 angels, VCs, and growth partners. Featuring 60 live startup pitches, investor roundtables, and dedicated 1-on-1 deal rooms.",
                bizCategory,
                palaceHotel,
                LocalDateTime.of(2026, 12, 10, 8, 30),
                149.0,
                400,
                62,
                "https://images.unsplash.com/photo-1515187029135-18ee286d815b?auto=format&fit=crop&w=1200&q=80",
                "Silicon Venture Alliance",
                true);

        upsertEvent(
                "Artisan Food & Craft Wine Showcase",
                "Savor gourmet dishes crafted by Michelin-starred guest chefs paired with rare vintages from 50+ boutique vineyards. Includes live tasting sessions and cooking masterclasses.",
                foodCategory,
                waterfrontPavilion,
                LocalDateTime.of(2026, 11, 28, 12, 0),
                65.0,
                350,
                22,
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80",
                "Pacific Culinary Society",
                false);

        upsertEvent(
                "Metro Half Marathon & Wellness Festival",
                "Run through scenic city landmarks, coastal promenades, and lush urban parks. Followed by a high-energy post-race festival with live music, recovery zones, and healthy food vendors.",
                sportsCategory,
                centralPark,
                LocalDateTime.of(2026, 12, 18, 7, 0),
                42.0,
                800,
                210,
                "https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&w=1200&q=80",
                "Northwest Athletic Club",
                false);

        // Seed initial booking if none exists for demo customer
        List<Booking> userBookings = bookingRepository.findByUserId(customer.getId());
        if (userBookings.isEmpty()) {
            Booking booking = new Booking();
            booking.setUser(customer);
            booking.setTotalAmount(178.0);
            booking.setTicketCount(2);
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);

            BookingItem bookingItem = new BookingItem();
            bookingItem.setBooking(booking);
            bookingItem.setEvent(techSummit);
            bookingItem.setQuantity(2);
            bookingItem.setUnitPrice(89.0);
            bookingItem.setSubtotal(178.0);
            bookingItemRepository.save(bookingItem);

            attendeeRepository.save(Attendee.builder()
                    .booking(booking)
                    .name("Alex Morgan")
                    .email(SAMPLE_EMAIL)
                    .phone("+1 (555) 234-5678")
                    .build());
        }
    }

    private EventCategory findOrCreateCategory(String name, String description) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(EventCategory.builder()
                        .name(name)
                        .description(description)
                        .status("ACTIVE")
                        .build()));
    }

    private Venue findOrCreateVenue(String name, String address, String city, int capacity) {
        return venueRepository.findAll().stream()
                .filter(venue -> venue.getName().equalsIgnoreCase(name) && venue.getCity().equalsIgnoreCase(city))
                .findFirst()
                .orElseGet(() -> venueRepository.save(Venue.builder()
                        .name(name)
                        .address(address)
                        .city(city)
                        .capacity(capacity)
                        .build()));
    }

    private Event upsertEvent(
            String title,
            String description,
            EventCategory category,
            Venue venue,
            LocalDateTime dateTime,
            double price,
            int totalSeats,
            int availableSeats,
            String imageUrl,
            String organizer,
            boolean featured) {

        return eventRepository.findByTitleContainingIgnoreCase(title).stream()
                .filter(event -> event.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .map(existingEvent -> {
                    existingEvent.setDescription(description);
                    existingEvent.setCategory(category);
                    existingEvent.setVenue(venue);
                    existingEvent.setDateTime(dateTime);
                    existingEvent.setPrice(price);
                    existingEvent.setTotalSeats(totalSeats);
                    existingEvent.setAvailableSeats(availableSeats);
                    existingEvent.setImageUrl(imageUrl);
                    existingEvent.setOrganizer(organizer);
                    existingEvent.setFeatured(featured);
                    existingEvent.setStatus("UPCOMING");
                    return eventRepository.save(existingEvent);
                })
                .orElseGet(() -> eventRepository.save(Event.builder()
                        .title(title)
                        .description(description)
                        .category(category)
                        .venue(venue)
                        .dateTime(dateTime)
                        .price(price)
                        .totalSeats(totalSeats)
                        .availableSeats(availableSeats)
                        .imageUrl(imageUrl)
                        .organizer(organizer)
                        .featured(featured)
                        .status("UPCOMING")
                        .build()));
    }
}