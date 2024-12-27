package by.server.service;

import by.server.models.entities.*;
import by.server.repositories.ProductRepository;
import by.server.repositories.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private final ProductRepository productRepository = new ProductRepository();
    private final UserRepository userRepository = new UserRepository();

    public byte[] createReportExpensesProduct(Long productId) {
        Product product = productRepository.findById(productId);
        if (product != null) {
            ProductionExpenses productionExpenses = product.getProductionExpenses();
            RealizationExpenses realizationExpenses = product.getRealizationExpenses();

            BigDecimal totalProductionCost = productionExpenses.getWagesCost()
                    .add(productionExpenses.getMaterialCost())
                    .add(productionExpenses.getOverheadCost())
                    .add(productionExpenses.getOtherExpenses());

            BigDecimal totalRealizationCost = realizationExpenses.getMarketingCost()
                    .add(realizationExpenses.getDistributionCost())
                    .add(realizationExpenses.getTransportationCost())
                    .add(realizationExpenses.getOtherExpenses());

            BigDecimal totalCost = totalProductionCost.add(totalRealizationCost);

            return generatePdf(
                    "Report for Product ID: " + productId,
                    "Product name: " + product.getProductName(),
                    "Total Production Cost: " + totalProductionCost,
                    "Total Realization Cost: " + totalRealizationCost,
                    "Total Cost: " + totalCost
            );
        } else {
            return generatePdf("Product not found");
        }
    }

    public byte[] createReportMarginality(Long productId) {
        Product product = productRepository.findById(productId);
        if (product != null) {
            BigDecimal costPrice = product.getCostPrice();
            BigDecimal finalPrice = product.getFinalPrice();
            BigDecimal markup = product.getMarkup();

            BigDecimal marginality = finalPrice.subtract(costPrice).divide(finalPrice, 4,  RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            return generatePdf(
                    "Marginality Report for Product ID: " + productId,
                    "Product name: " + product.getProductName(),
                    "Cost Price: " + costPrice,
                    "Final Price: " + finalPrice,
                    "Markup: " + markup,
                    "Marginality: " + marginality + "%"
            );
        } else {
            return generatePdf("Product not found");
        }
    }

    public byte[] createLogReport(Long userId) {
        User user = userRepository.findById(userId);
        if (user != null && user.getLogs() != null && !user.getLogs().isEmpty()) {
            List<Log> logs = user.getLogs();
            List<String> logLines = new ArrayList<>();
            logLines.add("Log Report for User: " + user.getUsername());
            logLines.add("Email: " + user.getEmail());
            logLines.add("Logs:");

            for (Log log : logs) {
                LocalDateTime creationDate = log.getDateCreation().toLocalDateTime();
                String formattedDate = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm-ss"));
                logLines.add("-" + formattedDate +
                        " | userId: " + log.getUser().getUserId() +
                        " | username: " + log.getUser().getUsername() +
                        " | " + log.getAction());
            }

            return generatePdf(logLines.toArray(new String[0]));
        } else {
            return generatePdf("User not found or has no logs.");
        }
    }

    public byte[] createReportHistoryProduct(Long productId) {
        Product product = productRepository.findById(productId);
        if (product != null && product.getPriceHistory() != null && !product.getPriceHistory().isEmpty()) {
            List<PriceHistory> priceHistories = product.getPriceHistory();
            List<String> historyLines = new ArrayList<>();
            historyLines.add("Product Report for Product: " + product.getProductName());
            historyLines.add("Creator Product: " + product.getCreatedBy().getUsername());
            historyLines.add("Histories:");

            for (PriceHistory priceHistory : priceHistories) {
                LocalDateTime creationDate = priceHistory.getChangeDate().toLocalDateTime();
                String formattedDate = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm-ss"));
                historyLines.add("-" + formattedDate +
                        " | old price: " + priceHistory.getOldPrice() +
                        " | new price: " + priceHistory.getNewPrice() );
            }

            return generatePdf(historyLines.toArray(new String[0]));
        } else {
            return generatePdf("User not found or has no logs.");
        }
    }

    private byte[] generatePdf(String... lines) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDType0Font font = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/Ebbe_Regular.ttf"));
            int margin = 50;
            int lineHeight = 15;
            int startY = 750;
            int currentY = startY;

            PDPage currentPage = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(document, currentPage);
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, currentY);

            for (String line : lines) {
                if (currentY <= margin + lineHeight) {
                    contentStream.endText();
                    contentStream.close();
                    document.addPage(currentPage);

                    currentPage = new PDPage();
                    contentStream = new PDPageContentStream(document, currentPage);
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    currentY = startY;
                    contentStream.newLineAtOffset(margin, currentY);
                }

                contentStream.showText(line);
                currentY -= lineHeight;
                contentStream.newLineAtOffset(0, -lineHeight);
            }

            contentStream.endText();
            contentStream.close();
            document.addPage(currentPage);

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }
    }
}
