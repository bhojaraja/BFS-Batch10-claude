# Product Catalog Management System - User Stories, Acceptance Criteria & Edge Cases

---

## 1. ADD PRODUCT FEATURE

### User Stories

#### US-001: Catalog Manager Adds a New Product
**As a** catalog manager  
**I want to** add a new product to the system with all required information  
**So that** customers can browse and purchase the new product

#### US-002: Prevent Duplicate Product IDs
**As a** system administrator  
**I want to** ensure that duplicate product IDs cannot be added  
**So that** each product has a unique identifier

#### US-003: Validate Product Information
**As a** quality assurance manager  
**I want to** ensure that only valid product information is stored  
**So that** the catalog maintains data integrity

---

### Acceptance Criteria for US-001 (Add New Product)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-001.1 | User can enter product ID | Product ID field accepts alphanumeric input |
| AC-001.2 | User can enter product name | Product name field accepts text up to 255 characters |
| AC-001.3 | User can enter product description | Description field accepts text up to 1000 characters |
| AC-001.4 | User can select product category | Category dropdown displays all available categories |
| AC-001.5 | User can enter product price | Price field accepts decimal numbers with 2 decimal places |
| AC-001.6 | User can enter stock quantity | Stock quantity field accepts non-negative integers |
| AC-001.7 | Form validation triggers for empty mandatory fields | Error message displayed: "Name, Category, and Price are required" |
| AC-001.8 | Product is successfully created | System displays confirmation: "Product [ID] created successfully" |
| AC-001.9 | Product data is persisted | Product is saved to database and retrievable |
| AC-001.10 | User receives success notification | Confirmation message with product details is displayed |

### Acceptance Criteria for US-002 (Prevent Duplicates)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-002.1 | System checks for duplicate ID on submission | Duplicate check performed before database write |
| AC-002.2 | Duplicate ID is rejected | Error message: "Product ID already exists" |
| AC-002.3 | User can re-enter different ID | Form remains available for correction |
| AC-002.4 | System prevents silent failure | No product is created if ID exists |

### Acceptance Criteria for US-003 (Validate Product Information)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-003.1 | Price must be positive | Price < 0 shows error: "Price must be greater than 0" |
| AC-003.2 | Stock quantity must be non-negative | Stock < 0 shows error: "Stock cannot be negative" |
| AC-003.3 | Name cannot be empty | Empty name shows error: "Product name is required" |
| AC-003.4 | Category must be selected | No category shows error: "Please select a category" |
| AC-003.5 | ID format is validated | Invalid ID format rejected with appropriate message |

---

### Edge Cases for Add Product

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-001 | SQL Injection attempt in name | User enters `'; DROP TABLE products; --` | Input is sanitized; treated as literal string |
| EC-002 | XSS attempt in description | User enters `<script>alert('xss')</script>` | Script tags escaped; displayed as text |
| EC-003 | Very large product name | User enters 5000 character name | Truncated to 255 chars; warning message shown |
| EC-004 | Decimal precision in price | User enters 99.999 | Rounded to 99.99 or rejected with message |
| EC-005 | Negative price entry | User enters -50.00 | Rejected with error message |
| EC-006 | Non-numeric stock quantity | User enters "100abc" | Rejected; only integers accepted |
| EC-007 | Zero price | User enters 0.00 | Either accepted (free product) or rejected based on business rules |
| EC-008 | Maximum integer stock | User enters 2147483647 | Accepted if within database limits; error if exceeded |
| EC-009 | Unicode characters in name | User enters "产品名称" | Accepted if system supports UTF-8 |
| EC-010 | Special characters in ID | User enters "PROD-2024/001" | Accepted or rejected based on ID format rules |
| EC-011 | Concurrent duplicate submissions | Two users submit same ID simultaneously | Only one succeeds; second receives duplicate error |
| EC-012 | Network timeout during creation | Connection drops mid-submission | Transaction rolled back; user notified to retry |
| EC-013 | Empty description field | User leaves description blank | Accepted; description is optional |
| EC-014 | Very long category name | System has category with 500 char name | Displayed correctly without truncation |
| EC-015 | Whitespace-only name | User enters "     " | Rejected as invalid/empty name |

