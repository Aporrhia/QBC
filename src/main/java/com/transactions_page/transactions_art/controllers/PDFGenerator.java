package com.transactions_page.transactions_art.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import com.transactions_page.transactions_art.models.Transaction;
import com.transactions_page.transactions_art.models.Account;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class PDFGenerator {

    public byte[] generatePdf(Transaction transaction, Optional<Account> account) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        long accNum = account.get().getAccNum();


        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Create a new font
            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Font midFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

            // Add an image
            Image image = Image.getInstance("src/main/resources/static/images/Q.png");

            // Scale the image
            float width = 50;
            float height = 50;
            image.scaleToFit(width, height);

            float[] columnWidths = {3f, 2f};
            // Header table - img, Bank, Country
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(columnWidths);
            PdfPTable headerTableCol1 = new PdfPTable(1);
            PdfPTable headerTableCol2 = new PdfPTable(1);

            // Header Column table 1
            headerTableCol1.addCell(removeBordersCell("Quantum Business Center"));
            headerTableCol1.addCell(removeBordersCell("7, Michael Blvd., London"));
            headerTableCol1.addCell(removeBordersCell("United Kingdom, UK"));

            // Header Column table 2
            // Create a SimpleDateFormat object with the desired format
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'of' MMMM, yyyy, HH:mm:ss");
            String formattedDate = sdf.format(new Date());
            PdfPCell dateCell = new PdfPCell(new Phrase(formattedDate, normalFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setBorder(Rectangle.NO_BORDER);
            headerTableCol2.addCell(dateCell);

            PdfPCell AccNumcell = new PdfPCell(new Phrase("Account number: " + accNum, normalFont));
            AccNumcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            AccNumcell.setBorder(Rectangle.NO_BORDER);
            headerTableCol2.addCell(AccNumcell);

            removeBordersFromTable(headerTableCol1);
            removeBordersFromTable(headerTableCol2);

            headerTable.addCell(headerTableCol1);
            headerTable.addCell(headerTableCol2);

            removeBordersFromTable(headerTable);
            headerTable.setSpacingAfter(30f);

            // Company table
            PdfPTable infoTable = new PdfPTable(1);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(70f);

            infoTable.addCell(removeBordersCell("Comapny Name"));
            infoTable.addCell(removeBordersCell("Comapny Address"));
            infoTable.addCell(removeBordersCell("State, Zip"));

            // Logged In account table
            Paragraph accTitle = new Paragraph("Account Details", boldFont);
            accTitle.setSpacingAfter(10f);

            
            Paragraph senderTitle = new Paragraph("SENDER", midFont);


            // Table with breaking line
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            Chunk line = new Chunk(new DottedLineSeparator());
            lineCell.addElement(line);
            lineCell.setBorder(Rectangle.NO_BORDER);
            lineTable.addCell(lineCell);


            PdfPTable accountTable = new PdfPTable(3);
            accountTable.setWidthPercentage(100);

            // Retrieve the sender's Account object from the Transaction
            Account senderAccount = transaction.getSenderAccount();

            // Add the sender's account info to the table
            accountTable.addCell(removeBordersCell("First Name"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(senderAccount.getAccFname(), Element.ALIGN_RIGHT));

            accountTable.addCell(removeBordersCell("Last Name"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(senderAccount.getAccLname(), Element.ALIGN_RIGHT));

            accountTable.addCell(removeBordersCell("Account Status"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(String.valueOf(senderAccount.getStatus()), Element.ALIGN_RIGHT));

            accountTable.addCell(removeBordersCell("IBAN"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(senderAccount.getAccIban(), Element.ALIGN_RIGHT));

            accountTable.addCell(removeBordersCell("Bank"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(senderAccount.getBank().getBankName(), Element.ALIGN_RIGHT));

            accountTable.addCell(removeBordersCell("Credit Card"));
            accountTable.addCell(removeBordersCell(" "));
            accountTable.addCell(createCell(transaction.getSenderCard().getNumber(), Element.ALIGN_RIGHT));

            accountTable.setSpacingAfter(20f);

            
            Paragraph recipientTitle = new Paragraph("RECIPIENT", midFont);

            // Destination account table
            Account distAccount = transaction.getRecipientAccount();

            PdfPTable distAccountTable = new PdfPTable(3);
            distAccountTable.setWidthPercentage(100);
            if (distAccount != null) {

                distAccountTable.addCell(removeBordersCell("First Name"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(distAccount.getAccFname(), Element.ALIGN_RIGHT));
            
                distAccountTable.addCell(removeBordersCell("Last Name"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(distAccount.getAccLname(), Element.ALIGN_RIGHT));
            
                distAccountTable.addCell(removeBordersCell("Account Status"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(String.valueOf(distAccount.getStatus()), Element.ALIGN_RIGHT));
            
                distAccountTable.addCell(removeBordersCell("IBAN"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(distAccount.getAccIban(), Element.ALIGN_RIGHT));
            
                distAccountTable.addCell(removeBordersCell("Bank"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(distAccount.getBank().getBankName(), Element.ALIGN_RIGHT));
                
                distAccountTable.addCell(removeBordersCell("Credit Card"));
                distAccountTable.addCell(removeBordersCell(" "));
                distAccountTable.addCell(createCell(transaction.getRecipientCard().getNumber(), Element.ALIGN_RIGHT));
            }

            // Transaction

            PdfPTable transactionTable = new PdfPTable(8);
            float[] transactionColumnWidths = {2, 2, 2, 1, 3, 1, 1, 2};
            transactionTable.setWidthPercentage(100);
            transactionTable.setWidths(transactionColumnWidths);

            Paragraph transactionTitle = new Paragraph("Transaction Details", boldFont);
            transactionTitle.setSpacingBefore(70f);
            transactionTitle.setSpacingAfter(10f);

            transactionTable.addCell(removeBordersCell("DATE"));
            transactionTable.addCell(removeBordersCell("SENDER"));
            transactionTable.addCell(removeBordersCell("RECIPIENT"));
            transactionTable.addCell(removeBordersCell(" "));
            transactionTable.addCell(removeBordersCell("DESCRIPTION"));
            transactionTable.addCell(removeBordersCell(" "));
            transactionTable.addCell(removeBordersCell(" "));
            PdfPCell amountTextCell = removeBordersCell("AMOUNT");
            amountTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(amountTextCell);
                        
            SimpleDateFormat transactionSDF = new SimpleDateFormat("dd/MM/yyyy");
            String transactionFormattedDate = transactionSDF.format(transaction.getTransactionDate());
            transactionTable.addCell(removeBordersCell(transactionFormattedDate));
            
            transactionTable.addCell(removeBordersCell(transaction.getSenderAccount().getAccIban()));
            transactionTable.addCell(removeBordersCell(transaction.getRecipientAccount().getAccIban()));
            transactionTable.addCell(removeBordersCell(" "));
            transactionTable.addCell(removeBordersCell(transaction.getDescription()));
            transactionTable.addCell(removeBordersCell(" "));
            transactionTable.addCell(removeBordersCell(" "));
            PdfPCell amountCell = removeBordersCell("$" + String.valueOf(transaction.getAmount()));
            amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(amountCell);

                        

            document.add(image);
            document.add(headerTable);
            document.add(infoTable);
            
            document.add(accTitle);
            document.add(senderTitle);
            document.add(lineTable);
            document.add(accountTable);
            
            document.add(recipientTitle);
            document.add(lineTable);
            document.add(distAccountTable);

            document.add(transactionTitle);
            document.add(transactionTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private PdfPCell createCell(String content, int alignment) {
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, normalFont));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell removeBordersCell(String content) {
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, normalFont));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void removeBordersFromTable(PdfPTable table) {
        for (PdfPRow row : table.getRows()) {
            for (PdfPCell cell : row.getCells()) {
                if (cell != null) {
                    cell.setBorder(Rectangle.NO_BORDER);
                }
            }
        }
    }
}