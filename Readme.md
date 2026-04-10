# BiblioTech - Library Management System

## Installation & Setup
1. **Prerequisites:** Install Java JDK 17+ and IntelliJ IDEA.
2. **Setup:** Open this project in IntelliJ and ensure JavaFX libraries are configured.
3. **Database:** The database (`bibliotech.db`) will be automatically created when you run the app.
4. **Run:** Right-click `Launcher.java` in `com.buet.bibliotech` and select **Run**.

## ✨ Key Features & Functionalities

### 👤 Dual-Role Authentication System
- **Administrative Portal:** A centralized control center for library staff to oversee all operations.
- **Member Access:** A personalized dashboard for students/members to manage their borrowed books and profiles.

### 📚 Advanced Inventory Management
- **Comprehensive Cataloging:** Effortlessly add, update, and remove books from the system.
- **Multi-Copy Tracking:** Integrated logic to track total vs. available copies in real-time.
- **Visual Engagement:** Supports book cover images for a modern and intuitive user experience.
- **Dynamic Search:** High-performance search functionality to find books by ID, Title, or Author instantly.

### 🔄 Efficient Transaction & Return Logic
- **Seamless Issuing:** Streamlined book-borrowing process with automatic stock updates.
- **Two-Step Return Verification:** An innovative "Appeal and Accept" system where members request returns and admins verify them, ensuring data integrity.
- **Transaction History:** Keep a detailed record of issued books and pending return requests.

### 💬 Integrated Communication Hub
- **Bi-directional Messaging:** A real-time chat interface bridging the gap between Library Admin and Members.
- **Intensive Chat Logs:** Database-driven message persistence allowing users to access their conversation history anytime.
- **Smart UI Design:** Conditional message styling for an enhanced "Messenger-like" chatting experience.

### 📊 Data Visualization & Analytics
- **Inventory Insights:** Visual representation of book distribution by category using interactive Pie Charts.
- **Borrowing Trends:** A Bar Chart analysis of top borrowers to identify active library users.
- **Real-time Statistics:** Instant dashboard updates for Total Books, Active Members, and Issued Items.

### 🛡️ Robust Security & Profile Customization
- **ID Uniqueness Validation:** A failsafe mechanism to ensure every Member ID remains unique during registration and updates.
- **Self-Service Profiling:** Empower members to update their personal information and credentials directly.
- **Data Integrity:** Built with SQLite and Prepared Statements to prevent SQL injection and ensure consistent data storage.