---

## 2. VIEW PRODUCTS FEATURE

### User Stories

#### US-004: Browse All Products
**As a** customer or catalog viewer  
**I want to** see all products in the catalog  
**So that** I can explore what is available

#### US-005: View Product Details
**As a** customer  
**I want to** see complete information about each product  
**So that** I can make informed purchasing decisions

#### US-006: Navigate Large Product Lists
**As a** user  
**I want to** view products with pagination  
**So that** the page loads quickly without overwhelming data

#### US-007: Sort Products Efficiently
**As a** user  
**I want to** sort products by different fields  
**So that** I can find products according to my preferences

---

### Acceptance Criteria for US-004 (Browse All Products)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-004.1 | View All Products page loads | Page displays within 2 seconds |
| AC-004.2 | All products are displayed | Every product in database is shown (with pagination) |
| AC-004.3 | Products are in a readable format | Table or card layout with clear columns/sections |
| AC-004.4 | Empty catalog is handled | Message: "No products available" when catalog is empty |
| AC-004.5 | Product count is displayed | Shows "Displaying X of Y products" |

### Acceptance Criteria for US-005 (View Product Details)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-005.1 | Product ID is displayed | ID clearly visible |
| AC-005.2 | Product name is displayed | Name shown in readable format |
| AC-005.3 | Description is displayed | Full description text is shown |
| AC-005.4 | Category is displayed | Category name is visible |
| AC-005.5 | Price is formatted correctly | Price shown as currency (e.g., $99.99) |
| AC-005.6 | Stock quantity is displayed | Current stock level shown |
| AC-005.7 | Stock status indicator | Out of stock/Low stock warning if applicable |
| AC-005.8 | Last updated timestamp | Shows when product was last modified |

### Acceptance Criteria for US-006 (Navigate Large Lists)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-006.1 | Default page size is set | First page shows 10/25/50 products (configurable) |
| AC-006.2 | Pagination controls are visible | Previous/Next buttons and page number inputs shown |
| AC-006.3 | User can go to specific page | Jump to page X by entering page number |
| AC-006.4 | Page size can be changed | User can select 10, 25, 50, or 100 items per page |
| AC-006.5 | Current page is highlighted | Active page button is visually distinct |
| AC-006.6 | Navigation at top and bottom | Pagination controls visible at both ends |
| AC-006.7 | Total pages count displayed | Shows "Page 2 of 15" |

### Acceptance Criteria for US-007 (Sort Products)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-007.1 | Sort by name | Products sorted alphabetically (A-Z or Z-A) |
| AC-007.2 | Sort by price | Products sorted by price (low-to-high or high-to-low) |
| AC-007.3 | Sort by category | Products grouped and sorted by category |
| AC-007.4 | Sort by stock quantity | Products sorted by stock level |
| AC-007.5 | Default sort order | Products sorted by creation date or ID by default |
| AC-007.6 | Sort direction toggle | User can reverse sort order with single click |
| AC-007.7 | Sort persistence | Selected sort maintained when paginating |

---

