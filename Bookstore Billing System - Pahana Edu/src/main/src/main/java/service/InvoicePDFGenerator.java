package service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;
import model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Complete InvoicePDFGenerator with Orange/Brown Theme and Logo Support
 * 
 * LOGO PLACEMENT:
 * Place your logo at: webapp/assets/Logo.png
 * Recommended size: 200x200 pixels or smaller
 * Supported formats: PNG, JPG, GIF
 */
public class InvoicePDFGenerator {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    
    // ORANGE/BROWN THEME COLOR SCHEME
    private static final BaseColor PRIMARY_COLOR = new BaseColor(216, 108, 54);   // #D86C36 - Main orange
    private static final BaseColor PRIMARY_DARK = new BaseColor(196, 85, 44);     // #C4552C - Darker orange  
    private static final BaseColor PRIMARY_DARKER = new BaseColor(166, 63, 34);   // #A63F22 - Darkest brown
    private static final BaseColor ACCENT_COLOR = new BaseColor(242, 162, 63);    // #f2a23f - Light orange/yellow
    private static final BaseColor BACKGROUND_LIGHT = new BaseColor(242, 231, 220); // #F2E7DC - Light cream

    // Supporting colors
    private static final BaseColor TEXT_COLOR = new BaseColor(31, 41, 55);        // #1f2937 - Dark text
    private static final BaseColor LIGHT_GRAY = new BaseColor(243, 244, 246);     // #f3f4f6 - Light background
    private static final BaseColor MEDIUM_GRAY = new BaseColor(156, 163, 175);    // #9ca3af - Medium gray
    private static final BaseColor SUCCESS_GREEN = new BaseColor(16, 185, 129);   // #10b981 - Keep for cash/success indicators
    private static final BaseColor WHITE = BaseColor.WHITE;
    
    /**
     * Generate invoice PDF with InputStream logo support (MAIN METHOD FOR PRINTING)
     */
    public static void generateInvoicePDFWithStream(InvoiceDTO invoice, HttpServletResponse response, InputStream logoStream) 
            throws DocumentException, IOException {
        
        System.out.println("Starting PDF generation with stream for invoice: " + invoice.getId());
        
        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"invoice-" + invoice.getId() + ".pdf\"");
        
