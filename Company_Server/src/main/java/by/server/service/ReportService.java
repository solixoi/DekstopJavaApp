package by.server.service;

import by.server.models.entities.Product;
import by.server.models.entities.ProductionExpenses;
import by.server.models.entities.RealizationExpenses;
import by.server.repositories.ProductRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportService {
    private final ProductRepository productRepository = new ProductRepository();

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

    private byte[] generatePdf(String... lines) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);
            PDType0Font font = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/Ebbe_Regular.ttf"));
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(font, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);

                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }
    }
}