### Edge Cases for View Products

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-016 | Empty product list | No products in database | Display "No products available" message |
| EC-017 | Single product in catalog | Only one product exists | Display that product; disable pagination |
| EC-018 | Exactly one page of products | Exactly 25 products with page size 25 | Show pagination but disable next button |
| EC-019 | Very large catalog | 1,000,000 products in system | Load times remain < 2 seconds with pagination |
| EC-020 | Special characters in product name | Product name contains "A&B <Company>" | Display correctly without HTML interpretation |
| EC-021 | Very long product description | Description is 1000+ characters | Display with text wrapping or truncation with "Read More" |
| EC-022 | Missing optional fields | Product has null description | Display as "N/A" or empty field |
| EC-023 | Zero stock products | Some products have 0 stock | Clearly marked as "Out of Stock" |
| EC-024 | Very large price | Product costs $999,999.99 | Displayed correctly in currency format |
| EC-025 | Concurrent view and update | User viewing products while another updates | User sees consistent data; refresh shows updates |
| EC-026 | Sort with pagination | User sorts and pages through results | Sort order consistent across all pages |
| EC-027 | Invalid page number | User requests page 1000 when only 50 pages exist | Redirect to last page or show error |
| EC-028 | Floating point display | Price is 19.99999 in database | Rounded to 19.99 for display |
| EC-029 | Case sensitivity in sorting | Sorting names with mixed case | Consistent alphabetical sorting regardless of case |
| EC-030 | Categories with special characters | Category is "Health & Wellness" | Displayed and sorted correctly |

---

## 3. SEARCH PRODUCT BY ID FEATURE

### User Stories

#### US-008: Search for Specific Product
**As a** customer or staff member  
**I want to** search for a product by its ID  
**So that** I can quickly find the product I'm looking for

#### US-009: Get Immediate Feedback on Search
**As a** user  
**I want to** know immediately if a product exists or not  
**So that** I can take appropriate action

---

### Acceptance Criteria for US-008 (Search by ID)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-008.1 | Search field accepts user input | ID field accepts text and numbers |
| AC-008.2 | Search triggers on button click | "Search" button initiates search |
| AC-008.3 | Product details displayed | Complete product info shown when ID found |
| AC-008.4 | Search is case-insensitive | "Prod-001" and "PROD-001" find same product |
| AC-008.5 | Whitespace is trimmed | " Prod-001 " finds same as "Prod-001" |
| AC-008.6 | Search result loads quickly | Results displayed within 500ms |
| AC-008.7 | Search field has placeholder | Shows hint text like "Enter Product ID" |

### Acceptance Criteria for US-009 (Search Feedback)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-009.1 | Product found - success message | Display: "Product found" with details |
| AC-009.2 | Product not found - error message | Display: "Product ID not found" |
| AC-009.3 | Empty search - validation message | Display: "Please enter a product ID" |
| AC-009.4 | Search button disabled when empty | Button grayed out until ID entered |
| AC-009.5 | Clear button available | "Clear" button resets search field |

---

### Edge Cases for Search by ID

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-031 | Empty search string | User clicks search with empty field | Error: "Product ID is required" |
| EC-032 | Whitespace only | User enters only spaces | Treated as empty; error message shown |
| EC-033 | Special characters in ID | User searches for "PROD@#$%" | No match found or error if invalid format |
| EC-034 | Very long ID string | User enters 10000 character ID | Truncated or error; no match found |
| EC-035 | SQL injection in search | User enters "' OR '1'='1" | Safely handled; treated as literal ID |
| EC-036 | Exact ID match required | Searching "PROD" when ID is "PROD-001" | No match found; partial match not returned |
| EC-037 | Case variations | DB has "Prod-001"; search "prod-001" | Found if case-insensitive search enabled |
| EC-038 | Leading zeros | ID "00123" vs "123" | Behavior depends on data type (string vs number) |
| EC-039 | Unicode ID | ID contains non-ASCII characters | Found if database supports UTF-8 |
| EC-040 | Product deleted during search | Product exists in cache but deleted from DB | "Product not found" displayed |
| EC-041 | Rapid successive searches | User rapidly clicks search button | Handled gracefully; no duplicate queries |
| EC-042 | Search with network latency | Network slow; search takes 5 seconds | Loading indicator shown; not timeout |
| EC-043 | Multiple matches | Duplicate IDs exist (data integrity issue) | First match returned or all matches shown |
| EC-044 | ID with hyphens | Searching "123-456" in system | Exact match required; "123456" not found |
| EC-045 | Deleted product in search history | User searches for recently deleted product | Confirms deletion; shows "Product not found" |