        // Create document
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        try {
            // Add content sections
            addHeaderWithStream(document, invoice, logoStream);
            addCustomerInfo(document, invoice);
            addInvoiceItems(document, invoice);
            addTotals(document, invoice);
            addFooter(document, invoice);
            
            System.out.println("PDF content added successfully");
            
        } finally {
            document.close();
            System.out.println("PDF document closed");
        }
    }
    
    /**
     * Generate invoice PDF to ByteArrayOutputStream for email attachment
     */
    public static void generateInvoicePDFToStream(InvoiceDTO invoice, ByteArrayOutputStream outputStream, InputStream logoStream) 
            throws Exception {
        
        System.out.println("Generating PDF to stream for email attachment: " + invoice.getId());
        
        // Create document
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        try {
            // Add content sections - same as print version
            addHeaderWithStream(document, invoice, logoStream);
            addCustomerInfo(document, invoice);
            addInvoiceItems(document, invoice);
            addTotals(document, invoice);
            addFooter(document, invoice);
            
            System.out.println("PDF content added to stream successfully");
            
        } finally {
            document.close();
            System.out.println("PDF stream document closed");
        }
    }
    
    /**
     * Generate invoice PDF with orange theme (backward compatibility)
     */
    public static void generateInvoicePDF(InvoiceDTO invoice, HttpServletResponse response) 
            throws DocumentException, IOException {
        generateInvoicePDFWithStream(invoice, response, null);
    }
    
    /**
     * Add header section with logo from InputStream
     */
    private static void addHeaderWithStream(Document document, InvoiceDTO invoice, InputStream logoStream) throws DocumentException {
        System.out.println("Adding header with logo stream...");
        
        // Company Header with Logo
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1.2f, 2.5f, 2f}); // Logo | Company Info | Invoice Info
        
        // LOGO CELL
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setPadding(5);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        try {
            if (logoStream != null) {
                System.out.println("Loading logo from InputStream...");
                
                // Read the stream into bytes
                byte[] logoBytes = logoStream.readAllBytes();
                logoStream.close();
                
                System.out.println("Logo bytes read: " + logoBytes.length + " bytes");
                
                // Create image from bytes
                Image logo = Image.getInstance(logoBytes);
                
                // Scale logo to fit
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_CENTER);
                
                // Add orange border around logo
                PdfPTable logoContainer = new PdfPTable(1);
                PdfPCell logoWrapper = new PdfPCell();
                logoWrapper.setBorder(Rectangle.BOX);
                logoWrapper.setBorderColor(PRIMARY_COLOR);
                logoWrapper.setBorderWidth(2);
                logoWrapper.setPadding(5);
                logoWrapper.setHorizontalAlignment(Element.ALIGN_CENTER);
                logoWrapper.addElement(logo);
                logoContainer.addCell(logoWrapper);
                
                logoCell.addElement(logoContainer);
                System.out.println("Logo added successfully to PDF");
                
            } else {
                throw new Exception("Logo stream is null");
            }
            
        } catch (Exception e) {
            // Logo fallback - styled placeholder
            System.out.println("Logo loading failed, using placeholder: " + e.getMessage());
            
            PdfPTable placeholderTable = new PdfPTable(1);
            PdfPCell placeholderCell = new PdfPCell();
            placeholderCell.setBackgroundColor(BACKGROUND_LIGHT);
            placeholderCell.setBorder(Rectangle.BOX);
            placeholderCell.setBorderColor(PRIMARY_COLOR);
            placeholderCell.setBorderWidth(2);
            placeholderCell.setPadding(20);
            placeholderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            Paragraph logoPlaceholder = new Paragraph("PAHANA\nBOOKSTORE", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR));
            logoPlaceholder.setAlignment(Element.ALIGN_CENTER);
            placeholderCell.addElement(logoPlaceholder);
            
            placeholderTable.addCell(placeholderCell);
            logoCell.addElement(placeholderTable);
        }
        
        headerTable.addCell(logoCell);
        
        // Company Info Cell
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(10);
        companyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph companyName = new Paragraph("PAHANA BOOKSTORE", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, PRIMARY_DARK));
        companyName.setSpacingAfter(8);
        companyCell.addElement(companyName);
        
        Paragraph address = new Paragraph(
            "123 Book Street, Literary District\n" +
            "Colombo, Sri Lanka\n" +
            "Phone: +94 11 234 5678\n" +
            "Email: info@pahanabookstore.lk", 
            FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR));
        companyCell.addElement(address);
        
        headerTable.addCell(companyCell);
        
        // Invoice Info Cell
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceCell.setPadding(10);
        invoiceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        // Invoice title with orange background
        PdfPTable invoiceTitleTable = new PdfPTable(1);
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(PRIMARY_COLOR);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(12);
        
        Paragraph invoiceTitle = new Paragraph("INVOICE", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, WHITE));
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(invoiceTitle);
        
        invoiceTitleTable.addCell(titleCell);
        invoiceCell.addElement(invoiceTitleTable);
        
        // Invoice details
        Paragraph spacer = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8));
        invoiceCell.addElement(spacer);
        
        Paragraph invoiceNumber = new Paragraph("Invoice #: " + 
            (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : String.valueOf(invoice.getId())), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TEXT_COLOR));
        invoiceNumber.setAlignment(Element.ALIGN_RIGHT);
        invoiceCell.addElement(invoiceNumber);
        
        Date invoiceDate = invoice.getInvoiceDate() != null ? invoice.getInvoiceDate() : invoice.getCreatedAt();
        Paragraph date = new Paragraph("Date: " + DATE_FORMAT.format(invoiceDate), 
            FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_COLOR));
        date.setAlignment(Element.ALIGN_RIGHT);
        invoiceCell.addElement(date);
        
        headerTable.addCell(invoiceCell);
        document.add(headerTable);
        
        // Orange separator line
        addOrangeSeparator(document);
    }
    
    private static void addCustomerInfo(Document document, InvoiceDTO invoice) throws DocumentException {
        // Customer Info Section
        Paragraph customerHeader = new Paragraph("BILL TO:", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_DARK));
        customerHeader.setSpacingAfter(10);
        document.add(customerHeader);
        
        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setWidths(new float[]{1, 1});
        
        // Customer Details
        PdfPCell customerDetailsCell = new PdfPCell();
        customerDetailsCell.setBorder(Rectangle.NO_BORDER);
        customerDetailsCell.setPadding(15);
        customerDetailsCell.setBackgroundColor(BACKGROUND_LIGHT);
        
        ClientDTO client = invoice.getClient();
        if (client != null) {
            Paragraph customerName = new Paragraph(client.getFullName(), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, TEXT_COLOR));
            customerName.setSpacingAfter(5);
            customerDetailsCell.addElement(customerName);
            
            Paragraph phone = new Paragraph("Phone: " + client.getPhone(), 
                FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_COLOR));
            phone.setSpacingAfter(3);
            customerDetailsCell.addElement(phone);
            
            if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
                Paragraph email = new Paragraph("Email: " + client.getEmail(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_COLOR));
                email.setSpacingAfter(3);
                customerDetailsCell.addElement(email);
            }
            
            Paragraph tier = new Paragraph("Tier: " + client.getTierLevel(), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY_COLOR));
            tier.setSpacingAfter(3);
            customerDetailsCell.addElement(tier);
            
            Paragraph points = new Paragraph("Loyalty Points: " + client.getLoyaltyPoints(), 
                FontFactory.getFont(FontFactory.HELVETICA, 11, ACCENT_COLOR));
            customerDetailsCell.addElement(points);
        } else {
            Paragraph walkIn = new Paragraph("Walk-in Customer", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, TEXT_COLOR));
            walkIn.setSpacingAfter(5);
            customerDetailsCell.addElement(walkIn);
            
            Paragraph note = new Paragraph("No loyalty discount applied", 
                FontFactory.getFont(FontFactory.HELVETICA, 11, MEDIUM_GRAY));
            customerDetailsCell.addElement(note);
        }
        
        customerTable.addCell(customerDetailsCell);
        
        // Payment Info
        PdfPCell paymentCell = new PdfPCell();
        paymentCell.setBorder(Rectangle.NO_BORDER);
        paymentCell.setPadding(15);
        paymentCell.setBackgroundColor(BACKGROUND_LIGHT);
        
        if (invoice.getCashGiven() != null && invoice.getCashGiven().doubleValue() > 0) {
            Paragraph paymentHeader = new Paragraph("PAYMENT DETAILS:", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY_DARK));
            paymentHeader.setSpacingAfter(8);
            paymentCell.addElement(paymentHeader);
            
            Paragraph cashGiven = new Paragraph("Cash Given: Rs. " + CURRENCY_FORMAT.format(invoice.getCashGiven()), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, SUCCESS_GREEN));
            cashGiven.setSpacingAfter(3);
            paymentCell.addElement(cashGiven);
            
            if (invoice.getChangeAmount() != null) {
                Paragraph change = new Paragraph("Change: Rs. " + CURRENCY_FORMAT.format(invoice.getChangeAmount()), 
                    FontFactory.getFont(FontFactory.HELVETICA, 11, ACCENT_COLOR));
                paymentCell.addElement(change);
            }
        } else {
            Paragraph paymentMethod = new Paragraph("Payment Method: Cash", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_COLOR));
            paymentCell.addElement(paymentMethod);
        }
        
        customerTable.addCell(paymentCell);
        document.add(customerTable);
        
        addOrangeSeparator(document);
    }
    
    private static void addInvoiceItems(Document document, InvoiceDTO invoice) throws DocumentException {
        // Items Header
        Paragraph itemsHeader = new Paragraph("ITEMS:", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_DARK));
        itemsHeader.setSpacingAfter(10);
        document.add(itemsHeader);
        
        // Items Table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{0.8f, 3f, 1f, 1.5f, 1.5f});
        
        // Table Headers
        addOrangeTableHeader(itemsTable, "#");
        addOrangeTableHeader(itemsTable, "Book Title & Author");
        addOrangeTableHeader(itemsTable, "Qty");
        addOrangeTableHeader(itemsTable, "Unit Price");
        addOrangeTableHeader(itemsTable, "Total");
        
        // Add items with alternating rows
        int itemNumber = 1;
        for (InvoiceItemDTO item : invoice.getItems()) {
            BaseColor rowColor = (itemNumber % 2 == 0) ? LIGHT_GRAY : WHITE;
            
            // Item number
            addTableCellWithBackground(itemsTable, String.valueOf(itemNumber++), rowColor);
            
            // Book details
            String bookDetails = item.getBook() != null ? 
                item.getBook().getTitle() + "\nby " + item.getBook().getAuthor() + 
                "\nISBN: " + item.getBook().getIsbn() :
                "Book ID: " + item.getBookId();
            addTableCellWithBackground(itemsTable, bookDetails, rowColor);
            
            // Quantity
            addTableCellWithBackground(itemsTable, String.valueOf(item.getQuantity()), rowColor);
            
            // Unit Price
            addTableCellWithBackground(itemsTable, "Rs. " + CURRENCY_FORMAT.format(item.getUnitPrice()), rowColor);
            
            // Total Price
            addTableCellWithBackground(itemsTable, "Rs. " + CURRENCY_FORMAT.format(item.getTotalPrice()), rowColor);
        }
        
        document.add(itemsTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 12)));
    }
    
    private static void addTotals(Document document, InvoiceDTO invoice) throws DocumentException {
        // Totals Table
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(60);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{2, 1});
        
        // Subtotal
        addTotalRow(totalsTable, "Subtotal:", "Rs. " + CURRENCY_FORMAT.format(invoice.getSubtotal()), false);
        
        // Discount
        if (invoice.getLoyaltyDiscount() != null && invoice.getLoyaltyDiscount().doubleValue() > 0) {
            String discountLabel = "Discount";
            if (invoice.getClient() != null) {
                discountLabel += " (" + invoice.getClient().getTierLevel() + ")";
            }
            addTotalRow(totalsTable, discountLabel + ":", "-Rs. " + CURRENCY_FORMAT.format(invoice.getLoyaltyDiscount()), false);
        }
        
        // Total Amount - highlighted
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL AMOUNT:", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setBackgroundColor(PRIMARY_COLOR);
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setPadding(12);
        totalsTable.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase("Rs. " + CURRENCY_FORMAT.format(invoice.getTotalAmount()), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setBackgroundColor(PRIMARY_COLOR);
        totalValueCell.setBorder(Rectangle.NO_BORDER);
        totalValueCell.setPadding(12);
        totalsTable.addCell(totalValueCell);
        
        // Points Earned
        if (invoice.getLoyaltyPointsEarned() > 0) {
            addTotalRow(totalsTable, "Points Earned:", String.valueOf(invoice.getLoyaltyPointsEarned()) + " pts", true);
        }
        
        document.add(totalsTable);
    }
    
    private static void addFooter(Document document, InvoiceDTO invoice) throws DocumentException {
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 16)));
        
        // Thank you message
        Paragraph footer = new Paragraph("Thank you for shopping with Pahana Bookstore!", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, PRIMARY_COLOR));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingAfter(10);
        document.add(footer);
        
        Paragraph terms = new Paragraph("All sales are final. For inquiries, please contact us at info@pahanabookstore.lk", 
            FontFactory.getFont(FontFactory.HELVETICA, 9, MEDIUM_GRAY));
        terms.setAlignment(Element.ALIGN_CENTER);
        document.add(terms);
        
        addOrangeSeparator(document);
    }
    
    // Helper Methods
    private static void addOrangeTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE)));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }
    
    private static void addTableCellWithBackground(PdfPTable table, String text, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, 
            FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBackgroundColor(backgroundColor);
        cell.setBorderColor(LIGHT_GRAY);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }
    
    private static void addTotalRow(PdfPTable table, String label, String value, boolean isHighlighted) {
        BaseColor bgColor = isHighlighted ? BACKGROUND_LIGHT : WHITE;
        BaseColor textColor = isHighlighted ? PRIMARY_COLOR : TEXT_COLOR;
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, 
            FontFactory.getFont(FontFactory.HELVETICA, 12, textColor)));
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(bgColor);
        labelCell.setPadding(8);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, textColor)));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBackgroundColor(bgColor);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
    
    private static void addOrangeSeparator(Document document) throws DocumentException {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingBefore(10);
        separator.setSpacingAfter(15);
        
        PdfPCell separatorCell = new PdfPCell();
        separatorCell.setBackgroundColor(PRIMARY_COLOR);
        separatorCell.setBorder(Rectangle.NO_BORDER);
        separatorCell.setFixedHeight(2);
        
        separator.addCell(separatorCell);
        document.add(separator);
    }
}