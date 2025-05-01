package propensi.amesta.service.Shipping;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import propensi.amesta.payload.response.Shipping.ShippingDocumentResponseDTO;
import propensi.amesta.payload.response.Shipping.ShippingDocumentItemResponseDTO;

@Service
public class PDFExportService {

    @Autowired
    private ShippingDocumentService shippingDocumentService;
    
    public byte[] generateShippingDocumentPDF(String shippingDocumentId) throws DocumentException {
        ShippingDocumentResponseDTO shippingDocument = shippingDocumentService.getShippingDocumentById(shippingDocumentId);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        
        document.open();
        
        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        titleFont.setSize(18);
        Paragraph title = new Paragraph("SURAT JALAN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Add space
        document.add(new Paragraph(" "));
        
        // Add document details
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA);
        normalFont.setSize(12);
        
        // Document info
        document.add(new Paragraph("No. Dokumen: " + shippingDocument.getId(), normalFont));
        document.add(new Paragraph("Tanggal: " + shippingDocument.getDocumentDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), normalFont));
        
        // Add space
        document.add(new Paragraph(" "));
        
        // Reference order info
        if (shippingDocument.getPurchaseOrderId() != null) {
            document.add(new Paragraph("Referensi Purchase Order: " + shippingDocument.getPurchaseOrderId(), normalFont));
        }
        
        if (shippingDocument.getSalesOrderId() != null) {
            document.add(new Paragraph("Referensi Sales Order: " + shippingDocument.getSalesOrderId(), normalFont));
        }
        
        // Add space
        document.add(new Paragraph(" "));
        
        // Customer info
        document.add(new Paragraph("KEPADA:", normalFont));
        document.add(new Paragraph("Nama: " + shippingDocument.getCustomerName(), normalFont));
        document.add(new Paragraph("Alamat: " + shippingDocument.getShippingAddress(), normalFont));
        document.add(new Paragraph("Penerima: " + shippingDocument.getRecipientName(), normalFont));
        
        // Add space
        document.add(new Paragraph(" "));
        
        // Items table
        document.add(new Paragraph("DAFTAR BARANG:", normalFont));
        document.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        
        // Set column widths
        float[] columnWidths = {1f, 4f, 2f, 1.5f, 2f};
        table.setWidths(columnWidths);
        
        // Add table headers
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        headerFont.setSize(12);
        
        PdfPCell headerCell;
        
        headerCell = new PdfPCell(new Phrase("No.", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(8);
        table.addCell(headerCell);
        
        headerCell = new PdfPCell(new Phrase("Nama Barang", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(8);
        table.addCell(headerCell);
        
        headerCell = new PdfPCell(new Phrase("Merk", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(8);
        table.addCell(headerCell);
        
        headerCell = new PdfPCell(new Phrase("Jumlah", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(8);
        table.addCell(headerCell);
        
        headerCell = new PdfPCell(new Phrase("Gudang", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(8);
        table.addCell(headerCell);
        
        // Add item rows
        int itemNumber = 1;
        for (ShippingDocumentItemResponseDTO item : shippingDocument.getItems()) {
            PdfPCell cell;
            
            cell = new PdfPCell(new Phrase(String.valueOf(itemNumber++), normalFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase(item.getBarangNama(), normalFont));
            cell.setPadding(6);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase(item.getBarangMerk(), normalFont));
            cell.setPadding(6);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase(item.getGudangNama(), normalFont));
            cell.setPadding(6);
            table.addCell(cell);
        }
        
        document.add(table);
        
        // Add space
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        // Add notes
        if (shippingDocument.getNotes() != null && !shippingDocument.getNotes().isEmpty()) {
            document.add(new Paragraph("Catatan:", normalFont));
            document.add(new Paragraph(shippingDocument.getNotes(), normalFont));
            document.add(new Paragraph(" "));
        }
        
        // Add signature sections
        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        
        // Pengirim
        PdfPCell signatureCell = new PdfPCell(new Phrase("Pengirim,", normalFont));
        signatureCell.setBorder(0);
        signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureCell.setPadding(10);
        signatureTable.addCell(signatureCell);
        
        // Penerima
        signatureCell = new PdfPCell(new Phrase("Penerima,", normalFont));
        signatureCell.setBorder(0);
        signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureCell.setPadding(10);
        signatureTable.addCell(signatureCell);
        
        // Mengetahui
        signatureCell = new PdfPCell(new Phrase("Mengetahui,", normalFont));
        signatureCell.setBorder(0);
        signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureCell.setPadding(10);
        signatureTable.addCell(signatureCell);
        
        // Space for signatures
        for (int i = 0; i < 3; i++) {
            PdfPCell spaceCell = new PdfPCell(new Phrase(" ", normalFont));
            spaceCell.setBorder(0);
            spaceCell.setFixedHeight(60);
            signatureTable.addCell(spaceCell);
        }
        
        // Name lines
        for (int i = 0; i < 3; i++) {
            PdfPCell lineCell = new PdfPCell(new Phrase("(____________________)", normalFont));
            lineCell.setBorder(0);
            lineCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            signatureTable.addCell(lineCell);
        }
        
        document.add(signatureTable);
        
        document.close();
        
        return byteArrayOutputStream.toByteArray();
    }
}