---

## 4. SEARCH PRODUCTS BY CATEGORY FEATURE

### User Stories

#### US-010: Browse Products by Category
**As a** customer  
**I want to** view all products in a specific category  
**So that** I can explore products relevant to my interests

#### US-011: See Category Options
**As a** user  
**I want to** see available product categories  
**So that** I can choose which category to explore

#### US-012: Handle Empty Categories
**As a** user  
**I want to** know when a category has no products  
**So that** I don't waste time searching

---

### Acceptance Criteria for US-010 (Browse by Category)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-010.1 | Category filter/dropdown available | Dropdown or list of categories displayed |
| AC-010.2 | User selects a category | Clicking category shows all products in it |
| AC-010.3 | All products in category displayed | Complete list with pagination |
| AC-010.4 | Product details fully visible | All fields shown for each product |
| AC-010.5 | Category filter is persistent | Selected category remains after pagination |
| AC-010.6 | Category name is clear | Selected category displayed at top of results |
| AC-010.7 | Product count shown | Displays "X products in [Category]" |

### Acceptance Criteria for US-011 (View Category Options)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-011.1 | Category list loads | All categories displayed within 1 second |
| AC-011.2 | Categories are alphabetically sorted | Categories listed in A-Z order |
| AC-011.3 | Category count shown | Displays number of products in each category |
| AC-011.4 | Disabled categories handled | Empty categories shown but marked differently |
| AC-011.5 | Search within categories | Optional search to filter categories |

### Acceptance Criteria for US-012 (Handle Empty Categories)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-012.1 | Empty category message | Display: "No products in this category" |
| AC-012.2 | Suggestion provided | Show: "Browse other categories" with link |
| AC-012.3 | Empty state is graceful | No errors; clean message display |
| AC-012.4 | Back button available | Easy navigation to category list |

---

### Edge Cases for Search by Category

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-046 | Empty category | Category exists but has 0 products | Show "No products" message; category still selectable |
| EC-047 | Very large category | Category with 100,000+ products | Use pagination; load times remain acceptable |
| EC-048 | Category deleted | User has bookmarked category that's now deleted | Show error; suggest browsing other categories |
| EC-049 | Special characters in category | Category name is "Men's & Women's" | Displayed and searched correctly |
| EC-050 | Very long category name | Category name is 500+ characters | Displayed with text wrapping or truncation |
| EC-051 | Category with spaces | Category is "Baby & Kids" with leading/trailing spaces | Trimmed automatically; not treated as different |
| EC-052 | Case sensitivity in category | "Electronics" vs "electronics" | Treated as same category (case-insensitive) |
| EC-053 | Duplicate categories | Two categories have same name | One treated as canonical; other hidden or merged |
| EC-054 | Product in multiple categories | Product assigned to 2+ categories | Product appears in results for each category |
| EC-055 | No categories defined | System has no categories | Show message: "Categories not available"; browse all |
| EC-056 | Category sort changed during browse | Admin changes category sort while user browsing | User sees consistent experience; refresh shows new order |
| EC-057 | Filter AND pagination | User filters category and pages through results | Sort order consistent across all pages |
| EC-058 | Case variation in filter | Filter by "electronics"; product category "Electronics" | Match found if case-insensitive comparison |
| EC-059 | Product moved to different category | Product category changed while user viewing | User sees old category; refresh shows new location |
| EC-060 | Unicode category names | Category is "衣服" (Chinese for "clothes") | Displayed and searched correctly |

---

## 5. UPDATE PRODUCT FEATURE

### User Stories

#### US-013: Modify Existing Product
**As a** catalog manager  
**I want to** update product information (name, price, stock, etc.)  
**So that** the catalog stays current and accurate

#### US-014: Partial Updates
**As a** staff member  
**I want to** update only specific product fields  
**So that** I don't accidentally change other information

#### US-015: Prevent Concurrent Update Conflicts
**As a** system administrator  
**I want to** prevent data loss from simultaneous updates  
**So that** the latest valid change is preserved

