package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.DTOs.ExtractedPage;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class TextExtractionService {

    private final Tika tika;

    public TextExtractionService(Tika tika) {
        this.tika = tika;
    }

    public String extractText(MultipartFile file) {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to extract text from document", e
            );
        }
    }

    public String detectFileType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to detect document type", e
            );
        }
    }

    public List<ExtractedPage> extractPages(InputStream inputStream) {

        try (PDDocument pdfDocument =
                     Loader.loadPDF(inputStream.readAllBytes())) {



            PDFTextStripper stripper =
                    new PDFTextStripper();

            List<ExtractedPage> pages = new ArrayList<>();

            int totalPages = pdfDocument.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {

                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String text =
                        stripper.getText(pdfDocument).trim();

                if (!text.isBlank()) {
                    pages.add(
                            new ExtractedPage(page, text)
                    );
                }
            }

            return pages;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to extract pages from PDF", e
            );
        }
    }

    public List<ExtractedPage> extractPagesUsingPath(Path pdfPath) {

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

            List<ExtractedPage> pages = new ArrayList<>();

            PDFTextStripper stripper = new PDFTextStripper();

            int pageCount = document.getNumberOfPages();

            for (int pageNumber = 1;
                 pageNumber <= pageCount;
                 pageNumber++) {

                stripper.setStartPage(pageNumber);
                stripper.setEndPage(pageNumber);

                String text = stripper.getText(document);

                pages.add(
                        new ExtractedPage(
                                pageNumber,
                                text
                        )
                );
            }

            return pages;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to extract text from PDF: " + pdfPath,
                    e
            );
        }
    }
}