---

### Acceptance Criteria for US-013 (Modify Product)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-013.1 | Edit product form loads | Form pre-populated with current product data |
| AC-013.2 | User can modify name | Name field is editable |
| AC-013.3 | User can modify description | Description field is editable |
| AC-013.4 | User can modify category | Category dropdown allows selection change |
| AC-013.5 | User can modify price | Price field is editable with validation |
| AC-013.6 | User can modify stock | Stock quantity field is editable with validation |
| AC-013.7 | Updated data is saved | Product updated in database with confirmation |
| AC-013.8 | Update confirmation displayed | Show: "Product [ID] updated successfully" |
| AC-013.9 | Update timestamp recorded | Last modified time updated |
| AC-013.10 | Validation applied to changes | Same validation rules as product creation |

### Acceptance Criteria for US-014 (Partial Updates)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-014.1 | Only changed fields are updated | Unchanged fields retain their values |
| AC-014.2 | Empty optional fields allowed | Can clear description without affecting other fields |
| AC-014.3 | Mandatory fields still required | Cannot leave required fields empty |
| AC-014.4 | Partial update validation | Only changed fields are re-validated |

### Acceptance Criteria for US-015 (Prevent Conflicts)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-015.1 | Optimistic locking implemented | Product version tracked for concurrency |
| AC-015.2 | Simultaneous update detected | Error: "Product was modified; please refresh" |
| AC-015.3 | User can view latest changes | Show current product state and option to retry |
| AC-015.4 | First update wins | First successful update is preserved |
| AC-015.5 | Second update fails | Second user receives conflict notification |

---

### Edge Cases for Update Product

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-061 | Product doesn't exist | User tries to update deleted product | Error: "Product not found" |
| EC-062 | ID cannot be changed | User attempts to change product ID | Field locked/read-only; ID unchangeable |
| EC-063 | Negative price in update | User changes price to -50 | Rejected; error message shown |
| EC-064 | Negative stock in update | User changes stock to -100 | Rejected; error message shown |
| EC-065 | SQL injection in name update | User enters malicious SQL in name field | Input sanitized; stored as literal string |
| EC-066 | XSS attempt in description | User enters script tags in description | Tags escaped; displayed as text |
| EC-067 | Very large decimal price | Price changed to 999999999.99 | Accepted or rejected based on DB limits |
| EC-068 | Concurrent updates - same field | Two users both change product price | Last update wins or conflict detection applies |
| EC-069 | Concurrent updates - different fields | User A changes price; User B changes stock | Both updates succeed; data consistency maintained |
| EC-070 | Network failure mid-update | Connection drops during save | Transaction rolled back; user prompted to retry |
| EC-071 | Update with same values | User saves without making changes | Accepted; update recorded (or silently rejected) |
| EC-072 | Category deleted | Product category deleted while user editing | Category selection shows deleted state or error |
| EC-073 | Update to empty string | User clears optional field | Accepted if field is optional |
| EC-074 | Very long updated name | User changes name to 5000 characters | Truncated to 255 or error shown |
| EC-075 | Update timestamp on concurrent view | Product updated while being viewed | Refresh shows new data; no stale information |

---

## 6. DELETE PRODUCT FEATURE

### User Stories

#### US-016: Remove Products from Catalog
**As a** catalog manager  
**I want to** delete products that are no longer available  
**So that** the catalog only shows current offerings

#### US-017: Prevent Accidental Deletion
**As a** user  
**I want to** confirm before deleting a product  
**So that** I don't accidentally remove products

#### US-018: Handle Product Dependencies
**As a** system administrator  
**I want to** safely delete products without breaking references  
**So that** data integrity is maintained

---

### Acceptance Criteria for US-016 (Remove Products)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-016.1 | Delete option available | Delete button/link visible on product detail |
| AC-016.2 | User initiates deletion | Click delete button to start process |
| AC-016.3 | Product removed from database | Product no longer accessible after deletion |
| AC-016.4 | Delete confirmation displayed | Show: "Product [ID] deleted successfully" |
| AC-016.5 | Redirect after deletion | User redirected to product list after delete |
| AC-016.6 | Deletion logged | Delete operation recorded in audit trail |

### Acceptance Criteria for US-017 (Confirmation Dialog)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-017.1 | Confirmation dialog appears | Modal/popup asks to confirm deletion |
| AC-017.2 | Product details shown | Display product ID and name in confirmation |
| AC-017.3 | Warning message displayed | Show: "This action cannot be undone" |
| AC-017.4 | Cancel option available | "Cancel" button closes dialog without deleting |
| AC-017.5 | Confirm button available | "Delete" button requires explicit confirmation |
| AC-017.6 | Visual warning | Red colors or warning icon used in dialog |

### Acceptance Criteria for US-018 (Handle Dependencies)

| # | Criteria | Expected Result |
|---|----------|-----------------|
| AC-018.1 | Check for references | System checks if product referenced in orders |
| AC-018.2 | Warn if in use | Display: "Product has X active orders; delete anyway?" |
| AC-018.3 | Option to cancel | User can cancel if not prepared for cascade |
| AC-018.4 | Soft delete option | Option to archive instead of permanently delete |
| AC-018.5 | Force delete allowed | Admin can force delete with extra confirmation |

---

### Edge Cases for Delete Product

| # | Edge Case | Scenario | Expected Behavior |
|---|-----------|----------|-------------------|
| EC-076 | Delete non-existent product | User attempts to delete already-deleted product | Error: "Product not found"; user notified |
| EC-077 | Confirm dialog timeout | Dialog stays open for 10 minutes | Gracefully time out; require reconfirmation |
| EC-078 | Product in active orders | Product being purchased in order | Show warning; allow deletion with cascade or soft delete |
| EC-079 | Product in shopping carts | Product currently in user shopping carts | Show warning; allow deletion; carts updated |
| EC-080 | Concurrent delete attempts | Two users delete same product simultaneously | First succeeds; second gets "not found" error |
| EC-081 | Network failure during delete | Connection drops after confirmation | Transaction rolled back; user prompted to retry |
| EC-082 | Accidental confirmation | User clicks delete multiple times | Single delete executed; second click shows error |
| EC-083 | Product deleted by another user | User deletes product; another user also deletes | Second user receives "Product not found" error |
| EC-084 | Delete with audit trail requirement | System must keep audit log of deletions | Soft delete performed; hard delete scheduled later |
| EC-085 | Insufficient permissions | Regular user attempts to delete | Error: "Permission denied"; only admins can delete |
| EC-086 | Reverse/Undo deletion | User wants to undo recent deletion | Undo available within 24 hours; soft delete used |
| EC-087 | Delete locked product | Product marked as locked by admin | Error: "Product is locked; cannot delete" |
| EC-088 | Cascading deletes | Delete product with 100 related orders | All references handled; system remains consistent |
| EC-089 | Backup before deletion | Ensure data backup before deletion | Backup triggered automatically; user can restore |
| EC-090 | Delete impact on reports | Reports include deleted products | Reports updated; deleted products excluded from future reports |

---

## Summary Table

### Total Count by Feature

| Feature | User Stories | Acceptance Criteria | Edge Cases |
|---------|--------------|-------------------|-----------|
| Add Product | 3 | 13 | 15 |
| View Products | 4 | 21 | 15 |
| Search by ID | 2 | 12 | 15 |
| Search by Category | 3 | 16 | 15 |
| Update Product | 3 | 17 | 15 |
| Delete Product | 3 | 16 | 15 |
| **TOTAL** | **18** | **95** | **90** |

### Grand Total: 203 items (18 User Stories + 95 Acceptance Criteria + 90 Edge Cases)

---

**Document Version:** 1.0  
**Last Updated:** 2026-08-24  
**Status:** Complete
